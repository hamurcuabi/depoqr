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
import android.text.Editable;
import android.text.TextWatcher;
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
import android.widget.TextView;
import android.widget.Toast;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Date;

import static com.emrehmrc.depoqr.AnaSayfa.MyPREFERENCES;

public class PlasiyerList extends AppCompatActivity {
    ActionBar ab;
    ConnectionClass connectionClass;
    SharedPreferences sharedPreferences;
    Bundle bundle;
    Vibrator vibrator;
    String memberid, comid;
    EditText cariArama;
    ListView lst_Cari;
    TextView tx_toplam;
    Button btn_satisbasla, btn_yenisatis;
    ProgressBar progressBar;

    ArrayAdapter<Depolar> adapterDepo;
    ArrayList<Depolar> depolars = new ArrayList<>();
    private PlasiyerListAdapter adapter;
    float toplam;
    ImageView btn_drop, btn_filtre;
    AutoCompleteTextView tx_deposec;
    String secilenDepoName, getSecilenDepoId;

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
        ab.setSubtitle("Satiş Listesi");
        ab.setBackgroundDrawable(getResources().getDrawable(R.drawable.arkaplan));
        progressBar = (ProgressBar) findViewById(R.id.pbbarP);
        btn_satisbasla = (Button) findViewById(R.id.btn_satisbasla);
        btn_yenisatis = (Button) findViewById(R.id.btn_yeniSatis);
        btn_satisbasla.setEnabled(false);
        tx_toplam = (TextView) findViewById(R.id.tx_toplam);
        cariArama = (EditText) findViewById(R.id.CariArama);
        lst_Cari = (ListView) findViewById(R.id.lst_Cari);
        btn_drop = (ImageView) findViewById(R.id.btn_drop);
        btn_filtre = (ImageView) findViewById(R.id.btn_filtre);
        tx_deposec = (AutoCompleteTextView) findViewById(R.id.tx_deposec);
        final FillList fillList = new FillList();
        fillList.execute("");
        FillListDepo fillListDepo = new FillListDepo();
        fillListDepo.execute("");
        btn_yenisatis.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(PlasiyerList.this, PlasiyerSatis.class);
                startActivity(intent);
            }
        });
        btn_satisbasla.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String disable = "disable";
                Intent intent = new Intent(PlasiyerList.this, PlasiyerProduct.class);
                intent.putExtra("disable", disable);
                startActivity(intent);
            }
        });
        btn_drop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                tx_deposec.showDropDown();
            }
        });
        btn_filtre.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!tx_deposec.getText().toString().isEmpty()) {
                    FillList2 fillList2 = new FillList2();
                    fillList2.execute("");
                } else {
                    FillList fillList1 = new FillList();
                    fillList1.execute("");
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
            Intent i = new Intent(PlasiyerList.this, SliderMenu.class);
            bundle = ActivityOptions.makeSceneTransitionAnimation(PlasiyerList.this).toBundle();
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
        String w = "";
        ArrayList<PlasiyerListModel> plasiyerArray = new ArrayList<PlasiyerListModel>();
        boolean exist = false;

        @Override
        protected void onPreExecute() {
            progressBar.setVisibility(View.VISIBLE);
        }

        @Override
        protected void onPostExecute(String s) {
            progressBar.setVisibility(View.GONE);
            if (exist) {
                adapter = new PlasiyerListAdapter(getApplicationContext(), plasiyerArray);
                lst_Cari.setAdapter(adapter);
                tx_toplam.setText("" + toplam);
                lst_Cari.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        PlasiyerList.PlasiyerListModel plasiyerListModel;
                        plasiyerListModel = (PlasiyerListModel) lst_Cari.getItemAtPosition(position);
                        btn_satisbasla.setEnabled(true);
                    }
                });
                cariArama.addTextChangedListener(new TextWatcher() {
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
            } else {
                Toast.makeText(getApplicationContext(), "Satış Bulunamadı.", Toast.LENGTH_LONG).show();
            }
        }

        @Override
        protected String doInBackground(String... strings) {
            try {
                Connection con = connectionClass.CONN();
                if (con == null) {
                    w = "Error in connection with SQL server";
                } else {
                    String query = "SELECT * FROM VW_WAREHOUSEPLASIER where WAREHOUSENAME = '" + tx_deposec.getText().toString() + "' ";
                    PreparedStatement ps = con.prepareStatement(query);
                    ResultSet rs = ps.executeQuery();

                    while (rs.next()) {
                        exist = true;
                        toplam += rs.getFloat("TOTAL");
                        plasiyerArray.add(new PlasiyerListModel(rs.getString("PLASIERCODE"),
                                rs.getString("DATE"),
                                rs.getString("CURRENTNAME"),
                                rs.getFloat("TOTALPRICE"),
                                rs.getFloat("TOTALKDV"),
                                rs.getFloat("TOTAL"),
                                rs.getString("WAREHOUSENAME")));
                    }
                    w = "Başarılı";
                }
            } catch (Exception ex) {
                w = "Veri Çekme Hatası";

            }
            return w;
        }
    }

    public class FillList2 extends AsyncTask<String, String, String> {
        String w = "";
        ArrayList<PlasiyerListModel> plasiyerArray = new ArrayList<PlasiyerListModel>();
        boolean exist = false;

        @Override
        protected void onPreExecute() {
            progressBar.setVisibility(View.VISIBLE);
        }

        @Override
        protected void onPostExecute(String s) {
            progressBar.setVisibility(View.GONE);
            if (exist) {
                adapter = new PlasiyerListAdapter(getApplicationContext(), plasiyerArray);
                lst_Cari.setAdapter(adapter);
                tx_toplam.setText("" + toplam);
                lst_Cari.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        PlasiyerList.PlasiyerListModel plasiyerListModel;
                        plasiyerListModel = (PlasiyerListModel) lst_Cari.getItemAtPosition(position);
                        btn_satisbasla.setEnabled(true);
                    }
                });
                cariArama.addTextChangedListener(new TextWatcher() {
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
            } else {
                Toast.makeText(getApplicationContext(), "Satış Bulunamadı.", Toast.LENGTH_LONG).show();
            }

        }

        @Override
        protected String doInBackground(String... strings) {
            try {
                Connection con = connectionClass.CONN();
                if (con == null) {
                    w = "Error in connection with SQL server";
                } else {
                    String query = "SELECT * FROM VW_WAREHOUSEPLASIER ";
                    PreparedStatement ps = con.prepareStatement(query);
                    ResultSet rs = ps.executeQuery();

                    while (rs.next()) {
                        exist = true;
                        toplam += rs.getFloat("TOTAL");
                        plasiyerArray.add(new PlasiyerListModel(rs.getString("PLASIERCODE"),
                                rs.getString("DATE"),
                                rs.getString("CURRENTNAME"),
                                rs.getFloat("TOTALPRICE"),
                                rs.getFloat("TOTALKDV"),
                                rs.getFloat("TOTAL"),
                                rs.getString("WAREHOUSENAME")));
                    }
                    w = "Başarılı";
                }
            } catch (Exception ex) {
                w = "Veri Çekme Hatası";

            }
            return w;
        }
    }

    public static class PlasiyerListModel {
        String plasiyercode;
        String cariTarih;
        String cariAdi;
        float toplamTutar;
        float kdv;
        float genelTutar;
        String depoName;


        public PlasiyerListModel(String plasiyercode, String cariTarih, String cariAdi, float toplamTutar, float kdv, float genelTutar, String depoName) {

            this.cariAdi = cariAdi;
            this.cariTarih = cariTarih;
            this.toplamTutar = toplamTutar;
            this.kdv = kdv;
            this.genelTutar = genelTutar;
            this.depoName = depoName;
            this.plasiyercode = plasiyercode;

        }

        public String getPlasiyercode() {
            return plasiyercode;
        }

        public void setPlasiyercode(String plasiyercode) {
            this.plasiyercode = plasiyercode;
        }

        public String getDepoName() {
            return depoName;
        }

        public void setDepoName(String depoName) {
            this.depoName = depoName;
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

    @SuppressLint("NewApi")
    public class FillListDepo extends AsyncTask<String, String, String> {
        String z = "";


        @Override
        protected void onPreExecute() {


        }

        @Override
        protected void onPostExecute(String r) {

            adapterDepo = new ArrayAdapter<Depolar>(getApplicationContext(), android.R.layout.simple_dropdown_item_1line, depolars);
            tx_deposec.setAdapter(adapterDepo);
            tx_deposec.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                    Depolar depolar = (Depolar) parent.getItemAtPosition(position);
                    secilenDepoName = depolar.getDepoadi();
                    getSecilenDepoId = depolar.getDepono();
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
