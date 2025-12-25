package com.itsol.vn.wallpaper.live.parallax.ui.activity.progress

import android.annotation.SuppressLint
import android.app.WallpaperManager
import android.content.ComponentName
import android.content.Intent
import android.util.Log
import androidx.activity.compose.BackHandler
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.navigation.compose.currentBackStackEntryAsState
import coil3.compose.AsyncImage
import com.itsol.ironsourcelib.AppOpenManager
import com.itsol.vn.wallpaper.live.parallax.LocalNavController
import com.itsol.vn.wallpaper.live.parallax.MainActivity
import com.itsol.vn.wallpaper.live.parallax.R
import com.itsol.vn.wallpaper.live.parallax.ads.AdsManager
import com.itsol.vn.wallpaper.live.parallax.service.ImageWallpaperService
import com.itsol.vn.wallpaper.live.parallax.service.ParallaxWallpaperService
import com.itsol.vn.wallpaper.live.parallax.ui.activity.components.ViewPhoneWallpaper
import com.itsol.vn.wallpaper.live.parallax.utils.Common
import com.itsol.vn.wallpaper.live.parallax.utils.Router
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.io.IOException


@SuppressLint("RestrictedApi")
@Composable
fun ProgressSetWallpaper(modifier: Modifier = Modifier, isSuccess: Boolean) {
    val context = LocalContext.current
    val navigationController = LocalNavController.current
    val scope = rememberCoroutineScope()
    var loadWallpaperSuccess by rememberSaveable { mutableStateOf(isSuccess) }
    val url4K by rememberSaveable { mutableStateOf(Common.wallPaperModel?.pathImage) }
    var isBackStack by remember { mutableStateOf(true) }
    val mActivityResult = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult()
    ) { activityResult ->
        if (activityResult.resultCode == -1) {
            loadWallpaperSuccess = true
        } else {
            if (isBackStack) {
                isBackStack = false
                if (navigationController.currentBackStack.value.isNotEmpty()) {
                    navigationController.popBackStack()
                }
            }
        }
    }
    val backStackEntry = navigationController.currentBackStackEntryAsState()
    DisposableEffect(backStackEntry.value) {
        // Đăng ký lắng nghe sự kiện lifecycle của màn hình
        val lifecycle = backStackEntry.value?.lifecycle
        val observer = LifecycleEventObserver { _, event ->
            when (event) {
                Lifecycle.Event.ON_RESUME -> {
                    scope.launch {
                        delay(3000)
                        AppOpenManager.getInstance()
                            .enableAppResumeWithActivity(MainActivity::class.java)
                    }
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
    LaunchedEffect(true) {
        AdsManager.logEvent(context, Router.PROGRESS_WALLPAPER)
        if (!isSuccess && !loadWallpaperSuccess) {
            Common.setWallpaperImage(
                url4K.toString(),
                context,
                Common.flagSetWallPaper,
                onLoadingWallpaper = {},
                onSuccessfulWallpaper = {
                    Log.e("AAAAAAAAAAAA", "ProgressSetWallpaper: ")
                    Intent(WallpaperManager.ACTION_CHANGE_LIVE_WALLPAPER).apply {
                        putExtra(
                            WallpaperManager.EXTRA_LIVE_WALLPAPER_COMPONENT,
                            ComponentName(context, ImageWallpaperService::class.java)
                        )
                    }.also {
                        mActivityResult.launch(it)
                    }
                    AppOpenManager.getInstance()
                        .disableAppResumeWithActivity(MainActivity::class.java)
                    try {
                        WallpaperManager.getInstance(context).clear()
                    } catch (e: IOException) {
                        e.printStackTrace()
                    } finally {
                    }
                })
        }

    }
    AdsManager.ScreenNameLogEffect(Router.PROGRESS_WALLPAPER)
    BackHandler {
        navigationController.navigate(route = Router.HOME) {
            popUpTo(0) { inclusive = true }
            launchSingleTop = true
            restoreState = true
        }
    }
    Box(
        modifier = modifier
            .fillMaxSize()
            .background(colorResource(R.color.color_bg))
    ) {
        if (loadWallpaperSuccess) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .align(Alignment.Center),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Spacer(modifier = Modifier.height(20.dp))
                Text(
                    textAlign = TextAlign.Center,
                    text = context.getString(R.string.successfully),
                    fontFamily = FontFamily(
                        Font(R.font.readexpro_bold)
                    ),
                    fontWeight = FontWeight(700),
                    color = colorResource(R.color.color_bold_900),
                    fontSize = 24.sp
                )
                Spacer(modifier = Modifier.height(30.dp))
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .wrapContentWidth()
                ) {
//                    AsyncImage(
//                        model = if (url4K != "") url4K else Common.wallPaperModel?.url,
//                        contentDescription = "",
//                        modifier = Modifier
//                            .fillMaxSize()
//                            .clip(RoundedCornerShape(32.dp))
//                            .padding(end = 5.dp),
//                        error = painterResource(R.drawable.img_wallpaper_demo),
//                        contentScale = ContentScale.FillBounds,
//                    )
//                    Image(
//                        painterResource(R.drawable.img_phone),
//                        contentDescription = "",
//                        Modifier.fillMaxSize(),
//                        contentScale = ContentScale.FillBounds
//                    )
                    AndroidView(factory = { context ->
                        ViewPhoneWallpaper(context).apply {
                            val uri = if (url4K != "") url4K else Common.wallPaperModel?.url
                            setImageWallpaper(uri.toString()) {

                            }
                        }
                    })
                }
                Spacer(modifier = Modifier.height(30.dp))
                Row(verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center,
                    modifier = modifier
                        .size(220.dp, 48.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(
                            brush = Brush.linearGradient(
                                colors = listOf(
                                    colorResource(R.color.color_gradient_secscond),
                                    colorResource(R.color.color_gradient_primary)
                                )
                            )
                        )
                        .clickable {
                            navigationController.navigate(route = Router.HOME) {
                                popUpTo(0) { inclusive = true }
                                launchSingleTop = true
                                restoreState = true
                            }
                        }

                ) {
                    Image(
                        ImageVector.vectorResource(R.drawable.ic_home),
                        contentDescription = "",
                        colorFilter = ColorFilter.tint(
                            colorResource(R.color.white)
                        )
                    )
                    Spacer(modifier=Modifier.width(4.dp))
                    Text(
                        modifier = Modifier.padding(8.dp),
                        textAlign = TextAlign.Center,
                        text = context.getString(R.string.back_to_home),
                        color = colorResource(R.color.white),
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 18.sp,
                        fontFamily = FontFamily(
                            Font(R.font.readexpro_semibold)
                        )
                    )
                }
                Spacer(modifier = Modifier.height(30.dp))
            }
        } else {
            Column(
                modifier = Modifier
                    .wrapContentWidth()
                    .wrapContentHeight()
                    .align(Alignment.Center)
            ) {
                CircularProgressIndicator(
                    modifier = Modifier
                        .width(64.dp)
                        .align(Alignment.CenterHorizontally),
                    color = colorResource(R.color.color_primary),
                    trackColor = colorResource(R.color.color_unselected),
                )
                Spacer(modifier = Modifier.height(40.dp))
                Text(
                    text = context.getString(R.string.loading),
                    fontSize = 14.sp,
                    fontFamily = FontFamily(
                        Font(R.font.readexpro_medium)
                    ),
                    fontWeight = FontWeight(500),
                    color = colorResource(R.color.color_bold_900)
                )

            }
        }

    }
}

@Preview(showBackground = true)
@Composable
private fun PreviewProgressSetWallpaper() {
    ProgressSetWallpaper(isSuccess = false)
}