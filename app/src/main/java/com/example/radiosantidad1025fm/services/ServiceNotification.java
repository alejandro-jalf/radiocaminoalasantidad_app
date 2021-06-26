package com.example.radiosantidad1025fm.services;

import android.content.Context;
import android.service.media.MediaBrowserService;

import androidx.core.app.NotificationCompat;

public class ServiceNotification {
    private static final String CHANE = "";
    private NotificationCompat.Builder notification;
    private Context context;

   public ServiceNotification(Context context) {
       notification = new NotificationCompat.Builder(context, CHANE);

   }
}
