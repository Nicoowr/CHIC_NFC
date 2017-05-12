package com.example.nico.chic_nfc;

import android.nfc.NfcAdapter;
import android.nfc.Tag;
import android.nfc.tech.NfcV;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

import java.io.IOException;


public class MainActivity extends AppCompatActivity {

    private NfcAdapter myNfcAdapter;
    private TextView myText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        myText = (TextView) findViewById(R.id.NFCAdapter);
        myNfcAdapter = NfcAdapter.getDefaultAdapter(this);
        if (myNfcAdapter == null)
            myText.setText("NFC is not available for the device!!!");
        else
            myText.setText("NFC is available for the device");

        if (NfcAdapter.ACTION_TECH_DISCOVERED.equals(getIntent().getAction())) {
            Tag detectedTag = getIntent().getParcelableExtra(NfcAdapter.EXTRA_TAG); NfcV nfcv = NfcV.get(detectedTag);
            try {
                nfcv.connect();
                if(nfcv.isConnected()){
                    myText.append("Connected to the tag");
                    myText.append("\nTag DSF: "+Byte.toString(nfcv.getDsfId()));
                    byte[] buffer;
                    /*buffer=nfcv.transceive(new byte[] {0x00, 0x20, (byte) 0});
                    myText.append("\nByte block 10:"+buffer);
                    myText.append("\nByte block 10 as string:"+new String(buffer));*/


                    //nfcv.transceive(new byte[] {0x00, 0x21, (byte) 0,0x01, 0x00, 0x10, 0x03, 0x02, 0x01, 0x01, 0x00});

                    buffer=nfcv.transceive(new byte[] {0x00, 0x20, (byte) 9});

                    StringBuilder sb = new StringBuilder();
                    for (byte b : buffer) {
                        sb.append(String.format("%02X ", b));
                    }
                    //System.out.println(sb.toString());

                    myText.append("\nByte block 10:"+sb.toString());
                    myText.append("\nByte block 10 as string:"+new String(buffer));
                    nfcv.close();
                } else

                    myText.append("Not connected to the tag");
            } catch (IOException e) { myText.append("Error");
            } }
    }
}
