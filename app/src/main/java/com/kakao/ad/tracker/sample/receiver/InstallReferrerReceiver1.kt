package com.kakao.ad.tracker.sample.receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.kakao.ad.tracker.sample.util.logv

class InstallReferrerReceiver1 : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action == "com.android.vending.INSTALL_REFERRER") {
            logv(
                "\"${javaClass.simpleName}\" receive the \"com.android.vending.INSTALL_REFERRER\" action. " +
                        "\"referrer\" is ${intent.getStringExtra("referrer")}"
            )
        }
    }
}
