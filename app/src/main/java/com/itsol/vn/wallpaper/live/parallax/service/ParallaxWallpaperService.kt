package com.itsol.vn.wallpaper.live.parallax.service

import android.view.SurfaceHolder
import com.itsol.vn.wallpaper.live.parallax.parallaxWallpaper.GLWallpaperService
import com.itsol.vn.wallpaper.live.parallax.parallaxWallpaper.MyRenderer

class ParallaxWallpaperService : GLWallpaperService() {
    override fun onCreateEngine(): Engine {
        return OpenGLES2Engine()
    }


    private inner class OpenGLES2Engine : GLEngine() {
        var renderer: MyRenderer? = null

        override fun onCreate(surfaceHolder: SurfaceHolder) {
            super.onCreate(surfaceHolder)

            // Set version
            setEGLContextClientVersion(2)
            setPreserveEGLContextOnPause(true)

            // Set renderer
            renderer = MyRenderer(applicationContext)
            setRenderer(renderer)
        }

        override fun onVisibilityChanged(visible: Boolean) {
            super.onVisibilityChanged(visible)
            if (visible) {
                renderer?.start(null)
            } else {
                renderer?.stop()
            }
        }

        override fun onOffsetsChanged(
            xOffset: Float,
            yOffset: Float,
            xOffsetStep: Float,
            yOffsetStep: Float,
            xPixelOffset: Int,
            yPixelOffset: Int
        ) {
            super.onOffsetsChanged(
                xOffset,
                yOffset,
                xOffsetStep,
                yOffsetStep,
                xPixelOffset,
                yPixelOffset
            )
            renderer!!.setOffset(xOffset)
        }
    }
}