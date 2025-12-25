package com.itsol.vn.wallpaper.live.parallax

import android.app.Activity
import android.app.Application
import android.app.Application.ActivityLifecycleCallbacks
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatDelegate
import com.itsol.vn.wallpaper.live.parallax.utils.Common

import com.orhanobut.hawk.Hawk
import dagger.hilt.android.HiltAndroidApp
import java.util.Locale

@HiltAndroidApp
class MyApplication : Application(), ActivityLifecycleCallbacks {
    override fun onCreate() {
        super.onCreate()
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        Hawk.init(applicationContext).build()
        registerActivityLifecycleCallbacks(this)

    }

    override fun onTrimMemory(level: Int) {
        super.onTrimMemory(level)
        if (level == TRIM_MEMORY_UI_HIDDEN){
//            AppOpenManager.getInstance().timeToBackground = System.currentTimeMillis()
        }
    }

    override fun onActivityCreated(activity: Activity, savedInstanceState: Bundle?) {
        val languagePosition = Common.getPositionLanguage(this)
        val languageKey = Common.getListLanguage()[languagePosition].key
        val locale = Locale(languageKey)
        Locale.setDefault(locale)
        val resources = activity.resources
        val configuration = activity.resources.configuration
        configuration.setLocale(locale)
        resources.updateConfiguration(configuration, resources.displayMetrics)
        Log.e("TAG", "onActivityCreated: $languageKey")

    }

    override fun onActivityStarted(activity: Activity) {
    }

    override fun onActivityResumed(activity: Activity) {
    }

    override fun onActivityPaused(activity: Activity) {

    }

    override fun onActivityStopped(activity: Activity) {
    }

    override fun onActivitySaveInstanceState(activity: Activity, outState: Bundle) {

    }

    override fun onActivityDestroyed(activity: Activity) {
    }

}