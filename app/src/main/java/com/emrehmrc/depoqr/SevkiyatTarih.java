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
import android.os.Vibrator;
import android.support.annotation.RequiresApi;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
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
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.UUID;

import static com.emrehmrc.depoqr.AnaSayfa.MyPREFERENCES;


public class SevkiyatTarih extends AppCompatActivity {
    ActionBar ab;
    ConnectionClass connectionClass;
    SharedPreferences sharedPreferences;
    Bundle bundle;
    Vibrator vibrator;
    String memberid, comid, sevkId, sevkdepoid, sevkdepoAd, sevkNo, codeid;
    TextView gelenDepo, gelenSevk;
    ArrayList<ProductsP> products = new ArrayList<>();
    boolean check = true;
    int productCount;
    ArrayAdapter<ProductsP> adapter;
    ImageView btn_drop;
    AutoCompleteTextView tx_urunadi;
    EditText tx_urunkodu;
    String secilenUrun;
    Button btn_gir, btnqrread,btnsend;
    ToneGenerator toneG;
    boolean empty;
    ProgressBar progressBar;
    SevkiyetTarihAdapter emreAdaptor;
    RecyclerView recyclerView;
    ArrayList<SevkiyatÜrünleriRecyclerView> datalist;
    SevkiyatÜrünleriRecyclerView gecici;
    CheckBox checkBoxall;
    ArrayList<String> emptyArray = new ArrayList<>();
    ArrayList<String> emptyArray2 = new ArrayList<>();
    int i = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sevkiyat_tarih);
        this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);
        vibrator = (Vibrator) getSystemService(VIBRATOR_SERVICE);
        sharedPreferences = getApplicationContext().getSharedPreferences(MyPREFERENCES, Context.MODE_PRIVATE);
        memberid = sharedPreferences.getString("ID", null);
        comid = sharedPreferences.getString("Companiesid", null);
        sevkId = sharedPreferences.getString("SevkiyatForwadingID", null);
        sevkNo = sharedPreferences.getString("sevkNo", null);
        sevkdepoid = sharedPreferences.getString("SevkiyatDepoID", null);
        sevkdepoAd = sharedPreferences.getString("SevkiyatDepoAdi", null);
        connectionClass = new ConnectionClass();
        datalist = new ArrayList<SevkiyatÜrünleriRecyclerView>();
        ab = getSupportActionBar();
        ab.setTitle("SEVKİYAT");
        ab.setSubtitle("Etiket Okut");
        ab.setBackgroundDrawable(getResources().getDrawable(R.drawable.arkaplan));
        toneG = new ToneGenerator(AudioManager.STREAM_ALARM, 100);
        gelenSevk = (TextView) findViewById(R.id.gelenSevk);
        gelenDepo = (TextView) findViewById(R.id.gelenDepo);
        tx_urunkodu = (EditText) findViewById(R.id.tx_urunKodu);
        tx_urunadi = (AutoCompleteTextView) findViewById(R.id.tx_urunAdi);
        btn_drop = (ImageView) findViewById(R.id.btn_drop2);
        btn_gir = (Button) findViewById(R.id.btn_gir);
        progressBar = (ProgressBar) findViewById(R.id.pbbarS);
        recyclerView = (RecyclerView) findViewById(R.id.recyclerview);
        checkBoxall = (CheckBox) findViewById(R.id.checkBoxall);
        btnqrread = (Button) findViewById(R.id.btnqrread);
        btnsend = (Button) findViewById(R.id.btnsend);

        gelenSevk.setText(sevkNo);
        gelenDepo.setText(sevkdepoAd);
        FillList fillList = new FillList();
        fillList.execute("");
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
                for (ProductsP productsP : products) {
                    if (productsP.getProductKod().equals(tx_urunkodu.getText().toString())) {
                        tx_urunadi.setText(productsP.getProductadi());
                        secilenUrun = productsP.getProductKod();
                        check = false;
                        FillProducts fillProducts = new FillProducts();
                        String g2 = "Select PRODUCTIONDATE, BARCODEID,BARCODENO,PRODUCTNAME,PALETID,PALETBARCODES,PRODUCTID," +
                                "PRODUCTCODE, " +
                                "FIRSTUNITNAME,SECONDUNITNAME,\n" +
                                "SUM(WDIRECTION * FIRSTAMOUNT)  AS FIRSTAMOUNT, \n" +
                                "SUM(WDIRECTION * SECONDAMOUNT) AS SECONDAMOUNT \n" +
                                "from VW_WAREHOUSESTOCKMOVEMENT where PRODUCTID IN (Select PRODUCTID from VW_FORWARDINGPRODUCTPLAN where FORWARDINGID ='" + sevkId + "') and\n" +
                                "PRODUCTCODE = '" + secilenUrun + "'\n" +
                                "group by PRODUCTIONDATE,BARCODEID,BARCODENO,PALETBARCODES,PRODUCTNAME,PALETID,PRODUCTID," +
                                "PRODUCTCODE," +
                                "FIRSTUNITNAME,SECONDUNITNAME\n" +
                                "having SUM(WDIRECTION * FIRSTAMOUNT) != 0 or \n" +
                                "SUM(WDIRECTION * SECONDAMOUNT) != 0 order by PRODUCTIONDATE";
                        fillProducts.execute(g2);
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
                            "from VW_WAREHOUSESTOCKMOVEMENT where PRODUCTID IN (Select PRODUCTID from VW_FORWARDINGPRODUCTPLAN where FORWARDINGID ='" + sevkId + "') and\n" +
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

                emreAdaptor = new SevkiyetTarihAdapter(getApplicationContext(), datalist);
                recyclerView.setAdapter(emreAdaptor);

            }
        });
        btnqrread.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent i = new Intent(SevkiyatTarih.this, CodeReaderForSevkiyat.class);
                startActivityForResult(i, 1);

            }
        });
        btnsend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!datalist.isEmpty()){
                    emptyArray.clear();
                    emptyArray2.clear();
                    IsSame ısSame = new IsSame();
                    ısSame.execute("");
                }else{
                    Toast.makeText(SevkiyatTarih.this, "LİSTEDE ÜRÜN BULUNAMADI :(", Toast.LENGTH_SHORT).show();

                }
            }
        });

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
                AlertDialog.Builder builder2 = new AlertDialog.Builder(SevkiyatTarih.this);
                builder2.setTitle("UYARI!");
                builder2.setMessage("Sevkiyatta AYNI ÜRÜN BULUNDU  SİLİNSİN Mİ?");
                builder2.setNegativeButton("EVET", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        DeletePro deletePro = new DeletePro();
                        deletePro.execute("");
                    }
                });
                builder2.setPositiveButton("HAYIR, Eklemek istiyorum", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        SendProductss sendProducts2 = new SendProductss();
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
                            String q = "Select * from SENTFORWARDING where BARCODEID='" + datalist.get(j).getBarcodeid() + "' and FORWARDINGID ='" + sevkId + "'  ";
                            PreparedStatement ps = con.prepareStatement(q);
                            ResultSet rs = ps.executeQuery();
                            if (rs.next()) {
                                emptyArray.add(datalist.get(j).getBarcodeid());

                            } else {
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
            else {
                Toast.makeText(getApplicationContext(), "AKTARILDI!", Toast.LENGTH_SHORT).show();
                datalist.clear();
                recyclerView.setAdapter(null);
            }
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
                                            "'" + memberid + "','" + comid + "'," +
                                            "'" + sevkId + "'" + ",'" + datalist.get(i).getFirstamount() + "'," +
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
            emreAdaptor = new SevkiyetTarihAdapter(getApplicationContext(), datalist);
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
                    while (datalist.size() > 0) {
                        if (datalist.get(i).isChecked()) {
                            UUID uuıd = UUID.randomUUID();
                            String query = "insert into SENTFORWARDING" + " " +
                                    "(ID,MEMBERID,COMPANIESID," + "FORWARDINGID,FIRSTUNITAMOUNT," +
                                    "SECONDUNITAMOUNT," + "WAREHOUSEID,PRODUCTID,BARCODEID,PALETID,ISOKEY)" +
                                    " values(" + "'" + uuıd + "'," +
                                    "'" + memberid + "','" + comid + "'," +
                                    "'" + sevkId + "'" + ",'" + datalist.get(i).getFirstamount() + "'," +
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
                datalist.clear();
                emreAdaptor = new SevkiyetTarihAdapter(getApplicationContext(), datalist);
                recyclerView.setAdapter(null);

                Toast.makeText(SevkiyatTarih.this, "BAŞARIYLA SİLİNDİ", Toast.LENGTH_SHORT).show();
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
                        String query = "Delete  from SENTFORWARDING where  BARCODEID='" + emptyArray.get(j) + "' and FORWARDINGID ='" + sevkId + "'";
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


    @SuppressLint("NewApi")
    public class CheckDepo extends AsyncTask<String, String, String> {
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
                toneG.startTone(ToneGenerator.TONE_CDMA_CALL_SIGNAL_ISDN_PING_RING, 200);
                Intent intent = new Intent(getBaseContext(), UyariBildirim.class);
                intent.putExtra("UYARI", "DEPODA ÜRÜN BULUNAMADI!");
                startActivity(intent);

            } else {
                toneG.startTone(ToneGenerator.TONE_CDMA_CALL_SIGNAL_ISDN_PING_RING, 200);
                Intent intent = new Intent(getBaseContext(), UyariBildirim.class);
                intent.putExtra("UYARI", "GEÇERSIZ KOD!");
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
                    String query = "Select BARCODEID,BARCODENO,PRODUCTNAME,PALETID,PALETBARCODES,PRODUCTID," +
                            "PRODUCTCODE, " +
                            "FIRSTUNITNAME,SECONDUNITNAME,\n" +
                            "SUM(WDIRECTION * FIRSTAMOUNT)  AS FIRSTAMOUNT, \n" +
                            "SUM(WDIRECTION * SECONDAMOUNT) AS SECONDAMOUNT \n" +
                            "from VW_WAREHOUSESTOCKMOVEMENT where PRODUCTID IN (Select PRODUCTID from VW_FORWARDINGPRODUCTPLAN where FORWARDINGID ='"+sevkId+"') and\n" +
                            "(DESTINATIONWAREHOUSEID = \n" +
                            "'" + sevkdepoid + "' \n" +
                            "or SOURCEWAREHOUSEID = '" + sevkdepoid + "') \n" +
                            "group by BARCODEID,BARCODENO,PALETBARCODES,PRODUCTNAME,PALETID,PRODUCTID," +
                            "PRODUCTCODE," +
                            "FIRSTUNITNAME,SECONDUNITNAME\n" +
                            "having SUM(WDIRECTION * FIRSTAMOUNT) != 0 or \n" +
                            "SUM(WDIRECTION * SECONDAMOUNT) != 0";
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

                toneG.startTone(ToneGenerator.TONE_CDMA_ALERT_CALL_GUARD, 200);
                emreAdaptor = new SevkiyetTarihAdapter(getApplicationContext(), datalist);
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


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        recyclerView.setAdapter(null);
        datalist.clear();
        if (requestCode == 1) {
            if (resultCode == Activity.RESULT_OK) {
                codeid = data.getStringExtra("codeid");
                check = true;
                for (ProductsP productsP : products) {
                    if (productsP.getProductKod().equals(codeid)) {
                        tx_urunadi.setText(productsP.getProductadi());
                        secilenUrun = productsP.getProductKod();
                        check = false;
                        FillProducts fillProducts = new FillProducts();
                        String g2 = "Select PRODUCTIONDATE, BARCODEID,BARCODENO,PRODUCTNAME,PALETID,PALETBARCODES,PRODUCTID," +
                                "PRODUCTCODE, " +
                                "FIRSTUNITNAME,SECONDUNITNAME,\n" +
                                "SUM(WDIRECTION * FIRSTAMOUNT)  AS FIRSTAMOUNT, \n" +
                                "SUM(WDIRECTION * SECONDAMOUNT) AS SECONDAMOUNT \n" +
                                "from VW_WAREHOUSESTOCKMOVEMENT where PRODUCTID IN (Select PRODUCTID from VW_FORWARDINGPRODUCTPLAN where FORWARDINGID ='" + sevkId + "') and\n" +
                                "PRODUCTCODE = '" + secilenUrun + "'\n" +
                                "group by PRODUCTIONDATE,BARCODEID,BARCODENO,PALETBARCODES,PRODUCTNAME,PALETID,PRODUCTID," +
                                "PRODUCTCODE," +
                                "FIRSTUNITNAME,SECONDUNITNAME\n" +
                                "having SUM(WDIRECTION * FIRSTAMOUNT) != 0 or \n" +
                                "SUM(WDIRECTION * SECONDAMOUNT) != 0 order by PRODUCTIONDATE";
                        fillProducts.execute(g2);
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
                            "from VW_WAREHOUSESTOCKMOVEMENT where PRODUCTID IN (Select PRODUCTID from VW_FORWARDINGPRODUCTPLAN where FORWARDINGID ='" + sevkId + "') and\n" +
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
            Intent i = new Intent(SevkiyatTarih.this, SliderMenu.class);
            bundle = ActivityOptions.makeSceneTransitionAnimation(SevkiyatTarih.this).toBundle();
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
}
