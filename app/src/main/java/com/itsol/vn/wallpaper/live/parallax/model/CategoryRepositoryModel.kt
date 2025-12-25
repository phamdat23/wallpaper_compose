package com.itsol.vn.wallpaper.live.parallax.model

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

@Parcelize
data class CategoryRepositoryModel(
    @SerializedName("success") val success: Boolean,
    @SerializedName("thumbs") val thumbs: List<CategoryModel>
): Parcelable
