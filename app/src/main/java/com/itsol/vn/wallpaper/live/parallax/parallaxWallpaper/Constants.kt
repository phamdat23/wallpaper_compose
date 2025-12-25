package com.itsol.vn.wallpaper.live.parallax.parallaxWallpaper


object Constants {
    var BASE_URL: String = "http://vapp-expert.com/"



    // System

    const val PACKAGE_NAME: String =
        "livewallpaper.transparent.transparentscreen.edgelighting.digitalclock"
    const val SERVICE_NAME: String = PACKAGE_NAME + ".MyWallpaperService"

    // Parallax
    const val SENSITIVITY_MIN: Double = 0.1
    const val SENSITIVITY_MAX: Double = 0.5
    const val DEPTH_MIN: Double = 0.001
    const val DEPTH_MAX: Double = 0.01
    const val FALLBACK_MIN: Double = 0.0
    const val FALLBACK_MAX: Double = 0.05
    const val ZOOM_MIN: Double = 0.6
    const val ZOOM_MAX: Double = 1.0
    const val SCROLL_AMOUNT_MIN: Double = 0.3
    const val SCROLL_AMOUNT_MAX: Double = 0.05
    const val DIM_MAX: Double = 200.0

    // Sensor
    const val VERTICAL_FIX: Double = 0.01



    // File system
    const val FS_DIR_ZERO: String = "Zero"
    const val FS_DIR_CACHE: String = "cache"

    // Local data
    const val LD_CATALOG: String = "list"
    const val LD_TIMESTAMP: String = "timestamp"

    // Background
    const val BG_FORMAT: String = ".png"

    // Preferences
    // NOTE: These are internal preferences not available to the user in the settings
    const val PREF_BACKGROUND: String = "background"
    const val PREF_BACKGROUND_DEFAULT: String = "fallback"
    const val PREF_CHECKSENS: String = "checksens"
    const val PREF_CHECKSENS_DEFAULT: Boolean = true
    const val PREF_FIRSTPREV: String = "firstprev"
    const val PREF_FIRSTPREV_DEFAULT: Boolean = true

    object ACTION {
        const val MAIN_ACTION: String = "test.action.main"
        const val START_ACTION: String = "test.action.start"
        const val STOP_ACTION: String = "test.action.stop"
    }
}
