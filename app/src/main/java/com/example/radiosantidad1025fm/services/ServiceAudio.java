package com.example.radiosantidad1025fm.services;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.IBinder;
import android.os.PowerManager;
import android.view.View;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.annotation.Nullable;

import com.example.radiosantidad1025fm.Configs.Config;
import com.example.radiosantidad1025fm.R;

import java.io.IOException;

public class ServiceAudio implements MediaPlayer.OnPreparedListener, MediaPlayer.OnErrorListener {
    private final int statusReady = 4;
    private final int statusInit = 3;
    private final int statusError = 2;
    private final int statusPlay = 1;
    private final int statusStop = 0;
    private int statusAudio;
    private MediaPlayer mediaPlayer;
    private Context context;
    private Config config;
    private ImageButton buttonPlayStop;

    public ServiceAudio(Context context, Config config, ImageButton buttonPlayStop) {
        this.context = context;
        this.config = config;
        this.buttonPlayStop = buttonPlayStop;
        buttonPlayStop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                toggleAudio();
            }
        });
        this.statusAudio = statusInit;
        mediaPlayer = new MediaPlayer();
        initMediaPlayer();
    }

    private void initMediaPlayer() {
        try {
            mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
            mediaPlayer.setDataSource(config.getUrlSound());
            mediaPlayer.setOnPreparedListener(this);
            mediaPlayer.setOnErrorListener(this);
            mediaPlayer.prepareAsync();
            mediaPlayer.setWakeMode(context, PowerManager.PARTIAL_WAKE_LOCK);
        } catch (IOException e) {
            e.printStackTrace();
            Toast.makeText(context, "Fallo al con el servidor de radio init", Toast.LENGTH_SHORT).show();
        }
    }

    public void toggleAudio() {
        if (statusAudio == statusInit)
            Toast.makeText(context, "Cargando audio.....", Toast.LENGTH_SHORT).show();
        else if(statusAudio == statusError) {
            Toast.makeText(context, "Fallo con el servidor de radio", Toast.LENGTH_SHORT).show();
            mediaPlayer.release();
            initMediaPlayer();
            statusAudio = statusInit;
        }
        else if (statusAudio == statusReady) {
            statusAudio = statusPlay;
            mediaPlayer.start();
            buttonPlayStop.setBackgroundResource(R.drawable.ic_baseline_stop_circle_35);
        } else if (statusAudio == statusStop) {
            mediaPlayer.prepareAsync();
            Toast.makeText(context, "Preparando para reproducir", Toast.LENGTH_LONG).show();
            statusAudio = statusInit;
        } else if (statusAudio == statusPlay) {
            statusAudio = statusStop;
            mediaPlayer.stop();
            buttonPlayStop.setBackgroundResource(R.drawable.ic_baseline_play_circle_filled_35);
        }
    }

    /*@Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }*/

    @Override
    public boolean onError(MediaPlayer mp, int what, int extra) {
        buttonPlayStop.setBackgroundResource(R.drawable.ic_baseline_play_circle_filled_35);
        Toast.makeText(context, "Fallo al cargar audio", Toast.LENGTH_LONG).show();
        statusAudio = statusError;
        return false;
    }

    @Override
    public void onPrepared(MediaPlayer mp) {
        statusAudio = statusReady;
    }
}
