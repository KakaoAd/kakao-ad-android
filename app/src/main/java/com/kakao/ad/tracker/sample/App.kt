package com.kakao.ad.tracker.sample

import android.app.Application
import com.kakao.ad.tracker.KakaoAdTracker
import com.kakao.ad.tracker.sample.util.logv

class App : Application() {

    override fun onCreate() {
        super.onCreate()

        if (!KakaoAdTracker.isInitialized) {
            KakaoAdTracker.init(this, getString(R.string.kakao_ad_track_id))
        }

        logv("KakaoAdTracker.isInitialized? ${KakaoAdTracker.isInitialized}")
    }
}