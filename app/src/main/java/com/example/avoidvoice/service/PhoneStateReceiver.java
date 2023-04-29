package com.example.avoidvoice.service;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.telephony.TelephonyManager;
import android.widget.Toast;

public class PhoneStateReceiver extends BroadcastReceiver {

    private Intent intent;
    private static boolean serviceStarted = false;

    @Override
    public void onReceive(Context context, Intent intent) {
        try {
            String state = intent.getStringExtra(TelephonyManager.EXTRA_STATE);
            String incomingNumber = intent.getStringExtra(TelephonyManager.EXTRA_INCOMING_NUMBER);
            intent = new Intent(context, VoiceAvoidService.class);

            if(state.equals(TelephonyManager.EXTRA_STATE_RINGING)){
                //Toast.makeText(context,"Ringing State Number is -"+incomingNumber,Toast.LENGTH_SHORT).show();

            }
            if ((state.equals(TelephonyManager.EXTRA_STATE_OFFHOOK))){
                //Toast.makeText(context,"Received State",Toast.LENGTH_SHORT).show();
                if(!serviceStarted) {
                    serviceStarted = true;
                    context.startService(intent);
                }
            }
            if (state.equals(TelephonyManager.EXTRA_STATE_IDLE)){
                //Toast.makeText(context,"Idle State",Toast.LENGTH_SHORT).show();
                if(serviceStarted) {
                    serviceStarted = false;
                    context.stopService(intent);
                }
            }
        }
        catch (Exception e){
            e.printStackTrace();
        }
    }
}