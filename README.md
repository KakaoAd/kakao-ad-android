# Kakao AD Android SDK Guide

## 시작하기
* 최신 버전의 Kakao AD SDK 사용을 권장합니다.
* 최신 버전의 [Android Studio](https://developer.android.com/studio/) 사용을 권장합니다. Eclipse에 대한 기술 지원은 하지 않습니다.
* 최신 버전의 [Kotlin](https://developer.android.com/kotlin/) 사용을 권장합니다.
* Kakao AD SDK는 [Android 4.0(Ice Cream Sandwich, API Level 14)](https://developer.android.com/about/versions/android-4.0) 이상 기기에서 동작합니다.


### 트랙 ID(Track ID) 발급받기
Kakao AD SDK를 이용하기 위해서는 먼저 고유한 식별값인 트랙 ID(Track Id)가 필요합니다.<br/>
Track ID는 [https://moment.kakao.com](https://moment.kakao.com/login) 에서 회원 가입 후, [https://moment.kakao.com/mypixel](https://moment.kakao.com/mypixel) 페이지에서 발급 받을 수 있습니다.
Track ID 발급이 완료된 후, 다음 단계의 안내에 따라 Kakao AD SDK를 설치해주세요.

### build.gradle 설정하기
Kakao AD SDK를 사용하기 위해서는 Kotlin과 Google Play Service SDK에 대한 설정이 필요합니다.<br/>
Kotlin과 Google Play Service SDK 설정 방법에 대해서는 아래 사이트와 샘플 프로젝트를 참고 부탁드립니다.
* Kotlin 설정 방법: [http://kotlinlang.org/docs/tutorials/kotlin-android.html](http://kotlinlang.org/docs/tutorials/kotlin-android.html)
* Google Play Service SDK 설정 방법: [https://developers.google.com/android/guides/setup](https://developers.google.com/android/guides/setup)

Kakao AD SDK를 추가하는 방법은 다음과 같습니다.
1. 먼저 최상위 [`build.gradle`](build.gradle) 파일에 Maven repository를 추가합니다.

    ```gradle
    allprojects {
        repositories {
            google()
            jcenter()
            maven { url 'http://devrepo.kakao.com:8088/nexus/content/groups/public/' }
        }
    }
    ```

2. App 모듈 [`build.gradle`](app/build.gradle) 파일에 최신 버전의 Kakao AD SDK를 추가합니다.

    ```gradle
    dependencies {
        implementation "org.jetbrains.kotlin:kotlin-stdlib-jdk7:$kotlin_version"
        implementation "com.google.android.gms:play-services-ads-identifier:$play_service_version"

        implementation "com.kakao.ad:tracker:$kakao_ad_tracker_version"
    }
    ```

3. 설정 후, 툴바의 "Sync Project with Gradle Files" 버튼을 눌러 변경사항을 반영합니다.


### AndroidManifest.xml 설정하기
Kakao AD SDK를 초기화 하기 위한 정보를 설정하는 방법은 다음과 같습니다.
1. 먼저 [`strings.xml`](app/src/main/res/values/strings.xml)에 발급 받은 Track ID를 문자열 리소스로 추가합니다.

    ```xml
    <string name="kakao_ad_track_id" translatable="false">Input Your Track ID</string>
    ```
2. [`AndroidManifest.xml`](app/src/main/AndroidManifest.xml) 파일 `<application>` 태그 하위에 `<meta-data>`를 추가합니다.<br/>
`<meta-data>`의 `name`은 `com.kakao.ad.tracker.TRACK_ID`를 사용하고, `value`는 위에서 추가한 리소스 정보를 사용합니다.

    ```xml
    <application>
        <!-- Track ID 정보 추가 -->
        <meta-data
            android:name="com.kakao.ad.tracker.TRACK_ID"
            android:value="@string/kakao_ad_track_id" />
    </application>
    ```

3. `com.android.vending.INSTALL_REFERRER` 정보를 Kakao AD SDK의 `KakaoAdInstallReferrerReceiver`외에
다른 `BroadcastReceiver`에 전달하기 위해서는 추가 설정이 필요합니다.<br/>
[`AndroidManifest.xml`](app/src/main/AndroidManifest.xml) 파일에 `KakaoAdInstallReferrerReceiver`를 추가합니다.<br/>
`KakaoAdInstallReferrerReceiver`는 다른 `BroadcastReceiver`보다 **먼저 추가해야 합니다!!!**<br/>
`KakaoAdInstallReferrerReceiver`의 `<receiver>` 태그 하위에 `<meta-data>`를 추가합니다.<br/>
`<meta-data>`의 `name`은 전달이 필요한 `BroadcastReceiver`의 별칭을 사용하고, `value`는 클래스명을 사용합니다.

    ```xml
    <!-- "KakaoAdInstallReferrerReceiver"를 BroadcastReceiver 중 가장 먼저 추가!!! -->
    <receiver
        android:name="com.kakao.ad.tracker.KakaoAdInstallReferrerReceiver"
        android:exported="true">
        <intent-filter>
            <action android:name="com.android.vending.INSTALL_REFERRER" />
        </intent-filter>

        <!-- "com.android.vending.INSTALL_REFERRER"를 수신할 BroadcastReceiver 추가 -->
        <meta-data
            android:name="receiver1"
            android:value="com.kakao.ad.tracker.sample.InstallReferrerReceiver1" />

        <!-- "com.android.vending.INSTALL_REFERRER"를 수신할 BroadcastReceiver 추가 -->
        <meta-data
            android:name="receiver2"
            android:value="com.kakao.ad.tracker.sample.InstallReferrerReceiver2" />
    </receiver>
    ```

* `<meta-data>`에 등록된 `BroadcastReceiver`는 `AndroidManifest.xml`에 `receiver`로 추가하지 않아도 `com.android.vending.INSTALL_REFERRER` 정보를 수신할 수 있습니다.
* `BroadcastReceiver`를 `AndroidManifest.xml`에 추가할 경우, 반드시 `KakaoAdInstallReferrerReceiver`를 추가한 다음 `BroadcastReceiver`를 추가해야 합니다.


## 이벤트 수집하기


###  이벤트 종류
Kakao AD SDK에서는 다음과 같은 이벤트를 제공합니다.<br/>
앱 설치 이벤트(AppInstall)와 실행 이벤트(AppLaunch)는 `KakaoAdTracker`를 초기화하는 시점에 자동적으로 수집됩니다.

| 이벤트 | 클래스| 자동 수집 유무 |
|---|---|---|
| 앱 설치 | AppInstall | O |
| 앱 실행 | AppLaunch | O |
| 가입 | CompleteRegistration  | X |
| 화면 방문 | PageView | X |
| 검색 | Search | X |
| 콘텐츠/상품 조회 | ViewContent | X |
| 장바구니 보기 | ViewCart | X |
| 구매 | Purchase | X |
| 인앱 구매 | InAppPurchase | X |


### KakaoAdTracker 초기화 하기
이벤트를 수집하기 위해서는 `KakaoAdTracker`를 초기화하는 과정이 필요합니다.<br/>
앱 실행 시점인 `Application#onCreate()` 또는 `Activity#onCreate()` 내에 다음과 같은 코드를 추가하여 `KakaoAdTracker`를 초기화합니다.

* **Kotlin**
    ```kotlin
    override fun onCreate() {
        super.onCreate()

        if (!KakaoAdTracker.isInitialized) {
            KakaoAdTracker.init(applicationContext, getString(R.string.kakao_ad_track_id))
        }
    }
    ```

* **Java**
    ```java
    @Override
    public void onCreate() {
        super.onCreate();

        if (!KakaoAdTracker.isInitialized()) {
            KakaoAdTracker.getInstance().init(getApplicationContext(), getString(R.string.kakao_ad_track_id));
        }
    }
    ```

### 이벤트 전송하기

#### Kotlin 샘플 코드

1. 가입
    ```kotlin
    val event = CompleteRegistration()
    event.tag = "Tag" // 분류
    event.send()
    ```

1. 페이지 방문
    ```kotlin
    val event = PageView()
    event.tag = "Tag" // 분류
    event.send()
    ```

1. 검색
    ```kotlin
    val event = Search()
    event.tag = "Tag" // 분류
    event.search_string = "Keyword" // 검색 문자열
    event.send()
    ```

1. 콘텐츠/상품 조회
    ```kotlin
    val event = ViewContent()
    event.tag = "Tag" // 분류
    event.content_id = "Content ID" // 상품 코드
    event.send()
    ```

1. 장바구니 보기
    ```kotlin
    val event = ViewCart()
    event.tag = "Tag" // 분류
    event.send()
    ```

1. 구매
    ```kotlin
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
    ```

1. 인앱 구매
    1. [Billing Library](https://developer.android.com/google/play/billing/billing_library_overview)를 사용하는 경우
        * `BillingClient` 생성 시, PurchasesUpdatedListener에서 `KakaoAdTracker.sendInAppPurchaseData(purchase.originalJson)` 호출
        ```kotlin
        BillingClient.newBuilder(context)
            .setListener (object : PurchasesUpdatedListener {
                override fun onPurchasesUpdated(responseCode: Int, purchases: MutableList<Purchase>?) {
                    if (responseCode == BillingResponse.OK && purchases != null) {
                        purchases.forEach { purchase ->
                            KakaoAdTracker.sendInAppPurchaseData(purchase.originalJson) // 인앱 구매 데이터 전송
                        }
                    }
                }
            })
            .build()
        ```
    2. [Billing Service AIDL](https://developer.android.com/google/play/billing/billing_library_overview)을 사용하는 경우
        * 인앱 구매 요청 후, `onActivityResult()`에서 `KakaoAdTracker.sendInAppBillingResult(data)` 호출
        ```kotlin
        override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
            super.onActivityResult(requestCode, resultCode, data)

            if (requestCode == REQUEST_CODE_BILLING && data != null) {
                KakaoAdTracker.sendInAppBillingResult(data) // 인앱 구매 데이터 전송
            }
        }
        ```
    3. 수동으로 전송하는 경우
        ```kotlin
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
        ```


## License

```
Copyright 2017 © Kakao Corp. All Rights Reserved.

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
```
