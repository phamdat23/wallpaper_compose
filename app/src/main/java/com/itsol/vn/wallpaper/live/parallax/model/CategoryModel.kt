package com.itsol.vn.wallpaper.live.parallax.model

import android.os.Parcelable
import androidx.compose.runtime.Immutable
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

@Entity("CategoryTable")
@Parcelize
data class CategoryModel(
//    @SerializedName("id")
    @ColumnInfo("id")
    @PrimaryKey(autoGenerate = true) val id: Int =0,
    @SerializedName("category_name") @ColumnInfo("category_name") val categoryName: String,
    @SerializedName("url") @ColumnInfo("url") val url: String
) : Parcelable {

}
