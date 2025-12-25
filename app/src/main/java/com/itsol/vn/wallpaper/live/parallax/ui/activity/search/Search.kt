package com.itsol.vn.wallpaper.live.parallax.ui.activity.search

import android.util.Log
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeContentPadding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.ScrollableTabRow
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.runtime.toMutableStateList
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextIndent
import androidx.compose.ui.text.toLowerCase
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.navigation.compose.currentBackStackEntryAsState
import com.itsol.ironsourcelib.AdmobUtils
import com.itsol.ironsourcelib.AppOpenManager
import com.itsol.vn.wallpaper.live.parallax.LocalNavController
import com.itsol.vn.wallpaper.live.parallax.MainActivity
import com.itsol.vn.wallpaper.live.parallax.R
import com.itsol.vn.wallpaper.live.parallax.ads.AdsManager
import com.itsol.vn.wallpaper.live.parallax.ads.RemoteConfig
import com.itsol.vn.wallpaper.live.parallax.model.HistorySearchModel
import com.itsol.vn.wallpaper.live.parallax.model.WallpaperModel
import com.itsol.vn.wallpaper.live.parallax.ui.activity.components.ItemWallpaper
import com.itsol.vn.wallpaper.live.parallax.ui.activity.components.TextWithStyle
import com.itsol.vn.wallpaper.live.parallax.ui.activity.home.tab.category.CategoryTab
import com.itsol.vn.wallpaper.live.parallax.ui.activity.home.tab.homeTab.HomeTab
import com.itsol.vn.wallpaper.live.parallax.ui.activity.home.tab.myCollection.MyCollection
import com.itsol.vn.wallpaper.live.parallax.ui.dialog.DialogIap
import com.itsol.vn.wallpaper.live.parallax.utils.Common
import com.itsol.vn.wallpaper.live.parallax.utils.Constants
import com.itsol.vn.wallpaper.live.parallax.utils.Router
import com.itsol.vn.wallpaper.live.parallax.viewModel.MainViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.util.Locale


@Immutable
enum class Searching {
    NONE,
    RESULT,
    LOADING,
    NOT_RESULT
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun Search(modifier: Modifier = Modifier) {
    val navigationController = LocalNavController.current
    val viewModel = hiltViewModel<MainViewModel>()
    val scope = rememberCoroutineScope()
    val context = LocalContext.current
    val keyboardController = LocalSoftwareKeyboardController.current
    var valueSearch by rememberSaveable {
        mutableStateOf("")
    }
//    val listWallpaper by viewModel.wallpaperSearch.collectAsState()
    var stateSearch by rememberSaveable {
        mutableStateOf(Searching.NONE)
    }
    var isLoading by remember { mutableStateOf(false) }

    var checkSearch by rememberSaveable {
        mutableStateOf(false)
    }
    val listHistorySearch = viewModel.getAllChooseSearch().toMutableStateList()
    var arraySearch by remember { mutableStateOf(listOf<String>()) }

    LaunchedEffect(true) {
        AdsManager.logEvent(context,Router.SEARCH)
//        viewModel.getAllWallPaper2()
        if (RemoteConfig.native_view_wallpaper == "1") {
            AdsManager.loadNative(context, AdsManager.nativeViewWallpaper)
        }
    }
    AdsManager.ScreenNameLogEffect(Router.SEARCH)
    val backStackEntry = navigationController.currentBackStackEntryAsState()
    DisposableEffect(backStackEntry.value) {
        // Đăng ký lắng nghe sự kiện lifecycle của màn hình
        val lifecycle = backStackEntry.value?.lifecycle
        val observer = LifecycleEventObserver { _, event ->
            when (event) {
                Lifecycle.Event.ON_RESUME -> {
                    if(checkSearch){
                        arraySearch = (searchingWallpaper(valueSearch))
                        viewModel.onSearchTextChange(valueSearch)
                    }


                }

                Lifecycle.Event.ON_START -> {

                }
                Lifecycle.Event.ON_PAUSE -> {

                }

                else -> {

                }
            }
        }

        // Thêm observer vào lifecycle
        lifecycle?.addObserver(observer)

        onDispose {
            // Xóa observer khi không cần thiết nữa
            lifecycle?.removeObserver(observer)
        }
    }

    LaunchedEffect(isLoading) {
        if (!isLoading) {
            if (checkSearch) {
                stateSearch = Searching.RESULT
            } else {
                stateSearch = Searching.NONE
            }
        }else{
            delay(500)
            isLoading= false
        }
    }
    var tabIndex by remember { mutableStateOf(0) }
    val state = rememberPagerState(pageCount = { arraySearch.size })
    Column(
        modifier = modifier
            .fillMaxSize()
            .background(color = colorResource(R.color.color_bg))
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .wrapContentHeight()
                .padding(start = 10.dp, end = 10.dp, top = 20.dp, bottom = 10.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Image(
                ImageVector.vectorResource(R.drawable.ic_arrow_back),
                contentDescription = "",
                modifier = Modifier
                    .size(36.dp)
                    .padding(end = 12.dp, top = 0.dp, bottom = 0.dp)
                    .clickable {
                        navigationController.popBackStack(route = Router.HOME, inclusive = false)
                    },
                colorFilter = ColorFilter.tint(colorResource(R.color.color_bold_900))
            )
            Row(modifier= Modifier
                .weight(1f)
                .wrapContentHeight()
                .background(
                    color = colorResource(R.color.color_neutral_200),
                    shape = RoundedCornerShape(12.dp)
                )
                .padding(vertical = 12.dp, horizontal = 10.dp)
                , horizontalArrangement = Arrangement.Center, verticalAlignment = Alignment.CenterVertically) {
                Image(ImageVector.vectorResource(R.drawable.ic_search), contentDescription = "")
                Spacer(modifier=Modifier.width(4.dp))
                BasicTextField(
                    value = valueSearch,
                    onValueChange = {
                        valueSearch = it
                    },
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(
                        imeAction = ImeAction.Search,
                        keyboardType = KeyboardType.Text,
                    ),
                    keyboardActions = KeyboardActions(
                        onSearch = {
//                        isLoading = true
                            keyboardController?.hide()
                            arraySearch = (searchingWallpaper(valueSearch))
                            stateSearch = Searching.RESULT
                            tabIndex =0
//                            viewModel.onSearchTextChange(valueSearch)
                            checkSearch = true
                            isLoading= true
                            AdsManager.logEvent(context,"${Router.SEARCH}_click_searching")
                            scope.launch {
                                state.scrollToPage(0)
                            }
//                        scope.launch {
//                            delay(1000)
//                            isLoading = false
//                        }
                            // search wallpaper
                        }
                    ),
                    textStyle = TextStyle(
                        fontSize = 14.sp,
                        color = colorResource(R.color.color_bold_900),
                        fontFamily = FontFamily(Font(R.font.readexpro_regular)),
                        fontWeight = FontWeight(400),
                        textAlign = TextAlign.Start,

                        ),
                    cursorBrush = SolidColor(colorResource(R.color.color_bold_900)),
                    decorationBox = {innerTextField ->
                        if(valueSearch.isEmpty()){
                            Text(
                                text = stringResource(R.string.search_for),
                                textAlign = TextAlign.Start,
                                fontWeight = FontWeight(400),
                                fontFamily = FontFamily(
                                    Font(R.font.readexpro_regular)
                                ),
                                color = colorResource(R.color.color_disable),
                                fontSize = 14.sp
                            )
                        }else{
                            innerTextField()
                        }


                    },
                    modifier = Modifier
                        .wrapContentHeight()
                        .weight(1f)
                        .padding(horizontal = 6.dp)



                )
                if (valueSearch.trim() != "") {
                    Image(ImageVector.vectorResource(R.drawable.ic_cancel), colorFilter = ColorFilter.tint(colorResource(R.color.color_bold_900)), contentDescription = "", modifier=Modifier.clickable {
                        valueSearch = ""
                        listHistorySearch.map { historySearchModel ->
                            historySearchModel.copy(isSelected = false)
                        }
                        stateSearch = Searching.NONE
                        checkSearch = false
                    })
                }

            }

            if (stateSearch == Searching.NONE) {
                TextWithStyle(
                    stringResource(R.string.search),
                    Brush.linearGradient(
                        listOf(
                            colorResource(R.color.color_gradient_search_start),
                            colorResource(R.color.color_gradient_search_end)
                        )
                    ), modifier = Modifier
                        .padding(0.dp)
                        .clickable {
                            if (valueSearch.isNotEmpty()) {
                                arraySearch = (searchingWallpaper(valueSearch))
                                stateSearch = Searching.RESULT
                                tabIndex = 0
//                            viewModel.onSearchTextChange(valueSearch)
                                checkSearch = true
                                keyboardController?.hide()
                                AdsManager.logEvent(context, "${Router.SEARCH}_click_searching")
                                isLoading= true
                                scope.launch {
                                    state.scrollToPage(0)
                                }

                            }
                        }
                )
            }
        }

        if (stateSearch == Searching.NONE) {
//            Spacer(modifier = Modifier.height(20.dp))
            Spacer(modifier = Modifier.height(14.dp))
            FlowRow(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .padding(horizontal = 16.dp),

                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                listHistorySearch.map {historySearchModel ->
                    TextChip(historySearchModel) { it1, it2->
                        if (it1) {
                            valueSearch += "${it2}, "
                        } else {
                            valueSearch = valueSearch.replace("${it2}, ", "")
                        }
                    }


                }


            }
        } else if (stateSearch == Searching.RESULT) {
//            Spacer(modifier = Modifier.height(10.dp))
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color.Transparent)
            ) {
                ScrollableTabRow(
                    modifier = Modifier.padding(start = 10.dp),
                    edgePadding = 0.dp,
                    selectedTabIndex = tabIndex,
                    containerColor = Color.Transparent,
                    indicator = {
                        if(it.isNotEmpty()){
                            TabRowDefaults.Indicator(
                                modifier =  Modifier.tabIndicatorOffset(it[tabIndex]),
                                color = colorResource(R.color.color_track_switch), // Đổi màu ở đây
                            )
                        }
                    }
                ) {
                    arraySearch.forEachIndexed { index, title ->
                        Tab(
                            text = {
                                Text(
                                    Common.capitalizeFirstLetter(title).replace(",","")
                                        .toString(),
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight(500),
                                    fontFamily = FontFamily(Font(R.font.readexpro_medium)),
                                    textAlign = TextAlign.Center
                                )
                            },
                            selectedContentColor = colorResource(R.color.color_track_switch),
                            unselectedContentColor = colorResource(R.color.color_disable_icon),

                            selected = tabIndex == index,
                            onClick = {
                                scope.launch {
                                    tabIndex = index
                                    state.scrollToPage(tabIndex)
                                    Log.e("AAAAAAAAA", "Search:$tabIndex ", )
                                }
                            },
                        )
                    }
                }
                Spacer(modifier = Modifier.height(6.dp))
                HorizontalPager(
                    state = state,
                    modifier = Modifier.weight(1f),
                    userScrollEnabled = false,
                    reverseLayout = true,

                ) {
                    tabIndex = it
                    val wallPaper by remember { mutableStateOf(viewModel.getWallpaperByCate(arraySearch[tabIndex].replace(",",""))) }
                    PageResult(arraySearch[tabIndex],wallPaper)
                }
            }
        } else {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                contentAlignment = Alignment.Center
            ) {
                Column(modifier = Modifier.align(Alignment.Center), horizontalAlignment = Alignment.CenterHorizontally) {
                    Image(
                        ImageVector.vectorResource(R.drawable.ic_not_result_search),
                        contentDescription = ""
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                    TextWithStyle2(
                        stringResource(R.string.no_result_found), Brush.linearGradient(
                            listOf(
                                colorResource(R.color.color_primary),
                                colorResource(R.color.color_secscond)
                            )
                        )
                    )
                    Text(
                        textAlign = TextAlign.Center,
                        text = stringResource(R.string.we_cant_find_any_item_matching_your_search),
                        fontWeight = FontWeight(300),
                        fontSize = 14.sp,
                        fontFamily = FontFamily(Font(R.font.readexpro_light)),
                        color = colorResource(R.color.color_bold_900),
                        modifier = Modifier.align(Alignment.CenterHorizontally)
                    )

                }

            }
        }
        if (isLoading && checkSearch) {
            Dialog(onDismissRequest = { /* Prevent dismiss by clicking outside */ }) {
                Box(
                    modifier = Modifier
                        .size(80.dp)
                        .background(Color.White, shape = RoundedCornerShape(40.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(color = Color.Black)
                }
            }
        }

    }
}

private fun searchingWallpaper(query: String): List<String> {
    return query.trim().split(", ")
}

@Composable
private fun PageResult(category: String, wallPaper: List<WallpaperModel>) {
    val context= LocalContext.current
    val stateList = rememberLazyGridState()
    val viewModel = hiltViewModel<MainViewModel>()
    val navigationController = LocalNavController.current
    var isBought by remember { mutableStateOf(Common.getBoughtIap(context)) }
    var isShowDialogIap by remember { mutableStateOf(false) }

    Column(modifier = Modifier.fillMaxSize()) {
        if (wallPaper.isNotEmpty()) {
            LazyVerticalGrid(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .padding(horizontal = 8.dp),
                columns = GridCells.Fixed(2),
                state = stateList,
                verticalArrangement = Arrangement.spacedBy(8.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                contentPadding = PaddingValues(bottom = 10.dp)
            ) {
                items(wallPaper) {
                    val nameCat by rememberSaveable { mutableStateOf(it.categories) }
                    val cate by rememberSaveable{ mutableStateOf(if (nameCat == "AI_Wallpaper") nameCat.replace(
                        "_",
                        " "
                    ) else nameCat) }
                    ItemWallpaper(
                        titleAttr = "",
                        modifier = Modifier.height(320.dp),
                        wallpaperModel = it,
                        isDownLoad = false,
                        isBought = (Common.checkCate(it.categories))&&Common.getCurrentKeyIap(context, cate)=="",
                        onClickItem = {
                            val cate = if (it.categories == "AI_Wallpaper") it.categories.replace(
                                "_",
                                " "
                            ) else it.categories
                            if(Common.checkCate(it.categories)){
                                if(Common.getCurrentKeyIap(context, cate)!=""){
                                    Common.isTypeSetWallpaper = Constants.IMAGE4K
                                    val json = Common.wallpaperToJson(it)
                                    navigationController.navigate("${Router.SET_WALLPAPER}?wallpaperModel=${json}") {
                                        launchSingleTop = true
                                        restoreState = true
                                    }
                                }else{
                                    var key ="0.5"
                                    for (i in 0 until Common.listCatName.size) {
                                        if (it.categories == "AI_Wallpaper") {
                                            if (Common.listCatName[i].equals(
                                                    it.categories.replace(
                                                        "_",
                                                        " "
                                                    ), true
                                                )
                                            ) {
                                                key = Constants.listKeyIap[i]
                                            }
                                        } else {
                                            if (Common.listCatName[i].equals(
                                                    it.categories,
                                                    true
                                                )
                                            ) {
                                                key = Constants.listKeyIap[i]
                                            }
                                        }
                                    }
                                    navigationController.navigate(
                                        Router.getIapScreen(
                                            key,
                                            if (it.categories == "AI_Wallpaper") it.categories.replace(
                                                "_",
                                                " "
                                            ) else it.categories
                                        )
                                    )
                                }
                            }else{
                                Common.isTypeSetWallpaper = Constants.IMAGE4K
                                val json = Common.wallpaperToJson(it)
                                navigationController.navigate("${Router.SET_WALLPAPER}?wallpaperModel=${json}") {
                                    launchSingleTop = true
                                    restoreState = true
                                }
                            }

                        },
                        onClickFavorite = {
                            viewModel.updateFavoriteWallPaper(it.id, it.favorite)
                        })
                }
            }
        } else {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                contentAlignment = Alignment.Center
            ) {
                Column(modifier = Modifier.align(Alignment.Center), horizontalAlignment = Alignment.CenterHorizontally) {
                    Image(
                        ImageVector.vectorResource(R.drawable.ic_not_result_search),
                        contentDescription = ""
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                    TextWithStyle2(
                        stringResource(R.string.no_result_found), Brush.linearGradient(
                            listOf(
                                colorResource(R.color.color_primary),
                                colorResource(R.color.color_secscond)
                            )
                        )
                    )
                    Text(
                        textAlign = TextAlign.Center,
                        text = stringResource(R.string.we_cant_find_any_item_matching_your_search),
                        fontWeight = FontWeight(300),
                        fontSize = 14.sp,
                        fontFamily = FontFamily(Font(R.font.readexpro_light)),
                        color = colorResource(R.color.color_bold_900),
                        modifier = Modifier.align(Alignment.CenterHorizontally)
                    )

                }

            }
        }

    }
//    if(isShowDialogIap){
//        DialogIap(onDismiss = {
//            isShowDialogIap= false
//        }, onSubscribeSuccess = {
//            isShowDialogIap= false
//            isBought = true
//        })
//    }
}

@Composable
private fun TextChip(
    historySearchModel: HistorySearchModel,
    onCLickItem: (Boolean, String) -> Unit
) {
    val history by remember { mutableStateOf(historySearchModel) }
    var isSelected by remember { mutableStateOf(historySearchModel.isSelected)}
    if (isSelected) {
        Text(
            text = Common.capitalizeFirstLetter(history.query),
            textAlign = TextAlign.Center,
            fontWeight = FontWeight(500),
            fontSize = 18.sp,
            fontFamily = FontFamily(
                Font(R.font.readexpro_medium)
            ),
            color = colorResource(R.color.color_bold_900),
            modifier = Modifier
                .clip(shape = RoundedCornerShape(16.dp))
                .background(
                    Brush.linearGradient(
                        listOf(
                            colorResource(R.color.color_primary_10),
                            colorResource(R.color.color_secscond_10)
                        )
                    )
                )
                .border(
                    BorderStroke(
                        1.dp, Brush.linearGradient(
                            listOf(
                                colorResource(R.color.color_primary),
                                colorResource(R.color.color_secscond)
                            )
                        )
                    ), shape = RoundedCornerShape(16.dp)
                )
                .padding(horizontal = 12.dp, vertical = 4.dp)
                .clickable {
                    isSelected = !isSelected
                    onCLickItem.invoke(isSelected, history.query)
                }
        )
    } else {
        Text(
            text = Common.capitalizeFirstLetter(history.query),
            textAlign = TextAlign.Center,
            fontWeight = FontWeight(300),
            fontSize = 18.sp,
            fontFamily = FontFamily(
                Font(R.font.readexpro_light)
            ),
            color = colorResource(R.color.color_bold_900),
            modifier = Modifier
                .clip(shape = RoundedCornerShape(16.dp))
                .background(
                    color = colorResource(R.color.color_neutral_300)
                )
                .padding(horizontal = 12.dp, vertical = 4.dp)
                .clickable {
//                    history.isSelected = !history.isSelected
                    isSelected = !isSelected
                    onCLickItem.invoke(isSelected, history.query)

                }

        )
    }

}

@Composable
private fun TextWithStyle2(previewText: String, brush: Brush, modifier: Modifier = Modifier) {
    val textStyle = TextStyle(
        textAlign = TextAlign.Center,
        lineHeight = 32.sp,
        fontSize = 24.sp,
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

@Preview(showBackground = true)
@Composable
private fun Preview() {
    Search()

}