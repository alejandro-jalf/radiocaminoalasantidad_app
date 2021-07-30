package com.radiosantidadapp.radiosantidad1025fm.fragments;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.net.Uri;
import android.os.Bundle;
import android.telephony.PhoneNumberUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;

import com.radiosantidadapp.radiosantidad1025fm.Configs.Config;
import com.radiosantidadapp.radiosantidad1025fm.R;

public class ContactsFragment extends Fragment {
    private ImageButton btnPopupMenuWhatR;
    private ImageButton btnPopupMenuCallR;
    private ImageButton btnPopupMenuFaceR;
    private ImageButton btnPopupMenuWhatE;
    private ImageButton btnPopupMenuCallE;
    private ImageButton btnPopupMenuRaWeb;
    private Config config;

    private AlertDialog alertDialog;
    private AlertDialog.Builder alertBuilder;
    private LayoutInflater layoutInflater;
    private ImageView imageViewCopy;
    private ImageView imageViewOpen;
    private ImageView imageViewCall;
    private ImageView imageViewMessage;
    private TextView textViewCopy;
    private TextView textViewOpen;
    private TextView textViewCall;
    private TextView textViewMessage;
    private TextView btnCalcelAlert;
    private TextView titleCard;
    private View viewAlert;
    private String numPhone;
    private String sourceActual;
    private Context context;

    private ClipData clip;
    private ClipboardManager clipboardManager;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_contacts, container, false);

        this.context = getContext();
        this.config = new Config();

        initComponents(root);
        initAlertDialog();
        return root;
    }

    private void initComponents(View view) {
        btnPopupMenuWhatR = view.findViewById(R.id.btnPopupWhatR);
        btnPopupMenuCallR = view.findViewById(R.id.btnPopupCallR);
        btnPopupMenuFaceR = view.findViewById(R.id.btnPopupFaceR);
        btnPopupMenuWhatE = view.findViewById(R.id.btnPopupWhatE);
        btnPopupMenuCallE = view.findViewById(R.id.btnPopupCallE);
        btnPopupMenuRaWeb = view.findViewById(R.id.btnPopupRadioWeb);

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
        btnPopupMenuRaWeb.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) { showAlertDialogFor("RadioWeb"); }
        });
    }

    private void initAlertDialog() {
        alertBuilder = new AlertDialog.Builder(context);
        layoutInflater = getLayoutInflater();
        viewAlert = layoutInflater.inflate(R.layout.alert_options, null);
        alertBuilder.setView(viewAlert);
        alertDialog = alertBuilder.create();

        titleCard = viewAlert.findViewById(R.id.titleCard);
        btnCalcelAlert = viewAlert.findViewById(R.id.btnCancelOption);
        imageViewMessage = viewAlert.findViewById(R.id.imageViewMessage);
        imageViewCopy = viewAlert.findViewById(R.id.imageViewCopy);
        imageViewCall = viewAlert.findViewById(R.id.imageViewCall);
        imageViewOpen = viewAlert.findViewById(R.id.imageViewOpen);
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
                Toast.makeText(context, "Estableciendo llamada", Toast.LENGTH_SHORT).show();
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
                else if (sourceActual.equals("RadioWeb")) openRadioWeb();
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
        if (source.equals("RadioWeb")) titleCard.setText(R.string.textRadioWeb);
    }

    private void optionsVisible(String source) {
        showAllOptions();
        if (source.equals("WhatRadio") || source.equals("WhatAdmin") || source.equals("FaceRadio") || source.equals("RadioWeb")) {
            imageViewCall.setVisibility(View.GONE);
            textViewCall.setVisibility(View.GONE);
            if (source.equals("FaceRadio") || source.equals("RadioWeb")) {
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
        try {
            startActivity(new Intent(Intent.ACTION_CALL, Uri.parse("tel:" + number)));
        } catch (Exception e) {
            Toast.makeText(context, "No se pudo completar la llamada en su dispositivo", Toast.LENGTH_SHORT).show();
        }
    }

    private void openCall(String number) {
        try {
            Toast.makeText(context, "Abriendo aplicacion de llamadas", Toast.LENGTH_SHORT).show();
            startActivity(new Intent(Intent.ACTION_DIAL, Uri.parse("tel:" + number)));
        } catch (Exception e) {
            Toast.makeText(context, "No se pudo abrir la aplicacion de llamadas", Toast.LENGTH_SHORT).show();
        }
    }

    private void senMessageTo(String number) {
        try {
            String message = "Bendiciones";
            Toast.makeText(context, "Abriendo aplicacion de mensajes: ", Toast.LENGTH_SHORT).show();
            Intent smsIntent = new Intent(Intent.ACTION_SENDTO, Uri.parse("smsto:" + number));
            smsIntent.putExtra("sms_body", message);
            startActivity(smsIntent);
        } catch (Exception e) {
            Toast.makeText(context, "No se pudo abrir la aplicacion de mensajes", Toast.LENGTH_SHORT).show();
        }
    }

    private void sendWhatsappTo(String number) {
        Intent intent = new Intent("android.intent.action.MAIN");
        intent.setComponent(new ComponentName("com.whatsapp","com.whatsapp.Conversation"));
        intent.putExtra("jid", PhoneNumberUtils.stripSeparators("52" + number)+"@s.whatsapp.net");
        try {
            startActivity(intent);
        } catch (android.content.ActivityNotFoundException ex) {
            ex.printStackTrace();
            Toast.makeText(context, "El dispositivo no tiene instalado WhatsApp", Toast.LENGTH_LONG).show();
        }
    }

    private void openFacebook() {
        try {
            ApplicationInfo applicationInfo = context.getPackageManager().getApplicationInfo("com.facebook.katana", 0);
            if (applicationInfo.enabled) {
                Intent intentFacebook = new Intent(Intent.ACTION_VIEW, Uri.parse(config.getFacebookId()));
                intentFacebook.setPackage("com.facebook.katana");
                startActivity(intentFacebook);
            }
        } catch (Exception e) {
            try {
                ApplicationInfo applicationInfo = context.getPackageManager().getApplicationInfo("com.facebook.lite", 0);
                if (applicationInfo.enabled) {
                    Intent intentFbLite = context.getPackageManager().getLaunchIntentForPackage("com.facebook.lite");
                    intentFbLite.setAction(Intent.ACTION_VIEW);
                    intentFbLite.setData(Uri.parse(config.getFacebookId()));
                    startActivity(intentFbLite);
                }
            } catch (Exception eFbLite) {
                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(config.getUrlPageFacebook())));
            }
        }
    }

    private void openRadioWeb() {
        try {
            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(config.getUrlRadioSantidadWeb())));
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(context, "No se pudo abrir la pagina", Toast.LENGTH_SHORT).show();
        }
    }

    private void copyClipBoard(String data) {
        clipboardManager = (ClipboardManager) context.getSystemService(context.CLIPBOARD_SERVICE);
        clip = ClipData.newPlainText("Dato", data);
        clipboardManager.setPrimaryClip(clip);
        Toast.makeText(context,"Copiado al portapapeles", Toast.LENGTH_SHORT).show();
    }
}