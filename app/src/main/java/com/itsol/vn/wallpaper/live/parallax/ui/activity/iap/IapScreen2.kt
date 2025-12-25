package com.itsol.vn.wallpaper.live.parallax.ui.activity.iap

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.net.ConnectivityManager
import android.net.Uri
import android.os.Build
import android.provider.Settings
import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.RadioButton
import androidx.compose.material3.RadioButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.android.billingclient.api.BillingResult
import com.android.billingclient.api.ProductDetails
import com.itsol.ironsourcelib.AdmobUtils
import com.itsol.ironsourcelib.AppOpenManager
import com.itsol.vn.wallpaper.live.parallax.LocalNavController
import com.itsol.vn.wallpaper.live.parallax.MainActivity
import com.itsol.vn.wallpaper.live.parallax.R
import com.itsol.vn.wallpaper.live.parallax.ads.AdsManager
import com.itsol.vn.wallpaper.live.parallax.ui.dialog.DialogInternet
import com.itsol.vn.wallpaper.live.parallax.utils.Common
import com.itsol.vn.wallpaper.live.parallax.utils.Constants
import com.itsol.vn.wallpaper.live.parallax.utils.Purchase
import com.itsol.vn.wallpaper.live.parallax.utils.Router
import com.itsol.vn.wallpaper.live.parallax.utils.fromatPriceIap
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Composable
fun IapScreen2(modifier: Modifier = Modifier) {
    val context = LocalContext.current
    val navigation = LocalNavController.current
    var isBackStack by remember { mutableStateOf(true) }
    val scope = rememberCoroutineScope()
    val listKey by remember {
        mutableStateOf(
            Constants.listKeyIapSub
        )
    }
    var listProduct by remember { mutableStateOf<List<ProductDetails>>(listOf()) }
    var currentProductDetails by remember { mutableStateOf<ProductDetails?>(null) }
    var isInternetConnected by remember { mutableStateOf(AdmobUtils.isNetworkConnected(context)) }
    var isError by remember { mutableStateOf(true) }
    val internetReceiver = remember {
        object : BroadcastReceiver() {
            override fun onReceive(p0: Context?, p1: Intent?) {
                if (p1?.action == ConnectivityManager.CONNECTIVITY_ACTION) {
                    // check internet ở đây
                    isInternetConnected = AdmobUtils.isNetworkConnected(context)
                }
            }
        }
    }
    DisposableEffect(context) {
        val intentFilter = IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            context.registerReceiver(internetReceiver, intentFilter, Context.RECEIVER_EXPORTED)
        } else {
            context.registerReceiver(internetReceiver, intentFilter)
        }

        onDispose {
            context.unregisterReceiver(internetReceiver)
        }
    }
    LaunchedEffect(isInternetConnected) {
        if (isInternetConnected) {
            Purchase.queryDetailPurchase(
                context,
                listKey = listKey,
                isInapp = false,
                object : Purchase.QueryPurChaseListenner {
                    override fun queryFail() {
                        scope.launch(Dispatchers.Main) {
                            isError = true
                        }
                    }

                    override fun querySussces(
                        billingResult1: BillingResult,
                        productDetailsList: List<ProductDetails>?
                    ) {
                        scope.launch(Dispatchers.Main) {
                            if (productDetailsList != null) {
                                listProduct = productDetailsList
                            }
                            if (listProduct.isNotEmpty()) {
                                currentProductDetails = listProduct[0]
                                isError = false
                            }
                        }
                    }

                    override fun updatePurchase() {
                        scope.launch(Dispatchers.Main) {
                            if (Purchase.isBought) {
                                Toast.makeText(context, "Success", Toast.LENGTH_SHORT).show()
                                // resset ve home
                                if (isBackStack) {
                                    isBackStack = false
                                    navigation.navigate(Router.HOME) {
                                        popUpTo(0)
                                    }
                                }
                            } else {
                                Toast.makeText(context, "Fail", Toast.LENGTH_SHORT).show()
                            }
                        }
                    }
                })
        }
    }
    Box(modifier = modifier.fillMaxSize()) {
        Image(
            painterResource(R.drawable.bg_wallpaper),
            contentDescription = "",
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.FillBounds
        )
        Column(
            modifier = modifier
                .fillMaxSize()

        ) {
            Row(
                Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 10.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Image(
                    ImageVector.vectorResource(R.drawable.ic_arrow_back),
                    contentDescription = "",
                    colorFilter = ColorFilter.tint(colorResource(R.color.white)),
                    modifier = Modifier
                        .size(36.dp)
                        .clickable {
                            if (isBackStack) {
                                isBackStack = false
                                navigation.popBackStack()
                            }
                        }
                )
                Spacer(modifier = Modifier.weight(1f))
                Text(
                    stringResource(R.string.restore),
                    textAlign = TextAlign.Center,
                    fontSize = 16.sp,
                    color = colorResource(R.color.white),
                    modifier = Modifier
                        .padding(top = 6.dp, start = 10.dp, end = 10.dp)
                        .wrapContentSize()
                        .clickable {
                            Purchase.reStorePurchase(context, object : Purchase.InitIapListenner {
                                override fun initPurcahseSussces(
                                    billingResult: BillingResult,
                                    purchases: List<com.android.billingclient.api.Purchase>
                                ) {
                                    scope.launch(Dispatchers.Main) {
                                        if (Purchase.isBought) {
                                            if (isBackStack) {
                                                isBackStack = false
                                                navigation.popBackStack()
                                                AppOpenManager
                                                    .getInstance()
                                                    .enableAppResumeWithActivity(MainActivity::class.java)

                                            }
                                            Toast
                                                .makeText(
                                                    context,
                                                    context.getString(R.string.subscription_has_been_purchased),
                                                    Toast.LENGTH_SHORT
                                                )
                                                .show()
                                        } else {
                                            Toast
                                                .makeText(
                                                    context,
                                                    context.getString(R.string.subscription_has_not_been_purchased),
                                                    Toast.LENGTH_SHORT
                                                )
                                                .show()
                                        }


                                    }
                                }
                            })
                        }
                )
            }
            Text(
                "Upgrade to Premium – Your Way",
                textAlign = TextAlign.Center,
                fontSize = 20.sp,
                color = colorResource(R.color.white),
                modifier = Modifier
                    .padding(top = 6.dp, start = 10.dp, end = 10.dp)
                    .fillMaxWidth()
            )
            Text(
                "Pick Your Perfect Plan",
                textAlign = TextAlign.Center,
                fontSize = 16.sp,
                color = colorResource(R.color.white),
                modifier = Modifier
                    .padding(top = 6.dp, start = 10.dp, end = 10.dp)
                    .fillMaxWidth()
            )
            Text(
                "Get unlimited access to all wallpapers",
                textAlign = TextAlign.Center,
                fontSize = 16.sp,
                color = colorResource(R.color.white),
                modifier = Modifier
                    .padding(top = 6.dp, start = 10.dp, end = 10.dp)
                    .fillMaxWidth()
            )



            Column(
                Modifier
                    .padding(vertical = 12.dp, horizontal = 16.dp)
                    .fillMaxWidth()
                    .weight(1f)
                    .background(
                        color = colorResource(R.color.white),
                        shape = RoundedCornerShape(16.dp)
                    )
                    .padding(16.dp)
            ) {
                LazyColumn(
                    modifier = Modifier
                        .padding(vertical = 16.dp)
                        .fillMaxWidth()
                        .weight(1f),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    if (isError) {
                        items(count = 5) {
                            ItemIap(
                                isSelected = false,
                                isError = isError,
                                data = null,
                                idIap = listKey[it],
                                onClickItem = {

                                })
                        }
                    } else {
                        itemsIndexed(listProduct) { index, data ->
                            ItemIap(
                                isSelected =
                                if (currentProductDetails != null)
                                    if (currentProductDetails?.productId == data.productId)
                                        true
                                    else false
                                else false,
                                data = data, idIap = data.productId, isError = isError,
                            ) {
                                currentProductDetails = it
                            }
                        }
                    }
                }
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(54.dp)
                        .background(
                            brush = Brush.linearGradient(
                                listOf(
                                    colorResource(R.color.color_primary),
                                    colorResource(R.color.color_primary),
                                    colorResource(R.color.color_primary)
                                )
                            ), shape = RoundedCornerShape(16.dp)
                        )
                        .shadow(
                            10.dp,
                            spotColor = colorResource(R.color.color_bg_language),
                            ambientColor = colorResource(R.color.color_gradient_primary),
                            shape = RoundedCornerShape(99.dp)
                        )
                        .clickable {
                            currentProductDetails?.let {
                                Purchase.subscribePurchase(context, it)
                            }
                        },
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    Text(
                        text = "START PLAN",
                        textAlign = TextAlign.Center,
                        color = colorResource(R.color.white),
                        fontSize = 16.sp,
                    )
                }
                Text(
                    "Auto-renews at the end of each billing cycle",
                    textAlign = TextAlign.Center,
                    fontSize = 14.sp,
                    fontWeight = FontWeight(600),
                    color = colorResource(R.color.color_primary),
                    modifier = Modifier
                        .padding(top = 6.dp, start = 10.dp, end = 10.dp)
                        .fillMaxWidth()
                )
                Text(
                    "Cancel anytime",
                    textAlign = TextAlign.Center,
                    fontSize = 14.sp,
                    fontWeight = FontWeight(600),
                    color = colorResource(R.color.color_primary),
                    modifier = Modifier
                        .padding(top = 6.dp, start = 10.dp, end = 10.dp)
                        .fillMaxWidth()
                )
                Text(
                    "Manage subscription",
                    textAlign = TextAlign.Center,
                    fontSize = 14.sp,
                    textDecoration = TextDecoration.Underline,
                    fontWeight = FontWeight(600),
                    color = colorResource(R.color.color_primary),
                    modifier = Modifier
                        .padding(top = 12.dp, start = 10.dp, end = 10.dp, bottom = 12.dp)
                        .align(Alignment.CenterHorizontally)
                        .clickable {
                            val intent = Intent(Intent.ACTION_VIEW).apply {
                                data =
                                    Uri.parse("https://play.google.com/store/account/subscriptions")
                                setPackage("com.android.vending")
                            }
                            context.startActivity(intent)
                        }
                )
            }
            Text(
                "Privacy Policy",
                textAlign = TextAlign.Center,
                fontSize = 14.sp,
                textDecoration = TextDecoration.Underline,
                color = colorResource(R.color.white),
                modifier = Modifier
                    .padding(top = 12.dp, start = 10.dp, end = 10.dp, bottom = 12.dp)
                    .align(Alignment.CenterHorizontally)
                    .clickable {
                        val url =
                            "https://docs.google.com/document/d/1v9omoM_WaWiJiTvU7Aa1E5hfoQi99g7waNBsnbb_ZqQ/edit?tab=t.0"
                        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
                        context.startActivity(intent)
                    }
            )


        }

    }


    if (!isInternetConnected) {
        DialogInternet(onDismissRequest = {}, onConfirmation = {
            val intent = Intent(Settings.ACTION_WIFI_SETTINGS)
            context.startActivity(intent)
        })
    }

}

@Composable
private fun ItemIap(
    modifier: Modifier = Modifier,
    isSelected: Boolean,
    data: ProductDetails?,
    idIap: String,
    isError: Boolean,
    onClickItem: (ProductDetails) -> Unit
) {
    var select by remember { mutableStateOf(false) }
    var textPrice by remember { mutableStateOf("") }
    var productDetails by remember { mutableStateOf<ProductDetails?>(data) }
    LaunchedEffect(isSelected) {
        select = isSelected
    }
    LaunchedEffect(data) {
        productDetails = data
        var price = ""
        if (!isError) {
            price = fromatPriceIap(
                productDetails?.subscriptionOfferDetails?.get(0)?.pricingPhases?.pricingPhaseList?.get(
                    0
                )?.formattedPrice.toString()
            )
        } else {
            price = "Error"
        }
        if (idIap.endsWith("5", ignoreCase = true)) {
            textPrice = "$price - 1 month"
        } else if (idIap.endsWith("10", ignoreCase = true)) {
            textPrice = "$price - 2 month"
        } else if (idIap.endsWith("20", ignoreCase = true)) {
            textPrice = "$price - 3 month"
        } else if (idIap.endsWith("50", ignoreCase = true)) {
            textPrice = "$price - 8 month"
        } else {
            textPrice = "$price - 1 year"
        }
    }
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .wrapContentHeight()
    ) {
        if (idIap.endsWith("5", ignoreCase = true) || idIap.endsWith("100", ignoreCase = true)) {
            Text(
                text = if (idIap.endsWith("5", ignoreCase = true)) "Monthly" else "Yearly",
                fontWeight = FontWeight(600),
                fontSize = 18.sp,
                color = colorResource(R.color.black),
                modifier = Modifier.padding(start = 10.dp, bottom = 14.dp)
            )
        }
        Row(
            modifier = modifier
                .wrapContentHeight()
                .fillMaxWidth()
                .background(
                    color = colorResource(if (select) R.color.color_primary else R.color.color_neutral_500),
                    shape = RoundedCornerShape(99.dp)
                )
                .shadow(
                    10.dp,
                    spotColor = colorResource(R.color.color_bg_language),
                    ambientColor = colorResource(R.color.color_bg_language),
                    shape = RoundedCornerShape(99.dp)
                )
                .clickable {
                    if (!isError) {
                        productDetails?.let {
                            onClickItem.invoke(it)
                        }
                    }


                },
            verticalAlignment = Alignment.CenterVertically
        ) {
            RadioButton(
                selected = select, onClick = {
                    if (!isError) {
                        productDetails?.let {
                            onClickItem.invoke(it)
                        }
                    }
                }, colors = RadioButtonDefaults.colors(
                    unselectedColor = colorResource(R.color.color_neutral_800),
                    selectedColor = colorResource(R.color.white),
                )
            )
            Text(
                text = textPrice,
                textAlign = TextAlign.Center,
                color = if (select) colorResource(R.color.white) else colorResource(R.color.black),
                fontSize = 18.sp,
                fontWeight = FontWeight(600),
                modifier = Modifier.weight(1f)
            )
        }
    }

}

@Composable
@Preview
private fun Preview() {
    IAPScreen(key = Constants.listKeyIap[0], nameCategory = "")
}