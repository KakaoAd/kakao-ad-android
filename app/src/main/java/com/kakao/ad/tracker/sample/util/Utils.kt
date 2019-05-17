package com.kakao.ad.tracker.sample.util

import android.app.Activity
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.IntentSender
import android.util.Log
import android.widget.Toast

fun logv(message: String) {
    Log.v("Sample", message)
}

fun loge(message: String) {
    Log.e("Sample", message)
}

fun Context.logAndToast(message: String) {
    Log.v("Sample", message)
    Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
}

@Throws(IntentSender.SendIntentException::class)
fun Activity.startPendingIntent(requestCode: Int, pendingIntent: PendingIntent) {
    startIntentSenderForResult(pendingIntent.intentSender, requestCode, Intent(), 0, 0, 0)
}