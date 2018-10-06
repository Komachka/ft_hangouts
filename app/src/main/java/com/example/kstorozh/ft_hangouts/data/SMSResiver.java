package com.example.kstorozh.ft_hangouts.data;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

public class SMSResiver extends BroadcastReceiver {
    private static final String LOG_TAG = SMSResiver.class.getSimpleName();

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.d(LOG_TAG, "In onRecove method FINALY!!!");
    }
}
