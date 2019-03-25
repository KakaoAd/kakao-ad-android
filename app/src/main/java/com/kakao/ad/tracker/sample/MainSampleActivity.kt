package com.kakao.ad.tracker.sample

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.kakao.ad.tracker.KakaoAdTracker

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
    }
}
