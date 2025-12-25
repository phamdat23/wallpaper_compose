package com.itsol.vn.wallpaper.live.parallax.ui.activity.iap

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.net.ConnectivityManager
import android.net.Uri
import android.os.Build
import android.provider.Settings
import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
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
import androidx.compose.ui.graphics.Brush
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
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat.startActivity
import com.android.billingclient.api.BillingClient
import com.android.billingclient.api.BillingResult
import com.android.billingclient.api.ProductDetails
import com.google.gson.Gson
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
fun IAPScreen(modifier: Modifier = Modifier, key: String, nameCategory: String) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val navigation = LocalNavController.current
    var textPrice by remember { mutableStateOf("ERROR") }
    var queryFail by remember { mutableStateOf(false) }
    var result by remember { mutableStateOf<ProductDetails?>(null) }
    var isBackStack by remember { mutableStateOf(true) }
    var isConnectInternet by remember { mutableStateOf(true) }
    LaunchedEffect(isConnectInternet) {
        Log.e("AAAAAAAAAAAAA", "IAPScreen: key: $key")
        AppOpenManager.getInstance().disableAppResumeWithActivity(MainActivity::class.java)
        Purchase.queryDetailPurchase(
            context = context,
            listKey = listOf<String>(key),
            isInapp = true,
            object : Purchase.QueryPurChaseListenner {
                override fun updatePurchase() {
                    scope.launch(Dispatchers.Main) {
                        if (isBackStack) {
                            Common.setCurrentKeyIap(context, key, "success")
                            isBackStack = false
                            navigation.popBackStack()
                            AppOpenManager.getInstance()
                                .enableAppResumeWithActivity(MainActivity::class.java)
                            Toast.makeText(context, "Success", Toast.LENGTH_SHORT).show()
                        }

                    }
                }

                override fun queryFail() {

                }

                override fun querySussces(
                    billingResult1: BillingResult,
                    productDetailsList: List<ProductDetails>?
                ) {
                    scope.launch(Dispatchers.Main) {
                        if (billingResult1.responseCode == BillingClient.BillingResponseCode.OK) {
                            if (!productDetailsList.isNullOrEmpty()) {
                                result = productDetailsList[0]
                                result?.oneTimePurchaseOfferDetails?.formattedPrice?.let {
                                    textPrice = fromatPriceIap(it)
                                }
                                Log.e("AAAAAAAAAAA", "querySussces: $result")
                            } else {
                                Log.e(
                                    "AAAAAAAAAAA",
                                    "querySussces: ${Gson().toJson(productDetailsList)}"
                                )
                                queryFail = true
                            }
                        }
                    }


                }
            })
    }
    val internetReceiver = remember {
        object : BroadcastReceiver() {
            override fun onReceive(p0: Context?, p1: Intent?) {
                if (p1?.action == ConnectivityManager.CONNECTIVITY_ACTION) {
                    // check internet ở đây
                    isConnectInternet = AdmobUtils.isNetworkConnected(context)
                }
            }
        }
    }
    AdsManager.ScreenNameLogEffect("IAP_SCREEN")
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
    Box(modifier = modifier.fillMaxSize()) {
        Image(
            painterResource(R.drawable.bg_wallpaper),
            contentDescription = "",
            contentScale = ContentScale.Crop,
            modifier = Modifier.fillMaxSize()
        )
        Spacer(
            modifier = Modifier
                .fillMaxSize()
                .background(color = colorResource(R.color.color_iap))
        )
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .wrapContentHeight()
                .align(Alignment.Center)
                .padding(start = 16.dp, end = 16.dp, bottom = 60.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = stringResource(R.string.unlock_all_wallpapers),
                textAlign = TextAlign.Center,
                fontWeight = FontWeight(700),
                fontFamily = FontFamily(Font(R.font.readexpro_bold)),
                fontSize = 24.sp,
                color = colorResource(R.color.white),
                modifier = Modifier.padding(horizontal = 10.dp)
            )
            Spacer(modifier = Modifier.height(20.dp))
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .wrapContentHeight()
                    .background(
                        brush = Brush.radialGradient(
                            listOf(
                                colorResource(R.color.color_gradient_iap_start),
                                colorResource(R.color.color_gradient_iap_end)
                            )
                        ),
                        shape = RoundedCornerShape(16.dp)
                    )
                    .border(
                        width = 1.dp,
                        color = colorResource(R.color.white),
                        shape = RoundedCornerShape(16.dp)
                    )
                    .padding(horizontal = 18.dp, vertical = 26.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = stringResource(R.string.description_iap),
                    textAlign = TextAlign.Center,
                    fontFamily = FontFamily(Font(R.font.readexpro_light)),
                    fontWeight = FontWeight(300),
                    fontSize = 16.sp,
                    color = colorResource(R.color.white)
                )
                Spacer(modifier = Modifier.height(30.dp))

                Text(
                    text = "Price: ${if (!queryFail) textPrice else "ERROR"} lifetime",
                    textAlign = TextAlign.Center,
                    fontWeight = FontWeight(300),
                    fontFamily = FontFamily(Font(R.font.readexpro_light)),
                    fontSize = 20.sp,
                    color = colorResource(R.color.white)
                )
                Spacer(modifier = Modifier.height(20.dp))
                Button(
                    onClick = {
                        scope.launch(Dispatchers.Main) {
                            result?.let {
                                Purchase.subscribePurchaseInapp(context = context, it)
                            }
                        }
                    }, colors = ButtonDefaults.buttonColors(
                        containerColor = colorResource(R.color.color_primary),
                        contentColor = colorResource(R.color.white)
                    ),
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier.height(54.dp)
                ) {
                    Text(
                        text = stringResource(R.string.unlock_now),
                        textAlign = TextAlign.Center,
                        fontWeight = FontWeight(700),
                        fontFamily = FontFamily(Font(R.font.quicksand_bold)),
                        fontSize = 16.sp,
                        color = colorResource(R.color.white)
                    )
                }
            }
        }

        Row(
            modifier = Modifier
                .align(Alignment.TopCenter)
                .fillMaxWidth()
                .wrapContentHeight()
                .padding(horizontal = 16.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = stringResource(R.string.restore),
                textAlign = TextAlign.Center,
                fontWeight = FontWeight(700),
                fontFamily = FontFamily(Font(R.font.quicksand_bold)),
                fontSize = 16.sp,
                color = colorResource(R.color.white),
                modifier = Modifier.clickable {
                    Purchase.reStorePurchase(context, object : Purchase.InitIapListenner {
                        override fun initPurcahseSussces(
                            billingResult: BillingResult,
                            purchases: List<com.android.billingclient.api.Purchase>
                        ) {
                            scope.launch(Dispatchers.Main) {
                                purchases.map {
                                    if (it.products.contains(key)) {
                                        Toast.makeText(
                                            context,
                                            context.getString(R.string.subscription_has_been_purchased),
                                            Toast.LENGTH_SHORT
                                        ).show()
                                        if (isBackStack) {
                                            Common.setCurrentKeyIap(context, key, "success")
                                            isBackStack = false
                                            navigation.popBackStack()
                                            AppOpenManager.getInstance()
                                                .enableAppResumeWithActivity(MainActivity::class.java)

                                        }
                                        return@launch
                                    }
                                }
                                Toast.makeText(
                                    context,
                                    context.getString(R.string.subscription_has_not_been_purchased),
                                    Toast.LENGTH_SHORT
                                ).show()

                            }
                        }
                    })
                }
            )
            Spacer(modifier = Modifier.weight(1f))
            Row(
                modifier = Modifier
                    .wrapContentSize()
                    .clickable {
                        if (isBackStack) {
                            isBackStack = false
                            navigation.popBackStack()
                            AppOpenManager
                                .getInstance()
                                .enableAppResumeWithActivity(MainActivity::class.java)
                        }
                    }, verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Skip",
                    textAlign = TextAlign.Center,
                    fontFamily = FontFamily(Font(R.font.readexpro_bold)),
                    fontWeight = FontWeight(700),
                    fontSize = 16.sp,
                    color = colorResource(R.color.white)
                )
                Spacer(modifier = Modifier.width(4.dp))
                Image(
                    ImageVector.vectorResource(R.drawable.ion_caret_next),
                    contentDescription = ""
                )
            }
        }

        Column(
            modifier = Modifier
                .wrapContentSize()
                .align(Alignment.BottomCenter)
                .padding(bottom = 50.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = stringResource(R.string.privacy_policy),
                textAlign = TextAlign.Center,
                fontFamily = FontFamily(Font(R.font.readexpro_light)),
                fontWeight = FontWeight(300),
                fontSize = 16.sp,
                color = colorResource(R.color.white),
                modifier = Modifier.clickable {
                    val url =
                        "https://docs.google.com/document/d/1v9omoM_WaWiJiTvU7Aa1E5hfoQi99g7waNBsnbb_ZqQ/edit?tab=t.0"
                    val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
                    context.startActivity(intent)


                }
            )
            Spacer(modifier = Modifier.height(10.dp))
            Text(
                text = stringResource(R.string.term_of_use),
                textAlign = TextAlign.Center,
                fontFamily = FontFamily(Font(R.font.readexpro_light)),
                fontWeight = FontWeight(300),
                fontSize = 16.sp,
                color = colorResource(R.color.white),
                modifier = Modifier
                    .alpha(0f)
                    .clickable {
//                    val url = "https://www.example.com"
//                    val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
//                    context.startActivity(intent)

                    }
            )
        }
        if (!isConnectInternet) {
            DialogInternet(onDismissRequest = {}, onConfirmation = {
                val intent = Intent(Settings.ACTION_WIFI_SETTINGS)
                context.startActivity(intent)
                AppOpenManager.getInstance().disableAppResumeWithActivity(MainActivity::class.java)
            })
        }

    }
}

@Composable
@Preview
private fun Preview() {
    IAPScreen(key = Constants.listKeyIap[0], nameCategory = "")
}