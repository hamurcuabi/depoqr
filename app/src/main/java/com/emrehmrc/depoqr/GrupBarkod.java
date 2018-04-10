package com.emrehmrc.depoqr;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.ActivityOptions;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.AudioManager;
import android.media.ToneGenerator;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Vibrator;
import android.support.annotation.Nullable;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;

import static com.emrehmrc.depoqr.AnaSayfa.MyPREFERENCES;

public class GrupBarkod extends AppCompatActivity {
    ActionBar ab;
    ConnectionClass connectionClass;
    SharedPreferences sharedPreferences;
    Bundle bundle;
    Vibrator vibrator;
    String memberid, comid, codeid;
    EditText tx_bagli;
    AutoCompleteTextView tx_anabarkod;
    Button btnQr, btnDevam;
    ImageView btn_ekle;
    ListView lst_grup;
    ArrayList<String> BarkodArray = new ArrayList<>();
    ArrayList<Gruplar> gruplars = new ArrayList<>();
    String anabarkod, secilenGrubId;
    Spinner spngrup;
    ImageView btnEkle;
    EditText edtKod;
    ProgressBar pbbar;

    RecyclerView recyclerView;
    DependedBarcodesAdaptor adaptor;
    ArrayList<DependedBarcodes> datalist;
    ArrayList<DependedBarcodes> datalistcame;
    DependedBarcodes dependedBarcodes;

    ToneGenerator toneG;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_grupbarkod);
        this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        vibrator = (Vibrator) getSystemService(VIBRATOR_SERVICE);

        datalist = new ArrayList<DependedBarcodes>();
        datalistcame = new ArrayList<DependedBarcodes>();
        recyclerView = (RecyclerView) findViewById(R.id.recyclerview);

        Bundle extras = getIntent().getExtras();
        anabarkod = extras.getString("anabarkod");

        pbbar = (ProgressBar) findViewById(R.id.pbarloading);
        edtKod = (EditText) findViewById(R.id.edtKodEnter);
        btn_ekle = (ImageView) findViewById(R.id.btnEkle);

        btnQr = (Button) findViewById(R.id.btnQr);
        btnDevam = (Button) findViewById(R.id.btndevam);
        sharedPreferences = getApplicationContext().getSharedPreferences(MyPREFERENCES, Context.MODE_PRIVATE);
        memberid = sharedPreferences.getString("ID", null);
        comid = sharedPreferences.getString("Companiesid", null);
        connectionClass = new ConnectionClass();
        ab = getSupportActionBar();
        ab.setTitle("GRUP BARKOD");
        ab.setSubtitle("Barkod Okut");
        ab.setBackgroundDrawable(getResources().getDrawable(R.drawable.arkaplan));
        spngrup = (Spinner) findViewById(R.id.spn_grup);
        final FillGrups fillGrups = new FillGrups();
        fillGrups.execute("");
        GetGrupProducts getGrupProducts = new GetGrupProducts();
        //Olması gerek Ürünleri al
        String q = "SELECT * from VW_GRUPPRODUCT where  PARENTPRODUCTID='" + anabarkod + "'";
        getGrupProducts.execute(q);
        toneG = new ToneGenerator(AudioManager.STREAM_ALARM, 100);


        btn_ekle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                FillProducts fillProducts = new FillProducts();
                String query = "SELECT * from VW_GRUPPRODUCT where CHILDBARCODENO='" + edtKod
                        .getText().toString() + "' and PARENTPRODUCTID='" + anabarkod + "'";
                fillProducts.execute(query);
            }
        });
        btnQr.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                Intent i = new Intent(GrupBarkod.this, CodeReaderGrupBarcode.class);
                startActivityForResult(i, 1);

            }
        });
        btnDevam.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (IsDone()) {

                    Intent i = new Intent(getApplicationContext(), GrupBarkodSecondStep.class);
                    Bundle bundle=new Bundle();
                    bundle.putSerializable("MyClass",datalist);
                    i.putExtras(bundle);
                    startActivity(i);


                }


            }
        });


    }

    public boolean IsDone() {

        int d = 0;
        String eksikler;

        if (datalist.size() != 0 && datalistcame.size() != 0) {

            for (int i = 0; i < datalist.size(); i++) {

                for (int j = 0; j < datalist.size(); j++) {

                    if (datalist.get(i).getCode().equals(datalistcame.get(j).getCode())) {
                        d++;
                        break;

                    }
                }
            }
        }
        else {
            Intent intent = new Intent(getBaseContext(), UyariBildirim.class);
            intent.putExtra("UYARI", "LİSTEDE ÜRÜN BULUNAMDI!!");
            startActivity(intent);
            return false;

        }

        if (d == datalist.size()) {

            return true;

        } else {
            Intent intent = new Intent(getBaseContext(), UyariBildirim.class);
            intent.putExtra("UYARI", "EKLENMEYEN " + (datalist.size() - d) + " ÜRÜN BULUNDU!!");
            startActivity(intent);
            return false;
        }


    }


    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.plasiyersatis, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.anasayfa) {

            Intent i = new Intent(GrupBarkod.this, SliderMenu.class);
            bundle = ActivityOptions.makeSceneTransitionAnimation(GrupBarkod.this).toBundle();
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
                codeid = data.getStringExtra("codeid");
                FillProducts fillProducts = new FillProducts();
                String query = "SELECT * from VW_GRUPPRODUCT where CHILDPRODUCTID='" + codeid + "' and " +
                        "PARENTPRODUCTID='" + anabarkod + "'";
                fillProducts.execute(query);
            }
        }
    }

    @SuppressLint("NewApi")
    public class FillProducts extends AsyncTask<String, String, String> {
        String z = "";
        boolean isEmpty;

        @Override
        protected void onPreExecute() {

            isEmpty = true;
            pbbar.setVisibility(View.VISIBLE);
        }

        @Override
        protected void onPostExecute(String r) {
            pbbar.setVisibility(View.GONE);
            for (int i = 0; i < datalist.size() - 1; i++) {

                for (int j = i + 1; j < datalist.size(); j++) {
                    if (datalist.get(i).getCode().equals(datalist.get(j).getCode())) {
                        datalist.remove(j);
                    }


                }
            }


            if (!isEmpty) {
                toneG.startTone(ToneGenerator.TONE_CDMA_ALERT_CALL_GUARD, 200);
                adaptor = new DependedBarcodesAdaptor(getApplicationContext(), datalist);
                recyclerView.setAdapter(adaptor);
                LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getApplicationContext());
                linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
                recyclerView.setLayoutManager(linearLayoutManager);

            } else {

                toneG.startTone(ToneGenerator.TONE_CDMA_CALL_SIGNAL_ISDN_PING_RING, 200);
                Intent intent = new Intent(getBaseContext(), UyariBildirim.class);
                intent.putExtra("UYARI", "GRUP ÜYESİ DEĞİLDİR!");
                startActivity(intent);
            }


        }

        @Override
        protected String doInBackground(String... params) {

            try {
                Connection con = connectionClass.CONN();
                if (con == null) {
                    z = "Error in connection with SQL server";
                } else {

                    PreparedStatement ps = con.prepareStatement(params[0]);
                    ResultSet rs = ps.executeQuery();


                    while (rs.next()) {
                        dependedBarcodes = new DependedBarcodes();
                        dependedBarcodes.setCheck(true);
                        dependedBarcodes.setCode(rs.getString("CHILDPRODUCTID"));
                        dependedBarcodes.setName(rs.getString("CHILDNAME"));
                        dependedBarcodes.setParentID(rs.getString("PARENTPRODUCTID"));
                        datalist.add(dependedBarcodes);

                        isEmpty = false;

                    }


                    z = "Başarılı";
                }
            } catch (Exception ex) {
                ex.printStackTrace();
                z = "Veri Çekme Hatası";

            }
            return z;
        }
    }

    @SuppressLint("NewApi")
    public class FillGrups extends AsyncTask<String, String, String> {
        String z = "";


        @Override
        protected void onPreExecute() {

            spngrup.setAdapter(null);
            gruplars.clear();
            pbbar.setVisibility(View.VISIBLE);
        }

        @Override
        protected void onPostExecute(String r) {

        pbbar.setVisibility(View.GONE);
            ArrayAdapter<Gruplar> adapter = new ArrayAdapter<Gruplar>(getApplicationContext
                    (), R.layout.specialspinner, gruplars);
            adapter.setDropDownViewResource(R.layout.support_simple_spinner_dropdown_item);
            spngrup.setAdapter(adapter);

            spngrup.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                    Gruplar gruplar = (Gruplar) spngrup.getItemAtPosition(position);
                    ((TextView) parent.getChildAt(0)).setGravity(Gravity.CENTER);
                    ((TextView) parent.getChildAt(0)).setTextColor(getResources().getColor(R.color.white));





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
                    String query = "SELECT distinct PARENTPRODUCTID, PARENTNAME from VW_GRUPPRODUCT";
                    PreparedStatement ps = con.prepareStatement(query);
                    ResultSet rs = ps.executeQuery();


                    while (rs.next()) {
                        gruplars.add(new Gruplar(rs.getString("PARENTNAME"), rs.getString("PARENTPRODUCTID")));

                    }


                    z = "Başarılı";
                }
            } catch (Exception ex) {
                ex.printStackTrace();
                z = "Veri Çekme Hatası";

            }
            return z;
        }
    }

    @SuppressLint("NewApi")
    public class GetGrupProducts extends AsyncTask<String, String, String> {
        String z = "";
        boolean isEmpty;

        @Override
        protected void onPreExecute() {

            isEmpty = true;
            pbbar.setVisibility(View.VISIBLE);

        }

        @Override
        protected void onPostExecute(String r) {
            pbbar.setVisibility(View.GONE);

        }

        @Override
        protected String doInBackground(String... params) {

            try {
                Connection con = connectionClass.CONN();
                if (con == null) {
                    z = "Error in connection with SQL server";
                } else {

                    PreparedStatement ps = con.prepareStatement(params[0]);
                    ResultSet rs = ps.executeQuery();


                    while (rs.next()) {
                        dependedBarcodes = new DependedBarcodes();
                        dependedBarcodes.setCheck(true);
                        dependedBarcodes.setCode(rs.getString("CHILDPRODUCTID"));
                        dependedBarcodes.setName(rs.getString("CHILDNAME"));
                        datalistcame.add(dependedBarcodes);


                    }


                    z = "Başarılı";
                }
            } catch (Exception ex) {
                ex.printStackTrace();
                z = "Veri Çekme Hatası";

            }
            return z;
        }
    }

    private class Gruplar {

        private String grubAdi;
        private String grubId;

        public Gruplar(String grubAdi) {
            this.grubAdi = grubAdi;
        }

        public Gruplar() {
        }

        public Gruplar(String grubAdi, String grubId) {
            this.grubAdi = grubAdi;
            this.grubId = grubId;
        }

        public String toString() {
            return grubAdi;
        }

        public String getGrubId() {
            return grubId;
        }

        public void setGrubId(String grubId) {
            this.grubId = grubId;
        }

        public String getGrubAdi() {
            return grubAdi;
        }

        public void setGrubAdi(String grubAdi) {
            this.grubAdi = grubAdi;
        }
    }

    @Override
    protected void onResume() {
        super.onResume();

    }
}


