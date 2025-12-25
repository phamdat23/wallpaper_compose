package com.itsol.vn.wallpaper.live.parallax.ui.activity.home.tab.myCollection

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.navigation.compose.currentBackStackEntryAsState
import com.google.accompanist.swiperefresh.SwipeRefresh
import com.google.accompanist.swiperefresh.rememberSwipeRefreshState
import com.itsol.vn.wallpaper.live.parallax.LocalNavController
import com.itsol.vn.wallpaper.live.parallax.R
import com.itsol.vn.wallpaper.live.parallax.ui.activity.components.ItemWallpaper
import com.itsol.vn.wallpaper.live.parallax.utils.Common
import com.itsol.vn.wallpaper.live.parallax.utils.Constants
import com.itsol.vn.wallpaper.live.parallax.utils.Router
import com.itsol.vn.wallpaper.live.parallax.viewModel.MainViewModel
import com.itsol.vn.wallpaper.live.parallax.ads.AdsManager
import com.itsol.vn.wallpaper.live.parallax.ui.dialog.DialogIap

@Composable
fun MyFavorites(modifier: Modifier=Modifier) {
    val navController = LocalNavController.current
    val viewModel = hiltViewModel<MainViewModel>()
    val isLoading by viewModel.loading.collectAsState()
    val context= LocalContext.current
    val listWallpaper by viewModel.favoriteWallpaper.collectAsState()
    val stateGridView= rememberLazyGridState()
    var isShowDialogIap by remember { mutableStateOf(false) }
    var isBought by remember { mutableStateOf(Common.getBoughtIap(context)) }
    LaunchedEffect(true) {
        AdsManager.logEvent(context, "my_favorite_tab_screen")

    }
    AdsManager.ScreenNameLogEffect("my_favorite_tab_screen")
    val refreshState = rememberSwipeRefreshState(isRefreshing = isLoading)
    val backStackEntry = navController.currentBackStackEntryAsState()
    DisposableEffect(backStackEntry.value) {
        // Đăng ký lắng nghe sự kiện lifecycle của màn hình
        val lifecycle = backStackEntry.value?.lifecycle
        val observer = LifecycleEventObserver { _, event ->
            when (event) {
                Lifecycle.Event.ON_RESUME -> {
                    viewModel.getFavoriteWallpaper()
                }

                Lifecycle.Event.ON_START -> {
                }

                else -> {

                }
            }
        }

        // Thêm observer vào lifecycle
        lifecycle?.addObserver(observer)

        onDispose {
            // Xóa observer khi không cần thiết nữa
            lifecycle?.removeObserver(observer)
        }
    }
    Box(
        modifier = modifier
            .fillMaxSize()
            .background(Color.Transparent)
    ) {
        if(listWallpaper.isNotEmpty()){
            SwipeRefresh(
                state = refreshState,
                onRefresh = { viewModel.getFavoriteWallpaper() }
            ){
                LazyVerticalGrid(
                    columns = GridCells.Fixed(2),
                    state = stateGridView,
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                ){
                    items(listWallpaper){wallpaper->
                        val nameCat by rememberSaveable { mutableStateOf(wallpaper.categories) }
                        val cate by rememberSaveable{ mutableStateOf(if (nameCat == "AI_Wallpaper") nameCat.replace(
                            "_",
                            " "
                        ) else nameCat) }
                        ItemWallpaper(
                            modifier = Modifier.height(320.dp),
                            wallpaperModel = wallpaper,
                            titleAttr = "",
                            isDownLoad = false,
                            isBought = (Common.checkCate(wallpaper.categories))&&Common.getCurrentKeyIap(context, cate)=="",
                            onClickItem = {
                                val cate = if (it.categories == "AI_Wallpaper") it.categories.replace(
                                    "_",
                                    " "
                                ) else it.categories
                                if(Common.checkCate(it.categories)){
                                    if(Common.getCurrentKeyIap(context, cate)!=""){
                                        val imageList = Common.parserListImage(it.urls)
                                        when(imageList?.get(0)?.type){
                                            Constants.PARALLAX->{
                                                Common.isTypeSetWallpaper= Constants.PARALLAX
                                            }
                                            Constants.LIVE->{
                                                Common.isTypeSetWallpaper= Constants.LIVE
                                            }
                                            else->{
                                                Common.isTypeSetWallpaper= Constants.IMAGE4K
                                            }
                                        }
                                        val json = Common.wallpaperToJson(it)
                                        navController.navigate("${Router.SET_WALLPAPER}?wallpaperModel=${json}"){

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
                                    val imageList = Common.parserListImage(it.urls)
                                    when(imageList?.get(0)?.type){
                                        Constants.PARALLAX->{
                                            Common.isTypeSetWallpaper= Constants.PARALLAX
                                        }
                                        Constants.LIVE->{
                                            Common.isTypeSetWallpaper= Constants.LIVE
                                        }
                                        else->{
                                            Common.isTypeSetWallpaper= Constants.IMAGE4K
                                        }
                                    }
                                    val json = Common.wallpaperToJson(it)
                                    navController.navigate("${Router.SET_WALLPAPER}?wallpaperModel=${json}"){

                                    }
                                }

                            },
                            onClickFavorite = {
                                viewModel.updateFavoriteWallPaper(it.id, it.favorite)
                                viewModel._favoriteWallpaper.value = viewModel.favoriteWallpaper.value.filter { it.favorite }
                            })
                    }
                }
            }


        }else{
            Column(
                modifier = Modifier.align(Alignment.Center),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Image(painterResource(R.drawable.ic_empty_folder), contentDescription = "")
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = stringResource(R.string.your_folder_is_empty),
                    color = colorResource(R.color.color_bold_900),
                    textAlign = TextAlign.Center,
                    fontSize = 18.sp,
                    fontFamily = FontFamily(
                        Font(R.font.readexpro_light)
                    ),
                    fontWeight = FontWeight(300)
                )



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
private fun PreviewMyFavorites() {
    MyFavorites()
}