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
import com.radiosantidadapp.radiosantidad1025fm.R;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

public class Contacto extends AppCompatActivity implements PopupMenu.OnMenuItemClickListener {
    private Menu menu;
    private PopupMenu popupMenuGeneral;
    private MenuInflater menuInflater;
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
    private TextView btnCalcelAlert;
    private TextView btnCopyOption;
    private ActionBar actionBar;

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
        this.config = new Config();
        //showCreateAlertDialog();
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return false;
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
            public void onClick(View v) { showCreateAlertDialog("WhatRadio"); }
        });
        btnPopupMenuCallR.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) { showCreateAlertDialog("CallRadio"); }
        });
        btnPopupMenuFaceR.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) { showCreateAlertDialog("FaceRadio"); }
        });
        btnPopupMenuWhatE.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) { showCreateAlertDialog("WhatAdmin"); }
        });
        btnPopupMenuCallE.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) { showCreateAlertDialog("CallAdmin"); }
        });
    }

    private void showMenu(String source, View v) {
        popupMenuGeneral = new PopupMenu(this, v);
        popupMenuGeneral.setOnMenuItemClickListener(this);
        menu = popupMenuGeneral.getMenu();
        menuInflater = popupMenuGeneral.getMenuInflater();
        menuInflater.inflate(R.menu.options_contacts, menu);
        sourceActual = source;
        try {
            Field popup = popupMenuGeneral.getClass().getDeclaredField("mPopup");
            popup.setAccessible(true);
            Object menuPopuHelper = popup.get(popupMenuGeneral);
            Class <?> classPopupHelper = Class.forName(menuPopuHelper.getClass().getName());
            Method serForceIcons = classPopupHelper.getMethod("setForceShowIcon", boolean.class);
            serForceIcons.invoke(menuPopuHelper, true);
        } catch (Exception e) {
            e.printStackTrace();
        }
        popupMenuGeneral.show();
        //optionsVisible(source, menu);
    }

    private void showCreateAlertDialog(String source) {
        layoutInflater = getLayoutInflater();
        viewAlert = layoutInflater.inflate(R.layout.alert_options, null);
        alertBuilder.setView(viewAlert);
        alertDialog = alertBuilder.create();
        alertDialog.show();

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
                numPhone = source.equals("CallRadio")
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
                if (source.equals("CallRadio")) openCall(config.getNumberRadio());
                else if (source.equals("CallAdmin")) openCall(config.getNumberAdmin());
                else if (source.equals("WhatRadio")) sendWhatsappTo(config.getNumberRadio());
                else if (source.equals("WhatAdmin")) sendWhatsappTo(config.getNumberAdmin());
                else if (source.equals("FaceRadio")) openFacebook();
            }
        });

        textViewCopy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertDialog.dismiss();
                if (source.equals("CallRadio") || source.equals("WhatRadio"))
                    copyClipBoard(config.getNumberRadio());
                else if (source.equals("CallAdmin") || source.equals("WhatAdmin"))
                    copyClipBoard(config.getNumberAdmin());
            }
        });

        textViewMessage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertDialog.dismiss();
                numPhone = source.equals("CallRadio")
                        ? config.getNumberRadio()
                        : config.getNumberAdmin();
                senMessageTo(numPhone);
            }
        });

        btnCalcelAlert = viewAlert.findViewById(R.id.btnCancelOption);
        btnCalcelAlert.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) { alertDialog.dismiss(); }
        });

        optionsVisible(source);
        setTitleCard(source);
    }

    private void setTitleCard(String source) {
        if (source.equals("WhatRadio") || source.equals("WhatAdmin")) titleCard.setText(R.string.textWhatRadio);
        if (source.equals("CallRadio")) titleCard.setText(R.string.textCallRadio);
        if (source.equals("CallAdmin")) titleCard.setText(R.string.textCallAdmin);
        if (source.equals("FaceRadio")) titleCard.setText(R.string.textFaceRadio);
    }

    private void optionsVisible(String source) {
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

    /* private void optionsVisible(String source, Menu menu) {
        if (source.equals("WhatRadio") || source.equals("WhatAdmin") || source.equals("FaceRadio")) {
            menu.setGroupVisible(R.id.grCall, false);
            if (source.equals("FaceRadio")) {
                menu.setGroupVisible(R.id.grMessage, false);
                menu.setGroupVisible(R.id.grCopy, false);
            }
        } else if (source.equals("CallRadio") || source.equals("CallAdmin")) {
            menu.setGroupVisible(R.id.grMessage, false);
        }
    } */

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
        // intent.setType("text/plain");
        // intent.putExtra(Intent.EXTRA_TEXT, "Bendiciones");
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

    @Override
    public boolean onMenuItemClick(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.optionCall:
                numPhone = sourceActual.equals("CallRadio")
                        ? config.getNumberRadio()
                        : config.getNumberAdmin();
                Toast.makeText(this, "Estableciendo llamada", Toast.LENGTH_SHORT).show();
                callTo(numPhone);
                return true;
            case R.id.optionCopy:
                if (sourceActual.equals("CallRadio") || sourceActual.equals("WhatRadio"))
                    copyClipBoard(config.getNumberRadio());
                else if (sourceActual.equals("CallAdmin") || sourceActual.equals("WhatAdmin"))
                    copyClipBoard(config.getNumberAdmin());
                return true;
            case R.id.optionOpen:
                if (sourceActual.equals("CallRadio")) openCall(config.getNumberRadio());
                else if (sourceActual.equals("CallAdmin")) openCall(config.getNumberAdmin());
                else if (sourceActual.equals("WhatRadio")) sendWhatsappTo(config.getNumberRadio());
                else if (sourceActual.equals("WhatAdmin")) sendWhatsappTo(config.getNumberAdmin());
                else if (sourceActual.equals("FaceRadio")) openFacebook();
                return true;
            case R.id.optionMessage:
                numPhone = sourceActual.equals("CallRadio")
                        ? config.getNumberRadio()
                        : config.getNumberAdmin();
                senMessageTo(numPhone);
                return true;
            default:
                return false;
        }
    }
}