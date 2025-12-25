package com.itsol.vn.wallpaper.live.parallax.di

import android.content.Context
import android.content.res.AssetManager
import android.content.res.Resources
import androidx.room.Room
import com.itsol.vn.wallpaper.live.parallax.database.AppDatabase
import com.itsol.vn.wallpaper.live.parallax.database.CategoryDao
import com.itsol.vn.wallpaper.live.parallax.database.HistorySearchDao
import com.itsol.vn.wallpaper.live.parallax.database.WallPaperDao
import com.itsol.vn.wallpaper.live.parallax.utils.Constants
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@InstallIn(SingletonComponent::class)
@Module
class AppModule {

    @Singleton
    @Provides
    fun provideAppContext(@ApplicationContext context: Context): Context = context

    @Singleton
    @Provides
    fun provideResource(context: Context): Resources = context.resources

    @Singleton
    @Provides
    fun provideAssetManger(context: Context): AssetManager = context.assets

    @Singleton
    @Provides
    fun provideAppDatabase(context: Context): AppDatabase =
        Room.databaseBuilder(context, AppDatabase::class.java, Constants.DATABASE_NAME)
            .fallbackToDestructiveMigration()
            .allowMainThreadQueries()
            .build()

    @Singleton
    @Provides
    fun providerWallpaperDao(appDatabase: AppDatabase): WallPaperDao = appDatabase.wallpaperDao()

    @Singleton
    @Provides
    fun providerCategoryDao(appDatabase: AppDatabase): CategoryDao = appDatabase.categoryDao()

    @Singleton
    @Provides
    fun providerHistorySearchDao(appDatabase: AppDatabase): HistorySearchDao = appDatabase.historySearchDao()
}