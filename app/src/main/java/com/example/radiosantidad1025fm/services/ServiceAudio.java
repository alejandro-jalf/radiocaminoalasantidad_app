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
import android.view.View;
import android.widget.ImageButton;
import android.widget.SeekBar;
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
    private final int STATUS_ERROR = 2;
    private final int STATUS_PLAY = 1;
    private final int STATUS_STOP = 0;
    private int statusAudio;
    private int statusMediaPlayer;
    private ServiceInstanciasComponents serviceInstanciasComponents;
    private ServiceNotification serviceNotification;
    private VerifyService verifyService;
    private MediaPlayer mediaPlayer;
    private Context context;
    private Config config;
    private ImageButton buttonPlayStop;
    private ImageButton buttonVolume;
    private SeekBar barVolume;
    private float volume;
    private Intent intent;

    public ServiceAudio() { }

    public ServiceAudio(Context context, Config config, VerifyService verifyService, ServiceNotification serviceNotification, ServiceInstanciasComponents serviceInstanciasComponents) {
        this.context = context;
        this.verifyService = verifyService;
        this.serviceNotification = serviceNotification;
        this.serviceInstanciasComponents = serviceInstanciasComponents;
        this.buttonPlayStop = serviceInstanciasComponents.getButtonPlayStop();
        this.buttonVolume = serviceInstanciasComponents.getButtonVolume();
        this.barVolume = serviceInstanciasComponents.getBarVolume();
        intent = new Intent(context, ServiceAudio.class);
        this.volume = 0;
        verifyServiceRunnning();
    }

    @Override
    public void onCreate() {
        super.onCreate();
        this.config = new Config();
        serviceNotification = new ServiceNotification(getApplicationContext());
    }

    private void verifyServiceRunnning() {
        if (verifyService.isServiceRunning(ServiceAudio.class)) {
            buttonPlayStop.setImageResource(R.drawable.ic_baseline_stop_circle_55);
            Toast.makeText(context, "En reproduccion", Toast.LENGTH_SHORT).show();
            buttonVolume.setVisibility(View.VISIBLE);
        } else {
            buttonVolume.setVisibility(View.GONE);
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

    public void toggleAudio() {
        if (verifyService.isServiceRunning(ServiceAudio.class)) {
            buttonPlayStop.setImageResource(R.drawable.ic_baseline_play_circle_filled_55);
            context.stopService(intent);
            if (barVolume.getVisibility() == View.VISIBLE)
                buttonVolume.performClick();
            buttonVolume.setVisibility(View.GONE);
            Toast.makeText(context, "Deteniendo......", Toast.LENGTH_SHORT).show();
        } else {
            context.startService(intent);
            buttonPlayStop.setImageResource(R.drawable.ic_baseline_stop_circle_55);
        }
    }

    @Override
    public boolean onError(MediaPlayer mp, int what, int extra) {
        Toast.makeText(getApplicationContext(), "Sin conexion con el servidor", Toast.LENGTH_SHORT).show();
        statusMediaPlayer = STATUS_ERROR;
        getApplicationContext().stopService(new Intent(getApplicationContext(), ServiceAudio.class));
        return false;
    }

    @Override
    public void onPrepared(MediaPlayer mp) {
        Toast.makeText(getApplicationContext(), "Reproduciendo.....", Toast.LENGTH_SHORT).show();
        statusMediaPlayer = STATUS_PLAY;
        mp.start();
        serviceNotification.showNotification("Play");
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
                serviceNotification.showNotification("Stop");
                getApplicationContext().stopService(new Intent(getApplicationContext(), ServiceAudio.class));
            } else if(statusActual.equals("Stop")) {
                statusAudio = STATUS_STOP;
                serviceNotification.showNotification("Load");
                mediaPlayer = new MediaPlayer();
                initMediaPlayer(config);
                Toast.makeText(getApplicationContext(), "Estableciendo conexion", Toast.LENGTH_SHORT).show();
            } else if(statusActual.equals("Close")) {
                statusAudio = STATUS_INIT;
                getApplicationContext().stopService(new Intent(getApplicationContext(), ServiceAudio.class));
            } else if (statusActual.equals("Volume")){
                volume = intent.getExtras().getFloat("volume");
                if (mediaPlayer != null)
                    mediaPlayer.setVolume(volume, volume);
            }
        } else {
            serviceNotification.showNotification("Load");
            mediaPlayer = new MediaPlayer();
            initMediaPlayer(config);
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

        if(statusAudio == STATUS_INIT || statusAudio == STATUS_ERROR) {
            serviceNotification.hiddeNotification();
            serviceNotification = null;
        }
        else
            serviceNotification.showNotification("Stop");
    }
}
