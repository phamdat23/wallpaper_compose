package com.itsol.vn.wallpaper.live.parallax.ui.activity.splash

import android.app.Activity
import android.content.Context
import android.os.Handler
import android.os.Looper
import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
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
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.android.billingclient.api.BillingResult
import com.itsol.ironsourcelib.AdmobUtils
import com.itsol.vn.wallpaper.live.parallax.LocalNavController
import com.itsol.vn.wallpaper.live.parallax.R
import com.itsol.vn.wallpaper.live.parallax.ads.AdsManager
import com.itsol.vn.wallpaper.live.parallax.ui.activity.components.LinearGradientProgressBar
import com.itsol.vn.wallpaper.live.parallax.ui.theme.WallpaperTheme
import com.itsol.vn.wallpaper.live.parallax.utils.Common
import com.itsol.vn.wallpaper.live.parallax.utils.Constants
import com.itsol.vn.wallpaper.live.parallax.utils.Router
import com.itsol.vn.wallpaper.live.parallax.viewModel.MainViewModel
import com.google.android.gms.ads.AdValue
import com.google.android.gms.ads.MobileAds
import com.google.android.gms.ads.RequestConfiguration
import com.itsol.ironsourcelib.AOAManager
import com.itsol.ironsourcelib.AppOpenManager
import com.itsol.vn.wallpaper.live.parallax.LocalApplication
import com.itsol.vn.wallpaper.live.parallax.MainActivity
import com.itsol.vn.wallpaper.live.parallax.ads.RemoteConfig
import com.itsol.vn.wallpaper.live.parallax.utils.Purchase

import com.orhanobut.hawk.Hawk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.json.JSONObject
import java.util.Locale
import kotlin.random.Random

@Composable
fun Splash(modifier: Modifier = Modifier) {
    val context = LocalContext.current
    val navigation = LocalNavController.current
    val scope = rememberCoroutineScope()
    var currentProgress by rememberSaveable { mutableStateOf(0f) }
    var isFirstOpen by remember { mutableStateOf(false) }
    var isConnectInternet by remember { mutableStateOf(false) }
    val application = LocalApplication.current
    var loadRemote by remember { mutableStateOf(false) }
    var aoaManager by remember { mutableStateOf<AOAManager?>(null) }
    var isLoadSuccessAOA by remember { mutableStateOf(false) }

    LaunchedEffect(true) {
        AdsManager.logEvent(context, Router.SPLASH)
        isConnectInternet = AdmobUtils.isNetworkConnected(context)
        isFirstOpen = Common.getOpenFirstApp(context)
        if (isConnectInternet) {
            Purchase.initIap(context, object : Purchase.InitIapListenner {
                override fun initPurcahseSussces(
                    billingResult: BillingResult,
                    purchases: List<com.android.billingclient.api.Purchase>
                ) {
                    scope.launch(Dispatchers.Main) {
                        for (item in purchases.indices) {
                            val json = purchases[item].toString().substringAfter(":").trim()
                            val jsonObject = JSONObject(json)
                            val productId = jsonObject.getString("productId")
                            Log.e("AAAAAAA", "onBillingSetupFinished:productId: $productId ", )
                            Common.setCurrentKeyIap(context, productId, "success")
                        }
                        Log.e("AAAAAAAA", "initPurcahseSussces:iap: ${Purchase.isBought} ")
                        RemoteConfig.initRemoteConfig(object : RemoteConfig.CompleteListener {
                            override fun onComplete() {
                                RemoteConfig.ads_test = RemoteConfig.getValues("ads_test")
                                AdsManager.isDebug =
                                    if (RemoteConfig.ads_test == "1") true else false
                                RemoteConfig.aoa_splash = RemoteConfig.getValues("aoa_splash")
                                RemoteConfig.onresume = RemoteConfig.getValues("onresume")
                                RemoteConfig.native_ob = RemoteConfig.getValues("native_ob")
                                RemoteConfig.banner_home = RemoteConfig.getValues("banner_home")
                                RemoteConfig.native_home = RemoteConfig.getValues("native_home")
                                RemoteConfig.inter_category =
                                    RemoteConfig.getValues("inter_category")
                                RemoteConfig.native_category =
                                    RemoteConfig.getValues("native_category")
                                RemoteConfig.native_view_wallpaper =
                                    RemoteConfig.getValues("native_view_wallpaper")
                                RemoteConfig.native_tutorial =
                                    RemoteConfig.getValues("native_tutorial")
                                RemoteConfig.native_language =
                                    RemoteConfig.getValues("native_language")
                                AdmobUtils.initAdmob(context, 10000, AdsManager.isDebug, true)
                                val testDeviceIds = listOf("") // Device ID của bạn
                                val configuration = RequestConfiguration.Builder()
                                    .setTestDeviceIds(testDeviceIds)
                                    .build()
                                MobileAds.setRequestConfiguration(configuration)
                                if (!loadRemote) {
                                    loadRemote = true
                                    if (RemoteConfig.onresume == "1") {
                                        AppOpenManager.getInstance()
                                            .init(application, AdsManager.onResume)
                                        AppOpenManager.getInstance()
                                            .disableAppResumeWithActivity(MainActivity::class.java)
                                    }
                                    if (RemoteConfig.aoa_splash == "1") {
                                        aoaManager = AOAManager(
                                            context as Activity,
                                            AdsManager.aoa,
                                            5000,
                                            object : AOAManager.AppOpenAdsListener {
                                                override fun onAdPaid(
                                                    adValue: AdValue,
                                                    adUnitAds: String
                                                ) {

                                                }

                                                override fun onAdsClose() {

                                                }

                                                override fun onAdsFailed(message: String) {
                                                    Log.e(
                                                        "AAAAAAAAAAAAAAAAAA",
                                                        "onAdsFailed: $message"
                                                    )
                                                    nextScreen(context, navigation, isFirstOpen)


                                                }

                                                override fun onAdsLoaded() {
                                                    isLoadSuccessAOA = true

                                                }

                                                override fun onShowed() {
                                                    Log.e("AAAAAAAAAA", "onShowed: ")
                                                    Handler().postDelayed({
                                                        nextScreen(context, navigation, isFirstOpen)
                                                    }, 1000)
                                                }
                                            })
                                        aoaManager?.loadAoA()

                                    } else {
                                        nextScreen(context, navigation, isFirstOpen)
                                    }

                                    if (RemoteConfig.inter_category == "1") {
                                        AdsManager.loadInterAds(context, AdsManager.interCategory)
                                    }
                                    if (isFirstOpen) {
                                        if (RemoteConfig.native_ob == "1") {
                                            AdsManager.loadNative(context, AdsManager.nativeIntro)
                                        }
                                        if (RemoteConfig.native_language == "1") {
                                            AdsManager.loadNative(
                                                context,
                                                AdsManager.nativeLanguage
                                            )
                                        }
                                    }
                                    if (RemoteConfig.native_home == "1") {
                                        AdsManager.loadNativeHome(context, AdsManager.nativeHome)
                                    }
                                    if (RemoteConfig.native_category == "1") {
                                        AdsManager.loadNativeHome(
                                            context,
                                            AdsManager.nativeCategory
                                        )
                                    }

                                }
                            }
                        })
                    }
                }
            })

        } else {
            Handler().postDelayed({
                nextScreen(context, navigation, isFirstOpen)
            }, 3000)

        }
        loadProgress {
            currentProgress = it
        }
    }
    AdsManager.ScreenNameLogEffect(Router.SPLASH)
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
        modifier = modifier
            .fillMaxSize()
            .background(colorResource(R.color.white))
    ) {

        Box(
            modifier = Modifier
                .wrapContentWidth()
                .wrapContentHeight()
        ) {
            Image(
                painterResource(R.drawable.prallax_splash),
                contentDescription = "",
                modifier = Modifier.align(Alignment.Center)
            )
            Image(
                painterResource(R.drawable.logo_splash),
                contentDescription = "",
                modifier = Modifier.align(
                    Alignment.BottomCenter
                )
            )
        }

        val gradient = Brush.linearGradient(
            colors = listOf(
                colorResource(R.color.color_gradient_primary),
                colorResource(R.color.color_gradient_secscond),
                colorResource(R.color.color_gradient_secscond),
                colorResource(R.color.color_gradient_secscond)
            ),
        )
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .wrapContentHeight()
                .padding(bottom = 20.dp, start = 100.dp, end = 100.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(20.dp))
            Box(
                modifier = Modifier
                    .wrapContentWidth()
                    .wrapContentHeight()
                    .clip(RoundedCornerShape(4.dp))
                    .background(colorResource(R.color.color_track_progress_splash))
                    .padding(horizontal = 2.dp, vertical = 2.dp)


            ) {
                LinearGradientProgressBar(
                    progress = { currentProgress },
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(shape = RoundedCornerShape(4.dp)),
                    trackColor = colorResource(R.color.color_track_progress_splash),
                    color = gradient,
                    strokeCap = StrokeCap.Round,
                )
            }

        }
        Spacer(modifier = Modifier.height(160.dp))
    }
}

fun nextScreen(context: Context, navController: NavController, isFirst: Boolean) {

    if (isFirst) {
        navController.navigate(Router.LANGUAGE)
        {
            popUpTo(0) { inclusive = true }
            launchSingleTop = true

        }
    } else {
        navController.navigate(Router.HOME_GROUP)
        {
            popUpTo(0) { inclusive = true }
            launchSingleTop = true
        }

    }
    val languagePosition = Common.getPositionLanguage(context)
    val languageKey = Common.getListLanguage()[languagePosition].key
    val locale = Locale(languageKey)
    Locale.setDefault(locale)
    val resources = (context as Activity).resources
    val configuration = (context as Activity).resources.configuration
    configuration.setLocale(locale)
    resources.updateConfiguration(configuration, resources.displayMetrics)
    Log.e("TAG", "language: $languageKey")
    AppOpenManager.getInstance().enableAppResumeWithActivity(MainActivity::class.java)
}


suspend fun loadProgress(updateProgress: (Float) -> Unit) {
    for (i in 1..100) {
        updateProgress(i.toFloat() / 100)
        delay(50)
    }
}


@Preview(showBackground = true)
@Composable
private fun PreviewSplash() {
    WallpaperTheme {
        Splash()
    }
}