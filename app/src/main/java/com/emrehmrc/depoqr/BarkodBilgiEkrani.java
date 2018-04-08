package com.emrehmrc.depoqr;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.ActivityOptions;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Vibrator;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.UUID;

import static com.emrehmrc.depoqr.AnaSayfa.MyPREFERENCES;

public class BarkodBilgiEkrani extends AppCompatActivity {
    Bundle bundle;
    ActionBar ab;
    ConnectionClass connectionClass;
    SharedPreferences sharedPreferences;
    Vibrator vibrator;
    String memberid, comid, codeid;
    AutoCompleteTextView tx_barkodsec;
    ImageView btn_drop;
    Button btn_qr, btn_barkodgir;
    UUID uuid;
    String PorB2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_barkod_bilgi_ekrani);
        this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        vibrator = (Vibrator) getSystemService(VIBRATOR_SERVICE);
        sharedPreferences = getApplicationContext().getSharedPreferences(MyPREFERENCES, Context.MODE_PRIVATE);
        memberid = sharedPreferences.getString("ID", null);
        comid = sharedPreferences.getString("Companiesid", null);
        connectionClass = new ConnectionClass();
        ab = getSupportActionBar();
        ab.setTitle("Barkod Bilgi Ekranı");
        ab.setBackgroundDrawable(getResources().getDrawable(R.drawable.arkaplan));
        tx_barkodsec = (AutoCompleteTextView) findViewById(R.id.tx_barkodsec);
        btn_drop = (ImageView) findViewById(R.id.btn_drop);
        btn_qr = (Button) findViewById(R.id.btn_qr);
        btn_barkodgir = (Button) findViewById(R.id.btn_barkodgir);

        FillBarkods fillBarkods = new FillBarkods();
        fillBarkods.execute("");
        btn_qr.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent i = new Intent(BarkodBilgiEkrani.this, CodeReaderForSevkiyat.class);
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
            finish();
            Intent i = new Intent(BarkodBilgiEkrani.this, SliderMenu.class);
            bundle = ActivityOptions.makeSceneTransitionAnimation(BarkodBilgiEkrani.this).toBundle();
            startActivity(i);


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
                codeid = data.getStringExtra("codeid");
            }
            try {
                uuid = UUID.fromString(codeid);
                PorB2 = "fdsfds";

            } catch (Exception ex) {
                PorB2 = codeid.substring(0, 1);
                codeid = codeid.substring(1, codeid.length());

            }
            if (PorB2.equals("P")) {
                Toast.makeText(getApplicationContext(), "PALET GIRILEMEZ!", Toast.LENGTH_SHORT).show();
            } else if (PorB2.equals("B")) {

            } else {


            }
        }
    }

    @SuppressLint("NewApi")
    public class FillBarkods extends AsyncTask<String, String, String> {
        String z = "";


        @Override
        protected void onPreExecute() {


        }

        @Override
        protected void onPostExecute(String r) {
        }

        @Override
        protected String doInBackground(String... strings) {
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

                    }
                    z = "Başarılı";
                }
            } catch (Exception ex) {
                z = "Veri Çekme Hatası";

            }
            return z;

        }
    }
}
