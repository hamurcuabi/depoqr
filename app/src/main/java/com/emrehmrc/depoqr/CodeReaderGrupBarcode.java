package com.emrehmrc.depoqr;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.media.ToneGenerator;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;

import com.google.zxing.Result;

import me.dm7.barcodescanner.zxing.ZXingScannerView;

public class CodeReaderGrupBarcode extends AppCompatActivity {


    public ZXingScannerView scannerView;
    String codeid = "";
    ActionBar ab;
    ToneGenerator toneG;


    @RequiresApi(api = Build.VERSION_CODES.CUPCAKE)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_codereader);

        toneG = new ToneGenerator(AudioManager.STREAM_ALARM, 100);


        ab = getSupportActionBar();
        ab.setTitle("KOD OKUT");
        //ab.setSubtitle("Alt Bşalık");
        ab.setBackgroundDrawable(getResources().getDrawable(R.drawable.arkaplan));
        scannerView = new ZXingScannerView(CodeReaderGrupBarcode.this);
        scannerView.setResultHandler(new Zxing());
        setContentView(scannerView);
        scannerView.startCamera();


    }


    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.diger, menu);
        return super.onCreateOptionsMenu(menu);
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.anasayfa) {
            finish();
            Intent i = new Intent(CodeReaderGrupBarcode.this, GrupBarkod.class);
            startActivity(i);

        } else if (id == R.id.geri) {
            finish();
            onBackPressed();

        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    class Zxing implements ZXingScannerView.ResultHandler {


        @RequiresApi(api = Build.VERSION_CODES.CUPCAKE)
        @Override
        public void handleResult(Result result) {


            scannerView.stopCamera();
            //Toast.makeText(CodeReader.this, result.getText(), Toast.LENGTH_SHORT).show();
            codeid = result.getText();
            if (!codeid.equals(null) || !codeid.equals("")) {
                toneG.startTone(ToneGenerator.TONE_CDMA_ALERT_CALL_GUARD, 200);
            } else toneG.startTone(ToneGenerator.TONE_CDMA_CALL_SIGNAL_ISDN_PING_RING, 200);
            scannerView.resumeCameraPreview(this);
            //  setContentView(R.layout.activity_codereader);

            Intent i = new Intent(CodeReaderGrupBarcode.this, GrupBarkod.class);
            i.putExtra("codeid", codeid);
            setResult(Activity.RESULT_OK, i);
            //   Toast.makeText(CodeReader.this,codeid,Toast.LENGTH_SHORT).show();

            finish();


        }

    }
}

