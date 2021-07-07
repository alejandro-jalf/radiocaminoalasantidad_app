package com.radiosantidadapp.radiosantidad1025fm.services;

import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.wifi.WifiManager;
import android.os.PowerManager;

import android.widget.Toast;


import com.radiosantidadapp.radiosantidad1025fm.Configs.Config;


import java.io.IOException;

public class ServiceAudio implements MediaPlayer.OnPreparedListener, MediaPlayer.OnErrorListener, MediaPlayer.OnCompletionListener {
    private final int STATUS_INIT = 3;
    private final int STATUS_ERROR = 2;
    private final int STATUS_PLAY = 1;
    private final int STATUS_STOP = 0;
    private int statusAudio;
    private int statusMediaPlayer;
    private ServiceNotification serviceNotification;
    private MediaPlayer mediaPlayer;
    private Context context;
    private Config config;
    private Intent intentBackround;
    private WifiManager.WifiLock wifiLock;

    public ServiceAudio() { }

    public ServiceAudio(Context context, ServiceNotification serviceNotification, Config config) {
        this.context = context;
        this.config = config;
        this.serviceNotification = serviceNotification;
        this.intentBackround = new Intent(context, ServiceBackground.class);
        this.wifiLock = ((WifiManager) context.getSystemService(Context.WIFI_SERVICE))
                .createWifiLock(WifiManager.WIFI_MODE_FULL, "mylock");
    }

    public void initMediaPlayer() {
        try {
            mediaPlayer = new MediaPlayer();
            mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
            mediaPlayer.setDataSource(config.getUrlSound());
            mediaPlayer.setOnPreparedListener(this);
            mediaPlayer.setOnErrorListener(this);
            mediaPlayer.prepareAsync();
            mediaPlayer.setWakeMode(context, PowerManager.PARTIAL_WAKE_LOCK);
            wifiLock.acquire();
        } catch (IOException e) {
            e.printStackTrace();
            Toast.makeText(context, "Fallo al con el servidor de radio init", Toast.LENGTH_SHORT).show();
        }
    }

    public void stopMediaPlayer() {
        if (mediaPlayer != null) mediaPlayer.reset();
        this.mediaPlayer = null;
        if (wifiLock != null) wifiLock.release();
        wifiLock = null;
    }

    public void setVolume(Float volume) {
        if (mediaPlayer != null)
            mediaPlayer.setVolume(volume, volume);
    }

    @Override
    public boolean onError(MediaPlayer mp, int what, int extra) {
        Toast.makeText(context, "Sin conexion con el servidor", Toast.LENGTH_SHORT).show();
        statusMediaPlayer = STATUS_ERROR;
        context.stopService(intentBackround);
        return false;
    }

    @Override
    public void onPrepared(MediaPlayer mp) {
        Toast.makeText(context, "Reproduciendo.....", Toast.LENGTH_SHORT).show();
        statusMediaPlayer = STATUS_PLAY;
        mp.start();
        serviceNotification.showNotification("Play");
    }

    @Override
    public void onCompletion(MediaPlayer mp) {
        Toast.makeText(context, "Transmicion terminada", Toast.LENGTH_SHORT).show();
        context.stopService(intentBackround);
    }
}
