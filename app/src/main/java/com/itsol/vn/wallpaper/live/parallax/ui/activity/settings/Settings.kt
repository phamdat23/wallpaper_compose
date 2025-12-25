package com.itsol.vn.wallpaper.live.parallax.ui.activity.settings

import android.annotation.SuppressLint
import android.preference.PreferenceManager
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.material3.Text
import androidx.compose.material3.Icon
import androidx.compose.material.Slider
import androidx.compose.material.SliderDefaults
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.input.pointer.pointerInput
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
import com.itsol.vn.wallpaper.live.parallax.LocalNavController
import com.itsol.vn.wallpaper.live.parallax.R
import com.itsol.vn.wallpaper.live.parallax.ads.AdsManager
import com.itsol.vn.wallpaper.live.parallax.ads.RemoteConfig
import com.itsol.vn.wallpaper.live.parallax.ui.activity.components.TextWithStyle
import com.itsol.vn.wallpaper.live.parallax.utils.Router

@SuppressLint("RestrictedApi")
@Composable
fun Settings(modifier: Modifier = Modifier) {
    val navigationController = LocalNavController.current
    val context = LocalContext.current

    val sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context)
    var currentEffect by rememberSaveable {
        mutableStateOf(
            sharedPreferences.getString(
                context.getString(R.string.pref_sensitivity_key),
                context.getString(R.string.pref_sensitivity_default)
            ).toString().toInt()
        )
    }
    var isCheckSwithBattery by rememberSaveable {
        mutableStateOf(
            !sharedPreferences.getBoolean(
                context.getString(R.string.pref_sensor_key),
                context.getResources().getBoolean(R.bool.pref_sensor_default)
            )
        )
    }
    LaunchedEffect(true) {
        AdsManager.logEvent(context, Router.SETTING)
        if (RemoteConfig.native_tutorial == "1") {
            AdsManager.loadNative(context, AdsManager.nativeTutorial)
        }
    }
    var isBackStack by remember { mutableStateOf(true) }
    AdsManager.ScreenNameLogEffect(Router.SETTING)
    Column(
        modifier = modifier
            .fillMaxSize()
            .background(color = colorResource(R.color.color_bg))
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .wrapContentHeight()
                .padding(horizontal = 8.dp, vertical = 30.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Image(
                painterResource(R.drawable.ic_arrow_back),
                contentDescription = "",
                colorFilter = ColorFilter.tint(
                    colorResource(R.color.color_bold_900)
                ),
                modifier = Modifier
                    .size(48.dp)
                    .padding(12.dp)
                    .clickable {
                        if(isBackStack){
                            isBackStack=false
                            if(navigationController.currentBackStack.value.isNotEmpty()){
                                navigationController.popBackStack()
                            }
                        }

                    }

            )
            Text(
                textAlign = TextAlign.Start,
                text = stringResource(R.string.setting),
                fontSize = 16.sp,
                fontFamily = FontFamily(
                    Font(R.font.readexpro_bold)
                ),
                fontWeight = FontWeight(700),
                color = colorResource(R.color.color_bold_900),
                modifier = Modifier
                    .weight(1f)
                    .padding(horizontal = 8.dp)
            )
            TextWithStyle(
                stringResource(R.string.done),
                Brush.linearGradient(
                    listOf(
                        colorResource(R.color.color_gradient_search_start),
                        colorResource(R.color.color_gradient_search_end)
                    )
                ), modifier = Modifier.pointerInput(Unit) {
                    detectTapGestures(onPress = {
                        // click done
                        sharedPreferences.edit().putBoolean(
                            context.getString(R.string.pref_sensor_key),
                            !isCheckSwithBattery
                        ).apply()
                        sharedPreferences.edit().putString(
                            context.getString(R.string.pref_sensitivity_key),
                            currentEffect.toInt().toString()
                        ).apply()
                        sharedPreferences.edit().putString(
                            context.getString(R.string.pref_depth_key),
                            currentEffect.toInt().toString()
                        ).apply()
                        if(navigationController.currentBackStack.value.isNotEmpty()){
                            navigationController.popBackStack()
                        }
                    })
                }
            )
        }

        Text(
            text = stringResource(R.string.general_settings),
            textAlign = TextAlign.Start,
            fontSize = 18.sp,
            fontFamily = FontFamily(
                Font(R.font.readexpro_bold)
            ),
            fontWeight = FontWeight(700),
            color = colorResource(R.color.color_secscond),
            modifier = Modifier.padding(start = 16.dp)
        )
        Spacer(modifier = Modifier.height(20.dp))
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .wrapContentHeight()
                .padding(horizontal = 16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Image(
                painterResource(R.drawable.ic_battery_charging),
                contentDescription = "",
                modifier = Modifier.size(24.dp),
                colorFilter = ColorFilter.tint(
                    colorResource(R.color.color_bold_900)
                )
            )
            Column(
                modifier = Modifier
                    .weight(1f)
                    .padding(horizontal = 12.dp)
            ) {
                Text(
                    text = stringResource(R.string.battery_super_save_mode),
                    textAlign = TextAlign.Start,
                    fontSize = 14.sp,
                    fontWeight = FontWeight(500),
                    fontFamily = FontFamily(
                        Font(R.font.readexpro_medium)
                    ), color = colorResource(R.color.color_bold_900)
                )
                Text(
                    text = stringResource(R.string.will_affect_the_responsiveness_of_4D_effect_),
                    textAlign = TextAlign.Start,
                    fontSize = 12.sp,
                    fontWeight = FontWeight(400),
                    fontFamily = FontFamily(
                        Font(R.font.readexpro_regular)
                    ), color = colorResource(R.color.color_bold_900)
                )
            }
            Switch(
                checked = isCheckSwithBattery,
                onCheckedChange = {
                    isCheckSwithBattery = it
                },
                thumbContent = {

                    if (isCheckSwithBattery) {
                        Icon(
                            ImageVector.vectorResource(R.drawable.ic_check),
                            contentDescription = "",
                            modifier = Modifier.size(SwitchDefaults.IconSize)
                        )
                    } else {
                        Icon(
                            painterResource(R.drawable.ic_close),
                            contentDescription = "",
                            modifier = Modifier
                                .size(SwitchDefaults.IconSize)
                                .padding(2.dp),

                        )
                    }
                },
                colors = SwitchDefaults.colors(
                    checkedTrackColor = colorResource(R.color.color_track_switch),
                    uncheckedTrackColor = colorResource(R.color.color_neutral_400),
                    checkedThumbColor = colorResource(R.color.white),
                    uncheckedThumbColor = colorResource(R.color.color_neutral_200),
                    uncheckedIconColor = colorResource(R.color.color_bold_900),
                    checkedIconColor = colorResource(R.color.color_track_switch),
                    uncheckedBorderColor = colorResource(R.color.color_neutral_400),
                    checkedBorderColor = colorResource(R.color.color_track_switch),
                )
            )

        }

        Spacer(modifier = Modifier.height(30.dp))
        Text(
            text = stringResource(R.string._4D_parallax_settings),
            textAlign = TextAlign.Start,
            fontSize = 18.sp,
            fontFamily = FontFamily(
                Font(R.font.readexpro_bold)
            ),
            fontWeight = FontWeight(700),
            color = colorResource(R.color.color_secscond),
            modifier = Modifier.padding(start = 16.dp)
        )
        Spacer(modifier = Modifier.height(20.dp))
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .wrapContentHeight()
                .padding(horizontal = 16.dp)
        ) {
            Image(
                ImageVector.vectorResource(R.drawable.ic_4d_effect),
                contentDescription = "",
                modifier = Modifier.align(Alignment.CenterVertically),
                colorFilter = ColorFilter.tint(
                    colorResource(R.color.color_bold_900)
                )
            )
            Spacer(modifier = Modifier.width(12.dp))
            Column(
                modifier = Modifier
                    .weight(1f)
                    .wrapContentHeight()
                    .align(Alignment.CenterVertically)
            ) {
                Text(
                    text = stringResource(R.string._4D_parallax_effect_strength),
                    textAlign = TextAlign.Start,
                    fontFamily = FontFamily(Font(R.font.readexpro_medium)),
                    fontWeight = FontWeight(500),
                    fontSize = 14.sp,
                    color = colorResource(R.color.color_bold_900),
                    modifier = Modifier.padding(start = 4.dp)
                )
                Row(modifier = Modifier) {
                    Slider(
                        value = currentEffect.toFloat(),
                        valueRange = 0f..300f,
                        onValueChange = {
                            currentEffect = it.toInt()
                        },

                        colors = SliderDefaults.colors(
                            thumbColor = colorResource(R.color.color_secscond),
                            activeTrackColor = colorResource(R.color.color_primary),
                            inactiveTrackColor = colorResource(R.color.color_neutral_300)
                        ), modifier = Modifier
                            .weight(1f)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "${currentEffect.toInt()}",
                        textAlign = TextAlign.Center,
                        fontWeight = FontWeight(400),
                        fontFamily = FontFamily(Font(R.font.readexpro_regular)),
                        fontSize = 12.sp,
                        color = colorResource(R.color.color_bold_900),
                        modifier = Modifier.align(Alignment.CenterVertically)
                    )
                }
            }

        }
        Spacer(modifier = Modifier.height(20.dp))
//        Row(
//            modifier = Modifier
//                .fillMaxWidth()
//                .wrapContentHeight()
//                .padding(horizontal = 16.dp)
//                .clickable {
//                    navigationController.navigate("${Router.INTRO}?isSetting=${true}")
//                },
//            verticalAlignment = Alignment.CenterVertically
//        ) {
//            Image(ImageVector.vectorResource(R.drawable.ic_tutorial), contentDescription = "")
//            Spacer(modifier = Modifier.width(4.dp))
//            Text(
//                text = stringResource(R.string.tutorial),
//                textAlign = TextAlign.Start,
//                fontWeight = FontWeight(700),
//                fontFamily = FontFamily(Font(R.font.readexpro_bold)),
//                fontSize = 18.sp,
//                color = colorResource(R.color.color_secscond),
//                modifier = Modifier.align(Alignment.CenterVertically)
//            )
//            Spacer(modifier = Modifier.weight(1f))
//            Image(
//                ImageVector.vectorResource(R.drawable.ic_arrow_forward), contentDescription = "",
//                colorFilter = ColorFilter.tint(
//                    colorResource(R.color.color_bold_900)
//                )
//            )
//        }
    }
}


@Preview(showSystemUi = true, showBackground = true)
@Composable
private fun PreviewSettings() {
    Settings()
}