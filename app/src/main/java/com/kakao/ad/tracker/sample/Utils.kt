package com.kakao.ad.tracker.sample

import android.content.Context
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