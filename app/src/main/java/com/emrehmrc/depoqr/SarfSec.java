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
import android.os.Vibrator;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.UUID;

import static com.emrehmrc.depoqr.AnaSayfa.MyPREFERENCES;

public class SarfSec extends AppCompatActivity {
    ActionBar ab;
    ConnectionClass connectionClass;
    SharedPreferences sharedPreferences;
    Bundle bundle;
    Vibrator vibrator;
    ToneGenerator toneG;
    String memberid, comid;
    String secilenDepoId, aciklama, secilenCari, secilenPersonel, secilenTibi;
    ArrayList<ProductsP> products = new ArrayList<>();
    ArrayAdapter<ProductsP> adapter;
    AutoCompleteTextView tx_urunadi;
    Button btn_gir;
    EditText tx_barkodNo;
    ImageView btn_drop2;
    String secilenUrunKodu, secilenUrunBarkodu;
    ProgressBar progressBar;
    SevkiyatÜrünleriRecyclerView gecici;
    DepoTransferUrunAdapter emreAdaptor;
    ArrayList<SevkiyatÜrünleriRecyclerView> datalist;
    RecyclerView recyclerView;
    UUID uuid;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sarf_sec);
        vibrator = (Vibrator) getSystemService(VIBRATOR_SERVICE);
        sharedPreferences = getApplicationContext().getSharedPreferences(MyPREFERENCES, Context.MODE_PRIVATE);
        memberid = sharedPreferences.getString("ID", null);
        comid = sharedPreferences.getString("Companiesid", null);
        Intent incomingIntent = getIntent();
        secilenDepoId = incomingIntent.getStringExtra("secilendepoId");
        aciklama = incomingIntent.getStringExtra("aciklama");
        secilenTibi = incomingIntent.getStringExtra("secilenTibi");
        if (secilenTibi.equals("perspnel")) {
            secilenPersonel = incomingIntent.getStringExtra("secilenId");
        } else if (secilenTibi.equals("cari")) {
            secilenCari = incomingIntent.getStringExtra("secilenId");
        }
        connectionClass = new ConnectionClass();
        ab = getSupportActionBar();
        ab.setTitle("SARF");
        ab.setSubtitle("BARKOD SEÇ");
        ab.setBackgroundDrawable(getResources().getDrawable(R.drawable.arkaplan));
        toneG = new ToneGenerator(AudioManager.STREAM_ALARM, 100);
        tx_urunadi = (AutoCompleteTextView) findViewById(R.id.tx_urunAdi);
        btn_gir = (Button) findViewById(R.id.btn_gir);
        tx_barkodNo = (EditText) findViewById(R.id.tx_barkodNo);
        btn_drop2 = (ImageView) findViewById(R.id.btn_drop2);
        progressBar = (ProgressBar) findViewById(R.id.pbbarS);
        recyclerView = (RecyclerView) findViewById(R.id.recyclerview);
        datalist = new ArrayList<SevkiyatÜrünleriRecyclerView>();

        btn_gir.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if ((!tx_barkodNo.getText().toString().isEmpty()) && (!tx_urunadi.getText().toString().isEmpty())) {
                    Toast.makeText(SarfSec.this, "ÜRÜN VE BARKOD BIRLIKTE GIRILEMEZ", Toast.LENGTH_SHORT).show();

                }
                else if ((tx_barkodNo.getText().toString().isEmpty()) && (tx_urunadi.getText().toString().isEmpty())) {
                    Toast.makeText(SarfSec.this, "ÜRÜN VEYA BARKOD GIRINIZ", Toast.LENGTH_SHORT).show();
                } else {
                    if (!tx_urunadi.getText().toString().isEmpty()) {
                        // buraya urun adi veya kodu aramak
                        boolean nameExist = false;
                        boolean kodExist = false;
                        for (int i = 0; i < products.size(); i++) {
                            if (products.get(i).getProductadi().equals(tx_urunadi.getText().toString())) {
                                secilenUrunKodu = products.get(i).getProductKod();
                                FillProducts fillProducts = new FillProducts();
                                String query = "Select PRODUCTIONDATE, BARCODEID,BARCODENO,PRODUCTNAME,PALETID,PALETBARCODES,PRODUCTID,PRODUCTCODE, FIRSTUNITNAME,SECONDUNITNAME,\n" +
                                        "SUM(WDIRECTION * FIRSTAMOUNT)  AS FIRSTAMOUNT, \n" +
                                        "SUM(WDIRECTION * SECONDAMOUNT) AS SECONDAMOUNT \n" +
                                        "from VW_WAREHOUSESTOCKMOVEMENT where\n" +
                                        "(DESTINATIONWAREHOUSEID = '" + secilenDepoId + "' or SOURCEWAREHOUSEID = '" + secilenDepoId + "') and COMPANIESID ='" + comid + "'\n" +
                                        "and PRODUCTCODE = '" + secilenUrunKodu + "'\n" +
                                        "group by PRODUCTIONDATE, BARCODEID,BARCODENO,PALETBARCODES,PRODUCTNAME,PALETID,PRODUCTID,PRODUCTCODE,FIRSTUNITNAME,SECONDUNITNAME\n" +
                                        "having SUM(WDIRECTION * FIRSTAMOUNT) != 0 or \n" +
                                        "SUM(WDIRECTION * SECONDAMOUNT) != 0 order by PRODUCTIONDATE";
                                fillProducts.execute(query);
                                nameExist = true;
                                break;
                            } else nameExist = false;
                        }
                        if (!nameExist) {
                            for (int i = 0; i < products.size(); i++) {
                                if (products.get(i).getProductKod().equals(tx_urunadi.getText().toString())) {
                                    secilenUrunKodu = tx_urunadi.getText().toString();
                                    FillProducts fillProducts = new FillProducts();
                                    String query = "Select PRODUCTIONDATE, BARCODEID,BARCODENO,PRODUCTNAME,PALETID,PALETBARCODES,PRODUCTID,PRODUCTCODE, FIRSTUNITNAME,SECONDUNITNAME,\n" +
                                            "SUM(WDIRECTION * FIRSTAMOUNT)  AS FIRSTAMOUNT, \n" +
                                            "SUM(WDIRECTION * SECONDAMOUNT) AS SECONDAMOUNT \n" +
                                            "from VW_WAREHOUSESTOCKMOVEMENT where\n" +
                                            "(DESTINATIONWAREHOUSEID = '" + secilenDepoId + "' or SOURCEWAREHOUSEID = '" + secilenDepoId + "') and COMPANIESID ='" + comid + "'\n" +
                                            "and PRODUCTCODE = '" + secilenUrunKodu + "'\n" +
                                            "group by PRODUCTIONDATE, BARCODEID,BARCODENO,PALETBARCODES,PRODUCTNAME,PALETID,PRODUCTID,PRODUCTCODE,FIRSTUNITNAME,SECONDUNITNAME\n" +
                                            "having SUM(WDIRECTION * FIRSTAMOUNT) != 0 or \n" +
                                            "SUM(WDIRECTION * SECONDAMOUNT) != 0 order by PRODUCTIONDATE";
                                    fillProducts.execute(query);
                                    kodExist = true;
                                    break;
                                } else kodExist = false;

                            }
                        }
                        if (!nameExist && !kodExist) {
                            CheckDepo fillProducts = new CheckDepo();
                            String g2 = "Select PRODUCTIONDATE, BARCODEID,BARCODENO,PRODUCTNAME,PALETID,PALETBARCODES,PRODUCTID," +
                                    "PRODUCTCODE, " +
                                    "FIRSTUNITNAME,SECONDUNITNAME,\n" +
                                    "SUM(WDIRECTION * FIRSTAMOUNT)  AS FIRSTAMOUNT, \n" +
                                    "SUM(WDIRECTION * SECONDAMOUNT) AS SECONDAMOUNT \n" +
                                    "from VW_WAREHOUSESTOCKMOVEMENT where " +
                                    "PRODUCTCODE = '" + secilenUrunKodu + "'\n" +
                                    "group by PRODUCTIONDATE,BARCODEID,BARCODENO,PALETBARCODES,PRODUCTNAME,PALETID,PRODUCTID," +
                                    "PRODUCTCODE," +
                                    "FIRSTUNITNAME,SECONDUNITNAME\n" +
                                    "having SUM(WDIRECTION * FIRSTAMOUNT) != 0 or \n" +
                                    "SUM(WDIRECTION * SECONDAMOUNT) != 0 order by PRODUCTIONDATE";
                            fillProducts.execute(g2);

                        }
                    } else {
                        FillBarkod fillProducts = new FillBarkod();
                        String query = "Select PRODUCTIONDATE, BARCODEID,BARCODENO,PRODUCTNAME,PALETID,PALETBARCODES,PRODUCTID,PRODUCTCODE, FIRSTUNITNAME,SECONDUNITNAME,\n" +
                                "SUM(WDIRECTION * FIRSTAMOUNT)  AS FIRSTAMOUNT, \n" +
                                "SUM(WDIRECTION * SECONDAMOUNT) AS SECONDAMOUNT \n" +
                                "from VW_WAREHOUSESTOCKMOVEMENT where\n" +
                                "COMPANIESID ='" + comid + "'\n" +
                                "and BARCODENO = '" + tx_barkodNo.getText().toString() + "'\n" +
                                "group by PRODUCTIONDATE, BARCODEID,BARCODENO,PALETBARCODES,PRODUCTNAME,PALETID,PRODUCTID,PRODUCTCODE,FIRSTUNITNAME,SECONDUNITNAME\n" +
                                "having SUM(WDIRECTION * FIRSTAMOUNT) != 0 or \n" +
                                "SUM(WDIRECTION * SECONDAMOUNT) != 0 order by PRODUCTIONDATE";
                        fillProducts.execute(query);

                    }
                }
            }
        });

        btn_drop2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                tx_urunadi.showDropDown();
            }
        });

        FillList fillList = new FillList();
        fillList.execute("");
    }


    @SuppressLint("NewApi")
    public class FillBarkod extends AsyncTask<String, String, String> {
        String z = "";
        boolean empty;

        @Override
        protected void onPreExecute() {

            empty = true;
            progressBar.setVisibility(View.VISIBLE);
        }

        @Override
        protected void onPostExecute(String r) {
            progressBar.setVisibility(View.GONE);
            if (!empty) {
                CheckBrakodDepo fillProducts = new CheckBrakodDepo();
                String query = "Select PRODUCTIONDATE, BARCODEID,BARCODENO,PRODUCTNAME,PALETID,PALETBARCODES,PRODUCTID,PRODUCTCODE, FIRSTUNITNAME,SECONDUNITNAME,\n" +
                        "SUM(WDIRECTION * FIRSTAMOUNT)  AS FIRSTAMOUNT, \n" +
                        "SUM(WDIRECTION * SECONDAMOUNT) AS SECONDAMOUNT \n" +
                        "from VW_WAREHOUSESTOCKMOVEMENT where\n" +
                        "(DESTINATIONWAREHOUSEID = '" + secilenDepoId + "' or SOURCEWAREHOUSEID = '" + secilenDepoId + "') and COMPANIESID ='" + comid + "'\n" +
                        "and PRODUCTCODE = '" + tx_barkodNo.getText().toString() + "'\n" +
                        "group by PRODUCTIONDATE, BARCODEID,BARCODENO,PALETBARCODES,PRODUCTNAME,PALETID,PRODUCTID,PRODUCTCODE,FIRSTUNITNAME,SECONDUNITNAME\n" +
                        "having SUM(WDIRECTION * FIRSTAMOUNT) != 0 or \n" +
                        "SUM(WDIRECTION * SECONDAMOUNT) != 0 order by PRODUCTIONDATE";
                fillProducts.execute(query);

            } else {
                toneG.startTone(ToneGenerator.TONE_CDMA_CALL_SIGNAL_ISDN_PING_RING, 200);
                Intent intent = new Intent(getBaseContext(), UyariBildirim.class);
                intent.putExtra("UYARI", "BARKOD BULUNAMADI!");
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

                    if (rs.next()) {
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
    public class CheckBrakodDepo extends AsyncTask<String, String, String> {
        String z = "";
        boolean empty;

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
                emreAdaptor = new DepoTransferUrunAdapter(getApplicationContext(), datalist);
                recyclerView.setAdapter(emreAdaptor);
                LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getApplicationContext());
                linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
                recyclerView.setLayoutManager(linearLayoutManager);

            } else {
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
                        gecici.setPrductionDate(rs.getString("PRODUCTIONDATE"));
                        gecici.setUyari1("");
                        gecici.setUyari2("");
                        datalist.add(gecici);
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
    public class FillProducts extends AsyncTask<String, String, String> {
        String z = "";
        boolean empty;

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
                emreAdaptor = new DepoTransferUrunAdapter(getApplicationContext(), datalist);
                recyclerView.setAdapter(emreAdaptor);
                LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getApplicationContext());
                linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
                recyclerView.setLayoutManager(linearLayoutManager);

            } else {
                toneG.startTone(ToneGenerator.TONE_CDMA_CALL_SIGNAL_ISDN_PING_RING, 200);
                Intent intent = new Intent(getBaseContext(), UyariBildirim.class);
                intent.putExtra("UYARI", "HATA!");
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
                        gecici.setPrductionDate(rs.getString("PRODUCTIONDATE"));
                        gecici.setUyari1("");
                        gecici.setUyari2("");
                        datalist.add(gecici);
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
    public class CheckDepo extends AsyncTask<String, String, String> {
        String z = "";
        Boolean empty;

        @Override
        protected void onPreExecute() {
            empty = true;
            progressBar.setVisibility(View.VISIBLE);
        }

        @Override
        protected void onPostExecute(String r) {
            progressBar.setVisibility(View.GONE);
            if (empty) {
                toneG.startTone(ToneGenerator.TONE_CDMA_CALL_SIGNAL_ISDN_PING_RING, 200);
                Intent intent = new Intent(getBaseContext(), UyariBildirim.class);
                intent.putExtra("UYARI", "GEÇERSIZ ÜRÜN!");
                startActivity(intent);
            } else {
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

                    if (rs.next()) {
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
    public class FillList extends AsyncTask<String, String, String> {
        String z = "";

        @Override
        protected void onPostExecute(String r) {
            for (int i = 0; i < products.size(); i++) {
                for (int j = i + 1; j < products.size(); j++) {
                    if (products.get(i).getProductKod().equals(products.get(j).getProductKod())) {
                        products.remove(j);
                        j--;
                    }
                }
            }
            adapter = new ArrayAdapter<ProductsP>(getApplicationContext(), android.R.layout.simple_dropdown_item_1line, products);
            tx_urunadi.setAdapter(adapter);

        }


        @Override
        protected String doInBackground(String... params) {
            try {
                Connection con = connectionClass.CONN();
                if (con == null) {
                    z = "Error in connection with SQL server";
                } else {
                    String query = "Select BARCODEID,BARCODENO,PRODUCTNAME,PALETID,PALETBARCODES,PRODUCTID,PRODUCTCODE, FIRSTUNITNAME,SECONDUNITNAME,\n" +
                            "SUM(WDIRECTION * FIRSTAMOUNT)  AS FIRSTAMOUNT, \n" +
                            "SUM(WDIRECTION * SECONDAMOUNT) AS SECONDAMOUNT \n" +
                            "from VW_WAREHOUSESTOCKMOVEMENT where\n" +
                            "(DESTINATIONWAREHOUSEID = '" + secilenDepoId + "' or SOURCEWAREHOUSEID = '" + secilenDepoId + "') and COMPANIESID ='" + comid + "'\n" +
                            "group by BARCODEID,BARCODENO,PALETBARCODES,PRODUCTNAME,PALETID,PRODUCTID,PRODUCTCODE,FIRSTUNITNAME,SECONDUNITNAME\n" +
                            "having SUM(WDIRECTION * FIRSTAMOUNT) != 0 or \n" +
                            "SUM(WDIRECTION * SECONDAMOUNT) != 0 order by PRODUCTNAME";
                    PreparedStatement ps = con.prepareStatement(query);
                    ResultSet rs = ps.executeQuery();
                    while (rs.next()) {
                        products.add(new ProductsP(rs.getString("PRODUCTNAME"), rs.getString("PRODUCTCODE")));

                    }
                    z = "Başarılı";
                }
            } catch (Exception ex) {
                z = "Veri Çekme Hatası";

            }
            return z;
        }
    }

    private class ProductsP {
        private String productadi;
        private String productKod;

        public ProductsP() {
        }

        public ProductsP(String productadi, String productKod) {
            this.productadi = productadi;
            this.productKod = productKod;
        }

        public String getProductadi() {
            return productadi;
        }

        public void setProductadi(String productadi) {
            this.productadi = productadi;
        }


        public String getProductKod() {
            return productKod;
        }

        public void setProductKod(String productKod) {
            this.productKod = productKod;
        }

        @Override
        public String toString() {
            return productadi;
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
            finish();
            Intent i = new Intent(SarfSec.this, SliderMenu.class);
            bundle = ActivityOptions.makeSceneTransitionAnimation(SarfSec.this).toBundle();
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
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        String PorB2  ;
        boolean checkifU=true;
        if (requestCode == 1) {
            if (resultCode == Activity.RESULT_OK) {
                secilenUrunBarkodu = data.getStringExtra("codeid");
                try {
                    uuid = UUID.fromString(secilenUrunBarkodu);
                    checkifU = false;
                } catch (Exception ex) {
                    secilenUrunBarkodu = secilenUrunBarkodu.substring(1, secilenUrunBarkodu.length());
                }
                if (checkifU) {
                    FillBarkod fillProducts = new FillBarkod();
                    String query = "Select BARCODEID,BARCODENO,PRODUCTNAME,PALETID,PALETBARCODES,PRODUCTID," +
                            "PRODUCTCODE, " +
                            "FIRSTUNITNAME,SECONDUNITNAME,\n" +
                            "SUM(WDIRECTION * FIRSTAMOUNT)  AS FIRSTAMOUNT, \n" +
                            "SUM(WDIRECTION * SECONDAMOUNT) AS SECONDAMOUNT \n" +
                            "from VW_WAREHOUSESTOCKMOVEMENT where \n" +
                            "BARCODENO = '" + secilenUrunBarkodu + "'\n" +
                            "group by BARCODEID,BARCODENO,PALETBARCODES,PRODUCTNAME,PALETID,PRODUCTID," +
                            "PRODUCTCODE," +
                            "FIRSTUNITNAME,SECONDUNITNAME\n" +
                            "having SUM(WDIRECTION * FIRSTAMOUNT) != 0 or \n" +
                            "SUM(WDIRECTION * SECONDAMOUNT) != 0";
                    fillProducts.execute(query);
                } else {

                    FillBarkod fillProducts = new FillBarkod();
                    String query = "Select BARCODEID,BARCODENO,PRODUCTNAME,PALETID,PALETBARCODES,PRODUCTID," +
                            "PRODUCTCODE, " +
                            "FIRSTUNITNAME,SECONDUNITNAME,\n" +
                            "SUM(WDIRECTION * FIRSTAMOUNT)  AS FIRSTAMOUNT, \n" +
                            "SUM(WDIRECTION * SECONDAMOUNT) AS SECONDAMOUNT \n" +
                            "from VW_WAREHOUSESTOCKMOVEMENT where \n" +
                            "BARCODEID='" + secilenUrunBarkodu + "')\n" +
                            "group by BARCODEID,BARCODENO,PALETBARCODES,PRODUCTNAME,PALETID,PRODUCTID," +
                            "PRODUCTCODE," +
                            "FIRSTUNITNAME,SECONDUNITNAME\n" +
                            "having SUM(WDIRECTION * FIRSTAMOUNT) != 0 or \n" +
                            "SUM(WDIRECTION * SECONDAMOUNT) != 0";
                    fillProducts.execute(query);
                }
            }
        }
        if (resultCode == Activity.RESULT_CANCELED) {
            //Write your code if there's no result
        }
    }
}



