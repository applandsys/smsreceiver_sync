package com.tencrm.smsreceiver2022;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.telephony.SmsMessage;

public class SMSManager extends BroadcastReceiver {

    private static final String TAG = "Message recieved";

    @Override
    public void onReceive(Context context, Intent intent) {
        if(intent.getAction().equals("android.provider.Telephony.SMS_RECEIVED")) {
            Bundle bundle = intent.getExtras();
            SmsMessage[] msgs = null;
            if (bundle != null){

                Object[] pdus = (Object[]) bundle.get("pdus");
                msgs = new SmsMessage[pdus.length];
                // int i =  0;
                for(int i=0; i<msgs.length; i++){
                    msgs[i] = SmsMessage.createFromPdu((byte[])pdus[i]);
                    String msgBody = msgs[i].getMessageBody();
                    String msgAddress = msgs[i].getOriginatingAddress();

                    Intent smsIntent=new Intent(context,MainActivity.class);
                    // smsIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    smsIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK |Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    smsIntent.putExtra("from", msgAddress);
                    smsIntent.putExtra("message", msgBody);
                    context.startActivity(smsIntent);

                }

            }
        }
    }
}
