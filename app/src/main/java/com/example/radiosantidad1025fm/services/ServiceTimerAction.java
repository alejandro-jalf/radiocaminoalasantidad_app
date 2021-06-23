package com.example.radiosantidad1025fm.services;

import android.os.Handler;
import android.util.Log;
import java.util.Timer;
import java.util.TimerTask;

public class ServiceTimerAction {
    private ServiceDataRadio serviceDataRadio;
    private int time;
    private final Handler handler = new Handler();
    private Timer timer;

    public ServiceTimerAction(ServiceDataRadio serviceDataRadio, int time) {
        this.serviceDataRadio = serviceDataRadio;
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
