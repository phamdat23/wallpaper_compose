package com.itsol.vn.wallpaper.live.parallax.ui.activity.home.tab.category

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.navigation.compose.currentBackStackEntryAsState
import com.itsol.vn.wallpaper.live.parallax.LocalNavController
import com.itsol.vn.wallpaper.live.parallax.R
import com.itsol.vn.wallpaper.live.parallax.ads.AdsManager
import com.itsol.vn.wallpaper.live.parallax.ui.activity.components.ItemCategory
import com.itsol.vn.wallpaper.live.parallax.utils.Router
import com.itsol.vn.wallpaper.live.parallax.viewModel.MainViewModel
import com.itsol.ironsourcelib.AdmobUtils
import com.itsol.ironsourcelib.AppOpenManager
import com.itsol.ironsourcelib.GoogleENative
import com.itsol.vn.wallpaper.live.parallax.MainActivity
import com.itsol.vn.wallpaper.live.parallax.ads.RemoteConfig
import com.itsol.vn.wallpaper.live.parallax.ui.activity.components.NotFoundLayout
import com.itsol.vn.wallpaper.live.parallax.ui.dialog.DialogIap
import com.itsol.vn.wallpaper.live.parallax.utils.Common
import com.itsol.vn.wallpaper.live.parallax.utils.Constants
import com.itsol.vn.wallpaper.live.parallax.utils.Purchase
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Composable
fun CategoryTab(modifier: Modifier = Modifier) {
    val context = LocalContext.current
    val navigationController = LocalNavController.current
    val scope = rememberCoroutineScope()
    val viewModel = hiltViewModel<MainViewModel>()
    val stateList = rememberLazyGridState()
    var remoteNativeCat by remember { mutableStateOf("") }
    var key by remember { mutableStateOf("") }
    LaunchedEffect(true) {
        viewModel.getAllCategory2()
//        remoteNativeCat = RemoteConfig.native_category

    }
    val listCategory by viewModel.category.collectAsState()

    LaunchedEffect(true) {
        AdsManager.logEvent(context, "category_tab_screen")
//        if(RemoteConfig.native_category=="1"){
//            AdsManager.loadNative(context, AdsManager.nativeCategory)
//        }
    }
    AdsManager.ScreenNameLogEffect("category_tab_screen")
    val backStackEntry = navigationController.currentBackStackEntryAsState()
    DisposableEffect(backStackEntry.value) {
        // Đăng ký lắng nghe sự kiện lifecycle của màn hình
        val lifecycle = backStackEntry.value?.lifecycle
        val observer = LifecycleEventObserver { _, event ->
            when (event) {
                Lifecycle.Event.ON_RESUME -> {
                    AppOpenManager.getInstance()
                        .enableAppResumeWithActivity(MainActivity::class.java)


//                    scope.launch {
//                        remoteNativeCat ="null"
//                        delay(50)
//                        remoteNativeCat =RemoteConfig.native_category
//                    }

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

    var previousVisibleItems by remember { mutableStateOf<List<Int>>(emptyList()) }
    LaunchedEffect(stateList) {
        snapshotFlow { stateList.layoutInfo.visibleItemsInfo.map { it.index } }
            .collect { visibleItems ->
                val swipedOutItems = previousVisibleItems - visibleItems.toSet()
                if (swipedOutItems.isNotEmpty()) {
                    // Lấy vị trí gần nhất bị vuốt khỏi màn hình
                    val closestSwipedOutItem = swipedOutItems.maxOrNull()
                    closestSwipedOutItem?.let {
                        if ((it + 1) % 3 == 0) {
                            if (!AdsManager.isReloadNativeHome && remoteNativeCat == "1") {
//                                AdsManager.loadNativeHome(context, AdsManager.nativeCategory)
                            }
                        }

                    }
                }
                // Cập nhật trạng thái hiển thị
                previousVisibleItems = visibleItems


            }
    }
    var isBought by remember { mutableStateOf(Purchase.isBought) }
    Column(
        modifier = modifier
            .fillMaxSize()
            .background(Color.Transparent)
    ) {
        Box(modifier = Modifier
            .weight(1f)
            .fillMaxWidth()) {
            if (listCategory.isEmpty()) {
                NotFoundLayout(modifier = Modifier.align(Alignment.Center))
            } else {
                LazyVerticalGrid(
                    columns = GridCells.Fixed(1),
                    state = stateList,
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    contentPadding = PaddingValues(horizontal = 16.dp, vertical = 12.dp)
                ) {
                    items(listCategory.size) { index ->
                        val category = listCategory[ index]
                        val cate = if (category.categoryName == "AI_Wallpaper") category.categoryName.replace(
                            "_",
                            " "
                        ) else category.categoryName
                        Common.getCurrentKeyIap(context,cate )!=""
                        ItemCategory(
                            modifier = Modifier.height(200.dp),
                            categoryModel = category,
                            isBought = ((Common.checkCate(category.categoryName))&&Common.getCurrentKeyIap(context, cate)==""),
                            onClickItem = {
                                if (Common.checkCate(category.categoryName)) {
                                    if (isBought||Common.getCurrentKeyIap(context, cate)!="") {
                                        if (AdsManager.isCLick) {
                                            if (RemoteConfig.inter_category == "1") {
                                                AdsManager.showInterAds(
                                                    context,
                                                    AdsManager.interCategory
                                                ) {
                                                    val title = category.categoryName
                                                    navigationController.navigate(Router.CATEGORY_DETAIL + "/$title") {
                                                        launchSingleTop = true
                                                        restoreState = true
                                                    }
                                                }
                                            } else {
                                                val title = category.categoryName
                                                navigationController.navigate(Router.CATEGORY_DETAIL + "/$title") {
                                                    launchSingleTop = true
                                                    restoreState = true
                                                }
                                            }

                                        }
                                    } else {
                                        for (i in 0 until Common.listCatName.size) {
                                            if (category.categoryName == "AI_Wallpaper") {
                                                if (Common.listCatName[i].equals(
                                                        category.categoryName.replace(
                                                            "_",
                                                            " "
                                                        ), true
                                                    )
                                                ) {
                                                    key = Constants.listKeyIap[i]
                                                }
                                            } else {
                                                if (Common.listCatName[i].equals(
                                                        category.categoryName,
                                                        true
                                                    )
                                                ) {
                                                    key = Constants.listKeyIap[i]
                                                }
                                            }
                                        }
                                        navigationController.navigate(
                                            Router.getIapScreen(
                                                key,
                                                if (category.categoryName == "AI_Wallpaper") category.categoryName.replace(
                                                    "_",
                                                    " "
                                                ) else category.categoryName
                                            )
                                        )
                                    }
                                } else {
                                    if (AdsManager.isCLick) {
                                        if (RemoteConfig.inter_category == "1") {
                                            AdsManager.showInterAds(
                                                context,
                                                AdsManager.interCategory
                                            ) {
                                                val title = category.categoryName
                                                navigationController.navigate(Router.CATEGORY_DETAIL + "/$title") {
                                                    launchSingleTop = true
                                                    restoreState = true
                                                }
                                            }
                                        } else {
                                            val title = category.categoryName
                                            navigationController.navigate(Router.CATEGORY_DETAIL + "/$title") {
                                                launchSingleTop = true
                                                restoreState = true
                                            }
                                        }

                                    }
                                }
                            })


                    }
                }
            }
        }

    }
//    if(isShowDialogIap){
//        DialogIap(
//            key = "", onDismiss = {
//            isShowDialogIap = false
//        }, onSubscribeSuccess = {
//            isShowDialogIap = false
//            isBought = true
//        })
//    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
private fun CategoryTabPreview() {

}