package com.emrehmrc.depoqr;

import android.annotation.TargetApi;
import android.app.ActivityOptions;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Vibrator;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuItem;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import static com.emrehmrc.depoqr.AnaSayfa.MyPREFERENCES;

public class SarfDuzenle extends AppCompatActivity {
    ActionBar ab;
    ConnectionClass connectionClass;
    SharedPreferences sharedPreferences;
    Bundle bundle;
    Vibrator vibrator;
    String memberid, comid;
    TextView tx_tarih,tx_kullanici,tx_cariPersonel,tx_barkodSayisi,tx_aciklama;
    ProgressBar pbbarS;
    RecyclerView recyclerview;
    Button btn_cikar;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sarf_duzenle);
        this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        vibrator = (Vibrator) getSystemService(VIBRATOR_SERVICE);
        sharedPreferences = getApplicationContext().getSharedPreferences(MyPREFERENCES, Context.MODE_PRIVATE);
        memberid = sharedPreferences.getString("ID", null);
        comid = sharedPreferences.getString("Companiesid", null);
        connectionClass = new ConnectionClass();
        ab = getSupportActionBar();
        ab.setTitle("Sarf");
        ab.setSubtitle("Satiş Detay");
        ab.setBackgroundDrawable(getResources().getDrawable(R.drawable.arkaplan));
        tx_tarih = (TextView) findViewById(R.id.tx_tarih);
        tx_kullanici = (TextView) findViewById(R.id.tx_kullanici);
        tx_cariPersonel = (TextView) findViewById(R.id.tx_cariPersonel);
        tx_barkodSayisi = (TextView) findViewById(R.id.tx_barkodSayisi);
        tx_aciklama = (TextView) findViewById(R.id.tx_aciklama);
        pbbarS = (ProgressBar) findViewById(R.id.pbbarS);
        recyclerview = (RecyclerView) findViewById(R.id.recyclerview);
        btn_cikar = (Button) findViewById(R.id.btn_cikar);
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
            Intent i = new Intent(SarfDuzenle.this, SliderMenu.class);
            bundle = ActivityOptions.makeSceneTransitionAnimation(SarfDuzenle.this).toBundle();
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
