package com.itsol.vn.wallpaper.live.parallax.ui.activity.home.tab.homeTab

import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.snapping.SnapPosition
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.RoundedCornerShape

import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.navigation.compose.currentBackStackEntryAsState
import com.itsol.vn.wallpaper.live.parallax.LocalNavController
import com.itsol.vn.wallpaper.live.parallax.R
import com.itsol.vn.wallpaper.live.parallax.ads.AdsManager
import com.itsol.vn.wallpaper.live.parallax.ui.activity.components.ItemWallpaper
import com.itsol.vn.wallpaper.live.parallax.utils.Common
import com.itsol.vn.wallpaper.live.parallax.utils.Constants
import com.itsol.vn.wallpaper.live.parallax.utils.Router
import com.itsol.vn.wallpaper.live.parallax.viewModel.MainViewModel
import com.itsol.ironsourcelib.AdmobUtils
import com.itsol.ironsourcelib.AppOpenManager
import com.itsol.ironsourcelib.GoogleENative
import com.itsol.vn.wallpaper.live.parallax.MainActivity
import com.itsol.vn.wallpaper.live.parallax.ads.RemoteConfig
import com.itsol.vn.wallpaper.live.parallax.model.WallpaperModel
import com.itsol.vn.wallpaper.live.parallax.ui.activity.components.GradientDotsIndicator
import com.itsol.vn.wallpaper.live.parallax.ui.dialog.DialogIap
import com.valentinilk.shimmer.shimmer
import kotlinx.coroutines.delay
import kotlinx.coroutines.yield

@Composable
fun TabPopular(modifier: Modifier = Modifier, bought: Boolean = false) {
    val navController = LocalNavController.current
    val viewModel = hiltViewModel<MainViewModel>()
    val scope = rememberCoroutineScope()
    val context = LocalContext.current
    var currentPage by rememberSaveable { mutableStateOf(1) }
    val listWallpaper by rememberSaveable { mutableStateOf<ArrayList<WallpaperModel>>(arrayListOf()) }
//    val listSlider = remember {
//        mutableStateListOf(R.drawable.img_banner, R.drawable.img_banner, R.drawable.img_banner)
//    }
//    val stateSlider = rememberPagerState { listSlider.size }
    val stateList = rememberLazyGridState()
    var isLoadingPage by rememberSaveable { mutableStateOf(false) }
    var isLoading by rememberSaveable { mutableStateOf(true) }
    var remoteNative by remember { mutableStateOf("") }
    var isShowDialogIap by remember { mutableStateOf(false) }
    var isBought by remember { mutableStateOf(false) }
    LaunchedEffect(bought) {
        isBought = bought
    }
    LaunchedEffect(true) {
        isLoadingPage = true
        val listNew = viewModel.getWallpaperCurrentPae(10, 1)
        delay(1000)
        listWallpaper.addAll(listNew)
        isLoadingPage = false
//        AdsManager.logEvent(context, "Home_tab_popular_screen")
//        remoteNative = RemoteConfig.native_home
        //  listWallpaper.clear()
        //listWallpaper.addAll(viewModel.getWallpaperCurrentPae(10, 1))
//        isLoading = false
//        AdsManager.loadNative(context, AdsManager.nativeHome)
    }
    AdsManager.ScreenNameLogEffect("Home_tab_popular_screen")
//    LaunchedEffect(true) {
//        while (true) {
//            yield()
//            delay(2000) // Thời gian chờ giữa các lần chuyển trang
//            val nextPage = (stateSlider.currentPage + 1) % listSlider.size
//            stateSlider.animateScrollToPage(nextPage) // Chuyển trang tiếp theo
//        }
//    }
    LaunchedEffect(stateList) {
        snapshotFlow { stateList.layoutInfo.visibleItemsInfo.lastOrNull()?.index }
            .collect { lastVisibleItemIndex ->
                if (lastVisibleItemIndex != null) {
                    if (lastVisibleItemIndex >= listWallpaper.size - 4 && !isLoadingPage) {
                        if (listWallpaper.size != 0) {
                            isLoadingPage = true
                            currentPage = (listWallpaper.size / 10) + 1
                            val listNew = viewModel.getWallpaperCurrentPae(10, currentPage)
                            delay(1000)
                            listWallpaper.addAll(listNew)
                            isLoadingPage = false
                        } else {
                            isLoadingPage = true
                            val listNew = viewModel.getWallpaperCurrentPae(10, 1)
                            delay(1000)
                            listWallpaper.addAll(listNew)
                            isLoadingPage = false
                        }


                    }
                }

            }
    }
    var previousVisibleItems by remember { mutableStateOf<List<Int>>(emptyList()) }
    LaunchedEffect(stateList) {
        snapshotFlow { stateList.layoutInfo.visibleItemsInfo.map { it.index } }
            .collect { visibleItems ->
                val swipedOutItems = previousVisibleItems - visibleItems.toSet()
                if (swipedOutItems.isNotEmpty()) {
                    // Lấy vị trí gần nhất bị vuốt khỏi màn hình
                    val closestSwipedOutItem = swipedOutItems.maxOrNull()
                    closestSwipedOutItem?.let {
                        if ((it + 1) % 5 == 0) {
                            if (!AdsManager.isReloadNativeHome && remoteNative == "1") {
                                Log.e(
                                    "AAAAAAAAAAA",
                                    "TabPopular: isReloadNativeHome:${AdsManager.isReloadNativeHome}",
                                )
                                AdsManager.loadNativeHome(context, AdsManager.nativeHome)
                            }

                        }

                    }
                }
                // Cập nhật trạng thái hiển thị
                previousVisibleItems = visibleItems


            }
    }
    val backStackEntry = navController.currentBackStackEntryAsState()
    DisposableEffect(backStackEntry.value) {
        // Đăng ký lắng nghe sự kiện lifecycle của màn hình
        val lifecycle = backStackEntry.value?.lifecycle
        val observer = LifecycleEventObserver { _, event ->
            when (event) {
                Lifecycle.Event.ON_RESUME -> {
                    AppOpenManager.getInstance()
                        .enableAppResumeWithActivity(MainActivity::class.java)

                }

                Lifecycle.Event.ON_START -> {

                }

                else -> {

                }
            }
        }
        lifecycle?.addObserver(observer)

        onDispose {
            lifecycle?.removeObserver(observer)
        }
    }
    Column(
        modifier = modifier
            .fillMaxSize()
            .background(Color.Transparent)
            .padding(horizontal = 16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        LazyVerticalGrid(
            modifier = Modifier,
            columns = GridCells.Fixed(2),
            state = stateList,
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            items(count = listWallpaper.size) { index ->
                val nameCat by rememberSaveable {
                    mutableStateOf(
                        listWallpaper[index].categories
                    )
                }
                val cate by rememberSaveable {
                    mutableStateOf(
                        if (nameCat == "AI_Wallpaper") nameCat.replace(
                            "_",
                            " "
                        ) else nameCat
                    )
                }
                ItemWallpaper(
                    modifier = Modifier.height(320.dp)
                    // chiếm 1 column
                    ,
                    wallpaperModel = listWallpaper[index],
                    titleAttr = "",
                    isDownLoad = false,
                    isBought = (Common.checkCate(
                        listWallpaper[index].categories
                    )) && Common.getCurrentKeyIap(context, cate) == "",
                    onClickItem = {
                        val cate = if (it.categories == "AI_Wallpaper") it.categories.replace(
                            "_",
                            " "
                        ) else it.categories
                        if (Common.checkCate(it.categories)) {
                            if (Common.getCurrentKeyIap(context, cate) != "") {
                                val imageList = Common.parserListImage(it.urls)
                                when (imageList?.get(0)?.type) {
                                    Constants.PARALLAX -> {
                                        Common.isTypeSetWallpaper = Constants.PARALLAX
                                    }

                                    Constants.LIVE -> {
                                        Common.isTypeSetWallpaper = Constants.LIVE
                                    }

                                    else -> {
                                        Common.isTypeSetWallpaper = Constants.IMAGE4K
                                    }
                                }
                                val json = Common.wallpaperToJson(it)
                                navController.navigate("${Router.SET_WALLPAPER}?wallpaperModel=${json}")
                            } else {
                                var key = "0.5"
                                for (i in 0 until Common.listCatName.size) {
                                    if (it.categories == "AI_Wallpaper") {
                                        if (Common.listCatName[i].equals(
                                                it.categories.replace(
                                                    "_",
                                                    " "
                                                ), true
                                            )
                                        ) {
                                            key = Constants.listKeyIap[i]
                                        }
                                    } else {
                                        if (Common.listCatName[i].equals(
                                                it.categories,
                                                true
                                            )
                                        ) {
                                            key = Constants.listKeyIap[i]
                                        }
                                    }
                                }
                                navController.navigate(
                                    Router.getIapScreen(
                                        key,
                                        cate
                                    )
                                )
                            }
                        } else {
                            val imageList = Common.parserListImage(it.urls)
                            when (imageList?.get(0)?.type) {
                                Constants.PARALLAX -> {
                                    Common.isTypeSetWallpaper = Constants.PARALLAX
                                }

                                Constants.LIVE -> {
                                    Common.isTypeSetWallpaper = Constants.LIVE
                                }

                                else -> {
                                    Common.isTypeSetWallpaper = Constants.IMAGE4K
                                }
                            }
                            val json = Common.wallpaperToJson(it)
                            navController.navigate("${Router.SET_WALLPAPER}?wallpaperModel=${json}")
                        }

                    },
                    onClickFavorite = {
                        viewModel.updateFavoriteWallPaper(it.id, it.favorite)
                    })
            }

//            items(listWallpaper.size) {index ->
//                val nameCat by rememberSaveable {
//                    mutableStateOf(
//                        listWallpaper[if (AdmobUtils.isNetworkConnected(
//                                context
//                            )
//                        ) index - index / 5 else index].categories
//                    )
//                }
//                val cate by rememberSaveable {
//                    mutableStateOf(
//                        if (nameCat == "AI_Wallpaper") nameCat.replace(
//                            "_",
//                            " "
//                        ) else nameCat
//                    )
//                }
//                ItemWallpaper(
//                    modifier = Modifier.height(320.dp),
//                    // chiếm 1 colum,
//                    wallpaperModel = listWallpaper[index],
//                    titleAttr = "",
//                    isDownLoad = false,
//                    isBought = (Common.checkCate(listWallpaper[index].categories
//                    )) && Common.getCurrentKeyIap(context, cate) == "",
//                    onClickItem = {
//                        val cate = if (it.categories == "AI_Wallpaper") it.categories.replace(
//                            "_",
//                            " "
//                        ) else it.categories
//                        if (Common.checkCate(it.categories)) {
//                            if (Common.getCurrentKeyIap(context, cate) != "") {
//                                val imageList = Common.parserListImage(it.urls)
//                                when (imageList?.get(0)?.type) {
//                                    Constants.PARALLAX -> {
//                                        Common.isTypeSetWallpaper = Constants.PARALLAX
//                                    }
//
//                                    Constants.LIVE -> {
//                                        Common.isTypeSetWallpaper = Constants.LIVE
//                                    }
//
//                                    else -> {
//                                        Common.isTypeSetWallpaper = Constants.IMAGE4K
//                                    }
//                                }
//                                val json = Common.wallpaperToJson(it)
//                                navController.navigate("${Router.SET_WALLPAPER}?wallpaperModel=${json}")
//                            } else {
//                                var key = "0.5"
//                                for (i in 0 until Common.listCatName.size) {
//                                    if (it.categories == "AI_Wallpaper") {
//                                        if (Common.listCatName[i].equals(
//                                                it.categories.replace(
//                                                    "_",
//                                                    " "
//                                                ), true
//                                            )
//                                        ) {
//                                            key = Constants.listKeyIap[i]
//                                        }
//                                    } else {
//                                        if (Common.listCatName[i].equals(
//                                                it.categories,
//                                                true
//                                            )
//                                        ) {
//                                            key = Constants.listKeyIap[i]
//                                        }
//                                    }
//                                }
//                                navController.navigate(
//                                    Router.getIapScreen(
//                                        key,
//                                        cate
//                                    )
//                                )
//                            }
//                        } else {
//                            val imageList = Common.parserListImage(it.urls)
//                            when (imageList?.get(0)?.type) {
//                                Constants.PARALLAX -> {
//                                    Common.isTypeSetWallpaper = Constants.PARALLAX
//                                }
//
//                                Constants.LIVE -> {
//                                    Common.isTypeSetWallpaper = Constants.LIVE
//                                }
//
//                                else -> {
//                                    Common.isTypeSetWallpaper = Constants.IMAGE4K
//                                }
//                            }
//                            val json = Common.wallpaperToJson(it)
//                            navController.navigate("${Router.SET_WALLPAPER}?wallpaperModel=${json}")
//                        }
//
//                    },
//                    onClickFavorite = {
//                        viewModel.updateFavoriteWallPaper(it.id, it.favorite)
//                    })
//            }
            if (isLoadingPage) {
                item(span = { GridItemSpan(2) }) {
                    Box(
                        modifier = Modifier
                            .shimmer()
                            .fillMaxWidth()
                            .height(100.dp)
                            .clip(
                                RoundedCornerShape(12.dp)
                            )
                            .background(Color.LightGray)
                    ) {

                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Box(
                        modifier = Modifier
                            .shimmer()
                            .fillMaxWidth()
                            .height(100.dp)
                            .clip(
                                RoundedCornerShape(12.dp)
                            )
                            .background(Color.LightGray)
                    ) {

                    }

                }
            }


        }
    }
//    if(isShowDialogIap){
//        DialogIap(onDismiss = {
//            isShowDialogIap = false
//        }, onSubscribeSuccess = {
//            isShowDialogIap = false
//            isBought = true
//        })
//    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
private fun PreviewTabPopular() {
    TabPopular()
}