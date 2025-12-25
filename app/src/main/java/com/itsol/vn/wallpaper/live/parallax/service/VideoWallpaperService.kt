package com.itsol.vn.wallpaper.live.parallax.service

import android.app.WallpaperManager
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.media.MediaPlayer
import android.media.PlaybackParams
import android.service.wallpaper.WallpaperService
import android.view.SurfaceHolder
import com.itsol.vn.wallpaper.live.parallax.utils.Common
import java.io.IOException


class VideoWallpaperService : WallpaperService() {
    override fun onCreateEngine(): Engine {
        return VideoEngine()
    }

    inner class VideoEngine : WallpaperService.Engine() {
        private var mediaPlayer: MediaPlayer? = null
        lateinit var mContext: Context
        override fun onCreate(surfaceHolder: SurfaceHolder?) {
            super.onCreate(surfaceHolder)
            mContext = this@VideoWallpaperService
        }

        override fun onSurfaceCreated(holder: SurfaceHolder) {
            super.onSurfaceCreated(holder)
            if (Common.wallPaperModel?.pathVideo != "") {
                mediaPlayer = MediaPlayer().apply {
                    setDataSource(Common.wallPaperModel?.pathVideo)
                    setSurface(holder.surface)
                    isLooping = true
//                    setVideoScalingMode(MediaPlayer.VIDEO_SCALING_MODE_SCALE_TO_FIT_WITH_CROPPING)
                    setVolume(0f, 0f)
                    prepare()
                    start()
                }
            }

        }

        override fun onVisibilityChanged(visible: Boolean) {
            if (visible) {
                mediaPlayer?.start()
            } else {
                mediaPlayer?.pause()
            }
        }

        override fun onSurfaceDestroyed(holder: SurfaceHolder) {
            super.onSurfaceDestroyed(holder)
            if (mediaPlayer?.isPlaying == true) mediaPlayer?.stop()
            mediaPlayer?.release()
            mediaPlayer = null
        }

        override fun onDestroy() {
            super.onDestroy()
            if (mediaPlayer?.isPlaying == true) mediaPlayer?.stop()
            mediaPlayer?.release()
            mediaPlayer = null
        }

    }

}