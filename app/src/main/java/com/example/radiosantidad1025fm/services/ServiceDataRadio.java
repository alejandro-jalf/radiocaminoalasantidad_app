package com.example.radiosantidad1025fm.services;

import android.content.Context;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.example.radiosantidad1025fm.Configs.Config;

import org.json.JSONException;
import org.json.JSONObject;

public class ServiceDataRadio {
    private Config configs;
    private JsonObjectRequest jsonObjectRequest = null;
    private Context context;

    public ServiceDataRadio(Context context, Config configs) {
        this.context = context;
        this. configs = configs;
    }

    public void getDataRadio() {
        jsonObjectRequest = new JsonObjectRequest(
                Request.Method.GET, configs.getUrlDataRadio(), null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                Toast.makeText(context, response.toString(), Toast.LENGTH_LONG).show();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(context, "Error en la solicitud", Toast.LENGTH_LONG).show();
            }
        }
        );
    }
}
