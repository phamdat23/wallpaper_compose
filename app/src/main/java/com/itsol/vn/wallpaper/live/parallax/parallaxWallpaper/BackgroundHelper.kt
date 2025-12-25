package com.itsol.vn.wallpaper.live.parallax.parallaxWallpaper

import android.content.Context
import android.content.res.Resources
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Log
import com.itsol.vn.wallpaper.live.parallax.parallaxWallpaper.Utils.Companion.getInstance
import java.io.File
import java.util.Arrays


object BackgroundHelper {
    private const val TAG = "BackgroundHelper"

    fun loadFromFile(id: String, context: Context): List<Layer>? {
        // Create final list
        val output: MutableList<Layer> = ArrayList()

        // Get background folder
        val fileRoot = getBackgroundFolder(id, context)
        if (fileRoot == null) {
            Log.e(TAG, "Directory $id not found in root!")
            return null
        }
        val file = File(fileRoot, fileRoot.name)
        if (file != null) {
            // Directory found
            Log.d(TAG, "Directory $id found in root!")

            val layers = file.listFiles()
            if (layers != null) {
                if (layers.size > 0) {
                    // Sort array by name

                    Arrays.sort(layers)
                    for (layerFile in layers) {
                        val zString = getInstance().getBetweenStrings(
                            layerFile.path,
                            id + "_",
                            Constants.BG_FORMAT
                        )
                        val layerZ = zString!!.toInt()

                        val layer = Layer(layerFile, layerZ)
                        output.add(layer)
                        Log.d(TAG, "Layer with name " + layerFile.name + " loaded with z=" + layerZ)
                    }
                } else {
                    Log.e(TAG, "Directory $id is empty!")
                    return null
                }
            }
        } else {
            // Directory not found
            Log.e(TAG, "Directory $id not found in root!")
            return null
        }

        return output
    }

    fun getBackgroundFolder(id: String, context: Context): File? {
        val files: Array<File> // File holder

        // Get files from root folder
        val root = getRootFolder(context)
        if (root != null) {
            files = root.listFiles()
        } else {
            return null
        }

        var backgroundPath: File? = null
        if (files != null) {
            for (file in files) {
                if (file.isDirectory && file.name == id) {
                    backgroundPath = file
                }
            }
        } else {
            Log.e(TAG, "Directory $id returned null content! Are permission ok?")
            return null
        }

        return backgroundPath
    }

    fun getRootFolder(context: Context): File? {
        // Get Zero folder
        var filePath = context.filesDir.toString()
        filePath = filePath + "/" + Constants.FS_DIR_ZERO + "/"
        val file = File(filePath)
        if (file.exists()) {
            return file
        } else {
            if (file.mkdir()) {
                Log.e(TAG, "Root directory \"" + file.path + "\" not found: create it")
                return file
            } else {
                Log.e(TAG, "Unable to create root directory \"" + file.path + "\"")
            }
        }

        return null
    }

    @JvmStatic
    fun decodeScaledFromFile(file: File): Bitmap {
        // Get the size
        val options = BitmapFactory.Options()
        options.inJustDecodeBounds = true
        options.inScaled = false

        BitmapFactory.decodeFile(file.path, options)

        options.inJustDecodeBounds = false

        return BitmapFactory.decodeFile(file.path, options)
    }

    @JvmStatic
    fun decodeScaledFromRes(res: Resources?, id: Int): Bitmap {
        // Get the size
        val options = BitmapFactory.Options()
        options.inJustDecodeBounds = true

        BitmapFactory.decodeResource(res, id, options)

        options.inJustDecodeBounds = false

        return BitmapFactory.decodeResource(res, id, options)
    }

    class Layer internal constructor(
        @JvmField var file: File, // thứ tự xếp chồng ảnh lên nhau
        @JvmField var z: Int
    )
}