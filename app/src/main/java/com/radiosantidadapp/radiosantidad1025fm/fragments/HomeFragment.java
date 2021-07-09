package com.radiosantidadapp.radiosantidad1025fm.fragments;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.google.android.material.switchmaterial.SwitchMaterial;
import com.radiosantidadapp.radiosantidad1025fm.Configs.Config;
import com.radiosantidadapp.radiosantidad1025fm.R;
import com.radiosantidadapp.radiosantidad1025fm.services.ServiceBackground;
import com.radiosantidadapp.radiosantidad1025fm.services.ServiceDataRadio;
import com.radiosantidadapp.radiosantidad1025fm.services.ServiceInstanciasComponents;
import com.radiosantidadapp.radiosantidad1025fm.services.ServiceInternet;
import com.radiosantidadapp.radiosantidad1025fm.services.ServiceTimerAction;
import com.radiosantidadapp.radiosantidad1025fm.utils.VerifyService;

public class HomeFragment extends Fragment {
    private ServiceInstanciasComponents serviceInstanciasComponents;
    private ServiceTimerAction serviceTimerAction;
    private ServiceDataRadio serviceDataRadio;
    private ServiceInternet serviceInternet;
    private VerifyService verifyService;
    private Config config;
    private Button buttonInternet;
    private ImageButton buttonPlayStop;
    private ImageButton buttonVolume;
    private ImageView imageInternet;
    private TextView backgroundInternet;
    private TextView textListeners;
    private TextView titleSound;
    private TextView backVolume;
    private TextView textVolume;
    private SeekBar barVolume;
    private SwitchMaterial switchMaterial;
    private int visibleBarVolume;
    private Intent intentBackground;
    private Intent intentBackgroundVolume;
    private Context context;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_home, container, false);

        this.context = getContext();
        initComponents(root);
        loadInstances();
        serviceTimerAction.initTask();
        verifyConnectionInternet();
        verifyServiceRunnning();
        setEvents();
        return root;
    }


    private void initComponents(View view) {
        switchMaterial = view.findViewById(R.id.switchStatus);
        switchMaterial.setChecked(false);
        switchMaterial.setText("Cargando....");
        switchMaterial.setEnabled(false);
        buttonPlayStop = view.findViewById(R.id.buttinPlayStop);
        buttonVolume = view.findViewById(R.id.buttonVolume);
        buttonVolume.setVisibility(View.GONE);
        buttonInternet = view.findViewById(R.id.buttonInternet);
        buttonInternet.setVisibility(View.GONE);
        titleSound = view.findViewById(R.id.titleSound);
        titleSound.setText("Cargando....");
        backgroundInternet = view.findViewById(R.id.backgroundInternet);
        backgroundInternet.setVisibility(View.GONE);
        imageInternet = view.findViewById(R.id.imageInternet);
        imageInternet.setVisibility(View.GONE);
        textListeners = view.findViewById(R.id.textListeners);
        backVolume = view.findViewById(R.id.backgroundVolume);
        backVolume.setVisibility(View.GONE);
        textVolume = view.findViewById(R.id.textVolumen);
        textVolume.setVisibility(View.GONE);
        barVolume = view.findViewById(R.id.barVolume);
        barVolume.setVisibility(View.GONE);
        barVolume.setMax(100);
        barVolume.setProgress(100);
        visibleBarVolume = barVolume.getVisibility();
    }

    private void loadInstances() {
        config = new Config();
        intentBackground = new Intent(context, ServiceBackground.class);
        intentBackgroundVolume = new Intent(context, ServiceBackground.class);
        serviceInstanciasComponents = new ServiceInstanciasComponents();
        serviceInstanciasComponents.setBarVolume(barVolume);
        serviceInstanciasComponents.setButtonPlayStop(buttonPlayStop);
        serviceInstanciasComponents.setButtonVolume(buttonVolume);
        serviceInstanciasComponents.setSwitchMaterial(switchMaterial);
        serviceInstanciasComponents.setTextListeners(textListeners);
        serviceInstanciasComponents.setTitleSound(titleSound);

        verifyService = new VerifyService(context);
        serviceDataRadio = new ServiceDataRadio(context, config, verifyService, serviceInstanciasComponents);
        serviceTimerAction = new ServiceTimerAction(serviceDataRadio, verifyService, serviceInstanciasComponents, 5000);
        serviceInternet = new ServiceInternet(context);
    }

    private void verifyConnectionInternet() {
        if (!serviceInternet.testInternet()) {
            buttonInternet.setVisibility(View.VISIBLE);
            backgroundInternet.setVisibility(View.VISIBLE);
            imageInternet.setVisibility(View.VISIBLE);
            Toast.makeText(context, "Sin conexion a internet", Toast.LENGTH_LONG).show();
        } else {
            buttonInternet.setVisibility(View.GONE);
            backgroundInternet.setVisibility(View.GONE);
            imageInternet.setVisibility(View.GONE);
        }
    }

    private void verifyServiceRunnning() {
        if (verifyService.isServiceRunning(ServiceBackground.class)) {
            buttonPlayStop.setImageResource(R.drawable.ic_baseline_stop_circle_55);
            Toast.makeText(context, "En reproduccion", Toast.LENGTH_SHORT).show();
            buttonVolume.setVisibility(View.VISIBLE);
        } else {
            buttonVolume.setVisibility(View.GONE);
            buttonPlayStop.setImageResource(R.drawable.ic_baseline_play_circle_filled_55);
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
            public void onClick(View v) { togglePlayStop(); }
        });

        barVolume.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                intentBackgroundVolume.putExtra("event", "Volume");
                intentBackgroundVolume.putExtra("volume", (float) progress/100);
                context.startService(intentBackgroundVolume);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {}

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {}
        });
    }

    public void togglePlayStop() {
        if (verifyService.isServiceRunning(ServiceBackground.class)) {
            buttonPlayStop.setImageResource(R.drawable.ic_baseline_play_circle_filled_55);
            context.stopService(intentBackground);
            if (barVolume.getVisibility() == View.VISIBLE)
                buttonVolume.performClick();
            buttonVolume.setVisibility(View.GONE);
            Toast.makeText(context, "Deteniendo......", Toast.LENGTH_SHORT).show();
        } else {
            context.startService(intentBackground);
            buttonPlayStop.setImageResource(R.drawable.ic_baseline_stop_circle_55);
        }
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