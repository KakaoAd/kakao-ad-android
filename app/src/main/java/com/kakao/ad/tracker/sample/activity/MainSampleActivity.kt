package com.kakao.ad.tracker.sample.activity

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.kakao.ad.common.json.CompleteRegistration
import com.kakao.ad.common.json.InAppPurchase
import com.kakao.ad.common.json.PageView
import com.kakao.ad.common.json.Product
import com.kakao.ad.common.json.Purchase
import com.kakao.ad.common.json.Search
import com.kakao.ad.common.json.ViewCart
import com.kakao.ad.common.json.ViewContent
import com.kakao.ad.tracker.KakaoAdTracker
import com.kakao.ad.tracker.sample.R
import com.kakao.ad.tracker.sample.util.logAndToast
import com.kakao.ad.tracker.send
import kotlinx.android.synthetic.main.activity_main_sample.sendCompleteRegistrationEventButton
import kotlinx.android.synthetic.main.activity_main_sample.sendInAppPurchaseEventButton
import kotlinx.android.synthetic.main.activity_main_sample.sendPageViewEventButton
import kotlinx.android.synthetic.main.activity_main_sample.sendPurchaseEventButton
import kotlinx.android.synthetic.main.activity_main_sample.sendSearchEventButton
import kotlinx.android.synthetic.main.activity_main_sample.sendViewCartEventButton
import kotlinx.android.synthetic.main.activity_main_sample.sendViewContentEventButton
import kotlinx.android.synthetic.main.activity_main_sample.startInAppBillingAidlTestButton
import kotlinx.android.synthetic.main.activity_main_sample.startInAppBillingLibTestButton
import java.util.Currency
import java.util.Locale

class MainSampleActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        if (!KakaoAdTracker.isInitialized) {
            KakaoAdTracker.init(this, getString(R.string.kakao_ad_track_id))
        }

        logAndToast(
            "KakaoAdTracker.isInitialized? ${KakaoAdTracker.isInitialized}\n" +
                    "KakaoAdTracker.version = ${KakaoAdTracker.VERSION}"
        )

        setContentView(R.layout.activity_main_sample)

        sendCompleteRegistrationEventButton.setOnClickListener { sendCompleteRegistrationEvent() }
        sendPageViewEventButton.setOnClickListener { sendPageViewEvent() }
        sendSearchEventButton.setOnClickListener { sendSearchEvent() }
        sendViewContentEventButton.setOnClickListener { sendViewContentEvent() }
        sendViewCartEventButton.setOnClickListener { sendViewCartEvent() }
        sendPurchaseEventButton.setOnClickListener { sendPurchaseEvent() }
        sendInAppPurchaseEventButton.setOnClickListener { sendInAppPurchaseEvent() }
        startInAppBillingLibTestButton.setOnClickListener {
            startActivity(Intent(it.context, BillingLibTestActivity::class.java))
        }
        startInAppBillingAidlTestButton.setOnClickListener {
            startActivity(Intent(it.context, BillingAidlTestActivity::class.java))
        }
    }

    /**
     * 가입 이밴트(CompleteRegistration)를 전송합니다.
     */
    fun sendCompleteRegistrationEvent() {
        val event = CompleteRegistration()
        event.tag = "Tag" // 분류
        event.send()
    }

    /**
     * 페이지 방문 이밴트(PageView)를 전송합니다.
     */
    fun sendPageViewEvent() {
        val event = PageView()
        event.tag = "Tag" // 분류
        event.send()
    }

    /**
     * 검색 이벤트(Search)를 전송합니다.
     */
    fun sendSearchEvent() {
        val event = Search()
        event.tag = "Tag" // 분류
        event.search_string = "Keyword" // 검색 문자열
        event.send()
    }

    /**
     * 콘텐츠/상품 조회 이벤트(ViewContent)를 전송합니다.
     */
    fun sendViewContentEvent() {
        val event = ViewContent()
        event.tag = "Tag" // 분류
        event.content_id = "Content ID" // 상품 코드
        event.send()
    }

    /**
     * 장바구니 보기 이벤트(ViewCart)를 전송합니다.
     */
    fun sendViewCartEvent() {
        val event = ViewCart()
        event.tag = "Tag" // 분류
        event.send()
    }

    /**
     * 구매 이벤트(Purchase)를 전송합니다.
     */
    fun sendPurchaseEvent() {
        val event = Purchase()
        event.tag = "Tag" // 분류
        event.products = // 구매 상품 목록
            listOf(
                Product().also { product ->
                    product.name = "Product 1" // 상품명
                    product.quantity = 1 // 개수
                    product.price = 1.1 // 금액
                },
                Product().also { product ->
                    product.name = "Product 2" // 상품명
                    product.quantity = 2 // 개수
                    product.price = 2.2 // 금액
                }
            )
        event.currency = Currency.getInstance(Locale.KOREA) // 통화코드(ISO-4217)
        event.total_quantity = event.products?.sumBy { it.quantity } // 총 개수
        event.total_price = event.products?.sumByDouble { it.price } // 총 금액
        event.send()
    }

    /**
     * 인앱 구매 이벤트(InAppPurchase)를 전송합니다.
     */
    fun sendInAppPurchaseEvent() {
        val event = InAppPurchase()
        event.tag = "Tag" // 분류
        event.products = // 구매 상품 목록
            listOf(
                Product().also { product ->
                    product.name = "Product 1" // 상품명
                    product.quantity = 1 // 개수
                    product.price = 1.1 // 금액
                },
                Product().also { product ->
                    product.name = "Product 2" // 상품명
                    product.quantity = 2 // 개수
                    product.price = 2.2 // 금액
                }
            )
        event.currency = Currency.getInstance(Locale.KOREA) // 통화코드(ISO-4217)
        event.total_quantity = event.products?.sumBy { it.quantity } // 총 개수
        event.total_price = event.products?.sumByDouble { it.price } // 총 금액
        event.send()
    }
}
