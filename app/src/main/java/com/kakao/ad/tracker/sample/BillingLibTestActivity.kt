package com.kakao.ad.tracker.sample

import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import com.android.billingclient.api.BillingClient
import com.android.billingclient.api.BillingClient.BillingResponse
import com.android.billingclient.api.BillingClient.SkuType
import com.kakao.ad.tracker.KakaoAdTracker
import com.kakao.ad.tracker.sample.util.consumePurchase
import com.kakao.ad.tracker.sample.util.launchBillingFlow
import com.kakao.ad.tracker.sample.util.logAndToast
import com.kakao.ad.tracker.sample.util.logv
import com.kakao.ad.tracker.sample.util.queryPurchases

class BillingLibTestActivity : AppCompatActivity() {

    private lateinit var client: BillingClient

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        if (!KakaoAdTracker.isInitialized) {
            KakaoAdTracker.init(this, getString(R.string.kakao_ad_track_id))
        }

        client =
            BillingClient.newBuilder(this)
                .setListener { responseCode, purchases ->
                    logv("Purchases updated :: responseCode = $responseCode, list = $purchases")
                    if (responseCode == BillingResponse.OK && purchases != null) {
                        purchases.forEach { purchase ->
                            KakaoAdTracker.sendInAppPurchaseData(purchase.originalJson)

                            logAndToast("\"${purchase.sku}\" billing flow finished")
                        }
                    }
                }
                .build()

        setContentView(R.layout.activity_billing_sample)

        findViewById<Button>(R.id.queryPurchasesButton)
            .setOnClickListener {
                client.queryPurchases(SkuType.INAPP) { purchases ->
                    val skuIds = purchases.joinToString { it.sku }
                        .takeIf { it.isNotEmpty() } ?: "empty"

                    logAndToast("Purchases = $skuIds")
                }
            }

        findViewById<Button>(R.id.querySubscriptionsButton)
            .setOnClickListener {
                client.queryPurchases(SkuType.SUBS) { purchases ->
                    val skuIds = purchases.joinToString { it.sku }
                        .takeIf { it.isNotEmpty() } ?: "empty"

                    logAndToast("Subscriptions = $skuIds")
                }
            }

        findViewById<Button>(R.id.buyProduct01Button)
            .setOnClickListener {
                client.launchBillingFlow(this, SkuType.INAPP, "purchase_01") { skuId ->
                    logAndToast("\"$skuId\" billing flow launched")
                }
            }

        findViewById<Button>(R.id.consumeProduct01Button)
            .setOnClickListener {
                client.consumePurchase(SkuType.INAPP, "purchase_01") { skuId ->
                    logAndToast("\"$skuId\" consumed")
                }
            }

        findViewById<Button>(R.id.buyProduct02Button)
            .setOnClickListener {
                client.launchBillingFlow(this, SkuType.INAPP, "purchase_02") { skuId ->
                    logAndToast("\"$skuId\" billing flow launched")
                }
            }

        findViewById<Button>(R.id.consumeProduct02Button)
            .setOnClickListener {
                client.consumePurchase(SkuType.INAPP, "purchase_02") { skuId ->
                    logAndToast("\"$skuId\" consumed")
                }
            }

        findViewById<Button>(R.id.subscribeButton)
            .setOnClickListener {
                client.launchBillingFlow(this, SkuType.SUBS, "subscription_01") { skuId ->
                    logAndToast("\"$skuId\" billing flow launched")
                }
            }
    }

    override fun onDestroy() {
        super.onDestroy()

        client.endConnection()
    }
}
