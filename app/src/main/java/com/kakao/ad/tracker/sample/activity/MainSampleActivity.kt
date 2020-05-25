package com.kakao.ad.tracker.sample.activity

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.kakao.ad.common.json.CompleteRegistration
import com.kakao.ad.common.json.InAppPurchase
import com.kakao.ad.common.json.Participation
import com.kakao.ad.common.json.Product
import com.kakao.ad.common.json.Purchase
import com.kakao.ad.common.json.Search
import com.kakao.ad.common.json.SignUp
import com.kakao.ad.common.json.ViewCart
import com.kakao.ad.common.json.ViewContent
import com.kakao.ad.tracker.KakaoAdTracker
import com.kakao.ad.tracker.sample.R
import com.kakao.ad.tracker.sample.util.logAndToast
import com.kakao.ad.tracker.send
import kotlinx.android.synthetic.main.activity_main_sample.sendCompleteRegistrationEventButton
import kotlinx.android.synthetic.main.activity_main_sample.sendInAppPurchaseEventButton
import kotlinx.android.synthetic.main.activity_main_sample.sendParticipationEventButton
import kotlinx.android.synthetic.main.activity_main_sample.sendPurchaseEventButton
import kotlinx.android.synthetic.main.activity_main_sample.sendSearchEventButton
import kotlinx.android.synthetic.main.activity_main_sample.sendSignUpEventButton
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
        sendSearchEventButton.setOnClickListener { sendSearchEvent() }
        sendViewContentEventButton.setOnClickListener { sendViewContentEvent() }
        sendViewCartEventButton.setOnClickListener { sendViewCartEvent() }
        sendPurchaseEventButton.setOnClickListener { sendPurchaseEvent() }
        sendInAppPurchaseEventButton.setOnClickListener { sendInAppPurchaseEvent() }
        sendParticipationEventButton.setOnClickListener { sendParticipationEvent() }
        sendSignUpEventButton.setOnClickListener { sendSignUpEvent() }
        startInAppBillingLibTestButton.setOnClickListener {
            startActivity(Intent(it.context, BillingLibTestActivity::class.java))
        }
        startInAppBillingAidlTestButton.setOnClickListener {
            startActivity(Intent(it.context, BillingAidlTestActivity::class.java))
        }
    }

    /**
     * 가입완료 이밴트(CompleteRegistration)를 전송합니다.
     */
    fun sendCompleteRegistrationEvent() {
        val event = CompleteRegistration()
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

    /**
     * 잠재고객 이벤트(Participation)를 전송합니다.
     *
     * 잠재고객([Participation]) 이벤트는 아래 태그([Participation.tag])를 추가 설정하면 전환을 최적화하는데 도움이 됩니다.
     *
     * 권장 태그 추가 목적 (태그값):
     * - 사전예약 (PreBooking)
     * - 상담신청 (Consulting)
     * - 시승신청 (DrivingTest)
     * - 대출한도조회 (LoanLimitCheck)
     * - 보험료조회 (InsuranceCheck)
     */
    fun sendParticipationEvent() {
        val event = Participation()
        event.tag = "Tag" // 분류
        event.send()
    }

    /**
     * 가입 및 등록 이벤트(SignUp)를 전송합니다.
     *
     * 가입 및 등록([SignUp]) 이벤트는 아래 태그([SignUp.tag])를 추가 설정하면 전환을 최적화하는데 도움이 됩니다.
     *
     * 권장 태그 추가 목적 (태그값):
     * - 서비스가입 (SignUp)
     * - 구독완료 (Subscription)
     * - 카드발급 (CardIssuance)
     * - 계좌개설 (OpeningAccount)
     * - 대출신청 (LoanApplication)
     */
    fun sendSignUpEvent() {
        val event = SignUp()
        event.tag = "Tag" // 분류
        event.send()
    }
}
