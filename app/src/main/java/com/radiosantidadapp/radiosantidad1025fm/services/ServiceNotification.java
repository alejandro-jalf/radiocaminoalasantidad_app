package com.radiosantidadapp.radiosantidad1025fm.services;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.TaskStackBuilder;
import android.content.Context;
import android.content.Intent;
import android.os.Build;

import androidx.core.app.NotificationCompat;

import com.radiosantidadapp.radiosantidad1025fm.MainActivity;
import com.radiosantidadapp.radiosantidad1025fm.R;

public class ServiceNotification {
    private Context context;
    private Intent intentBackround;
    private NotificationCompat.Builder nBuilder;
    private final int ID_NOTIFICATION = 1996;
    private final String ID_CHANNEL = "Canal_radio";
    private final String NAME_CHANNEL = "Canal_radio";
    private NotificationManager notificationManager;
    private NotificationChannel channel;

   public ServiceNotification(Context context) {
       this.intentBackround = new Intent(context, ServiceBackground.class);
       this.context = context;
       notificationManager =  (NotificationManager) context.getSystemService(context.NOTIFICATION_SERVICE);
       if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
           channel = new  NotificationChannel(ID_CHANNEL, NAME_CHANNEL, NotificationManager.IMPORTANCE_HIGH);
           notificationManager.createNotificationChannel(channel);
       }
   }

   public void showNotification(String status) {
       nBuilder = createNotification(status, nBuilder);
       notificationManager.notify(ID_NOTIFICATION, nBuilder.build());
   }

   public void hiddeNotification() {
       notificationManager.cancel(ID_NOTIFICATION);
   }

    private NotificationCompat.Builder createNotification(String Status, NotificationCompat.Builder notificationBuilder) {
        int iconToggle;
        String statusSound;
        String statusToggle;

        if (Status.equals("Play")) {
            iconToggle = R.drawable.ic_baseline_stop_circle_30;
            statusSound = "Reproduciendo";
            statusToggle = "Detener";
        } else if (Status.equals("Stop")) {
            iconToggle = R.drawable.ic_baseline_play_circle_filled_30;
            statusSound = "Detenido";
            statusToggle = "Reproducir";
        } else {
            iconToggle = R.drawable.ic_baseline_access_time_filled_30;
            statusSound = "Cargando";
            statusToggle = "Espere....";
        }

        notificationBuilder = new NotificationCompat.Builder(context, ID_CHANNEL);
        notificationBuilder.setAutoCancel(false);
        notificationBuilder.setSmallIcon(R.mipmap.ic_logo);
        notificationBuilder.setTicker("Radio Santidad");
        notificationBuilder.setPriority(Notification.PRIORITY_HIGH);
        notificationBuilder.setWhen(System.currentTimeMillis());
        notificationBuilder.setContentTitle("Radio Santidad 102.5 FM");
        notificationBuilder.setContentText("La expresion de la verdad");
        notificationBuilder.setContentInfo(statusSound);
        notificationBuilder.setVisibility(NotificationCompat.VISIBILITY_PUBLIC);
        notificationBuilder.setOnlyAlertOnce(true);
        notificationBuilder.setOngoing(true);

        Intent intent = new Intent(context, MainActivity.class);
        PendingIntent pendingIntent = TaskStackBuilder.create(context)
                .addNextIntent(intent)
                .getPendingIntent(10, PendingIntent.FLAG_UPDATE_CURRENT);

        Intent intentToggle = intentBackround;
        intentToggle.putExtra("event", Status);
        intentToggle.setAction("toggle");
        PendingIntent pendingIntentToggle = PendingIntent.getService(context, 0, intentToggle, PendingIntent.FLAG_UPDATE_CURRENT);

        Intent intentClose = intentBackround;
        intentClose.putExtra("event", "Close");
        intentClose.setAction("Close");
        PendingIntent pendingIntentClose = PendingIntent.getService(context, 0, intentClose, PendingIntent.FLAG_UPDATE_CURRENT);

        notificationBuilder.setContentIntent(pendingIntent);
        notificationBuilder.addAction(iconToggle, statusToggle, pendingIntentToggle);
        notificationBuilder.addAction(R.drawable.ic_baseline_close_30, "Cerrar", pendingIntentClose);

        return  notificationBuilder;
    }
}
