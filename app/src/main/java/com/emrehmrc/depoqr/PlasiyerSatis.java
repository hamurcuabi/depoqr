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
import android.support.annotation.Nullable;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;

import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;


import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;


import static com.emrehmrc.depoqr.AnaSayfa.MyPREFERENCES;


public class PlasiyerSatis extends AppCompatActivity {
    ActionBar ab;
    Bundle bundle;
    Spinner depo;
    SharedPreferences sharedPreferences;
    String memberid, comid;
    ConnectionClass connectionClass;
    EditText cariadi;
    ListView newlist;
    Button moveto, moveto2;
    TextView secilen, secilen2;
    private CariAdapter adapter;
    String secilenkod, secilenad, secilendepo, secilendepoId, secilenCariId;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_plasiyer_satis);
        this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        sharedPreferences = getApplicationContext().getSharedPreferences(MyPREFERENCES, Context.MODE_PRIVATE);
        memberid = sharedPreferences.getString("ID", null);
        comid = sharedPreferences.getString("Companiesid", null);
        connectionClass = new ConnectionClass();
        ab = getSupportActionBar();
        ab.setTitle("Plasiyer Satiş");
        ab.setSubtitle("Cari Seçimi");
        ab.setBackgroundDrawable(getResources().getDrawable(R.drawable.arkaplan));
        depo = (Spinner) findViewById(R.id.depoSec);
        secilen = (TextView) findViewById(R.id.secilen);
        secilen2 = (TextView) findViewById(R.id.secilen2);
        cariadi = (EditText) findViewById(R.id.carisec);
        newlist = (ListView) findViewById(R.id.lstcari);
        moveto = (Button) findViewById(R.id.moveto3);
        moveto2 = (Button) findViewById(R.id.moveto2);
        moveto2.setEnabled(false);
        moveto.setEnabled(false);

       /* FillList filldepo = new FillList();
        filldepo.execute("");*/
        FillList2 fillcari = new FillList2();
        fillcari.execute("");

      /*  moveto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(PlasiyerSatis.this, PlasiyerSatisSec.class);
                intent.putExtra("secilenad", secilenad);
                intent.putExtra("secilenkod", secilenkod);
                intent.putExtra("secilendepo", secilendepo);
                intent.putExtra("secilendepoId", secilendepoId);
                intent.putExtra("secilenCariId", secilenCariId);
                startActivity(intent);

            }
        });*/
        moveto2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(PlasiyerSatis.this, PlasiyerProduct.class);
                /*intent.putExtra("secilenad", secilenad);
                intent.putExtra("secilenkod", secilenkod);
                //intent.putExtra("secilendepo", secilendepo);
               //intent.putExtra("secilendepoId", secilendepoId);
                intent.putExtra("secilenCariId", secilenCariId);
                */intent.putExtra("disable", "undisable");
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putString("plasiyerCariAd", secilenad);
                editor.putString("plasiyerCariKod", secilenkod);
                editor.putString("plasiyerCariId", secilenCariId);
                editor.commit();
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
            Intent i = new Intent(PlasiyerSatis.this, SliderMenu.class);
            bundle = ActivityOptions.makeSceneTransitionAnimation(PlasiyerSatis.this).toBundle();
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
  /*  public class FillList extends AsyncTask<String, String, String> {
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
                    // Depolar depolar = (Depolar) depo.getItemAtPosition(position);
                    Depolar depolar1 = new Depolar();
                    depolar1 = (Depolar) depo.getItemAtPosition(position);
                    secilendepo = depolar1.getDepoadi();
                    secilendepoId = depolar1.getDepono();
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
    }*/

  /*  private class Depolar {
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


    }*/

    public class FillList2 extends AsyncTask<String, String, String> {
        String w = "";
        ArrayList<ModelCari> carilers = new ArrayList<>();

        @Override
        protected void onPreExecute() {

        }

        @Override
        protected void onPostExecute(String s) {
            adapter = new CariAdapter(getApplicationContext(), carilers);
            newlist.setAdapter(adapter);
            cariadi.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {

                    adapter.getFilter().filter(s.toString());
                }

                @Override
                public void afterTextChanged(Editable s) {


                }
            });
            newlist.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    secilen2.setText("");
                    secilen.setText("");
                    ModelCari modelCari = new ModelCari();
                    modelCari = (ModelCari) newlist.getItemAtPosition(position);
                    secilenkod = modelCari.getCarikod();
                    secilenad = modelCari.getCariadi();
                    secilenCariId = modelCari.getCariId();
                    secilen.append(secilenkod);
                    secilen2.append(secilenad);
                    moveto.setEnabled(true);
                    moveto2.setEnabled(true);
                }
            });
        }

        @Override
        protected String doInBackground(String... strings) {

            try {
                Connection con = connectionClass.CONN();
                if (con == null) {
                    w = "Error in connection with SQL server";
                } else {
                    String query = "SELECT CODE , NAME , ID FROM " +
                            "VW_CURRENTDETAIL where COMPANIESID='" + comid + "'";
                    PreparedStatement ps = con.prepareStatement(query);
                    ResultSet rs = ps.executeQuery();

                    while (rs.next()) {
                        carilers.add(new ModelCari(rs.getString
                                ("NAME"), rs.getString("CODE"), rs.getString("ID")));
                    }
                    w = "Başarılı";
                }
            } catch (Exception ex) {
                w = "Veri Çekme Hatası";

            }
            return w;
        }
    }

}


