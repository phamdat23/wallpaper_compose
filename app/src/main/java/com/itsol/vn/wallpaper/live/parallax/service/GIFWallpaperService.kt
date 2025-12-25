package com.itsol.vn.wallpaper.live.parallax.service

import android.service.wallpaper.WallpaperService
import android.graphics.Movie
import android.os.Handler
import android.util.Log
import android.view.SurfaceHolder
import android.view.WindowInsets
import com.itsol.vn.wallpaper.live.parallax.utils.Common
import java.io.FileDescriptor
import java.io.IOException
import java.io.PrintWriter

class GIFWallpaperService : WallpaperService() {

    override fun dump(fd: FileDescriptor, out: PrintWriter, args: Array<String>) {
        super.dump(fd, out, args)
    }

    override fun onCreateEngine(): Engine? {
        return try {
            val movie = Movie.decodeFile(Common.wallPaperModel?.pathVideo)
            GIFWallpaperEngine(movie)
        } catch (e: IOException) {
            Log.e("GIF", "Could not load asset")
            null
        }
    }

    internal inner class GIFWallpaperEngine(private val movie: Movie) : Engine() {
        private var holder: SurfaceHolder? = null
        private var visible = false
        private val handler: Handler = Handler()
        override fun onCreate(surfaceHolder: SurfaceHolder) {
            super.onCreate(surfaceHolder)
            holder = surfaceHolder
        }

        private val drawGIF = Runnable { draw() }
        private fun draw() {
            if (visible) {
                val canvas = holder!!.lockCanvas()
                canvas.save()
                // Adjust size and position so that
                // the image looks good on your screen
                val width = canvas.width
                val height = canvas.height
                val movieWidth = movie.width()
                val movieHeight = movie.height()

                val scaleX = width.toFloat() / movieWidth.toFloat()
                val scaleY = height.toFloat() / movieHeight.toFloat()
                val scale = Math.max(scaleX, scaleY)
                canvas.scale(scale, scale)
                movie.draw(canvas,0f, 0f)
                canvas.restore()
                holder?.unlockCanvasAndPost(canvas)
                movie.setTime((System.currentTimeMillis() % movie.duration()).toInt())
                handler.removeCallbacks(drawGIF)
                val frameDuration = 20
                handler.postDelayed(drawGIF, frameDuration.toLong())
            }
        }


        override fun onApplyWindowInsets(insets: WindowInsets?) {
            super.onApplyWindowInsets(insets)
        }

        override fun onVisibilityChanged(visible: Boolean) {
            this.visible = visible
            if (visible) {
                handler.post(drawGIF)
            } else {
                handler.removeCallbacks(drawGIF)
            }
        }


        override fun onDestroy() {
            super.onDestroy()
            handler.removeCallbacks(drawGIF)
        }

    }
}