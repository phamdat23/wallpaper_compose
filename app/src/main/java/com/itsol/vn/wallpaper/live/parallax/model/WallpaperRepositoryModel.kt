package com.itsol.vn.wallpaper.live.parallax.model

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

@Parcelize
data class WallpaperRepositoryModel(
    @SerializedName("success") val success: Boolean,
    @SerializedName("data") val data: List<WallpaperModel>
) : Parcelable
