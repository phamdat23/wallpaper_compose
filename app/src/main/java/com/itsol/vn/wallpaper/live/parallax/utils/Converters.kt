package com.itsol.vn.wallpaper.live.parallax.utils

import androidx.room.TypeConverter
import com.itsol.vn.wallpaper.live.parallax.model.ImageTypes
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken


class Converters {
    private val gson = Gson()

    @TypeConverter
    fun fromImageTypeList(value: List<ImageTypes>?): String {
        return gson.toJson(value)
    }

    @TypeConverter
    fun toImageTypeList(value: String): List<ImageTypes>? {
        val listType = object : TypeToken<List<ImageTypes>>() {}.type
        return gson.fromJson(value, listType)
    }
}