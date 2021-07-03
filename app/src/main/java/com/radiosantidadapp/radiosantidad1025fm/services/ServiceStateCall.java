package com.radiosantidadapp.radiosantidad1025fm.services;

import android.content.Context;
import android.content.Intent;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.widget.Toast;

public class ServiceStateCall {
    private PhoneStateListener phoneStateListener;
    private Context context;
    private Intent intentAudio;

    public ServiceStateCall(Context context) {
        this.context = context;
        this. intentAudio = new Intent(context, ServiceAudio.class);
        initStateListener();
    }

    private void initStateListener() {
        phoneStateListener = new PhoneStateListener() {
            @Override
            public void onCallStateChanged(int state, String incomingNumber) {
                super.onCallStateChanged(state, incomingNumber);

                switch (state) {
                    case TelephonyManager.CALL_STATE_IDLE:
                        // context.startService(intentAudio);
                        break;
                    case TelephonyManager.CALL_STATE_RINGING:
                        context.stopService(intentAudio);
                        Toast.makeText(context, "Llamada entrante", Toast.LENGTH_SHORT).show();
                        break;
                    case TelephonyManager.CALL_STATE_OFFHOOK: // Se incia la llamada
                        context.stopService(intentAudio);
                        break;
                }
            }
        };
    }

    public PhoneStateListener getPhoneStateListener() {
        return phoneStateListener;
    }
}
