package com.itsol.vn.wallpaper.live.parallax.utils

import android.app.Activity
import android.content.Context
import android.util.Log
import android.widget.Toast
import com.android.billingclient.api.AcknowledgePurchaseParams
import com.android.billingclient.api.BillingClient
import com.android.billingclient.api.BillingClientStateListener
import com.android.billingclient.api.BillingFlowParams
import com.android.billingclient.api.BillingResult
import com.android.billingclient.api.ConsumeParams
import com.android.billingclient.api.ProductDetails
import com.android.billingclient.api.Purchase
import com.android.billingclient.api.PurchasesUpdatedListener
import com.android.billingclient.api.QueryProductDetailsParams
import com.android.billingclient.api.QueryPurchasesParams
import com.itsol.ironsourcelib.AdmobUtils
import com.itsol.vn.wallpaper.live.parallax.ads.AdsManager
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import okhttp3.internal.trimSubstring
import org.json.JSONObject
import java.time.LocalDateTime

object Purchase {
    var billingClient: BillingClient? = null
    var isBought = false
    var isEnableAds = true

    private var lifeTime = false
    private val purchasesUpdatedListener =
        PurchasesUpdatedListener { _, _ -> }

    fun initIap(context: Context, listenner: InitIapListenner) {
        billingClient = BillingClient.newBuilder(context)
            .setListener(purchasesUpdatedListener)
            .enablePendingPurchases()
            .build()
        billingClient?.startConnection(object : BillingClientStateListener {
            override fun onBillingServiceDisconnected() {

            }

            override fun onBillingSetupFinished(p0: BillingResult) {
                val params = QueryPurchasesParams.newBuilder()
                    .setProductType(BillingClient.ProductType.SUBS)
                    .build()
                billingClient?.queryPurchasesAsync(params) { billingClient, purchases ->
                    if (purchases.isNotEmpty()) {
                        isBought = true
                        isEnableAds = false
                        Common.setBoughtIap(context, true)
                        if (isBought) {
                            for (purchase in purchases) {
                                handlePurchase(purchase)
                            }
                        }
                        listenner.initPurcahseSussces(billingClient, purchases)
                    } else {
                        val params2 = QueryPurchasesParams.newBuilder()
                            .setProductType(BillingClient.ProductType.INAPP)
                            .build()
                        com.itsol.vn.wallpaper.live.parallax.utils.Purchase.billingClient?.queryPurchasesAsync(
                            params2
                        ) { _, purchasesSub ->
                            if (purchasesSub.isNotEmpty()) {
//                                isBought = true
//                                Common.setBoughtIap(context, true)
//                                isEnableAds = false
                            } else {
                                isBought = false
                                Common.setBoughtIap(context, false)
                                isEnableAds = true
                            }
//                            if (isBought) {
//                                for (purchase in purchasesSub) {
//                                    handlePurchase(purchase)
//                                }
//                            }
                            listenner.initPurcahseSussces(billingClient, purchasesSub)
                        }
                    }
                }
            }
        })
    }

    private fun handlePurchase(purchase: Purchase) {
        if (purchase.purchaseState == Purchase.PurchaseState.PURCHASED && !purchase.isAcknowledged) {
            val acknowledgePurchaseParams = AcknowledgePurchaseParams.newBuilder()
                .setPurchaseToken(purchase.purchaseToken)
            billingClient?.acknowledgePurchase(
                acknowledgePurchaseParams.build()
            ) {

            }
        }
    }

    fun reStorePurchase(context: Context, listenner: InitIapListenner) {
        billingClient?.startConnection(object : BillingClientStateListener {
            override fun onBillingServiceDisconnected() {

            }

            override fun onBillingSetupFinished(p0: BillingResult) {
                val params =
                    QueryPurchasesParams.newBuilder()
                        .setProductType(BillingClient.ProductType.SUBS)
                        .build()
                billingClient?.queryPurchasesAsync(params) { billingClient, purchases ->
                    if (purchases.isNotEmpty()) {
                        isBought = true
                        isEnableAds = false
                        Common.setBoughtIap(context, true)
                        if (isBought) {
                            for (purchase in purchases) {
                                handlePurchase(purchase)
                            }
                        }
                        listenner.initPurcahseSussces(billingClient, purchases)
                    } else {
                        val paramsSub =
                            QueryPurchasesParams.newBuilder()
                                .setProductType(BillingClient.ProductType.INAPP)
                                .build()
                        com.itsol.vn.wallpaper.live.parallax.utils.Purchase.billingClient?.queryPurchasesAsync(
                            paramsSub
                        ) { _, purchasesSub ->
                            if (purchasesSub.isNotEmpty()) {
//                                isBought = true
//                                Common.setBoughtIap(context, true)
//                                isEnableAds = false
                                purchasesSub.map {
                                    val jsonObject = JSONObject(it.toString())
                                    Common.setCurrentKeyIap(
                                        context,
                                        jsonObject.getString("productId"),
                                        "success"
                                    )
                                }
                            } else {
                                isBought = false
                                Common.setBoughtIap(context, false)
                                isEnableAds = true
                            }
//                            if (isBought) {
//                                for (purchase in purchasesSub) {
//                                    handlePurchase(purchase)
//                                }
//                            }
                            listenner.initPurcahseSussces(billingClient, purchases)
                        }
                    }
                }
            }
        })
    }

    fun queryDetailPurchase(
        context: Context,
        listKey: List<String>,
        isInapp: Boolean,
        queryIap: QueryPurChaseListenner,
    ) {
        billingClient =
            BillingClient.newBuilder(context).enablePendingPurchases().setListener { p0, p1 ->
                if (p0.responseCode == BillingClient.BillingResponseCode.OK && p1 != null) {
                    for (purchase in p1) {
                        handlePurchase(purchase)
                    }
                    isBought = true
                    Common.setBoughtIap(context, true)
                    isEnableAds = false
                    queryIap.updatePurchase()
                } else {
//                    queryIap.queryFail()
                }
            }.build()
        billingClient?.startConnection(object : BillingClientStateListener {
            override fun onBillingServiceDisconnected() {
                queryIap.queryFail()
            }

            override fun onBillingSetupFinished(p0: BillingResult) {
                if (p0.responseCode == BillingClient.BillingResponseCode.OK) {
                    val productList = ArrayList<QueryProductDetailsParams.Product>()
                    for (key in listKey) {
                        productList.add(
                            QueryProductDetailsParams.Product.newBuilder()
                                .setProductId(key)
                                .setProductType(if (isInapp) BillingClient.ProductType.INAPP else BillingClient.ProductType.SUBS)
                                .build()
                        )
                    }
                    val params = QueryProductDetailsParams.newBuilder()
                    params.setProductList(productList)
                    billingClient?.queryProductDetailsAsync(params.build()) { billingResult1: BillingResult, productDetailsList: List<ProductDetails>? ->
                        if (productDetailsList != null) {
                            queryIap.querySussces(
                                billingResult1,
                                productDetailsList.sortedBy {
                                    it.subscriptionOfferDetails?.get(0)?.pricingPhases?.pricingPhaseList?.get(
                                        0
                                    )?.priceAmountMicros
                                })
                        }else{
                            queryIap.queryFail()
                        }
                    }
                } else {
                    queryIap.queryFail()
                }
            }
        })
    }

    fun subscribePurchase(context: Context, productDetails: ProductDetails) {
        try {
            lifeTime = false
            val offerToken = productDetails.subscriptionOfferDetails?.get(0)?.offerToken
            val productDetailsParamsList =
                listOf(
                    offerToken?.let { it1 ->
                        BillingFlowParams.ProductDetailsParams.newBuilder()
                            .setProductDetails(productDetails).setOfferToken(it1)
                            .build()
                    }
                )
            val billingFlowParams =
                BillingFlowParams.newBuilder()
                    .setProductDetailsParamsList(productDetailsParamsList)
                    .build()
            val billingResult =
                billingClient?.launchBillingFlow(context as Activity, billingFlowParams)
            Log.d("===IAP", "lnMonthly: $offerToken|$billingResult")
        } catch (e: Exception) {
            Toast.makeText(context, "Error", Toast.LENGTH_SHORT).show()
        }
    }

    fun subscribePurchaseInapp(context: Context, productDetails: ProductDetails) {
        try {
            lifeTime = true
            val productDetailsParamsList =
                listOf(
                    BillingFlowParams.ProductDetailsParams.newBuilder()
                        .setProductDetails(productDetails)
                        .build()
                )
            val billingFlowParams =
                BillingFlowParams.newBuilder()
                    .setProductDetailsParamsList(productDetailsParamsList)
                    .build()
            val billingResult =
                billingClient?.launchBillingFlow(context as Activity, billingFlowParams)
            Log.d("===IAP", "lnMonthly: $billingResult")
        } catch (e: Exception) {
            Toast.makeText(context, "Error: ${e.message}", Toast.LENGTH_SHORT).show()
        }
    }

    fun stopConnect() {
        if (billingClient != null) {
            billingClient?.endConnection()
        }
    }


    interface InitIapListenner {
        fun initPurcahseSussces(billingResult: BillingResult, purchases: List<Purchase>)

    }

    interface QueryPurChaseListenner {
        fun updatePurchase()
        fun queryFail()
        fun querySussces(billingResult1: BillingResult, productDetailsList: List<ProductDetails>?)
    }
}

fun fromatPriceIap(p: String): String {
    return try {
        val str1 = p.trimSubstring(0, 1)
        str1.toInt()
        val str = p.trimSubstring(p.length - 1, p.length)
        val price = p.substring(0, p.length - 1)
        price + "" + str
    } catch (e: Exception) {
        val str = p.trimSubstring(0, 1)
        val price = p.substring(1, p.length)
        price + "" + str
    }
}