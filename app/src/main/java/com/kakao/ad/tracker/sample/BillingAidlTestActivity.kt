package com.kakao.ad.tracker.sample

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import com.android.billingclient.api.BillingClient.SkuType
import com.kakao.ad.tracker.sample.util.BillingServiceHelper
import com.kakao.ad.tracker.sample.util.logAndToast
import com.kakao.ad.tracker.sample.util.loge
import com.kakao.ad.tracker.sample.util.startPendingIntent
import org.json.JSONObject

class BillingAidlTestActivity : AppCompatActivity() {

    companion object {

        private const val REQUEST_CODE_BILLING = 1001
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_billing_sample)

        findViewById<Button>(R.id.queryPurchasesButton)
            .setOnClickListener {
                BillingServiceHelper.queryPurchasesData(this, SkuType.INAPP) { purchasesData ->
                    val skuIds = purchasesData.joinToString { JSONObject(it).optString("productId") }
                        .takeIf { it.isNotEmpty() } ?: "empty"

                    logAndToast("Purchases = $skuIds")
                }
            }

        findViewById<Button>(R.id.querySubscriptionsButton)
            .setOnClickListener {
                BillingServiceHelper.queryPurchasesData(this, SkuType.SUBS) { purchasesData ->
                    val skuIds = purchasesData.joinToString { JSONObject(it).optString("productId") }
                        .takeIf { it.isNotEmpty() } ?: "empty"

                    logAndToast("Subscriptions = $skuIds")
                }
            }

        findViewById<Button>(R.id.buyProduct01Button)
            .setOnClickListener {
                BillingServiceHelper.createBuyIntent(this, SkuType.INAPP, "purchase_01") { skuId, pendingIntent ->
                    try {
                        startPendingIntent(REQUEST_CODE_BILLING, pendingIntent)
                    } catch (t: Throwable) {
                        loge("Failed to start pending intent :: $t")
                        return@createBuyIntent
                    }

                    logAndToast("\"$skuId\" billing flow launched")
                }
            }

        findViewById<Button>(R.id.consumeProduct01Button)
            .setOnClickListener {
                BillingServiceHelper.consumePurchase(this, SkuType.INAPP, "purchase_01") { skuId ->
                    logAndToast("\"$skuId\" consumed")
                }
            }

        findViewById<Button>(R.id.buyProduct02Button)
            .setOnClickListener {
                BillingServiceHelper.createBuyIntent(this, SkuType.INAPP, "purchase_02") { skuId, pendingIntent ->
                    try {
                        startPendingIntent(REQUEST_CODE_BILLING, pendingIntent)
                    } catch (t: Throwable) {
                        loge("Failed to start pending intent :: $t")
                        return@createBuyIntent
                    }

                    logAndToast("\"$skuId\" billing flow launched")
                }
            }

        findViewById<Button>(R.id.consumeProduct02Button)
            .setOnClickListener {
                BillingServiceHelper.consumePurchase(this, SkuType.INAPP, "purchase_02") { skuId ->
                    logAndToast("\"$skuId\" consumed")
                }
            }

        findViewById<Button>(R.id.subscribeButton)
            .setOnClickListener {
                BillingServiceHelper.createBuyIntent(this, SkuType.SUBS, "subscription_01") { skuId, pendingIntent ->
                    try {
                        startPendingIntent(REQUEST_CODE_BILLING, pendingIntent)
                    } catch (t: Throwable) {
                        loge("Failed to start pending intent :: $t")
                        return@createBuyIntent
                    }

                    logAndToast("\"$skuId\" billing flow launched")
                }
            }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == REQUEST_CODE_BILLING && data != null) {
            val skuId =
                try {
                    JSONObject(data.getStringExtra("INAPP_PURCHASE_DATA"))
                        .optString("productId")
                        .takeIf { it.isNotEmpty() } ?: return
                } catch (ignored: Throwable) {
                    return
                }
            logAndToast("\"$skuId\" billing flow finished")
        }
    }
}

