package com.emrehmrc.depoqr;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.ActivityOptions;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Vibrator;
import android.support.annotation.Nullable;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;

import static com.emrehmrc.depoqr.AnaSayfa.MyPREFERENCES;

public class PlasiyerProduct extends AppCompatActivity {
    ActionBar ab;
    ConnectionClass connectionClass;
    SharedPreferences sharedPreferences;
    Bundle bundle;
    Vibrator vibrator;
    String memberid, comid;
    ProgressBar progressBar;
    String secilendepo,secilendepoId,disable,incomingCariAd,incomingCariKod,incomingCariId;
    TextView cariAdi,depo;
    LinearLayout relativeLayout3,lastGG;
    Button btn_direkSatis,btn_barkodSatis;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_plasiyer_product);
        this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        vibrator = (Vibrator) getSystemService(VIBRATOR_SERVICE);
        sharedPreferences = getApplicationContext().getSharedPreferences(MyPREFERENCES, Context.MODE_PRIVATE);
        progressBar = (ProgressBar) findViewById(R.id.pbbarP);
        cariAdi = (TextView) findViewById(R.id.tx_cariAdi);
        depo = (TextView) findViewById(R.id.depoProduct);
        btn_barkodSatis = (Button) findViewById(R.id.btn_barkodSatis);
        btn_direkSatis = (Button) findViewById(R.id.btn_direkSatis);
        relativeLayout3 = (LinearLayout) findViewById(R.id.relativeLayout3);
        lastGG = (LinearLayout) findViewById(R.id.lastGG);
        memberid = sharedPreferences.getString("ID", null);
        comid = sharedPreferences.getString("Companiesid", null);
        Intent incomingIntent = getIntent();
        incomingCariAd = sharedPreferences.getString("plasiyerCariAd", null);
        incomingCariKod = sharedPreferences.getString("plasiyerCariKod", null);
        incomingCariId = sharedPreferences.getString("plasiyerCariId", null);
        secilendepo = sharedPreferences.getString("plasiyerDepoAd", null);
        secilendepoId = sharedPreferences.getString("plasiyerDepoId", null);
        disable = incomingIntent.getStringExtra("disable");
        cariAdi.setText(incomingCariAd);
        depo.setText(secilendepo);

        if(disable.equals("disable")){
            relativeLayout3.setVisibility(View.GONE);
            lastGG.setVisibility(View.GONE);
        }
        connectionClass = new ConnectionClass();
        ab = getSupportActionBar();
        ab.setTitle("Plasiyer Satiş");
        ab.setSubtitle("");
        ab.setBackgroundDrawable(getResources().getDrawable(R.drawable.arkaplan));

        btn_barkodSatis.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(PlasiyerProduct.this, PlasiyerSatisThree.class);

                startActivity(intent);
            }
        });
        btn_direkSatis.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(PlasiyerProduct.this, PlasiyerSatisSec.class);
                startActivity(intent);
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
            finish();
            Intent i = new Intent(PlasiyerProduct.this, SliderMenu.class);
            bundle = ActivityOptions.makeSceneTransitionAnimation(PlasiyerProduct.this).toBundle();
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

}
