 package com.radiosantidadapp.radiosantidad1025fm.views;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintSet;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.ComponentName;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.telephony.PhoneNumberUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import com.radiosantidadapp.radiosantidad1025fm.Configs.Config;
import com.radiosantidadapp.radiosantidad1025fm.MainActivity;
import com.radiosantidadapp.radiosantidad1025fm.R;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

public class Contacto extends AppCompatActivity {
    private ImageButton btnPopupMenuWhatR;
    private ImageButton btnPopupMenuCallR;
    private ImageButton btnPopupMenuFaceR;
    private ImageButton btnPopupMenuWhatE;
    private ImageButton btnPopupMenuCallE;
    private String sourceActual;
    private String numPhone;
    private Config config;
    private ClipboardManager clipboardManager;
    private ClipData clip;
    private AlertDialog alertDialog;
    private AlertDialog.Builder alertBuilder;
    private LayoutInflater layoutInflater;
    private View viewAlert;
    private ActionBar actionBar;

    private TextView btnCalcelAlert;
    private ImageView imageViewCopy;
    private TextView textViewCopy;
    private ImageView imageViewOpen;
    private TextView textViewOpen;
    private ImageView imageViewCall;
    private TextView textViewCall;
    private ImageView imageViewMessage;
    private TextView textViewMessage;
    private TextView titleCard;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contacto);

        actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);

        initComponents();
        initAlertDialog();
        this.config = new Config();
    }

    @Override
    public boolean onSupportNavigateUp() {
        startActivity(new Intent(this, MainActivity.class));
        finish();
        return false;
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == event.KEYCODE_BACK) {
            startActivity(new Intent(this, MainActivity.class));
            finish();
        }
        return super.onKeyDown(keyCode, event);
    }

    private void initComponents() {
        alertBuilder = new AlertDialog.Builder(this);
        btnPopupMenuWhatR = findViewById(R.id.btnPopupWhatR);
        btnPopupMenuCallR = findViewById(R.id.btnPopupCallR);
        btnPopupMenuFaceR = findViewById(R.id.btnPopupFaceR);
        btnPopupMenuWhatE = findViewById(R.id.btnPopupWhatE);
        btnPopupMenuCallE = findViewById(R.id.btnPopupCallE);

        btnPopupMenuWhatR.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) { showAlertDialogFor("WhatRadio"); }
        });
        btnPopupMenuCallR.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) { showAlertDialogFor("CallRadio"); }
        });
        btnPopupMenuFaceR.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) { showAlertDialogFor("FaceRadio"); }
        });
        btnPopupMenuWhatE.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) { showAlertDialogFor("WhatAdmin"); }
        });
        btnPopupMenuCallE.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) { showAlertDialogFor("CallAdmin"); }
        });
    }

    private void initAlertDialog() {
        layoutInflater = getLayoutInflater();
        viewAlert = layoutInflater.inflate(R.layout.alert_options, null);
        alertBuilder.setView(viewAlert);
        alertDialog = alertBuilder.create();

        btnCalcelAlert = viewAlert.findViewById(R.id.btnCancelOption);
        titleCard = viewAlert.findViewById(R.id.titleCard);
        imageViewCopy = viewAlert.findViewById(R.id.imageViewCopy);
        imageViewCall = viewAlert.findViewById(R.id.imageViewCall);
        imageViewOpen = viewAlert.findViewById(R.id.imageViewOpen);
        imageViewMessage = viewAlert.findViewById(R.id.imageViewMessage);
        textViewCopy = viewAlert.findViewById(R.id.optionTextCopy);
        textViewCall = viewAlert.findViewById(R.id.optionTextCall);
        textViewOpen = viewAlert.findViewById(R.id.optionTextOpen);
        textViewMessage = viewAlert.findViewById(R.id.optionTextMessage);

        textViewCall.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertDialog.dismiss();
                numPhone = sourceActual.equals("CallRadio")
                        ? config.getNumberRadio()
                        : config.getNumberAdmin();
                Toast.makeText(getApplicationContext(), "Estableciendo llamada", Toast.LENGTH_SHORT).show();
                callTo(numPhone);
            }
        });

        textViewOpen.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertDialog.dismiss();
                if (sourceActual.equals("CallRadio")) openCall(config.getNumberRadio());
                else if (sourceActual.equals("CallAdmin")) openCall(config.getNumberAdmin());
                else if (sourceActual.equals("WhatRadio")) sendWhatsappTo(config.getNumberRadio());
                else if (sourceActual.equals("WhatAdmin")) sendWhatsappTo(config.getNumberAdmin());
                else if (sourceActual.equals("FaceRadio")) openFacebook();
            }
        });

        textViewCopy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertDialog.dismiss();
                if (sourceActual.equals("CallRadio") || sourceActual.equals("WhatRadio"))
                    copyClipBoard(config.getNumberRadio());
                else if (sourceActual.equals("CallAdmin") || sourceActual.equals("WhatAdmin"))
                    copyClipBoard(config.getNumberAdmin());
            }
        });

        textViewMessage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertDialog.dismiss();
                numPhone = sourceActual.equals("CallRadio")
                        ? config.getNumberRadio()
                        : config.getNumberAdmin();
                senMessageTo(numPhone);
            }
        });

        btnCalcelAlert.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) { alertDialog.dismiss(); }
        });
    }

    private void showAlertDialogFor(String source) {
        sourceActual = source;
        optionsVisible(source);
        setTitleCard(source);
        alertDialog.show();
    }

    private void setTitleCard(String source) {
        if (source.equals("WhatRadio") || source.equals("WhatAdmin")) titleCard.setText(R.string.textWhatRadio);
        if (source.equals("CallRadio")) titleCard.setText(R.string.textCallRadio);
        if (source.equals("CallAdmin")) titleCard.setText(R.string.textCallAdmin);
        if (source.equals("FaceRadio")) titleCard.setText(R.string.textFaceRadio);
    }

    private void optionsVisible(String source) {
        showAllOptions();
        if (source.equals("WhatRadio") || source.equals("WhatAdmin") || source.equals("FaceRadio")) {
            imageViewCall.setVisibility(View.GONE);
            textViewCall.setVisibility(View.GONE);
            if (source.equals("FaceRadio")) {
                imageViewMessage.setVisibility(View.GONE);
                textViewMessage.setVisibility(View.GONE);
                imageViewCopy.setVisibility(View.GONE);
                textViewCopy.setVisibility(View.GONE);
            }
        } else if (source.equals("CallRadio") || source.equals("CallAdmin")) {
            imageViewMessage.setVisibility(View.GONE);
            textViewMessage.setVisibility(View.GONE);
        }
    }

    private void showAllOptions() {
        imageViewCopy.setVisibility(View.VISIBLE);
        imageViewCall.setVisibility(View.VISIBLE);
        imageViewOpen.setVisibility(View.VISIBLE);
        imageViewMessage.setVisibility(View.VISIBLE);
        textViewCopy.setVisibility(View.VISIBLE);
        textViewCall.setVisibility(View.VISIBLE);
        textViewOpen.setVisibility(View.VISIBLE);
        textViewMessage.setVisibility(View.VISIBLE);
    }

    private void callTo(String number) {
        startActivity(new Intent(Intent.ACTION_CALL, Uri.parse("tel:" + number)));
    }

    private void openCall(String number) {
        Toast.makeText(this, "Abriendo aplicacion de llamadas", Toast.LENGTH_SHORT).show();
        startActivity(new Intent(Intent.ACTION_DIAL, Uri.parse("tel:" + number)));
    }

    private void senMessageTo(String number) {
        String message = "Bendiciones";
        Toast.makeText(this, "Abriendo aplicacion de mensajes: ", Toast.LENGTH_SHORT).show();
        Intent smsIntent = new Intent(Intent.ACTION_SENDTO, Uri.parse("smsto:" + number));
        smsIntent.putExtra("sms_body", message);
        startActivity(smsIntent);
    }

    private void sendWhatsappTo(String number) {
        Intent intent = new Intent("android.intent.action.MAIN");
        intent.setComponent(new ComponentName("com.whatsapp","com.whatsapp.Conversation"));
        intent.putExtra("jid", PhoneNumberUtils.stripSeparators("52" + number)+"@s.whatsapp.net");
        intent.setType("text/plain");
        intent.putExtra(Intent.EXTRA_TEXT, "Bendiciones");
        try {
            startActivity(intent);
        } catch (android.content.ActivityNotFoundException ex) {
            ex.printStackTrace();
            Toast.makeText(this, "El dispositivo no tiene instalado WhatsApp", Toast.LENGTH_LONG).show();
        }
    }

    private void openFacebook() {
        try {
            Intent intentFacebook = new Intent(Intent.ACTION_VIEW, Uri.parse(config.getFacebookId()));
            intentFacebook.setPackage("com.facebook.katana");
            startActivity(intentFacebook);
        } catch (Exception e) {
            Log.d("Facebook", "Aplicación no instalada.");
            try {
                Intent intentFacebookLite = new Intent(Intent.ACTION_VIEW, Uri.parse(config.getFacebookId()));
                intentFacebookLite.setPackage("com.facebook.lite");
                startActivity(intentFacebookLite);
            } catch (Exception el) {
                Log.d("Facebook Lite", "Aplicación no instalada.");
                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(config.getUrlPageFacebook())));
            }
        }
    }

    private void copyClipBoard(String data) {
        clipboardManager = (ClipboardManager) this.getSystemService(this.CLIPBOARD_SERVICE);
        clip = ClipData.newPlainText("Dato", data);
        clipboardManager.setPrimaryClip(clip);
        Toast.makeText(this,"Copiado al portapapeles", Toast.LENGTH_SHORT).show();
    }
}