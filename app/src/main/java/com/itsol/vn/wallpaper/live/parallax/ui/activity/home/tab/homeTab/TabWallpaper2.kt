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
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.itsol.vn.wallpaper.live.parallax.LocalNavController
import com.itsol.vn.wallpaper.live.parallax.R
import com.itsol.vn.wallpaper.live.parallax.ui.activity.components.ItemWallpaper
import com.itsol.vn.wallpaper.live.parallax.utils.Common
import com.itsol.vn.wallpaper.live.parallax.utils.Router
import com.itsol.vn.wallpaper.live.parallax.viewModel.MainViewModel
import com.google.accompanist.swiperefresh.SwipeRefresh
import com.google.accompanist.swiperefresh.rememberSwipeRefreshState
import com.itsol.vn.wallpaper.live.parallax.ads.AdsManager
import com.itsol.vn.wallpaper.live.parallax.utils.Constants

@Composable
fun TabWallpaper2(modifier: Modifier = Modifier, titleAttr: String) {
    val navController = LocalNavController.current
    val viewModel = hiltViewModel<MainViewModel>()
    val context = LocalContext.current
    var arrt by rememberSaveable {
        mutableStateOf(Constants.LIVE)
    }
    val isLoading by viewModel.loading.collectAsState()
    val listWallpaper by viewModel.wallpaperByAttr.collectAsState()
    LaunchedEffect(true) {
        AdsManager.logEvent(context, "Home_tab_${Constants.LIVE}_screen")
        Common.isTypeSetWallpaper= Constants.LIVE
        viewModel.getWallpaperByAttr(Constants.LIVE)
    }
    AdsManager.ScreenNameLogEffect("Home_tab_${Constants.LIVE}_screen")
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
            onRefresh = { viewModel.getWallpaperByAttr(arrt) }
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
                    ItemWallpaper(
                        modifier = Modifier.height(320.dp),
                        wallpaperModel = wallpaper,
                        titleAttr = arrt,
                        isDownLoad = false,
                        onClickItem = {

                            val json = Common.wallpaperToJson(it)
                            navController.navigate("${Router.SET_WALLPAPER}?wallpaperModel=${json}"){
                                launchSingleTop =true
                            }
                        },
                        onClickFavorite = {
                            viewModel.updateFavoriteWallPaper(it.id, it.favorite)
                        })
                }
            }
        }

    }

}

@Preview(showBackground = true, showSystemUi = true)
@Composable
private fun PreviewTabWallpaper() {
    TabWallpaper(titleAttr = stringResource(R.string.wallpaper_4d))
}