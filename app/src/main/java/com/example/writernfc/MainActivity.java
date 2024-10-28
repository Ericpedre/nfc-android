package com.example.writernfc;

import android.nfc.NfcAdapter;
import android.nfc.Tag;
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

                NfcA nfcA = NfcA.get(tag);
                if (nfcA != null) {
                    try {
                        nfcA.connect();
                        byte[] atqa = nfcA.getAtqa();
                        String atqaHex = bytesToHex(atqa);
                        Log.d("NFC", "NfcA ATQA: " + atqaHex);

                        // Leer varios bloques de la etiqueta
                        for (int i = 4; i < 8; i++) { // Ejemplo: leer bloques 4 a 7
                            byte[] response = readBlock(nfcA, i);
                            Log.d("NFC", "NfcA Response (Block " + i + "): " + response);
                            String responseHex = bytesToHex(response);
                            Log.d("NFC", "NfcA Response (Block " + i + "): " + responseHex);
                        }

                        nfcA.close();
                    } catch (IOException e) {
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

    private String bytesToHex(byte[] bytes) {
        StringBuilder sb = new StringBuilder();
        for (byte b : bytes) {
            sb.append(String.format("%02X", b));
        }
        return sb.toString();
    }
}