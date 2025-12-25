package com.itsol.vn.wallpaper.live.parallax

import android.app.Activity
import android.app.Application
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.navigation
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.itsol.ironsourcelib.AdmobUtils
import com.itsol.vn.wallpaper.live.parallax.ads.AdsManager
import com.itsol.vn.wallpaper.live.parallax.model.HistorySearchModel
import com.itsol.vn.wallpaper.live.parallax.ui.activity.SetAsWallpaper
import com.itsol.vn.wallpaper.live.parallax.ui.activity.category_detail.CategoryDetail
import com.itsol.vn.wallpaper.live.parallax.ui.activity.home.Home
import com.itsol.vn.wallpaper.live.parallax.ui.activity.iap.IAPScreen
import com.itsol.vn.wallpaper.live.parallax.ui.activity.iap.IapScreen2
import com.itsol.vn.wallpaper.live.parallax.ui.activity.intro.Intro
import com.itsol.vn.wallpaper.live.parallax.ui.activity.language.Language
import com.itsol.vn.wallpaper.live.parallax.ui.activity.onboarding.OnBoardingScreen
import com.itsol.vn.wallpaper.live.parallax.ui.activity.progress.ProgressSetWallpaper
import com.itsol.vn.wallpaper.live.parallax.ui.activity.search.Search
import com.itsol.vn.wallpaper.live.parallax.ui.activity.settings.Settings
import com.itsol.vn.wallpaper.live.parallax.ui.activity.splash.Splash
import com.itsol.vn.wallpaper.live.parallax.ui.theme.WallpaperTheme
import com.itsol.vn.wallpaper.live.parallax.utils.Common
import com.itsol.vn.wallpaper.live.parallax.utils.Constants
import com.itsol.vn.wallpaper.live.parallax.utils.Router
import com.itsol.vn.wallpaper.live.parallax.viewModel.MainViewModel
import com.orhanobut.hawk.Hawk
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import java.util.Locale
import kotlin.random.Random

val LocalNavController =
    staticCompositionLocalOf<NavController> { error("No NavController found!") }
val LocalApplication =
    staticCompositionLocalOf<Application> { error("No Application found!") }

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    private val viewModel by viewModels<MainViewModel>()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        window.decorView.systemUiVisibility =
            (View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                    or View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                    or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                    or View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                    or View.SYSTEM_UI_FLAG_FULLSCREEN
                    or View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                    )
        if (Common.getOpenFirstApp(this)) {
            if (AdmobUtils.isNetworkConnected(this)) {
                if (Common.getOpenCallApi(this)) {
                    Common.setOpenCallApi(this, false)
                    viewModel.getAllWallPaper()
                    viewModel.getAllCategory()
                }
            }
            lifecycleScope.launch {
                viewModel.listWallpaper.collect {
                    if (it.isNotEmpty()) {
                        Common.wallPaperRandom = it[Random.nextInt(it.size)]
                    }
                }
            }

        } else {
            if (AdmobUtils.isNetworkConnected(this)) {
                viewModel.getVersionApi()
            }
            lifecycleScope.launch {
                viewModel.versionApi.collect {
                    if (it.isNotEmpty()) {
                        Hawk.put(Constants.VERSION_API, it[0])
                        viewModel.getAndUpdateWallpaper()
                        viewModel.getAndUpdateAllCategory()

                    }
                }

            }
            lifecycleScope.launch {
                viewModel.listWallpaper.collect {
                    if (it.isNotEmpty()) {
                        Common.wallPaperRandom = it[Random.nextInt(it.size)]
                    }
                }
            }
        }
//        lifecycleScope.launch {
//            viewModel.category.collect {
//                it.map {
//                    Common.listChoseCategory.add(HistorySearchModel(query = Common.capitalizeFirstLetter( it.categoryName)))
//                }
//            }
//        }
        setContent {
            WallpaperTheme {
                Scaffold(modifier = Modifier.background(color = Color.Gray).fillMaxSize()) { innerPadding ->
                    val navigationController = rememberNavController()
                    CompositionLocalProvider(
                        LocalNavController provides navigationController,
                        LocalApplication provides application
                    ) {

                        NavHost(
                            navController = navigationController,
                            startDestination = Router.SPLASH_GROUP
                        ) {
                            navigation(
                                startDestination = Router.SPLASH,
                                route = Router.SPLASH_GROUP
                            ) {
                                composable(route = Router.SPLASH) {
                                    Splash(modifier = Modifier.padding(bottom = 0.dp))
                                }
                                composable(route = Router.LANGUAGE) {
                                    Language()
                                }
                                composable(route = Router.ONBOARDING) {
                                    OnBoardingScreen()
                                }
                            }
                            navigation(route = Router.HOME_GROUP, startDestination = Router.HOME) {
                                composable(route = Router.HOME) {
                                    Home(
                                        navigationController,
                                        modifier = Modifier.padding(bottom = 0.dp)
                                    )
                                }
                                composable(route = Router.SEARCH) {
                                    Search(
                                        modifier = Modifier.padding(bottom = 0.dp)
                                    )
                                }
                                composable(route = Router.CATEGORY_DETAIL + "/{title}") {
                                    val title = it.arguments?.getString("title")
                                    CategoryDetail(
                                        title = title.toString(),
                                        modifier = Modifier.padding(bottom = 0.dp)
                                    )
                                }

                                composable(route = "${Router.SET_WALLPAPER}?wallpaperModel={wallpaperModel}",
                                    arguments = listOf(
                                        navArgument("wallpaperModel") {
                                            type = NavType.StringType
                                        }
                                    )) {
                                    val wallPaper = Common.wallpaperFromJson(
                                        it.arguments?.getString("wallpaperModel").toString()
                                    )
                                    SetAsWallpaper(
                                        modifier = Modifier.padding(
                                            bottom = 0.dp,
                                            top = innerPadding.calculateTopPadding() - innerPadding.calculateTopPadding()
                                        ), wallpaperModel = wallPaper
                                    )
                                }

                                composable(route = "${Router.PROGRESS_WALLPAPER}?isSuccess={isSuccess}",
                                    arguments = listOf(
                                        navArgument("isSuccess") {
                                            type = NavType.BoolType
                                        }
                                    )) {
                                    val isSuccess = it.arguments?.getBoolean("isSuccess", false)
                                    if (isSuccess != null) {
                                        ProgressSetWallpaper(
                                            modifier = Modifier.padding(bottom = 0.dp),
                                            isSuccess = isSuccess
                                        )
                                    } else {
                                        ProgressSetWallpaper(
                                            modifier = Modifier.padding(bottom = 0.dp),
                                            isSuccess = false
                                        )

                                    }
                                }


                            }
                            navigation(
                                route = Router.SETTING_GROUP,
                                startDestination = Router.SETTING
                            ) {
                                composable(route = Router.SETTING) {
                                    Settings(
                                        modifier = Modifier.padding(bottom = 0.dp)
                                    )
                                }
                                composable(route = "${Router.INTRO}?isSetting={isSetting}",
                                    arguments = listOf(
                                        navArgument("isSetting") {
                                            type = NavType.BoolType
                                        }
                                    )) {
                                    val isSetting = it.arguments?.getBoolean("isSetting", false)
                                    if (isSetting != null) {
                                        Intro(
                                            navigationController,
                                            modifier = Modifier.padding(bottom = 0.dp),
                                            isSetting = isSetting
                                        )
                                    } else {
                                        Intro(
                                            navigationController,
                                            modifier = Modifier.padding(bottom = 0.dp),
                                            isSetting = false
                                        )
                                    }
                                }
                            }
                            composable(route= Router.IAP_SCREEN, arguments = listOf(
                                navArgument("key"){
                                    type = NavType.StringType
                                },
                                navArgument("nameCategory"){
                                    type = NavType.StringType
                                }
                            )){
                                val key = it.arguments?.getString("key").toString()
                                val nameKey=it.arguments?.getString("nameCategory").toString()
                                IAPScreen(key = key, nameCategory = nameKey)
                            }
                            composable(route = Router.IAP_SCREEN_2){
                                IapScreen2()
                            }
                        }
                    }

                }

            }


        }

    }

    override fun onRestart() {
        super.onRestart()

        AdmobUtils.initAdmob(this, 10000, AdsManager.isDebug, true)
    }

    override fun onWindowFocusChanged(hasFocus: Boolean) {
        super.onWindowFocusChanged(hasFocus)
        window.decorView.systemUiVisibility =
            (View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                    or View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                    or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                    or View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                    or View.SYSTEM_UI_FLAG_FULLSCREEN
                    or View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                    )
//        if(AdmobUtils.isNetworkConnected(this)&&Common.getOpenCallApi(this)){
//            viewModel.getVersionApi()
//            Common.setOpenCallApi(this@MainActivity, false)
//        }
        AdmobUtils.initAdmob(this, 10000, AdsManager.isDebug, true)
    }
}