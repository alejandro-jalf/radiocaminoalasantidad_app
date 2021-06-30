package com.example.radiosantidad1025fm.services;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.app.TaskStackBuilder;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Icon;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.os.PowerManager;
import android.util.Log;
import android.widget.ImageButton;
import android.widget.Toast;
import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;

import com.example.radiosantidad1025fm.Configs.Config;
import com.example.radiosantidad1025fm.MainActivity;
import com.example.radiosantidad1025fm.R;
import com.example.radiosantidad1025fm.utils.VerifyService;

import java.io.IOException;

public class ServiceAudio extends Service implements MediaPlayer.OnPreparedListener, MediaPlayer.OnErrorListener, MediaPlayer.OnCompletionListener {
    private final int STATUS_INIT = 3;
    private int STATUS_ERROR = 2;
    private int STATUS_PLAY = 1;
    private int STATUS_STOP = 0;
    private int statusAudio;
    int statusMediaPlayer;
    protected MediaPlayer mediaPlayer;
    private Context context;
    private Config config;
    private ImageButton buttonPlayStop;
    private float volume;
    private Intent intent;
    private VerifyService verifyService;

    private NotificationCompat.Builder nBuilder;
    private final int ID_NOTIFICATION = 1996;
    private final String ID_CHANNEL = "Canal_radio";
    private final String NAME_CHANNEL = "Canal_radio";
    private NotificationManager notificationManager;
    private NotificationChannel channel;

    public ServiceAudio() { }

    public ServiceAudio(Context context, Config config, VerifyService verifyService, ImageButton buttonPlayStop) {
        this.context = context;
        this.config = config;
        this.buttonPlayStop = buttonPlayStop;
        this.volume = 0;
        this.verifyService = verifyService;
        intent = new Intent(context, ServiceAudio.class);
        verifyServiceRunnning();
    }

    @Override
    public void onCreate() {
        super.onCreate();
        final int STATUS_ERROR = 2;
        final int STATUS_PLAY = 1;
        int statusMediaPlayer = 0;
        int statusAudio;

        notificationManager =  (NotificationManager) getSystemService(getApplicationContext().NOTIFICATION_SERVICE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            channel = new  NotificationChannel(ID_CHANNEL, NAME_CHANNEL, NotificationManager.IMPORTANCE_HIGH);
            notificationManager.createNotificationChannel(channel);
        }
    }

    private void verifyServiceRunnning() {
        if (verifyService.isServiceRunning(ServiceAudio.class)) {
            buttonPlayStop.setImageResource(R.drawable.ic_baseline_stop_circle_55);
            Toast.makeText(context, "En reproduccion", Toast.LENGTH_SHORT).show();
        } else {
            buttonPlayStop.setImageResource(R.drawable.ic_baseline_play_circle_filled_55);
        }
    }

    private void initMediaPlayer(Config config) {
        try {
            mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
            mediaPlayer.setDataSource(config.getUrlSound());
            mediaPlayer.setOnPreparedListener(this);
            mediaPlayer.setOnErrorListener(this);
            mediaPlayer.prepareAsync();
            mediaPlayer.setWakeMode(getApplicationContext(), PowerManager.PARTIAL_WAKE_LOCK);
        } catch (IOException e) {
            e.printStackTrace();
            Toast.makeText(getApplicationContext(), "Fallo al con el servidor de radio init", Toast.LENGTH_SHORT).show();
        }
    }

    public void changeVolume(int progress) {
        volume = (float) progress/100;
        mediaPlayer.setVolume(volume, volume);
    }

    public void toggleAudio() {
        if (verifyService.isServiceRunning(ServiceAudio.class)) {
            buttonPlayStop.setImageResource(R.drawable.ic_baseline_play_circle_filled_55);
            context.stopService(intent);
            Toast.makeText(context, "Deteniendo......", Toast.LENGTH_SHORT).show();
        } else {
            context.startService(intent);
            buttonPlayStop.setImageResource(R.drawable.ic_baseline_stop_circle_55);
        }
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

        notificationBuilder = new NotificationCompat.Builder(this, ID_CHANNEL);
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

        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
        PendingIntent pendingIntent = TaskStackBuilder.create(getApplicationContext())
                .addNextIntent(intent)
                .getPendingIntent(10, PendingIntent.FLAG_UPDATE_CURRENT);

        Intent intentToggle = new Intent(getApplicationContext(), ServiceAudio.class);
        intentToggle.putExtra("event", Status);
        intentToggle.setAction("toggle");
        PendingIntent pendingIntentToggle = PendingIntent.getService(getApplicationContext(), 0, intentToggle, PendingIntent.FLAG_UPDATE_CURRENT);

        Intent intentClose = new Intent(getApplicationContext(), ServiceAudio.class);
        intentClose.putExtra("event", "Close");
        intentClose.setAction("Close");
        PendingIntent pendingIntentClose = PendingIntent.getService(getApplicationContext(), 0, intentClose, PendingIntent.FLAG_UPDATE_CURRENT);

        notificationBuilder.setContentIntent(pendingIntent);
        notificationBuilder.addAction(iconToggle, statusToggle, pendingIntentToggle);
        notificationBuilder.addAction(R.drawable.ic_baseline_close_30, "Cerrar", pendingIntentClose);

        return  notificationBuilder;
    }

    @Override
    public boolean onError(MediaPlayer mp, int what, int extra) {
        Toast.makeText(getApplicationContext(), "Sin conexion con el servidor", Toast.LENGTH_SHORT).show();
        statusMediaPlayer = STATUS_ERROR;

        nBuilder = createNotification("Stop", nBuilder);
        getApplicationContext().stopService(new Intent(getApplicationContext(), ServiceAudio.class));
        return false;
    }

    @Override
    public void onPrepared(MediaPlayer mp) {
        Toast.makeText(getApplicationContext(), "Reproduciendo.....", Toast.LENGTH_SHORT).show();
        statusMediaPlayer = STATUS_PLAY;
        mp.start();

        nBuilder = createNotification("Play", nBuilder);
        notificationManager.notify(ID_NOTIFICATION, nBuilder.build());
    }

    @Override
    public void onCompletion(MediaPlayer mp) {
        Toast.makeText(getApplicationContext(), "Transmicion terminada", Toast.LENGTH_SHORT).show();
        getApplicationContext().stopService(new Intent(getApplicationContext(), ServiceAudio.class));
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (intent.getExtras() != null) {
            String statusActual = intent.getExtras().getString("event");
            if (statusActual.equals("Play")) {
                statusAudio = STATUS_PLAY;
                nBuilder = createNotification("Stop", nBuilder);
                notificationManager.notify(ID_NOTIFICATION, nBuilder.build());
                getApplicationContext().stopService(new Intent(getApplicationContext(), ServiceAudio.class));
            } else if(statusActual.equals("Stop")) {
                statusAudio = STATUS_STOP;
                nBuilder = createNotification("Load", nBuilder);
                notificationManager.notify(ID_NOTIFICATION, nBuilder.build());
                mediaPlayer = new MediaPlayer();
                initMediaPlayer(new Config());
                Toast.makeText(getApplicationContext(), "Estableciendo conexion", Toast.LENGTH_SHORT).show();
            } else if(statusActual.equals("Close")) {
                statusAudio = STATUS_INIT;
                getApplicationContext().stopService(new Intent(getApplicationContext(), ServiceAudio.class));
            }
        } else {
            nBuilder = createNotification("Load", nBuilder);
            notificationManager.notify(ID_NOTIFICATION, nBuilder.build());
            mediaPlayer = new MediaPlayer();
            initMediaPlayer(new Config());
            Toast.makeText(getApplicationContext(), "Estableciendo conexion", Toast.LENGTH_SHORT).show();
        }
        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (statusMediaPlayer == STATUS_PLAY) mediaPlayer.stop();
        if (mediaPlayer != null) mediaPlayer.reset();
        mediaPlayer = null;

        nBuilder = createNotification("Stop", nBuilder);
        nBuilder.setContentInfo("Detenido");
        if(statusAudio == STATUS_INIT || statusAudio == STATUS_ERROR)
            notificationManager.cancel(ID_NOTIFICATION);
        else
            notificationManager.notify(ID_NOTIFICATION, nBuilder.build());
    }
}
