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

import static com.emrehmrc.depoqr.AnaSayfa.MyPREFERENCES;

/**
 * Created by cenah on 2/27/2018.
 */

public class PlasiyerSatisSec extends AppCompatActivity {
    ActionBar ab;
    ConnectionClass connectionClass;
    SharedPreferences sharedPreferences;
    Bundle bundle;
    TextView tx_depono, tx_cariadi, tx_urunkodu, tx_iskdv, tx_typeName, tx_moneyType, tx_productCount;
    EditText tx_price, tx_kdv;
    String incomingKod, incomingAd, incomingDepo, memberid, comid, incomingDepoId, secilenUrun, incomingCariId, control, priceId;
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
        Intent incomingIntent = getIntent();
        incomingAd = sharedPreferences.getString("plasiyerCariAd", null);
        incomingKod = sharedPreferences.getString("plasiyerCariKod", null);
        incomingCariId = sharedPreferences.getString("plasiyerCariId", null);
        incomingDepo = sharedPreferences.getString("plasiyerDepoAd", null);
        incomingDepoId = sharedPreferences.getString("plasiyerDepoId", null);

        tx_depono.setText(incomingDepo);
        tx_cariadi.setText(incomingAd);
        FillList filldepo = new FillList();
        filldepo.execute("");
        btn_drop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                tx_urunadi.showDropDown();
            }
        });
        btn_gir.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                check=true;
                for (ProductsP productsP : products) {
                    if (productsP.getProductKod().equals(tx_urunkodu.getText().toString())) {
                        tx_urunadi.setText(productsP.getProductadi());
                        secilenUrun = productsP.getProductno();
                        check = false;
                        CheckProductInDepo checkdepo = new CheckProductInDepo();
                        checkdepo.execute("");

                    }
                }
                if(check) Toast.makeText(getApplicationContext(), "Yanliş Ürün Kodu.", Toast.LENGTH_SHORT).show();
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
                    String query = "SELECT ID , NAME , CODE FROM " + "VW_PRODUCT  where COMPANIESID='" + comid + "' and ISDELETE ='0' ";
                    PreparedStatement ps = con.prepareStatement(query);
                    ResultSet rs = ps.executeQuery();
                    while (rs.next()) {
                        products.add(new ProductsP(rs.getString("NAME")
                                , rs.getString("ID")
                                , rs.getString("CODE")));

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
        private String productno;
        private String productKod;

        public ProductsP() {
        }

        public ProductsP(String productadi, String productno, String productKod) {
            this.productadi = productadi;
            this.productno = productno;
            this.productKod = productKod;
        }

        public String getProductadi() {
            return productadi;
        }

        public void setProductadi(String productadi) {
            this.productadi = productadi;
        }

        public String getProductno() {
            return productno;
        }

        public void setProductno(String productno) {
            this.productno = productno;
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

    public class CheckProductInDepo extends AsyncTask<String, String, String> {

        String z = "";

        @Override
        protected void onPostExecute(String r) {

            if (control.equals("Empty")) {

                Toast.makeText(getApplicationContext(), "Depoda Ürün Bulunamadı.", Toast.LENGTH_SHORT).show();
                toneG.startTone(ToneGenerator.TONE_CDMA_ALERT_CALL_GUARD, 200);
                vibrator.vibrate(100);
                tx_urunadi.setText("");
            } else {

                CheckNewestCurrent newestCurrent = new CheckNewestCurrent();
                newestCurrent.execute("");
                ProductInfo infoget = new ProductInfo();
                infoget.execute("");
            }

        }

        @Override
        protected String doInBackground(String... strings) {
            try {
                Connection con = connectionClass.CONN();
                if (con == null) {
                    z = "Error in connection with SQL server";
                } else {
                    String query = "SELECT PRODUCTID FROM " + "VW_WAREHOUSEPRODUCT  where WAREHOUSEID ='" + incomingDepoId + "' and PRODUCTID = '" + secilenUrun + "'and COMPANIESID='" + comid + "'";
                    PreparedStatement ps = con.prepareStatement(query);
                    ResultSet rs = ps.executeQuery();
                    if (rs.next()) {
                        control = rs.getString("PRODUCTID");
                    } else {
                        control = "Empty";
                    }
                    z = "Başarılı";
                }
            } catch (Exception ex) {
                z = "Veri Çekme Hatası";
            }
            return z;
        }
    }

    public class CheckNewestCurrent extends AsyncTask<String, String, String> {
        String z = "";

        @Override
        protected String doInBackground(String... strings) {
            try {
                Connection con = connectionClass.CONN();
                if (con == null) {
                    z = "Error in connection with SQL server";
                } else {
                    String query = "select PRICELISTID from VW_CURRENTPRICELIST where PRICEDATE = (\n" +
                            "select MAX(PRICEDATE) from VW_CURRENTPRICELIST WHERE PRICEDATE <= GETDATE() and CURRENTID = '" + incomingCariId + "' ) and\n" +
                            " CURRENTID = '" + incomingCariId + "'";
                    PreparedStatement ps = con.prepareStatement(query);
                    ResultSet rs = ps.executeQuery();
                    if (rs.next()) {
                        priceId = rs.getString("PRICELISTID");
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
        String deneme;


        @Override
        protected void onPostExecute(String r) {

            tx_price.setText(modelProductInfo.getProductPrice());
            tx_kdv.setText(modelProductInfo.getProductKdv());
            tx_iskdv.setText(modelProductInfo.getProductKdvC());
            tx_typeName.setText(modelProductInfo.getTypeName());
            tx_iskdv.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String change;
                    change = modelProductInfo.getProductKdvC();
                    if (change.equals("EVET")) {
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
        }


        @Override
        protected String doInBackground(String... strings) {
            try {
                Connection con = connectionClass.CONN();
                if (con == null) {
                    z = "Error in connection with SQL server";
                } else {
                    String query = "select KDV , ISKDV , SALARY , NAME , MONEYUNITID from VW_PRICELISTPRODUCT where PRICELISTID ='" + priceId + "' and PRODUCTID ='" + control + "'";
                    PreparedStatement ps = con.prepareStatement(query);
                    ResultSet rs = ps.executeQuery();
                    if (rs.next()) {
                        modelProductInfo.setProductKdv(rs.getString("KDV"));
                        modelProductInfo.setProductKdvC(rs.getString("ISKDV"));
                        modelProductInfo.setProductPrice(rs.getString("SALARY"));
                        modelProductInfo.setTypeName(rs.getString("NAME"));
                        modelProductInfo.setMoneyUnitId(rs.getString("MONEYUNITID"));
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
            tx_moneyType.setText(modelProductInfo.getMoneyunit());
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
