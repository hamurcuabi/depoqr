
package com.emrehmrc.depoqr;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.ActivityOptions;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.AudioManager;
import android.media.ToneGenerator;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.Toast;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import static com.emrehmrc.depoqr.AnaSayfa.MyPREFERENCES;

public class SevkiyatSecondStep extends AppCompatActivity {

    RecyclerView recyclerView;
    ActionBar ab;
    boolean empty;
    SharedPreferences sharedpreferences;
    ConnectionClass connectionClass;
    String memberid, codeid;
    Button btnenterbarcode, btnqrread, btnsend;
    EditText edtCode;
    Spinner spnPB;
    String sevkNo, companiesid;
    EmreAdaptor emreAdaptor;
    ArrayList<SevkiyatÜrünleriRecyclerView> datalist;
    SevkiyatÜrünleriRecyclerView gecici;
    ToneGenerator toneG;
    CheckBox checkBoxAll;
    String sevkdepoid;
    ProgressBar progressBar;
    String PorB;
    String PorB2;
    int way = 0;
    String control = "";
    ArrayList<String> emptyArray = new ArrayList<>();
    ArrayList<String> emptyArray2 = new ArrayList<>();
    UUID uuid;
    int i = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sevkiyat_second_step);
        sharedpreferences = getApplicationContext().getSharedPreferences(MyPREFERENCES, Context.MODE_PRIVATE);
        memberid = sharedpreferences.getString("ID", null);
        sevkNo = sharedpreferences.getString("SevkiyatForwadingID", null);
        sevkdepoid = sharedpreferences.getString("SevkiyatDepoID", null);
        companiesid = sharedpreferences.getString("Companiesid", null);
        ab = getSupportActionBar();
        ab.setTitle("SEVKİYAT");
        ab.setSubtitle("Sevkiyat İşlemleri");
        ab.setBackgroundDrawable(getResources().getDrawable(R.drawable.arkaplan));


        datalist = new ArrayList<SevkiyatÜrünleriRecyclerView>();
        toneG = new ToneGenerator(AudioManager.STREAM_ALARM, 100);

        connectionClass = new ConnectionClass();
        edtCode = (EditText) findViewById(R.id.edtCode);
        edtCode.setOnTouchListener(new View.OnTouchListener() {

            @Override
            public boolean onTouch(View v, MotionEvent event) {

                v.setFocusable(true);
                v.setFocusableInTouchMode(true);
                return false;
            }
        });

        progressBar = (ProgressBar) findViewById(R.id.pbbarS);
        btnenterbarcode = (Button) findViewById(R.id.btnentercode);
        btnqrread = (Button) findViewById(R.id.btnqrread);
        btnsend = (Button) findViewById(R.id.btnsend);
        spnPB = (Spinner) findViewById(R.id.spnPB);
        spnPB.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                switch (position) {
                    case 0:
                        PorB = "P";
                        edtCode.setHint("PALET...");

                        break;
                    case 1:
                        PorB = "B";
                        edtCode.setHint("BARKOD...");

                        break;
                    default:
                        ;


                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }

        });
        recyclerView = (RecyclerView) findViewById(R.id.recyclerview);

        checkBoxAll = (CheckBox) findViewById(R.id.checkBoxall);
        checkBoxAll.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

                int a = datalist.size();
                if (isChecked) {
                    for (int i = 0; i < a; i++) {
                        datalist.get(i).setChecked(true);
                    }

                } else {
                    for (int i = 0; i < a; i++) {
                        datalist.get(i).setChecked(false);
                    }
                }
                emreAdaptor = new EmreAdaptor(getApplicationContext(), datalist);
                recyclerView.setAdapter(emreAdaptor);
            }
        });
        btnenterbarcode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                datalist.clear();
                recyclerView.setAdapter(null);
                way = 0;
                if (PorB.equals("P")) {

                    FillProducts fillProducts = new FillProducts();
                    String g2 = "Select BARCODEID,BARCODENO,PRODUCTNAME,PALETID,PALETBARCODES,PRODUCTID," +
                            "PRODUCTCODE, " +
                            "FIRSTUNITNAME,SECONDUNITNAME,\n" +
                            "SUM(WDIRECTION * FIRSTAMOUNT)  AS FIRSTAMOUNT, \n" +
                            "SUM(WDIRECTION * SECONDAMOUNT) AS SECONDAMOUNT \n" +
                            "from VW_WAREHOUSESTOCKMOVEMENT where PRODUCTID IN (Select PRODUCTID from VW_FORWARDINGPRODUCTPLAN where FORWARDINGID ='" + sevkNo + "') and \n" +
                            "PALETBARCODES = '" + edtCode.getText().toString() + "'\n" +
                            "group by BARCODEID,BARCODENO,PALETBARCODES,PRODUCTNAME,PALETID,PRODUCTID," +
                            "PRODUCTCODE," +
                            "FIRSTUNITNAME,SECONDUNITNAME\n" +
                            "having SUM(WDIRECTION * FIRSTAMOUNT) != 0 or \n" +
                            "SUM(WDIRECTION * SECONDAMOUNT) != 0";
                    fillProducts.execute(g2);

                } else if (PorB.equals("B")) {
                    FillProducts fillProducts = new FillProducts();
                    String g2 = "Select BARCODEID,BARCODENO,PRODUCTNAME,PALETID,PALETBARCODES,PRODUCTID," +
                            "PRODUCTCODE, " +
                            "FIRSTUNITNAME,SECONDUNITNAME,\n" +
                            "SUM(WDIRECTION * FIRSTAMOUNT)  AS FIRSTAMOUNT, \n" +
                            "SUM(WDIRECTION * SECONDAMOUNT) AS SECONDAMOUNT \n" +
                            "from VW_WAREHOUSESTOCKMOVEMENT where PRODUCTID IN (Select PRODUCTID from VW_FORWARDINGPRODUCTPLAN where FORWARDINGID ='" + sevkNo + "') and \n" +
                            "BARCODENO = '" + edtCode.getText().toString() + "'\n" +
                            "group by BARCODEID,BARCODENO,PALETBARCODES,PRODUCTNAME,PALETID,PRODUCTID," +
                            "PRODUCTCODE," +
                            "FIRSTUNITNAME,SECONDUNITNAME\n" +
                            "having SUM(WDIRECTION * FIRSTAMOUNT) != 0 or \n" +
                            "SUM(WDIRECTION * SECONDAMOUNT) != 0";
                    fillProducts.execute(g2);


                }


            }
        });
        btnqrread.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent i = new Intent(SevkiyatSecondStep.this, CodeReaderForSevkiyat.class);
                startActivityForResult(i, 1);

            }


        });
        btnsend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                emptyArray.clear();
                emptyArray2.clear();
                IsSame ısSame = new IsSame();
                ısSame.execute("");


            }
        });
    }

    @SuppressLint("NewApi")
    public class FillProducts extends AsyncTask<String, String, String> {
        String z = "";

        @Override
        protected void onPreExecute() {

            empty = true;
            progressBar.setVisibility(View.VISIBLE);
        }

        @Override
        protected void onPostExecute(String r) {

            progressBar.setVisibility(View.GONE);

            if (!empty) {
                if (way == 0) {
                    if (PorB.equals("P")) {

                        FillProductsSec fillProducts = new FillProductsSec();
                        String g2 = "Select BARCODEID,BARCODENO,PRODUCTNAME,PALETID,PALETBARCODES,PRODUCTID," +
                                "PRODUCTCODE, " +
                                "FIRSTUNITNAME,SECONDUNITNAME,\n" +
                                "SUM(WDIRECTION * FIRSTAMOUNT)  AS FIRSTAMOUNT, \n" +
                                "SUM(WDIRECTION * SECONDAMOUNT) AS SECONDAMOUNT \n" +
                                "from VW_WAREHOUSESTOCKMOVEMENT where PRODUCTID IN (Select PRODUCTID from VW_FORWARDINGPRODUCTPLAN where FORWARDINGID ='" + sevkNo + "') and \n" +
                                "(DESTINATIONWAREHOUSEID = \n" +
                                "'" + sevkdepoid + "' \n" +
                                "or SOURCEWAREHOUSEID = '" + sevkdepoid + "') and\n" +
                                "PALETBARCODES = '" + edtCode.getText().toString() + "'\n" +
                                "group by BARCODEID,BARCODENO,PALETBARCODES,PRODUCTNAME,PALETID,PRODUCTID," +
                                "PRODUCTCODE," +
                                "FIRSTUNITNAME,SECONDUNITNAME\n" +
                                "having SUM(WDIRECTION * FIRSTAMOUNT) != 0 or \n" +
                                "SUM(WDIRECTION * SECONDAMOUNT) != 0";
                        fillProducts.execute(g2);


                    } else if (PorB.equals("B")) {
                        FillProductsSec fillProducts = new FillProductsSec();
                        String g2 = "Select BARCODEID,BARCODENO,PRODUCTNAME,PALETID,PALETBARCODES,PRODUCTID," +
                                "PRODUCTCODE, " +
                                "FIRSTUNITNAME,SECONDUNITNAME,\n" +
                                "SUM(WDIRECTION * FIRSTAMOUNT)  AS FIRSTAMOUNT, \n" +
                                "SUM(WDIRECTION * SECONDAMOUNT) AS SECONDAMOUNT \n" +
                                "from VW_WAREHOUSESTOCKMOVEMENT where PRODUCTID IN (Select PRODUCTID from VW_FORWARDINGPRODUCTPLAN where FORWARDINGID ='" + sevkNo + "') and \n" +
                                "(DESTINATIONWAREHOUSEID = \n" +
                                "'" + sevkdepoid + "' \n" +
                                "or SOURCEWAREHOUSEID = '" + sevkdepoid + "') and\n" +
                                "BARCODENO = '" + edtCode.getText().toString() + "'\n" +
                                "group by BARCODEID,BARCODENO,PALETBARCODES,PRODUCTNAME,PALETID,PRODUCTID," +
                                "PRODUCTCODE," +
                                "FIRSTUNITNAME,SECONDUNITNAME\n" +
                                "having SUM(WDIRECTION * FIRSTAMOUNT) != 0 or \n" +
                                "SUM(WDIRECTION * SECONDAMOUNT) != 0";
                        fillProducts.execute(g2);


                    }
                }

                if (way == 1) {
                    if (PorB2.equals("P")) {
                        FillProductsSec fillProducts = new FillProductsSec();
                        String query = "Select BARCODEID,BARCODENO,PRODUCTNAME,PALETID,PALETBARCODES,PRODUCTID," +
                                "PRODUCTCODE, " +
                                "FIRSTUNITNAME,SECONDUNITNAME,\n" +
                                "SUM(WDIRECTION * FIRSTAMOUNT)  AS FIRSTAMOUNT, \n" +
                                "SUM(WDIRECTION * SECONDAMOUNT) AS SECONDAMOUNT \n" +
                                "from VW_WAREHOUSESTOCKMOVEMENT where PRODUCTID IN (Select PRODUCTID from VW_FORWARDINGPRODUCTPLAN where FORWARDINGID ='" + sevkNo + "') and \n" +
                                "(DESTINATIONWAREHOUSEID = \n" +
                                "'" + sevkdepoid + "' \n" +
                                "or SOURCEWAREHOUSEID = '" + sevkdepoid + "') and\n" +
                                "PALETBARCODES = '" + codeid + "'\n" +
                                "group by BARCODEID,BARCODENO,PALETBARCODES,PRODUCTNAME,PALETID,PRODUCTID," +
                                "PRODUCTCODE," +
                                "FIRSTUNITNAME,SECONDUNITNAME\n" +
                                "having SUM(WDIRECTION * FIRSTAMOUNT) != 0 or \n" +
                                "SUM(WDIRECTION * SECONDAMOUNT) != 0";
                        fillProducts.execute(query);

                    } else if (PorB2.equals("B")) {
                        FillProductsSec fillProducts = new FillProductsSec();
                        String query = "Select BARCODEID,BARCODENO,PRODUCTNAME,PALETID,PALETBARCODES,PRODUCTID," +
                                "PRODUCTCODE, " +
                                "FIRSTUNITNAME,SECONDUNITNAME,\n" +
                                "SUM(WDIRECTION * FIRSTAMOUNT)  AS FIRSTAMOUNT, \n" +
                                "SUM(WDIRECTION * SECONDAMOUNT) AS SECONDAMOUNT \n" +
                                "from VW_WAREHOUSESTOCKMOVEMENT where PRODUCTID IN (Select PRODUCTID from VW_FORWARDINGPRODUCTPLAN where FORWARDINGID ='" + sevkNo + "') and \n" +
                                "(DESTINATIONWAREHOUSEID = \n" +
                                "'" + sevkdepoid + "' \n" +
                                "or SOURCEWAREHOUSEID = '" + sevkdepoid + "') and\n" +
                                "BARCODENO = '" + codeid + "'\n" +
                                "group by BARCODEID,BARCODENO,PALETBARCODES,PRODUCTNAME,PALETID,PRODUCTID," +
                                "PRODUCTCODE," +
                                "FIRSTUNITNAME,SECONDUNITNAME\n" +
                                "having SUM(WDIRECTION * FIRSTAMOUNT) != 0 or \n" +
                                "SUM(WDIRECTION * SECONDAMOUNT) != 0";
                        fillProducts.execute(query);


                    } else {
                        FillProductsSec fillProducts = new FillProductsSec();
                        String query = "Select BARCODEID,BARCODENO,PRODUCTNAME,PALETID,PALETBARCODES,PRODUCTID," +
                                "PRODUCTCODE, " +
                                "FIRSTUNITNAME,SECONDUNITNAME,\n" +
                                "SUM(WDIRECTION * FIRSTAMOUNT)  AS FIRSTAMOUNT, \n" +
                                "SUM(WDIRECTION * SECONDAMOUNT) AS SECONDAMOUNT \n" +
                                "from VW_WAREHOUSESTOCKMOVEMENT where PRODUCTID IN (Select PRODUCTID from VW_FORWARDINGPRODUCTPLAN where FORWARDINGID ='" + sevkNo + "') and \n" +
                                "(DESTINATIONWAREHOUSEID = \n" +
                                "'" + sevkdepoid + "' \n" +
                                "or SOURCEWAREHOUSEID = '" + sevkdepoid + "') and\n" +
                                "(PALETID='" + uuid + "' or BARCODEID='" + uuid + "')\n" +
                                "group by BARCODEID,BARCODENO,PALETBARCODES,PRODUCTNAME,PALETID,PRODUCTID," +
                                "PRODUCTCODE," +
                                "FIRSTUNITNAME,SECONDUNITNAME\n" +
                                "having SUM(WDIRECTION * FIRSTAMOUNT) != 0 or \n" +
                                "SUM(WDIRECTION * SECONDAMOUNT) != 0";
                        fillProducts.execute(query);

                    }

                }

            }
             else {
                if (way == 0) {
                    if (PorB.equals("P")) {

                        FillProductsThre fillProducts = new FillProductsThre();
                        String g2 = "select * from VW_SENTFORWADINGLIST where FORWARDINGID='"+sevkNo+"' and COMPANIESID='"+companiesid+"' and WAREHOUSEID='"+sevkdepoid+"' PALETID IN (select PALETID from VW_WAREHOUSEPRODUCT where PALETBARCODES = '"+edtCode.getText().toString()+"')";
                        fillProducts.execute(g2);


                    } else if (PorB.equals("B")) {
                        FillProductsThre fillProducts = new FillProductsThre();
                        String g2 = "select * from VW_SENTFORWADINGLIST where FORWARDINGID='"+sevkNo+"' and COMPANIESID='"+companiesid+"' and WAREHOUSEID='"+sevkdepoid+"' and BARCODENO='"+edtCode.getText().toString()+"'";
                        fillProducts.execute(g2);


                    }
                }

                if (way == 1) {
                    if (PorB2.equals("P")) {
                        FillProductsThre fillProducts = new FillProductsThre();
                        String query = "select * from VW_SENTFORWADINGLIST where FORWARDINGID='"+sevkNo+"' and COMPANIESID='"+companiesid+"' and WAREHOUSEID='"+sevkdepoid+"' and PALETID IN (select PALETID from VW_WAREHOUSEPRODUCT where PALETBARCODES = '"+codeid+"')";
                        fillProducts.execute(query);

                    } else if (PorB2.equals("B")) {
                        FillProductsThre fillProducts = new FillProductsThre();
                        String query = "select * from VW_SENTFORWADINGLIST where FORWARDINGID='"+sevkNo+"' and COMPANIESID='"+companiesid+"' and WAREHOUSEID='"+sevkdepoid+"'  and BARCODENO='"+codeid+"'";
                        fillProducts.execute(query);


                    } else {
                        FillProductsThre fillProducts = new FillProductsThre();
                        String query = "select * from VW_SENTFORWADINGLIST where FORWARDINGID='"+sevkNo+"' and COMPANIESID='"+companiesid+"' and WAREHOUSEID='"+sevkdepoid+"' and (BARCODEID ='"+uuid+"' or PALETID='"+uuid+"')";
                        fillProducts.execute(query);

                    }
                }

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

                    if (rs.next()) {
                        empty = false;
                    } else {
                        empty = true;
                    }
                    z = "Başarılı";
                }
            } catch (Exception ex) {
                z = "Veri Çekme Hatası";
                ex.printStackTrace();

            }
            return z;
        }
    }

    @SuppressLint("NewApi")
    public class FillProductsSec extends AsyncTask<String, String, String> {
        String z = "";

        @Override
        protected void onPreExecute() {

            empty = true;
            progressBar.setVisibility(View.VISIBLE);
        }

        @Override
        protected void onPostExecute(String r) {
            progressBar.setVisibility(View.GONE);
            if (!empty) {

                toneG.startTone(ToneGenerator.TONE_CDMA_ALERT_CALL_GUARD, 200);
                emreAdaptor = new EmreAdaptor(getApplicationContext(), datalist);
                recyclerView.setAdapter(emreAdaptor);
                LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getApplicationContext());
                linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
                recyclerView.setLayoutManager(linearLayoutManager);

            } else {
                toneG.startTone(ToneGenerator.TONE_CDMA_CALL_SIGNAL_ISDN_PING_RING, 200);
                Intent intent = new Intent(getBaseContext(), UyariBildirim.class);
                intent.putExtra("UYARI", "YANLIS NUMARA!");
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
                        gecici = new SevkiyatÜrünleriRecyclerView();
                        boolean same = false;
                        gecici.setChecked(true);
                        gecici.setName(rs.getString("PRODUCTNAME"));
                        gecici.setFirstUnit(rs.getString("FIRSTUNITNAME"));
                        gecici.setSecondUnit(rs.getString("SECONDUNITNAME"));
                        gecici.setFirstamount(Float.parseFloat(rs.getString("FIRSTAMOUNT")));
                        gecici.setSecondamount(Float.parseFloat(rs.getString("SECONDAMOUNT")));
                        gecici.setPaletid(rs.getString("PALETID"));
                        gecici.setUniqCode(rs.getString("BARCODENO"));
                        gecici.setProductid(rs.getString("PRODUCTID"));
                        gecici.setBarcodeid(rs.getString("BARCODEID"));
                        gecici.setUyari1("");
                        gecici.setUyari2("");
                        for (int j = 0; j < datalist.size(); j++) {
                            if (datalist.get(j).getBarcodeid().equals(gecici.getBarcodeid())) {
                                same = true;
                                break;
                            }
                        }
                        if (!same) datalist.add(gecici);
                        empty = false;
                    }
                    z = "Başarılı";
                }
            } catch (Exception ex) {
                z = "Veri Çekme Hatası";
                ex.printStackTrace();

            }
            return z;
        }

    }
    public class FillProductsThre extends AsyncTask<String, String, String> {
        String z = "";
        @Override
        protected void onPreExecute() {

            empty = true;
            progressBar.setVisibility(View.VISIBLE);
        }

        @Override
        protected void onPostExecute(String r) {
            progressBar.setVisibility(View.GONE);
            if (!empty) {

                toneG.startTone(ToneGenerator.TONE_CDMA_ALERT_CALL_GUARD, 200);
                emreAdaptor = new EmreAdaptor(getApplicationContext(), datalist);
                recyclerView.setAdapter(emreAdaptor);
                LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getApplicationContext());
                linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
                recyclerView.setLayoutManager(linearLayoutManager);
            }
            else{
                toneG.startTone(ToneGenerator.TONE_CDMA_CALL_SIGNAL_ISDN_PING_RING, 200);
                Intent intent = new Intent(getBaseContext(), UyariBildirim.class);
                intent.putExtra("UYARI", "DEPODA BULUNAMADI!");
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
                        gecici = new SevkiyatÜrünleriRecyclerView();
                        boolean same = false;
                        gecici.setChecked(true);
                        gecici.setName(rs.getString("PRODUCTNAME"));
                        gecici.setFirstUnit(rs.getString("FIRSTUNITNAME"));
                        gecici.setSecondUnit(rs.getString("SECONDUNITNAME"));
                        gecici.setFirstamount(Float.parseFloat(rs.getString("FIRSTAMOUNT")));
                        gecici.setSecondamount(Float.parseFloat(rs.getString("SECONDAMOUNT")));
                        gecici.setPaletid(rs.getString("PALETID"));
                        gecici.setUniqCode(rs.getString("BARCODENO"));
                        gecici.setProductid(rs.getString("PRODUCTID"));
                        gecici.setBarcodeid(rs.getString("BARCODEID"));
                        gecici.setUyari1("");
                        gecici.setUyari2("");
                        for (int j = 0; j < datalist.size(); j++) {
                            if (datalist.get(j).getBarcodeid().equals(gecici.getBarcodeid())) {
                                same = true;
                                break;
                            }
                        }
                        if (!same) datalist.add(gecici);
                        empty = false;
                    }
                    z = "Başarılı";
                }
            } catch (Exception ex) {
                z = "Veri Çekme Hatası";
                ex.printStackTrace();

            }
            return z;
        }
    }
    @SuppressLint("NewApi")
    public class SendProducts extends AsyncTask<String, String, String> {
        String z = "";
        boolean hata;

        @Override
        protected void onPreExecute() {
            progressBar.setVisibility(View.VISIBLE);
            hata = false;

        }

        @Override
        protected void onPostExecute(String r) {
            progressBar.setVisibility(View.GONE);
            emreAdaptor = new EmreAdaptor(getApplicationContext(), datalist);
            recyclerView.setAdapter(emreAdaptor);
            if (!hata)
                Toast.makeText(getApplicationContext(), "AKTARILDI!", Toast.LENGTH_SHORT).show();
        }

        @Override
        protected String doInBackground(String... params) {
            try {
                Connection con = connectionClass.CONN();
                if (con == null) {
                    z = "Error in connection with SQL server";
                } else {
                    while (datalist.size() > 0) {
                        if (datalist.get(i).isChecked()) {
                            UUID uuıd = UUID.randomUUID();
                            String query = "insert into SENTFORWARDING" + " " +
                                    "(ID,MEMBERID,COMPANIESID," + "FORWARDINGID,FIRSTUNITAMOUNT," +
                                    "SECONDUNITAMOUNT," + "WAREHOUSEID,PRODUCTID,BARCODEID,PALETID,ISOKEY)" +
                                    " values(" + "'" + uuıd + "'," +
                                    "'" + memberid + "','" + companiesid + "'," +
                                    "'" + sevkNo + "'" + ",'" + datalist.get(i).getFirstamount() + "'," +
                                    "'" + datalist.get(i).getSecondamount() + "'" + ",'" + sevkdepoid + "'," +
                                    "'" + datalist.get(i).getProductid() + "'" +
                                    "," + "'" + datalist.get(i).getBarcodeid() + "','" + datalist.get(i)
                                    .getPaletid() + "','0')";
                            datalist.remove(i);
                            PreparedStatement preparedStatement = con.prepareStatement(query);
                            preparedStatement.executeUpdate();
                        } else i++;
                        hata = false;
                    }


                }


            } catch (Exception ex) {
                z = "Veri Çekme Hatası";
                ex.printStackTrace();


            }
            return z;
        }
    }

    @SuppressLint("NewApi")
    public class SendProductss extends AsyncTask<String, String, String> {
        String z = "";
        boolean hata;
        int i = 0;
        boolean deneme = true;

        @Override
        protected void onPreExecute() {
            progressBar.setVisibility(View.VISIBLE);
            hata = false;

        }

        @Override
        protected void onPostExecute(String r) {
            progressBar.setVisibility(View.GONE);
            if (deneme)
                Toast.makeText(getApplicationContext(), "AKTARILACAK ÜRÜN YOK!", Toast.LENGTH_SHORT).show();
            else Toast.makeText(getApplicationContext(), "AKTARILDI!", Toast.LENGTH_SHORT).show();
            // if (hata) Toast.makeText(getApplicationContext(), "HATA!", Toast.LENGTH_SHORT).show();
        }

        @Override
        protected String doInBackground(String... params) {
            try {
                Connection con = connectionClass.CONN();
                if (con == null) {
                    z = "Error in connection with SQL server";
                } else {
                    if (!emptyArray2.isEmpty()) {
                        while (datalist.size() > 0) {
                            if (datalist.get(i).isChecked()) {
                                if (emptyArray2.contains(datalist.get(i).getBarcodeid())) {
                                    UUID uuıd = UUID.randomUUID();
                                    String query = "insert into SENTFORWARDING" + " " +
                                            "(ID,MEMBERID,COMPANIESID," + "FORWARDINGID,FIRSTUNITAMOUNT," +
                                            "SECONDUNITAMOUNT," + "WAREHOUSEID,PRODUCTID,BARCODEID,PALETID,ISOKEY)" +
                                            " values(" + "'" + uuıd + "'," +
                                            "'" + memberid + "','" + companiesid + "'," +
                                            "'" + sevkNo + "'" + ",'" + datalist.get(i).getFirstamount() + "'," +
                                            "'" + datalist.get(i).getSecondamount() + "'" + ",'" + sevkdepoid + "'," +
                                            "'" + datalist.get(i).getProductid() + "'" +
                                            "," + "'" + datalist.get(i).getBarcodeid() + "','" + datalist.get(i)
                                            .getPaletid() + "','0')";
                                    PreparedStatement preparedStatement = con.prepareStatement(query);
                                    preparedStatement.executeUpdate();
                                    datalist.remove(i);
                                    deneme = false;
                                } else i++;
                            } else i++;
                        }
                    }
                }
            } catch (Exception ex) {
                z = "Veri Çekme Hatası";
                hata = true;
                ex.printStackTrace();


            }
            return z;
        }
    }

    @SuppressLint("NewApi")
    public class IsSame extends AsyncTask<String, String, String> {
        String z = "";


        @Override
        protected void onPreExecute() {
            progressBar.setVisibility(View.VISIBLE);
            i = 0;
        }

        @Override
        protected void onPostExecute(String r) {
            progressBar.setVisibility(View.GONE);

            if (!emptyArray.isEmpty()) {
                AlertDialog.Builder builder2 = new AlertDialog.Builder(SevkiyatSecondStep.this);
                builder2.setTitle("UYARI!");
                builder2.setMessage("Sevkiyatta AYNI ÜRÜN BULUNDU  SİLİNSİN Mİ?");
                builder2.setNegativeButton("EVET", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        SevkiyatSecondStep.DeletePro deletePro = new SevkiyatSecondStep.DeletePro();
                        deletePro.execute("");
                    }
                });
                builder2.setPositiveButton("HAYIR, Eklemek istiyorum", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        SevkiyatSecondStep.SendProductss sendProducts2 = new SevkiyatSecondStep.SendProductss();
                        sendProducts2.execute("");
                    }
                });
                builder2.show();
            } else {
                SendProducts sendProducts = new SendProducts();
                sendProducts.execute("");
            }
        }

        @Override
        protected String doInBackground(String... params) {
            try {
                Connection con = connectionClass.CONN();
                if (con == null) {
                    z = "Error in connection with SQL server";
                } else {
                    for (int j = 0; j < datalist.size(); j++) {
                        if (datalist.get(j).isChecked()) {
                            String q = "Select * from SENTFORWARDING where BARCODEID='" + datalist.get(j).getBarcodeid() + "' and FORWARDINGID ='" + sevkNo + "'  ";
                            PreparedStatement ps = con.prepareStatement(q);
                            ResultSet rs = ps.executeQuery();
                            if (rs.next()) {
                                control = "exist";
                                emptyArray.add(datalist.get(j).getBarcodeid());

                            } else {
                                control = "Empty";
                                emptyArray2.add(datalist.get(j).getBarcodeid());
                            }
                            z = "Başarılı";
                        }
                    }
                }

            } catch (Exception ex) {
                z = "Veri Çekme Hatası";
                ex.printStackTrace();

            }
            return z;
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
                emreAdaptor = new EmreAdaptor(getApplicationContext(), datalist);
                recyclerView.setAdapter(emreAdaptor);
                Toast.makeText(SevkiyatSecondStep.this, "BAŞARIYLA SİLİNDİ", Toast.LENGTH_SHORT).show();
            }
        }

        @Override
        protected String doInBackground(String... params) {

            try {
                Connection con = connectionClass.CONN();
                if (con == null) {
                    z = "Error in connection with SQL server";
                } else {
                    for (int j = 0; j < emptyArray.size(); j++) {
                        String query = "Delete  from SENTFORWARDING where  BARCODEID='" + emptyArray.get(j) + "' and FORWARDINGID ='" + sevkNo + "'";
                        PreparedStatement preparedStatement = con.prepareStatement(query);
                        preparedStatement.executeUpdate();
                        isSuccess = true;
                    }

                }
            } catch (Exception ex) {
                ex.printStackTrace();
                isSuccess = false;
                z = "SQL HATASI!";
            }

            return z;
        }
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.anasayfa) {

            Intent i = new Intent(SevkiyatSecondStep.this, SliderMenu.class);
            Bundle bundle = ActivityOptions.makeSceneTransitionAnimation(SevkiyatSecondStep.this).toBundle();
            finish();
        } else if (id == R.id.geri) {
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
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (requestCode == 1) {
            way = 1;
            if (resultCode == Activity.RESULT_OK) {
                codeid = data.getStringExtra("codeid");


                try {
                    uuid = UUID.fromString(codeid);
                    PorB2 = "fdsfds";

                } catch (Exception ex) {
                    PorB2 = codeid.substring(0, 1);
                    codeid = codeid.substring(1, codeid.length());


                }
                datalist.clear();
                recyclerView.setAdapter(null);
                if (PorB2.equals("P")) {
                    FillProducts fillProducts = new FillProducts();
                    String query = "Select BARCODEID,BARCODENO,PRODUCTNAME,PALETID,PALETBARCODES,PRODUCTID," +
                            "PRODUCTCODE, " +
                            "FIRSTUNITNAME,SECONDUNITNAME,\n" +
                            "SUM(WDIRECTION * FIRSTAMOUNT)  AS FIRSTAMOUNT, \n" +
                            "SUM(WDIRECTION * SECONDAMOUNT) AS SECONDAMOUNT \n" +
                            "from VW_WAREHOUSESTOCKMOVEMENT where PRODUCTID IN (Select PRODUCTID from VW_FORWARDINGPRODUCTPLAN where FORWARDINGID ='" + sevkNo + "') and \n" +
                            "PALETBARCODES = '" + codeid + "'\n" +
                            "group by BARCODEID,BARCODENO,PALETBARCODES,PRODUCTNAME,PALETID,PRODUCTID," +
                            "PRODUCTCODE," +
                            "FIRSTUNITNAME,SECONDUNITNAME\n" +
                            "having SUM(WDIRECTION * FIRSTAMOUNT) != 0 or \n" +
                            "SUM(WDIRECTION * SECONDAMOUNT) != 0";
                    fillProducts.execute(query);

                } else if (PorB2.equals("B")) {
                    FillProducts fillProducts = new FillProducts();
                    String query = "Select BARCODEID,BARCODENO,PRODUCTNAME,PALETID,PALETBARCODES,PRODUCTID," +
                            "PRODUCTCODE, " +
                            "FIRSTUNITNAME,SECONDUNITNAME,\n" +
                            "SUM(WDIRECTION * FIRSTAMOUNT)  AS FIRSTAMOUNT, \n" +
                            "SUM(WDIRECTION * SECONDAMOUNT) AS SECONDAMOUNT \n" +
                            "from VW_WAREHOUSESTOCKMOVEMENT where PRODUCTID IN (Select PRODUCTID from VW_FORWARDINGPRODUCTPLAN where FORWARDINGID ='" + sevkNo + "') and \n" +
                            "BARCODENO = '" + codeid + "'\n" +
                            "group by BARCODEID,BARCODENO,PALETBARCODES,PRODUCTNAME,PALETID,PRODUCTID," +
                            "PRODUCTCODE," +
                            "FIRSTUNITNAME,SECONDUNITNAME\n" +
                            "having SUM(WDIRECTION * FIRSTAMOUNT) != 0 or \n" +
                            "SUM(WDIRECTION * SECONDAMOUNT) != 0";
                    fillProducts.execute(query);


                } else {

                    FillProducts fillProducts = new FillProducts();
                    String query = "Select BARCODEID,BARCODENO,PRODUCTNAME,PALETID,PALETBARCODES,PRODUCTID," +
                            "PRODUCTCODE, " +
                            "FIRSTUNITNAME,SECONDUNITNAME,\n" +
                            "SUM(WDIRECTION * FIRSTAMOUNT)  AS FIRSTAMOUNT, \n" +
                            "SUM(WDIRECTION * SECONDAMOUNT) AS SECONDAMOUNT \n" +
                            "from VW_WAREHOUSESTOCKMOVEMENT where PRODUCTID IN (Select PRODUCTID from VW_FORWARDINGPRODUCTPLAN where FORWARDINGID ='" + sevkNo + "') and \n" +
                            "(PALETID='" + uuid + "' or BARCODEID='" + uuid + "')\n" +
                            "group by BARCODEID,BARCODENO,PALETBARCODES,PRODUCTNAME,PALETID,PRODUCTID," +
                            "PRODUCTCODE," +
                            "FIRSTUNITNAME,SECONDUNITNAME\n" +
                            "having SUM(WDIRECTION * FIRSTAMOUNT) != 0 or \n" +
                            "SUM(WDIRECTION * SECONDAMOUNT) != 0";
                    fillProducts.execute(query);

                }
            }
            if (resultCode == Activity.RESULT_CANCELED) {
                //Write your code if there's no result
            }
        }
    }

}
