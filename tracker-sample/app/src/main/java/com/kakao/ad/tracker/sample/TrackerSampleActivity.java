package com.kakao.ad.tracker.sample;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.android.vending.billing.IInAppBillingService;
import com.kakao.ad.common.json.CompleteRegistration;
import com.kakao.ad.common.json.Event;
import com.kakao.ad.common.json.InAppPurchase;
import com.kakao.ad.common.json.PageView;
import com.kakao.ad.common.json.Product;
import com.kakao.ad.common.json.Purchase;
import com.kakao.ad.common.json.Search;
import com.kakao.ad.common.json.ViewCart;
import com.kakao.ad.common.json.ViewContent;
import com.kakao.ad.tracker.KakaoAdTracker;
import com.kakao.ad.tracker.sample.util.IabHelper;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Currency;
import java.util.Locale;

public class TrackerSampleActivity extends AppCompatActivity {
    IInAppBillingService mService;
    ServiceConnection mServiceConn = new ServiceConnection() {
        @Override
        public void onServiceDisconnected(ComponentName name) {
            mService = null;
        }

        @Override
        public void onServiceConnected(ComponentName name,
                                       IBinder service) {
            mService = IInAppBillingService.Stub.asInterface(service);
        }
    };

    private Button search;
    private Button viewContent;
    private Button completeRegistration;
    private Button viewCart;
    private Button purchase;
    private Button inAppPurchase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_acts_sample);

        KakaoAdTracker.getInstance().init(this, "testApiKey");

        search = findViewById(R.id.search_button);
        viewContent = findViewById(R.id.view_content_button);
        completeRegistration = findViewById(R.id.complete_registration_button);
        viewCart = findViewById(R.id.view_cart_button);
        purchase = findViewById(R.id.purchase_button);
        inAppPurchase = findViewById(R.id.in_app_purchase_button);

        View.OnClickListener listener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Event event = null;

                switch (v.getId()) {
                    case R.id.search_button:
                        event = new Search();
                        break;

                    case R.id.view_content_button:
                        event = new ViewContent();
                        break;

                    case R.id.complete_registration_button:
                        event = new CompleteRegistration();
                        break;

                    case R.id.view_cart_button:
                        event = new ViewCart();
                        break;

                    case R.id.purchase_button:
                        event = new Purchase();

                        Product product1 = new Product();
                        product1.name = "product1";
                        product1.quantity = 1;
                        product1.price = 1.11;

                        Product product2 = new Product();
                        product2.name = "product2";
                        product2.quantity = 2;
                        product2.price = 2.22;

                        ((Purchase) event).setProducts(new ArrayList<>(Arrays.asList(product1, product2)));
                        ((Purchase) event).currency = Currency.getInstance(Locale.KOREA);

                        break;

                    case R.id.in_app_purchase_button:
                        event = new InAppPurchase();
                        break;
                }

                KakaoAdTracker.getInstance().sendEvent(event);
            }
        };

        search.setOnClickListener(listener);
        viewContent.setOnClickListener(listener);
        completeRegistration.setOnClickListener(listener);
        viewCart.setOnClickListener(listener);
        purchase.setOnClickListener(listener);
        inAppPurchase.setOnClickListener(listener);

        // 인앱 구매 이벤트 측정
        Intent serviceIntent = new Intent("com.android.vending.billing.InAppBillingService.BIND");
        serviceIntent.setPackage("com.android.vending");
        bindService(serviceIntent, mServiceConn, Context.BIND_AUTO_CREATE);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        if (mService != null) {
            unbindService(mServiceConn);
        }
    }
}
