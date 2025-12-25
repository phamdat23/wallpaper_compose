package com.itsol.vn.wallpaper.live.parallax.ads


import com.google.firebase.remoteconfig.ConfigUpdate
import com.google.firebase.remoteconfig.ConfigUpdateListener
import com.google.firebase.remoteconfig.FirebaseRemoteConfig
import com.google.firebase.remoteconfig.FirebaseRemoteConfigException
import com.google.firebase.remoteconfig.FirebaseRemoteConfigSettings
import com.itsol.vn.wallpaper.live.parallax.R


object RemoteConfig {
    var aoa_splash = "1"
    var banner_home = "1"
    var inter_category = "1"
    var native_category = "1"
    var native_home = "1"
    var native_language = "1"
    var native_ob = "1"
    var native_tutorial = "1"
    var native_view_wallpaper = "1"
    var onresume = "1"
    var ads_test="1"


    fun initRemoteConfig(listener: CompleteListener) {
        val mFirebaseRemoteConfig: FirebaseRemoteConfig = FirebaseRemoteConfig.getInstance()
        val configSettings: FirebaseRemoteConfigSettings = FirebaseRemoteConfigSettings.Builder()
            .setMinimumFetchIntervalInSeconds(0)
            .build()
        mFirebaseRemoteConfig.setConfigSettingsAsync(configSettings)
        mFirebaseRemoteConfig.setDefaultsAsync(R.xml.remote_config_defaults)
        mFirebaseRemoteConfig.addOnConfigUpdateListener(object : ConfigUpdateListener {
            override fun onUpdate(configUpdate: ConfigUpdate) {
                listener.onComplete()
            }

            override fun onError(error: FirebaseRemoteConfigException) {
                listener.onComplete()
            }
        })
        mFirebaseRemoteConfig.fetchAndActivate().addOnCompleteListener {
            listener.onComplete()
        }
    }

    fun getValues(key: String): String {
        val mFirebaseRemoteConfig: FirebaseRemoteConfig = FirebaseRemoteConfig.getInstance()
        return mFirebaseRemoteConfig.getString(key)
    }

    interface CompleteListener {
        fun onComplete()
    }

}