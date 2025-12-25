package com.itsol.vn.wallpaper.live.parallax.parallaxWallpaper

import android.content.Context
import android.content.SharedPreferences
import android.content.res.Configuration
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Color
import android.opengl.GLES20
import android.opengl.GLSurfaceView
import android.opengl.GLUtils
import android.opengl.Matrix
import android.preference.PreferenceManager
import android.util.Log
import com.itsol.vn.wallpaper.live.parallax.R
import com.itsol.vn.wallpaper.live.parallax.parallaxWallpaper.BackgroundHelper.decodeScaledFromFile
import com.itsol.vn.wallpaper.live.parallax.parallaxWallpaper.BackgroundHelper.decodeScaledFromRes
import com.itsol.vn.wallpaper.live.parallax.parallaxWallpaper.Constants.DEPTH_MAX
import com.itsol.vn.wallpaper.live.parallax.parallaxWallpaper.Constants.DEPTH_MIN
import com.itsol.vn.wallpaper.live.parallax.parallaxWallpaper.Constants.DIM_MAX
import com.itsol.vn.wallpaper.live.parallax.parallaxWallpaper.Constants.FALLBACK_MAX
import com.itsol.vn.wallpaper.live.parallax.parallaxWallpaper.Constants.FALLBACK_MIN
import com.itsol.vn.wallpaper.live.parallax.parallaxWallpaper.Constants.SENSITIVITY_MAX
import com.itsol.vn.wallpaper.live.parallax.parallaxWallpaper.Constants.SENSITIVITY_MIN
import com.itsol.vn.wallpaper.live.parallax.parallaxWallpaper.Constants.ZOOM_MAX
import com.itsol.vn.wallpaper.live.parallax.parallaxWallpaper.Constants.ZOOM_MIN
import com.itsol.vn.wallpaper.live.parallax.utils.Common
import java.io.File
import java.nio.ByteBuffer
import java.nio.ByteOrder
import javax.microedition.khronos.egl.EGLConfig
import javax.microedition.khronos.opengles.GL10
import kotlin.math.abs


class MyRenderer : GLSurfaceView.Renderer {
    // Screen
    private var orientation = 0
    private var deltaXMax = 0f
    private var deltaYMax = 0f

    // Values
    private var deltaInit = false
    private var deltaArrayNew: Array<FloatArray>? = null
    private var deltaArrayOld: Array<FloatArray>? = null

    // Preferences
    private val sharedPreferences: SharedPreferences
    private var prefSensor = true

    //    private var prefScroll = true
    private var prefLimit = true
    private var prefDepth = 0.0

    //    private var prefScrollAmount = 0.0
    private var prefZoom = 0f
    private var prefDim = 0

    // External
    private var offset = 0.0

    // Internal
    private var isPreview: Boolean
    private var hasOverlay = false
    private var isFallback = false

    private val layerList: MutableList<BackgroundHelper.Layer> = ArrayList()

    // Opengl stuff
    private var glLayer: GLLayer? = null
    private val MVPMatrix = FloatArray(16)
    private val projectionMatrix = FloatArray(16)
    private val viewMatrix = FloatArray(16)
    private var textures: IntArray? = null

    private val parallax: Parallax
    private val context: Context

    // Default constructor
    constructor(context: Context) {
        this.context = context
        parallax = Parallax(context)
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context)
        // Not a preview
        isPreview = false

        start(null)
    }

    interface CallBackStartParallax {
        fun onCompleted()
    }

    // Preview constructor
    constructor(
        context: Context,
        prefWallpaperId: String?,
        callBackStartParallax: CallBackStartParallax?
    ) {
        this.context = context
        parallax = Parallax(context)
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context)

        // Is a preview
        isPreview = true

        start(callBackStartParallax)
    }

    override fun onSurfaceCreated(gl: GL10, config: EGLConfig) {
        // Nothing
    }

    override fun onSurfaceChanged(gl: GL10, width: Int, height: Int) {
        GLES20.glViewport(0, 0, width, height)
        // Refit wallpaper to match screen orientation
        if (orientation == Configuration.ORIENTATION_PORTRAIT) {
            val ratio = width.toFloat() / height
            deltaXMax = (2.5f * ratio)
            deltaYMax = 3f
//            Matrix.frustumM(
//                projectionMatrix,
//                0,
//                -ratio ,
//                ratio ,
//                -prefZoom,
//                prefZoom,
//                3f,
//                7f
//            )
//            val f = this.prefZoom
//            this.deltaXMax = (0.5f * 1.0f) / f
//            this.deltaYMax = 1.0f - f
            Matrix.frustumM(
                this.projectionMatrix,
                0,
                (-1.0f) * prefZoom,
                1.0f * prefZoom,
                -prefZoom,
                prefZoom,
                3.0f,
                7.0f
            )
        } else {
            val ratio = height.toFloat() / width
            deltaXMax = (3f)
            deltaYMax = (2.5f * ratio)
//            Matrix.frustumM(
//                projectionMatrix,
//                0,
//                -prefZoom,
//                prefZoom,
//                -ratio ,
//                ratio ,
//                3f,
//                7f
//            )
//            val ratio = (height.toFloat()) / (width.toFloat())
//            val f2 = this.prefZoom
//            this.deltaXMax = 1.0f - f2
//            this.deltaYMax = (0.5f * ratio) / f2
            Matrix.frustumM(
                this.projectionMatrix, 0, -prefZoom, prefZoom,
                (-ratio) * prefZoom, ratio * prefZoom, 3.0f, 7.0f
            )
        }

        // Create layers only if wallpaper has changed
        generateLayers()
    }

    override fun onDrawFrame(gl: GL10) {
        // Set the camera position (View matrix)
        Matrix.setLookAtM(viewMatrix, 0, 0f, 0f, 3f, 0f, 0f, 0f, 0f, 1.0f, 0.0f)
        // Calculate the projection and view transformation
        Matrix.multiplyMM(MVPMatrix, 0, projectionMatrix, 0, viewMatrix, 0)

        // Redraw background color
        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT)

        // Initialize arrays
        if (!deltaInit) {
            deltaArrayNew = Array(textures!!.size) { FloatArray(2) }
            deltaArrayOld = Array(textures!!.size) { FloatArray(2) }
            deltaInit = true
        }

        // Compute deltas
        for (i in 0 until textures!!.size - 1) {
            // Get layer z
            var z = if (!isFallback) {
                layerList[i].z.toDouble()
            } else {
                0.0
            }


            // Compute the x-y offset
            val deltaX = -((parallax.degX / 180.0 * (prefDepth * z))).toFloat()
            val deltaY = (parallax.degY / 180.0 * (prefDepth * z)).toFloat()

            // Limit max offset
            if ((abs(deltaX.toDouble()) > deltaXMax || abs(deltaY.toDouble()) > deltaYMax) && prefLimit) {
                deltaArrayNew = deltaArrayOld?.clone()
                break
            }

            deltaArrayOld = deltaArrayNew?.clone()

            deltaArrayNew?.get(i)?.set(0, deltaX)
            deltaArrayNew?.get(i)?.set(1, deltaY)
        }
        val layerCount = if (hasOverlay) {
            textures!!.size - 1
        } else {
            textures!!.size
        }

        // Draw layers
        for (i in 0 until layerCount) {
            val layerMatrix = MVPMatrix.clone()
            Matrix.translateM(
                layerMatrix, 0,
                deltaArrayNew?.get(i)?.get(0) ?: 0f, deltaArrayNew?.get(i)?.get(1) ?: 0f, 0f
            )
            glLayer?.draw(textures!![i], layerMatrix)
        }

        // Overlay
        if (hasOverlay) {
            // Has an overlay
            val layerMatrix = MVPMatrix.clone()
            glLayer?.draw(textures!![textures!!.size - 1], layerMatrix)
        }
    }


    // This method must be called every time the renderer is started or to reload the settings
    fun start(callBackStartParallax: CallBackStartParallax?) {
        reloadSettings()

        deltaInit = false

        // Get current screen orientation
        orientation = context.resources.configuration.orientation

        if (prefSensor) parallax.start(callBackStartParallax)
    }

    // Only pauses the sensor! OpenGL view is managed elsewhere
    fun stop() {
        if (prefSensor) parallax.stop()
    }

    fun reloadSettings() {
        // tắt sensor tạo hiệu ứng
        prefSensor = sharedPreferences.getBoolean(
            context.getString(R.string.pref_sensor_key),
            context.getResources().getBoolean(R.bool.pref_sensor_default)
        );
//        prefSensor = true

        prefLimit = sharedPreferences.getBoolean(
            context.getString(R.string.pref_limit_key),
            context.resources.getBoolean(R.bool.pref_limit_default)
        )
        //điều chỉnh độ sâu của các layer
        val depthString = sharedPreferences.getString(
            context.getString(R.string.pref_depth_key),
            context.getString(R.string.pref_depth_default)
        )
        prefDepth = DEPTH_MIN + depthString!!.toDouble() * (DEPTH_MAX / ((layerList.size - 2) * 5))
        Log.e(TAG, "reloadSettings:prefDepth:${prefDepth} ")

        // tăng giảm  độ nhạy cảm biến
        val sensitivityString =
            sharedPreferences.getString(
                context.getString(R.string.pref_sensitivity_key),
                context.getString(R.string.pref_sensitivity_default)
            );
//        val sensitivityString = "50"


        val sensitivity: Double =
            SENSITIVITY_MIN + (sensitivityString.toString().toDouble()) * (SENSITIVITY_MAX / 100.0)
        Log.e(TAG, "reloadSettings: sensitivityString: $sensitivity")
        //        String fallbackString = sharedPreferences.getString(context.getString(R.string.pref_fallback_key), context.getString(R.string.pref_fallback_default));
        val fallbackString = "50"
        Log.e(TAG, "reloadSettings: $fallbackString")
        val fallback: Double = FALLBACK_MIN + fallbackString.toDouble() * (FALLBACK_MAX / 100.0)

        val zoomString = "0"
        prefZoom =
            (ZOOM_MIN + (100 - zoomString.toDouble()) * ((ZOOM_MAX - ZOOM_MIN) / 100.0)).toFloat()
        Log.e(TAG, "reloadSettings: prefZoom:$prefZoom")
        // bật tắt scroll
//        prefScroll = sharedPreferences.getBoolean(
//            context.getString(R.string.pref_scroll_key),
//            context.resources.getBoolean(R.bool.pref_scroll_default)
//        )
//
//        val scrollAmountString = sharedPreferences.getString(
//            context.getString(R.string.pref_scroll_amount_key),
//            context.getString(R.string.pref_scroll_amount_default)
//        )
//        prefScrollAmount =
//            SCROLL_AMOUNT_MIN + scrollAmountString!!.toDouble() * (SCROLL_AMOUNT_MAX / 100.0)

        val dimString = sharedPreferences.getString(
            context.getString(R.string.pref_dim_key),
            context.getString(R.string.pref_dim_default)
        )
        Log.e(TAG, "reloadSettings: dimString: $dimString")
        prefDim = ((dimString!!.toDouble()) * (DIM_MAX / 100.0)).toInt()

        // Set parallax settings
        parallax.setFallback(fallback)
        parallax.setSensitivity(sensitivity)
    }

    private fun generateLayers() {
        // Clean old textures (if any) before loading the new ones
        clearTextures()

        // Assume that the layer is fallback
        val layerCount: Int
        isFallback = false
        hasOverlay = true

        //  thêm layer ảnh vào list
        val listFile = File(Common.wallPaperModel?.pathParallax.toString())
        layerList.clear()
        listFile.listFiles()?.map {
            layerList.add(
                BackgroundHelper.Layer(
                    it.absoluteFile,
                    it.absolutePath.substringBeforeLast(".").last().toString().toInt()
                )
            )

        }
        layerList.sortBy { it.z }
        Log.e(TAG, "generateLayers: layer: ${layerList.toString()}")
        if (layerList.size != 0) {
            // Layer loaded correctly
            isFallback = false
            layerCount = layerList.size
        } else {
            deployFallbackWallpaper()
            return
        }
        // Useful info
        var width = 0
        var height = 0

        // Create glTexture array
        textures = IntArray(layerCount + 1)
        GLES20.glGenTextures(layerCount + 1, textures, 0) // Layer + Overlay

        var tempBitmap: Bitmap

        try {
            for (i in textures!!.indices) {
                if (i < textures!!.size - 1) {
                    // Load bitmap
                    val bitmapFile = layerList[i].file
                    Log.e(TAG, "generateLayers: " + bitmapFile.absolutePath)
                    tempBitmap = decodeScaledFromFile(bitmapFile)
                    width = tempBitmap.width
                    height = tempBitmap.height
                } else {
                    // Generate overlay
                    tempBitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
//                    tempBitmap = Bitmap.createScaledBitmap(tempBitmap, width, height, true)
                    tempBitmap.eraseColor(Color.argb(prefDim, 0, 0, 0))
                }
                if (i == 0) {
                    // Solid black background
                    GLES20.glClearColor(0.0f, 0.0f, 0.0f, 1.0f)
                }

                GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, textures!![i])
                GLES20.glTexParameteri(
                    GLES20.GL_TEXTURE_2D,
                    GLES20.GL_TEXTURE_MIN_FILTER,
                    GLES20.GL_NEAREST
                )
                GLES20.glTexParameteri(
                    GLES20.GL_TEXTURE_2D,
                    GLES20.GL_TEXTURE_MAG_FILTER,
                    GLES20.GL_NEAREST
                )
                GLES20.glTexParameteri(
                    GLES20.GL_TEXTURE_2D,
                    GLES20.GL_TEXTURE_WRAP_S,
                    GLES20.GL_CLAMP_TO_EDGE
                )
                GLES20.glTexParameteri(
                    GLES20.GL_TEXTURE_2D,
                    GLES20.GL_TEXTURE_WRAP_T,
                    GLES20.GL_CLAMP_TO_EDGE
                )
                try {
//                    GLUtils.texImage2D(GLES20.GL_TEXTURE_2D, 0, tempBitmap, 0)
                    val buffer = ByteBuffer.allocateDirect(tempBitmap.byteCount)
                        .order(ByteOrder.nativeOrder())
                    tempBitmap.copyPixelsToBuffer(buffer)
                    buffer.position(0)
                    GLES20.glTexImage2D(
                        GLES20.GL_TEXTURE_2D,
                        0,
                        GLES20.GL_RGBA,
                        tempBitmap.width,
                        tempBitmap.height,
                        0,
                        GLES20.GL_RGBA,
                        GLES20.GL_UNSIGNED_BYTE,
                        buffer
                    )

                } catch (e: NullPointerException) {
                    Log.e(TAG, "Null pointer wile genrating layers", e)
                    deployFallbackWallpaper()
                    return
                }

                // Free memory
                tempBitmap.recycle()
            }
        } catch (e: Exception) {
            Log.e(TAG, "generateLayers: $e")
        }
        glLayer = GLLayer()

    }

//    fun decodeScaledFromFile(file: File): Bitmap {
//        val options = BitmapFactory.Options().apply {
//            inScaled = false // Không scale tự động
//        }
//        return BitmapFactory.decodeFile(file.absolutePath, options)
//    }

    fun setOffset(offset: Float) {
        this.offset = offset.toDouble()
    }

    private fun deployFallbackWallpaper() {
        clearTextures()

        isFallback = true
        hasOverlay = false

        val fallbackBitmap = decodeScaledFromRes(context.resources, R.drawable.fallback)

        textures = IntArray(1)

        GLES20.glClearColor(0.0f, 0.0f, 0.0f, 1.0f)

        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, textures!![0])

        GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MIN_FILTER, GLES20.GL_LINEAR)
        GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MAG_FILTER, GLES20.GL_LINEAR)
        GLES20.glTexParameteri(
            GLES20.GL_TEXTURE_2D,
            GLES20.GL_TEXTURE_WRAP_S,
            GLES20.GL_CLAMP_TO_EDGE
        )
        GLES20.glTexParameteri(
            GLES20.GL_TEXTURE_2D,
            GLES20.GL_TEXTURE_WRAP_T,
            GLES20.GL_CLAMP_TO_EDGE
        )

        GLUtils.texImage2D(GLES20.GL_TEXTURE_2D, 0, fallbackBitmap, 0)

        glLayer = GLLayer()


    }

    private fun clearTextures() {
        if (textures != null) {
            GLES20.glDeleteTextures(textures!!.size, textures, 0)
        }
    }

    companion object {
        private const val TAG = "Renderer"
    }
}
