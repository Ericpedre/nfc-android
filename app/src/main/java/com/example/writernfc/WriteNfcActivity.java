package com.example.writernfc;

import android.nfc.NfcAdapter;
import android.nfc.Tag;
import android.nfc.tech.NfcA;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

public class WriteNfcActivity extends AppCompatActivity {

    private NfcAdapter nfcAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        nfcAdapter = NfcAdapter.getDefaultAdapter(this);

        Button buttonStartWrite = findViewById(R.id.button_start_write);
        buttonStartWrite.setOnClickListener(v -> enableWriteMode());
    }

    private void enableWriteMode() {
        nfcAdapter.enableReaderMode(this, new NfcAdapter.ReaderCallback() {
            @Override
            public void onTagDiscovered(Tag tag) {
                Log.d("NFC", "Tag discovered");

                NfcA nfcA = NfcA.get(tag);
                if (nfcA != null) {
                    try {
                        nfcA.connect();
                        byte[] dataToWrite = "Hello NFC".getBytes(StandardCharsets.UTF_8);
                        writeBlock(nfcA, 4, dataToWrite); // Escribir en el bloque 4
                        nfcA.close();
                    } catch (IOException e) {
                        Log.e("NFC", "Error communicating with NFC tag", e);
                    }
                }
            }
        }, NfcAdapter.FLAG_READER_NFC_A, null);
    }

    private void writeBlock(NfcA nfcA, int blockNumber, byte[] data) throws IOException {
        if (data.length != 4) {
            throw new IllegalArgumentException("Data must be exactly 4 bytes long");
        }
        byte[] command = new byte[6];
        command[0] = (byte) 0xA2; // Comando de escritura
        command[1] = (byte) blockNumber;
        System.arraycopy(data, 0, command, 2, data.length);
        nfcA.transceive(command);
    }
}