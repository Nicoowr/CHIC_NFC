package com.example.nico.chic_nfc;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.PendingIntent;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.nfc.NfcAdapter;
import android.nfc.Tag;
import android.nfc.tech.Ndef;
import android.nfc.tech.NdefFormatable;
import android.nfc.tech.NfcV;
import android.os.Bundle;
import android.widget.TextView;

import java.io.IOException;

/**
 * Created by Nico on 12/05/2017.
 */

public class Reset_Activity extends Activity{

    private NfcAdapter myNfcAdapter;
    private PendingIntent mPendingIntent;
    private IntentFilter[] mFilters;
    private String[][] mTechLists;
    private TextView myText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reset);


        myText = (TextView) findViewById(R.id.resetText);
        myNfcAdapter = NfcAdapter.getDefaultAdapter(this);
        if (myNfcAdapter == null)
            myText.setText("NFC is not available for the device!!!");
        else
            myText.setText("NFC is available for the device");

        mPendingIntent = PendingIntent.getActivity(this, 0,
                new Intent(this, getClass()).addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP), 0);
        IntentFilter nfcv = new IntentFilter(NfcAdapter.ACTION_TECH_DISCOVERED);
        mFilters = new IntentFilter[] {
                nfcv,
        };
        mTechLists = new String[][] { new String[] { NfcV.class.getName() },
                new String[] { NdefFormatable.class.getName() }};

    }

    @Override
    public void onNewIntent(Intent intent) {
        //if (NfcAdapter.ACTION_TECH_DISCOVERED.equals(getIntent().getAction())) {
        Tag detectedTag = intent.getParcelableExtra(NfcAdapter.EXTRA_TAG);
        NfcV nfcv = NfcV.get(detectedTag);
        try {
            nfcv.connect();
            if (nfcv.isConnected()) {
                myText.append("Connected to the tag");
                myText.append("\nTag DSF: " + Byte.toString(nfcv.getDsfId()));


                byte command[] = new byte[]{//Send reset
                        0x00,
                        0x21,
                        (byte) 0,
                        -128, //General control register : reset
                        0x00, //Firmware Status register
                        0x00, //Sensor control register
                        0x00, //Frequency control register
                        0x00, //Number of passes register
                        0x01, //Averaging register
                        0x00, //Interrupt control register: infinite sampling
                        0x00 //Error control register
                };

                nfcv.transceive(command);
            } else {
            }
            nfcv.close();

        } catch (IOException e) {
            myText.append("Error");
        }

    }

    @Override
    public void onResume() {
        super.onResume();
        if (myNfcAdapter != null) myNfcAdapter.enableForegroundDispatch(this, mPendingIntent, mFilters,
                mTechLists);
    }

    @Override
    public void onPause() {
        super.onPause();
        myNfcAdapter.disableForegroundDispatch(this);
    }

}

