package com.emrehmrc.depoqr;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.ActivityOptions;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.os.Vibrator;
import android.support.annotation.Nullable;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;

import static com.emrehmrc.depoqr.AnaSayfa.MyPREFERENCES;

/**
 * Created by cenah on 3/8/2018.
 */

public class PlasiyerSatisThree extends AppCompatActivity {
    ActionBar ab;
    ConnectionClass connectionClass;
    SharedPreferences sharedPreferences;
    Bundle bundle;
    String incomingKod, incomingAd, incomingDepo, memberid, comid, incomingDepoId, incomingCariId, incomingReader;
    Vibrator vibrator;
    TextView gelenad, gelendepo;
    Button btn_barcodeoku;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_plasiyersatisthree);
        Intent incomingIntent = getIntent();
        this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        vibrator = (Vibrator) getSystemService(VIBRATOR_SERVICE);
        sharedPreferences = getApplicationContext().getSharedPreferences(MyPREFERENCES, Context.MODE_PRIVATE);
        memberid = sharedPreferences.getString("ID", null);
        comid = sharedPreferences.getString("Companiesid", null);
        connectionClass = new ConnectionClass();
        ab = getSupportActionBar();
        ab.setTitle("Plasiyer Satiş");
        ab.setSubtitle("Barkod Satiş");
        ab.setBackgroundDrawable(getResources().getDrawable(R.drawable.arkaplan));
        incomingAd = incomingIntent.getStringExtra("secilenad");
        incomingKod = incomingIntent.getStringExtra("secilenkod");
        incomingDepo = incomingIntent.getStringExtra("secilendepo");
        incomingDepoId = incomingIntent.getStringExtra("secilendepoId");
        incomingCariId = incomingIntent.getStringExtra("secilenCariId");
        gelenad = (TextView) findViewById(R.id.gelenad2);
        gelendepo = (TextView) findViewById(R.id.gelendepo2);
        gelendepo.setText(incomingDepo);
        gelenad.setText(incomingAd);
        btn_barcodeoku = (Button) findViewById(R.id.btn_barcodoku);
        btn_barcodeoku.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(PlasiyerSatisThree.this, CodeReader.class);
                startActivityForResult(i, 1);
            }
        });
    }

    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.plasiyersatis, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.anasayfa) {

            Intent i = new Intent(PlasiyerSatisThree.this, SliderMenu.class);
            bundle = ActivityOptions.makeSceneTransitionAnimation(PlasiyerSatisThree.this)
                    .toBundle();
            finish();
        } else if (id == R.id.geri) {
            finish();
            onBackPressed();

        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (requestCode == 1) {
            if (resultCode == Activity.RESULT_OK) {
                incomingReader = data.getStringExtra("codeid");

            }
            if (resultCode == Activity.RESULT_CANCELED) {
                //Write your code if there's no result
            }
        }
    }


}
