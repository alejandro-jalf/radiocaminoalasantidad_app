package com.radiosantidadapp.radiosantidad1025fm.services;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

public class ServiceInternet {
    private ConnectivityManager connectivityManager;
    private NetworkInfo networkInfo;
    private Context context;

    public ServiceInternet(Context context) {
        this.context = context;
        this.connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
    }

    public Boolean testInternet() {
        networkInfo = connectivityManager.getActiveNetworkInfo();
        return networkInfo != null && networkInfo.isConnected();
    }

}
