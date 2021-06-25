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
import android.widget.SeekBar;
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
    private SeekBar barVolume;
    private float volumen;

    public ServiceAudio(Context context, Config config, ImageButton buttonPlayStop, SeekBar barVolume) {
        this.context = context;
        this.config = config;
        this.barVolume = barVolume;
        this.buttonPlayStop = buttonPlayStop;
        buttonPlayStop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                toggleAudio();
            }
        });
        this.statusAudio = statusInit;
        this.volumen = 0;
        mediaPlayer = new MediaPlayer();
        initMediaPlayer();
        Toast.makeText(context, "Estableciendo conexion.......", Toast.LENGTH_LONG).show();
        setChangeVolume();
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

    private void setChangeVolume() {
        barVolume.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                volumen = (float) progress/100;
                mediaPlayer.setVolume(volumen, volumen);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {}

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {}
        });
    }

    public void toggleAudio() {
        if (statusAudio == statusInit)
            Toast.makeText(context, "Cargando audio, intentelo en unos segundos mas.....", Toast.LENGTH_SHORT).show();
        else if(statusAudio == statusError) {
            Toast.makeText(context, "Servidor sin conexion", Toast.LENGTH_SHORT).show();
            buttonPlayStop.setImageResource(R.drawable.ic_baseline_play_circle_filled_55);
        }
        else if (statusAudio == statusReady) {
            statusAudio = statusPlay;
            mediaPlayer.start();
            mediaPlayer.seekTo(0);
            buttonPlayStop.setImageResource(R.drawable.ic_baseline_stop_circle_55);
        } else if (statusAudio == statusStop) {
            // mediaPlayer.prepareAsync();
            // Toast.makeText(context, "Preparando para reproducir.....", Toast.LENGTH_LONG).show();
            statusAudio = statusPlay;
        } else if (statusAudio == statusPlay) {
            statusAudio = statusInit;
            mediaPlayer.stop();
            //mediaPlayer.reset();
            mediaPlayer.prepareAsync();
            buttonPlayStop.setImageResource(R.drawable.ic_baseline_play_circle_filled_55);
        }
    }

    /*@Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }*/

    @Override
    public boolean onError(MediaPlayer mp, int what, int extra) {
        buttonPlayStop.setBackgroundResource(R.drawable.ic_baseline_play_circle_filled_55);
        Toast.makeText(context, "Sin conexion con el servidor", Toast.LENGTH_LONG).show();
        statusAudio = statusError;
        return false;
    }

    @Override
    public void onPrepared(MediaPlayer mp) {
        Toast.makeText(context, "Listo para reproducir", Toast.LENGTH_LONG).show();
        statusAudio = statusReady;
    }
}
