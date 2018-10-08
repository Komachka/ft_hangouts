package com.example.kstorozh.ft_hangouts.data;

import android.annotation.TargetApi;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.telephony.SmsMessage;
import android.util.Log;
import android.widget.Toast;

import com.example.kstorozh.ft_hangouts.ReadSMS;

public class SMSResiver extends BroadcastReceiver {
    private static final String LOG_TAG = SMSResiver.class.getSimpleName();
    public static final String SMS_BUNDLE = "pdus";


    @Override
    public void onReceive(Context context, Intent intent) {
        Log.d(LOG_TAG, "In onRecove method FINALY!!!");
        Bundle intentExtras = intent.getExtras();
        if (intentExtras != null)
        {
            Object [] sms = (Object[]) intentExtras.get(SMS_BUNDLE);
            String smsMessStr = "";
            String format = intentExtras.getString("format");
            for (Object o: sms) {
                SmsMessage smsMessage = SmsMessage.createFromPdu((byte[]) o);
                String adress = smsMessage.getOriginatingAddress();
                String body = smsMessage.getMessageBody().toString();
                smsMessStr += "SMS from " + adress + "\n";
                smsMessStr += body + "\n";
            }
            Toast.makeText(context, smsMessStr, Toast.LENGTH_LONG).show();

            //Update UI with messages
            ReadSMS inst = ReadSMS.instance();
            inst.updateList(smsMessStr);
        }
    }
}
