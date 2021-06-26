package com.example.radiosantidad1025fm.services;

import android.app.ActivityManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.IBinder;
import android.os.Parcelable;
import android.os.PowerManager;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.annotation.Nullable;

import com.example.radiosantidad1025fm.Configs.Config;
import com.example.radiosantidad1025fm.R;

import java.io.IOException;
import java.io.Serializable;
import java.lang.reflect.Field;

public class ServiceAudio extends Service implements MediaPlayer.OnPreparedListener, MediaPlayer.OnErrorListener {
    private final int STATUS_INIT = 3;
    private final int STATUS_ERROR = 2;
    private final int STATUS_PLAY = 1;
    private final int STATUS_STOP = 0;
    private int statusAudio;
    protected MediaPlayer mediaPlayer;
    private Context context;
    private Config config;
    private ImageButton buttonPlayStop;
    private float volume;
    private Intent intent;
    private ActivityManager activityManager;

    public ServiceAudio() { }

    public ServiceAudio(Context context, Config config, ImageButton buttonPlayStop) {
        this.context = context;
        this.config = config;
        this.buttonPlayStop = buttonPlayStop;
        this.volume = 0;
        intent = new Intent(context, ServiceAudio.class);
        verifyServiceRunnning();
    }

    private void verifyServiceRunnning() {
        if (isServiceRunning(ServiceAudio.class)) {
            statusAudio = STATUS_PLAY;
            buttonPlayStop.setImageResource(R.drawable.ic_baseline_stop_circle_55);
            Toast.makeText(context, "En reproduccion", Toast.LENGTH_SHORT).show();
        } else {
            buttonPlayStop.setImageResource(R.drawable.ic_baseline_play_circle_filled_55);
            statusAudio = STATUS_STOP;
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
        if (isServiceRunning(ServiceAudio.class)) {
            buttonPlayStop.setImageResource(R.drawable.ic_baseline_play_circle_filled_55);
            context.stopService(intent);
            Toast.makeText(context, "Deteniendo......", Toast.LENGTH_SHORT).show();
        } else {
            context.startService(intent);
            buttonPlayStop.setImageResource(R.drawable.ic_baseline_stop_circle_55);
        }
    }



    private Boolean isServiceRunning(Class<?> serviceClass) {
        activityManager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : activityManager.getRunningServices(Integer.MAX_VALUE)) {
            if (serviceClass.getName().equals(service.service.getClassName()))
                return true;
        }
        return false;
    }

    @Override
    public boolean onError(MediaPlayer mp, int what, int extra) {
        Toast.makeText(getApplicationContext(), "Sin conexion con el servidor", Toast.LENGTH_SHORT).show();
        mp.reset();
        context.stopService(intent);
        return false;
    }

    @Override
    public void onPrepared(MediaPlayer mp) {
        Toast.makeText(getApplicationContext(), "Reproduciendo.....", Toast.LENGTH_SHORT).show();
        mp.start();
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        mediaPlayer = new MediaPlayer();
        initMediaPlayer(new Config());
        Log.d("onStart: ", "On start Service");
        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mediaPlayer.stop();
        mediaPlayer.reset();
    }
}
