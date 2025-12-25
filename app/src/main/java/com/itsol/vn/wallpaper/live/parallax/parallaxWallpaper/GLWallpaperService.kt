package com.itsol.vn.wallpaper.live.parallax.parallaxWallpaper

import android.annotation.SuppressLint
import android.content.Context
import android.opengl.GLSurfaceView
import android.service.wallpaper.WallpaperService
import android.view.SurfaceHolder


@SuppressLint("Registered")
open class GLWallpaperService : WallpaperService() {
    override fun onCreateEngine(): Engine? {
        return null
    }

    internal open inner class GLEngine : Engine() {
        private var glSurfaceView: WallpaperGLSurfaceView? = null
        private var rendererHasBeenSet = false

        override fun onCreate(surfaceHolder: SurfaceHolder) {
            super.onCreate(surfaceHolder)
            glSurfaceView = WallpaperGLSurfaceView(this@GLWallpaperService)
        }

        override fun onVisibilityChanged(visible: Boolean) {
            super.onVisibilityChanged(visible)

            if (rendererHasBeenSet) {
                if (visible) {
                    glSurfaceView!!.onResume()
                } else {
                    glSurfaceView!!.onPause()
                }
            }
        }

        override fun onDestroy() {
            super.onDestroy()
            glSurfaceView!!.onDestroy()
        }

        fun setRenderer(renderer: GLSurfaceView.Renderer?) {
            glSurfaceView!!.setRenderer(renderer)
            rendererHasBeenSet = true
        }

        fun setEGLContextClientVersion(version: Int) {
            glSurfaceView!!.setEGLContextClientVersion(version)
        }

        fun setPreserveEGLContextOnPause(preserve: Boolean) {
            glSurfaceView!!.preserveEGLContextOnPause = preserve
        }

        internal inner class WallpaperGLSurfaceView(context: Context?) : GLSurfaceView(context) {
            override fun getHolder(): SurfaceHolder {
                return surfaceHolder
            }

            fun onDestroy() {
                super.onDetachedFromWindow()
            }
        }
    }
}
