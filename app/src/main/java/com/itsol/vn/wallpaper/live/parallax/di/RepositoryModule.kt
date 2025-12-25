package com.itsol.vn.wallpaper.live.parallax.di


import com.itsol.vn.wallpaper.live.parallax.repository.WallpaperRepo
import com.itsol.vn.wallpaper.live.parallax.repository.WallpaperRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@InstallIn(SingletonComponent::class)
@Module
class RepositoryModule {

    @Provides
    @Singleton
    fun providerWallPaper(wallpaperRepository: WallpaperRepository): WallpaperRepo =
        wallpaperRepository

}