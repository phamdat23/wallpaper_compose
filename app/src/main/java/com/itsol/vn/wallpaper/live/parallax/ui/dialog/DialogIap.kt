package com.itsol.vn.wallpaper.live.parallax.ui.dialog

import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.android.billingclient.api.BillingClient
import com.android.billingclient.api.BillingResult
import com.android.billingclient.api.ProductDetails
import com.google.gson.Gson
import com.itsol.vn.wallpaper.live.parallax.R
import com.itsol.vn.wallpaper.live.parallax.utils.Purchase
import com.itsol.vn.wallpaper.live.parallax.utils.fromatPriceIap
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Composable
fun DialogIap(
    modifier: Modifier = Modifier,
    key: String,
    onDismiss: () -> Unit,
    onSubscribeSuccess: () -> Unit
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    var queryFail by remember { mutableStateOf(false) }
    var result by remember { mutableStateOf<ProductDetails?>(null) }
    var textBuy by remember { mutableStateOf("18.000 VND") }
    var keyIap by remember { mutableStateOf("") }

    LaunchedEffect(key) {
        keyIap = key
        Purchase.queryDetailPurchase(
            context = context,
            listKey = listOf<String>(keyIap),
            isInapp = true,
            object : Purchase.QueryPurChaseListenner {
                override fun updatePurchase() {
                    onSubscribeSuccess.invoke()
                }

                override fun queryFail() {

                }

                override fun querySussces(
                    billingResult1: BillingResult,
                    productDetailsList: List<ProductDetails>?
                ) {
                    if (billingResult1.responseCode == BillingClient.BillingResponseCode.OK) {
                        if (!productDetailsList.isNullOrEmpty()) {
                            result = productDetailsList[0]
                            productDetailsList[0].oneTimePurchaseOfferDetails?.formattedPrice?.let {
                                textBuy = fromatPriceIap(it)
                            }
                            Log.e("AAAAAAAAAAA", "querySussces: $result")
                        } else {
                            Log.e("AAAAAAAAAAA", "querySussces: ${Gson().toJson(productDetailsList)}")
                            queryFail = true
                        }
                    }


                }
            })
    }
    Dialog(
        onDismissRequest = onDismiss, properties = DialogProperties(
            dismissOnClickOutside = false,
            dismissOnBackPress = true,
            decorFitsSystemWindows = true,
            usePlatformDefaultWidth = true
        )
    ) {
        Column(
            modifier = modifier
                .wrapContentSize()
                .background(color = colorResource(R.color.white), shape = RoundedCornerShape(8.dp))
                .padding(12.dp)
        ) {
            Image(
                ImageVector.vectorResource(R.drawable.ic_close),
                contentDescription = "",
                modifier = Modifier
                    .size(36.dp)
                    .padding(8.dp)
                    .align(Alignment.End)
                    .clickable {
                        onDismiss.invoke()
                    }
            )
            Text(
                textAlign = TextAlign.Start,
                text = if (!queryFail) textBuy else "Error",
                fontSize = 22.sp,
                color = colorResource(R.color.color_bold_900),
                fontWeight = FontWeight(700),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 12.dp, vertical = 16.dp)

            )
            Button(
                onClick = {
                    scope.launch(Dispatchers.Main) {
                        result?.let {
                            Purchase.subscribePurchaseInapp(context = context, it)
                        }
                    }
                },
                shape = RoundedCornerShape(8.dp), colors = ButtonDefaults.buttonColors(
                    containerColor = colorResource(R.color.color_primary),
                    contentColor = colorResource(R.color.white)
                ),
                modifier = Modifier.align(Alignment.CenterHorizontally)
            ) {
                Text(
                    textAlign = TextAlign.Center,
                    text = if (queryFail) "ERROR" else "Buy Now",
                    fontSize = 16.sp,
                    color = colorResource(R.color.white)
                )
            }
            Spacer(modifier = Modifier.height(16.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Term of use",
                    textAlign = TextAlign.Center,
                    fontWeight = FontWeight(400),
                    fontSize = 14.sp,
                    color = colorResource(R.color.color_bg_bottom_bar),
                    textDecoration = TextDecoration.Underline,
                    modifier = Modifier.clickable {

                    }
                )
                Spacer(modifier = Modifier.weight(1f))
                Text(
                    text = "Privacy policy",
                    textAlign = TextAlign.Center,
                    fontWeight = FontWeight(400),
                    fontSize = 14.sp,
                    color = colorResource(R.color.color_bg_bottom_bar),
                    textDecoration = TextDecoration.Underline,
                    modifier = Modifier.clickable {

                    }
                )
            }


        }
    }


}