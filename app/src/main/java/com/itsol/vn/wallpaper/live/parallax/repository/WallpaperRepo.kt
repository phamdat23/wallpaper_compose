package com.itsol.vn.wallpaper.live.parallax.repository

import com.itsol.vn.wallpaper.live.parallax.model.CategoryModel
import com.itsol.vn.wallpaper.live.parallax.model.VersionRepositoryModel
import com.itsol.vn.wallpaper.live.parallax.model.WallpaperModel

interface WallpaperRepo {

    suspend fun getAllWallpaper(): List<WallpaperModel>
    suspend fun getALlCategory(): List<CategoryModel>

    suspend fun getWallpaperByCategory(category: String): List<WallpaperModel>

    suspend fun updateFavoriteWallpaper(id: Int, favorite: Boolean)

    suspend fun getWallPaperByAttr(attr: String): List<WallpaperModel>

    suspend fun getFavoriteWallpaper(): List<WallpaperModel>
    suspend fun getDownloadWallpaper(): List<WallpaperModel>

    suspend fun setPathDownloadWallpaper(id: Int, pathVideo: String, pathParallax: String,  pathImage: String, download: Boolean)

    suspend fun deleteImageDownloaded(id: Int)

    suspend fun getVersionApi(): VersionRepositoryModel

    suspend fun getWallpaperByCurrentPage(limit: Int, page: Int): List<WallpaperModel>

}