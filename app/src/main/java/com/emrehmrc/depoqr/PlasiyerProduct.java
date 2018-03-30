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
    Spinner depo;
    TextView cariAdi;
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
        depo = (Spinner) findViewById(R.id.depoSec);
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
        disable = incomingIntent.getStringExtra("disable");
        cariAdi.setText(incomingCariAd);


        if(disable.equals("disable")){
            relativeLayout3.setVisibility(View.GONE);
            lastGG.setVisibility(View.GONE);
        }
        connectionClass = new ConnectionClass();
        ab = getSupportActionBar();
        ab.setTitle("Plasiyer Satiş");
        ab.setSubtitle("");
        ab.setBackgroundDrawable(getResources().getDrawable(R.drawable.arkaplan));

        FillList filldepo = new FillList();
        filldepo.execute("");

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

    @SuppressLint("NewApi")
    public class FillList extends AsyncTask<String, String, String> {
        String z = "";
        ArrayList<Depolar> depolars = new ArrayList<>();

        @Override
        protected void onPreExecute() {


        }

        @Override
        protected void onPostExecute(String r) {

            ArrayAdapter<Depolar> adapter = new ArrayAdapter<Depolar>(getApplicationContext(), R.layout.support_simple_spinner_dropdown_item, depolars);
            adapter.setDropDownViewResource(R.layout.support_simple_spinner_dropdown_item);
            depo.setAdapter(adapter);
            depo.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                    ((TextView) parent.getChildAt(0)).setGravity(Gravity.CENTER);
                    ((TextView) parent.getChildAt(0)).setTextColor(getResources().getColor(R.color.white));
                    Depolar depolar1;
                    depolar1 = (Depolar) depo.getItemAtPosition(position);
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.putString("plasiyerDepoAd", depolar1.getDepoadi());
                    editor.putString("plasiyerDepoId", depolar1.getDepono());
                    editor.commit();

                }

                @Override
                public void onNothingSelected(AdapterView<?> parent) {

                }
            });
        }


        @Override
        protected String doInBackground(String... params) {

            try {
                Connection con = connectionClass.CONN();
                if (con == null) {
                    z = "Error in connection with SQL server";
                } else {
                    String query = "SELECT distinct WAREHOUSEID , NAME FROM " +
                            "VW_WAREHOUSEPERMISSION where MEMBERID='" + memberid + "' and ISACTIVE='1' and " +
                            "ISSHOW='1'  and WAREHOUSEMENUID='" + 3 + "'  order by NAME";
                    PreparedStatement ps = con.prepareStatement(query);
                    ResultSet rs = ps.executeQuery();

                    while (rs.next()) {
                        depolars.add(new Depolar(rs.getString
                                ("NAME"), rs.getString("WAREHOUSEID")));

                    }
                    z = "Başarılı";
                }
            } catch (Exception ex) {
                z = "Veri Çekme Hatası";

            }
            return z;
        }
    }

    private class Depolar {
        private String depoadi;
        private String depono;

        public Depolar() {
        }

        public Depolar(String depoadi, String depono) {
            this.depoadi = depoadi;
            this.depono = depono;
        }

        public String getDepoadi() {
            return depoadi;
        }

        public void setDepoadi(String depoadi) {
            this.depoadi = depoadi;
        }

        public String getDepono() {
            return depono;
        }

        public void setDepono(String depono) {
            this.depono = depono;
        }


        @Override
        public String toString() {
            return depoadi;
        }


    }

}
