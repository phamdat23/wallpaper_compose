package com.itsol.vn.wallpaper.live.parallax.ui.activity.onboarding

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentWidth
//noinspection UsingMaterialAndMaterial3Libraries
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.itsol.vn.wallpaper.live.parallax.R

@Composable
fun SinglePageObBoarding(data: OnBoardingData) {
    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Image(
            modifier = Modifier.fillMaxWidth().weight(1f),
            painter = painterResource(data.image),
            contentDescription = "",
        )

        Text(
            modifier = Modifier.padding(top = 16.dp),
            text = data.description,
            color = Color.White,
            fontWeight = FontWeight(700),
            fontFamily = FontFamily(Font(R.font.readexpro_bold)),
            fontSize = 26.sp,
            textAlign = TextAlign.Center
        )
    }
}

@Preview
@Composable
fun SinglePageObBoardingPreview() {
    val context = LocalContext.current
    SinglePageObBoarding(
        data = OnBoardingData(
            image = R.drawable.onbroading1,
            description = context.getString(R.string.wallpaper_4d)
        )
    )
}