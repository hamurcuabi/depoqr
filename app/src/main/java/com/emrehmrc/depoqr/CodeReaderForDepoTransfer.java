package com.emrehmrc.depoqr;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import com.google.zxing.Result;

import me.dm7.barcodescanner.zxing.ZXingScannerView;

public class CodeReaderForDepoTransfer extends AppCompatActivity {


    String codeid = "";
    ActionBar ab;


    private ZXingScannerView scannerView;

    @RequiresApi(api = Build.VERSION_CODES.CUPCAKE)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_codereader);
        ab = getSupportActionBar();
        ab.setTitle("KOD OKUT");
        //ab.setSubtitle("Alt Bşalık");
        ab.setBackgroundDrawable(getResources().getDrawable(R.drawable.arkaplan));
        scannerView = new ZXingScannerView(CodeReaderForDepoTransfer.this);
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
            Intent i= new Intent(CodeReaderForDepoTransfer.this,SliderMenu.class);
            startActivity(i);

        }
        else if (id == R.id.geri) {
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
            scannerView.resumeCameraPreview(this);
            //  setContentView(R.layout.activity_codereader);
            finish();
            Intent i = new Intent(CodeReaderForDepoTransfer.this, DepoTransferMalDegisim.class);
            i.putExtra("codeid", codeid);
            startActivity(i);


        }

    }
}
