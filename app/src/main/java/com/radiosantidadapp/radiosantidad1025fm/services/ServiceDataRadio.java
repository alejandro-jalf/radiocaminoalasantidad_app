package com.radiosantidadapp.radiosantidad1025fm.services;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.widget.ImageButton;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.radiosantidadapp.radiosantidad1025fm.Configs.Config;
import com.radiosantidadapp.radiosantidad1025fm.R;
import com.radiosantidadapp.radiosantidad1025fm.utils.VerifyService;
import com.google.android.material.switchmaterial.SwitchMaterial;

import org.json.JSONException;
import org.json.JSONObject;

public class ServiceDataRadio {
    private ServiceInstanciasComponents serviceInstanciasComponents;
    private VerifyService verifyService;
    private Config configs;
    private JsonObjectRequest jsonObjectRequest = null;
    private Context context;
    private SwitchMaterial switchMaterial;
    private RequestQueue requestQueue;
    private JSONObject dataResponse;
    private TextView titleSong;
    private TextView textListeners;
    private ImageButton buttonPlayStop;

    public ServiceDataRadio(Context context, Config configs, VerifyService verifyService, ServiceInstanciasComponents serviceInstanciasComponents) {
        this.context = context;
        this.configs = configs;
        this.verifyService = verifyService;
        this.serviceInstanciasComponents = serviceInstanciasComponents;
        this.switchMaterial = serviceInstanciasComponents.getSwitchMaterial();
        this.buttonPlayStop = serviceInstanciasComponents.getButtonPlayStop();
        this.textListeners = serviceInstanciasComponents.getTextListeners();
        this.titleSong = serviceInstanciasComponents.getTitleSound();
        this.requestQueue = Volley.newRequestQueue(context);
    }

    public void getDataRadio() {
        jsonObjectRequest = new JsonObjectRequest(
                Request.Method.GET, configs.getUrlDataRadio(), null, new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {
                    try {
                        dataResponse = response.getJSONObject("data");
                        if (dataResponse.getString("status").equals("Online")) {
                            switchMaterial.setChecked(true);
                            switchMaterial.setText("Al aire");
                            switchMaterial.setTextColor(Color.rgb(0, 200, 83));
                            titleSong.setText(dataResponse.getString("title"));
                            textListeners.setText(dataResponse.getString("listeners").substring(12));
                        }
                        else {
                            switchMaterial.setChecked(false);
                            switchMaterial.setText("Fuera del aire");
                            switchMaterial.setTextColor(Color.rgb(213, 0, 0));
                            titleSong.setText("Sin informacion de transmision");
                            textListeners.setText("0");
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    switchMaterial.setChecked(false);
                    switchMaterial.setText("Fallos con la red");
                    switchMaterial.setTextColor(Color.rgb(213, 0, 0));
                    titleSong.setText("Revise su conexion a internet");
                    textListeners.setText("0");
                    if (verifyService.isServiceRunning(ServiceAudio.class)) {
                        context.stopService(new Intent(context, ServiceAudio.class));
                        buttonPlayStop.setImageResource(R.drawable.ic_baseline_play_circle_filled_55);
                    }
                }
            }
        );
        requestQueue.add(jsonObjectRequest);
    }
}
