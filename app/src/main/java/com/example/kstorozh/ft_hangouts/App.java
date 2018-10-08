package com.example.kstorozh.ft_hangouts;

import android.app.Application;
import android.content.IntentFilter;
import android.provider.Telephony;
import android.util.Log;

import com.example.kstorozh.ft_hangouts.data.SMSResiver;

public class App extends Application {


    private static final String LOG_TAG = App.class.getSimpleName();
    public static final int SMS_PERMISSION_CODE = 20;


    private SMSResiver smsResiver;
    @Override
    public void onCreate() {
        super.onCreate();


        smsResiver = new SMSResiver();
        IntentFilter filter = new IntentFilter();
        filter.addAction("android.provider.Telephony.SMS_RECEIVED");
        filter.setPriority(999);
        registerReceiver(smsResiver, filter);
    }

    @Override
    public void onTerminate() {
        super.onTerminate();
    }
}
