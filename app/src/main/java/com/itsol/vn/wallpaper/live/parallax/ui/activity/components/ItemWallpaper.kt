package com.itsol.vn.wallpaper.live.parallax.ui.activity.components

import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.compose.ui.zIndex
import coil3.ImageLoader
import coil3.compose.AsyncImage
import coil3.request.CachePolicy
import coil3.request.ImageRequest
import com.itsol.vn.wallpaper.live.parallax.R
import com.itsol.vn.wallpaper.live.parallax.model.WallpaperModel
import com.itsol.vn.wallpaper.live.parallax.utils.Common
import com.itsol.vn.wallpaper.live.parallax.utils.Constants

enum class StateLoadingImage {
    ON_LOADING,
    SUCCESS,
    ERROR
}

@Composable
fun ItemWallpaper(
    modifier: Modifier = Modifier,
    titleAttr: String,
    isBought: Boolean = false,
    isDownLoad: Boolean = false,
    wallpaperModel: WallpaperModel,
    onClickItem: (WallpaperModel) -> Unit,
    onClickFavorite: (WallpaperModel) -> Unit,
) {
    val context = LocalContext.current
    var stateLoadingImage by remember {
        mutableStateOf(StateLoadingImage.ON_LOADING)
    }
    var favorite by remember {
        mutableStateOf(wallpaperModel.favorite)
    }
    val isDownloads by rememberSaveable { mutableStateOf(isDownLoad) }
    val types by remember { mutableStateOf(wallpaperModel.types.split(",")) }
    var isPurchase by remember { mutableStateOf(false) }
    val nameCat by rememberSaveable { mutableStateOf(wallpaperModel.categories) }
    val cate by rememberSaveable{ mutableStateOf(if (nameCat == "AI_Wallpaper") nameCat.replace(
        "_",
        " "
    ) else nameCat) }
    LaunchedEffect(isBought) {
        Log.e("AAAAAAAAAA", "ItemCategory: ${!isBought} and $${Common.getCurrentKeyIap(context =context , cate)==""}", )
        isPurchase = isBought
    }
    Box(modifier = modifier) {
        // loading image
        if (stateLoadingImage == StateLoadingImage.ON_LOADING) {
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
        Box(
            modifier = Modifier
                .fillMaxSize()
                .alpha(if (stateLoadingImage == StateLoadingImage.SUCCESS) 1f else 0f)
                .clickable {
                    onClickItem.invoke(wallpaperModel)
                }
        ) {
            AsyncImage(
                model =
//                R.drawable.thumb_cat_1
                ImageRequest.Builder(LocalContext.current)
                    .data(wallpaperModel.url)
                    .diskCachePolicy(CachePolicy.DISABLED)
                    .build(),
                contentDescription = "",
                modifier = modifier
                    .fillMaxWidth()
                    .clip(shape = RoundedCornerShape(12.dp)),
                error = painterResource(R.drawable.img_wallpaper_demo),
                contentScale = ContentScale.Crop,
                onLoading = {
                    stateLoadingImage = StateLoadingImage.ON_LOADING
                },
                onSuccess = {
                    stateLoadingImage = StateLoadingImage.SUCCESS
                },
                onError = {
                    stateLoadingImage = StateLoadingImage.ERROR
                }

            )

            Box(
                modifier = Modifier
                    .padding(10.dp)
                    .align(Alignment.TopEnd)
                    .alpha(if (stateLoadingImage == StateLoadingImage.SUCCESS) 1f else 0f)
                    .clickable {
                        if (isDownloads) {
                            // delete wallpaper in device
                            onClickFavorite.invoke(wallpaperModel)
                        } else {
                            favorite = !wallpaperModel.favorite
                            wallpaperModel.favorite = favorite
                            onClickFavorite.invoke(wallpaperModel)
                        }

                    }

            ) {
                if (isDownloads) {
                    Image(
                        ImageVector.vectorResource(R.drawable.ic_delete),
                        contentDescription = "",

                        modifier = Modifier
                            .size(28.dp)
                            .clip(shape = CircleShape)
                            .background(color = colorResource(R.color.white))
                            .padding(6.dp)
                    )
                } else {
                    if (favorite || wallpaperModel.favorite) {
                        Image(
                            ImageVector.vectorResource(R.drawable.ic_favorite),
                            contentDescription = "",
                            colorFilter = ColorFilter.tint(
                                colorResource(R.color.color_red)
                            ),
                            modifier = Modifier
                                .size(28.dp)
                                .clip(shape = CircleShape)
                                .background(color = colorResource(R.color.white))
                                .padding(6.dp)
                        )
                    } else {
                        Image(
                            ImageVector.vectorResource(R.drawable.ic_favorite),
                            contentDescription = "",
                            colorFilter = ColorFilter.tint(
                                colorResource(R.color.color_disable_icon)
                            ),
                            modifier = Modifier
                                .size(28.dp)
                                .clip(shape = CircleShape)
                                .background(color = colorResource(R.color.white))
                                .padding(6.dp)
                        )
                    }
                }


            }

            Row(
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .padding(12.dp)
                    .alpha(if (stateLoadingImage == StateLoadingImage.SUCCESS) 1f else 0f),
                horizontalArrangement = Arrangement.SpaceAround
            ) {
                types.map {
                    if (it == Constants.IMAGE4K && it != titleAttr) {
                        Image(
                            ImageVector.vectorResource(R.drawable.ic_tab_4k),
                            contentDescription = "",
                            colorFilter = ColorFilter.tint(colorResource(R.color.white)),
                            modifier = Modifier
                                .size(28.dp)
                                .clip(shape = CircleShape)
                                .background(color = colorResource(R.color.color_black_50))
                                .padding(6.dp)
                        )

                    } else if (it == Constants.LIVE && it != titleAttr) {
                        Spacer(modifier = Modifier.width(4.dp))
                        Image(
                            ImageVector.vectorResource(R.drawable.ic__tab_live),
                            contentDescription = "",
                            colorFilter = ColorFilter.tint(colorResource(R.color.white)),
                            modifier = Modifier
                                .size(28.dp)
                                .clip(shape = CircleShape)
                                .background(color = colorResource(R.color.color_black_50))
                                .padding(6.dp)
                        )
                    } else if (it == Constants.PARALLAX && it != titleAttr) {
                        Spacer(modifier = Modifier.width(4.dp))
                        Image(
                            ImageVector.vectorResource(R.drawable.ic_tab_4d),
                            contentDescription = "",
                            colorFilter = ColorFilter.tint(colorResource(R.color.white)),
                            modifier = Modifier
                                .size(28.dp)
                                .clip(shape = CircleShape)
                                .background(color = colorResource(R.color.color_black_50))
                                .padding(6.dp)
                        )

                    }
                }


            }

            if (isPurchase) {
                Image(
                    ImageVector.vectorResource(R.drawable.ic_premium_wallpaper),
                    contentDescription = "",
                    modifier=Modifier.align(Alignment.TopStart)
                )
            }


        }

    }


}

@Preview(showBackground = true, showSystemUi = true)
@Composable
private fun PreviewItemWallpaper() {
//    ItemWallpaper()
}
