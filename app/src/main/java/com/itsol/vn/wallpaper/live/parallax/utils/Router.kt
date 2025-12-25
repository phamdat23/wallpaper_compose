package com.itsol.vn.wallpaper.live.parallax.utils

object Router {
    const val SPLASH = "splash"
    const val LANGUAGE = "language"
    const val ONBOARDING = "onboarding"
    const val INTRO = "intro"
    const val HOME = "home"
    const val SEARCH = "search"
    const val SETTING = "settings"
    const val CATEGORY_DETAIL = "category_detail"
    const val CATEGORY = "category"
    const val SET_WALLPAPER = "set_wallpaper"
    const val PROGRESS_WALLPAPER="progress_wallpaper"

    const val SPLASH_GROUP="SPLASH_GROUP"
    const val HOME_GROUP="HOME_GROUP"
    const val SETTING_GROUP="SETTING_GROUP"
    const val IAP_SCREEN="iap_screen/{key}/{nameCategory}"

    const val IAP_SCREEN_2="iap_screen2"

    fun getIapScreen(key:String, nameCategory: String):String{
        return "iap_screen/$key/$nameCategory"
    }

    var lastScreenLog=""
}