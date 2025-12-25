package com.itsol.vn.wallpaper.live.parallax.service

import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Matrix
import android.graphics.Paint
import android.service.wallpaper.WallpaperService
import android.view.SurfaceHolder
import com.itsol.vn.wallpaper.live.parallax.utils.Common

class ImageWallpaperService: WallpaperService() {
    override fun onCreateEngine(): Engine {
        return ImageEngin()
    }
    inner class ImageEngin: Engine(){
        private var bitmap: Bitmap?=null
        init {
            bitmap = Common.bitmapImage
        }
        override fun onSurfaceCreated(holder: SurfaceHolder) {
            super.onSurfaceCreated(holder)
//            drawWallpaper()
        }

        private fun drawWallpaper() {
            val canvas: Canvas? = surfaceHolder.lockCanvas()
            canvas?.let {
                val paint = Paint()
                it.drawColor(0xFF000000.toInt()) // Nền đen
                bitmap?.let { bmp ->
                    // Tính toán kích thước màn hình
                    val screenWidth = canvas.width
                    val screenHeight = canvas.height

                    // Scale ảnh để fit màn hình
                    val scaledBitmap = scaleBitmapToFitScreen(bmp, screenWidth, screenHeight)

                    // Căn giữa hình ảnh
                    val x = (screenWidth - scaledBitmap.width) / 2f
                    val y = (screenHeight - scaledBitmap.height) / 2f

                    // Vẽ ảnh
                    it.drawBitmap(scaledBitmap, x, y, paint)
                }

                surfaceHolder.unlockCanvasAndPost(it)
            }
        }

        override fun onVisibilityChanged(visible: Boolean) {
            super.onVisibilityChanged(visible)
            if (visible) {
                drawWallpaper()
            }
        }
        fun scaleBitmapToFitScreen(bitmap: Bitmap, screenWidth: Int, screenHeight: Int): Bitmap {
            // Tính toán tỷ lệ scale
            val widthScale = screenWidth.toFloat() / bitmap.width
            val heightScale = screenHeight.toFloat() / bitmap.height
            val scale = maxOf(widthScale, heightScale) // Scale lớn hơn để đảm bảo ảnh bao phủ toàn màn hình

            // Scale ảnh
            val matrix = Matrix()
            matrix.setScale(scale, scale)

            // Tạo bitmap mới đã scale
            return Bitmap.createBitmap(
                bitmap, 0, 0, bitmap.width, bitmap.height, matrix, true
            )
        }
    }

}