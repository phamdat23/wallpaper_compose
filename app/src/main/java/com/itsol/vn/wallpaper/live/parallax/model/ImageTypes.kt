package com.itsol.vn.wallpaper.live.parallax.model

import android.os.Parcelable
import androidx.room.ColumnInfo
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

@Parcelize
data class ImageTypes(
    @SerializedName("type") @ColumnInfo("type") val type: String,
    @SerializedName("url") @ColumnInfo("url") val url: String,
) : Parcelable {
}