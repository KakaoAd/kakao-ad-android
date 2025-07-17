package com.kakao.ad.tracker.sample.activity

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.kakao.ad.common.json.*
import com.kakao.ad.tracker.KakaoAdTracker
import com.kakao.ad.tracker.sample.R
import com.kakao.ad.tracker.sample.util.logAndToast
import com.kakao.ad.tracker.send
import com.kakao.ad.tracker.sample.databinding.ActivityMainSampleBinding
import java.util.Currency
import java.util.Locale

class MainSampleActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainSampleBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainSampleBinding.inflate(layoutInflater)

        if (!KakaoAdTracker.isInitialized) {
            KakaoAdTracker.init(this, getString(R.string.kakao_ad_track_id))
        }

        logAndToast(
            "KakaoAdTracker.isInitialized? ${KakaoAdTracker.isInitialized}\n" +
                    "KakaoAdTracker.version = ${KakaoAdTracker.VERSION}"
        )

        setContentView(binding.root)

        binding.sendCompleteRegistrationEventButton.setOnClickListener { sendCompleteRegistrationEvent() }
        binding.sendSearchEventButton.setOnClickListener { sendSearchEvent() }
        binding.sendViewContentEventButton.setOnClickListener { sendNewViewContentEvent() }
        binding.sendViewCartEventButton.setOnClickListener { sendViewCartEvent() }
        binding.sendAddToCartEventButton.setOnClickListener { sendNewAddToCartEvent() }
        binding.sendAddToWishListEventButton.setOnClickListener { sendNewAddToWishListEvent() }
        binding.sendPurchaseEventButton.setOnClickListener { sendPurchaseEvent() }
        binding.sendInAppPurchaseEventButton.setOnClickListener { sendInAppPurchaseEvent() }
        binding.sendParticipationEventButton.setOnClickListener { sendParticipationEvent() }
        binding.sendSignUpEventButton.setOnClickListener { sendSignUpEvent() }
        binding.startInAppBillingLibTestButton.setOnClickListener {
            startActivity(Intent(it.context, BillingLibTestActivity::class.java))
        }

        binding.sendLoginEventButton.setOnClickListener { sendLoginEvent() }
        binding.sendPreparationEventButton.setOnClickListener { sendPreparationEvent() }
        binding.sendTutorialEventButton.setOnClickListener { sendTutorialEvent() }
        binding.sendMissionCompleteEventButton.setOnClickListener { sendMissionCompleteEvent() }
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
     * @deprecated - 'Use sendNewViewContentEvent() instead'
     */
    fun sendViewContentEvent() {
        val event = ViewContent()
        event.tag = "Tag" // 분류
        event.content_id = "V-Content ID" // 상품 코드
        event.send()

        logAndToast("ViewContent = ${event.content_id}")
    }

    fun sendNewViewContentEvent() {
        val event = ViewContent()
        event.tag = "Tag" // 분류
        event.products =
            listOf(
                Product().also { product ->
                    product.id = "V0001" // 상품 ID
                    product.name = "View Product 1" // 상품명
                    product.quantity = 1 // 개수
                    product.price = 1.1 // 금액
                },
                Product().also { product ->
                    product.id = "V0002" // 상품 ID
                    product.name = "View Product 2" // 상품명
                    product.quantity = 2 // 개수
                    product.price = 2.2 // 금액
                }
            )
        event.send()
        logAndToast("ViewContent = ${event.products?.map { it.id }.toString()}")
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
     * 장바구니 추가 이벤트(장바구니추가))를 전송합니다.
     * @deprecated - 'Use sendNewAddToCartEvent() instead'
     */
    fun sendAddToCartEvent() {
        val event = AddToCart()
        event.tag = "Tag" // 분류
        event.content_id = "C-Content ID" // 상품 코드
        event.send()
        logAndToast("AddToCart = ${event.content_id}")
    }

    /**
     * 장바구니 추가 이벤트(장바구니추가))를 전송합니다.
     */
    fun sendNewAddToCartEvent() {
        val event = AddToCart()
        event.tag = "Tag" // 분류
        event.products =
            listOf(
                Product().also { product ->
                    product.id = "C0001" // 상품 ID
                    product.name = "Cart Product 1" // 상품명
                    product.quantity = 1 // 개수
                    product.price = 2.1 // 금액
                },
                Product().also { product ->
                    product.id = "C0002" // 상품 ID
                    product.name = "Cart Product 2" // 상품명
                    product.quantity = 2 // 개수
                    product.price = 3.2 // 금액
                }
            )
        event.send()
        logAndToast("AddToCart = ${event.products?.map { it.id }.toString()}")
    }

    /**
     * 관심상품 추가 이벤트(AddToWishlist)를 전송합니다.
     * @deprecated - 'Use sendNewAddToWishListEvent() instead'
     */
    fun sendAddToWishListEvent() {
        val event = AddToWishList()
        event.tag = "Tag" // 분류
        event.content_id = "W-Content ID" // 상품 코드
        event.send()
        logAndToast("AddToWishList = ${event.content_id}")
    }

    /**
     * 관심상품 추가 이벤트(AddToWishlist)를 전송합니다.
     */
    fun sendNewAddToWishListEvent() {
        val event = AddToWishList()
        event.tag = "Tag" // 분류
        event.products =
            listOf(
                Product().also { product ->
                    product.id = "W0001" // 상품 ID
                    product.name = "Wish Product 1" // 상품명
                    product.quantity = 1 // 개수
                    product.price = 3.1 // 금액
                },
                Product().also { product ->
                    product.id = "W0002" // 상품 ID
                    product.name = "Wart Product 2" // 상품명
                    product.quantity = 2 // 개수
                    product.price = 4.2 // 금액
                },
                Product().also { product ->
                    product.id = "W0003" // 상품 ID
                    product.name = "Wart Product 3" // 상품명
                    product.quantity = 1 // 개수
                    product.price = 2.2 // 금액
                }
            )
        event.send()
        logAndToast("AddToWishList = ${event.products?.map { it.id }.toString()}")
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
                    product.id = "P0001" // 상품 ID
                    product.name = "Product 1" // 상품명
                    product.quantity = 1 // 개수
                    product.price = 1.1 // 금액
                },
                Product().also { product ->
                    product.id = "P0002" // 상품 ID
                    product.name = "Product 2" // 상품명
                    product.quantity = 2 // 개수
                    product.price = 2.2 // 금액
                }
            )
        event.currency = Currency.getInstance(Locale.KOREA) // 통화코드(ISO-4217)
        event.total_quantity = event.products?.sumOf { it.quantity } // 총 개수
        event.total_price = event.products?.sumOf { it.price } // 총 금액
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
                    product.id = "P0001" // 상품 ID
                    product.name = "Product 1" // 상품명
                    product.quantity = 1 // 개수
                    product.price = 1.1 // 금액
                },
                Product().also { product ->
                    product.id = "P0002" // 상품 ID
                    product.name = "Product 2" // 상품명
                    product.quantity = 2 // 개수
                    product.price = 2.2 // 금액
                }
            )
        event.currency = Currency.getInstance(Locale.KOREA) // 통화코드(ISO-4217)
        event.total_quantity = event.products?.sumOf { it.quantity } // 총 개수
        event.total_price = event.products?.sumOf { it.price } // 총 금액
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

    /**
     * 로그인 (Login) 이벤트를 전송합니다.
     */
    fun sendLoginEvent() {
        val event = Login()
        event.tag = "Tag"
        event.send()
    }

    /**
     * 사전준비(Preparation) 이벤트를 전송합니다.
     */
    fun sendPreparationEvent() {
        val event = Preparation()
        event.tag = "Tag"
        event.send()
    }

    /**
     * 튜토리얼(Tutorial) 이벤트를 전송합니다.
     */
    fun sendTutorialEvent() {
        val event = Tutorial()
        event.tag = "Tag"
        event.send()
    }

    /**
     * 미션완료(Mission Complete) 이벤트를 전송합니다.
     */
    fun sendMissionCompleteEvent() {
        val event = MissionComplete()
        event.tag = "Tag"
        event.send()
    }
}
