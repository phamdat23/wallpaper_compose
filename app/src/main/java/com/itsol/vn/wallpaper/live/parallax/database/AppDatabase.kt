package com.itsol.vn.wallpaper.live.parallax.database

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.itsol.vn.wallpaper.live.parallax.database.CategoryDao
import com.itsol.vn.wallpaper.live.parallax.database.WallPaperDao
import com.itsol.vn.wallpaper.live.parallax.model.CategoryModel
import com.itsol.vn.wallpaper.live.parallax.model.HistorySearchModel
import com.itsol.vn.wallpaper.live.parallax.model.WallpaperModel
import com.itsol.vn.wallpaper.live.parallax.utils.Converters

@Database(entities = [WallpaperModel::class, CategoryModel::class, HistorySearchModel::class], version = 2, exportSchema = false )
@TypeConverters(Converters::class)
abstract class AppDatabase: RoomDatabase() {
    abstract fun wallpaperDao(): WallPaperDao
    abstract fun categoryDao(): CategoryDao
    abstract fun historySearchDao(): HistorySearchDao
}