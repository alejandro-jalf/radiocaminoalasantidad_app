package com.example.radiosantidad1025fm;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.radiosantidad1025fm.Configs.Config;
import com.example.radiosantidad1025fm.services.ServiceAudio;
import com.example.radiosantidad1025fm.services.ServiceDataRadio;
import com.example.radiosantidad1025fm.services.ServiceInternet;
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
    private ServiceInternet serviceInternet;
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
        verifyConnectionInternet();
        setEvents();
    }

    private void initComponents() {
        switchMaterial = findViewById(R.id.switchStatus);
        switchMaterial.setChecked(true);
        switchMaterial.setEnabled(false);
        buttonPlayStop = findViewById(R.id.buttinPlayStop);
        buttonVolume = findViewById(R.id.buttonVolume);
        buttonInternet = findViewById(R.id.buttonInternet);
        buttonInternet.setVisibility(View.GONE);
        titleSound = findViewById(R.id.titleSound);
        backgroundInternet = findViewById(R.id.backgroundInternet);
        backgroundInternet.setVisibility(View.GONE);
        imageInternet = findViewById(R.id.imageInternet);
        imageInternet.setVisibility(View.GONE);
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

    private void loadInstances() {
        config = new Config();
        serviceDataRadio = new ServiceDataRadio(getApplicationContext(), config, switchMaterial, titleSound, textListeners);
        serviceTimerAction = new ServiceTimerAction(serviceDataRadio, 5000);
        serviceAudio = new ServiceAudio(getBaseContext(), config, buttonPlayStop);
        serviceInternet = new ServiceInternet(getApplicationContext());
    }

    private void verifyConnectionInternet() {
        if (!serviceInternet.testInternet()) {
            buttonInternet.setVisibility(View.VISIBLE);
            backgroundInternet.setVisibility(View.VISIBLE);
            imageInternet.setVisibility(View.VISIBLE);
            Toast.makeText(this, "Sin conexion a internet", Toast.LENGTH_LONG).show();
        } else {
            buttonInternet.setVisibility(View.GONE);
            backgroundInternet.setVisibility(View.GONE);
            imageInternet.setVisibility(View.GONE);
        }
    }

    private void setEvents() {
        buttonVolume.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) { toggleBarVolume(); }
        });

        buttonInternet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                buttonInternet.setVisibility(View.GONE);
                backgroundInternet.setVisibility(View.GONE);
                imageInternet.setVisibility(View.GONE);
            }
        });

        buttonPlayStop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) { serviceAudio.toggleAudio(); }
        });

        barVolume.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                serviceAudio.changeVolume(progress);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {}

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {}
        });
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