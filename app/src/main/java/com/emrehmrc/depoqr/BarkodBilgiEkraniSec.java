package com.emrehmrc.depoqr;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
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
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.emrehmrc.depoqr.AnaSayfa.MyPREFERENCES;

public class BarkodBilgiEkraniSec extends AppCompatActivity {
    String secilenProductId;
    Bundle bundle;
    ActionBar ab;
    ConnectionClass connectionClass;
    SharedPreferences sharedPreferences;
    Vibrator vibrator;
    String memberid, comid;
    ListView lst_sevkiyat, lst_transfer, lst_malkabul;
    ProgressBar pbbarSevkiyat, pbbarTransfer, pbbarMalkabul;
    LinearLayout transferError,sevkiyatError,malkabulError;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_barkod_bilgi_ekrani_sec);
        Intent incomingIntent = getIntent();
        secilenProductId = incomingIntent.getStringExtra("secilenProductId");
        this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        vibrator = (Vibrator) getSystemService(VIBRATOR_SERVICE);
        sharedPreferences = getApplicationContext().getSharedPreferences(MyPREFERENCES, Context.MODE_PRIVATE);
        memberid = sharedPreferences.getString("ID", null);
        comid = sharedPreferences.getString("Companiesid", null);
        connectionClass = new ConnectionClass();
        ab = getSupportActionBar();
        ab.setTitle("Barkod Hareketleri");
        ab.setBackgroundDrawable(getResources().getDrawable(R.drawable.arkaplan));
        lst_sevkiyat = (ListView) findViewById(R.id.lst_sevkiyat);
        lst_transfer = (ListView) findViewById(R.id.lst_transfer);
        lst_malkabul = (ListView) findViewById(R.id.lst_malkabul);
        pbbarSevkiyat = (ProgressBar) findViewById(R.id.pbbarSevkiyat);
        pbbarTransfer = (ProgressBar) findViewById(R.id.pbbarTransfer);
        pbbarMalkabul = (ProgressBar) findViewById(R.id.pbbarMalkabul);
        transferError = (LinearLayout) findViewById(R.id.transferError);
        sevkiyatError = (LinearLayout) findViewById(R.id.sevkiyatError);
        malkabulError = (LinearLayout) findViewById(R.id.malkabulError);

        FillListMalKabul fillListMalKabul = new FillListMalKabul();
        fillListMalKabul.execute("");
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
            Intent i = new Intent(BarkodBilgiEkraniSec.this, SliderMenu.class);
            bundle = ActivityOptions.makeSceneTransitionAnimation(BarkodBilgiEkraniSec.this).toBundle();
            startActivity(i);


        } else if (id == R.id.geri) {
            finish();
            onBackPressed();

        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressLint("NewApi")
    public class FillListMalKabul extends AsyncTask<String, String, String> {
        String z = "";
        List<Map<String, String>> malkbulModelArrayList = new ArrayList<Map<String, String>>();
        Map<String, String> datanum;
        boolean exist = false;

        @Override
        protected void onPreExecute() {
            pbbarMalkabul.setVisibility(View.VISIBLE);
            pbbarSevkiyat.setVisibility(View.VISIBLE);
            pbbarTransfer.setVisibility(View.VISIBLE);
        }

        @Override
        protected void onPostExecute(String s) {
            pbbarMalkabul.setVisibility(View.GONE);
            if (exist) {
                String[] from = {"A", "B", "C"};
                int[] views = {R.id.view_tarih, R.id.view_cariadi, R.id.view_toplamtutar};
                BaseAdapter ADA = new SpecialAdapter(BarkodBilgiEkraniSec.this, malkbulModelArrayList, R.layout.barkodview3items, from, views);
                lst_malkabul.setAdapter(ADA);

            } else {
               lst_malkabul.setVisibility(View.GONE);
               malkabulError.setVisibility(View.VISIBLE);
            }
            FillListSevkiyat fillListSevkiyat = new FillListSevkiyat();
            fillListSevkiyat.execute("");

        }

        @Override
        protected String doInBackground(String... strings) {
            try {
                Connection con = connectionClass.CONN();
                if (con == null) {
                    z = "Error in connection with SQL server";
                } else {
                    String query = "SELECT DATE, MEMBERNAME, WAREHOUSENAME from VW_WAREHOUSESTOCKMOVEMENT where COMPANIESID = '" + comid + "' and BARCODEID ='" + secilenProductId + "' and ( MOVEID = '3' or MOVEID ='7') order by DATE  ";
                    PreparedStatement ps = con.prepareStatement(query);
                    ResultSet rs = ps.executeQuery();

                    while (rs.next()) {
                        datanum = new HashMap<>();
                        datanum.put("A", rs.getString("DATE"));
                        datanum.put("B", rs.getString("MEMBERNAME"));
                        datanum.put("C", rs.getString("WAREHOUSENAME"));
                        malkbulModelArrayList.add(datanum);
                        exist = true;
                    }
                    z = "Başarılı";
                }
            } catch (Exception ex) {
                z = "Veri Çekme Hatası";

            }
            return z;
        }
    }

    @SuppressLint("NewApi")
    public class FillListSevkiyat extends AsyncTask<String, String, String> {
        String z = "";
        List<Map<String, String>> sevkiyatArraylist = new ArrayList<Map<String, String>>();
        Map<String, String> datanum;
        boolean exist = false;

        @Override


        protected void onPreExecute() {
            pbbarSevkiyat.setVisibility(View.VISIBLE);
        }

        @Override
        protected void onPostExecute(String s) {
            pbbarSevkiyat.setVisibility(View.GONE);
            if (exist) {
                String[] from = {"A", "B", "C", "D"};
                int[] views = {R.id.view_1, R.id.view_2, R.id.view_3, R.id.view_4};
                BaseAdapter ADA = new SpecialAdapter(BarkodBilgiEkraniSec.this, sevkiyatArraylist, R.layout.barkodview4elements, from, views);
                lst_sevkiyat.setAdapter(ADA);
            } else {
               lst_sevkiyat.setVisibility(View.GONE);
               sevkiyatError.setVisibility(View.VISIBLE);
            }
            FillListTransfer fillListTransfer = new FillListTransfer();
            fillListTransfer.execute("");
        }

        @Override
        protected String doInBackground(String... strings) {
            try {
                Connection con = connectionClass.CONN();
                if (con == null) {
                    z = "Error in connection with SQL server";
                } else {
                    String query = "SELECT whsm.DATE, whsm.MEMBERNAME, whsm.FORWARID,fp.CURRENTNAME,fp.FORWARDINGNO  from VW_WAREHOUSESTOCKMOVEMENT as whsm INNER JOIN VW_FORWARDINGPLAN as fp ON fp.ID=whsm.FORWARID where whsm.COMPANIESID = '" + comid + "' and ( whsm.MOVEID = '1' or whsm.MOVEID = '8' ) and whsm.BARCODEID ='" + secilenProductId + "' order by whsm.DATE";
                    PreparedStatement ps = con.prepareStatement(query);
                    ResultSet rs = ps.executeQuery();

                    while (rs.next()) {
                        datanum = new HashMap<>();
                        datanum.put("A", rs.getString("DATE"));
                        datanum.put("B", rs.getString("MEMBERNAME"));
                        datanum.put("C", rs.getString("FORWARDINGNO"));
                        datanum.put("D", rs.getString("CURRENTNAME"));
                        sevkiyatArraylist.add(datanum);
                        exist = true;
                    }
                    z = "Başarılı";
                }
            } catch (Exception ex) {
                z = "Veri Çekme Hatası";

            }
            return z;
        }
    }

    @SuppressLint("NewApi")
    public class FillListTransfer extends AsyncTask<String, String, String> {
        String z = "";
        List<Map<String, String>> trnasferArraylist = new ArrayList<Map<String, String>>();
        Map<String, String> datanum;
        boolean exist = false;

        @Override
        protected void onPostExecute(String s) {
            pbbarTransfer.setVisibility(View.GONE);
            if (exist) {
                String[] from = {"A", "B", "C", "D"};
                int[] views = {R.id.view_1, R.id.view_2, R.id.view_3, R.id.view_4};
                BaseAdapter ADA = new SpecialAdapter(BarkodBilgiEkraniSec.this, trnasferArraylist, R.layout.barkodview4elements, from, views);
                lst_transfer.setAdapter(ADA);
            } else {
                lst_transfer.setVisibility(View.GONE);
                transferError.setVisibility(View.VISIBLE);
            }


        }

        @Override
        protected String doInBackground(String... strings) {
            try {
                Connection con = connectionClass.CONN();
                if (con == null) {
                    z = "Error in connection with SQL server";
                } else {
                    String query = " SELECT DATE, MEMBERNAME, SOURCEWAREHOUSENAME, DESTINATIONWAREHOUSENAME from VW_WAREHOUSETRANSFER where COMPANIESID = '" + comid + "' and BARCODEID = '"+secilenProductId+"' order by DATE ";
                    PreparedStatement ps = con.prepareStatement(query);
                    ResultSet rs = ps.executeQuery();

                    while (rs.next()) {
                        datanum = new HashMap<>();
                        datanum.put("A", rs.getString("DATE"));
                        datanum.put("B", rs.getString("MEMBERNAME"));
                        datanum.put("C", rs.getString("SOURCEWAREHOUSENAME"));
                        datanum.put("D", rs.getString("DESTINATIONWAREHOUSENAME"));
                        trnasferArraylist.add(datanum);
                        exist = true;
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
