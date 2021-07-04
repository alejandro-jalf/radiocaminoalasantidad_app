package com.radiosantidadapp.radiosantidad1025fm;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.radiosantidadapp.radiosantidad1025fm.Configs.Config;
import com.radiosantidadapp.radiosantidad1025fm.services.ServiceAudio;
import com.radiosantidadapp.radiosantidad1025fm.services.ServiceBackground;
import com.radiosantidadapp.radiosantidad1025fm.services.ServiceDataRadio;
import com.radiosantidadapp.radiosantidad1025fm.services.ServiceInstanciasComponents;
import com.radiosantidadapp.radiosantidad1025fm.services.ServiceInternet;
import com.radiosantidadapp.radiosantidad1025fm.services.ServiceNotification;
import com.radiosantidadapp.radiosantidad1025fm.services.ServiceTimerAction;
import com.radiosantidadapp.radiosantidad1025fm.utils.VerifyService;
import com.radiosantidadapp.radiosantidad1025fm.views.Contacto;
import com.google.android.material.switchmaterial.SwitchMaterial;

public class MainActivity extends AppCompatActivity {

    private ServiceInstanciasComponents serviceInstanciasComponents;
    private ServiceNotification serviceNotification;
    private ServiceTimerAction serviceTimerAction;
    private ServiceDataRadio serviceDataRadio;
    private ServiceInternet serviceInternet;
    private VerifyService verifyService;
    // private ServiceAudio serviceAudio;
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
    private int idItemMenu;
    private Intent intentAudio;
    private Intent intentContacto;
    private Intent intent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        this.intentAudio = new Intent(getApplicationContext(), ServiceAudio.class);
        initComponents();
        loadInstances();
        serviceTimerAction.initTask();
        verifyConnectionInternet();
        verifyServiceRunnning();
        setEvents();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.opciones, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        idItemMenu = item.getItemId();
        if (idItemMenu == R.id.itemContacto) {
            intentContacto = new Intent(this, Contacto.class);
            startActivity(intentContacto);
            this.finish();
        }
        return super.onOptionsItemSelected(item);
    }

    private void initComponents() {
        switchMaterial = findViewById(R.id.switchStatus);
        switchMaterial.setChecked(false);
        switchMaterial.setText("Cargando....");
        switchMaterial.setEnabled(false);
        buttonPlayStop = findViewById(R.id.buttinPlayStop);
        buttonVolume = findViewById(R.id.buttonVolume);
        buttonVolume.setVisibility(View.GONE);
        buttonInternet = findViewById(R.id.buttonInternet);
        buttonInternet.setVisibility(View.GONE);
        titleSound = findViewById(R.id.titleSound);
        titleSound.setText("Cargando....");
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
        intent = new Intent(this, ServiceBackground.class);
        serviceInstanciasComponents = new ServiceInstanciasComponents();
        serviceInstanciasComponents.setBarVolume(barVolume);
        serviceInstanciasComponents.setButtonPlayStop(buttonPlayStop);
        serviceInstanciasComponents.setButtonVolume(buttonVolume);
        serviceInstanciasComponents.setSwitchMaterial(switchMaterial);
        serviceInstanciasComponents.setTextListeners(textListeners);
        serviceInstanciasComponents.setTitleSound(titleSound);

        verifyService = new VerifyService(getApplicationContext());
        serviceNotification = new ServiceNotification(getApplicationContext());
        serviceDataRadio = new ServiceDataRadio(getApplicationContext(), config, verifyService, serviceInstanciasComponents);
        serviceTimerAction = new ServiceTimerAction(serviceDataRadio, verifyService, serviceInstanciasComponents, 5000);
        //serviceAudio = new ServiceAudio(getBaseContext(), verifyService, serviceNotification, serviceInstanciasComponents);
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

    private void verifyServiceRunnning() {
        if (verifyService.isServiceRunning(ServiceAudio.class)) {
            buttonPlayStop.setImageResource(R.drawable.ic_baseline_stop_circle_55);
            Toast.makeText(this, "En reproduccion", Toast.LENGTH_SHORT).show();
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
                intentAudio.putExtra("event", "Volume");
                intentAudio.putExtra("volume", (float) progress/100);
                startService(intentAudio);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {}

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {}
        });
    }

    public void togglePlayStop() {
        if (verifyService.isServiceRunning(ServiceAudio.class)) {
            buttonPlayStop.setImageResource(R.drawable.ic_baseline_play_circle_filled_55);
            stopService(intent);
            if (barVolume.getVisibility() == View.VISIBLE)
                buttonVolume.performClick();
            buttonVolume.setVisibility(View.GONE);
            Toast.makeText(this, "Deteniendo......", Toast.LENGTH_SHORT).show();
        } else {
            startService(intent);
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