package com.emrehmrc.depoqr;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.ActivityOptions;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Vibrator;
import android.support.annotation.Nullable;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
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
import android.widget.Toast;

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
    String secilendepo, secilendepoId, disable, incomingCariAd, incomingCariKod, incomingCariId;
    TextView cariAdi, depo;
    LinearLayout relativeLayout3, lastGG;
    Button btn_direkSatis, btn_barkodSatis, btn_tamamla;
    RecyclerView lst_products;

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
        lst_products = (RecyclerView) findViewById(R.id.lst_products);
        btn_tamamla = (Button) findViewById(R.id.btn_tamamla);
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

        if (disable.equals("disable")) {
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
                Intent intent = new Intent(PlasiyerProduct.this, PlasiyerSatisSec.class);

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
        btn_tamamla.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (lst_products == null) {
                    Toast.makeText(getApplicationContext(), "Satış Bulunamadı.", Toast.LENGTH_LONG).show();
                } else {
                    AlertDialog.Builder builder2 = new AlertDialog.Builder(PlasiyerProduct.this);
                    builder2.setTitle("UYARI!");
                    builder2.setMessage("Satışı tamamlamak istediğinizden emin misiniz?");
                    builder2.setNegativeButton("EVET", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            Tamamla tamamla = new Tamamla();
                            tamamla.execute("");
                        }
                    });

                    builder2.setPositiveButton("HAYIR", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {

                        }
                    });
                    builder2.show();
                }
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

    public class FillList extends AsyncTask<String, String, String> {

        @Override
        protected void onPreExecute() {
            progressBar.setVisibility(View.VISIBLE);
        }

        @Override
        protected void onPostExecute(String s) {
            progressBar.setVisibility(View.GONE);
        }

        @Override
        protected String doInBackground(String... strings) {
            return null;
        }
    }

    public class Tamamla extends AsyncTask<String, String, String> {


        @Override
        protected void onPreExecute() {
            progressBar.setVisibility(View.VISIBLE);
        }

        @Override
        protected void onPostExecute(String s) {
            progressBar.setVisibility(View.GONE);
        }

        @Override
        protected String doInBackground(String... strings) {
            return null;
        }
    }
}

