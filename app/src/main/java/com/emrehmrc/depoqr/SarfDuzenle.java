package com.emrehmrc.depoqr;

import android.annotation.TargetApi;
import android.app.ActivityOptions;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Vibrator;
import android.support.annotation.RequiresApi;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.view.ContextThemeWrapper;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.PopupMenu;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;

import static com.emrehmrc.depoqr.AnaSayfa.MyPREFERENCES;

public class SarfDuzenle extends AppCompatActivity implements PopupMenu.OnMenuItemClickListener {
    ActionBar ab;
    ConnectionClass connectionClass;
    SharedPreferences sharedPreferences;
    Bundle bundle;
    Vibrator vibrator;
    String memberid, comid, secilenSarf;
    TextView tx_tarih, tx_kullanici, tx_cariPersonel, tx_barkodSayisi, tx_aciklama;
    ProgressBar pbbarS;
    Button btn_cikar;
    String tarih, kullaici, cariPersonel, aciklama;
    int barkodsayisi;
    SarfDuzenleAdapter adapter;
    ListView listView;
    String silinecekId;
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
        ab.setSubtitle("Sarf Detay");
        Intent incomingIntent = getIntent();
        secilenSarf = incomingIntent.getStringExtra("secilenSarf");
        ab.setBackgroundDrawable(getResources().getDrawable(R.drawable.arkaplan));
        tx_tarih = (TextView) findViewById(R.id.tx_tarih);
        tx_kullanici = (TextView) findViewById(R.id.tx_kullanici);
        tx_cariPersonel = (TextView) findViewById(R.id.tx_cariPersonel);
        tx_barkodSayisi = (TextView) findViewById(R.id.tx_barkodSayisi);
        tx_aciklama = (TextView) findViewById(R.id.tx_aciklama);
        pbbarS = (ProgressBar) findViewById(R.id.pbbarS);
        listView = (ListView) findViewById(R.id.lst_Cari);
        btn_cikar = (Button) findViewById(R.id.btn_cikar);
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

    @Override
    public boolean onMenuItemClick(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_Sil:
                DeletePro silenecekler = new DeletePro();
                silenecekler.execute("");
                return true;
            default:
                return false;
        }
    }

    public class FillList extends AsyncTask<String, String, String> {
        String w = "";
        ArrayList<SarfProducts> productsArrayList = new ArrayList<SarfProducts>();
        boolean exist = false;

        @Override
        protected void onPreExecute() {

            pbbarS.setVisibility(View.VISIBLE);
            barkodsayisi = 0;
        }

        @Override
        protected void onPostExecute(String s) {
            pbbarS.setVisibility(View.GONE);
            if (exist) {
                if(aciklama==null) tx_aciklama.setText("YOKTUR");
                else tx_aciklama.setText(aciklama);
                tx_barkodSayisi.setText(""+barkodsayisi);
                tx_cariPersonel.setText(cariPersonel);
                tx_kullanici.setText(kullaici);
                tx_tarih.setText(tarih);
                adapter = new SarfDuzenleAdapter(getApplicationContext(), productsArrayList);
                listView.setAdapter(adapter);
                listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
                    @Override
                    public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                       SarfProducts plasiyerListModel;
                        plasiyerListModel = (SarfProducts) listView.getItemAtPosition(position);
                        silinecekId = plasiyerListModel.getId();
                        Context wrapper = new ContextThemeWrapper(SarfDuzenle.this, R.style.YOURSTYLE);
                        PopupMenu popup = new PopupMenu(wrapper, view);

                        popup.setOnMenuItemClickListener((PopupMenu.OnMenuItemClickListener) SarfDuzenle.this);
                        popup.inflate(R.menu.sarfduzenlepop);
                        popup.show();
                        return true;
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
                    String query = " select * from VW_CONSUMPTION where COMPANIESID = '" + comid + "' and CODE = '" + secilenSarf + "'";
                    PreparedStatement ps = con.prepareStatement(query);
                    ResultSet rs = ps.executeQuery();

                    while (rs.next()) {
                        barkodsayisi++;
                        tarih = rs.getString("DATE");
                        kullaici = rs.getString("MEMBEREMPLOYEENAME");
                        aciklama = rs.getString("DESCRIPTION");
                        exist = true;
                        String deneme = rs.getString("MEMBERNAME");
                        String deneme2 = rs.getString("CURRENTNAME");
                        if (deneme == null) {
                            cariPersonel = deneme2;
                        } else if (deneme2 == null) {
                            cariPersonel = deneme;
                        } else {
                            cariPersonel = "YOKTUR";
                        }
                        String firstz = Float.toString(rs.getFloat("FIRSTAMOUNT"));
                        String secondz = Float.toString(rs.getFloat("SECONDAMOUNT"));

                        productsArrayList.add(new SarfProducts(rs.getString("ID"),
                                rs.getString("PRODUCTNAME"), rs.getString("BARCODENO")
                                , firstz + " " + rs.getString("FIRSTUNITNAME"),
                                secondz + " " + rs.getString("SECONDUNITNAME")));
                    }
                    w = "Başarılı";
                }
            } catch (Exception ex) {
                w = "Veri Çekme Hatası";

            }
            return w;
        }
    }

    public static class SarfProducts {
        String id;
        String productname;
        String barkodeNo;
        String birincibirim;
        String ikincibirim;

        public SarfProducts(String id, String productname, String barkodeNo, String birincibirim, String ikincibirim) {
            this.id = id;
            this.productname = productname;
            this.barkodeNo = barkodeNo;
            this.birincibirim = birincibirim;
            this.ikincibirim = ikincibirim;
        }

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public String getProductname() {
            return productname;
        }

        public void setProductname(String productname) {
            this.productname = productname;
        }

        public String getBarkodeNo() {
            return barkodeNo;
        }

        public void setBarkodeNo(String barkodeNo) {
            this.barkodeNo = barkodeNo;
        }

        public String getBirincibirim() {
            return birincibirim;
        }

        public void setBirincibirim(String birincibirim) {
            this.birincibirim = birincibirim;
        }

        public String getIkincibirim() {
            return ikincibirim;
        }

        public void setIkincibirim(String ikincibirim) {
            this.ikincibirim = ikincibirim;
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.CUPCAKE)
    public class DeletePro extends AsyncTask<String, String, String> {


        String z = "";
        Boolean isSuccess = false;

        @Override
        protected void onPreExecute() {
            pbbarS.setVisibility(View.VISIBLE);
        }

        @Override
        protected void onPostExecute(String r) {
            pbbarS.setVisibility(View.GONE);
            if (isSuccess) {
                Toast.makeText(SarfDuzenle.this, "BAŞARIYLA SİLİNDİ", Toast.LENGTH_SHORT).show();
                FillList fillList = new FillList();
                fillList.execute("");
            }
        }

        @Override
        protected String doInBackground(String... params) {

            try {
                Connection con = connectionClass.CONN();
                if (con == null) {
                    z = "Error in connection with SQL server";
                } else {
                    String query = "Delete  from CONSUMPTION where ID = '" + silinecekId + "'";
                    PreparedStatement preparedStatement = con.prepareStatement(query);
                    preparedStatement.executeUpdate();
                    isSuccess = true;
                }
            } catch (Exception ex) {
                ex.printStackTrace();
                isSuccess = false;
                z = "SQL HATASI!";
            }

            return z;
        }

    }
}
