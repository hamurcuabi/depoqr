package com.emrehmrc.depoqr;

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
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Date;

import static com.emrehmrc.depoqr.AnaSayfa.MyPREFERENCES;

public class PlasiyerList extends AppCompatActivity {
    ActionBar ab;
    ConnectionClass connectionClass;
    SharedPreferences sharedPreferences;
    Bundle bundle;
    Vibrator vibrator;
    String memberid,comid;
    EditText cariArama;
    ListView lst_Cari;
    TextView tx_toplam;
    Button btn_satisbasla;
    ProgressBar progressBar;
    ArrayList<PlasiyerListModel> plasiyerArray;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_plasiyerlist);
        this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        vibrator = (Vibrator) getSystemService(VIBRATOR_SERVICE);
        sharedPreferences = getApplicationContext().getSharedPreferences(MyPREFERENCES, Context.MODE_PRIVATE);
        memberid = sharedPreferences.getString("ID", null);
        comid = sharedPreferences.getString("Companiesid", null);
        connectionClass = new ConnectionClass();
        ab = getSupportActionBar();
        ab.setTitle("Plasiyer Satiş");
        ab.setBackgroundDrawable(getResources().getDrawable(R.drawable.arkaplan));
        progressBar = (ProgressBar) findViewById(R.id.pbbarP);
        plasiyerArray = new ArrayList<PlasiyerListModel>();
        btn_satisbasla = (Button) findViewById(R.id.btn_satisbasla);
        tx_toplam = (TextView) findViewById(R.id.tx_toplam);
        cariArama = (EditText) findViewById(R.id.CariArama);

        FillList fillList = new FillList();
        fillList.execute("");


    }


    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.plasiyersatis, menu);
        return super.onCreateOptionsMenu(menu);
    }
    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.anasayfa) {

            Intent i = new Intent(PlasiyerList.this, SliderMenu.class);
            bundle = ActivityOptions.makeSceneTransitionAnimation(PlasiyerList.this).toBundle();
            finish();
        } else if (id == R.id.geri) {
            finish();
            onBackPressed();

        }

        return super.onOptionsItemSelected(item);
    }


    public class FillList extends AsyncTask<String,String,String>{

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

    public static class PlasiyerListModel{
        String id;
        String cariTarih;
        String cariAdi;
        float toplamTutar;
        float kdv;
        float genelTutar;

        public PlasiyerListModel(String id, String cariTarih, String cariAdi, float toplamTutar, float kdv, float genelTutar){
            this.id = id;
            this.cariAdi = cariAdi;
            this.cariTarih = cariTarih;
            this.toplamTutar = toplamTutar;
            this.kdv = kdv;
            this.genelTutar = genelTutar;

        }

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }
        public String getCariTarih() {
            return cariTarih;
        }

        public void setCariTarih(String cariTarih) {
            this.cariTarih = cariTarih;
        }

        public String getCariAdi() {
            return cariAdi;
        }

        public void setCariAdi(String cariAdi) {
            this.cariAdi = cariAdi;
        }

        public float getToplamTutar() {
            return toplamTutar;
        }

        public void setToplamTutar(float toplamTutar) {
            this.toplamTutar = toplamTutar;
        }

        public float getKdv() {
            return kdv;
        }

        public void setKdv(float kdv) {
            this.kdv = kdv;
        }

        public float getGenelTutar() {
            return genelTutar;
        }

        public void setGenelTutar(float genelTutar) {
            this.genelTutar = genelTutar;
        }
    }


}
