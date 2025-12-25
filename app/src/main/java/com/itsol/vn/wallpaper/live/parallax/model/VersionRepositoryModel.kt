package com.itsol.vn.wallpaper.live.parallax.model

import com.google.gson.annotations.SerializedName

data class VersionRepositoryModel(
    @SerializedName("success") val success: Boolean,
    @SerializedName("data") val data: VersionModel
) {
    data class VersionModel(
        @SerializedName("version") val version: Float,
        @SerializedName("change_log") val changeLog: LogVersion
    )

    data class LogVersion(
        @SerializedName("category_update") val categoryUpdate: Boolean,
        @SerializedName("wallpaper_name_new_or_update") val wallpaperNameNewOrUpdate:List<String>,
        @SerializedName("wallpaper_name_deleted") val wallpaperNameDeleted: List<String>
    )
}
