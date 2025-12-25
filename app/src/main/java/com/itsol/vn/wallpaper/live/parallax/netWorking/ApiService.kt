package com.itsol.vn.wallpaper.live.parallax.netWorking

import com.itsol.vn.wallpaper.live.parallax.model.CategoryRepositoryModel
import com.itsol.vn.wallpaper.live.parallax.model.VersionRepositoryModel
import com.itsol.vn.wallpaper.live.parallax.model.WallpaperRepositoryModel
import com.itsol.vn.wallpaper.live.parallax.repository.WallpaperRepository
import retrofit2.http.GET
import retrofit2.http.Query

interface ApiService {
    @GET("wallpaper/allthumb")
    suspend fun getAllWallpaper(): WallpaperRepositoryModel

    @GET("category/allthumb")
    suspend fun getAllCategory(): CategoryRepositoryModel

    @GET("wallpaper/version")
    suspend fun getVersion(): VersionRepositoryModel

    @GET("wallpaper/thumb/by/names")
    suspend fun getWallpaperByName(@Query("names") listName: String): WallpaperRepositoryModel
}