package com.kakao.ad.tracker.sample.util

import android.app.PendingIntent
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.os.Bundle
import android.os.IBinder
import com.android.billingclient.api.BillingClient.BillingResponse
import com.android.billingclient.api.BillingClient.SkuType
import com.android.billingclient.util.BillingHelper.*
import com.android.vending.billing.IInAppBillingService
import org.json.JSONObject

class BillingServiceHelper(private val context: Context) {

    companion object {

        fun connect(context: Context, onConnected: (helper: BillingServiceHelper) -> Unit) {
            BillingServiceHelper(context).also { helper ->
                helper.connect {
                    onConnected(helper)
                    helper.disconnect()
                }
            }
        }

        fun queryPurchasesData(context: Context, @SkuType skuType: String, onSuccess: (List<String>) -> Unit) {
            BillingServiceHelper.connect(context) { helper ->
                val purchasesData = helper.queryPurchasesData(skuType) ?: return@connect
                onSuccess(purchasesData)
            }
        }

        fun createBuyIntent(
            context: Context,
            @SkuType skuType: String,
            skuId: String,
            onSuccess: (String, PendingIntent) -> Unit
        ) {
            BillingServiceHelper.connect(context) { helper ->
                val intent = helper.createBuyIntent(skuType, skuId) ?: return@connect
                onSuccess(skuId, intent)
            }
        }

        fun consumePurchase(context: Context, @SkuType skuType: String, skuId: String, onSuccess: (String) -> Unit) {
            BillingServiceHelper.connect(context) { helper ->
                val purchasesData = helper.queryPurchasesData(skuType) ?: return@connect
                val purchaseData = purchasesData
                    .asSequence()
                    .map { JSONObject(it) }
                    .find { it.optString("productId") == skuId }

                if (purchaseData == null) {
                    logv("No purchase :: skuType = $skuType, skuId = $skuId")
                    return@connect
                }

                val purchaseToken = purchaseData.optString("token", purchaseData.optString("purchaseToken"))
                if (purchaseToken.isNullOrEmpty()) {
                    logv("Purchase token is empty :: skuType = $skuType, skuId = $skuId, purchaseData = $purchaseData")
                    return@connect
                }

                if (helper.consumePurchase(purchaseToken)) {
                    onSuccess(skuId)
                }
            }
        }
    }

    private var connection: ServiceConnection? = null
    private var service: IInAppBillingService? = null

    private var isConnectedOrConnecting
        get() = service != null && connection != null
        private set(value) {
            if (!value) {
                service = null
                connection = null
            }
        }

    private fun connect(onConnected: () -> Unit) {
        if (isConnectedOrConnecting) {
            loge("In-app billing service is already connected or connecting")
            return
        }

        val intent = Intent()
            .setPackage("com.android.vending")
            .setAction("com.android.vending.billing.InAppBillingService.BIND")
        connection = object : ServiceConnection {

            override fun onServiceConnected(componentName: ComponentName?, binder: IBinder?) {
                try {
                    connection = this
                    service = IInAppBillingService.Stub.asInterface(binder)!!
                } catch (t: Throwable) {
                    loge("In-app billing service connecting failed :: $t")
                    disconnect()
                    return
                }

                logv("In-app billing service connected")
                onConnected()
            }

            override fun onServiceDisconnected(componentName: ComponentName?) {
                loge("In-app billing service disconnected")
                isConnectedOrConnecting = false
            }
        }

        try {
            context.bindService(intent, connection!!, Context.BIND_AUTO_CREATE)
        } catch (t: Throwable) {
            loge("In-app billing service binding failed :: $t")
            return
        }

        logv("In-app billing service connection started")
    }

    private fun disconnect() {
        val connection = connection ?: return
        try {
            context.unbindService(connection)
        } catch (t: Throwable) {
            loge("In-app billing service unbinding failed :: $t")
        }

        logv("In-app billing service connection is finished")
        this.isConnectedOrConnecting = false
    }

    fun queryBillingSupported(@SkuType skuType: String): Boolean {
        val service = service
        if (service == null) {
            loge("In-app billing service is not connected")
            return false
        }

        val responseCode = try {
            service.isBillingSupported(3, context.packageName, skuType)
        } catch (t: Throwable) {
            loge("Billing supported query failed :: skuType = $skuType, error = $t")
            return false
        }

        logv("Billing supported query finished :: skuType = $skuType, responseCode = $responseCode")
        return (responseCode == BillingResponse.OK)
    }

    fun queryPurchasesData(@SkuType skuType: String): List<String>? {
        val service = service
        if (service == null) {
            loge("In-app billing service is not connected")
            return null
        }

        val purchasesData = arrayListOf<String>()
        var continueToken: String? = null
        do {
            val response = try {
                service.getPurchases(3, context.packageName, skuType, continueToken)
            } catch (t: Throwable) {
                loge("Purchases data query failed :: skuType = $skuType, continueToken = $continueToken, error = $t")
                break
            }

            val responseCode = response.getResponseCode()
            logv("Purchases data query finished :: skuType = $skuType, responseCode = $responseCode, response = $response")
            if (responseCode != BillingResponse.OK) {
                break
            }

            purchasesData += response.getStringArrayList(RESPONSE_INAPP_PURCHASE_DATA_LIST) ?: break
            continueToken = response.getString(INAPP_CONTINUATION_TOKEN)
        } while (continueToken?.isNotEmpty() == true)
        return purchasesData
    }

    fun createBuyIntent(@SkuType skuType: String, skuId: String): PendingIntent? {
        val service = service
        if (service == null) {
            loge("In-app billing service is not connected")
            return null
        }

        if (!queryBillingSupported(skuType)) {
            loge("Billing is not supported :: skuType = $skuType")
            return null
        }

        val response = try {
            service.getBuyIntent(3, context.packageName, skuId, skuType, "")
        } catch (t: Throwable) {
            loge("Buy intent creating failed :: skuType = $skuType ")
            return null
        }

        val responseCode = response.getResponseCode()
        logv("Buy intent created :: skuType = $skuType, skuId = $skuId, responseCode = $responseCode, response = $response")
        if (responseCode != BillingResponse.OK) {
            return null
        }

        return response.getParcelable(RESPONSE_BUY_INTENT_KEY)
    }

    fun consumePurchase(purchaseToken: String): Boolean {
        val service = service
        if (service == null) {
            loge("In-app billing service is not connected")
            return false
        }

        val responseCode = try {
            service.consumePurchase(3, context.packageName, purchaseToken)
        } catch (t: Throwable) {
            loge("Purchase consuming failed :: purchaseToken = $purchaseToken")
            return false
        }

        logv("Purchase consumed :: purchaseToken = $purchaseToken")
        return (responseCode == BillingResponse.OK)
    }

    private fun Bundle.getResponseCode(): Int {
        val responseCode = get(RESPONSE_CODE)
        return when (responseCode) {
            null -> BillingResponse.OK
            is Int -> responseCode.toInt()
            is Long -> responseCode.toInt()
            else -> {
                loge("Bundle response code is unexpected :: ${responseCode.javaClass.name}")
                throw RuntimeException("Unexpected type for bundle response code: " + responseCode.javaClass.name)
            }
        }
    }
}