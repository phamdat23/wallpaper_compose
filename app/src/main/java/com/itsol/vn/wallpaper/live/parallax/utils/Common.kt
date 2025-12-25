package com.itsol.vn.wallpaper.live.parallax.utils

import android.app.WallpaperManager
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.drawable.Drawable
import android.text.format.DateFormat
import android.util.DisplayMetrics
import android.util.Log
import android.view.WindowManager
import androidx.activity.result.ActivityResultLauncher
import com.bumptech.glide.Glide
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.transition.Transition
import com.itsol.vn.wallpaper.live.parallax.R
import com.itsol.vn.wallpaper.live.parallax.model.LanguageModel
import com.itsol.vn.wallpaper.live.parallax.model.WallpaperModel
import com.itsol.vn.wallpaper.live.parallax.service.GIFWallpaperService
import com.itsol.vn.wallpaper.live.parallax.service.ParallaxWallpaperService
import com.itsol.vn.wallpaper.live.parallax.service.VideoWallpaperService
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.itsol.vn.wallpaper.live.parallax.model.HistorySearchModel
import com.itsol.vn.wallpaper.live.parallax.model.ImageTypes
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.IOException
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneId


object Common {

    var typeWallpaper = 0
    var pathGifWallpaper = ""
    var flagSetWallPaper = FlagSetWallPaper.LOCK_AND_HOME_SCREEN

    var wallPaperRandom: WallpaperModel? = null
    var wallPaperModel: WallpaperModel? = null
    var urlImage4K = ""

    var isTypeSetWallpaper = Constants.IMAGE4K

    val listCatName = arrayListOf<String>()

    fun setOpenFirstApp(context: Context, open: Boolean) {
        val preferences =
            context.getSharedPreferences(context.packageName, Context.MODE_PRIVATE)
        preferences.edit().putBoolean("open_first_app", open).apply()
    }

    fun getOpenFirstApp(context: Context): Boolean {
        val preferences =
            context.getSharedPreferences(context.packageName, Context.MODE_PRIVATE)
        return preferences.getBoolean("open_first_app", true)
    }
    fun setBoughtIap(context: Context, open: Boolean) {
        val preferences =
            context.getSharedPreferences(context.packageName, Context.MODE_PRIVATE)
        preferences.edit().putBoolean("is_bought_iap", open).apply()
    }

    fun getBoughtIap(context: Context): Boolean {
        val preferences =
            context.getSharedPreferences(context.packageName, Context.MODE_PRIVATE)
        return preferences.getBoolean("is_bought_iap", false)
    }
    fun setOpenCallApi(context: Context, open: Boolean) {
        val preferences =
            context.getSharedPreferences(context.packageName, Context.MODE_PRIVATE)
        preferences.edit().putBoolean("open_call_api", open).apply()
    }

    fun getOpenCallApi(context: Context): Boolean {
        val preferences =
            context.getSharedPreferences(context.packageName, Context.MODE_PRIVATE)
        return preferences.getBoolean("open_call_api", true)
    }

    fun setPositionLanguage(context: Context, pos: Int) {
        val preferences =
            context.getSharedPreferences(context.packageName, Context.MODE_PRIVATE)
        preferences.edit().putInt("position_language", pos).apply()
    }

    fun getPositionLanguage(context: Context): Int {
        val preferences =
            context.getSharedPreferences(context.packageName, Context.MODE_PRIVATE)
        return preferences.getInt("position_language", 1)
    }

    @JvmStatic
    fun getListLanguage(): ArrayList<LanguageModel> =
        arrayListOf<LanguageModel>().apply {
            add(
                LanguageModel(
                    R.string.vietnamese,
                    R.drawable.vietnam,
                    "vi"
                )
            )
            add(
                LanguageModel(
                    R.string.english,
                    R.drawable.united_kingdom,
                    "en"
                )
            )
            add(
                LanguageModel(
                    R.string.portuguese,
                    R.drawable.portugal,
                    "pt"
                )
            )
            add(
                LanguageModel(
                    R.string.korean,
                    R.drawable.south_korea,
                    "ko"
                )
            )
            add(
                LanguageModel(
                    R.string.japanese,
                    R.drawable.japan,
                    "ja"
                )
            )
            add(
                LanguageModel(
                    R.string.chinese,
                    R.drawable.china,
                    "zh"
                )
            )
        }

    fun setDateString(context: Context) {
        val preferences =
            context.getSharedPreferences(context.packageName, Context.MODE_PRIVATE)
        val date = DateFormat.format("yyyy/MM/dd", System.currentTimeMillis()).toString()
        preferences.edit().putString("current_date", date).apply()
    }

    fun getDateString(context: Context): String {
        val preferences =
            context.getSharedPreferences(context.packageName, Context.MODE_PRIVATE)
        return preferences.getString("current_date", "0000/00/00").toString()
    }


    fun wallpaperToJson(wallpaperModel: WallpaperModel): String {
        return Gson().toJson(wallpaperModel)
    }

    fun wallpaperFromJson(json: String): WallpaperModel {
        return Gson().fromJson(json, WallpaperModel::class.java)
    }

    var bitmapImage: Bitmap? = null
    fun setWallpaperImage(
        url: String,
        context: Context,
        flag: FlagSetWallPaper,
        onLoadingWallpaper: (Boolean) -> Unit,
        onSuccessfulWallpaper: (Boolean) -> Unit
    ) {
        onLoadingWallpaper.invoke(true)
        Glide.with(context).asBitmap().load(url).into(object : CustomTarget<Bitmap>() {
            override fun onResourceReady(resource: Bitmap, transition: Transition<in Bitmap>?) {
                CoroutineScope(Dispatchers.Default).launch {
                    try {
                        val wallpaperManager = WallpaperManager.getInstance(context)
                        bitmapImage = resource
                    } catch (e: Exception) {
                        withContext(Dispatchers.Main) {
                            onSuccessfulWallpaper.invoke(false)
                        }

                    } finally {
                        withContext(Dispatchers.Main) {
                            onSuccessfulWallpaper.invoke(true)
                            onLoadingWallpaper.invoke(false)
                        }

                    }

                }
            }

            override fun onLoadCleared(placeholder: Drawable?) {
                onSuccessfulWallpaper.invoke(false)
                onLoadingWallpaper.invoke(false)
                Log.e("AAAAAAAAAAAAA", "onLoadCleared:error ")
            }
        })

    }

    private fun getScreenSize(context: Context): Pair<Int, Int> {
        val wm = context.getSystemService(Context.WINDOW_SERVICE) as WindowManager
        val displayMetrics = DisplayMetrics()
        wm.defaultDisplay.getMetrics(displayMetrics)
        return Pair(displayMetrics.widthPixels, displayMetrics.heightPixels)
    }

    private fun scaleBitmapToScreen(bitmap: Bitmap, screenWidth: Int, screenHeight: Int): Bitmap {
        val widthScale = screenWidth.toFloat() / bitmap.width
        val heightScale = screenHeight.toFloat() / bitmap.height
        var scale = minOf(widthScale, heightScale)
        Log.e("AAAAAAAAAAAAA", "scaleBitmapToScreen:$scale ")
        // Kích thước ảnh sau khi scale
        val scaledWidth = (bitmap.width * scale).toInt()
        val scaledHeight = (bitmap.height * scale).toInt()

        // Tạo Bitmap mới với kích thước màn hình
        val finalBitmap = Bitmap.createBitmap(screenWidth, screenHeight, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(finalBitmap)


        // Vẽ hình ảnh đã scale lên trung tâm của màn hình
        val offsetX = (screenWidth - scaledWidth) / 2f
        val offsetY = (screenHeight - scaledHeight) / 2f
        canvas.drawBitmap(
            Bitmap.createScaledBitmap(bitmap, scaledWidth, scaledHeight, true),
            offsetX,
            offsetY,
            Paint()
        )
        return finalBitmap

    }

    fun setWallPaperVideo(
        context: Context,
        activityResult: ActivityResultLauncher<Intent>
    ) {
        Intent(WallpaperManager.ACTION_CHANGE_LIVE_WALLPAPER).apply {
            putExtra(
                WallpaperManager.EXTRA_LIVE_WALLPAPER_COMPONENT,
                ComponentName(context, VideoWallpaperService::class.java)
            )
        }.also {
            activityResult.launch(it)
        }
        try {
            WallpaperManager.getInstance(context).clear()
        } catch (e: IOException) {
            e.printStackTrace()
        } finally {
        }
    }

    fun setWallPaperParallax(
        context: Context,
        activityResult: ActivityResultLauncher<Intent>
    ) {
        Intent(WallpaperManager.ACTION_CHANGE_LIVE_WALLPAPER).apply {
            putExtra(
                WallpaperManager.EXTRA_LIVE_WALLPAPER_COMPONENT,
                ComponentName(context, ParallaxWallpaperService::class.java)
            )
        }.also {
            activityResult.launch(it)
        }
        try {
            WallpaperManager.getInstance(context).clear()
        } catch (e: IOException) {
            e.printStackTrace()
        } finally {
        }
    }

//    fun setWallPaperFluid(
//        context: Context,
//        path: String,
//        activityResult: ActivityResultLauncher<Intent>
//    ) {
//        SettingsStorage.loadConfigFromFile(
//            path,
//            Config.LWPCurrent
//        )
//        SettingsStorage.saveSessionConfig(
//            context, Config.LWPCurrent, SettingsStorage.SETTINGS_LWP_NAME
//        )
//        Config.LWPCurrent.ReloadRequired = true
//        Config.LWPCurrent.ReloadRequiredPreview = true
//        Intent(WallpaperManager.ACTION_CHANGE_LIVE_WALLPAPER).apply {
//            putExtra(
//                WallpaperManager.EXTRA_LIVE_WALLPAPER_COMPONENT,
//                ComponentName(context, FluidWallpaperService::class.java)
//            )
//        }.also {
//            activityResult.launch(it)
//        }
//        try {
//            WallpaperManager.getInstance(context).clear()
//        } catch (e: IOException) {
//            e.printStackTrace()
//        } finally {
//        }
//    }

    fun setWallPaperGif(
        context: Context,
        activityResult: ActivityResultLauncher<Intent>
    ) {
        Intent(WallpaperManager.ACTION_CHANGE_LIVE_WALLPAPER).apply {
            putExtra(
                WallpaperManager.EXTRA_LIVE_WALLPAPER_COMPONENT,
                ComponentName(context, GIFWallpaperService::class.java)
            )
        }.also {
            activityResult.launch(it)
        }
        try {
            WallpaperManager.getInstance(context).clear()
        } catch (e: IOException) {
            e.printStackTrace()
        } finally {
        }
    }

    fun parserListImage(json: String):List<ImageTypes>?{
        try {
            val listType = object : TypeToken<List<ImageTypes>>() {}.type
            val imageList: List<ImageTypes> = Gson().fromJson(json, listType)
            return imageList
        }catch (e:Exception){
            Log.e("ERROR", "parserListImage: ${e.message}", )
            return null
        }
    }

    fun capitalizeFirstLetter(input: String): String {
        if(input=="ai wallpaper"){
            return "AI Wallpaper"
        }else{
            return input.split(" ").joinToString(" ") {
                it.replaceFirstChar { char ->
                    if (char.isLowerCase()) char.uppercaseChar() else char
                }
            }
        }

    }

    fun checkCate(cate: String): Boolean{
        listCatName.map {
            if(cate=="AI_Wallpaper"){
                if(it.equals(cate.replace("_"," "), true)){
                    return true
                }
            }else{
                if(it.equals(cate, true)){
                    return true
                }
            }

        }
        return false
    }

    fun setCurrentTimeBoughtIap(context: Context) {
        val time = LocalDateTime.now().atZone(ZoneId.systemDefault()).toInstant().toEpochMilli()
        val preferences =
            context.getSharedPreferences(context.packageName, Context.MODE_PRIVATE)
        preferences.edit().putLong("time_bought_iap", time).apply()
    }

    fun getCurrentTimeBoughtIap(context: Context): LocalDateTime {
        val preferences =
            context.getSharedPreferences(context.packageName, Context.MODE_PRIVATE)
        return LocalDateTime.ofInstant(
            Instant.ofEpochMilli(
                preferences.getLong(
                    "time_bought_iap",
                    0L
                )
            ), ZoneId.systemDefault()
        )
    }

    fun setCurrentKeyIap(context: Context, key: String,nameBook:String) {
        val preferences =
            context.getSharedPreferences(context.packageName, Context.MODE_PRIVATE)
        preferences.edit().putString("current_key_iap_${nameBook}", key).apply()
    }

    fun getCurrentKeyIap(context: Context,nameBook:String): String {
        val preferences =
            context.getSharedPreferences(context.packageName, Context.MODE_PRIVATE)
        return preferences.getString("current_key_iap_${nameBook}", "").toString()
    }


}