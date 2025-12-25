package com.itsol.vn.wallpaper.live.parallax.ui.dialog

//noinspection UsingMaterialAndMaterial3Libraries
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
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
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextIndent
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.hilt.navigation.compose.hiltViewModel
import coil3.compose.AsyncImage
import coil3.request.CachePolicy
import coil3.request.ImageRequest
import com.itsol.vn.wallpaper.live.parallax.R
import com.itsol.vn.wallpaper.live.parallax.model.WallpaperModel
import com.itsol.vn.wallpaper.live.parallax.ui.activity.components.ButtonSetAsWallpaper
import com.itsol.vn.wallpaper.live.parallax.ui.activity.components.ItemWallpaper
import com.itsol.vn.wallpaper.live.parallax.ui.activity.components.StateLoadingImage
import com.itsol.vn.wallpaper.live.parallax.utils.Common
import com.itsol.vn.wallpaper.live.parallax.utils.Constants
import com.itsol.vn.wallpaper.live.parallax.viewModel.MainViewModel

@Composable
fun DialogDailyReward(
    onDismiss: () -> Unit,
    onSetWallpaper: () -> Unit,
    wallpaperModel: WallpaperModel
) {
    var isLoading by remember { mutableStateOf(false) }
    val viewModel = hiltViewModel<MainViewModel>()
    val context= LocalContext.current
    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(
            usePlatformDefaultWidth = true,
            dismissOnClickOutside = false,
            dismissOnBackPress = true,
            decorFitsSystemWindows = false
        )
    ) {
        Card(
            shape = RoundedCornerShape(12.dp),
            modifier = Modifier
                .fillMaxWidth()
                .height(650.dp)
                .background(color = Color.Transparent)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(12.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Row(
                    horizontalArrangement = Arrangement.End,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Image(
                        modifier = Modifier
                            .size(30.dp)
                            .padding(4.dp)
                            .clickable { onDismiss() },
                        imageVector = ImageVector.vectorResource(R.drawable.ic_close),
                        contentDescription = "",
                        colorFilter = ColorFilter.tint(colorResource(R.color.color_disable))
                    )
                }
                TextDailyReward(
                    previewText = stringResource(R.string.daily_reward),
                    brush = Brush.linearGradient(
                        listOf(
                            colorResource(R.color.color_gradient_primary),
                            colorResource(R.color.color_gradient_secscond)
                        )
                    )
                )
                Spacer(modifier = Modifier.padding(top = 16.dp))
                ItemWallpaper2(
                    modifier = Modifier.weight(1f).padding(horizontal = 6.dp),
                    titleAttr = "",
                    isDownLoad = false,
                    wallpaperModel = wallpaperModel,
                    isBought = (Common.checkCate(wallpaperModel.categories)&&!Common.getBoughtIap(context =context )),
                    onClickItem = {},
                    onClickFavorite = { wallpaperModel ->
                        viewModel.updateFavoriteWallPaper(
                            wallpaperModel.id,
                            wallpaperModel.favorite
                        )
                    }
                )

                Spacer(modifier = Modifier.padding(top = 16.dp))
                ButtonSetAsWallpaper(
                    modifier = Modifier
                        .height(48.dp)
                        .width(213.dp),
                    onClickSetWallpaper = {
                        onSetWallpaper.invoke()
                        onDismiss()
                    },
                    isDownLoad =  if (!wallpaperModel.download) false else true
                )
                Spacer(modifier = Modifier.padding(top = 16.dp))
                if (isLoading) {
                    Dialog(onDismissRequest = { }) {
                        Box(
                            modifier = Modifier
                                .wrapContentSize()
                                .background(Color.Transparent)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(60.dp)
                                    .background(Color.White, shape = RoundedCornerShape(40.dp)),
                                contentAlignment = Alignment.Center
                            ) {
                                CircularProgressIndicator(color = Color.Black)
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun TextDailyReward(previewText: String, brush: Brush, modifier: Modifier = Modifier) {
    val textStyle = TextStyle(
        textAlign = TextAlign.Center,
        lineHeight = 26.sp,
        fontSize = 24.sp,
        fontFamily = FontFamily(Font(R.font.readexpro_bold)),
        fontWeight = FontWeight(700),
//        textIndent = TextIndent(firstLine = 14.sp, restLine = 3.sp),
        brush = brush
    )

    Text(
        text = previewText,
        modifier = modifier,
        style = textStyle,
        textAlign = TextAlign.Center
    )
}


@Composable
private fun ItemWallpaper2(
    modifier: Modifier = Modifier,
    titleAttr: String,
    isDownLoad: Boolean=false,
    isBought: Boolean = false,
    wallpaperModel: WallpaperModel,
    onClickItem: (WallpaperModel) -> Unit,
    onClickFavorite: (WallpaperModel) -> Unit,
) {
    var stateLoadingImage by remember {
        mutableStateOf(StateLoadingImage.ON_LOADING)
    }
    var favorite by remember {
        mutableStateOf(wallpaperModel.favorite)
    }
    val context = LocalContext.current
    val isDownloads by rememberSaveable { mutableStateOf(isDownLoad) }
    val types by remember { mutableStateOf(wallpaperModel.types.split(",")) }
    var isPurchase by remember { mutableStateOf(false) }
    LaunchedEffect(isBought) {
        isPurchase = isBought
    }
    Box(modifier = modifier){
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
                    .build()
                ,
                contentDescription = "",
                modifier = modifier
                    .fillMaxWidth()
                    .clip(shape = RoundedCornerShape(12.dp)),
                error = painterResource(R.drawable.img_wallpaper_demo),
                contentScale = ContentScale.FillBounds,
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
                    .align(Alignment.TopEnd)
                    .alpha(if (stateLoadingImage == StateLoadingImage.SUCCESS) 1f else 0f)
                    .padding(top = 10.dp, end = 15.dp)
                    .clickable {
                        if(isDownloads){
                            // delete wallpaper in device
                            onClickFavorite.invoke(wallpaperModel)
                        }else{
                            favorite=!wallpaperModel.favorite
                            wallpaperModel.favorite=favorite
                            onClickFavorite.invoke(wallpaperModel)
                        }

                    }

            ) {
                if(isDownloads){
                    Image(
                        ImageVector.vectorResource(R.drawable.ic_delete),
                        contentDescription = "",
                        modifier = Modifier
                            .size(28.dp)
                            .clip(shape = CircleShape)
                            .background(color = colorResource(R.color.white))
                            .padding(6.dp)
                    )
                }else{
                    if (favorite) {
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

                    }else if (it == Constants.LIVE && it != titleAttr) {
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
                    }else if (it == Constants.PARALLAX && it != titleAttr) {
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

@Preview(showBackground = true)
@Composable
fun DialogDailyRewardPreview() {

}