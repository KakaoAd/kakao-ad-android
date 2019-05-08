package com.kakao.ad.tracker.sample.util

import android.app.Activity
import com.android.billingclient.api.BillingClient
import com.android.billingclient.api.BillingClient.BillingResponse
import com.android.billingclient.api.BillingClient.SkuType
import com.android.billingclient.api.BillingClientStateListener
import com.android.billingclient.api.BillingFlowParams
import com.android.billingclient.api.Purchase
import com.android.billingclient.api.SkuDetails
import com.android.billingclient.api.SkuDetailsParams

fun BillingClient.connect(onReady: () -> Unit) {
    if (isReady) {
        onReady()
        return
    }

    startConnection(object : BillingClientStateListener {
        override fun onBillingSetupFinished(@BillingResponse responseCode: Int) {
            logv("Billing service setup finished :: responseCode = $responseCode")
            if (responseCode == BillingResponse.OK) {
                onReady()
            }
        }

        override fun onBillingServiceDisconnected() {
            logv("Billing service disconnected")
        }
    })
}

fun BillingClient.queryPurchases(@SkuType skuType: String, onSuccess: (List<Purchase>) -> Unit) {
    connect {
        val result = queryPurchases(skuType)
        logv("Purchases query finished :: skuType = $skuType, responseCode = ${result.responseCode}, list = ${result.purchasesList}")
        if (result.responseCode == BillingResponse.OK) {
            onSuccess(result.purchasesList ?: return@connect)
        }
    }
}

fun BillingClient.launchBillingFlow(
    activity: Activity,
    @SkuType skuType: String,
    skuId: String,
    onSuccess: (String) -> Unit
) {
    connect {
        querySkuDetails(skuType, skuId) { skuDetail ->
            val responseCode = launchBillingFlow(
                activity,
                BillingFlowParams.newBuilder()
                    .setSkuDetails(skuDetail)
                    .build()
            )

            logv("Billing flow launching finished :: skuType = $skuType, skuId= $skuId, responseCode = $responseCode")
            if (responseCode == BillingResponse.OK) {
                onSuccess(skuId)
            }
        }
    }
}

fun BillingClient.querySkuDetails(@SkuType skuType: String, skuId: String, onSuccess: (SkuDetails) -> Unit) {
    connect {
        querySkuDetailsAsync(
            SkuDetailsParams.newBuilder()
                .setType(skuType)
                .setSkusList(listOf(skuId))
                .build()
        ) { responseCode, skuDetailsList ->
            logv("SkuDetails query finished :: responseCode = $responseCode, list = $skuDetailsList")
            if (responseCode == BillingResponse.OK) {
                onSuccess(skuDetailsList?.firstOrNull() ?: return@querySkuDetailsAsync)
            }
        }
    }
}

fun BillingClient.consumePurchase(@SkuType skuType: String, skuId: String, onSuccess: (String) -> Unit) {
    queryPurchases(skuType) { purchases ->
        val purchase = purchases.find { it.sku == skuId }
        if (purchase == null) {
            loge("No purchase :: skuType = $skuType, skuId = $skuId")
            return@queryPurchases
        }

        if (purchase.purchaseToken.isNullOrEmpty()) {
            loge("Purchase token is empty :: skuType = $skuType, skuId = $skuId, purchase = $purchase")
            return@queryPurchases
        }

        consumeAsync(purchase.purchaseToken) { responseCode, purchaseToken ->
            logv("Purchase consuming finished :: skuId = $skuId, responseCode = $responseCode, token = $purchaseToken")
            if (responseCode == BillingResponse.OK) {
                onSuccess(skuId)
            }
        }
    }
}
