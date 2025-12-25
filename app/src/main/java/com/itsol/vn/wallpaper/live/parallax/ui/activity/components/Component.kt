package com.itsol.vn.wallpaper.live.parallax.ui.activity.components

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.pager.PagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextIndent
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.itsol.vn.wallpaper.live.parallax.R

@Composable
fun TextWithStyle(previewText: String, brush: Brush, modifier: Modifier = Modifier) {
    val textStyle = TextStyle(
        textAlign = TextAlign.Center,
        lineHeight = 16.sp,
        fontSize = 16.sp,
        fontFamily = FontFamily(Font(R.font.quicksand_bold)),
        fontWeight = FontWeight(700),
        textIndent = TextIndent(firstLine = 14.sp, restLine = 3.sp),
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
fun TabLayout(indexSelected: Int, listItemTab: List<Int>, onClickItem: (Int) -> Unit) {
    var widthIndicator by rememberSaveable {
        mutableStateOf(IntSize.Zero.width)
    }

    Box (modifier = Modifier.fillMaxWidth()){
        Spacer(modifier = Modifier.fillMaxWidth().height(1.dp).background(color = colorResource(R.color.color_disable_icon)).align(Alignment.BottomCenter))
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .wrapContentHeight(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceAround
        ) {
            if (indexSelected == 0) {
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .padding(start = 12.dp, top = 8.dp, end = 12.dp, bottom = 0.dp)
                        .clickable {
                            onClickItem.invoke(0)
                        },
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Row(
                        modifier = Modifier
                            .wrapContentHeight()
                            .fillMaxWidth()
                            .onGloballyPositioned {
                                widthIndicator = (it.size.width / listItemTab.size)+10
                            },
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Text(
                            text = stringResource(listItemTab[0]),
                            fontSize = 16.sp,
                            textAlign = TextAlign.Center,
                            maxLines = 1,
                            overflow = TextOverflow.Clip,
                            color = colorResource(R.color.color_secscond),
                            fontWeight = FontWeight(700),
                            fontFamily = FontFamily(Font(R.font.readexpro_bold)),
                        )

                    }
                    Spacer(modifier = Modifier.height(4.dp))
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
//                            .width(widthIndicator.dp)
                            .height(2.dp)
                            .background(
                                Brush.linearGradient(
                                    listOf(
                                        colorResource(R.color.color_primary),
                                        colorResource(R.color.color_primary),
                                        colorResource(R.color.color_primary)
                                    )
                                )
                            )
                    )
                }

            } else {
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .padding(start = 12.dp, top = 8.dp, end = 12.dp, bottom = 0.dp)
                        .clickable {
                            onClickItem.invoke(0)
                        },
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Row(
                        modifier = Modifier
                            .wrapContentHeight()

                            .onGloballyPositioned {
                                widthIndicator = (it.size.width / listItemTab.size)+10
                            },
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Text(
                            text = stringResource(listItemTab[0]),
                            fontSize = 16.sp,
                            textAlign = TextAlign.Center,
                            maxLines = 1,
                            overflow = TextOverflow.Clip,
                            color = colorResource(R.color.color_disable),
                            fontWeight = FontWeight(700),
                            fontFamily = FontFamily(Font(R.font.readexpro_bold)),
                        )

                    }
                    Spacer(modifier = Modifier.height(4.dp))
                    Box(
                        modifier = Modifier
                            .height(1.dp)
                            .background(
                                Brush.linearGradient(
                                    listOf(
                                        colorResource(R.color.color_primary),
                                        colorResource(R.color.color_primary),
                                        colorResource(R.color.color_primary)
                                    )
                                )
                            )
                            .alpha(0f)
                    )
                }

            }
            if (indexSelected == 1) {
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .padding(start = 12.dp, top = 8.dp, end = 12.dp, bottom = 0.dp)
                        .onGloballyPositioned {
                            widthIndicator = (it.size.width / listItemTab.size)+10
                        }
                        .clickable {
                            onClickItem.invoke(1)
                        },
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Row(
                        modifier = Modifier
                            .wrapContentHeight()
                            .fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Text(
                            text = stringResource(listItemTab[1]),
                            fontSize = 16.sp,
                            textAlign = TextAlign.Center,
                            maxLines = 1,
                            overflow = TextOverflow.Clip,
                            color = colorResource(R.color.color_secscond),
                            fontWeight = FontWeight(700),
                            fontFamily = FontFamily(Font(R.font.readexpro_bold))
                        )

                    }
                    Spacer(modifier = Modifier.height(4.dp))
                    Box(
                        modifier = Modifier
//                            .width(widthIndicator.dp)
                            .fillMaxWidth()
                            .height(2.dp)
                            .background(
                                Brush.linearGradient(
                                    listOf(
                                        colorResource(R.color.color_primary),
                                        colorResource(R.color.color_primary),
                                        colorResource(R.color.color_primary)
                                    )
                                )
                            )
                    )
                }

            } else {
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .padding(start = 12.dp, top = 8.dp, end = 12.dp, bottom = 0.dp)
                        .clickable {
                            onClickItem.invoke(1)
                        },
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Row(
                        modifier = Modifier
                            .wrapContentHeight()
                            .onGloballyPositioned {
                                widthIndicator = (it.size.width / listItemTab.size)+10
                            },
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Text(
                            text = stringResource(listItemTab[1]),
                            fontSize = 16.sp,
                            textAlign = TextAlign.Center,
                            maxLines = 1,
                            overflow = TextOverflow.Clip,
                            color = colorResource(R.color.color_disable),
                            fontWeight = FontWeight(700),
                            fontFamily = FontFamily(Font(R.font.readexpro_bold))
                        )

                    }
                    Spacer(modifier = Modifier.height(4.dp))
                    Box(
                        modifier = Modifier
                            .height(1.dp)
                            .background(
                                Brush.linearGradient(
                                    listOf(
                                        colorResource(R.color.color_primary),
                                        colorResource(R.color.color_primary),
                                        colorResource(R.color.color_primary)
                                    )
                                )
                            )
                            .alpha(0f)
                    )
                }

            }
        }

    }

}

@Composable
fun TextGradient(previewText: String, brush: Brush, modifier: Modifier = Modifier,textSize: TextUnit ) {
    val textStyle = TextStyle(
        textAlign = TextAlign.Center,
        lineHeight = 16.sp,
        fontSize = textSize,
        fontFamily = FontFamily(Font(R.font.readexpro_bold)),
        fontWeight = FontWeight(700),
        textIndent = TextIndent(firstLine = 14.sp, restLine = 3.sp),
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
fun ButtonSet(text: String,
    modifier: Modifier = Modifier,
    onClickSetWallpaper: () -> Unit
) {
    val context = LocalContext.current
    Row(verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center,
        modifier = modifier
            .clip(RoundedCornerShape(0.dp))
            .background(
                brush = Brush.linearGradient(
                    colors = listOf(
                        colorResource(R.color.color_primary),
                        colorResource(R.color.color_primary)
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
        androidx.compose.material.Text(
            modifier = Modifier.padding(
                start = 4.dp,
                top = 8.dp,
                bottom = 8.dp,
                end = 16.dp
            ),
            text =  text,
            color = colorResource(R.color.white),
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center
        )
    }

}

@Composable
fun GradientDotsIndicator(
    dotCount: Int,
    pagerState: PagerState,
    modifier: Modifier = Modifier,
    dotSpacing: Dp = 6.dp,
    selectedDotWidth: Dp = 20.dp,
    dotSize: Dp = 8.dp
) {
    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically
    ) {
        repeat(dotCount) { index ->
            val isSelected = pagerState.currentPage == index
            val dotModifier = if (isSelected) {
                Modifier
                    .width(selectedDotWidth)
                    .height(dotSize)
                    .background(
                        brush = Brush.linearGradient(
                            colors = listOf(
                                colorResource(R.color.color_secscond),
                                colorResource(R.color.color_primary)
                            )
                        ),
                        shape = RoundedCornerShape(50)
                    )
            } else {
                Modifier
                    .size(dotSize)
                    .background(
                        color = colorResource(R.color.color_disable_icon),
                        shape = CircleShape
                    )
            }

            Box(
                modifier = dotModifier
                    .padding(horizontal = dotSpacing / 2)
            )
            if (index < dotCount - 1) {
                Spacer(modifier = Modifier.width(dotSpacing))
            }
        }
    }


}

@Composable
fun NotFoundLayout(modifier: Modifier=Modifier){
    Box(
        modifier = modifier
            .fillMaxWidth()
            .wrapContentHeight(),
        contentAlignment = Alignment.Center
    ) {
        Column(
            modifier = Modifier,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Image(
                ImageVector.vectorResource(R.drawable.ic_not_result_search),
                contentDescription = ""
            )
            Spacer(modifier = Modifier.height(10.dp))
            TextGradient(
                previewText = stringResource(R.string.no_result_found),
                brush = Brush.linearGradient(
                    listOf(
                        colorResource(R.color.color_primary),
                        colorResource(R.color.color_secscond)
                    )
                ),
                textSize = 18.sp
            )


        }

    }
}