package com.emrehmrc.depoqr;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
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
import android.text.Editable;
import android.text.TextWatcher;
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
import android.widget.TextView;
import android.widget.Toast;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import static com.emrehmrc.depoqr.AnaSayfa.MyPREFERENCES;

/**
 * Created by cenah on 2/27/2018.
 */

public class PlasiyerSatisSec extends AppCompatActivity {
    ActionBar ab;
    ConnectionClass connectionClass;
    SharedPreferences sharedPreferences;
    Bundle bundle;
    TextView tx_depono, tx_cariadi, tx_urunkodu, tx_iskdv, tx_typeName, tx_moneyType, tx_productCount, tx_toplam;
    EditText tx_price, tx_kdv;
    String incomingKod, incomingAd, incomingDepo, memberid, comid, incomingDepoId, secilenUrun, incomingCariId, priceId, secilenUrunAdi;
    AutoCompleteTextView tx_urunadi;
    ImageView btn_drop;
    ArrayAdapter<ProductsP> adapter;
    Vibrator vibrator;
    ModelProductInfo modelProductInfo = new ModelProductInfo();
    ToneGenerator toneG;
    Button btn_gir;
    String deneme;
    ArrayList<ProductsP> products = new ArrayList<>();
    boolean check = true;
    float genelToplam;
    int productCount;
    Button btn_satis, btn_direkSatis;
    String typeUnitId, secilenUrunId;
    float birinciBirimFaktor, typeUnitFaktor;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_plasiyersatis_sec);
        this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        vibrator = (Vibrator) getSystemService(VIBRATOR_SERVICE);
        toneG = new ToneGenerator(AudioManager.STREAM_ALARM, 100);
        sharedPreferences = getApplicationContext().getSharedPreferences(MyPREFERENCES, Context.MODE_PRIVATE);
        memberid = sharedPreferences.getString("ID", null);
        comid = sharedPreferences.getString("Companiesid", null);
        connectionClass = new ConnectionClass();
        ab = getSupportActionBar();
        ab.setTitle("Plasiyer Satiş");
        ab.setSubtitle("Satiş");
        ab.setBackgroundDrawable(getResources().getDrawable(R.drawable.arkaplan));
        tx_cariadi = (TextView) findViewById(R.id.gelenad);
        tx_depono = (TextView) findViewById(R.id.gelendepo);
        tx_urunkodu = (TextView) findViewById(R.id.tx_urunKodu);
        tx_urunadi = (AutoCompleteTextView) findViewById(R.id.tx_urunAdi);
        btn_drop = (ImageView) findViewById(R.id.btn_drop);
        tx_price = (EditText) findViewById(R.id.tx_price);
        tx_kdv = (EditText) findViewById(R.id.tx_kdv);
        tx_iskdv = (TextView) findViewById(R.id.tx_iskdv);
        tx_typeName = (TextView) findViewById(R.id.tx_typeName);
        tx_moneyType = (TextView) findViewById(R.id.tx_moneyTeype);
        tx_productCount = (TextView) findViewById(R.id.tx_productCount);
        btn_gir = (Button) findViewById(R.id.btn_gir);
        btn_satis = (Button) findViewById(R.id.btn_satis);
        btn_direkSatis = (Button) findViewById(R.id.btn_direkSatis);
        incomingAd = sharedPreferences.getString("plasiyerCariAd", null);
        incomingKod = sharedPreferences.getString("plasiyerCariKod", null);
        incomingCariId = sharedPreferences.getString("plasiyerCariId", null);
        incomingDepo = sharedPreferences.getString("plasiyerDepoAd", null);
        incomingDepoId = sharedPreferences.getString("plasiyerDepoId", null);
        tx_depono.setText(incomingDepo);
        tx_cariadi.setText(incomingAd);
        FillList fillProduct = new FillList();
        fillProduct.execute("");
        btn_drop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                tx_urunadi.showDropDown();
            }
        });
        btn_gir.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                check = true;
                for (ProductsP productsP : products) {
                    if (productsP.getProductKod().equals(tx_urunkodu.getText().toString())) {
                        tx_urunadi.setText(productsP.getProductadi());
                        secilenUrun = productsP.getProductKod();
                        secilenUrunAdi = productsP.getProductadi();
                        secilenUrunId = productsP.getProductId();
                        check = false;
                        CountAndGetType countAndGetType = new CountAndGetType();
                        countAndGetType.execute("");
                        break;
                    }
                }
                if (check)
                    Toast.makeText(getApplicationContext(), "Yanliş Ürün Kodu.", Toast.LENGTH_SHORT).show();
            }
        });
        btn_satis.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                float kdv, fiyat;
                if (!tx_productCount.getText().toString().isEmpty()) {
                    if (!tx_kdv.getText().toString().isEmpty()) {
                        if (!tx_price.getText().toString().isEmpty()) {
                            fiyat = Float.valueOf(tx_price.getText().toString());
                            kdv = Float.valueOf(tx_kdv.getText().toString());
                            if (fiyat >= 0 && kdv >= 0) {
                                Intent intent = new Intent(PlasiyerSatisSec.this, PlasiyerSatisThree.class);
                                intent.putExtra("BirimFiyat", tx_price.getText().toString());
                                intent.putExtra("KDV", tx_kdv.getText().toString());
                                intent.putExtra("secilenUrun", secilenUrun);
                                intent.putExtra("secilenUrunAdi", secilenUrunAdi);


                                if (tx_iskdv.getText().toString().equals("EVET")) {
                                    intent.putExtra("KDVDAHIL", "1");
                                } else intent.putExtra("KDVDAHIL", "0");
                                finish();
                                startActivity(intent);

                            } else
                                Toast.makeText(getApplicationContext(), "HATA, KDV VE BIRIM FIYAT SIFIRDAN BÜYÜK OLMALI! .", Toast.LENGTH_LONG).show();
                        } else
                            Toast.makeText(getApplicationContext(), "BIRIM FIYAT BOŞ GIRILEMEZ.", Toast.LENGTH_LONG).show();
                    } else
                        Toast.makeText(getApplicationContext(), "KDV BOŞ GIRILEMEZ.", Toast.LENGTH_LONG).show();
                } else
                    Toast.makeText(getApplicationContext(), "LÜTFEN ÜRÜN SEÇINIZ.", Toast.LENGTH_LONG).show();


            }
        });
        btn_direkSatis.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                float kdv, fiyat;
                if (!tx_productCount.getText().toString().isEmpty()) {
                    if (!tx_kdv.getText().toString().isEmpty()) {
                        if (!tx_price.getText().toString().isEmpty()) {
                            fiyat = Float.valueOf(tx_price.getText().toString());
                            kdv = Float.valueOf(tx_kdv.getText().toString());
                            if (fiyat >= 0 && kdv >= 0) {
                                Intent intent = new Intent(PlasiyerSatisSec.this, PlasiyerSatisDirek.class);
                                intent.putExtra("BirimFiyat", tx_price.getText().toString());
                                intent.putExtra("KDV", tx_kdv.getText().toString());
                                intent.putExtra("secilenUrun", secilenUrun);
                                intent.putExtra("secilenUrunAdi", secilenUrunAdi);


                                if (tx_iskdv.getText().toString().equals("EVET")) {
                                    intent.putExtra("KDVDAHIL", "1");
                                } else intent.putExtra("KDVDAHIL", "0");
                                finish();
                                startActivity(intent);

                            } else
                                Toast.makeText(getApplicationContext(), "HATA, KDV VE BIRIM FIYAT SIFIRDAN BÜYÜK OLMALI! .", Toast.LENGTH_LONG).show();
                        } else
                            Toast.makeText(getApplicationContext(), "BIRIM FIYAT BOŞ GIRILEMEZ.", Toast.LENGTH_LONG).show();
                    } else
                        Toast.makeText(getApplicationContext(), "KDV BOŞ GIRILEMEZ.", Toast.LENGTH_LONG).show();
                } else
                    Toast.makeText(getApplicationContext(), "LÜTFEN ÜRÜN SEÇINIZ.", Toast.LENGTH_LONG).show();

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
            Intent i = new Intent(PlasiyerSatisSec.this, SliderMenu.class);
            bundle = ActivityOptions.makeSceneTransitionAnimation(PlasiyerSatisSec.this).toBundle();
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
    public class FillList extends AsyncTask<String, String, String> {
        String z = "";

        @Override
        protected void onPostExecute(String r) {
            for (int i = 0; i < products.size(); i++) {
                for (int j = i + 1; j < products.size(); j++) {
                    if (products.get(i).getProductKod().equals(products.get(j).getProductKod())) {
                        productCount++;
                        products.remove(j);
                        j--;
                    }
                }
            }
            adapter = new ArrayAdapter<ProductsP>(getApplicationContext(), android.R.layout.simple_dropdown_item_1line, products);
            tx_urunadi.setAdapter(adapter);
            tx_urunadi.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    ProductsP products1;
                    products1 = (ProductsP) parent.getItemAtPosition(position);
                    //secilenUrun = products1.getProductno();
                    String secilenUrunKodu = products1.getProductKod();
                    tx_urunkodu.setText(secilenUrunKodu);
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
                    String query = "Select BARCODEID,BARCODENO,PRODUCTNAME,PALETID,PALETBARCODES,PRODUCTID," +
                            "PRODUCTCODE, " +
                            "FIRSTUNITNAME,SECONDUNITNAME,\n" +
                            "SUM(WDIRECTION * FIRSTAMOUNT)  AS FIRSTAMOUNT, \n" +
                            "SUM(WDIRECTION * SECONDAMOUNT) AS SECONDAMOUNT \n" +
                            "from VW_WAREHOUSESTOCKMOVEMENT where\n" +
                            "(DESTINATIONWAREHOUSEID = \n" +
                            "'" + incomingDepoId + "' \n" +
                            "or SOURCEWAREHOUSEID = '" + incomingDepoId + "') \n" +
                            "group by BARCODEID,BARCODENO,PALETBARCODES,PRODUCTNAME,PALETID,PRODUCTID," +
                            "PRODUCTCODE," +
                            "FIRSTUNITNAME,SECONDUNITNAME\n" +
                            "having SUM(WDIRECTION * FIRSTAMOUNT) != 0 or \n" +
                            "SUM(WDIRECTION * SECONDAMOUNT) != 0";
                    PreparedStatement ps = con.prepareStatement(query);
                    ResultSet rs = ps.executeQuery();
                    while (rs.next()) {
                        products.add(new ProductsP(rs.getString("PRODUCTNAME"), rs.getString("PRODUCTCODE"), rs.getString("PRODUCTID")));

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
    public class CountAndGetType extends AsyncTask<String, String, String> {
        String z = "";
        String birim;
        float birimcount;

        @Override
        protected void onPostExecute(String r) {
            String gecici = Float.toString(birimcount);
            tx_productCount.setText(gecici);
            tx_typeName.setText(birim);
            CheckNewestCurrent checkNewestCurrent = new CheckNewestCurrent();
            checkNewestCurrent.execute("");

        }

        @Override
        protected String doInBackground(String... params) {
            try {
                Connection con = connectionClass.CONN();
                if (con == null) {
                    z = "Error in connection with SQL server";
                } else {
                    String query = "Select BARCODEID,BARCODENO,PRODUCTNAME,PALETID,PALETBARCODES,PRODUCTID," +
                            "PRODUCTCODE, " +
                            "FIRSTUNITNAME,SECONDUNITNAME,\n" +
                            "SUM(WDIRECTION * FIRSTAMOUNT)  AS FIRSTAMOUNT, \n" +
                            "SUM(WDIRECTION * SECONDAMOUNT) AS SECONDAMOUNT \n" +
                            "from VW_WAREHOUSESTOCKMOVEMENT where\n" +
                            "(DESTINATIONWAREHOUSEID = \n" +
                            "'" + incomingDepoId + "' \n" +
                            "or SOURCEWAREHOUSEID = '" + incomingDepoId + "') and PRODUCTCODE = '" + secilenUrun + "' \n" +
                            "group by BARCODEID,BARCODENO,PALETBARCODES,PRODUCTNAME,PALETID,PRODUCTID," +
                            "PRODUCTCODE," +
                            "FIRSTUNITNAME,SECONDUNITNAME\n" +
                            "having SUM(WDIRECTION * FIRSTAMOUNT) != 0 or \n" +
                            "SUM(WDIRECTION * SECONDAMOUNT) != 0";
                    PreparedStatement ps = con.prepareStatement(query);
                    ResultSet rs = ps.executeQuery();
                    while (rs.next()) {
                        birimcount = birimcount + rs.getFloat("FIRSTAMOUNT");
                        birim = rs.getString("FIRSTUNITNAME");
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
        private String productId;

        public ProductsP(String productadi, String productKod, String productId) {
            this.productadi = productadi;
            this.productKod = productKod;
            this.productId = productId;
        }

        public ProductsP() {
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

        public String getProductId() {
            return productId;
        }

        public void setProductId(String productId) {
            this.productId = productId;
        }

        @Override
        public String toString() {
            return productadi;
        }
    }

    public class CheckNewestCurrent extends AsyncTask<String, String, String> {
        String z = "";
        boolean check = true;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected void onPostExecute(String r) {
            if (check) {
                ProductInfo infoget = new ProductInfo();
                infoget.execute("");

            } else {
                tx_iskdv.setText("EVET");
                Toast.makeText(getApplicationContext(), "CARI FIYAT LISTESI BULUNAMADI.", Toast.LENGTH_SHORT).show();
                tx_iskdv.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (tx_iskdv.getText().toString().equals("EVET")) {
                            modelProductInfo.setProductKdvC("0");
                            tx_iskdv.setText(modelProductInfo.getProductKdvC());
                        } else {
                            modelProductInfo.setProductKdvC("1");
                            tx_iskdv.setText(modelProductInfo.getProductKdvC());
                        }
                    }
                });
            }
        }

        @Override
        protected String doInBackground(String... strings) {
            try {
                Connection con = connectionClass.CONN();
                if (con == null) {
                    z = "Error in connection with SQL server";
                } else {
                    String query = "select PRICELISTID from VW_CURRENTPRICELIST where PRICEDATE = (\n" +
                            "select MAX(PRICEDATE) from VW_CURRENTPRICELIST WHERE PRICEDATE <= GETDATE() and CURRENTID = '" + incomingCariId + "' ) and\n" +
                            " CURRENTID = '" + incomingCariId + "'and ISDELETE = '0'";
                    PreparedStatement ps = con.prepareStatement(query);
                    ResultSet rs = ps.executeQuery();
                    if (rs.next()) {
                        priceId = rs.getString("PRICELISTID");
                        check = true;
                    } else {
                        check = false;
                    }
                    z = "Başarılı";
                }
            } catch (Exception ex) {
                z = "Veri Çekme Hatası";
            }
            return z;
        }
    }

    public class ProductInfo extends AsyncTask<String, String, String> {
        String z = "";
        boolean check = true;


        @Override
        protected void onPostExecute(String r) {
            if (check) {
                tx_kdv.setText(modelProductInfo.getProductKdv());
                tx_iskdv.setText(modelProductInfo.getProductKdvC());
                tx_iskdv.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        if (tx_iskdv.getText().toString().equals("EVET")) {
                            modelProductInfo.setProductKdvC("0");
                            tx_iskdv.setText(modelProductInfo.getProductKdvC());
                        } else {
                            modelProductInfo.setProductKdvC("1");
                            tx_iskdv.setText(modelProductInfo.getProductKdvC());
                        }
                    }
                });
                MoneyType Type = new MoneyType();
                Type.execute("");
            } else {
                //  Toast.makeText(getApplicationContext(), "Hata.", Toast.LENGTH_SHORT).show();
                tx_iskdv.setText("EVET");
                tx_iskdv.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (tx_iskdv.getText().toString().equals("EVET")) {
                            modelProductInfo.setProductKdvC("0");
                            tx_iskdv.setText(modelProductInfo.getProductKdvC());
                        } else {
                            modelProductInfo.setProductKdvC("1");
                            tx_iskdv.setText(modelProductInfo.getProductKdvC());
                        }
                    }
                });
            }

        }


        @Override
        protected String doInBackground(String... strings) {
            try {
                Connection con = connectionClass.CONN();
                if (con == null) {
                    z = "Error in connection with SQL server";
                } else {
                    String query = "select KDV , ISKDV , SALARY , NAME , MONEYUNITID , TYPEUNITID from VW_PRICELISTPRODUCT where PRICELISTID ='" + priceId + "' and PRODUCTCODE='" + secilenUrun + "'";
                    PreparedStatement ps = con.prepareStatement(query);
                    ResultSet rs = ps.executeQuery();
                    if (rs.next()) {
                        modelProductInfo.setProductKdv(rs.getString("KDV"));
                        modelProductInfo.setProductKdvC(rs.getString("ISKDV"));
                        modelProductInfo.setProductPrice(rs.getString("SALARY"));
                        modelProductInfo.setMoneyUnitId(rs.getString("MONEYUNITID"));
                        typeUnitId = rs.getString("TYPEUNITID");
                        check = true;
                    } else {
                        check = false;
                    }
                    z = "Başarılı";
                }
            } catch (Exception ex) {
                z = "Veri Çekme Hatası";
            }
            return z;
        }
    }

    public class UnitIdSec extends AsyncTask<String, String, String> {
        String z = "";
        String deneme;
        float toplam, price;

        @Override
        protected void onPostExecute(String s) {
            if (!modelProductInfo.getProductPrice().isEmpty() && typeUnitFaktor != 0.0f && birinciBirimFaktor != 0.0f) {
                price = Float.parseFloat(modelProductInfo.getProductPrice());
                if (price != 0) {
                    toplam = (birinciBirimFaktor * price) / typeUnitFaktor;
                    tx_price.setText(toplam + "");
                } else tx_price.setText(modelProductInfo.getProductPrice());

            } else tx_price.setText(modelProductInfo.getProductPrice());

        }

        @Override
        protected String doInBackground(String... strings) {

            try {
                Connection con = connectionClass.CONN();
                if (con == null) {
                    z = "Error in connection with SQL server";
                } else {
                    String query = "select  FAKTOR from PRODUCTTYPEUNIT where TYPEUNITID = '" + typeUnitId + "' and  PRODUCTID ='" + secilenUrunId + "' ";
                    PreparedStatement ps = con.prepareStatement(query);
                    ResultSet rs = ps.executeQuery();
                    if (rs.next()) {
                        typeUnitFaktor = rs.getFloat("FAKTOR");

                    } else {
                        deneme = "Empty";
                    }
                    z = "Başarılı";
                }
            } catch (Exception ex) {
                z = "Veri Çekme Hatası";
            }
            return z;
        }
    }

    public class UnitId extends AsyncTask<String, String, String> {
        String z = "";
        String deneme;

        @Override
        protected void onPostExecute(String s) {
            UnitIdSec unitIdSec = new UnitIdSec();
            unitIdSec.execute("");
        }

        @Override
        protected String doInBackground(String... strings) {

            try {
                Connection con = connectionClass.CONN();
                if (con == null) {
                    z = "Error in connection with SQL server";
                } else {
                    String query = "select  FAKTOR from PRODUCTTYPEUNIT where PRODUCTID ='" + secilenUrunId + "' and ISFIRST = '1' ";
                    PreparedStatement ps = con.prepareStatement(query);
                    ResultSet rs = ps.executeQuery();
                    if (rs.next()) {
                        birinciBirimFaktor = rs.getFloat("FAKTOR");

                    } else {
                        deneme = "Empty";
                    }
                    z = "Başarılı";
                }
            } catch (Exception ex) {
                z = "Veri Çekme Hatası";
            }
            return z;
        }
    }

    public class MoneyType extends AsyncTask<String, String, String> {
        String z = "";
        String deneme;

        @Override
        protected void onPostExecute(String s) {
            if (modelProductInfo.getMoneyunit().isEmpty()) {
                tx_moneyType.setText("TL");
            } else tx_moneyType.setText(modelProductInfo.getMoneyunit());

            UnitId unitId = new UnitId();
            unitId.execute("");
        }

        @Override
        protected String doInBackground(String... strings) {

            try {
                Connection con = connectionClass.CONN();
                if (con == null) {
                    z = "Error in connection with SQL server";
                } else {
                    String query = "select  NAME from MONEYUNIT where COMPANIESID ='" + comid + "' and ID ='" + modelProductInfo.getMoneyUnitId() + "'";
                    PreparedStatement ps = con.prepareStatement(query);
                    ResultSet rs = ps.executeQuery();
                    if (rs.next()) {
                        modelProductInfo.setMoneyunit(rs.getString("NAME"));
                    } else {
                        deneme = "Empty";
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
