package com.itsol.vn.wallpaper.live.parallax.ui.activity.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
//noinspection UsingMaterialAndMaterial3Libraries
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
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
import com.itsol.vn.wallpaper.live.parallax.R

@Composable
fun ButtonSetAsWallpaper(
    modifier: Modifier = Modifier,
    onClickSetWallpaper: () -> Unit,
    isDownLoad: Boolean,
) {
    Row(verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center,
        modifier = modifier
            .clip(RoundedCornerShape(0.dp))
            .background(
                brush = Brush.linearGradient(
                    colors = listOf(
                        colorResource(R.color.color_gradient_primary),
                        colorResource(R.color.color_gradient_primary)
                    )
                )
            )
            .clickable {
                onClickSetWallpaper.invoke()
            }

    ) {
        Image(
            modifier = Modifier.padding(
                top = 8.dp,
                bottom = 8.dp,
                start = 16.dp,
                end = 4.dp
            ),
            imageVector = ImageVector.vectorResource(R.drawable.rule_settings),
            contentDescription = ""
        )
        Text(
            modifier = Modifier.padding(
                start = 4.dp,
                top = 8.dp,
                bottom = 8.dp,
                end = 16.dp
            ),
            text = if (!isDownLoad) {
                stringResource(R.string.download)
            } else {
                stringResource(R.string.set_as_wallpaper)
            },
            color = colorResource(R.color.white),
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center,
            fontFamily = FontFamily(Font(R.font.readexpro_bold))
        )
    }

}

@Preview(showBackground = true)
@Composable
fun ButtonSetAsWallpaperPreView() {

}