package com.radiosantidadapp.radiosantidad1025fm.services;

import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.SeekBar;

import com.radiosantidadapp.radiosantidad1025fm.R;
import com.radiosantidadapp.radiosantidad1025fm.utils.VerifyService;

import java.util.Timer;
import java.util.TimerTask;

public class ServiceTimerAction {
    private ServiceInstanciasComponents serviceInstanciasComponents;
    private ServiceDataRadio serviceDataRadio;
    private VerifyService verifyService;
    private ImageButton buttonPlayStop;
    private ImageButton buttonVolume;
    private SeekBar barVolume;
    private int time;
    private final Handler handler = new Handler();
    private Timer timer;

    public ServiceTimerAction(ServiceDataRadio serviceDataRadio, VerifyService verifyService, ServiceInstanciasComponents serviceInstanciasComponents, int time) {
        this.serviceDataRadio = serviceDataRadio;
        this.verifyService = verifyService;
        this.serviceInstanciasComponents = serviceInstanciasComponents;
        this.buttonPlayStop = serviceInstanciasComponents.getButtonPlayStop();
        this.buttonVolume = serviceInstanciasComponents.getButtonVolume();
        this.barVolume = serviceInstanciasComponents.getBarVolume();
        this.time = time;
        this.timer = new Timer();
    }

    public void initTask() {
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
