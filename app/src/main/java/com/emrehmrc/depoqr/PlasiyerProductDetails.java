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
import android.view.ContextThemeWrapper;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.PopupMenu;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.emrehmrc.depoqr.adapter.PlasiyerProductDetailsAdapter;
import com.emrehmrc.depoqr.connection.ConnectionClass;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;

import static com.emrehmrc.depoqr.AnaSayfa.MyPREFERENCES;

public class PlasiyerProductDetails extends AppCompatActivity   implements  PopupMenu.OnMenuItemClickListener {
    ActionBar ab;
    ConnectionClass connectionClass;
    SharedPreferences sharedPreferences;
    Bundle bundle;
    Vibrator vibrator;
    String memberid, comid;
    ProgressBar progressBar;
    String secilenSatis,secilenSatisAdi;
    TextView tx_urunAdi;
    ListView listView;
    PlasiyerProductDetailsAdapter adapter;
    String silinecek;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_plasiyer_product_details);
        this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);
        vibrator = (Vibrator) getSystemService(VIBRATOR_SERVICE);
        sharedPreferences = getApplicationContext().getSharedPreferences(MyPREFERENCES, Context.MODE_PRIVATE);
        memberid = sharedPreferences.getString("ID", null);
        comid = sharedPreferences.getString("Companiesid", null);
        connectionClass = new ConnectionClass();
        ab = getSupportActionBar();
        ab.setTitle("Satış Detay");
        //ab.setSubtitle("Etiket Okut");
        ab.setBackgroundDrawable(getResources().getDrawable(R.drawable.arkaplan));
        Intent intent = getIntent();
        secilenSatis = intent.getStringExtra("secilenSatis");
        secilenSatisAdi = intent.getStringExtra("secilenSatisAdi");
        progressBar = (ProgressBar) findViewById(R.id.pbbarP);
        tx_urunAdi = (TextView) findViewById(R.id.tx_urunAdi);
        tx_urunAdi.setText(secilenSatisAdi);
        listView = (ListView) findViewById(R.id.lst_Cari);
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
            Intent i = new Intent(PlasiyerProductDetails.this, SliderMenu.class);
            bundle = ActivityOptions.makeSceneTransitionAnimation(PlasiyerProductDetails.this).toBundle();
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
        ArrayList<PlasiyerProductDetailModel> plasiyerArray = new ArrayList<PlasiyerProductDetailModel>();
        boolean exist = false;
        @Override
        protected void onPreExecute() {
            progressBar.setVisibility(View.VISIBLE);
            listView.setAdapter(null);
        }

        @Override
        protected void onPostExecute(String s) {
            progressBar.setVisibility(View.GONE);
            if(exist){
                adapter = new PlasiyerProductDetailsAdapter(getApplicationContext(), plasiyerArray);
                listView.setAdapter(adapter);
                listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
                    @Override
                    public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                        PlasiyerProductDetailModel plasiyerListModel;
                        plasiyerListModel = (PlasiyerProductDetailModel) listView.getItemAtPosition(position);
                        silinecek = plasiyerListModel.getId();
                        Context wrapper = new ContextThemeWrapper(PlasiyerProductDetails.this, R.style.YOURSTYLE);
                        PopupMenu popup = new PopupMenu(wrapper, view);

                        popup.setOnMenuItemClickListener((PopupMenu.OnMenuItemClickListener) PlasiyerProductDetails.this);
                        popup.inflate(R.menu.sarfduzenlepop);
                        popup.show();
                        return true;
                    }
                });
            }
        }

        @Override
        protected String doInBackground(String... strings) {
            try {
                Connection con = connectionClass.CONN();
                if (con == null) {
                    w = "Error in connection with SQL server";
                } else {
                    String query = "select * from VW_WAREHOUSEPLASIERDETAIL where CODE = '"+secilenSatis+"'";
                    PreparedStatement ps = con.prepareStatement(query);
                    ResultSet rs = ps.executeQuery();

                    while (rs.next()) {
                        exist = true;
                        plasiyerArray.add(new PlasiyerProductDetailModel(rs.getString("ID")
                                ,rs.getString("BARCODENO")
                                ,rs.getFloat("UNITPRICE")
                                ,rs.getFloat("FIRSTAMOUNT")
                                ,rs.getFloat("TOTAL"),rs.getFloat("KDVTOTAL"),rs.getFloat("GENERALTOTAL")));
                    }
                    w = "Başarılı";
                }
            } catch (Exception ex) {
                w = "Veri Çekme Hatası";

            }
            return w;
        }
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

    public static class PlasiyerProductDetailModel{
        String id;
        String barkod;
        float fiyat;
        float miktar;
        float toplamTutar;
        float kdv;
        float genelTutar;

        public PlasiyerProductDetailModel(String id, String barkod, float fiyat, float miktar, float toplamTutar, float kdv, float genelTutar) {
            this.id = id;
            this.barkod = barkod;
            this.fiyat = fiyat;
            this.miktar = miktar;
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

        public String getBarkod() {
            return barkod;
        }

        public void setBarkod(String barkod) {
            this.barkod = barkod;
        }

        public float getFiyat() {
            return fiyat;
        }

        public void setFiyat(float fiyat) {
            this.fiyat = fiyat;
        }

        public float getMiktar() {
            return miktar;
        }

        public void setMiktar(float miktar) {
            this.miktar = miktar;
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

    @RequiresApi(api = Build.VERSION_CODES.CUPCAKE)
    public class DeletePro extends AsyncTask<String, String, String> {


        String z = "";
        Boolean isSuccess = false;

        @Override
        protected void onPreExecute() {
            progressBar.setVisibility(View.VISIBLE);
        }

        @Override
        protected void onPostExecute(String r) {
            progressBar.setVisibility(View.GONE);
            if (isSuccess) {
                Toast.makeText(PlasiyerProductDetails.this, "BAŞARIYLA SİLİNDİ", Toast.LENGTH_SHORT).show();
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

                    String query = "Delete  from WAREHOUSEPLASIERDETAIL where ID = '"+silinecek+"' ";
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
