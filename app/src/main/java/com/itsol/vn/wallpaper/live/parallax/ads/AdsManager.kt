package com.itsol.vn.wallpaper.live.parallax.ads

import android.app.Activity
import android.content.Context
import android.os.Bundle
import android.os.Handler
import android.util.Log
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import com.google.android.gms.ads.AdListener
import com.google.android.gms.ads.AdLoader
import com.google.android.gms.ads.AdSize
import com.google.android.gms.ads.AdValue
import com.google.android.gms.ads.AdView
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.VideoOptions
import com.google.android.gms.ads.interstitial.InterstitialAd
import com.google.android.gms.ads.nativead.NativeAd
import com.google.android.gms.ads.nativead.NativeAdOptions
import com.google.firebase.Firebase
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.analytics.analytics
import com.google.firebase.analytics.logEvent
import com.itsol.ironsourcelib.AdmobUtils
import com.itsol.ironsourcelib.AdmobUtils.adRequest
import com.itsol.ironsourcelib.AdmobUtils.isNetworkConnected
import com.itsol.ironsourcelib.AdmobUtils.isShowAds
import com.itsol.ironsourcelib.AdmobUtils.isTesting
import com.itsol.ironsourcelib.AdmobUtilsCompose
import com.itsol.ironsourcelib.AppOpenManager
import com.itsol.ironsourcelib.CollapsibleBanner
import com.itsol.ironsourcelib.GoogleENative
import com.itsol.ironsourcelib.utils.admod.BannerHolder
import com.itsol.ironsourcelib.utils.admod.InterHolderAdmob
import com.itsol.ironsourcelib.utils.admod.NativeHolderAdmob
import com.itsol.ironsourcelib.utils.admod.callback.AdCallBackInterLoad
import com.itsol.ironsourcelib.utils.admod.callback.AdsInterCallBack
import com.itsol.ironsourcelib.utils.admod.callback.NativeAdmobCallback
import com.itsol.vn.wallpaper.live.parallax.BuildConfig
import com.itsol.vn.wallpaper.live.parallax.utils.Router
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

object AdsManager {

    var isDebug = true
    val aoa = BuildConfig.aoa
    val onResume =BuildConfig.onResume

    val bannerHome = BannerHolder(BuildConfig.bannerHome)

    val nativeLanguage = NativeHolderAdmob(BuildConfig.nativeLanguage)
    val nativeIntro = NativeHolderAdmob(BuildConfig.nativeIntro)
    val nativeTutorial = NativeHolderAdmob(BuildConfig.nativeTutorial)
    val nativeViewWallpaper = NativeHolderAdmob(BuildConfig.nativeViewWallpaper)

    val nativeHome = NativeHolderAdmob(BuildConfig.nativeHome)
    val nativeCategory = NativeHolderAdmob(BuildConfig.nativeCategory)

    val interCategory = InterHolderAdmob(BuildConfig.interCategory)

    var isCLick = true

    fun loadNative(context: Context, nativeHolder: NativeHolderAdmob) {
        if (!AdmobUtils.isNetworkConnected(context)) {
            return
        }
        Log.e("admob", "load native new : ")
        AdmobUtils.loadAndGetNativeAds(context, nativeHolder, object : NativeAdmobCallback {
            override fun onLoadedAndGetNativeAd(p0: NativeAd?) {

            }

            override fun onNativeAdLoaded() {
                Log.e("admob", "onNativeAdLoaded: ")
            }

            override fun onAdFail(p0: String?) {

            }

            override fun onAdPaid(p0: AdValue?, p1: String?) {

            }
        })
    }
    var isReloadNativeHome = false
    fun loadNativeHome(context: Context, nativeHolder: NativeHolderAdmob) {
        if (!AdmobUtils.isNetworkConnected(context)) {
            return
        }

        nativeHolder.nativeAd=null
        Log.e("admob", "load native new333333 : ")
        isReloadNativeHome=true
        AdmobUtils.loadAndGetNativeAds(context, nativeHolder, object : NativeAdmobCallback {
            override fun onLoadedAndGetNativeAd(p0: NativeAd?) {
                isReloadNativeHome=false
                Log.e("abmob", "onLoadedAndGetNativeAd: $isReloadNativeHome", )
            }

            override fun onNativeAdLoaded() {
                Log.e("admob", "onNativeAdLoaded: ")
                isReloadNativeHome=false
            }

            override fun onAdFail(p0: String?) {
                Log.e("admob", "onAdFail: $p0", )
                isReloadNativeHome=false
            }

            override fun onAdPaid(p0: AdValue?, p1: String?) {

            }
        })
    }

    @Composable
    fun ShowNativeWithLayout(
        context: Context,
        nativeHolderAdmob: NativeHolderAdmob,
        layout: Int,
        size: GoogleENative
    ) {
        if(!AdmobUtils.isNetworkConnected(context)){
            return
        }
        AdmobUtilsCompose.ShowNativeAdsWithLayout(
            context,
            nativeHolderAdmob,
            layout,
            size,
            object : AdmobUtils.AdsNativeCallBackAdmod {
                override fun NativeFailed(massage: String) {
                    Log.e("AAAAAAA", "NativeFailed: $massage", )
                }

                override fun NativeLoaded() {

                }

                override fun onPaidNative(adValue: AdValue, adUnitAds: String) {

                }
            })
    }

    @Composable
    fun LoadAndShowNativeWithLayout(
        context: Context,
        nativeHolderAdmob: NativeHolderAdmob,
        layout: Int,
        size: GoogleENative
    ) {
        AdmobUtilsCompose.LoadAndShowNativeAdsWithLayout(
            context,
            nativeHolderAdmob,
            layout,
            size,
            object :
                AdmobUtils.AdsNativeCallBackAdmod {
                override fun NativeFailed(massage: String) {

                }

                override fun NativeLoaded() {

                }

                override fun onPaidNative(adValue: AdValue, adUnitAds: String) {

                }
            })
    }

    fun loadInterAds(context: Context, interHolder: InterHolderAdmob) {
        AdmobUtils.loadAndGetAdInterstitial(context, interHolder, object : AdCallBackInterLoad {
            override fun onAdClosed() {

            }

            override fun onEventClickAdClosed() {

            }

            override fun onAdShowed() {

            }

            override fun onAdLoaded(p0: InterstitialAd?, p1: Boolean) {

            }

            override fun onAdFail(p0: String?) {
                Log.e("AAAAAAAAAAAAAAAA", "onAdFail: ")
            }
        })

    }

    fun showInterAds(context: Context, interHolder: InterHolderAdmob, onCloseAndFail: () -> Unit) {
        if (!AdmobUtils.isNetworkConnected(context)) {
            isCLick = true
            onCloseAndFail.invoke()
            return
        }
        isCLick = false
        AppOpenManager.getInstance().isAppResumeEnabled = true
        AdmobUtils.showAdInterstitialWithCallbackNotLoadNew(
            context as Activity,
            interHolder,
            800,
            object : AdsInterCallBack {
                override fun onStartAction() {

                }

                override fun onEventClickAdClosed() {
                    isCLick = true
                    onCloseAndFail.invoke()
                    loadInterAds(context, interHolder)
                }

                override fun onAdShowed() {
                    Handler().postDelayed({
                        try {
                            AdmobUtils.dismissAdDialog()

                        } catch (_: Exception) {

                        }

                    }, 800)
                    AppOpenManager.getInstance().isAppResumeEnabled = false
                }

                override fun onAdLoaded() {

                }

                override fun onAdFail(p0: String?) {
                    isCLick = true
                    onCloseAndFail.invoke()
                    loadInterAds(context, interHolder)
                    Log.e("AAAAAAAAAAAA", "onAdFail: $p0")
                }

                override fun onPaid(p0: AdValue?, p1: String?) {

                }
            },
            true
        )

    }

    @Composable
    fun ShowBannerCollapsible(context: Context, bannerHolder: BannerHolder) {
        if (!AdmobUtils.isNetworkConnected(context)) {
            return
        }
        AdmobUtilsCompose.ShowBannerCollapsibleNotReload(
            context,
            bannerHolder,
            CollapsibleBanner.BOTTOM,
            object : AdmobUtils.BannerCollapsibleAdCallback {
                override fun onAdFail(message: String) {
                    Log.e("AAAAAAAAAAAAAA", "onAdFail: $message")
                }

                override fun onAdPaid(adValue: AdValue, mAdView: AdView) {

                }

                override fun onBannerAdLoaded(adSize: AdSize) {

                }

                override fun onClickAds() {

                }
            })
    }
    var isLoadBanner = false
    @Composable
    fun ShowBanner(context: Context, bannerHolder: BannerHolder, onLoaded: () -> Unit) {
        if (!AdmobUtils.isNetworkConnected(context)) {
            return
        }
        isLoadBanner=true
        AdmobUtilsCompose.ShowBanner(context, bannerHolder.ads, object : AdmobUtils.BannerCallBack {
            override fun onClickAds() {

            }

            override fun onFailed(message: String) {
                isLoadBanner=false
                onLoaded.invoke()
            }

            override fun onLoad() {
                isLoadBanner=false
                onLoaded.invoke()
            }

            override fun onPaid(adValue: AdValue?, mAdView: AdView?) {

            }
        })

    }

    fun logEvent(context: Context, eventName: String) {
        val firebaseAnalytics = FirebaseAnalytics.getInstance(context)
        val bundle = Bundle()
        bundle.putString("onCreateScreeen", context.javaClass.simpleName)
        firebaseAnalytics.logEvent(eventName + "_ver_" + BuildConfig.VERSION_CODE, bundle)
        Log.e("===Event", eventName + "_" + BuildConfig.VERSION_CODE)
    }
    @Composable
    fun ScreenNameLogEffect(
        screenName: String,
        event: Lifecycle.Event = Lifecycle.Event.ON_RESUME
    ) {
        val owner = androidx.lifecycle.compose.LocalLifecycleOwner.current
        DisposableEffect(key1 = "ScreenNameLogEffect") {
            val observer = LifecycleEventObserver { _, e ->
                if (event == e) {
                    firebaseLogScreenView(screenName)
                }
            }
            owner.lifecycle.addObserver(observer)
            onDispose {
                owner.lifecycle.removeObserver(observer)
            }
        }
    }

    fun firebaseLogScreenView(name: String) {
        if (Router.lastScreenLog == name) {
            return
        }
        Router.lastScreenLog = name
        CoroutineScope(Dispatchers.IO).launch {
            Firebase.analytics.logEvent(FirebaseAnalytics.Event.SCREEN_VIEW) {
                param(FirebaseAnalytics.Param.SCREEN_NAME, "${name}_ver_${BuildConfig.VERSION_NAME}")
            }
        }
        if (BuildConfig.DEBUG) {
            Log.e("Log screen :",  "${name}_ver_${BuildConfig.VERSION_NAME}")
            return
        }
    }


}