package com.example.radiosantidad1025fm;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

import com.example.radiosantidad1025fm.Configs.Config;
import com.example.radiosantidad1025fm.services.ServiceAudio;
import com.example.radiosantidad1025fm.services.ServiceDataRadio;
import com.example.radiosantidad1025fm.services.ServiceTimerAction;
import com.google.android.material.switchmaterial.SwitchMaterial;

public class MainActivity extends AppCompatActivity {

    private ImageButton buttonPlayStop;
    private ImageButton buttonVolume;
    private SeekBar barVolume;
    private TextView titleSound;
    private TextView backVolume;
    private TextView textVolume;
    private TextView textListeners;
    private SwitchMaterial switchMaterial;
    private int visibleBarVolume;
    private Config config;
    private ServiceDataRadio serviceDataRadio;
    private ServiceTimerAction serviceTimerAction;
    private ServiceAudio serviceAudio;
    private TextView backgroundInternet;
    private ImageView imageInternet;
    private Button buttonInternet;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initComponents();
        loadInstances();
    }

    private void loadInstances() {
        config = new Config();
        serviceDataRadio = new ServiceDataRadio(getApplicationContext(), config, switchMaterial, titleSound, textListeners);
        //serviceDataRadio.getDataRadio();
        serviceTimerAction = new ServiceTimerAction(serviceDataRadio, 5000);
        serviceAudio = new ServiceAudio(getBaseContext(), config, buttonPlayStop, barVolume);
    }

    private void initComponents() {
        switchMaterial = findViewById(R.id.switchStatus);
        switchMaterial.setChecked(true);
        switchMaterial.setEnabled(false);
        buttonPlayStop = findViewById(R.id.buttinPlayStop);
        buttonVolume = findViewById(R.id.buttonVolume);
        buttonVolume.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                toggleBarVolume();
            }
        });
        titleSound = findViewById(R.id.titleSound);
        textListeners = findViewById(R.id.textListeners);
        backVolume = findViewById(R.id.backgroundVolume);
        backVolume.setVisibility(View.GONE);
        textVolume = findViewById(R.id.textVolumen);
        textVolume.setVisibility(View.GONE);
        barVolume = findViewById(R.id.barVolume);
        barVolume.setVisibility(View.GONE);
        barVolume.setMax(100);
        barVolume.setProgress(100);
        visibleBarVolume = barVolume.getVisibility();
    }

    private void toggleBarVolume() {
        visibleBarVolume = barVolume.getVisibility();
        if (visibleBarVolume == View.GONE || visibleBarVolume == View.INVISIBLE) {
            barVolume.setVisibility(View.VISIBLE);
            textVolume.setVisibility(View.VISIBLE);
            backVolume.setVisibility(View.VISIBLE);
        } else {
            barVolume.setVisibility(View.GONE);
            textVolume.setVisibility(View.GONE);
            backVolume.setVisibility(View.GONE);
        }
    }
}