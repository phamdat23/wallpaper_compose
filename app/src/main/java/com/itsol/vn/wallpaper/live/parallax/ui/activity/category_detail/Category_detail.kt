package com.itsol.vn.wallpaper.live.parallax.ui.activity.category_detail

import android.annotation.SuppressLint
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
//noinspection UsingMaterialAndMaterial3Libraries
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.itsol.vn.wallpaper.live.parallax.LocalNavController
import com.itsol.vn.wallpaper.live.parallax.R
import com.itsol.vn.wallpaper.live.parallax.ads.AdsManager
import com.itsol.vn.wallpaper.live.parallax.ads.RemoteConfig
import com.itsol.vn.wallpaper.live.parallax.ui.activity.components.ItemWallpaper
import com.itsol.vn.wallpaper.live.parallax.ui.activity.components.NotFoundLayout
import com.itsol.vn.wallpaper.live.parallax.utils.Common
import com.itsol.vn.wallpaper.live.parallax.utils.Constants
import com.itsol.vn.wallpaper.live.parallax.utils.Router
import com.itsol.vn.wallpaper.live.parallax.viewModel.MainViewModel

@SuppressLint("RestrictedApi")
@Composable
fun CategoryDetail(
    title: String,
    modifier: Modifier = Modifier
) {
    val navigationController = LocalNavController.current
    val viewModel = hiltViewModel<MainViewModel>()
    val context = LocalContext.current
    LaunchedEffect(Unit) {
        viewModel.getWallpaperByCategory(title)
    }
    val listWallpaperByCategory by viewModel.wallpaperByCategory.collectAsState()
    LaunchedEffect(true) {
        AdsManager.logEvent(context, Router.CATEGORY_DETAIL)
        if (RemoteConfig.native_view_wallpaper == "1") {
            AdsManager.loadNative(context, AdsManager.nativeViewWallpaper)
        }

    }
    AdsManager.ScreenNameLogEffect(Router.CATEGORY_DETAIL)
    var isBackStack by remember { mutableStateOf(true) }
    var isShowDialogIap by remember { mutableStateOf(false) }
    var isBought by remember { mutableStateOf(Common.getBoughtIap(context)) }
    Column(
        modifier
            .fillMaxSize()
            .background(color = colorResource(R.color.color_bg))
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 8.dp, end = 16.dp, top = 30.dp, bottom = 16.dp)
        ) {
            Image(
                modifier = Modifier
                    .clickable {
                        if (isBackStack) {
                            isBackStack = false
                            if (navigationController.currentBackStack.value.isNotEmpty()) {
                                navigationController.popBackStack()
                            }
                        }
                    }
                    .padding(8.dp),
                imageVector = ImageVector.vectorResource(R.drawable.ic_arrow_back),
                colorFilter = ColorFilter.tint(colorResource(R.color.color_bold_900)),
                contentDescription = ""
            )
            Text(
                text = Common.capitalizeFirstLetter(title),
                color = colorResource(R.color.color_bold_900),
                fontSize = 20.sp,
                fontWeight = FontWeight(600),
                fontFamily = FontFamily(
                    Font(R.font.readexpro_semibold)
                )
            )
        }
        Box(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
        ) {

            if (listWallpaperByCategory.isNotEmpty()) {
                LazyVerticalGrid(
                    columns = GridCells.Fixed(2),

                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                    contentPadding = PaddingValues(bottom = 10.dp)
                ) {
                    items(listWallpaperByCategory) {
                        ItemWallpaper(
                            modifier = Modifier.height(320.dp),
                            titleAttr = "",
                            isDownLoad = false,
                            isBought = (Common.checkCate(it.categories) && !isBought),
                            wallpaperModel = it,
                            onClickItem = {
                                val cate =
                                    if (it.categories == "AI_Wallpaper") it.categories.replace(
                                        "_",
                                        " "
                                    ) else it.categories
                                if (Common.checkCate(it.categories)) {
                                    if (isBought && Common.getCurrentKeyIap(context, cate)
                                            .equals(cate, ignoreCase = true)
                                    ) {
                                        Common.isTypeSetWallpaper = Constants.IMAGE4K
                                        val json = Common.wallpaperToJson(it)
                                        navigationController.navigate("${Router.SET_WALLPAPER}?wallpaperModel=${json}")
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
                                        navigationController.navigate(
                                            Router.getIapScreen(
                                                key,
                                                cate
                                            )
                                        )
                                    }
                                } else {
                                    Common.isTypeSetWallpaper = Constants.IMAGE4K
                                    val json = Common.wallpaperToJson(it)
                                    navigationController.navigate("${Router.SET_WALLPAPER}?wallpaperModel=${json}")
                                }

                            },
                            onClickFavorite = { it ->
                                viewModel.updateFavoriteWallPaper(it.id, it.favorite)
                            })
                    }
                }
            } else {
                NotFoundLayout(modifier = Modifier.align(Alignment.Center))
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

@Preview(showBackground = true)
@Composable
private fun CategoryDetailPreview() {
}