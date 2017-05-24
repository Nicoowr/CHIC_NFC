package com.example.nico.chic_nfc;

import android.app.Activity;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.IntentFilter;
import android.nfc.NfcAdapter;
import android.nfc.Tag;
import android.nfc.tech.NdefFormatable;
import android.nfc.tech.NfcV;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.widget.TextView;

import java.io.IOException;

/**
 * Created by Nico on 18/05/2017.
 */

public class NFC_Read extends Activity {
    private NfcAdapter myNfcAdapter;
    private PendingIntent mPendingIntent;
    private IntentFilter[] mFilters;
    private String[][] mTechLists;
    private TextView myText;
    private int currentData;
    private int samplingNb;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_read);


        myText = (TextView) findViewById(R.id.TextRead);
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
                myText.append("\nTag DSF: " + Byte.toString(nfcv.getDsfId())+ "\n");

                /////////////////////Read the number of samples since the last time/////////////
                byte index[];
                index = nfcv.transceive(new byte[]{0x00, (byte) -64, 0x07, 0x41, 0x06});
                samplingNb = ((index[4] & 0xff) << 8) | (index[5] & 0xff);
                myText.append("Number of sampling since last time: " + String.format("%1$d", currentData));
                myText.append("\n");




                int blockCount = 1;
                for ( int k = 0; k < 1; k++) { //32 corresponds to 32*2048

                    /////////////////Start transferring from FRAM to RAM////////////////
                    byte command[] = new byte[]{
                            0x00,
                            0x21,
                            (byte) 0,
                            0x01, //General control register
                            0x00, //Firmware Status register
                            0x20, //Sensor control register: ExtFram
                            0x03, //Frequency control register
                            0x01, //Number of passes register
                            0x01, //Averaging register
                            0x00, //Interrupt control register
                            //0x20 //Error control register: log into ram
                            0x00
                    };

                    nfcv.transceive(command);

                    ////////////////////wait 100 ms///////////////////////
                    final Handler handler = new Handler();
                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {

                        }
                    }, 100);

                    //////////////////Initialize transfer from RAM to phone////////////
                    byte[] buffer;// buffer containing the data
                    StringBuilder sb = new StringBuilder();

                    byte i = 0x44; //Start block of data
                    int j = 0;

                    //////////////////Start transfer/////////////////////
                    //First blocks from 0x644 to 0x6FF
                    while (j < 188) {
                        buffer = nfcv.transceive(new byte[]{0x00, (byte) -64, 0x07, i, 0x06});//Read single block

                        sb.append("Number " + blockCount + "\n");
                        /*for (byte b : buffer) {
                            sb.append(String.format("%02X ", b));
                        }*/
                        for(int l = 0 ; l < buffer.length; l++){
                            if(l%2 == 1){
                                Log.i(String.format("%1$d",blockCount),String.format("%8s", Integer.toBinaryString(buffer[l] & 0xFF)).replace(' ', '0'));
                                Log.i(String.format("%1$d",blockCount),String.format("%8s", Integer.toBinaryString(buffer[l+1] & 0xFF)).replace(' ', '0'));
                                currentData = ((buffer[l] & 0xff) << 8) | (buffer[l+1] & 0xff);

                                sb.append(String.format("%1$d", currentData));
                                sb.append(" ");
                            }

                        }
                        sb.append("\n");
                        j++;
                        i++;
                        blockCount++;
                    }

                    //Second blocks
                    j = 0;
                    while (j < 68) {
                        buffer = nfcv.transceive(new byte[]{0x00, (byte) -64, 0x07, i, 0x07});

                        sb.append("Number " + blockCount + "\n");
                        for (byte b : buffer) {
                            sb.append(String.format("%02X ", b));
                        }
                        sb.append("\n");
                        j++;
                        i++;
                        blockCount++;
                    }

                    myText.append("\n" + sb.toString());
                }

                //////////////////////Reset the device /////////////////////
                byte resetCommand[] = new byte[]{//Send reset
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

                nfcv.transceive(resetCommand);
                myText.append("\n Reset Done");



                nfcv.close();

            }else{
                myText.append("Not connected to the tag");
            }


            } catch(IOException e){
                myText.append("Error");
            }
            //}

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


