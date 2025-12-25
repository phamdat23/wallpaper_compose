package com.itsol.vn.wallpaper.live.parallax.ui.activity.home.tab.myCollection

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.itsol.vn.wallpaper.live.parallax.R
import com.itsol.vn.wallpaper.live.parallax.ads.AdsManager
import com.itsol.vn.wallpaper.live.parallax.ui.activity.components.TabLayout
import kotlinx.coroutines.launch

@Composable
fun MyCollection(modifier: Modifier = Modifier,onChangePage: () -> Unit) {
    val scopeCoroutine = rememberCoroutineScope()
    val context = LocalContext.current
    val listTitleTab = remember {
        mutableStateListOf(R.string.my_wallpaper, R.string.my_favorites)
    }
    val statePager = rememberPagerState(pageCount = { listTitleTab.size })
    var indexSelected by rememberSaveable {
        mutableStateOf(statePager.currentPage)
    }
    LaunchedEffect(statePager.currentPage) {
        snapshotFlow { statePager.currentPage }.collect {
            if(indexSelected!=it){
                onChangePage.invoke()
                indexSelected = it
            }

        }
    }
    LaunchedEffect(true) {
        AdsManager.logEvent(context, "my_collection_tab_screen")

    }
    AdsManager.ScreenNameLogEffect("my_collection_tab_screen")
    Column(
        modifier = modifier
            .fillMaxSize()
            .background(Color.Transparent)

    ) {
        TabLayout(indexSelected, listTitleTab) {
            scopeCoroutine.launch {
                statePager.scrollToPage(it)
            }
        }
        HorizontalPager(state = statePager, modifier = Modifier.weight(1f).padding(horizontal = 16.dp, vertical = 6.dp)) { index ->
            if(index==0){
                MyWallpaper()
            }else{
                MyFavorites()
            }
        }

    }
}


@Preview(showBackground = true)
@Composable
private fun PreviewMyCollection() {

}