package com.example.radiosantidad1025fm.services;

import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

import com.google.android.material.switchmaterial.SwitchMaterial;

public class ServiceInstanciasComponents {
    private ImageButton buttonPlayStop;
    private ImageButton buttonVolume;
    private TextView titleSound;
    private TextView textListeners;
    private SeekBar barVolume;
    private SwitchMaterial switchMaterial;

    public ServiceInstanciasComponents() {}

    public ImageButton getButtonPlayStop() {
        return buttonPlayStop;
    }

    public void setButtonPlayStop(ImageButton buttonPlayStop) {
        this.buttonPlayStop = buttonPlayStop;
    }

    public ImageButton getButtonVolume() {
        return buttonVolume;
    }

    public void setButtonVolume(ImageButton buttonVolume) {
        this.buttonVolume = buttonVolume;
    }

    public TextView getTitleSound() {
        return titleSound;
    }

    public void setTitleSound(TextView titleSound) {
        this.titleSound = titleSound;
    }

    public TextView getTextListeners() {
        return textListeners;
    }

    public void setTextListeners(TextView textListeners) {
        this.textListeners = textListeners;
    }

    public SeekBar getBarVolume() {
        return barVolume;
    }

    public void setBarVolume(SeekBar barVolume) {
        this.barVolume = barVolume;
    }

    public SwitchMaterial getSwitchMaterial() {
        return switchMaterial;
    }

    public void setSwitchMaterial(SwitchMaterial switchMaterial) {
        this.switchMaterial = switchMaterial;
    }
}
