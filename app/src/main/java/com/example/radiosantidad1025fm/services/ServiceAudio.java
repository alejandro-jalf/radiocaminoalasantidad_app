package com.example.radiosantidad1025fm.services;

import android.app.ActivityManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.IBinder;
import android.os.PowerManager;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.Toast;
import androidx.annotation.Nullable;
import com.example.radiosantidad1025fm.Configs.Config;
import com.example.radiosantidad1025fm.R;
import com.example.radiosantidad1025fm.utils.VerifyService;

import java.io.IOException;

public class ServiceAudio
        extends
            Service
        implements
            MediaPlayer.OnPreparedListener,
            MediaPlayer.OnErrorListener,
            MediaPlayer.OnCompletionListener {
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

    @Override
    public boolean onError(MediaPlayer mp, int what, int extra) {
        Toast.makeText(getApplicationContext(), "Sin conexion con el servidor", Toast.LENGTH_SHORT).show();
        mp.reset();
        statusMediaPlayer = STATUS_ERROR;
        getApplicationContext().stopService(new Intent(getApplicationContext(), ServiceAudio.class));
        return false;
    }

    @Override
    public void onPrepared(MediaPlayer mp) {
        Toast.makeText(getApplicationContext(), "Reproduciendo.....", Toast.LENGTH_SHORT).show();
        statusMediaPlayer = STATUS_PLAY;
        mp.start();
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
        mediaPlayer = new MediaPlayer();
        initMediaPlayer(new Config());
        Toast.makeText(getApplicationContext(), "Estableciendo conexion", Toast.LENGTH_SHORT).show();
        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (statusMediaPlayer == STATUS_PLAY)
            mediaPlayer.stop();
        mediaPlayer.reset();
        mediaPlayer = null;
    }
}
