package com.example.writernfc;

import android.nfc.FormatException;
import android.nfc.NdefMessage;
import android.nfc.NdefRecord;
import android.nfc.NfcAdapter;
import android.nfc.Tag;
import android.nfc.tech.Ndef;
import android.nfc.tech.NfcA;
import android.os.Bundle;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        NfcAdapter nfcAdapter = NfcAdapter.getDefaultAdapter(this);

        nfcAdapter.enableReaderMode(this, new NfcAdapter.ReaderCallback() {
            @Override
            public void onTagDiscovered(Tag tag) {
                Log.d("NFC", "Tag discovered");

                Ndef ndef = Ndef.get(tag);
                if (ndef != null) {
                    try {
                        ndef.connect();
                        NdefMessage ndefMessage = ndef.getNdefMessage();
                        if (ndefMessage != null) {
                            for (NdefRecord ndefRecord : ndefMessage.getRecords()) {
                                if (ndefRecord.getTnf() == NdefRecord.TNF_WELL_KNOWN &&
                                        java.util.Arrays.equals(ndefRecord.getType(), NdefRecord.RTD_TEXT)) {
                                    String payload = new String(ndefRecord.getPayload(), StandardCharsets.UTF_8);
                                    Log.d("NFC", "Ndef Record: " + payload);
                                }
                            }
                        }
                        ndef.close();
                    } catch (IOException | FormatException e) {
                        Log.e("NFC", "Error communicating with NFC tag", e);
                    }
                }
            }
        }, NfcAdapter.FLAG_READER_NFC_A, null);
    }

    private byte[] readBlock(NfcA nfcA, int blockNumber) throws IOException {
        byte[] command = new byte[]{(byte)0x30, (byte)blockNumber}; // Comando de lectura
        return nfcA.transceive(command);
    }
}