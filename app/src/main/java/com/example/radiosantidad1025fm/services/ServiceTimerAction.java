package com.example.radiosantidad1025fm.services;

import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.SeekBar;

import com.example.radiosantidad1025fm.R;
import com.example.radiosantidad1025fm.utils.VerifyService;

import java.util.Timer;
import java.util.TimerTask;

public class ServiceTimerAction {
    private ServiceDataRadio serviceDataRadio;
    private VerifyService verifyService;
    private ImageButton buttonPlayStop;
    private ImageButton buttonVolume;
    private SeekBar barVolume;
    private int time;
    private final Handler handler = new Handler();
    private Timer timer;

    public ServiceTimerAction(ServiceDataRadio serviceDataRadio, VerifyService verifyService, ImageButton buttonPlayStop, ImageButton buttonVolume, SeekBar barVolume, int time) {
        this.serviceDataRadio = serviceDataRadio;
        this.verifyService = verifyService;
        this.buttonPlayStop = buttonPlayStop;
        this.buttonVolume = buttonVolume;
        this.barVolume = barVolume;
        this.time = time;
        this.timer = new Timer();
        taskRun();
    }

    private void taskRun() {
        TimerTask task = new TimerTask() {
            @Override
            public void run() {
                handler.post(new Runnable() {
                    public void run() {
                        try {
                            serviceDataRadio.getDataRadio();
                            if (verifyService.isServiceRunning(ServiceAudio.class)) {
                                buttonPlayStop.setImageResource(R.drawable.ic_baseline_stop_circle_55);
                                buttonVolume.setVisibility(View.VISIBLE);
                            } else {
                                buttonPlayStop.setImageResource(R.drawable.ic_baseline_play_circle_filled_55);
                                if (barVolume.getVisibility() == View.VISIBLE)
                                    buttonVolume.performClick();
                                buttonVolume.setVisibility(View.GONE);
                            }
                        } catch (Exception e) {
                            Log.e("error", e.getMessage());
                        }
                    }
                });
            }
        };

        timer.schedule(task, 0, time);
    }
}
