package com.itsol.vn.wallpaper.live.parallax.viewModel

import android.util.Log
import androidx.compose.ui.text.toLowerCase
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.itsol.vn.wallpaper.live.parallax.model.CategoryModel
import com.itsol.vn.wallpaper.live.parallax.model.HistorySearchModel
import com.itsol.vn.wallpaper.live.parallax.model.VersionRepositoryModel
import com.itsol.vn.wallpaper.live.parallax.model.WallpaperModel
import com.itsol.vn.wallpaper.live.parallax.repository.WallpaperRepository
import com.itsol.vn.wallpaper.live.parallax.utils.Common
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.io.File
import java.util.Locale
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    private val wallpaperRepository: WallpaperRepository
) : BaseViewModel() {

    private var _isLoading = MutableStateFlow(false)
    val loading = _isLoading.asStateFlow()

    private val wallPaper = MutableStateFlow(arrayListOf<WallpaperModel>())
    val listWallpaper: StateFlow<ArrayList<WallpaperModel>> = wallPaper.stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(5000),
        wallPaper.value
    )

    private val _categorys = MutableStateFlow(listOf<CategoryModel>())
    val category: StateFlow<List<CategoryModel>> = _categorys.stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(5000),
        _categorys.value
    )


    private val _wallpaperByAttr = MutableStateFlow(arrayListOf<WallpaperModel>())
    val wallpaperByAttr: StateFlow<List<WallpaperModel>> = _wallpaperByAttr.asStateFlow()

    private val _wallpaperByCategory = MutableStateFlow(listOf<WallpaperModel>())
    val wallpaperByCategory: StateFlow<List<WallpaperModel>> = _wallpaperByCategory.stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(5000),
        _wallpaperByCategory.value
    )

    val _favoriteWallpaper = MutableStateFlow(listOf<WallpaperModel>())
    val favoriteWallpaper: StateFlow<List<WallpaperModel>> = _favoriteWallpaper.asStateFlow()

    private val _downloadWallpaper = MutableStateFlow(listOf<WallpaperModel>())
    val downloadWallpaper: StateFlow<List<WallpaperModel>> = _downloadWallpaper.asStateFlow()

    private val _textQuery = MutableStateFlow("")
    val textQuery: StateFlow<String> = _textQuery.asStateFlow()

    private val _isSearching = MutableStateFlow(false)
    val isSearching: StateFlow<Boolean> = _isSearching.asStateFlow()

    private val _versionApi = MutableStateFlow(listOf<VersionRepositoryModel>())
    val versionApi: StateFlow<List<VersionRepositoryModel>> = _versionApi.asStateFlow()

    private val _wallpaperCurrentPage = MutableStateFlow(listOf<WallpaperModel>())
    val wallpaperCurrentPage: StateFlow<List<WallpaperModel>> = _wallpaperCurrentPage.asStateFlow()
    private var _isLoadingPage = MutableStateFlow(false)
    val isLoadingPage = _isLoadingPage.asStateFlow()


    private val _listWallpaperSearch = MutableStateFlow(listOf<WallpaperModel>())
    val listWallpaperSearch: StateFlow<List<WallpaperModel>> = _listWallpaperSearch.asStateFlow()

    private val _historySearch = MutableStateFlow(listOf<HistorySearchModel>())
    val historySearch: StateFlow<List<HistorySearchModel>> = _historySearch.asStateFlow()


    val wallpaperSearch = _textQuery
        .onEach { _isSearching.update { true } }
        .combine(MutableStateFlow(wallpaperRepository.getWallpaper())) { query, wallpaper ->
            if (query.trim().isEmpty()) {
                listOf<WallpaperModel>()
            } else {
                wallpaper.filter {
                    it.doseMatchSearchQuery(query)
                }
            }
        }
        .onEach {
            delay(1000)
            _isSearching.update { false }

        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(4000), listWallpaperSearch.value)

    fun onSearchTextChange(text: String) {
        _textQuery.value = text
    }


    fun getAllWallPaper() {
        viewModelScope.launch(Dispatchers.IO) {
            val list = wallpaperRepository.getAllWallpaper() as ArrayList<WallpaperModel>
            wallPaper.emit(list)
            Log.e("AAAAAAAAAAAAA", "getAllWallPaper: ${wallPaper.value.size}")
        }
    }

    fun getAllWallPaper2() {
        viewModelScope.launch(Dispatchers.IO) {
            val list = wallpaperRepository.getWallpaper()
            wallPaper.value.clear()
            wallPaper.value.addAll(list)
        }
    }

    fun getAndUpdateWallpaper() {
        viewModelScope.launch(Dispatchers.IO) {
            val list = wallpaperRepository.getAndUpdateWallpaper() as ArrayList<WallpaperModel>
            wallPaper.emit(list)
        }
    }


    fun getWallPaperLimitOffset(limit: Int, page: Int) {
        viewModelScope.launch(Dispatchers.IO) {
            _isLoadingPage.value = true
            _isLoadingPage.value = false

        }
    }

    fun getWallpaperCurrentPae(limit: Int, page: Int): ArrayList<WallpaperModel> {
        return wallpaperRepository.getWallpaperByCurrentPage2(limit, page)
    }

    fun getAllCategory() {
        viewModelScope.launch(Dispatchers.IO) {
            val list = wallpaperRepository.getALlCategory()
            _categorys.emit(list)
        }
    }

    fun getAllCategory2() {
        viewModelScope.launch(Dispatchers.IO) {
            val list = wallpaperRepository.getAllCategory2()
            _categorys.emit(list)
        }
    }

    fun getAllChooseSearch(): List<HistorySearchModel> {
        val list = arrayListOf<HistorySearchModel>()
        wallpaperRepository.getAllCate().map {
            list.add(HistorySearchModel(query = Common.capitalizeFirstLetter( it.categoryName)))
        }
        return list
    }

    fun getAndUpdateAllCategory() {
        viewModelScope.launch(Dispatchers.IO) {
            val list = wallpaperRepository.getAndUpdateCategory()
            _categorys.emit(list)
        }
    }


    fun downloadedWallpaper(
        id: Int,
        pathVideo: String,
        pathParallax: String,
        pathImage: String,
        download: Boolean
    ) {
        viewModelScope.launch(Dispatchers.IO) {
            wallpaperRepository.setPathDownloadWallpaper(
                id,
                pathVideo,
                pathParallax,
                pathImage,
                download
            )
        }
    }

    fun deleteImageDownloaded(wallpaper: WallpaperModel) {
        viewModelScope.launch(Dispatchers.IO) {
            wallpaperRepository.deleteImageDownloaded(wallpaper.id)
            _downloadWallpaper.value = _downloadWallpaper.value.filter { it.id != wallpaper.id }
//            _downloadWallpaper.value.remove(wallpaper)
        }
    }

    fun updateFavoriteWallPaper(id: Int, favorite: Boolean) {
        viewModelScope.launch(Dispatchers.IO) {
            wallpaperRepository.updateFavoriteWallpaper(id, favorite)
        }
    }

    fun getWallpaperByCategory(category: String) {
        viewModelScope.launch(Dispatchers.IO) {
            if (category.toLowerCase(Locale.ROOT) == "ai wallpaper"||category.toLowerCase(Locale.ROOT) == "magic fluids") {
                val list = wallpaperRepository.getWallpaperByCategory(category.replace(" ", "_"))
                _wallpaperByCategory.emit(list)
            } else if (category.toLowerCase(Locale.ROOT) == "cats"||category.toLowerCase(Locale.ROOT) == "games") {
                val list = wallpaperRepository.getWallpaperByCategory(category.replace("s", ""))
                _wallpaperByCategory.emit(list)
            }
            else if (category.toLowerCase(Locale.ROOT) == "halloween") {
                val list = wallpaperRepository.getWallpaperByCategory("hallowen")
                _wallpaperByCategory.emit(list)
            } else {
                val list = wallpaperRepository.getWallpaperByCategory(category)
                _wallpaperByCategory.emit(list)
            }

        }
    }

    fun getWallpaperByCate(category: String): List<WallpaperModel>{
        if (category.toLowerCase(Locale.ROOT) == "ai wallpaper"||category.toLowerCase(Locale.ROOT) == "magic fluids") {
            val list = wallpaperRepository.getWallpaperByCategoryMain(category.replace(" ", "_"))
            return list
        } else if (category.toLowerCase(Locale.ROOT) == "cats"||category.toLowerCase(Locale.ROOT) == "games") {
            val list = wallpaperRepository.getWallpaperByCategoryMain(category.replace("s", ""))
            return list
        }
        else if (category.toLowerCase(Locale.ROOT) == "halloween") {
            val list = wallpaperRepository.getWallpaperByCategoryMain("hallowen")
            return list
        } else {
            val list = wallpaperRepository.getWallpaperByCategoryMain(category)
            return list
        }
    }


    fun getWallpaperByAttr(attr: String) {
        viewModelScope.launch(Dispatchers.IO) {
            Log.e("AAAAAAAA", "getWallpaperByAttr:$attr ")
            val list = wallpaperRepository.getWallPaperByAttr(attr)
            _isLoading.value = true
            _wallpaperByAttr.value.clear()
            _wallpaperByAttr.value.addAll(list)
            delay(1000)
            _isLoading.value = false
        }
    }
    fun getWallpaperByAttrMain(attr: String): List<WallpaperModel> {
        _isLoading.value = true
        Log.e("AAAAAAAA", "getWallpaperByAttr:$attr ")
        val list = wallpaperRepository.getWallPaperByAttrMain(attr)
        _isLoading.value = false
        return list
    }

    fun getFavoriteWallpaper() {
        viewModelScope.launch(Dispatchers.IO) {
            val list = wallpaperRepository.getFavoriteWallpaper()
            _isLoading.value = true
            _favoriteWallpaper.emit(list)
            delay(1000)
            _isLoading.value = false
        }
    }

    fun getDownloadWallpaper() {
        viewModelScope.launch(Dispatchers.IO) {
            val list = wallpaperRepository.getDownloadWallpaper()
            _isLoading.value = true
            _downloadWallpaper.emit(list)
            _isLoading.value = false
        }
    }

    fun getDownloadWallpaperMain(): List<WallpaperModel> {
        viewModelScope.launch {
            _isLoading.value = true
            delay(500)
            _isLoading.value = false
        }

        return wallpaperRepository.getWallpaperDownloadMain()
    }

    fun getVersionApi() {
        viewModelScope.launch(Dispatchers.IO) {
            val api = wallpaperRepository.getVersionApi()
            _versionApi.emit(listOf(api))
            Log.e("AAAAAAAAAA", "getVersionApi:${_versionApi.value.size} ")
        }
    }

    fun insertHistory(query: String) {
        viewModelScope.launch(Dispatchers.IO) {
            wallpaperRepository.insertHistory(HistorySearchModel(query = query))
        }
    }

    fun getHistory() {
        viewModelScope.launch(Dispatchers.IO) {
            _historySearch.update { wallpaperRepository.getHistory() }
        }
    }

    fun getCountWallpaper(): Int {
        return wallpaperRepository.getCountWallpaper()
    }

    fun getCountCategory(): Int {
        return wallpaperRepository.getCountCategory()
    }

}