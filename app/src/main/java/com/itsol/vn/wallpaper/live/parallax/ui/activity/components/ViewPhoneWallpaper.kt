package com.itsol.vn.wallpaper.live.parallax.ui.activity.components

import android.content.Context
import android.graphics.drawable.Drawable
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.FrameLayout
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
import com.itsol.vn.wallpaper.live.parallax.databinding.LayoutPhoneWallpaperBinding

class ViewPhoneWallpaper @JvmOverloads constructor(context: Context, attr: AttributeSet? = null) :
    FrameLayout(context, attr) {

    private val binding = LayoutPhoneWallpaperBinding.inflate(LayoutInflater.from(context), this, true)

    fun setImageWallpaper(uri: String, onLoadFail:(StateLoadingImage)->Unit) {
        onLoadFail.invoke(StateLoadingImage.ON_LOADING)
        Glide.with(binding.imgWallpaper).load(uri).addListener(object :RequestListener<Drawable>{
            override fun onLoadFailed(
                e: GlideException?,
                model: Any?,
                target: Target<Drawable>,
                isFirstResource: Boolean
            ): Boolean {
                onLoadFail.invoke(StateLoadingImage.ERROR)
                return false
            }

            override fun onResourceReady(
                resource: Drawable,
                model: Any,
                target: Target<Drawable>?,
                dataSource: DataSource,
                isFirstResource: Boolean
            ): Boolean {
                onLoadFail.invoke(StateLoadingImage.SUCCESS)
                return false
            }
        }).into(binding.imgWallpaper)
    }
}