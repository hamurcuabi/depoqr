package com.emrehmrc.depoqr;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.ProgressBar;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import me.dm7.barcodescanner.zxing.ZXingScannerView;

public class ProductsinPalet extends AppCompatActivity {


    ListView lstScaningProducts;
    ConnectionClass connectionClass;
    SpecialAdapter ADA;
    String paletid = "";
    ProgressBar pbbar;
    ActionBar ab;
    Button btnprint;
    ArrayList<String> myListsend;

    private ZXingScannerView scannerView;

    @RequiresApi(api = Build.VERSION_CODES.CUPCAKE)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_productsin_palet);

        ab = getSupportActionBar();
        ab.setTitle("PALET YAZDIR");
        //ab.setSubtitle("Alt Bşalık");
        ab.setBackgroundDrawable(getResources().getDrawable(R.drawable.arkaplan));

        connectionClass = new ConnectionClass();
        lstScaningProducts = (ListView) findViewById(R.id.lstproductscaned);
        pbbar = (ProgressBar) findViewById(R.id.pbar);
        pbbar.setVisibility(View.GONE);
        btnprint=(Button)findViewById(R.id.btnprint);

        Intent i = getIntent();
        paletid = i.getStringExtra("paletid");

        FillList fillList = new FillList();
        String query = "select * from VW_PALETBARCODE where PALETID='" + paletid + "' ";
        fillList.execute(query);


        btnprint.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(ProductsinPalet.this, PaletEtiket.class);
                myListsend = new ArrayList<String>();
                myListsend.add(0, String.valueOf(paletid));
                i.putExtra("mylist", myListsend);
                startActivity(i);
            }
        });


    }

    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.anasayfa) {
            finish();
            Intent i= new Intent(ProductsinPalet.this,SliderMenu.class);
            startActivity(i);

        }
        else if (id == R.id.geri) {
            finish();
            onBackPressed();

        }

        return super.onOptionsItemSelected(item);
    }
    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.diger, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public void onBackPressed() {

        super.onBackPressed();
    }

    @RequiresApi(api = Build.VERSION_CODES.CUPCAKE)
    public class UpdatePro extends AsyncTask<String, String, String> {


        String z = "";
        Boolean isSuccess = false;


        @Override
        protected void onPreExecute() {
            pbbar.setVisibility(View.VISIBLE);
        }

        @Override
        protected void onPostExecute(String r) {
            pbbar.setVisibility(View.GONE);
            // Toast.makeText(Products.this, r, Toast.LENGTH_SHORT).show();
            if (isSuccess == true) {

            }

        }

        @Override
        protected String doInBackground(String... params) {

            try {
                Connection con = connectionClass.CONN();
                if (con == null) {
                    z = "Error in connection with SQL server";
                } else {
                    isSuccess = true;
                    String dates = new SimpleDateFormat("MM/dd/yyyy", Locale.ENGLISH)
                            .format(Calendar.getInstance().getTime());


                    PreparedStatement preparedStatement = con.prepareStatement(params[0]);
                    preparedStatement.executeUpdate();
                    z = "Updated Successfully";

                    isSuccess = true;
                }
            } catch (Exception ex) {
                isSuccess = false;
                z = "Exceptions";
            }

            return z;
        }
    }



    @RequiresApi(api = Build.VERSION_CODES.CUPCAKE)
    public class FillList extends AsyncTask<String, String, String> {
        String z = "";
        List<Map<String, String>> prolist = new ArrayList<Map<String, String>>();

        @Override
        protected void onPreExecute() {

            pbbar.setVisibility(View.VISIBLE);
        }

        @Override
        protected void onPostExecute(String r) {

            pbbar.setVisibility(View.GONE);
            String[] from = {"A", "B", "C", "D"};
            int[] views = {R.id.procode, R.id.proname, R.id.proweight, R.id.proamount};
            ADA = new SpecialAdapter(ProductsinPalet.this,
                    prolist, R.layout.listbarcode, from,
                    views);
            lstScaningProducts.setAdapter(ADA);


            lstScaningProducts.setOnItemClickListener(new AdapterView.OnItemClickListener() {

                @Override
                public void onItemClick(AdapterView<?> arg0, View arg1,
                                        int arg2, long arg3) {
                    HashMap<String, Object> obj = (HashMap<String, Object>) ADA.getItem(arg2);
                    String code = (String) obj.get("A");
                    String name = (String) obj.get("B");
                    String amount = (String) obj.get("C");
                    String weight = (String) obj.get("D");


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

                    PreparedStatement ps = con.prepareStatement(params[0]);
                    ResultSet rs = ps.executeQuery();
                    while (rs.next()) {
                        Map<String, String> datanum = new HashMap<String, String>();
                        datanum.put("A", rs.getString("PRODUCTCODE"));
                        datanum.put("B", rs.getString("PRODUCTNAME"));
                        datanum.put("C", rs.getString("FIRSTUNITAMOUNT"));
                        datanum.put("D", rs.getString("SECONDUNITAMOUNT"));
                        prolist.add(datanum);
                    }

                    z = "BARCODE GELDİ";
                }
            } catch (Exception ex) {
                z = "Veri Çekme Hatası";

            }

            return z;
        }
    }

}
