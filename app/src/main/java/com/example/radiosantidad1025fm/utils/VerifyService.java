package com.example.radiosantidad1025fm.utils;

import android.app.ActivityManager;
import android.content.Context;

public class VerifyService {

    private ActivityManager activityManager;
    private Context context;

    public VerifyService(Context context) {
        this.context = context;
    }

    public Boolean isServiceRunning(Class<?> serviceClass) {
        activityManager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : activityManager.getRunningServices(Integer.MAX_VALUE)) {
            if (serviceClass.getName().equals(service.service.getClassName()))
                return true;
        }
        return false;
    }
}
