package com.itsol.vn.wallpaper.live.parallax.ui.activity.home.tab.homeTab

import android.util.Log
import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
//noinspection UsingMaterialAndMaterial3Libraries
import androidx.compose.material.Text
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.itsol.vn.wallpaper.live.parallax.LocalNavController
import com.itsol.vn.wallpaper.live.parallax.R
import com.itsol.vn.wallpaper.live.parallax.ads.AdsManager
import com.itsol.vn.wallpaper.live.parallax.ads.RemoteConfig
import com.itsol.vn.wallpaper.live.parallax.utils.Constants
import com.itsol.vn.wallpaper.live.parallax.utils.Router
import com.itsol.vn.wallpaper.live.parallax.viewModel.MainViewModel
import kotlinx.coroutines.launch


@Composable
fun HomeTab(modifier: Modifier = Modifier, isSubscribe: Boolean, onchangePage: () -> Unit) {
    val scopeCoroutine = rememberCoroutineScope()
    val context = LocalContext.current
    val listItemTab = remember {
        mutableStateListOf(
            ItemTab(
                title = R.string.popular,
                R.drawable.ic_tab_popular,
                R.drawable.ic_tab_popular
            ),
            ItemTab(
                title = R.string.wallpaper_4d,
                R.drawable.ic_tab_4d,
                R.drawable.ic_tab_4d
            ),
            ItemTab(
                title = R.string.live_wallpaper,
                R.drawable.ic__tab_live,
                R.drawable.ic__tab_live
            ),
            ItemTab(
                title = R.string.wallpaper_4k,
                R.drawable.ic_tab_4k,
                R.drawable.ic_tab_4k
            ),
        )
    }

    val statePager = rememberPagerState(pageCount = { listItemTab.size })
    var indexSelected by rememberSaveable {
        mutableStateOf(statePager.currentPage)
    }
    var isBought by remember { mutableStateOf(isSubscribe) }
    LaunchedEffect(isSubscribe) {
        isBought = isSubscribe
    }

    LaunchedEffect(statePager.currentPage) {
        snapshotFlow { statePager.currentPage }.collect {
//            if (indexSelected != it) {
//                indexSelected = it
//                onchangePage.invoke()
//            }
        }
    }
    LaunchedEffect(true) {
        AdsManager.logEvent(context, "Home_tab_screen")

    }
    AdsManager.ScreenNameLogEffect("Home_tab_screen")

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(Color.Transparent)
    ) {
        TabLayout(indexSelected, listItemTab) {
            scopeCoroutine.launch {
                statePager.scrollToPage(it)
                indexSelected = it
                onchangePage.invoke()
            }
        }
        HorizontalPager(
            state = statePager,
            modifier = Modifier.padding(top = 10.dp).weight(1f),
            userScrollEnabled = false,
            reverseLayout = true,

            ) { index ->
            indexSelected = index
            if (indexSelected == 0) {
                TabPopular(bought = isBought)
            } else if (indexSelected == 1) {
                TabWallpaper(titleAttr = Constants.PARALLAX)
            } else if (indexSelected == 2) {
                TabWallpaper(titleAttr = Constants.LIVE)
            } else if (indexSelected == 3) {
                TabWallpaper(titleAttr = Constants.IMAGE4K)
            }
        }
    }
}

@Composable
private fun TabLayout(indexSelected: Int, listItemTab: List<ItemTab>, onClickItem: (Int) -> Unit) {
    var widthIndicator by rememberSaveable {
        mutableStateOf(IntSize.Zero.width)
    }
    Box {
        Spacer(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
                .height(1.dp)
                .background(
                    colorResource(R.color.color_neutral_300)
                )
        )
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .wrapContentHeight()
                .padding(horizontal = 4.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            if (indexSelected == 0) {
                Column(
                    modifier = Modifier
                        .wrapContentWidth()
                        .padding(horizontal = 6.dp)
                        .background(color = colorResource(R.color.color_bg_tab))
                        .clickable {
                            onClickItem.invoke(0)
                        },
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Spacer(modifier = Modifier.height(6.dp))
                    Row(
                        modifier = Modifier
                            .wrapContentHeight()
                            .onGloballyPositioned {
                                widthIndicator = it.size.width / listItemTab.size
                            },
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Image(
                            ImageVector.vectorResource(listItemTab[0].iconSelected),
                            colorFilter = ColorFilter.tint(colorResource(R.color.color_primary)),
                            contentDescription = "",
                            modifier = Modifier.size(24.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = stringResource(listItemTab[0].title),
                            fontSize = 12.sp,
                            textAlign = TextAlign.Start,
                            color = colorResource(R.color.color_primary),
                            fontWeight = FontWeight(700),
                            fontFamily = FontFamily(Font(R.font.readexpro_bold)),
                        )
                    }
                    Spacer(modifier = Modifier.height(6.dp))
                    Box(
                        modifier = Modifier
                            .width((widthIndicator + 50).dp)
                            .height(1.5.dp)
                            .background(
                                Brush.linearGradient(
                                    listOf(
                                        colorResource(R.color.color_primary),
                                        colorResource(R.color.color_primary),
                                        colorResource(R.color.color_primary)
                                    )
                                )
                            )
                    )
                }

            } else {
                Row(
                    modifier = Modifier
                        .wrapContentSize()
                        .padding(horizontal = 12.dp)
                        .clickable {
                            onClickItem.invoke(0)
                        },
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    Image(
                        ImageVector.vectorResource(listItemTab[0].iconRes),
                        contentDescription = "",
                        modifier = Modifier
                            .size(36.dp)
                            .padding(4.dp)

                    )
                }

            }
            if (indexSelected == 1) {
                Column(
                    modifier = Modifier
                        .wrapContentWidth()
                        .padding(horizontal = 6.dp)
                        .background(color = colorResource(R.color.color_bg_tab))
                        .clickable {
                            onClickItem.invoke(1)
                        },
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Spacer(modifier = Modifier.height(6.dp))
                    Row(
                        modifier = Modifier
                            .wrapContentHeight()
                            .onGloballyPositioned {
                                widthIndicator = it.size.width / listItemTab.size
                            },
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Image(
                            ImageVector.vectorResource(listItemTab[1].iconSelected),
                            colorFilter = ColorFilter.tint(colorResource(R.color.color_primary)),
                            contentDescription = "",
                            modifier = Modifier.size(24.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = stringResource(listItemTab[1].title),
                            fontSize = 12.sp,
                            textAlign = TextAlign.Start,
                            color = colorResource(R.color.color_primary),
                            fontWeight = FontWeight(700),
                            fontFamily = FontFamily(Font(R.font.readexpro_bold)),
                        )
                    }
                    Spacer(modifier = Modifier.height(6.dp))
                    Box(
                        modifier = Modifier
                            .width((widthIndicator + 50).dp)
                            .height(1.5.dp)
                            .background(
                                Brush.linearGradient(
                                    listOf(
                                        colorResource(R.color.color_primary),
                                        colorResource(R.color.color_primary),
                                        colorResource(R.color.color_secscond)
                                    )
                                )
                            )
                    )
                }

            } else {
                Row(
                    modifier = Modifier
                        .wrapContentSize()
                        .padding(horizontal = 12.dp)
                        .clickable {
                            onClickItem.invoke(1)
                        },
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    Image(
                        ImageVector.vectorResource(listItemTab[1].iconRes),
                        contentDescription = "",
                        modifier = Modifier
                            .size(36.dp)
                            .padding(4.dp)
                    )
                }

            }
            if (indexSelected == 2) {
                Column(
                    modifier = Modifier
                        .wrapContentWidth()
                        .padding(horizontal = 6.dp)
                        .background(color = colorResource(R.color.color_bg_tab))
                        .clickable {
                            onClickItem.invoke(2)
                        },
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Spacer(modifier = Modifier.height(6.dp))
                    Row(
                        modifier = Modifier
                            .wrapContentHeight()
                            .onGloballyPositioned {
                                widthIndicator = it.size.width / listItemTab.size
                            },
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Image(
                            ImageVector.vectorResource(listItemTab[2].iconSelected),
                            colorFilter = ColorFilter.tint(colorResource(R.color.color_primary)),
                            contentDescription = "",
                            modifier = Modifier.size(24.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = stringResource(listItemTab[2].title),
                            fontSize = 12.sp,
                            textAlign = TextAlign.Start,
                            color = colorResource(R.color.color_primary),
                            fontWeight = FontWeight(700),
                            fontFamily = FontFamily(Font(R.font.readexpro_bold)),
                        )
                    }
                    Spacer(modifier = Modifier.height(6.dp))
                    Box(
                        modifier = Modifier
                            .width((widthIndicator + 50).dp)
                            .height(1.5.dp)
                            .background(
                                Brush.linearGradient(
                                    listOf(
                                        colorResource(R.color.color_primary),
                                        colorResource(R.color.color_primary),
                                        colorResource(R.color.color_secscond)
                                    )
                                )
                            )
                    )
                }

            } else {
                Row(
                    modifier = Modifier
                        .wrapContentSize()
                        .padding(horizontal = 12.dp)
                        .clickable {
                            onClickItem.invoke(2)
                        },
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    Image(
                        ImageVector.vectorResource(listItemTab[2].iconRes),
                        contentDescription = "",
                        modifier = Modifier
                            .size(36.dp)
                            .padding(4.dp)
                    )
                }

            }
            if (indexSelected == 3) {
                Column(
                    modifier = Modifier
                        .wrapContentWidth()
                        .padding(horizontal = 6.dp)
                        .background(color = colorResource(R.color.color_bg_tab))
                        .clickable {
                            onClickItem.invoke(3)
                        },
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Spacer(modifier = Modifier.height(6.dp))
                    Row(
                        modifier = Modifier
                            .wrapContentHeight()
                            .onGloballyPositioned {
                                widthIndicator = it.size.width / listItemTab.size
                            },
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Image(
                            ImageVector.vectorResource(listItemTab[3].iconSelected),
                            colorFilter = ColorFilter.tint(colorResource(R.color.color_primary)),
                            contentDescription = "",
                            modifier = Modifier.size(24.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = stringResource(listItemTab[3].title),
                            fontSize = 12.sp,
                            textAlign = TextAlign.Start,
                            color = colorResource(R.color.color_primary),
                            fontWeight = FontWeight(700),
                            fontFamily = FontFamily(Font(R.font.readexpro_bold)),
                        )
                    }
                    Spacer(modifier = Modifier.height(6.dp))
                    Box(
                        modifier = Modifier
                            .width((widthIndicator + 50).dp)
                            .height(1.5.dp)
                            .background(
                                Brush.linearGradient(
                                    listOf(
                                        colorResource(R.color.color_primary),
                                        colorResource(R.color.color_primary),
                                        colorResource(R.color.color_secscond)
                                    )
                                )
                            )
                    )
                }

            } else {
                Row(
                    modifier = Modifier
                        .wrapContentSize()
                        .padding(horizontal = 6.dp)
                        .clickable {
                            onClickItem.invoke(3)
                        },
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    Image(
                        ImageVector.vectorResource(listItemTab[3].iconRes),
                        contentDescription = "",
                        colorFilter = ColorFilter.tint(colorResource(R.color.color_disable)),
                        modifier = Modifier
                            .size(36.dp)
                            .padding(4.dp)
                    )
                }

            }
        }
    }

}

private data class ItemTab(
    @StringRes val title: Int,
    @DrawableRes val iconRes: Int,
    @DrawableRes val iconSelected: Int
)

@Preview(showSystemUi = false, showBackground = false)
@Composable
private fun HomeTabPreview() {

//    TabLayout(3, listItemTab = listOf( ItemTab(
//        title = R.string.popular,
//        R.drawable.ic_tab_popular,
//        R.drawable.ic_tab_popular
//    ),
//        ItemTab(
//            title = R.string.wallpaper_4d,
//            R.drawable.ic_tab_4d,
//            R.drawable.ic_tab_4d
//        ),
//        ItemTab(
//            title = R.string.live_wallpaper,
//            R.drawable.ic__tab_live,
//            R.drawable.ic__tab_live
//        ),
//        ItemTab(
//            title = R.string.wallpaper_4k,
//            R.drawable.ic_tab_4k,
//            R.drawable.ic_tab_4k
//        ),) ) {
//
//    }
}