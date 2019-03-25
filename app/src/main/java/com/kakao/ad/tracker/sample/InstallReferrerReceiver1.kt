package com.kakao.ad.tracker.sample

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent

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
