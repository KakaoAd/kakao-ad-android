# Kakao Ad Android SDK Guide
> 이 가이드는 Android Application에 이벤트 수집을 위한 가이드를 제공하고 있습니다.

## 1. 소개
- Android 어플리케이션에서 다음과 같은 이벤트를 수집하고 전송하고 있습니다. 수집된 데이터를 이용하여 다양한 목적으로 활용할 수 있습니다.
- 앱 설치 / 앱 실행 / 가입 완료
- 장바구니 보기 / 콘텐츠/상품 보기 / 검색
- 구매 / 앱 내 구매

## 2. SDK 설치
* SDK 설치에 관한 가이드는 Android Studio 기준이며, Eclipse에 대한 기술지원은 하지 않습니다.
* Android Studio 사용을 권장합니다.
* Android API 14 이상에서 사용가능합니다.

### 2.0. SDK 구성

* `tracker-library-X.X.X.aar` : SDK 라이브러리
* `tracker-sample/src/com/kakao/ad/tracker/sample` : 샘플 프로젝트

### 2.1. 트랙 ID(Track ID) 발급받기
카카오계정에서는 카카오SDK에서 사용하기 위한 고유한 식별값(Track Id)을 발급할 수 있습니다.
이벤트 수집 SDK를 이용하기 위해서는, 먼저 고유한 식별값이 필요합니다. <br>
[https://moment.kakao.com](https://moment.kakao.com/login) 에서 회원 가입 후, <br>
[https://moment.kakao.com/mypixel](https://moment.kakao.com/mypixel) 페이지에서 Track ID 발급 단계를 진행할 수 있습니다.

Track ID 발급이 완료된 후, 다음 단계의 안내에 따라 Kakao SDK를 설치해주세요.

### 2.2. 라이브러리 추가 (Android Studio 기준)
`build.gradle` 파일에 아래 내용을 추가합니다.
```gradle
repositories {
    maven { url 'http://devrepo.kakao.com:8088/nexus/content/groups/public/' }
}

dependencies {
    implementation 'com.kakao.ad:tracker:0.2.7@aar'
}
```

### 2.3. AndroidManifest.xml 설정
- 아래 두 가지 필수 권한을 AndroidManifist.xml 에 추가합니다.

```
<uses-permission android:name="android.permission.INTERNET" />
<uses-permission android:name="android.permission.ACCESS_NETWORK_STATE" />
```

### 2.4. Google Play Service SDK 설정
Google Play Store에 App을 개시하는 경우, App 내에 광고가 있다면 [반드시 Google Advertising ID를 사용하도록 규정이 변경](https://play.google.com/about/developer-content-policy.html#ADID)되었습니다.

이에 따라, SDK에서 Google Play Service SDK를 사용할 수 있는 경우에 한해 Google Advertising ID를 사용할 수 있도록 기능이 추가되었습니다.

참고로, Google Play Service SDK를 사용하지 않은 앱에 대해서는 _"광고가 Google Advertising ID를 사용하지 않았다"_ 는 이유로 Google Play Store에서 임의로 Reject 당할 수도 있습니다.

이에 따라, **<span style="color:red">SDK에서 Google Play Service SDK가 없이는 라이브러리를 사용할 수 없도록 변경</span>되었습니다.**

#### 2.4.1. Google Play Service SDK 설정 추가하기

Google Play Services SDK를 사용하기 위해, build.gradle을 다음과 같이 합니다.

1. 먼저 최상위 `build.gradle` 파일에 Google's Maven repository를 추가합니다.
```gradle
allprojects {
    repositories {
        google()

        // 사용 중인 Gradle 버전이 4.1보다 낮은 경우, 다음과 같이 대신 설정합니다.
        //
        // maven {
        //     url 'https://maven.google.com'
        // }
    }
}
```

2. App 모듈의 dependencies 블럭에 최신 버전의 `play-services` 라이브러리를 추가합니다.
```gradle
dependencies {
    implementation 'com.google.android.gms:play-services-base:+'
    implementation 'com.google.android.gms:play-services-ads:+'
}
```

3. 설정 후, 툴바의 **Sync Project with Gradle Files**를 눌러 변경사항을 반영합니다.

위 내용은 TrackerSample 프로젝트에 적용되어 있으니 참고 부탁드리며,
Google Play Service SDK에 대한 자세한 사항은 [Setting Up Google Play Services 링크](https://developers.google.com/android/guides/setup)를 참고 부탁드립니다.

## 3. 이벤트 트래킹 사용 가이드
다양한 이벤트를 트래킹을 제공합니다. 이벤트의 종류에 따라 자동으로 트래킹이 되는 것이 있고, 명시적으로 이벤트 트래킹을 해야하는 이벤트가 있습니다.

| 이벤트 | 클래스| 자동 트래킹 유무 |
|---|---|---|
| 앱 설치 | AppInstall | O |
| 앱 실행 | AppLaunch | O |
| 가입 | CompleteRegistration  | X |
| 검색 | Search | X |
| 콘텐츠/상품 조회 | ViewContent | X |
| 장바구니 보기 | ViewCart | X |
| 구매 | Purchase | X |
| InApp 구매 | InAppPurchase | X |

### 3.1. 초기화
```
KakaoAdTracker.getInstance().init(this, "track_id");
```

### 3.2. 앱 설치
별도의 Install Referrer를 처리 하지 않는다면 아래와 같이 `<application />` tag 안에 `receiver`를 추가한다.

#### 3.2.1. 신규 Receiver를 추가할 경우
```xml
<manifest xmlns:android="http://schemas.android.com/apk/res/android"
    package="your.app.package.namespace">
    <application>

        <!-- Application Tag 하위에 아래 내용을 추가한다 -->
        <receiver
            android:name="com.kakao.ad.tracker.KakaoAdInstallReferrerReceiver"
            android:exported="true">
            <intent-filter>
                <action android:name="com.android.vending.INSTALL_REFERRER">
                </action>
            </intent-filter>
        </receiver>
    </application>
</manifest>
```

#### 3.2.2. 기존에 Receiver를 사용 중일 경우
```xml
<manifest xmlns:android="http://schemas.android.com/apk/res/android"
    package="your.app.package.namespace">
    <application>

        <!-- Application Tag 하위에 아래 내용을 추가한다 -->
        <receiver
            android:name="com.kakao.ad.tracker.KakaoAdInstallReferrerReceiver"
            android:exported="true">
            <intent-filter>
                <action android:name="com.android.vending.INSTALL_REFERRER">
                </action>
            </intent-filter>

            <!--
                이벤트를 다른 Receiver에 전달하고 싶은 경우, 해당 Receiver의 Class를 value에 적어준다

                예) Tune의 com.tune.TuneTracker에 INSTALL_REFERRER 액션을 전달하고 싶은 경우,
                   아래와 같이 meta-data를 추가하면 된다.
            -->
            <meta-data android:name="tuneTracker" android:value="com.tune.TuneTracker"></meta-data>
        </receiver>
    </application>
</manifest>
```
### 3.3. 앱 실행
별 다른 설정 없이, 최초에 앱이 설치된 이후에 재실행시에는 앱 실행 이벤트가 전송된다.

### 3.4. 가입
```
Event event = new CompleteRegistration();
event.tag = "tag";

KakaoAdTracker.getInstance().sendEvent(event);
```

### 3.5. 검색
```
Event event = new Search();
event.tag = "tag";
((Search) event).search_string = "robot";

KakaoAdTracker.getInstance().sendEvent(event);
```

### 3.6. 콘텐츠/상품 조회
```
Event event = new ViewContent();
event.tag = "tag";
((ViewContent) event).content_id = "contentId";

KakaoAdTracker.getInstance().sendEvent(event);
```

### 3.7. 장바구니 보기
```
Event event = new ViewCart();
event.tag = "tag";

KakaoAdTracker.getInstance().sendEvent(event);
```

### 3.8. 구매
```
Event event = new Purchase();
event.tag = "tag";

// 구매상품 1
Product product1 = new Product();
product1.name = "product1";
product1.quantity = 1;
product1.price = 1.11;

// 구매상품 2
Product product2 = new Product();
product2.name = "product2";
product2.quantity = 2;
product2.price = 2.22;

((Purchase) event).setProducts(new ArrayList<>(Arrays.asList(product1, product2)));
((Purchase) event).currency = Currency.getInstance(Locale.KOREA);
((Purchase) event).total_price = 1000.40;
((Purchase) event).total_quantity = 10;

KakaoAdTracker.getInstance().sendEvent(event);
```

### 3.9 InApp 구매
```
Event event = new InAppPurchase();
event.tag = "tag";

KakaoAdTracker.getInstance().sendEvent(event);
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
