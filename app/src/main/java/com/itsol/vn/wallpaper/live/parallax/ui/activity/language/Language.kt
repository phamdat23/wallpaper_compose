package com.itsol.vn.wallpaper.live.parallax.ui.activity.language

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.toMutableStateList
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.itsol.vn.wallpaper.live.parallax.LocalNavController
import com.itsol.vn.wallpaper.live.parallax.MainActivity
import com.itsol.vn.wallpaper.live.parallax.R
import com.itsol.vn.wallpaper.live.parallax.ads.AdsManager
import com.itsol.vn.wallpaper.live.parallax.utils.Common
import com.itsol.vn.wallpaper.live.parallax.utils.Router
import com.itsol.ironsourcelib.AppOpenManager
import com.itsol.ironsourcelib.GoogleENative
import com.itsol.vn.wallpaper.live.parallax.ads.RemoteConfig
import com.itsol.vn.wallpaper.live.parallax.model.LanguageModel
import java.util.Locale

@Composable
fun Language() {
    val context = LocalContext.current
    val navigation = LocalNavController.current
    var positionSelect by remember {
        mutableStateOf(1)

    }
    val listLanguage = remember {
        mutableStateListOf<LanguageModel>()
    }
    var remoteLangauge by remember { mutableStateOf("") }
    LaunchedEffect(true) {
        AdsManager.logEvent(context,Router.LANGUAGE)
        remoteLangauge= RemoteConfig.native_language
        positionSelect = Common.getPositionLanguage(context)
        listLanguage.addAll(Common.getListLanguage())
        AppOpenManager.getInstance().enableAppResumeWithActivity(MainActivity::class.java)
    }
    AdsManager.ScreenNameLogEffect(Router.LANGUAGE)
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(colorResource(R.color.color_bg_language))
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(70.dp)
                .background(
                    brush = Brush.linearGradient(
                        colors = listOf(
                            colorResource(R.color.color_secscond),
                            colorResource(R.color.color_primary)
                        )
                    )
                )
            , verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            Image(
                modifier = Modifier
                    .size(48.dp)
                    .padding(8.dp)
                    .alpha(0f),
                imageVector = ImageVector.vectorResource(R.drawable.right),
                contentDescription = ""
            )
            Text(
                modifier = Modifier
                    .weight(1f),
                text = stringResource(R.string.language),
                color = Color.White,
                fontWeight = FontWeight.Bold,
                fontFamily = FontFamily(Font(R.font.readexpro_bold)),
                fontSize = 20.sp,
                textAlign = TextAlign.Center
            )
            Image(
                modifier = Modifier
                    .size(48.dp)
                    .padding(8.dp)
                    .clickable {
                        if (positionSelect != -1) {
                            Common.setPositionLanguage(context, positionSelect)
//                            startActivity(context)
                            navigation.navigate(Router.ONBOARDING){
                                popUpTo(Router.LANGUAGE){
                                    inclusive = true
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
                        }
                    },
                imageVector = ImageVector.vectorResource(R.drawable.right),
                contentDescription = ""
            )
        }
        LazyColumn(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
                .padding(top = 32.dp)
        ) {
            items(listLanguage.size) { index ->
                LanguageItem(
                    data = Common.getListLanguage()[index],
                    index = index,
                    isSelected = index == positionSelect,
                    onCLickItemListener = { selectedIndex ->
                        positionSelect = selectedIndex
                        Log.e("selected_position", selectedIndex.toString())
                    }
                )
            }
        }
        Spacer(modifier = Modifier.height(10.dp))
        if(remoteLangauge=="1"){
            AdsManager.ShowNativeWithLayout(context, AdsManager.nativeLanguage, R.layout.ad_template_language, GoogleENative.UNIFIED_MEDIUM)

        }



    }
}

fun startActivity(context: Context) {
    val intent = Intent(context, MainActivity::class.java)
    intent.putExtra("from_language_screen", true)
    intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
    context.startActivity(intent)
}


@Preview
@Composable
fun LanguagePreview() {
    Language()
}