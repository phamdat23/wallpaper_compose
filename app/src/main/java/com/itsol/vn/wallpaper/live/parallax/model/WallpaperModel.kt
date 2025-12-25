package com.itsol.vn.wallpaper.live.parallax.model

import android.os.Parcelable
import android.util.Log
import androidx.compose.runtime.Immutable
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

@Entity(tableName = "WallpaperTable")
@Parcelize
data class WallpaperModel(
//    @SerializedName("id")
    @ColumnInfo("id")
    @PrimaryKey(autoGenerate = true) val id: Int,

    @SerializedName("key")
    @ColumnInfo("key")
    val key: String,

    @SerializedName("wallpaper_id")
    @ColumnInfo("wallpaperId")
    val wallpaperId: String,

    @SerializedName("wallpaper_name")
    @ColumnInfo("wallpaperName")
    val wallpaperName: String,

    @SerializedName("types")
    @ColumnInfo("types")
    val types: String,

    @SerializedName("url")
    @ColumnInfo("url")
    val url: String,


    @SerializedName("categories")
    @ColumnInfo("categories")
    val categories: String,

    @SerializedName("pricing_type")
    @ColumnInfo("pricingType")
    val pricingType: String,

    @SerializedName("active")
    @ColumnInfo("active")
    val active: String,


    @SerializedName("install_count")
    @ColumnInfo("installCount")
    val installCount: String,

    @SerializedName("urls")
    @ColumnInfo("urls")
    val urls: String,


//    @SerializedName("size")
//    @ColumnInfo("size")
//    val size: Int,
//
//    @SerializedName("uploaded")
//    @ColumnInfo("uploaded")
//    val uploaded: String?=null,

//    @SerializedName("favorite")
    @ColumnInfo("favorite")
    var favorite: Boolean = false,

//    @SerializedName("download")
    @ColumnInfo("download")
    var download: Boolean = false,

//    @SerializedName("pathParallax")
    @ColumnInfo("pathParallax", defaultValue = "")
    var pathParallax: String = "",

//    @SerializedName("pathVideo")
    @ColumnInfo("pathVideo", defaultValue = "")
    var pathVideo: String = "",
    @ColumnInfo("pathImage", defaultValue = "")
    var pathImage: String = "",

    ) : Parcelable {


    fun doseMatchSearchQuery(query: String): Boolean {
        val matchingCombinations = arrayListOf(
            categories,
        )
        val a =  matchingCombinations.any {
            query.contains(it, ignoreCase = true)
        } or matchingCombinations.any {
            it.contains(query)
        }
        return a
    }
}
