package com.example.radiosantidad1025fm.services;

import android.os.Handler;
import android.util.Log;
import android.widget.ImageButton;

import com.example.radiosantidad1025fm.R;
import com.example.radiosantidad1025fm.utils.VerifyService;

import java.util.Timer;
import java.util.TimerTask;

public class ServiceTimerAction {
    private ServiceDataRadio serviceDataRadio;
    private VerifyService verifyService;
    private ImageButton buttonPlayStop;
    private int time;
    private final Handler handler = new Handler();
    private Timer timer;

    public ServiceTimerAction(ServiceDataRadio serviceDataRadio, VerifyService verifyService, ImageButton buttonPlayStop, int time) {
        this.serviceDataRadio = serviceDataRadio;
        this.verifyService = verifyService;
        this.buttonPlayStop = buttonPlayStop;
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
                            if (verifyService.isServiceRunning(ServiceAudio.class))
                                buttonPlayStop.setImageResource(R.drawable.ic_baseline_stop_circle_55);
                            else
                                buttonPlayStop.setImageResource(R.drawable.ic_baseline_play_circle_filled_55);
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
