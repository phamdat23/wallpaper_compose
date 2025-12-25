package com.itsol.vn.wallpaper.live.parallax.ui.activity.components


import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil3.ImageLoader
import coil3.compose.AsyncImage
import coil3.request.CachePolicy
import coil3.request.ImageRequest
import com.itsol.vn.wallpaper.live.parallax.R
import com.itsol.vn.wallpaper.live.parallax.model.CategoryModel
import com.itsol.vn.wallpaper.live.parallax.utils.Common
import com.itsol.vn.wallpaper.live.parallax.utils.Purchase

@Composable
fun ItemCategory(
    modifier: Modifier = Modifier,
    categoryModel: CategoryModel,
    isBought: Boolean = false,
    onClickItem: (CategoryModel) -> Unit
) {
    val context = LocalContext.current
    var isLoading by rememberSaveable {
        mutableStateOf(true)
    }
    var isPurchase by remember { mutableStateOf(false) }
    val cate = if (categoryModel.categoryName == "AI_Wallpaper") categoryModel.categoryName.replace(
        "_",
        " "
    ) else categoryModel.categoryName

    LaunchedEffect(isBought) {
        isPurchase = isBought
    }
    Box(modifier = modifier.clickable {
        onClickItem.invoke(categoryModel)
    }) {

        AsyncImage(
            model = ImageRequest.Builder(LocalContext.current)
                .data(categoryModel.url)
                .diskCachePolicy(CachePolicy.DISABLED)
                .build(),
            contentDescription = "",
            modifier = Modifier
                .clip(shape = RoundedCornerShape(12.dp))
                .fillMaxSize()
                .background(colorResource(R.color.color_text_gray)),
            contentScale = ContentScale.FillBounds,
            onLoading = { isLoading = true },
            onSuccess = { isLoading = false },

            )
        if (isPurchase&&!Purchase.isBought) {
            Image(
                ImageVector.vectorResource(R.drawable.ic_premium_wallpaper),
                contentDescription = "",
                modifier=Modifier.align(Alignment.TopStart)
            )
        }
        if (isLoading) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .clip(shape = RoundedCornerShape(12.dp))
                    .background(colorResource(R.color.color_disable))
            ) {
                CircularProgressIndicator(
                    modifier = Modifier
                        .width(64.dp)
                        .align(Alignment.Center),
                    color = colorResource(R.color.color_primary),
                    trackColor = colorResource(R.color.color_unselected),

                    )
            }
        }
//        Text(
//            text = categoryModel.categoryName.replace(categoryModel.categoryName.first(), categoryModel.categoryName.first().uppercaseChar()),
//            textAlign = TextAlign.Start,
//            fontFamily = FontFamily(Font(R.font.readexpro_bold)),
//            fontWeight = FontWeight(700),
//            fontSize = 28.sp,
//            color = colorResource(R.color.white),
//            modifier = Modifier.align(Alignment.BottomEnd).padding(12.dp)
//        )


    }
}

@Preview(showBackground = true)
@Composable
private fun ItemCategoryPreview() {

}