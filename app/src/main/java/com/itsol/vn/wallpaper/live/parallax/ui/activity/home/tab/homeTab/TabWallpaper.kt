package com.itsol.vn.wallpaper.live.parallax.ui.activity.home.tab.homeTab

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.runtime.toMutableStateList
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.hilt.navigation.compose.hiltViewModel
import com.itsol.vn.wallpaper.live.parallax.LocalNavController
import com.itsol.vn.wallpaper.live.parallax.R
import com.itsol.vn.wallpaper.live.parallax.ui.activity.components.ItemWallpaper
import com.itsol.vn.wallpaper.live.parallax.utils.Common
import com.itsol.vn.wallpaper.live.parallax.utils.Router
import com.itsol.vn.wallpaper.live.parallax.viewModel.MainViewModel
import com.google.accompanist.swiperefresh.SwipeRefresh
import com.google.accompanist.swiperefresh.rememberSwipeRefreshState
import com.itsol.ironsourcelib.AdmobUtils
import com.itsol.vn.wallpaper.live.parallax.ads.AdsManager
import com.itsol.vn.wallpaper.live.parallax.ui.dialog.DialogIap
import com.itsol.vn.wallpaper.live.parallax.utils.Constants

@Composable
fun TabWallpaper(modifier: Modifier = Modifier, titleAttr: String) {
    val navController = LocalNavController.current
    val viewModel = hiltViewModel<MainViewModel>()
    val context = LocalContext.current
    var arrt by rememberSaveable {
        mutableStateOf(titleAttr)
    }
    val isLoading by viewModel.loading.collectAsState()
    var listWallpaper =viewModel.getWallpaperByAttrMain(titleAttr).toMutableStateList()
    var isShowDialogIap by remember { mutableStateOf(false) }
    var isBought by remember { mutableStateOf(Common.getBoughtIap(context)) }
    LaunchedEffect(true) {
        AdsManager.logEvent(context, "Home_tab_${titleAttr}_screen")
        Common.isTypeSetWallpaper= titleAttr

    }
    AdsManager.ScreenNameLogEffect("Home_tab_${titleAttr}_screen")
    val refreshState = rememberSwipeRefreshState(isRefreshing = isLoading)
    val stateList = rememberLazyGridState()
    Column(
        modifier = modifier
            .fillMaxSize()
            .background(Color.Transparent)
            .padding(horizontal = 16.dp)
    ) {
        SwipeRefresh(
            state = refreshState,
            onRefresh = {listWallpaper= viewModel.getWallpaperByAttrMain(arrt).toMutableStateList() }
        ) {
            LazyVerticalGrid(
                columns = GridCells.Fixed(2),
                state = stateList,
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier
                    .fillMaxSize()
                    .padding(top = 8.dp)
            ) {
                items(listWallpaper) { wallpaper ->
                    val nameCat by rememberSaveable { mutableStateOf(wallpaper.categories) }
                    val cate by rememberSaveable{ mutableStateOf(if (nameCat == "AI_Wallpaper") nameCat.replace(
                        "_",
                        " "
                    ) else nameCat) }
                    ItemWallpaper(
                        modifier = Modifier.height(320.dp),
                        wallpaperModel = wallpaper,
                        isBought = (Common.checkCate(wallpaper.categories))&&Common.getCurrentKeyIap(context, cate)=="",
                        titleAttr = arrt,
                        isDownLoad = false,
                        onClickItem = {
                            val cate = if (it.categories == "AI_Wallpaper") it.categories.replace(
                                "_",
                                " "
                            ) else it.categories
                            if(Common.checkCate(wallpaper.categories)){
                                if(Common.getCurrentKeyIap(context, cate)!=""){
                                    val json = Common.wallpaperToJson(it)
                                    navController.navigate("${Router.SET_WALLPAPER}?wallpaperModel=${json}"){
                                        launchSingleTop =true
                                    }
                                }else{
                                    var key ="0.5"
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
                                            if (it.categories == "AI_Wallpaper") it.categories.replace(
                                                "_",
                                                " "
                                            ) else it.categories
                                        )
                                    )
                                }
                            }else{
                                val json = Common.wallpaperToJson(it)
                                navController.navigate("${Router.SET_WALLPAPER}?wallpaperModel=${json}"){
                                    launchSingleTop =true
                                }
                            }

                        },
                        onClickFavorite = {
                            viewModel.updateFavoriteWallPaper(it.id, it.favorite)
                        })
                }
            }
        }

    }
//    if(isShowDialogIap){
//        DialogIap(onDismiss = {
//            isShowDialogIap= false
//        }, onSubscribeSuccess = {
//            isShowDialogIap= false
//            isBought = true
//        })
//    }

}

@Preview(showBackground = true, showSystemUi = true)
@Composable
private fun PreviewTabWallpaper() {
    TabWallpaper(titleAttr = stringResource(R.string.wallpaper_4d))
}