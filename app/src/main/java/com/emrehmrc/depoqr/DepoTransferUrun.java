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
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.emrehmrc.depoqr.adapter.DepoTransferUrunAdapter;
import com.emrehmrc.depoqr.adapter.SpecialAdapter;
import com.emrehmrc.depoqr.codereader.CodeReaderForSevkiyat;
import com.emrehmrc.depoqr.connection.ConnectionClass;
import com.emrehmrc.depoqr.model.SevkiyatÜrünleriRecyclerView;
import com.emrehmrc.depoqr.popup.UyariBildirim;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import static com.emrehmrc.depoqr.AnaSayfa.MyPREFERENCES;

public class DepoTransferUrun extends AppCompatActivity {
    ActionBar ab;
    ConnectionClass connectionClass;
    SharedPreferences sharedPreferences;
    Bundle bundle;
    Vibrator vibrator;
    String memberid, comid;
    ToneGenerator toneG;
    EditText edtName;
    int arraysize;
    TextView txtana, txthedef;
    String hedefdepoid, anadepoid;
    String ilksecim;
    public Boolean same;
    ArrayList<ProductsP> products = new ArrayList<>();
    ArrayAdapter<ProductsP> adapter;
    AutoCompleteTextView tx_urunadi;
    ImageView btn_drop;
    EditText tx_urunkodu;
    ProgressBar pbbar;
    private boolean check = true;
    Button btn_gir;
    public String secilenUrun;
    SpecialAdapter ADA;
    List<Map<String, String>> prolist = new ArrayList<Map<String, String>>();
    ListView lstBarcode;
    String paletid;
    private String barcodeid;
    Map<String, String> datanum;
    Button btnsend,btnqrread;
    String paletsil,barcodesil;
    private String codeid;
    DepoTransferUrunAdapter emreAdaptor;
    ArrayList<SevkiyatÜrünleriRecyclerView> datalist;
    RecyclerView recyclerView;
    SevkiyatÜrünleriRecyclerView gecici;
    CheckBox checkBoxall;
    ArrayList<Float> firstAmount=new ArrayList<>();
    ArrayList<Float> secondAmount=new ArrayList<>();
    TextView tx_birinciBirim,tx_ikinciBirim,tx_barkodSayisi;
    ImageView btn_calculate;
    float birincibirim,ikincibirim;
    int barkodsayisi;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_depo_transfer_urun);

        this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN); // make screen goes up when keybourd is open
        vibrator = (Vibrator) getSystemService(VIBRATOR_SERVICE);
        sharedPreferences = getApplicationContext().getSharedPreferences(MyPREFERENCES, Context.MODE_PRIVATE);
        memberid = sharedPreferences.getString("ID", null);
        comid = sharedPreferences.getString("Companiesid", null);
        connectionClass = new ConnectionClass();
        arraysize = 0;
        recyclerView = (RecyclerView) findViewById(R.id.recyclerview);

        datalist = new ArrayList<SevkiyatÜrünleriRecyclerView>();
        tx_birinciBirim = (TextView) findViewById(R.id.tx_birinciBirim);
        tx_ikinciBirim = (TextView) findViewById(R.id.tx_ikinciBirim);
        tx_barkodSayisi = (TextView) findViewById(R.id.tx_barkodSayisi);
        btn_calculate = (ImageView) findViewById(R.id.btn_calculate);
        btn_gir = (Button) findViewById(R.id.btn_gir);
        txtana = (TextView) findViewById(R.id.txtana);
        txthedef = (TextView) findViewById(R.id.txthedef);
        tx_urunadi = (AutoCompleteTextView) findViewById(R.id.tx_urunAdi);
        btn_drop = (ImageView) findViewById(R.id.btn_drop2);
        tx_urunkodu = (EditText) findViewById(R.id.tx_urunKodu);
        lstBarcode = (ListView) findViewById(R.id.lstproductsinfo);
        btnsend = (Button) findViewById(R.id.btnsend);
        btnqrread = (Button) findViewById(R.id.btnqrread);
        pbbar = (ProgressBar) findViewById(R.id.pbbar);
        checkBoxall = (CheckBox) findViewById(R.id.checkBoxall);

        ab = getSupportActionBar();
        ab.setTitle("DEPOLAR ARASI TRANSFER");
        ab.setSubtitle("Sevkiyat İşlemleri");
        ab.setBackgroundDrawable(getResources().getDrawable(R.drawable.arkaplan));
        toneG = new ToneGenerator(AudioManager.STREAM_ALARM, 100);
        txtana.setText(sharedPreferences.getString("AnaDepoName", null));
        txthedef.setText(sharedPreferences.getString("HedefDepoName", null));
        hedefdepoid = sharedPreferences.getString("HedefDepoId", null);
        anadepoid = sharedPreferences.getString("AnaDepoId", null);
        comid = sharedPreferences.getString("Companiesid", null);
        memberid = sharedPreferences.getString("ID", null);
        ilksecim = "1";
        same = false;
        FillList fillList = new FillList();
        fillList.execute("");
        btn_calculate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!datalist.isEmpty()) {
                    birincibirim = 0;
                    ikincibirim = 0;
                    for (int i = 0; i < datalist.size(); i++) {
                        if (datalist.get(i).isChecked()) {
                            birincibirim = birincibirim + firstAmount.get(i);
                            ikincibirim = ikincibirim + secondAmount.get(i);
                        }
                    }
                    tx_birinciBirim.setText(birincibirim + "");
                    tx_ikinciBirim.setText(ikincibirim + "");

                }
            }
        });

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
                recyclerView.setAdapter(null);
                datalist.clear();
                firstAmount.clear();
                secondAmount.clear();
                for (ProductsP productsP : products) {
                    if (productsP.getProductKod().equals(tx_urunkodu.getText().toString())) {
                        tx_urunadi.setText(productsP.getProductadi());
                        secilenUrun = productsP.getProductKod();
                        FillProducts fillProducts = new FillProducts();
                        String query = "Select PRODUCTIONDATE, BARCODEID,BARCODENO,PRODUCTNAME,PALETID,PALETBARCODES,PRODUCTID,PRODUCTCODE, FIRSTUNITNAME,SECONDUNITNAME,\n" +
                                "SUM(WDIRECTION * FIRSTAMOUNT)  AS FIRSTAMOUNT, \n" +
                                "SUM(WDIRECTION * SECONDAMOUNT) AS SECONDAMOUNT \n" +
                                "from VW_WAREHOUSESTOCKMOVEMENT where\n" +
                                "(DESTINATIONWAREHOUSEID = '" + anadepoid + "' or SOURCEWAREHOUSEID = '" + anadepoid + "') and COMPANIESID ='" + comid + "'\n" +
                                "and PRODUCTCODE = '" + secilenUrun + "'\n" +
                                "group by PRODUCTIONDATE, BARCODEID,BARCODENO,PALETBARCODES,PRODUCTNAME,PALETID,PRODUCTID,PRODUCTCODE,FIRSTUNITNAME,SECONDUNITNAME\n" +
                                "having SUM(WDIRECTION * FIRSTAMOUNT) != 0 or \n" +
                                "SUM(WDIRECTION * SECONDAMOUNT) != 0 order by PRODUCTIONDATE";
                        fillProducts.execute(query);
                        check = false;
                        break;
                    }
                }
                if (check) {
                    CheckDepo fillProducts = new CheckDepo();
                    String g2 = "Select PRODUCTIONDATE, BARCODEID,BARCODENO,PRODUCTNAME,PALETID,PALETBARCODES,PRODUCTID," +
                            "PRODUCTCODE, " +
                            "FIRSTUNITNAME,SECONDUNITNAME,\n" +
                            "SUM(WDIRECTION * FIRSTAMOUNT)  AS FIRSTAMOUNT, \n" +
                            "SUM(WDIRECTION * SECONDAMOUNT) AS SECONDAMOUNT \n" +
                            "from VW_WAREHOUSESTOCKMOVEMENT where " +
                            "PRODUCTCODE = '" + tx_urunkodu.getText().toString() + "'\n" +
                            "group by PRODUCTIONDATE,BARCODEID,BARCODENO,PALETBARCODES,PRODUCTNAME,PALETID,PRODUCTID," +
                            "PRODUCTCODE," +
                            "FIRSTUNITNAME,SECONDUNITNAME\n" +
                            "having SUM(WDIRECTION * FIRSTAMOUNT) != 0 or \n" +
                            "SUM(WDIRECTION * SECONDAMOUNT) != 0 order by PRODUCTIONDATE";
                    fillProducts.execute(g2);
                }

            }
        });
        btnsend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!datalist.isEmpty()) {

                    SendProducts addPalet = new SendProducts();
                    addPalet.execute("");
                } else
                    Toast.makeText(DepoTransferUrun.this, "LİSTEDE ÜRÜN BULUNAMADI :(", Toast.LENGTH_SHORT).show();
            }
        });
        btnqrread.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent i = new Intent(DepoTransferUrun.this, CodeReaderForSevkiyat.class);
                startActivityForResult(i, 1);

            }
        });
        checkBoxall.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
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

                emreAdaptor = new DepoTransferUrunAdapter(getApplicationContext(), datalist,firstAmount,secondAmount);
                recyclerView.setAdapter(emreAdaptor);

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
            Intent i = new Intent(DepoTransferUrun.this, SliderMenu.class);
            bundle = ActivityOptions.makeSceneTransitionAnimation(DepoTransferUrun.this).toBundle();
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
                    String secilenUrunKodu = products1.getProductKod();
                    tx_urunkodu.setText(secilenUrunKodu);
                    tx_urunadi.dismissDropDown();
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
                    String query = "Select BARCODEID,BARCODENO,PRODUCTNAME,PALETID,PALETBARCODES,PRODUCTID,PRODUCTCODE, FIRSTUNITNAME,SECONDUNITNAME,\n" +
                            "SUM(WDIRECTION * FIRSTAMOUNT)  AS FIRSTAMOUNT, \n" +
                            "SUM(WDIRECTION * SECONDAMOUNT) AS SECONDAMOUNT \n" +
                            "from VW_WAREHOUSESTOCKMOVEMENT where\n" +
                            "(DESTINATIONWAREHOUSEID = '" + anadepoid + "' or SOURCEWAREHOUSEID = '" + anadepoid + "') and COMPANIESID ='" + comid + "'\n" +
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

    @SuppressLint("NewApi")
    public class CheckDepo extends AsyncTask<String, String, String> {
        String z = "";
        Boolean empty;

        @Override
        protected void onPreExecute() {
            empty = true;
            pbbar.setVisibility(View.VISIBLE);
        }

        @Override
        protected void onPostExecute(String r) {
            pbbar.setVisibility(View.GONE);
            if (empty) {
                toneG.startTone(ToneGenerator.TONE_CDMA_CALL_SIGNAL_ISDN_PING_RING, 200);
                Intent intent = new Intent(getBaseContext(), UyariBildirim.class);
                intent.putExtra("UYARI", "GEÇERSIZ ÜRÜN KODU!");
                startActivity(intent);
            }else{
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

    @SuppressLint("NewApi")
    public class FillProducts extends AsyncTask<String, String, String> {
        String z = "";
        boolean empty;
        @Override
        protected void onPreExecute() {

            empty = true;
            pbbar.setVisibility(View.VISIBLE);
            birincibirim =0;
            ikincibirim=0;
            barkodsayisi=0;
        }

        @Override
        protected void onPostExecute(String r) {
            pbbar.setVisibility(View.GONE);
            if (!empty) {
                tx_barkodSayisi.setText(barkodsayisi+"");
                toneG.startTone(ToneGenerator.TONE_CDMA_ALERT_CALL_GUARD, 200);
                emreAdaptor = new DepoTransferUrunAdapter(getApplicationContext(), datalist,firstAmount,secondAmount);
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
                        barkodsayisi++;
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


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == 1) {
            if (resultCode == Activity.RESULT_OK) {
                codeid = data.getStringExtra("codeid");
                check = true;
                firstAmount.clear();
                secondAmount.clear();
                datalist.clear();
                recyclerView.setAdapter(null);
                for (ProductsP productsP : products) {
                    if (productsP.getProductKod().equals(codeid)) {
                        tx_urunadi.setText(productsP.getProductadi());
                        secilenUrun = productsP.getProductKod();
                        check = false;
                        FillProducts fillProducts = new FillProducts();
                        String query = "Select PRODUCTIONDATE, BARCODEID,BARCODENO,PRODUCTNAME,PALETID,PALETBARCODES,PRODUCTID,PRODUCTCODE, FIRSTUNITNAME,SECONDUNITNAME,\n" +
                                "SUM(WDIRECTION * FIRSTAMOUNT)  AS FIRSTAMOUNT, \n" +
                                "SUM(WDIRECTION * SECONDAMOUNT) AS SECONDAMOUNT \n" +
                                "from VW_WAREHOUSESTOCKMOVEMENT where\n" +
                                "(DESTINATIONWAREHOUSEID = '" + anadepoid + "' or SOURCEWAREHOUSEID = '" + anadepoid + "') and COMPANIESID ='" + comid + "'\n" +
                                "and PRODUCTCODE = '" + secilenUrun + "'\n" +
                                "group by PRODUCTIONDATE,BARCODEID,BARCODENO,PALETBARCODES,PRODUCTNAME,PALETID,PRODUCTID,PRODUCTCODE,FIRSTUNITNAME,SECONDUNITNAME\n" +
                                "having SUM(WDIRECTION * FIRSTAMOUNT) != 0 or \n" +
                                "SUM(WDIRECTION * SECONDAMOUNT) != 0 order by PRODUCTIONDATE";
                        fillProducts.execute(query);
                        break;

                    }
                }
                if (check) {
                    CheckDepo fillProducts = new CheckDepo();
                    String g2 = "Select PRODUCTIONDATE, BARCODEID,BARCODENO,PRODUCTNAME,PALETID,PALETBARCODES,PRODUCTID," +
                            "PRODUCTCODE, " +
                            "FIRSTUNITNAME,SECONDUNITNAME,\n" +
                            "SUM(WDIRECTION * FIRSTAMOUNT)  AS FIRSTAMOUNT, \n" +
                            "SUM(WDIRECTION * SECONDAMOUNT) AS SECONDAMOUNT \n" +
                            "from VW_WAREHOUSESTOCKMOVEMENT where " +
                            "PRODUCTCODE = '" + codeid + "'\n" +
                            "group by PRODUCTIONDATE,BARCODEID,BARCODENO,PALETBARCODES,PRODUCTNAME,PALETID,PRODUCTID," +
                            "PRODUCTCODE," +
                            "FIRSTUNITNAME,SECONDUNITNAME\n" +
                            "having SUM(WDIRECTION * FIRSTAMOUNT) != 0 or \n" +
                            "SUM(WDIRECTION * SECONDAMOUNT) != 0 order by PRODUCTIONDATE";
                    fillProducts.execute(g2);
                }

            }
            if (resultCode == Activity.RESULT_CANCELED) {
                //Write your code if there's no result
            }
        }
    }

    @SuppressLint("NewApi")
    public class SendProducts extends AsyncTask<String, String, String> {
        String z = "";
        boolean hata;
        int i = 0;
        @Override
        protected void onPreExecute() {
            pbbar.setVisibility(View.VISIBLE);
            hata = false;

        }

        @Override
        protected void onPostExecute(String r) {
            pbbar.setVisibility(View.GONE);
            emreAdaptor = new DepoTransferUrunAdapter(getApplicationContext(), datalist,firstAmount,secondAmount);
            recyclerView.setAdapter(emreAdaptor);
            if (!hata)
                Toast.makeText(getApplicationContext(), "AKTARILDI!", Toast.LENGTH_SHORT).show();
            datalist.clear();
            recyclerView.setAdapter(null);
        }

        @Override
        protected String doInBackground(String... params) {
            try {
                Connection con = connectionClass.CONN();
                if (con == null) {
                    z = "Error in connection with SQL server";
                } else {
                    for (int i = 0; i < datalist.size(); i++) {
                        if (datalist.get(i).isChecked()) {
                            paletsil = datalist.get(i).getPaletid();
                            barcodesil = datalist.get(i).getBarcodeid();
                            UUID uuıd = UUID.randomUUID();
                            String query2 = "insert into WAREHOUSETRANSFER" + " values ('" + uuıd + "','" + memberid + "','" + barcodesil + "','" + paletsil + "','" + anadepoid + "','" + hedefdepoid + "','"+firstAmount.get(i)+"','"+secondAmount.get(i)+"',GETDATE())";
                            PreparedStatement preparedStatement = con.prepareStatement(query2);
                            preparedStatement.executeUpdate();
                        }
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

}
