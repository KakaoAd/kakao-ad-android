package com.kakao.ad.tracker.sample;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

public class InstallReferrerReceiver extends BroadcastReceiver {
 
    @Override
    public void onReceive(Context context, Intent intent) {

        String referrerString = "";
        if (intent.getAction().equals("com.android.vending.INSTALL_REFERRER")) {
            Bundle extras = intent.getExtras();
            referrerString = extras.getString("referrer");
            Log.d("Referrer Sample", "REFERRER: " + referrerString);
        }
    }

}
