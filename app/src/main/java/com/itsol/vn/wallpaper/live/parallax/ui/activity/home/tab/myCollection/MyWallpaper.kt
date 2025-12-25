package com.itsol.vn.wallpaper.live.parallax.ui.activity.home.tab.myCollection

import android.content.Context
import android.util.Log
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
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.toMutableStateList
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
import com.google.accompanist.swiperefresh.SwipeRefresh
import com.google.accompanist.swiperefresh.rememberSwipeRefreshState
import com.itsol.vn.wallpaper.live.parallax.LocalNavController
import com.itsol.vn.wallpaper.live.parallax.R
import com.itsol.vn.wallpaper.live.parallax.model.ImageTypes
import com.itsol.vn.wallpaper.live.parallax.ui.activity.components.ItemWallpaper
import com.itsol.vn.wallpaper.live.parallax.utils.Common
import com.itsol.vn.wallpaper.live.parallax.utils.Constants
import com.itsol.vn.wallpaper.live.parallax.utils.Router
import com.itsol.vn.wallpaper.live.parallax.viewModel.MainViewModel
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.itsol.vn.wallpaper.live.parallax.ads.AdsManager
import com.itsol.vn.wallpaper.live.parallax.ui.activity.components.ButtonSet
import java.io.File


@Composable
fun MyWallpaper(modifier: Modifier = Modifier) {
    val navController = LocalNavController.current
    val viewModel = hiltViewModel<MainViewModel>()
    val isLoading by viewModel.loading.collectAsState()
    val context = LocalContext.current
    LaunchedEffect(Unit) {

    }
    val listWallpaper by viewModel.downloadWallpaper.collectAsState()
    Log.e("list size", "${listWallpaper.size}")
    val stateGridView = rememberLazyGridState()

    LaunchedEffect(true) {
        viewModel.getDownloadWallpaper()
        AdsManager.logEvent(context, "my_wallpaper_tab_screen")

    }
    AdsManager.ScreenNameLogEffect("my_wallpaper_tab_screen")
    val refreshState = rememberSwipeRefreshState(isRefreshing = isLoading)

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(Color.Transparent)

    ) {
        if (listWallpaper.isNotEmpty()) {
            SwipeRefresh(
                state = refreshState,
                onRefresh = {  viewModel.getDownloadWallpaper() }
            ){
                LazyVerticalGrid(
                    columns = GridCells.Fixed(2),
                    state = stateGridView,
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                ) {
                    items(listWallpaper) { wallpaper ->
                        ItemWallpaper(
                            modifier = Modifier.height(320.dp),
                            wallpaperModel = wallpaper,
                            isDownLoad =true,
                            titleAttr = "",
                            onClickItem = {
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
                                navController.navigate("${Router.SET_WALLPAPER}?wallpaperModel=${json}")
                            },
                            onClickFavorite = {
                                val fileNameImage = File(it.pathImage).name
                                val fileNameVideo= File(it.pathVideo).name
                                val fileNameParallax = File(it.pathParallax).path.substringAfterLast("/")
                                deleteFile(context,fileNameImage)
                                deleteFile(context,fileNameVideo)
                                deleteFileFolder(context,fileNameParallax)
                                viewModel.deleteImageDownloaded(it)
                            }
                        )
                    }
                }
            }

        } else {
            Column(
                modifier = Modifier.align(Alignment.Center),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Image(painterResource(R.drawable.ic_empty_folder), contentDescription = "")
                Spacer(modifier = Modifier.height(10.dp))
                Text(
                    text = stringResource(R.string.text_empty),
                    color = colorResource(R.color.color_bold_900),
                    textAlign = TextAlign.Center,
                    fontSize = 18.sp,
                    fontFamily = FontFamily(
                        Font(R.font.readexpro_light)
                    ),
                    fontWeight = FontWeight(300)
                )
                Spacer(modifier = Modifier.height(40.dp))
                ButtonSet(text = stringResource(R.string.add_wallpaper)){
                    AdsManager.logEvent(context, "AddWallpaper_click")
                    navController.navigate(route = Router.HOME) {
                        popUpTo(0) {
                            inclusive = true
                        }
                        launchSingleTop = true
                        restoreState = true
                    }
                }
            }
        }


    }

}
fun deleteFile(context: Context,fileName: String){
    if(fileName!=""){
        val file = File(context.getExternalFilesDir(""),fileName )
        if(file.exists()){
            val a =file.delete()
            Log.e("AAAAAAAA", "deleteFile:$fileName : $a", )
        }
    }

}
fun deleteFileFolder(context: Context,fileName: String){
    if(fileName!=""){
        val file = File(context.getExternalFilesDir(""),fileName )
        if(file.exists()){
            val a =file.deleteRecursively()
            Log.e("AAAAAAAA", "deleteFile:$fileName : $a", )
        }
    }

}


@Preview(showBackground = true)
@Composable
private fun PreviewMyWallpaper() {
    MyWallpaper()

}