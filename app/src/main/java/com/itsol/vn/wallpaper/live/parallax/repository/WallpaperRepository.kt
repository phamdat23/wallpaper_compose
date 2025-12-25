package com.itsol.vn.wallpaper.live.parallax.repository

import android.util.Log
import com.itsol.vn.wallpaper.live.parallax.database.CategoryDao
import com.itsol.vn.wallpaper.live.parallax.database.HistorySearchDao
import com.itsol.vn.wallpaper.live.parallax.database.WallPaperDao
import com.itsol.vn.wallpaper.live.parallax.model.CategoryModel
import com.itsol.vn.wallpaper.live.parallax.model.HistorySearchModel
import com.itsol.vn.wallpaper.live.parallax.model.VersionRepositoryModel
import com.itsol.vn.wallpaper.live.parallax.model.WallpaperModel
import com.itsol.vn.wallpaper.live.parallax.netWorking.ApiService
import com.itsol.vn.wallpaper.live.parallax.utils.Common
import com.itsol.vn.wallpaper.live.parallax.utils.Constants
import com.orhanobut.hawk.Hawk
import okio.IOException
import javax.inject.Inject

class WallpaperRepository @Inject constructor(
    private val apiService: ApiService,
    private val wallPaperDao: WallPaperDao,
    private val categoryDao: CategoryDao,
    private val historySearchDao: HistorySearchDao
) : WallpaperRepo {
    override suspend fun getAllWallpaper(): List<WallpaperModel> {
        try {
            val list = apiService.getAllWallpaper().data
            if (wallPaperDao.getCountWallpaper()==0) {
                Log.e("AAAAAAAAAA", "getAllWallpaper: init data first")
                wallPaperDao.deleteTable()
                list.map { wallpaperModel ->
                    wallPaperDao.insertWallPaper(wallpaperModel)
                }
            }
        }catch (e:IOException){
            Log.e("AAAAAAAA", "getAllWallpaper:${e.message} ", )
        }

        return wallPaperDao.getAllWallPaper()
    }

    suspend fun getAndUpdateWallpaper(): List<WallpaperModel> {
        if(wallPaperDao.getCountWallpaper()==0){
            getAllWallpaper()
        }else{
            Log.e("ERROR", "getAndUpdateWallpaper: ", )
            val version = Hawk.get<VersionRepositoryModel>(Constants.VERSION_API)
            if ((version.data.changeLog.wallpaperNameNewOrUpdate.isNotEmpty() || version.data.changeLog.wallpaperNameDeleted.isNotEmpty()) && version.data.version != Hawk.get(
                    Constants.VERSION_API_CODE, 0.0f
                )
            ) {
                Hawk.put(Constants.VERSION_API_CODE, version.data.version)
                var listName = ""
                version.data.changeLog.wallpaperNameNewOrUpdate.map {
                    listName += "$it,"
                }
                var listNameDelete = ""
                version.data.changeLog.wallpaperNameNewOrUpdate.map {
                    listNameDelete += "$it,"
                }
                val list = apiService.getWallpaperByName(listName).data
                list.map { wallpaperModel ->
                    if (wallPaperDao.getWallpaperById(wallpaperModel.wallpaperId) != null) {
                        wallPaperDao.updateWallpaper(
                            wallpaperModel.wallpaperId,
                            wallpaperModel.types,
                            wallpaperModel.urls,
                            wallpaperModel.url
                        )
                    } else {
                        wallPaperDao.insertWallPaper(wallpaperModel)
                    }

                }
                val listDelete = apiService.getWallpaperByName(listNameDelete).data
                listDelete.map {
                    wallPaperDao.deleteWallpaper(it.wallpaperId)
                }
            }
        }
        val listCateResult =  wallPaperDao.getAllWallPaper()


        return listCateResult
    }

    fun getWallpaper(): List<WallpaperModel> {
        return wallPaperDao.getAllWallPaper()
    }



    override suspend fun getALlCategory(): List<CategoryModel> {
        try {
            val listCategory = apiService.getAllCategory().thumbs
            if (categoryDao.getCountCategory()==0) {
                Log.e("AAAAAAAA", "getALlCategory:init cat ", )
                categoryDao.deleteCategoryTable()
                listCategory.map {
                    categoryDao.insertCategory(it)
                }
            }
        }catch (e: IOException){
            Log.e("ERROR", "getALlCategory: ${e.message}", )
        }
        val listCateResult = categoryDao.getAllCategory()
        listCateResult.mapIndexed { index, wallpaperModel ->
            if(index<6){
                Common.listCatName.add(wallpaperModel.categoryName)
            }
        }
        return categoryDao.getAllCategory()
    }
    suspend fun getAndUpdateCategory(): List<CategoryModel> {
        if(categoryDao.getCountCategory()!=0){
            val version = Hawk.get<VersionRepositoryModel>(Constants.VERSION_API)
            if (version.data.changeLog.categoryUpdate&&version.data.version!=Hawk.get(
                    Constants.VERSION_API_CODE, 0.0f
                )) {
                Log.e("AAAAAAAAAAAA", "getALlCategory: update data")
                val listCategory = apiService.getAllCategory().thumbs
                categoryDao.deleteCategoryTable()
                listCategory.map {
                    categoryDao.insertCategory(it)
                }
            }
        }else{
            getALlCategory()
        }
        val listCateResult = categoryDao.getAllCategory()
        listCateResult.mapIndexed { index, wallpaperModel ->
            if(index<6){
                Common.listCatName.add(wallpaperModel.categoryName)
            }
        }
        return listCateResult
    }

    suspend fun getAllCategory2(): List<CategoryModel>{
        return categoryDao.getAllCategory()
    }
    fun getAllCate(): List<CategoryModel> = categoryDao.getAllCategory()

    override suspend fun getWallpaperByCategory(category: String): List<WallpaperModel> {
        return wallPaperDao.getWallPaperByCategory(category)
    }
    fun getWallpaperByCategoryMain(category: String): List<WallpaperModel> {
        return wallPaperDao.getWallPaperByCategory(category)
    }


    override suspend fun updateFavoriteWallpaper(id: Int, favorite: Boolean) {
        wallPaperDao.updateFavorite(id, favorite)
    }

    override suspend fun getWallPaperByAttr(attr: String): List<WallpaperModel> {
        return wallPaperDao.getWallpaperByAttr(attr)
    }
    fun getWallPaperByAttrMain(attr: String): List<WallpaperModel> {
        return wallPaperDao.getWallpaperByAttr(attr)
    }

    override suspend fun getFavoriteWallpaper(): List<WallpaperModel> {
        return wallPaperDao.getFavoriteWallPaper()
    }

    override suspend fun getDownloadWallpaper(): List<WallpaperModel> {
        return wallPaperDao.getDownloadWallPaper()
    }

    override suspend fun setPathDownloadWallpaper(
        id: Int,
        pathVideo: String,
        pathParallax: String,
        pathImage: String,
        download: Boolean
    ) {
        Log.e("downloadedWallpaper", "setPathDownloadWallpaper WallpaperRepository")
        wallPaperDao.setPathDownloadWallpaper(id, pathVideo, pathParallax, pathImage, download)
    }

    override suspend fun deleteImageDownloaded(id: Int) {
        wallPaperDao.setPathDownloadWallpaper(id, "", "", "", false)
    }
    fun getWallpaperDownloadMain(): List<WallpaperModel> {
       return wallPaperDao.getDownloadWallPaper()
    }

    override suspend fun getVersionApi(): VersionRepositoryModel {
        return apiService.getVersion()
    }

    override suspend fun getWallpaperByCurrentPage(limit: Int, page: Int): List<WallpaperModel> {
        return wallPaperDao.getWallpaperPage(limit, page * limit)
    }

    fun getWallpaperByCurrentPage2(limit: Int, page: Int): ArrayList<WallpaperModel> {
        val list = arrayListOf<WallpaperModel>()
        if (page == 1) {
            list.addAll(wallPaperDao.getWallpaperPage(limit, 0))
        } else {
            list.addAll(wallPaperDao.getWallpaperPage(limit, page * limit))
        }

        return list
    }

    suspend fun insertHistory(historySearchModel: HistorySearchModel) {
        historySearchDao.insert(historySearchModel)
    }

    suspend fun getHistory(): ArrayList<HistorySearchModel> {
        val list = arrayListOf<HistorySearchModel>()
        list.addAll(historySearchDao.getHistoryModel())
        if (list.size == 0) {
            list.add(HistorySearchModel(query = "Anime"))
            list.add(HistorySearchModel(query = "Halloween"))
            list.add(HistorySearchModel(query = "Abstract"))
            list.add(HistorySearchModel(query = "Christmas"))
            list.add(HistorySearchModel(query = "Festival"))
        }
        return list
    }

    fun getCountWallpaper(): Int{
        return wallPaperDao.getCountWallpaper()
    }
    fun getCountCategory(): Int{
        return categoryDao.getCountCategory()
    }


}