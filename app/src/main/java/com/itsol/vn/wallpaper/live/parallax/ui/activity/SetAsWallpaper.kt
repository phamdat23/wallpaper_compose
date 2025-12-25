package com.itsol.vn.wallpaper.live.parallax.ui.activity

import DownloadImageAndSaveStorage
import android.annotation.SuppressLint
import android.content.Context
import android.util.Log
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
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
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.TileMode
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.compose.ui.window.Dialog
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.navigation.compose.currentBackStackEntryAsState
import coil3.compose.AsyncImage
import com.itsol.vn.wallpaper.live.parallax.LocalNavController
import com.itsol.vn.wallpaper.live.parallax.R
import com.itsol.vn.wallpaper.live.parallax.ads.AdsManager
import com.itsol.vn.wallpaper.live.parallax.model.ImageTypes
import com.itsol.vn.wallpaper.live.parallax.model.WallpaperModel
import com.itsol.vn.wallpaper.live.parallax.ui.activity.components.ButtonSetAsWallpaper
import com.itsol.vn.wallpaper.live.parallax.ui.activity.components.StateLoadingImage
import com.itsol.vn.wallpaper.live.parallax.ui.dialog.DialogSetWallpaper
import com.itsol.vn.wallpaper.live.parallax.utils.Common
import com.itsol.vn.wallpaper.live.parallax.utils.Constants
import com.itsol.vn.wallpaper.live.parallax.utils.Router
import com.itsol.vn.wallpaper.live.parallax.viewModel.MainViewModel
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.itsol.ironsourcelib.AdmobUtils
import com.itsol.ironsourcelib.AppOpenManager
import com.itsol.ironsourcelib.GoogleENative
import com.itsol.vn.wallpaper.live.parallax.MainActivity
import com.itsol.vn.wallpaper.live.parallax.ads.RemoteConfig
import com.itsol.vn.wallpaper.live.parallax.ui.activity.components.ViewPhoneWallpaper
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.logging.Handler


@SuppressLint("RestrictedApi")
@OptIn(ExperimentalLayoutApi::class)
@Composable
fun SetAsWallpaper(modifier: Modifier = Modifier, wallpaperModel: WallpaperModel) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val navigationController = LocalNavController.current
    val heightScreen = LocalConfiguration.current.screenHeightDp
    val viewModel = hiltViewModel<MainViewModel>()
    var stateLoadingImage by remember {
        mutableStateOf(StateLoadingImage.ON_LOADING)
    }
    var wallpaperModelState by rememberSaveable { mutableStateOf(wallpaperModel) }
    var isLoading by rememberSaveable { mutableStateOf(false) }
    var selectedButton by rememberSaveable { mutableStateOf(Common.isTypeSetWallpaper) }
    val gradientBrush = Brush.linearGradient(
        colors = listOf(
            colorResource(R.color.color_gradient_primary),
            colorResource(R.color.color_gradient_secscond),
            colorResource(R.color.color_gradient_secscond)
        ),
        tileMode = TileMode.Clamp


    )
    val gradientBrushDefault = Brush.linearGradient(
        colors = listOf(
            Color.Transparent,
            Color.Transparent
        )
    )
    var url4K by remember { mutableStateOf("") }
    var listTypeImage by remember { mutableStateOf<List<ImageTypes>?>(null) }
    var isShowDialogSetWallpaper by remember { mutableStateOf(false) }
    var remoteNative by remember { mutableStateOf("") }

    var isConnectInternet by remember { mutableStateOf(false) }
    var isSetWallpaper by remember { mutableStateOf(false) }

    val mActivityResult = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult()
    ) { activityResult ->
        if (activityResult.resultCode == -1) {
            navigationController.navigate(route = "${Router.PROGRESS_WALLPAPER}?isSuccess=${true}") {
                launchSingleTop = true
                restoreState = true
            }
        }
    }

    LaunchedEffect(true) {
        Log.e("WALLPAPER", "SetAsWallpaper:key: ${wallpaperModel.wallpaperId} ")
        AdsManager.logEvent(context, Router.SET_WALLPAPER)
        isConnectInternet = AdmobUtils.isNetworkConnected(context)
        remoteNative = RemoteConfig.native_view_wallpaper
        try {
            if (wallpaperModelState.wallpaperId == "wallpapers/thumb/christmas_19") {
                listTypeImage = wallpaperModelState?.let { Common.parserListImage(it.urls) }
                listTypeImage = listTypeImage?.asReversed()
            } else {
                listTypeImage = wallpaperModelState?.let { Common.parserListImage(it.urls) }
            }


            listTypeImage?.map {
                if (it.type == Constants.IMAGE4K) {
                    url4K = it.url
                    return@map
                }
            }
        } catch (e: Exception) {

        }

    }
    AdsManager.ScreenNameLogEffect(Router.SET_WALLPAPER)
    val backStackEntry = navigationController.currentBackStackEntryAsState()
    DisposableEffect(backStackEntry.value) {
        // Đăng ký lắng nghe sự kiện lifecycle của màn hình
        val lifecycle = backStackEntry.value?.lifecycle
        val observer = LifecycleEventObserver { _, event ->
            when (event) {
                Lifecycle.Event.ON_RESUME -> {
                    if (isSetWallpaper) {
                        android.os.Handler().postDelayed({
                            AppOpenManager.getInstance()
                                .enableAppResumeWithActivity(MainActivity::class.java)
                        }, 1000)
                        isSetWallpaper = false
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
    var isBackStack by remember { mutableStateOf(true) }
    Box(
        modifier = modifier
            .fillMaxSize()
            .background(colorResource(R.color.color_bg))
            .padding(top = 10.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .align(Alignment.TopCenter),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Row(
                modifier = Modifier
                    .wrapContentWidth()
                    .weight(1f),
//                    .height(if (remoteNative == "1" && isConnectInternet) (heightScreen - (heightScreen / 4)).dp else heightScreen.dp)
            ) {
                Image(
                    modifier = Modifier
//                        .align(Alignment.TopStart)
                        .size(36.dp)
                        .padding(top = 5.dp, start = 10.dp)
                        .clickable {
                            if (isBackStack) {
                                isBackStack = false
                                if (navigationController.currentBackStack.value.isNotEmpty()) {
                                    navigationController.popBackStack()
                                }
                            }

                        },
                    imageVector = ImageVector.vectorResource(R.drawable.ic_arrow_back),
                    colorFilter = ColorFilter.tint(colorResource(R.color.color_bold_900)),
                    contentDescription = ""
                )
                Spacer(modifier = Modifier.width(8.dp))
                Column(
                    modifier = Modifier
                        .fillMaxHeight()
                        .weight(1f)
                        .verticalScroll(rememberScrollState()),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Spacer(modifier = Modifier.height(20.dp))
                    Box(modifier = Modifier.weight(1f)) {
                        AndroidView(factory = { context ->
                            ViewPhoneWallpaper(context).apply {
                                wallpaperModelState?.let {
                                    setImageWallpaper(it.url) {
                                        stateLoadingImage = it
                                    }
                                }
                            }
                        })
                        Column(
                            modifier = Modifier
                                .padding(bottom = 30.dp)
                                .align(Alignment.BottomCenter),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Row(
                                modifier = Modifier.padding(bottom = 16.dp)
                            ) {
                                // Button 4D

                                listTypeImage?.map {
                                    if (it.type == Constants.PARALLAX) {
                                        Spacer(modifier = Modifier.padding(start = 6.dp))
                                        Box(
                                            modifier = Modifier
                                                .clip(RoundedCornerShape(26.dp))
                                                .background(colorResource(R.color.color_black_50))
                                                .border(
                                                    BorderStroke(
                                                        2.dp,
                                                        if (selectedButton == Constants.PARALLAX) gradientBrush else gradientBrushDefault
                                                    ), shape = RoundedCornerShape(100)
                                                )
                                                .padding(2.dp) // Padding để tạo khoảng cách giữa viền và nền của button
                                                .size(44.dp)
                                                .clickable {
                                                    selectedButton = Constants.PARALLAX
                                                }
                                        ) {
                                            if (selectedButton == Constants.PARALLAX) {
                                                Image(
                                                    modifier = Modifier.align(Alignment.Center),
                                                    imageVector = ImageVector.vectorResource(R.drawable.ic_tab_4d),
                                                    colorFilter = ColorFilter.tint(colorResource(R.color.color_primary)),
                                                    contentDescription = ""
                                                )
                                            } else {
                                                Image(
                                                    modifier = Modifier.align(Alignment.Center),
                                                    imageVector = ImageVector.vectorResource(R.drawable.ic_tab_4d),
                                                    colorFilter = ColorFilter.tint(colorResource(R.color.white)),
                                                    contentDescription = ""
                                                )
                                            }
                                        }
                                        Spacer(modifier = Modifier.padding(start = 6.dp))
                                    } else if (it.type == Constants.IMAGE4K) {
                                        // Button 4K
                                        Spacer(modifier = Modifier.padding(start = 6.dp))
                                        Box(
                                            modifier = Modifier
                                                .clip(RoundedCornerShape(26.dp))
                                                .background(colorResource(R.color.color_black_50))
                                                .border(
                                                    BorderStroke(
                                                        2.dp,
                                                        if (selectedButton == Constants.IMAGE4K) gradientBrush else gradientBrushDefault
                                                    ), shape = RoundedCornerShape(100)
                                                )
                                                .padding(2.dp)
                                                .size(44.dp)
                                                .clickable {
                                                    selectedButton = Constants.IMAGE4K
                                                }
                                        ) {
                                            if (selectedButton == Constants.IMAGE4K) {
                                                Image(
                                                    modifier = Modifier.align(Alignment.Center),
                                                    imageVector = ImageVector.vectorResource(R.drawable.ic_tab_4k),
                                                    colorFilter = ColorFilter.tint(colorResource(R.color.color_primary)),
                                                    contentDescription = ""
                                                )
                                            } else {
                                                Image(
                                                    modifier = Modifier.align(Alignment.Center),
                                                    imageVector = ImageVector.vectorResource(R.drawable.ic_tab_4k),
                                                    colorFilter = ColorFilter.tint(colorResource(R.color.white)),
                                                    contentDescription = ""
                                                )
                                            }
                                        }
                                        Spacer(modifier = Modifier.padding(start = 6.dp))

                                    } else if (it.type == Constants.LIVE) {
                                        // button live
                                        Spacer(modifier = Modifier.padding(start = 6.dp))
                                        Box(
                                            modifier = Modifier
                                                .clip(RoundedCornerShape(26.dp))
                                                .background(colorResource(R.color.color_black_50))
                                                .border(
                                                    BorderStroke(
                                                        2.dp,
                                                        if (selectedButton == Constants.LIVE) gradientBrush else gradientBrushDefault
                                                    ), shape = RoundedCornerShape(100)
                                                )
                                                .padding(2.dp)
                                                .size(44.dp)
                                                .clickable {
                                                    selectedButton = Constants.LIVE
                                                }
                                        ) {
                                            if (selectedButton == Constants.LIVE) {
                                                Image(
                                                    modifier = Modifier.align(Alignment.Center),
                                                    imageVector = ImageVector.vectorResource(R.drawable.ic__tab_live),
                                                    colorFilter = ColorFilter.tint(colorResource(R.color.color_primary)),
                                                    contentDescription = ""
                                                )
                                            } else {
                                                Image(
                                                    modifier = Modifier.align(Alignment.Center),
                                                    imageVector = ImageVector.vectorResource(R.drawable.ic__tab_live),
                                                    colorFilter = ColorFilter.tint(colorResource(R.color.white)),
                                                    contentDescription = ""
                                                )
                                            }
                                        }
                                        Spacer(modifier = Modifier.padding(start = 6.dp))
                                    }
                                }
                            }
                        }
                    }
                    Spacer(modifier = Modifier.height(20.dp))
                    ButtonSetAsWallpaper(
                        modifier = Modifier
                            .height(48.dp)
                            .width(213.dp),
                        onClickSetWallpaper = {
                            if (wallpaperModelState?.download == false) {
                                if (isConnectInternet) {
                                    isLoading = true
                                    var pathVideo = ""
                                    var pathFolder = ""
                                    var pathImage = ""
                                    scope.launch(Dispatchers.IO) {
                                        val imageList = wallpaperModelState?.let {
                                            Common.parserListImage(
                                                it.urls
                                            )
                                        }
                                        imageList?.map {
                                            Log.e("AAAAAAAAA", "SetAsWallpaper: type: ${it.type}")
                                            if (it.type == Constants.LIVE) {
                                                pathVideo =
                                                    DownloadImageAndSaveStorage.downloadImageAndSaveToStorage(
                                                        context,
                                                        it.url
                                                    ).toString()

                                            } else if (it.type == Constants.PARALLAX) {
                                                pathFolder =
                                                    DownloadImageAndSaveStorage.downloadImageAndUnZipToStorage(
                                                        context,
                                                        it.url
                                                    ).toString()
                                            } else if (it.type == Constants.IMAGE4K) {
                                                pathImage =
                                                    DownloadImageAndSaveStorage.downloadImageAndSaveToStorage(
                                                        context,
                                                        it.url
                                                    ).toString()
                                            }
                                        }
                                        wallpaperModelState?.let {
                                            viewModel.downloadedWallpaper(
                                                it.id,
                                                pathVideo,
                                                pathFolder,
                                                pathImage,
                                                true
                                            )
                                        }
                                        withContext(Dispatchers.Main) {
                                            wallpaperModelState = wallpaperModelState.copy(
                                                download = true,
                                                pathVideo = pathVideo,
                                                pathParallax = pathFolder,
                                                pathImage = pathImage
                                            )
                                            isLoading = false
                                        }


                                    }
                                } else {
                                    Toast.makeText(context, "No Internet", Toast.LENGTH_SHORT)
                                        .show()
                                }

                            } else {
                                // code set wallpaper đây
                                Common.wallPaperModel = wallpaperModelState
                                isSetWallpaper = true
                                when (selectedButton) {
                                    Constants.PARALLAX -> {
                                        Common.setWallPaperParallax(context, mActivityResult)
                                        AppOpenManager.getInstance()
                                            .disableAppResumeWithActivity(MainActivity::class.java)
                                    }

                                    Constants.LIVE -> {
                                        Common.setWallPaperVideo(context, mActivityResult)
                                        AppOpenManager.getInstance()
                                            .disableAppResumeWithActivity(MainActivity::class.java)
                                    }

                                    Constants.IMAGE4K -> {
//                                        isShowDialogSetWallpaper = true
                                        Common.urlImage4K = url4K
                                        navigationController.navigate(route = "${Router.PROGRESS_WALLPAPER}?isSuccess=${false}") {
                                            launchSingleTop = true
                                            restoreState = true
                                        }
                                        AppOpenManager.getInstance()
                                            .disableAppResumeWithActivity(MainActivity::class.java)
                                    }
                                }
                            }


                        },
                        isDownLoad = if (wallpaperModelState?.download == false) false else true
                    )
                    Spacer(modifier = Modifier.height(20.dp))

                }
                Spacer(modifier = Modifier.width(8.dp))
                Image(
                    modifier = Modifier
                        .size(36.dp)
                        .padding(top = 5.dp, end = 10.dp)
                        .alpha(0f),
                    imageVector = ImageVector.vectorResource(R.drawable.ic_arrow_back),
                    colorFilter = ColorFilter.tint(colorResource(R.color.color_bold_900)),
                    contentDescription = ""
                )
            }
            if (remoteNative == "1") {
                AdsManager.ShowNativeWithLayout(
                    context,
                    AdsManager.nativeViewWallpaper,
                    R.layout.ad_template_medium,
                    GoogleENative.UNIFIED_MEDIUM
                )
            }


        }


        if (isLoading) {
            Dialog(onDismissRequest = { /* Prevent dismiss by clicking outside */ }) {
                Box(
                    modifier = Modifier
                        .size(80.dp)
                        .background(Color.White, shape = RoundedCornerShape(40.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(color = Color.Black)
                }
            }
        }
        if (isShowDialogSetWallpaper) {
            DialogSetWallpaper {
                isShowDialogSetWallpaper = false
                if (it) {
                    Common.urlImage4K = url4K
                    navigationController.navigate(route = "${Router.PROGRESS_WALLPAPER}?isSuccess=${false}") {
                        launchSingleTop = true
                        restoreState = true
                    }
                }

            }
        }

    }
}

fun downLoadWallpaper(
    context: Context,
    wallpaperModel: WallpaperModel,
    viewModel: MainViewModel,
    onLoading: (Boolean, image: String?, parallax: String?, video: String?) -> Unit
) {
    onLoading.invoke(true, null, null, null)
    var pathVideo = ""
    var pathFolder = ""
    var pathImage = ""
    CoroutineScope(Dispatchers.IO).launch {
        val imageList = wallpaperModel?.let {
            Common.parserListImage(
                it.urls
            )
        }
        imageList?.map {
            if (it.type == Constants.LIVE) {
                pathVideo =
                    DownloadImageAndSaveStorage.downloadImageAndSaveToStorage(
                        context,
                        it.url
                    ).toString()

            } else if (it.type == Constants.PARALLAX) {
                pathFolder =
                    DownloadImageAndSaveStorage.downloadImageAndUnZipToStorage(
                        context,
                        it.url
                    ).toString()
            } else if (it.type == Constants.IMAGE4K) {
                pathImage =
                    DownloadImageAndSaveStorage.downloadImageAndSaveToStorage(
                        context,
                        it.url
                    ).toString()
            }
        }
        wallpaperModel?.let {
            viewModel.downloadedWallpaper(
                it.id,
                pathVideo,
                pathFolder,
                pathImage,
                true
            )
        }
        onLoading.invoke(false, pathImage, pathFolder, pathVideo)

    }
}

@Preview(showBackground = true)
@Composable
fun SetAsWallpaperPreview() {
//    SetAsWallpaper()
}