package com.itsol.vn.wallpaper.live.parallax.ui.activity.intro

import android.annotation.SuppressLint
import androidx.annotation.DrawableRes
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
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
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
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.itsol.vn.wallpaper.live.parallax.R
import com.itsol.vn.wallpaper.live.parallax.ads.AdsManager
import com.itsol.vn.wallpaper.live.parallax.ui.theme.WallpaperTheme
import com.itsol.vn.wallpaper.live.parallax.utils.Router
import com.itsol.ironsourcelib.GoogleENative
import com.itsol.vn.wallpaper.live.parallax.ads.RemoteConfig
import com.itsol.vn.wallpaper.live.parallax.ui.activity.components.GradientDotsIndicator


@SuppressLint("RestrictedApi")
@Composable
fun Intro(navigationController: NavController, modifier: Modifier = Modifier, isSetting: Boolean) {
    val context = LocalContext.current
    val scopeCoroutine = rememberCoroutineScope()
    val listIntroModel by remember {
        mutableStateOf(arrayListOf<IntroModel>().apply {
            add(
                IntroModel(
                    R.drawable.bg_tutorial1,
                    R.drawable.img_tutorial1,
                    context.getString(R.string.title_intro_1),
                    context.getString(R.string.content_intro_1)
                )
            )
            add(
                IntroModel(
                    R.drawable.bg_tutorial2,
                    R.drawable.img_tutorial2,
                    context.getString(R.string.title_intro_2),
                    context.getString(R.string.content_intro_2)
                )
            )
            add(
                IntroModel(
                    R.drawable.bg_tutorial3,
                    R.drawable.img_tuorial_3_new,
                    context.getString(R.string.title_intro_3),
                    context.getString(R.string.content_intro_3)
                )
            )
            add(
                IntroModel(
                    R.drawable.bg_tutorial4,
                    R.drawable.img_tutorial4,
                    context.getString(R.string.title_intro_4),
                    context.getString(R.string.content_intro_4)
                )
            )
            add(
                IntroModel(
                    R.drawable.bg_white,
                    R.drawable.img_tutorial5,
                    context.getString(R.string.title_intro_5),
                    context.getString(R.string.content_intro_5)
                )
            )
        })
    }
    var remoteNativeTutorial by remember { mutableStateOf("") }
    LaunchedEffect(true) {
        AdsManager.logEvent(context, Router.INTRO)
        remoteNativeTutorial = RemoteConfig.native_tutorial

    }
    AdsManager.ScreenNameLogEffect(Router.INTRO)

    val statePager =
        rememberPagerState(pageCount = { listIntroModel.size })
    var currentpage by rememberSaveable {
        mutableStateOf(statePager.currentPage)
    }

    LaunchedEffect(statePager.currentPage) {
        snapshotFlow { statePager.currentPage }.collect {
            currentpage = it
        }
    }
    var isBackStack by remember { mutableStateOf(true) }
    Box(modifier = modifier.fillMaxSize()) {
        Image(
            painterResource(listIntroModel[currentpage].backgroundRes),
            contentDescription = "",
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.FillWidth
        )
        Column(
            modifier = Modifier
                .fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 16.dp, horizontal = 16.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.End
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.clickable {
                            if(isBackStack){
                                isBackStack=false
                                if(navigationController.currentBackStack.value.isNotEmpty()){
                                    navigationController.popBackStack()
                                }
                            }
                        }) {
                        Text(
                            text = stringResource(R.string.skip),
                            textAlign = TextAlign.Center,
                            color = colorResource(R.color.color_bg_bottom_bar),
                            fontSize = 16.sp,
                            fontFamily = FontFamily(Font(R.font.readexpro_bold)),
                            fontWeight = FontWeight(700),
                        )
                        Image(
                            painter = painterResource(R.drawable.icon_next_tutorial),
                            contentDescription = "",
                        )
                    }
                }
            }
            HorizontalPager(
                state = statePager,
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
            ) { index ->
                ItemIntro(listIntroModel[index], index)
            }
            GradientDotsIndicator(
                modifier = Modifier.padding(vertical = 8.dp),
                dotCount = listIntroModel.size,
                pagerState = statePager,
                dotSpacing = 6.dp,
                // modifier = Modifier.size(width = 70.dp, height = 20.dp)
            )
            if (remoteNativeTutorial == "1") {
                AdsManager.ShowNativeWithLayout(
                    context,
                    AdsManager.nativeTutorial,
                    R.layout.ad_template_ob,
                    GoogleENative.UNIFIED_MEDIUM
                )

            }

        }

    }

}



@Composable
private fun ItemIntro(introModel: IntroModel, index: Int) {
    Box {

        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = introModel.title,
                textAlign = TextAlign.Center,
                color = colorResource(R.color.color_primary),
                fontSize = 20.sp,
                fontWeight = FontWeight(700),
                fontFamily = FontFamily(Font(R.font.readexpro_bold)),
                modifier = Modifier.padding(horizontal = 26.dp)

            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = introModel.content,
                textAlign = TextAlign.Center,
                color = colorResource(R.color.color_bg_bottom_bar),
                fontSize = if(index==4) 14.sp else 16.sp,
                fontWeight = FontWeight(400),
                fontFamily = FontFamily(Font(R.font.readexpro_regular)),
                modifier = Modifier.padding(horizontal = 26.dp)

            )
            Spacer(modifier = Modifier.height(8.dp))
            Image(
                painterResource(introModel.imageRes),
                contentDescription = "",
                Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .padding(start = if(index==2) 36.dp else 26.dp , end = 26.dp)
            )
        }
    }

}

@Preview(showBackground = true)
@Composable
private fun PreviewIntro() {
    WallpaperTheme {

    }
}

@Immutable
private data class IntroModel(
    @DrawableRes val backgroundRes: Int,
    @DrawableRes val imageRes: Int,
    val title: String,
    val content: String
)