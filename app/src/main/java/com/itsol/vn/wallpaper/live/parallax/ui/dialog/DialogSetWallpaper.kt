package com.itsol.vn.wallpaper.live.parallax.ui.dialog

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Card
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.itsol.vn.wallpaper.live.parallax.R
import com.itsol.vn.wallpaper.live.parallax.utils.Common
import com.itsol.vn.wallpaper.live.parallax.utils.FlagSetWallPaper

@Composable
fun DialogSetWallpaper(onDismiss: (Boolean) -> Unit) {
    Dialog(

        onDismissRequest = { onDismiss.invoke(false) },
        properties = DialogProperties(
            usePlatformDefaultWidth = true,
            decorFitsSystemWindows = false
        )
    ) {
        Card(
            shape = RoundedCornerShape(12.dp),
            modifier = Modifier
                .fillMaxWidth()
                .wrapContentHeight()
                .background(color = Color.Transparent)
                .padding(8.dp)
        ) {

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(color = colorResource(R.color.color_bg_bottom_bar))
                    .padding(vertical = 12.dp),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = stringResource(R.string.home_screen),
                    textAlign = TextAlign.Center,
                    fontFamily = FontFamily(
                        Font(R.font.readexpro_semibold)
                    ),
                    fontWeight = FontWeight(600),
                    color = colorResource(R.color.white),
                    fontSize = 18.sp,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(4.dp)
                        .clickable {
                            Common.flagSetWallPaper = FlagSetWallPaper.HOME_SCREEN
                            onDismiss.invoke(true)
                        }
                )
                Box(modifier = Modifier
                    .fillMaxWidth()
                    .height(1.dp)
                    .background(color = colorResource(R.color.color_secscond))) {  }
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = stringResource(R.string.Lock_screen),
                    textAlign = TextAlign.Center,
                    fontFamily = FontFamily(
                        Font(R.font.readexpro_semibold)
                    ),
                    fontWeight = FontWeight(600),
                    color = colorResource(R.color.white),
                    fontSize = 18.sp,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(4.dp)
                        .clickable {
                            Common.flagSetWallPaper = FlagSetWallPaper.LOCK_SCREEN
                            onDismiss.invoke(true)
                        }
                )
                Box(modifier = Modifier
                    .fillMaxWidth()
                    .height(1.dp)
                    .background(color = colorResource(R.color.color_secscond))) {  }
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = stringResource(R.string.both_screens),
                    textAlign = TextAlign.Center,
                    fontFamily = FontFamily(
                        Font(R.font.readexpro_semibold)
                    ),
                    fontWeight = FontWeight(600),
                    color = colorResource(R.color.white),
                    fontSize = 18.sp,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 4.dp)
                        .clickable {
                            Common.flagSetWallPaper = FlagSetWallPaper.LOCK_AND_HOME_SCREEN
                            onDismiss.invoke(true)
                        }
                )

            }
        }
    }

}

@Preview(showBackground = true)
@Composable
private fun PreviewDialogSetWallpaper() {
    DialogSetWallpaper {

    }
}