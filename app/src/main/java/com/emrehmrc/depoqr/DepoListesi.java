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
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.emrehmrc.depoqr.adapter.DepoListesiAdapter;
import com.emrehmrc.depoqr.connection.ConnectionClass;
import com.emrehmrc.depoqr.model.DepoListesiModel;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;

import static com.emrehmrc.depoqr.AnaSayfa.MyPREFERENCES;

public class DepoListesi extends AppCompatActivity {
    Bundle bundle;
    ActionBar ab;
    ConnectionClass connectionClass;
    SharedPreferences sharedPreferences;
    Vibrator vibrator;
    String memberid, comid;
    String secilenDepoName, getSecilenDepoId;
    ArrayAdapter<Depolar> adapterDepo;
    ArrayAdapter<String> adapterName;
    ArrayList<Depolar> depolars = new ArrayList<>();
    AutoCompleteTextView tx_deposec;
    EditText tx_productName;
    ImageView btn_drop;
    //ImageView  btn_filter;
    Button btn_depoAra;
    ProgressBar pbbarP;
    ArrayList<DepoListesiModel> products = new ArrayList<>();
    private DepoListesiAdapter adapter;
    ListView lst_prdoucts;
    ArrayList<String> nameArray = new ArrayList<>();
    ArrayList<String> codeArray = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_depo_listesi);
        this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        vibrator = (Vibrator) getSystemService(VIBRATOR_SERVICE);
        sharedPreferences = getApplicationContext().getSharedPreferences(MyPREFERENCES, Context.MODE_PRIVATE);
        memberid = sharedPreferences.getString("ID", null);
        comid = sharedPreferences.getString("Companiesid", null);
        connectionClass = new ConnectionClass();
        ab = getSupportActionBar();
        ab.setTitle("Depo Listesi");
        ab.setBackgroundDrawable(getResources().getDrawable(R.drawable.arkaplan));
        tx_deposec = (AutoCompleteTextView) findViewById(R.id.tx_deposec);
        tx_productName = (EditText) findViewById(R.id.tx_productName);
        btn_drop = (ImageView) findViewById(R.id.btn_drop);
        btn_depoAra = (Button) findViewById(R.id.btn_depoAra);
        pbbarP = (ProgressBar) findViewById(R.id.pbbarP);
        //btn_filter = (ImageView) findViewById(R.id.btn_filter);
        lst_prdoucts = (ListView) findViewById(R.id.lst_prdoucts);
        FillListDepo fillListDepo = new FillListDepo();
        fillListDepo.execute("");

        btn_drop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                tx_deposec.showDropDown();
            }
        });
        btn_depoAra.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                lst_prdoucts.setAdapter(null);
                products.clear();
                boolean find = true;
                String deposecimi = tx_deposec.getText().toString();
                if (!deposecimi.isEmpty()) {
                    for (int i = 0; i < depolars.size(); i++) {
                        if (depolars.get(i).getDepoadi().equals(deposecimi)) {
                            getSecilenDepoId = depolars.get(i).getDepono();
                            FillListProduct fillListProduct = new FillListProduct();
                            fillListProduct.execute("");
                            find=true;
                            break;
                        } else {
                            find = false;
                        }
                    }

                } else
                    Toast.makeText(DepoListesi.this, "Depo Giriniz!", Toast.LENGTH_LONG).show();
                if (!find)
                    Toast.makeText(DepoListesi.this, "Geçersiz Depo!", Toast.LENGTH_SHORT).show();

            }
        });
       /* btn_filter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String nameCode;
                nameCode = tx_productName.getText().toString();
                if (!nameCode.isEmpty()) {
                    if (nameArray.contains(nameCode) || codeArray.contains(nameCode)) {
                        adapter.getFilter().filter(nameCode);
                    } else {
                        Toast.makeText(DepoListesi.this, "Geçersiz Ürün!", Toast.LENGTH_LONG).show();
                        tx_productName.setText("");
                    }
                } else
                    Toast.makeText(DepoListesi.this, "Ürün Ismi veya Kodu Giriniz!", Toast.LENGTH_LONG).show();
            }
        });*/
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
            Intent i = new Intent(DepoListesi.this, SliderMenu.class);
            bundle = ActivityOptions.makeSceneTransitionAnimation(DepoListesi.this).toBundle();
            startActivity(i);


        } else if (id == R.id.geri) {
            finish();
            onBackPressed();

        }

        return super.onOptionsItemSelected(item);
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
            tx_deposec.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    Depolar depolar = (Depolar) parent.getItemAtPosition(position);
                    secilenDepoName = depolar.getDepoadi();
                    getSecilenDepoId = depolar.getDepono();
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

    @SuppressLint("NewApi")
    public class FillListProduct extends AsyncTask<String, String, String> {
        String z = "";
        boolean check = false;

        @Override
        protected void onPreExecute() {
            pbbarP.setVisibility(View.VISIBLE);

        }

        @Override
        protected void onPostExecute(String r) {
            pbbarP.setVisibility(View.GONE);
            if (check) {
                for (int i = 0; i < products.size(); i++) {
                    for (int j = i + 1; j < products.size(); j++) {
                        if (products.get(i).getProductKod().equals(products.get(j).getProductKod())) {
                            float hesapla = products.get(j).getFirsAmount() + products.get(i).getFirsAmount();
                            float hesapla2 = products.get(j).getSecondAmount() + products.get(i).getSecondAmount();
                            products.get(i).setFirsAmount(hesapla);
                            products.get(i).setSecondAmount(hesapla2);
                            products.remove(j);
                            j--;

                        }
                    }

                }
                adapter = new DepoListesiAdapter(getApplicationContext(), products);
                lst_prdoucts.setAdapter(adapter);
             // adapterName = new ArrayAdapter<String>(getApplicationContext(), android.R.layout.simple_dropdown_item_1line, nameArray);
                //tx_productName.setAdapter(adapterName);
                tx_productName.addTextChangedListener(new TextWatcher() {
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

            } else
                Toast.makeText(DepoListesi.this, "Depoda Ürün Bulunamadı!", Toast.LENGTH_LONG).show();

        }

        @Override
        protected String doInBackground(String... strings) {
            try {
                Connection con = connectionClass.CONN();
                if (con == null) {
                    z = "Error in connection with SQL server";
                } else {
                    String query = "Select PRODUCTNAME,PRODUCTID,PRODUCTCODE, FIRSTUNITNAME,SECONDUNITNAME, SUM(WDIRECTION * FIRSTAMOUNT)  AS FIRSTAMOUNT, SUM(WDIRECTION * SECONDAMOUNT) AS SECONDAMOUNT from VW_WAREHOUSESTOCKMOVEMENT where (DESTINATIONWAREHOUSEID = '" + getSecilenDepoId + "' or SOURCEWAREHOUSEID = '" + getSecilenDepoId + "') group by BARCODEID,BARCODENO,PALETBARCODES,PRODUCTNAME,PALETID,PRODUCTID,PRODUCTCODE,FIRSTUNITNAME,SECONDUNITNAME having SUM(WDIRECTION * FIRSTAMOUNT) != 0 or SUM(WDIRECTION * SECONDAMOUNT) != 0 order by PRODUCTNAME";
                    PreparedStatement ps = con.prepareStatement(query);
                    ResultSet rs = ps.executeQuery();

                    while (rs.next()) {
                        products.add(new DepoListesiModel(rs.getString("PRODUCTNAME"), rs.getString("PRODUCTCODE"), rs.getFloat("FIRSTAMOUNT"), rs.getFloat("SECONDAMOUNT"), rs.getString("FIRSTUNITNAME"), rs.getString("SECONDUNITNAME")));
                        check = true;
                       // nameArray.add(rs.getString("PRODUCTNAME"));
                       // codeArray.add(rs.getString("PRODUCTCODE"));
                    }
                    z = "Başarılı";
                }
            } catch (Exception ex) {
                z = "Veri Çekme Hatası";
                check = false;

            }
            return z;
        }
    }

}


