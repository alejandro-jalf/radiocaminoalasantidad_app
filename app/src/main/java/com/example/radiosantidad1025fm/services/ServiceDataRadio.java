package com.example.radiosantidad1025fm.services;

import android.content.Context;
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

    public ServiceDataRadio(Context context, Config configs, SwitchMaterial switchMaterial) {
        this.context = context;
        this. configs = configs;
        this.switchMaterial = switchMaterial;
        this.requestQueue = Volley.newRequestQueue(context);
        Toast.makeText(context, "Alggo", Toast.LENGTH_SHORT).show();
    }

    public void getDataRadio() {
        jsonObjectRequest = new JsonObjectRequest(
                Request.Method.GET, configs.getUrlDataRadio(), null, new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {
                    Toast.makeText(context, "Respuesta: " + response.toString(), Toast.LENGTH_LONG).show();
                    switchMaterial.setChecked(true);
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
