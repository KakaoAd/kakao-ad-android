package com.kakao.ad.tracker.sample.activity

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.android.billingclient.api.BillingClient
import com.android.billingclient.api.BillingClient.BillingResponseCode
import com.android.billingclient.api.BillingClient.ProductType
import com.android.billingclient.api.BillingClientStateListener
import com.android.billingclient.api.BillingFlowParams
import com.android.billingclient.api.BillingResult
import com.android.billingclient.api.ConsumeParams
import com.android.billingclient.api.ProductDetails
import com.android.billingclient.api.Purchase
import com.android.billingclient.api.QueryProductDetailsParams
import com.android.billingclient.api.QueryPurchasesParams
import com.kakao.ad.tracker.KakaoAdTracker
import com.kakao.ad.tracker.sample.R
import com.kakao.ad.tracker.sample.databinding.ActivityBillingSampleBinding
import com.kakao.ad.tracker.sample.util.logAndToast
import com.kakao.ad.tracker.sample.util.logv
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class BillingLibTestActivity : AppCompatActivity() {

    private lateinit var client: BillingClient
    private lateinit var binding: ActivityBillingSampleBinding

    private var inAppProducts = mutableListOf<ProductDetails>()
    private var subProducts = mutableListOf<ProductDetails>()
    private var inAppPurchaseList = mutableListOf<Purchase>()
    private var subPurchaseList = mutableListOf<Purchase>()

    private val supportedInAppProducts = listOf(
        QueryProductDetailsParams.Product.newBuilder()
            .setProductId("purchase_01")
            .setProductType(ProductType.INAPP)
            .build(),
        QueryProductDetailsParams.Product.newBuilder()
            .setProductId("purchase_02")
            .setProductType(ProductType.INAPP)
            .build()
    )

    private val supportedSubProducts = listOf(
        QueryProductDetailsParams.Product.newBuilder()
            .setProductId("subscription_01")
            .setProductType(ProductType.SUBS)
            .build(),
        QueryProductDetailsParams.Product.newBuilder()
            .setProductId("subscription_02")
            .setProductType(ProductType.SUBS)
            .build(),
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityBillingSampleBinding.inflate(layoutInflater)

        setContentView(binding.root)

        createAndConnectBillingClient()

        if (!KakaoAdTracker.isInitialized) {
            KakaoAdTracker.init(this, getString(R.string.kakao_ad_track_id))
        }

        setupUI()
    }

    override fun onDestroy() {
        super.onDestroy()
        client.endConnection()
    }

    private fun createAndConnectBillingClient() {
        client =
            BillingClient.newBuilder(this)
                .setListener { result, purchases ->
                    if (result.responseCode == BillingResponseCode.OK && purchases != null) {
                        purchases.forEach { purchase ->
                            KakaoAdTracker.sendInAppPurchaseData(purchase.originalJson)
                            CoroutineScope(Dispatchers.Main).launch {
                                logAndToast("\"${purchase.products.first()}\" billing flow finished")
                            }
                        }
                    }
                }
                .enablePendingPurchases()
                .build()

        client.startConnection(object : BillingClientStateListener {
            override fun onBillingSetupFinished(result: BillingResult) {
                if (result.responseCode == BillingClient.BillingResponseCode.OK) {
                    CoroutineScope(Dispatchers.Main).launch {
                        logAndToast("BillingClient connected")
                    }
                }
            }

            override fun onBillingServiceDisconnected() {
                client.endConnection()
            }
        })
    }

    private fun setupUI() {
        binding.queryPurchasesButton
            .setOnClickListener {
                val queryPurchasesParams = QueryPurchasesParams.newBuilder()
                    .setProductType(BillingClient.ProductType.INAPP)
                    .build()

                client.queryPurchasesAsync(queryPurchasesParams) { result, purchases ->
                    if (result.responseCode == BillingResponseCode.OK && purchases.isNotEmpty()) {
                        purchases.forEach { purchase ->
                            inAppPurchaseList.add(purchase)
                        }
                        CoroutineScope(Dispatchers.Main).launch {
                            logAndToast("Purchases(InApp) = ${inAppPurchaseList.toString()}")
                        }
                    }
                }
            }

        binding.querySubscriptionsButton
            .setOnClickListener {
                val queryPurchasesParams = QueryPurchasesParams.newBuilder()
                    .setProductType(BillingClient.ProductType.SUBS)
                    .build()

                client.queryPurchasesAsync(queryPurchasesParams) { result, purchases ->
                    if (result.responseCode == BillingResponseCode.OK && purchases.isNotEmpty()) {
                        purchases.forEach { purchase ->
                            subPurchaseList.add(purchase)
                        }
                        CoroutineScope(Dispatchers.Main).launch {
                            logAndToast("Purchases(SUBS) = ${subPurchaseList.toString()}")
                        }
                    }
                }
            }

        binding.buyProduct01Button
            .setOnClickListener {
                buy(true, 0)
            }

        binding.consumeProduct01Button
            .setOnClickListener {
                consume(0)
            }

        binding.buyProduct02Button
            .setOnClickListener {
                buy(true, 1)
            }

        binding.consumeProduct02Button
            .setOnClickListener {
                consume(1)
            }

        binding.subscribeButton
            .setOnClickListener {
                buy(false, 0)
            }
    }

    private fun buy(isConsumable: Boolean, index:Int) {
        val productDetailsParamList = mutableListOf<BillingFlowParams.ProductDetailsParams>()

        if (isConsumable) {
            productDetailsParamList.add(
                BillingFlowParams.ProductDetailsParams.newBuilder()
                    .setProductDetails(inAppProducts[index])
                    .build()
            )
        } else {
            productDetailsParamList.add(
                BillingFlowParams.ProductDetailsParams.newBuilder()
                    .setProductDetails(subProducts[index])
                    .build()
            )
        }

        val billingFlowParams = BillingFlowParams.newBuilder()
            .setProductDetailsParamsList(productDetailsParamList)
            .build()

        val billingResult = client.launchBillingFlow(this, billingFlowParams)
        if (billingResult.responseCode == BillingClient.BillingResponseCode.OK) {
            if (isConsumable) {
                logAndToast("\"${inAppProducts[index].productId}\" billing flow launched")
            } else {
                logAndToast("\"${subProducts[index].productId}\" billing flow launched")
            }
        }
    }

    private fun consume(index: Int) {
        if (inAppPurchaseList.isEmpty())
            return

        val productId = if (index == 0) "purchase_01" else "purchase_02"

        val purchase = inAppPurchaseList.filter { it.products.first() == productId }
            .first()

        val consumeParams = ConsumeParams.newBuilder()
            .setPurchaseToken(purchase.purchaseToken)
            .build()

        client.consumeAsync(consumeParams) { result, purchaseToken ->
            if (result.responseCode == BillingClient.BillingResponseCode.OK) {
                inAppPurchaseList.remove(purchase)
            }
            CoroutineScope(Dispatchers.Main).launch {
                logAndToast("Consume result = ${result.responseCode}")
            }
        }
    }
}
