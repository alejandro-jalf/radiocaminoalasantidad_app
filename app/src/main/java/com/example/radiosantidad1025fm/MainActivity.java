package com.example.radiosantidad1025fm;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.SeekBar;
import android.widget.TextView;

import com.example.radiosantidad1025fm.Configs.Config;
import com.example.radiosantidad1025fm.services.ServiceAudio;
import com.example.radiosantidad1025fm.services.ServiceDataRadio;
import com.example.radiosantidad1025fm.services.ServiceTimerAction;

public class MainActivity extends AppCompatActivity {

    private ImageButton buttonPlayStop;
    private ImageButton buttonVolume;
    private SeekBar barVolume;
    private TextView titleSound;
    private int visibleBarVolume;
    private Config config;
    private ServiceDataRadio serviceDataRadio;
    private ServiceTimerAction serviceTimerAction;
    private ServiceAudio serviceAudio;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        loadInstances();
        initComponents();
    }

    private void loadInstances() {
        config = new Config();
        serviceDataRadio = new ServiceDataRadio(this, config);
        serviceTimerAction = new ServiceTimerAction(serviceDataRadio, 5000);
        serviceAudio = new ServiceAudio(this, config, buttonPlayStop);
    }

    private void initComponents() {
        buttonPlayStop = findViewById(R.id.buttinPlayStop);
        buttonVolume = findViewById(R.id.buttonVolume);
        buttonVolume.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                toggleBarVolume();
            }
        });
        titleSound = findViewById(R.id.titleSound);
        barVolume = findViewById(R.id.barVolume);
        barVolume.setVisibility(View.GONE);
        visibleBarVolume = barVolume.getVisibility();
    }

    private void toggleBarVolume() {
        visibleBarVolume = barVolume.getVisibility();
        if (visibleBarVolume == View.GONE || visibleBarVolume == View.INVISIBLE)
            barVolume.setVisibility(View.VISIBLE);
        else
            barVolume.setVisibility(View.GONE);
    }
}