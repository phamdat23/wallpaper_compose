package com.itsol.vn.wallpaper.live.parallax.ui.activity.language

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.itsol.vn.wallpaper.live.parallax.R
import com.itsol.vn.wallpaper.live.parallax.model.LanguageModel

@Composable
fun LanguageItem(
    data: LanguageModel,
    index: Int,
    isSelected: Boolean,
    onCLickItemListener: (index: Int) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp)
            .height(60.dp)
            .clip(RoundedCornerShape(16.dp))
            .background(color = colorResource(R.color.color_bg_bottom_bar)),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Spacer(modifier = Modifier.width(16.dp))
            Image(
                modifier = Modifier.size(32.dp),
                painter = painterResource(data.flag),
                contentDescription = ""
            )

            Text(
                modifier = Modifier.padding(start = 16.dp),
                text = stringResource(data.name),
                color = Color.White,
                fontFamily = FontFamily(Font(R.font.readexpro_medium)),
                fontWeight = FontWeight(500),
                fontSize = 16.sp
            )
        }
        GradientRadioButton(
            selected = isSelected, // Truyền trạng thái chọn vào đây
            onClick = {
                onCLickItemListener(index) // Thông báo cho composable cha về lựa chọn mới
            },
            modifier = Modifier.padding(end = 16.dp)
        )
    }
}

@Composable
fun GradientRadioButton(
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val gradientBrush = Brush.linearGradient(
        colors = listOf(
            colorResource(R.color.color_secscond),
            colorResource(R.color.color_primary)
        )
    )

    Box(
        modifier = modifier
            .size(24.dp)
            .border(
                BorderStroke(2.dp, gradientBrush),
                shape = RoundedCornerShape(16.dp)
            )
            .clickable { onClick() } // Gọi hàm onClick khi nhấn
    ) {
        if (selected) {
            Box(
                modifier = Modifier
                    .size(15.5.dp)
                    .background(
                        brush = gradientBrush,
                        shape = CircleShape
                    )
                    .align(Alignment.Center)
            )
        }
    }
}

@Preview
@Composable
fun LanguageItemPreview() {
    LanguageItem(
        LanguageModel(
            R.string.vietnamese,
            R.drawable.vietnam,
            "vn"
        ),
        0, true
    ) {

    }
}