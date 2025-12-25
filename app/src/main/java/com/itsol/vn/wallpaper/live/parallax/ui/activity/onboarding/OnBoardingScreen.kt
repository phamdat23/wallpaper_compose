package com.itsol.vn.wallpaper.live.parallax.ui.activity.onboarding

import android.annotation.SuppressLint
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.PagerState
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.itsol.vn.wallpaper.live.parallax.LocalNavController
import com.itsol.vn.wallpaper.live.parallax.R
import com.itsol.vn.wallpaper.live.parallax.ads.AdsManager
import com.itsol.vn.wallpaper.live.parallax.utils.Router
import com.itsol.ironsourcelib.GoogleENative
import com.itsol.vn.wallpaper.live.parallax.ads.RemoteConfig
import com.itsol.vn.wallpaper.live.parallax.ui.activity.components.GradientDotsIndicator
import com.itsol.vn.wallpaper.live.parallax.utils.Common
import kotlinx.coroutines.launch

@SuppressLint("MutableCollectionMutableState")
@Composable
fun OnBoardingScreen() {
    val context = LocalContext.current
    val navigation = LocalNavController.current
    val listOnBoardingData by remember {
        mutableStateOf(arrayListOf<OnBoardingData>().apply {
            add(
                OnBoardingData(
                    image = R.drawable.onbroading1,
                    description = context.getString(R.string.wallpaper_4d)
                )
            )
//            add(
//                OnBoardingData(
//                    image = R.drawable.onboarding2,
//                    description = context.getString(R.string.title_intro_4)
//                )
//            )
            add(
                OnBoardingData(
                    image = R.drawable.img_tutorial5,
                    description = context.getString(R.string.title_intro_5)
                )
            )
        })
    }
    var remoteNativeIntro by remember { mutableStateOf("") }
    LaunchedEffect(true) {
        AdsManager.logEvent(context,Router.ONBOARDING)
        remoteNativeIntro = RemoteConfig.native_ob
    }
    AdsManager.ScreenNameLogEffect(Router.ONBOARDING)

    val pageState = rememberPagerState { listOnBoardingData.size }
    val scope = rememberCoroutineScope()
    LaunchedEffect(pageState.currentPage) {
        snapshotFlow { pageState.currentPage }.collect{
            AdsManager.nativeIntro.nativeAd=null
            AdsManager.loadNative(context, AdsManager.nativeIntro)
            remoteNativeIntro="null"
            remoteNativeIntro=RemoteConfig.native_ob
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        Image(
            painter = painterResource(R.drawable.bg_onboarding), contentDescription = "",
            contentScale = ContentScale.Crop,
            modifier = Modifier.fillMaxSize()
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(top = 30.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            HorizontalPager(
                modifier = Modifier.weight(1f),
                state = pageState,
            ) { index ->
                SinglePageObBoarding(data = listOnBoardingData[index])
            }

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 10.dp, end = 16.dp, start = 16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                GradientDotsIndicator(
                    dotCount = listOnBoardingData.size,
                    pagerState = pageState,
                    dotSpacing = 6.dp,
                    modifier = Modifier.size(width = 70.dp, height = 20.dp)
                )

                Text(
                    modifier = Modifier
                        .padding(8.dp)
                        .clickable {
                            scope.launch {
                                if (pageState.currentPage < listOnBoardingData.size - 1) {
                                    pageState.animateScrollToPage(pageState.currentPage + 1)
                                    AdsManager.logEvent(context,"${Router.ONBOARDING}_page_${pageState.currentPage}")

                                } else {
                                    navigation.navigate(Router.HOME_GROUP) {
                                        popUpTo(0) { inclusive = true }
                                    }
                                    Common.setOpenFirstApp(context, false)
                                }
                            }
                        },
                    text = if (pageState.currentPage == listOnBoardingData.size - 1)
                        context.getString(R.string.get_start)
                    else
                        context.getString(R.string.next),
                    style = TextStyle(
                        brush = Brush.linearGradient(
                            colors = listOf(
                                colorResource(R.color.color_primary),
                                colorResource(R.color.color_secscond)
                            )
                        ),
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp,
                        fontFamily = FontFamily(Font(R.font.readexpro_bold))
                    )
                )
            }
            Spacer(modifier = Modifier.height(20.dp))
            if(remoteNativeIntro=="1"){
                AdsManager.ShowNativeWithLayout(context, AdsManager.nativeIntro, R.layout.ad_template_intro, GoogleENative.UNIFIED_MEDIUM)
            }
        }
    }
}

@Preview
@Composable
fun OnBoardingScreenPreview() {
    OnBoardingScreen()
}



