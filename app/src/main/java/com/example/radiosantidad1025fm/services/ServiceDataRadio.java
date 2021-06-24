package com.example.radiosantidad1025fm.services;

import android.content.Context;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.radiosantidad1025fm.Configs.Config;
import com.google.android.material.switchmaterial.SwitchMaterial;

import org.json.JSONException;
import org.json.JSONObject;

public class ServiceDataRadio {
    private Config configs;
    private JsonObjectRequest jsonObjectRequest = null;
    private Context context;
    private SwitchMaterial switchMaterial;
    private RequestQueue requestQueue;
    private JSONObject dataResponse;
    private TextView titleSong;
    private TextView textListeners;

    public ServiceDataRadio(Context context, Config configs, SwitchMaterial switchMaterial, TextView titleSong, TextView textListeners) {
        this.context = context;
        this. configs = configs;
        this.switchMaterial = switchMaterial;
        this.titleSong = titleSong;
        this.textListeners = textListeners;
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
                            switchMaterial.setText("Al aire music");
                            titleSong.setText(dataResponse.getString("title"));
                            textListeners.setText(dataResponse.getString("listeners").substring(12));
                        }
                        else {
                            switchMaterial.setChecked(false);
                            switchMaterial.setText("Fuera del aire");
                            titleSong.setText("Sin datos de titulo");
                            textListeners.setText("0");
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    Toast.makeText(context, "Error en la solicitud", Toast.LENGTH_LONG).show();
                    switchMaterial.setChecked(false);
                }
            }
        );
        requestQueue.add(jsonObjectRequest);
    }
}
