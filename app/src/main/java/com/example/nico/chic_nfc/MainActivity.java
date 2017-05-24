package com.example.nico.chic_nfc;

import android.content.Intent;
import android.nfc.NfcAdapter;
import android.nfc.Tag;
import android.nfc.tech.NfcV;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.io.IOException;


public class MainActivity extends AppCompatActivity {



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button goToNFC = (Button) findViewById(R.id.NFCActivity);
        goToNFC.setOnClickListener(new View.OnClickListener()
        {
            public void onClick(View view) {
                Intent myIntent = new Intent( view.getContext(), NFC_Activity.class);
                startActivityForResult(myIntent, 0);
            }
        });

        Button goToRead = (Button) findViewById(R.id.NFCRead);
        goToRead.setOnClickListener(new View.OnClickListener()
        {
            public void onClick(View view) {
                Intent myIntent = new Intent( view.getContext(), NFC_Read.class);
                startActivityForResult(myIntent, 0);
            }
        });

        Button goToReset = (Button) findViewById(R.id.NFCReset);
        goToReset.setOnClickListener(new View.OnClickListener()
        {
            public void onClick(View view) {
                Intent myIntent = new Intent( view.getContext(), Reset_Activity.class);
                startActivityForResult(myIntent, 0);
            }
        });

    }
}
