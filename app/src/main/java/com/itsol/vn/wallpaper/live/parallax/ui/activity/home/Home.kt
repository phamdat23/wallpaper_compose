package com.itsol.vn.wallpaper.live.parallax.ui.activity.home

import android.app.Activity
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.net.ConnectivityManager
import android.net.Uri
import android.os.Build
import android.provider.Settings
import android.text.format.DateFormat
import android.util.Log
import androidx.activity.compose.BackHandler
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.IntentSenderRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
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
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.input.pointer.pointerInput
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.navigation.NavController
import androidx.navigation.compose.currentBackStackEntryAsState
import com.itsol.vn.wallpaper.live.parallax.MainActivity
import com.itsol.vn.wallpaper.live.parallax.R
import com.itsol.vn.wallpaper.live.parallax.ads.AdsManager
import com.itsol.vn.wallpaper.live.parallax.ui.activity.home.tab.category.CategoryTab
import com.itsol.vn.wallpaper.live.parallax.ui.activity.home.tab.homeTab.HomeTab
import com.itsol.vn.wallpaper.live.parallax.ui.activity.home.tab.myCollection.MyCollection
import com.itsol.vn.wallpaper.live.parallax.ui.dialog.DialogDailyReward
import com.itsol.vn.wallpaper.live.parallax.ui.theme.WallpaperTheme
import com.itsol.vn.wallpaper.live.parallax.utils.Common
import com.itsol.vn.wallpaper.live.parallax.utils.Router
import com.itsol.ironsourcelib.AdmobUtils
import com.itsol.ironsourcelib.AppOpenManager
import com.itsol.vn.wallpaper.live.parallax.ads.RemoteConfig
import com.itsol.vn.wallpaper.live.parallax.model.WallpaperModel
import com.itsol.vn.wallpaper.live.parallax.ui.activity.components.TextGradient
import com.itsol.vn.wallpaper.live.parallax.ui.dialog.DialogIap
import com.itsol.vn.wallpaper.live.parallax.ui.dialog.DialogInternet
import com.itsol.vn.wallpaper.live.parallax.ui.dialog.DialogUpdateApp
import com.itsol.vn.wallpaper.live.parallax.utils.Constants
import com.itsol.vn.wallpaper.live.parallax.utils.Purchase
import com.itsol.vn.wallpaper.live.parallax.utils.UpdateAppManager
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Composable
fun Home(navigationController: NavController, modifier: Modifier = Modifier) {
    val scopeCoroutine = rememberCoroutineScope()
    val context = LocalContext.current
    val statePager = rememberPagerState(pageCount = { 3 })
    var currentPage by rememberSaveable {
        mutableStateOf(statePager.currentPage)
    }
    var isConnectInternet by rememberSaveable { mutableStateOf(true) }
    var showDialog by rememberSaveable {
        mutableStateOf(
            false
        )
    }
    var wallpaperRandom by remember {
        mutableStateOf<WallpaperModel?>(Common.wallPaperRandom)
    }
    var remoteBanner by remember { mutableStateOf("") }
    var remoteNative by remember { mutableStateOf("") }
    var isShowDialogIap by remember { mutableStateOf(false) }
    val checkKey by rememberSaveable { mutableStateOf(
        Common.getCurrentKeyIap(context, Constants.listKeyIap[0]) != "" &&
                Common.getCurrentKeyIap(context, Constants.listKeyIap[1]) != "" &&
                Common.getCurrentKeyIap(context, Constants.listKeyIap[2]) != "" &&
                Common.getCurrentKeyIap(context, Constants.listKeyIap[3]) != "" &&
                Common.getCurrentKeyIap(context, Constants.listKeyIap[4]) != "" &&
                Common.getCurrentKeyIap(context, Constants.listKeyIap[5]) != "") }
    var isBought by remember { mutableStateOf(Purchase.isBought) }
    var isShowDialogUpdate by remember { mutableStateOf(false) }
    val updateLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartIntentSenderForResult()
    ) { result ->
        if (result.resultCode == android.app.Activity.RESULT_OK) {

        } else {

        }
    }
    LaunchedEffect(true) {
        Log.e("AAAAAAAAAAA", "Home: ${Common.listCatName.toString()}")
        remoteNative = RemoteConfig.native_home
        remoteBanner = RemoteConfig.banner_home
        AppOpenManager.getInstance().enableAppResumeWithActivity(MainActivity::class.java)
        if (RemoteConfig.native_view_wallpaper == "1") {
            AdsManager.loadNative(context, AdsManager.nativeViewWallpaper)
        }
        delay(10)
        showDialog = if (DateFormat.format(
                "yyyy/MM/dd",
                System.currentTimeMillis()
            ).toString() != Common.getDateString(context)
        ) true else false
    }
    BackHandler {
        (context as Activity).finish()
        AppOpenManager.getInstance().disableAppResumeWithActivity(MainActivity::class.java)
    }
    val internetReceiver = remember {
        object : BroadcastReceiver() {
            override fun onReceive(p0: Context?, p1: Intent?) {
                if (p1?.action == ConnectivityManager.CONNECTIVITY_ACTION) {
                    // check internet ở đây
                    isConnectInternet = AdmobUtils.isNetworkConnected(context)
                }
            }
        }
    }
    AdsManager.ScreenNameLogEffect(Router.HOME)
    DisposableEffect(context) {
        val intentFilter = IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            context.registerReceiver(internetReceiver, intentFilter, Context.RECEIVER_EXPORTED)
        } else {
            context.registerReceiver(internetReceiver, intentFilter)
        }

        onDispose {
            context.unregisterReceiver(internetReceiver)
        }
    }
    var isOnLoaded by remember { mutableStateOf(false) }
    val backStackEntry = navigationController.currentBackStackEntryAsState()
    DisposableEffect(backStackEntry.value) {
        // Đăng ký lắng nghe sự kiện lifecycle của màn hình
        val lifecycle = backStackEntry.value?.lifecycle
        val observer = LifecycleEventObserver { _, event ->
            when (event) {
                Lifecycle.Event.ON_RESUME -> {
                    AppOpenManager.getInstance()
                        .enableAppResumeWithActivity(MainActivity::class.java)
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
    LaunchedEffect(statePager.currentPage) {
        snapshotFlow { statePager.currentPage }.collect {
            currentPage = it
            if (!AdsManager.isLoadBanner) {
                remoteBanner = "null"
                delay(30)
                remoteBanner = RemoteConfig.banner_home
            }


        }
    }
    LaunchedEffect(true) {
        UpdateAppManager.initAppManager(context, launcher = updateLauncher, onRequest = { request ->
            scopeCoroutine.launch(Dispatchers.Main) {
                Log.e("AAAAAAA", "Home:UpdateAppManager: $request ")
                isShowDialogUpdate = request
                if (!request) {
                    showDialog = if (DateFormat.format(
                            "yyyy/MM/dd",
                            System.currentTimeMillis()
                        ).toString() != Common.getDateString(context)
                    ) true else false
                }
            }

        })
    }
    Column(
        modifier = modifier
            .fillMaxSize()
            .background(color = colorResource(R.color.color_bg))
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 16.dp, end = 16.dp, bottom = 12.dp, top = 12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            when (currentPage) {
                0 -> {
                    Row(
                        modifier = Modifier
                            .wrapContentHeight()
                            .wrapContentWidth(), verticalAlignment = Alignment.CenterVertically
                    ) {
                        Image(
                            painterResource(R.drawable.img_icon_title_home),
                            contentDescription = "",
                            contentScale = ContentScale.Inside,
                            modifier = Modifier
                                .wrapContentWidth()
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = stringResource(R.string.app_name),
                            textAlign = TextAlign.Center,
                            fontSize = 32.sp,
                            fontFamily = FontFamily(Font(R.font.readexpro_medium)),
                            fontWeight = FontWeight(500),
                            color = colorResource(R.color.color_primary)
                        )

                    }
                }

                1 -> {
                    Text(
                        text = stringResource(R.string.category),
                        fontSize = 20.sp,
                        fontFamily = FontFamily(Font(R.font.readexpro_bold)),
                        fontWeight = FontWeight(700),
                        color = colorResource(R.color.color_primary)
                    )
                }

                2 -> {
                    Text(
                        text = stringResource(R.string.my_collection),
                        fontSize = 20.sp,
                        fontFamily = FontFamily(Font(R.font.readexpro_bold)),
                        fontWeight = FontWeight(700),
                        color = colorResource(R.color.color_primary)
                    )
                }
            }
            Spacer(modifier = Modifier.weight(1f))
            if(!checkKey){
                if (!isBought) {
                    Image(
                        painterResource(R.drawable.ic_sub),
                        contentDescription = "",
                        modifier = Modifier
                            .size(48.dp)
                            .padding(12.dp)
                            .clickable {
                                if (AdsManager.isCLick) {
                                    navigationController.navigate(Router.IAP_SCREEN_2)
                                }
                            }
                    )
                }else{

                }
//                Image(
//                    painterResource(R.drawable.ic_sub),
//                    contentDescription = "",
//                    modifier = Modifier
//                        .size(48.dp)
//                        .padding(12.dp)
//                        .clickable {
//                            if (AdsManager.isCLick) {
//                                navigationController.navigate(Router.IAP_SCREEN_2)
//                            }
//                        }
//                )
            }else{

            }



            Image(
                ImageVector.vectorResource(R.drawable.ic_search),
                contentDescription = "",
                colorFilter = ColorFilter.tint(colorResource(R.color.color_bold_900)),
                modifier = Modifier
                    .size(48.dp)
                    .padding(12.dp)
                    .clickable {
                        if (AdsManager.isCLick) {
                            navigationController.navigate(Router.SEARCH)

                        }

                    }
            )
            Image(
                ImageVector.vectorResource(R.drawable.ic_settings),
                contentDescription = "",
                colorFilter = ColorFilter.tint(colorResource(R.color.color_bold_900)),
                modifier = Modifier
                    .size(48.dp)
                    .padding(12.dp)
                    .clickable {
                        if (AdsManager.isCLick) {
                            navigationController.navigate(Router.SETTING)
                        }

                    })
        }
        HorizontalPager(
            state = statePager,
            modifier = Modifier.weight(1f),
            userScrollEnabled = false, beyondViewportPageCount = 3
        ) {
            when (it) {
                0 -> {
                    HomeTab(isSubscribe = isBought) {
                        scopeCoroutine.launch {
                            if (!AdsManager.isLoadBanner) {
                                remoteBanner = "null"
                                delay(30)
                                remoteBanner = RemoteConfig.banner_home
                            }
                        }
                    }
                }

                1 -> {
                    CategoryTab()
                }

                2 -> {
                    MyCollection() {
                        scopeCoroutine.launch {
                            if (!AdsManager.isLoadBanner) {
                                remoteBanner = "null"
                                delay(30)
                                remoteBanner = RemoteConfig.banner_home
                            }

                        }
                    }

                }
            }
        }
        BottomBar(currentPage) { index ->
            if (AdsManager.isCLick) {
                scopeCoroutine.launch {
                    statePager.scrollToPage(index)
                }
            }
        }
        if (remoteBanner == "1") {
            Spacer(
                modifier = Modifier
                    .height(1.dp)
                    .background(colorResource(R.color.black))
            )
            AdsManager.ShowBanner(context, AdsManager.bannerHome) {

            }
        }
        if (!isConnectInternet) {
            DialogInternet(onDismissRequest = {}, onConfirmation = {
                val intent = Intent(Settings.ACTION_WIFI_SETTINGS)
                context.startActivity(intent)
                AppOpenManager.getInstance().disableAppResumeWithActivity(MainActivity::class.java)
            })
        }
//        if (showDialog && isConnectInternet) {
//            wallpaperRandom?.let {
//                DialogDailyReward(
//                    onDismiss = {
//                        showDialog = false
//                        Common.setDateString(context)
//                    },
//                    onSetWallpaper = {
//                        if(Common.checkCate(it.categories)){
//                            if (Common.getBoughtIap(context)) {
//                                val json = Common.wallpaperToJson(it)
//                                navigationController.navigate("${Router.SET_WALLPAPER}?wallpaperModel=${json}")
//                            } else {
//                                var key ="0.5"
//                                for (i in 0 until Common.listCatName.size) {
//                                    if (it.categories == "AI_Wallpaper") {
//                                        if (Common.listCatName[i].equals(
//                                                it.categories.replace(
//                                                    "_",
//                                                    " "
//                                                ), true
//                                            )
//                                        ) {
//                                            key = Constants.listKeyIap[i]
//                                        }
//                                    } else {
//                                        if (Common.listCatName[i].equals(
//                                                it.categories,
//                                                true
//                                            )
//                                        ) {
//                                            key = Constants.listKeyIap[i]
//                                        }
//                                    }
//                                }
//                                navigationController.navigate(
//                                    Router.getIapScreen(
//                                        key,
//                                        if (it.categories == "AI_Wallpaper") it.categories.replace(
//                                            "_",
//                                            " "
//                                        ) else it.categories
//                                    )
//                                )
//
//                            }
//                        }else{
//                            val json = Common.wallpaperToJson(it)
//                            navigationController.navigate("${Router.SET_WALLPAPER}?wallpaperModel=${json}")
//                        }
//                        showDialog = false
//                        Common.setDateString(context)
//
//                    },
//                    wallpaperModel = it
//                )
//            }
//        }
        if (isShowDialogUpdate) {
            DialogUpdateApp(onDismissRequest = {}, onConfirmation = {
                AppOpenManager.getInstance().disableAppResumeWithActivity(MainActivity::class.java)
                val packageName = context.packageName
                try {
                    // Mở trực tiếp trên ứng dụng CH Play
                    val intent =
                        Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=$packageName"))
                    intent.setPackage("com.android.vending")
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                    context.startActivity(intent)
                } catch (e: Exception) {
                    // Nếu CH Play không có sẵn, mở trên trình duyệt
                    val intent = Intent(
                        Intent.ACTION_VIEW,
                        Uri.parse("https://play.google.com/store/apps/details?id=$packageName")
                    )
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                    context.startActivity(intent)
                }
            })
        }
    }
}

@Composable
private fun BottomBar(currentPage: Int, onClickItem: (Int) -> Unit) {
    val list by remember {
        mutableStateOf(
            arrayListOf(
                Pair<Int, Int>(R.drawable.ic_home_selected, R.drawable.ic_home) to R.string.home,
                Pair<Int, Int>(
                    R.drawable.ic_category_selected,
                    R.drawable.ic_category
                ) to R.string.category,
                Pair<Int, Int>(
                    R.drawable.ic_my_colletion_selected,
                    R.drawable.ic_my_colletion
                ) to R.string.my_collection
            )
        )
    }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .wrapContentHeight()
            .clip(shape = RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp))
            .background(color = colorResource(R.color.white))
            .padding(vertical = 12.dp)
    ) {
        list.forEachIndexed { index, item ->
            Column(
                modifier = Modifier
                    .weight(1f)
                    .wrapContentHeight()
                    .clickable {
                        onClickItem.invoke(index)
                    },
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally

            ) {
                if (index == currentPage) {
                    Image(
                        ImageVector.vectorResource(item.first.first),
                        contentDescription = "",
                        colorFilter = ColorFilter.tint(
                            colorResource(R.color.color_primary)
                        ),
                        modifier = Modifier.size(24.dp)
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = stringResource(item.second),
                        textAlign = TextAlign.Center,
                        fontSize = 12.sp,
                        fontWeight = FontWeight(500),
                        color = colorResource(R.color.color_primary),
                        fontFamily = FontFamily(Font(R.font.readexpro_medium)),
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                } else {
                    if (index == 2) {
                        Image(
                            painterResource(item.first.second),
                            contentDescription = "",
                            colorFilter = ColorFilter.tint(
                                colorResource(R.color.color_bg_bottom_bar)
                            ),
                            modifier = Modifier.size(24.dp)
                        )
                    } else {
                        Image(

                            ImageVector.vectorResource(item.first.second),
                            contentDescription = "",
                            colorFilter = ColorFilter.tint(
                                colorResource(R.color.color_bg_bottom_bar)
                            ),
                            modifier = Modifier.size(24.dp)
                        )
                    }

                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = stringResource(item.second),
                        textAlign = TextAlign.Center,
                        fontSize = 12.sp,
                        fontWeight = FontWeight(500),
                        color = colorResource(R.color.color_bg_bottom_bar),
                        fontFamily = FontFamily(Font(R.font.readexpro_medium)),
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                }
            }
        }


    }


}

@Preview(showBackground = true, showSystemUi = true)
@Composable
private fun PreviewHome() {
    WallpaperTheme {
//        Home()

    }
}