package com.emrehmrc.depoqr;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Activity;
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
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static com.emrehmrc.depoqr.AnaSayfa.MyPREFERENCES;

public class BarkodBilgiEkrani extends AppCompatActivity {
    Bundle bundle;
    ActionBar ab;
    ConnectionClass connectionClass;
    SharedPreferences sharedPreferences;
    Vibrator vibrator;
    String memberid, comid, codeid;
    AutoCompleteTextView tx_barkodsec;
    ImageView btn_drop;
    Button btn_qr, btn_barkodgir, btn_hareket;
    UUID uuid;
    String PorB2;
    ArrayList<BarkodProducts> products = new ArrayList<>();
    ArrayAdapter<BarkodProducts> adapter;
    String secilenProductId;
    ProgressBar pbbarP;
    TextView tx_productName, tx_productCode, tx_productFirst, tx_productSecond, tx_productDate, tx_productPallete, tx_productInfo;
    String productName;
    String productCode;
    String productDetails;
    String productDate;
    float first;
    float secod;
    String firstAmount;
    String secondAmount;
    String productPalette;
    String deneme;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_barkod_bilgi_ekrani);
        this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        vibrator = (Vibrator) getSystemService(VIBRATOR_SERVICE);
        sharedPreferences = getApplicationContext().getSharedPreferences(MyPREFERENCES, Context.MODE_PRIVATE);
        memberid = sharedPreferences.getString("ID", null);
        comid = sharedPreferences.getString("Companiesid", null);
        connectionClass = new ConnectionClass();
        ab = getSupportActionBar();
        ab.setTitle("Barkod Bilgi Ekranı");
        ab.setBackgroundDrawable(getResources().getDrawable(R.drawable.arkaplan));
        tx_barkodsec = (AutoCompleteTextView) findViewById(R.id.tx_barkodsec);
        btn_drop = (ImageView) findViewById(R.id.btn_drop);
        btn_qr = (Button) findViewById(R.id.btn_qr);
        btn_barkodgir = (Button) findViewById(R.id.btn_barkodgir);
        pbbarP = (ProgressBar) findViewById(R.id.pbbarP);
        btn_hareket = (Button) findViewById(R.id.btn_hareket);
        tx_productName = (TextView) findViewById(R.id.tx_productName);
        tx_productCode = (TextView) findViewById(R.id.tx_productCode);
        tx_productFirst = (TextView) findViewById(R.id.tx_productFirst);
        tx_productSecond = (TextView) findViewById(R.id.tx_productSecond);
        tx_productDate = (TextView) findViewById(R.id.tx_productDate);
        tx_productPallete = (TextView) findViewById(R.id.tx_productPallete);
        tx_productInfo = (TextView) findViewById(R.id.tx_productInfo);

        FillBarkods fillBarkods = new FillBarkods();
        fillBarkods.execute("");
        btn_qr.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent i = new Intent(BarkodBilgiEkrani.this, CodeReaderForSevkiyat.class);
                startActivityForResult(i, 1);

            }


        });
        btn_drop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                tx_barkodsec.showDropDown();
            }
        });
        btn_barkodgir.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (tx_barkodsec.getText().toString().isEmpty()) {
                    Toast.makeText(getApplicationContext(), "Barkod Seçiniz.", Toast.LENGTH_SHORT).show();
                    btn_hareket.setVisibility(View.GONE);
                } else {
                    boolean exist = true;
                    for (int i = 0; i < products.size(); i++) {
                        if (products.get(i).getProductCode().equals(tx_barkodsec.getText().toString())) {
                            secilenProductId = products.get(i).getProductId();
                            FillListProduct fillListProduct = new FillListProduct();
                            fillListProduct.execute("");
                            exist = true;
                            break;
                        } else exist = false;
                    }
                    if (!exist)
                        Toast.makeText(getApplicationContext(), "Barkod Bulunamadı.", Toast.LENGTH_SHORT).show();
                    btn_hareket.setVisibility(View.GONE);

                }
            }
        });
        btn_hareket.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (secilenProductId.isEmpty())
                    Toast.makeText(getApplicationContext(), "Barkod Seçiniz.", Toast.LENGTH_SHORT).show();
                else {
                    Intent intent = new Intent(BarkodBilgiEkrani.this, BarkodBilgiEkraniSec.class);
                    intent.putExtra("secilenProductId", secilenProductId);
                    startActivity(intent);
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
            Intent i = new Intent(BarkodBilgiEkrani.this, SliderMenu.class);
            bundle = ActivityOptions.makeSceneTransitionAnimation(BarkodBilgiEkrani.this).toBundle();
            startActivity(i);


        } else if (id == R.id.geri) {
            finish();
            onBackPressed();

        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == 1) {

            if (resultCode == Activity.RESULT_OK) {
                codeid = data.getStringExtra("codeid");

                try {
                    uuid = UUID.fromString(codeid);
                    PorB2 = "fdsfds";

                } catch (Exception ex) {
                    PorB2 = codeid.substring(0, 1);
                    codeid = codeid.substring(1, codeid.length());

                }
                if (PorB2.equals("P")) {
                    Toast.makeText(getApplicationContext(), "PALET GIRILEMEZ!", Toast.LENGTH_SHORT).show();
                } else if (PorB2.equals("B")) {
                    Boolean exist = true;
                    for (int i = 0; i < products.size(); i++) {
                        if (products.get(i).getProductCode().equals(codeid)) {
                            secilenProductId = products.get(i).productId;
                            FillListProduct fillListProduct = new FillListProduct();
                            fillListProduct.execute("");
                            exist = true;
                            break;

                        } else exist = false;
                    }
                    if (!exist)
                        Toast.makeText(getApplicationContext(), "Barkod Bulunamadı.", Toast.LENGTH_SHORT).show();

                } else {
                    Boolean exist = true;
                    codeid = codeid.toUpperCase();
                    for (int i = 0; i < products.size(); i++) {
                        if (products.get(i).getProductId().equals(codeid)) {
                            secilenProductId = products.get(i).productId;
                            FillListProduct fillListProduct = new FillListProduct();
                            fillListProduct.execute("");
                            exist = true;
                            break;

                        } else exist = false;
                    }
                    if (!exist)
                        Toast.makeText(getApplicationContext(), "Barkod Bulunamadı.", Toast.LENGTH_SHORT).show();

                }
            }
            if (resultCode == Activity.RESULT_CANCELED) {
                //Write your code if there's no result
            }
        }
    }

    @SuppressLint("NewApi")
    public class FillBarkods extends AsyncTask<String, String, String> {
        String z = "";


        @Override
        protected void onPreExecute() {
        }

        @Override
        protected void onPostExecute(String r) {
            adapter = new ArrayAdapter<>(getApplicationContext(), android.R.layout.simple_dropdown_item_1line, products);
            tx_barkodsec.setAdapter(adapter);
        }

        @Override
        protected String doInBackground(String... strings) {
            try {
                Connection con = connectionClass.CONN();
                if (con == null) {
                    z = "Error in connection with SQL server";
                } else {
                    String query = "SELECT BARCODEID , PRODUCTBARCODE from VW_PALETBARCODE where ISDELETE = '0' and COMPANIESID = '" + comid + "' ";
                    PreparedStatement ps = con.prepareStatement(query);
                    ResultSet rs = ps.executeQuery();

                    while (rs.next()) {
                        products.add(new BarkodProducts(rs.getString("PRODUCTBARCODE"), rs.getString("BARCODEID")));
                    }
                    z = "Başarılı";
                }
            } catch (Exception ex) {
                z = "Veri Çekme Hatası";

            }
            return z;

        }
    }

    private class BarkodProducts {
        String productCode;
        String productId;

        private BarkodProducts(String productCode, String productId) {
            this.productCode = productCode;
            this.productId = productId;

        }

        public String getProductCode() {
            return productCode;
        }

        public void setProductCode(String productCode) {
            this.productCode = productCode;
        }

        public String getProductId() {
            return productId;
        }

        public void setProductId(String productId) {
            this.productId = productId;
        }

        @Override
        public String toString() {
            return productCode;
        }
    }

    @SuppressLint("NewApi")
    public class FillListProduct extends AsyncTask<String, String, String> {
        String z = "";

        @Override
        protected void onPreExecute() {
            pbbarP.setVisibility(View.VISIBLE);

        }

        @Override
        protected void onPostExecute(String r) {
            pbbarP.setVisibility(View.GONE);
            btn_hareket.setVisibility(View.VISIBLE);
            tx_productName.setText(productName);
            tx_productCode.setText(productCode);
            tx_productFirst.setText(new DecimalFormat("##.##").format(first)+ " " +firstAmount);
            if(secondAmount ==null || secod==0){
                tx_productSecond.setText("YOKTUR");
            }else{
                tx_productSecond.setText(new DecimalFormat("##.##").format(secod)+" "+secondAmount);
            }
            tx_productDate.setText(productDate);
            tx_productPallete.setText(productPalette);
            if (productDetails == null) tx_productInfo.setText("AÇIKLAMA YOK..");
            else tx_productInfo.setText(productDetails);
        }


        @Override
        protected String doInBackground(String... strings) {
            try {
                Connection con = connectionClass.CONN();
                if (con == null) {
                    z = "Error in connection with SQL server";
                } else {
                    String query = "SELECT * from VW_PALETBARCODE where ISDELETE = '0' and COMPANIESID = '" + comid + "' and BARCODEID ='" + secilenProductId + "'  ";
                    PreparedStatement ps = con.prepareStatement(query);
                    ResultSet rs = ps.executeQuery();

                    if (rs.next()) {
                        productName = rs.getString("PRODUCTNAME");
                        productCode = rs.getString("PRODUCTCODE");
                        productDetails = rs.getString("DESCRIPTION");
                        productDate = rs.getString("PRODUCTDATE");
                        first = rs.getFloat("FIRSTUNITAMOUNT");
                        firstAmount = rs.getString("FIRSTUNITNAME");
                        secod = rs.getFloat("SECONDUNITAMOUNT");
                        secondAmount = rs.getString("SECONDUNITNAME");

                        productPalette = rs.getString("PALETBARCODES");
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
