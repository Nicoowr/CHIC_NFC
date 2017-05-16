package com.example.nico.chic_nfc;

import android.app.Activity;
import android.app.PendingIntent;
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

public class NFC_Activity extends Activity{

    private NfcAdapter myNfcAdapter;
    private PendingIntent mPendingIntent;
    private IntentFilter[] mFilters;
    private String[][] mTechLists;
    private TextView myText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_nfc);


        myText = (TextView) findViewById(R.id.NFCAdapter);
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
        if (NfcAdapter.ACTION_TECH_DISCOVERED.equals(getIntent().getAction())) {
            Tag detectedTag = getIntent().getParcelableExtra(NfcAdapter.EXTRA_TAG);
            NfcV nfcv = NfcV.get(detectedTag);
            try {
                nfcv.connect();
                if (nfcv.isConnected()) {
                    myText.append("Connected to the tag");
                    myText.append("\nTag DSF: " + Byte.toString(nfcv.getDsfId()));
                    byte[] buffer;
                    /*buffer=nfcv.transceive(new byte[] {0x00, 0x20, (byte) 0});
                    myText.append("\nByte block 10:"+buffer);
                    myText.append("\nByte block 10 as string:"+new String(buffer));*/


                    nfcv.transceive(new byte[]{0x00, 0x21, (byte) 0, 0x01, 0x00, 0x10, 0x03, 0x02, 0x01, 0x01, 0x00});

                    buffer = nfcv.transceive(new byte[]{0x00, 0x20, (byte) 9});

                    StringBuilder sb = new StringBuilder();
                    for (byte b : buffer) {
                        sb.append(String.format("%02X ", b));
                    }
                    //System.out.println(sb.toString());

                    myText.append("\nByte block 10:" + sb.toString());
                    myText.append("\nByte block 10 as string:" + new String(buffer));
                    nfcv.close();
                } else

                    myText.append("Not connected to the tag");
            } catch (IOException e) {
                myText.append("Error");
            }
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

