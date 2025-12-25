package com.itsol.vn.wallpaper.live.parallax.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.itsol.vn.wallpaper.live.parallax.model.WallpaperModel

@Dao
interface WallPaperDao {
    @Query("SELECT * FROM wallpapertable")
    fun getAllWallPaper(): List<WallpaperModel>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertWallPaper(wallpaperModel: WallpaperModel)

    @Query("SELECT * FROM WallpaperTable WHERE categories LIKE '%' || :category || ',%' OR categories LIKE :category || ',%,' OR categories LIKE '%,' || :category OR categories LIKE :category")
    fun getWallPaperByCategory(category: String): List<WallpaperModel>

    @Query("SELECT COUNT() FROM wallpapertable")
    fun getCountWallpaper(): Int

    @Query("UPDATE wallpapertable SET favorite =:favorite  WHERE id = :id")
    fun updateFavorite(id: Int, favorite: Boolean)


    //@Query("SELECT * FROM WallpaperTable WHERE types LIKE '%' || :attr || ',%' OR types LIKE :attr || ',%,' OR types LIKE '%,' || :attr OR types LIKE  :attr")
    @Query("SELECT * FROM WallpaperTable WHERE types LIKE '%' || :attr || '%'")
    fun getWallpaperByAttr(attr: String): List<WallpaperModel>

    @Query("SELECT * FROM wallpapertable WHERE favorite = 1")
    fun getFavoriteWallPaper(): List<WallpaperModel>


    @Query("SELECT * FROM wallpapertable WHERE download = 1")
    fun getDownloadWallPaper(): List<WallpaperModel>

    @Query("UPDATE wallpapertable SET pathVideo = :pathVideo, download = :download, pathParallax = :pathParallax, pathImage =:pathImage WHERE id = :id")
    fun setPathDownloadWallpaper(
        id: Int,
        pathVideo: String,
        pathParallax: String,
        pathImage: String,
        download: Boolean
    )

    @Query("delete from WallpaperTable")
    fun deleteTable()

    @Query("SELECT * FROM wallpapertable  LIMIT :limit OFFSET :page")
    fun getWallpaperPage(limit: Int, page: Int): List<WallpaperModel>

    @Query("delete from WallpaperTable where wallpaperId =:id")
    fun deleteWallpaper(id: String);

    @Query("SELECT * FROM wallpapertable where wallpaperId=:id")
    fun getWallpaperById(id: String): WallpaperModel?

    @Query("update wallpapertable set types =:type , download= 0 , urls =:urls , url=:url where wallpaperId =:wallpaperId ")
    fun updateWallpaper(wallpaperId: String, type: String, urls: String, url: String)


}