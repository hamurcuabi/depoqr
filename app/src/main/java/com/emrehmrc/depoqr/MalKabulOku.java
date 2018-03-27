package com.emrehmrc.depoqr;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Activity;
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
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class MalKabulOku extends AppCompatActivity {


    EditText edtKod, edtName, edtCode;
    ActionBar ab;
    Button btnbarcoderead, btnonayla, btnbarcodewrite;
    ProgressBar pbbar;
    String barcodeid;
    ListView lstBarcode;
    ConnectionClass connectionClass;
    SpecialAdapter ADA;
    String codeid, paletid;
    String depoid;
    int arraysize;
    String query;
    String firstype, secondtype, ftype, stype;
    String Companiesid, memberid;
    int ilkgiris = 0;
    Map<String, String> datanum;
    String paletsil;
    String ilksecim;
    Bundle bundle;
    UUID uid2;
    ToneGenerator toneG;
    String PorB = "";
    String codeidfake = "", barcodesil;
    int silinenürün = 0;
    AddPalet add;
    Spinner spn;
    String findP;
    float first, second;
    Boolean isSuccess;
    private Menu menu;
    String PorB2 = "fdsfds";
    Boolean way = true;
    int sayma = 0;
    String incomingDepoAd;
    TextView gelenDeop;
    ArrayList<String> findPArray = new ArrayList<>();

    @RequiresApi(api = Build.VERSION_CODES.CUPCAKE)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mal_kabul_oku);
        gelenDeop = (TextView) findViewById(R.id.gelenDepo);
        Intent incomingIntent = getIntent();
        incomingDepoAd = incomingIntent.getStringExtra("secilenDepo");
        gelenDeop.setText("Depo: "+ incomingDepoAd);

        arraysize = 0;
        connectionClass = new ConnectionClass();
        ab = getSupportActionBar();
        ab.setTitle("MAL KABULÜ");
        ab.setSubtitle("Mal Aktarımı");
        ab.setBackgroundDrawable(getResources().getDrawable(R.drawable.arkaplan));
        toneG = new ToneGenerator(AudioManager.STREAM_ALARM, 100);
        spn = (Spinner) findViewById(R.id.spnPB);
        edtKod = (EditText) findViewById(R.id.edtKodinfo);
        edtName = (EditText) findViewById(R.id.edtNameinfo);
        edtCode = (EditText) findViewById(R.id.edtCode);
        edtCode.setOnTouchListener(new View.OnTouchListener() {

            @Override
            public boolean onTouch(View v, MotionEvent event) {

                v.setFocusable(true);
                v.setFocusableInTouchMode(true);
                return false;
            }
        });

        edtName.setText("TOPLAM ÜRÜN SAYISI: " + arraysize);
        btnbarcoderead = (Button) findViewById(R.id.btnbarcoderead);
        btnonayla = (Button) findViewById(R.id.btnonay);
        btnbarcodewrite = (Button) findViewById(R.id.btnbarcodeenter);
        pbbar = (ProgressBar) findViewById(R.id.pbbar);
        pbbar.setVisibility(View.GONE);
        lstBarcode = (ListView) findViewById(R.id.lstproductsinfo);
        connectionClass = new ConnectionClass();
        SharedPreferences sharedpreferences = getSharedPreferences(AnaSayfa.MyPREFERENCES, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedpreferences.edit();
        depoid = sharedpreferences.getString("AnaDepoId", null);
        Companiesid = sharedpreferences.getString("Companiesid", null);
        memberid = sharedpreferences.getString("ID", null);
        ilksecim = "1";
        spn.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                switch (position) {
                    case 0:
                        PorB = "P";

                        break;
                    case 1:
                        PorB = "B";

                        break;
                    default:
                        ;


                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {


            }

        });
        btnonayla.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DepoyaAktar();

            }
        });
        btnbarcodewrite.setOnClickListener(new View.OnClickListener() {
            @TargetApi(Build.VERSION_CODES.LOLLIPOP)
            @Override
            public void onClick(View v) {
                SevkiyatExist2 sevkiyatExist = new SevkiyatExist2();
                sevkiyatExist.execute();

            }
        });
        btnbarcoderead.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ilkgiris = 1;
                //finish();
                //Barcode okut aşağıda görünsün
                Intent i = new Intent(MalKabulOku.this, CodeReader.class);
                startActivityForResult(i, 1);
            }
        });


    }

    public void DepoyaAktar() {
        if (arraysize != 0) {
            AddPalet addPalet = new AddPalet();
            addPalet.execute("");
        } else
            Toast.makeText(MalKabulOku.this, "LİSTEDE ÜRÜN BULUNAMADI !", Toast.LENGTH_SHORT).show();

    }

    public boolean onCreateOptionsMenu(Menu menu) {

        this.menu = menu;
        getMenuInflater().inflate(R.menu.malkabul, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.anasayfa) {
            finish();
            Intent i = new Intent(MalKabulOku.this, SliderMenu.class);
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

        if (requestCode == 1) {
            if (resultCode == Activity.RESULT_OK) {
                codeid = data.getStringExtra("codeid");

                try {
                    uid2 = UUID.fromString(codeid);
                    PorB2 = "G";
                } catch (Exception ex) {
                    PorB2 = codeid.substring(0, 1);
                    codeidfake = codeid.substring(1, codeid.length());
                }
                SevkiyatExist sevkiyatExist = new SevkiyatExist();
                sevkiyatExist.execute("");

            }
            if (resultCode == Activity.RESULT_CANCELED) {
            }
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.CUPCAKE)
    public class SevkiyatExist extends AsyncTask<String, String, String> {
        String z = "";
        String query3 = "";

        @Override
        protected void onPreExecute() {
            pbbar.setVisibility(View.VISIBLE);
            isSuccess = false;


        }

        @TargetApi(Build.VERSION_CODES.LOLLIPOP)
        @Override
        protected void onPostExecute(String r) {
            pbbar.setVisibility(View.GONE);

            if (isSuccess) {
                Toast.makeText(getApplication(), "Sevkiyat Ürünüdür Eklenmez!", Toast.LENGTH_SHORT).show();

            } else {
                WareHouseExist wareHouseExist = new WareHouseExist();
                wareHouseExist.execute("");

            }


        }

        @Override
        protected String doInBackground(String... params) {

            try {
                Connection con = connectionClass.CONN();
                if (con == null) {
                    z = "Bağlantı Hatası";

                } else {
                    if (PorB2.equals("P")) {
                        query3 = "Select * from VW_SENTFORWADINGLIST where PALETBARCODES='" + codeidfake + "'";
                    } if (PorB2.equals("B")) {
                        query3 = "Select * from VW_SENTFORWADINGLIST where BARCODENO='" + codeidfake + "'" + " )";

                    }if (PorB2.equals("G")){
                        uid2 = UUID.fromString(codeid);
                        query3 = "Select * from VW_SENTFORWADINGLIST where BARCODEID='" + uid2 + "' or PALETID='" + uid2 + "'";
                    }
                    Statement stmt = con.createStatement();
                    ResultSet rs = stmt.executeQuery(query3);

                    if (rs.next()) {
                        z = "Giriş Başarılı";
                        isSuccess = true;
                    } else {
                        z = "Hata";
                        isSuccess = false;
                    }

                }
            } catch (Exception ex) {
                isSuccess = false;
                z = "Hata";
            }

            return z;
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.CUPCAKE)
    public class WareHouseExist extends AsyncTask<String, String, String> {
        String z = "";
        String query4 = "";

        @Override
        protected void onPreExecute() {
            pbbar.setVisibility(View.VISIBLE);
            isSuccess = false;

        }

        @TargetApi(Build.VERSION_CODES.LOLLIPOP)
        @Override
        protected void onPostExecute(String r) {
            pbbar.setVisibility(View.GONE);

            if (isSuccess) {
                way = true;
                ExistInOther existInOther = new ExistInOther();
                existInOther.execute();

            } else {
                FillList fillList = new FillList();
                String q = "";
                if (PorB2.equals("P")) {
                    q = "Select * from VW_PALETBARCODE where PALETBARCODES = '" + codeidfake + "'  and COMPANIESID='" + Companiesid + "'";
                    fillList.execute(q);
                }

                if (PorB2.equals("B")) {
                    q = "Select * from VW_PALETBARCODE where PRODUCTBARCODE = '" + codeidfake + "'  and COMPANIESID='" + Companiesid + "'";
                    fillList.execute(q);
                } if (PorB2.equals("G")) {
                    uid2 = UUID.fromString(codeid);
                    q = "Select * from VW_PALETBARCODE where (BARCODEID = '" + uid2 + "'  or PALETID='" + uid2 + "')  and COMPANIESID='" + Companiesid + "'";
                    fillList.execute(q);
                }
            }
        }

        @Override
        protected String doInBackground(String... params) {

            try {
                Connection con = connectionClass.CONN();
                if (con == null) {
                    z = "Bağlantı Hatası";

                } else {

                    if (PorB2.equals("P")) {
                        query4 = "Select * from VW_WAREHOUSEPRODUCT where PALETBARCODES='" + codeidfake + "'";
                    }
                    if (PorB2.equals("B")) {
                        query4 = "Select * from VW_WAREHOUSEPRODUCT where BARCODENO='" + codeidfake + "'";

                    }
                    if (PorB2.equals("G")) {
                        uid2 = UUID.fromString(codeid);
                        query4 = "Select * from VW_WAREHOUSEPRODUCT where BARCODEID='" + uid2 + "' or PALETID='" + uid2 + "'";
                    }
                    Statement stmt = con.createStatement();
                    ResultSet rs = stmt.executeQuery(query4);

                    if (rs.next()) {
                        z = "Giriş Başarılı";
                        isSuccess = true;
                    } else {
                        z = "Hata";
                        isSuccess = false;
                    }

                }
            } catch (Exception ex) {
                isSuccess = false;
                z = "Hata";
            }

            return z;
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.CUPCAKE)
    public class SevkiyatExist2 extends AsyncTask<String, String, String> {
        String z = "";

        @Override
        protected void onPreExecute() {
            pbbar.setVisibility(View.VISIBLE);
            isSuccess = false;


        }

        @TargetApi(Build.VERSION_CODES.LOLLIPOP)
        @Override
        protected void onPostExecute(String r) {
            pbbar.setVisibility(View.GONE);

            if (isSuccess) {
                Toast.makeText(getApplication(), "Sevkiyat Ürünüdür Eklenmez!", Toast.LENGTH_SHORT).show();

            } else {
                WareHouseExist2 wareHouseExist = new WareHouseExist2();
                wareHouseExist.execute("");
            }
        }

        @Override
        protected String doInBackground(String... params) {
            if (PorB.equals("P")) {
                try {
                    Connection con = connectionClass.CONN();
                    if (con == null) {
                        z = "Bağlantı Hatası";
                    } else {
                        String query3 = "Select * from VW_SENTFORWADINGLIST where PALETBARCODES='" + edtCode.getText().toString() + "' ";
                        Statement stmt = con.createStatement();
                        ResultSet rs = stmt.executeQuery(query3);
                        if (rs.next()) {
                            z = "Giriş Başarılı";
                            isSuccess = true;
                        } else {
                            z = "Hata";
                            isSuccess = false;
                        }

                    }
                } catch (Exception ex) {
                    isSuccess = false;
                    z = "Hata";
                }
            }

            if (PorB.equals("B")) {
                try {
                    Connection con = connectionClass.CONN();
                    if (con == null) {
                        z = "Bağlantı Hatası";
                    } else {
                        String query3 = "Select * from VW_SENTFORWADINGLIST where BARCODENO='" + edtCode.getText().toString() + "'";
                        Statement stmt = con.createStatement();
                        ResultSet rs = stmt.executeQuery(query3);
                        if (rs.next()) {
                            z = "Giriş Başarılı";
                            isSuccess = true;
                        } else {
                            z = "Hata";
                            isSuccess = false;
                        }
                    }
                } catch (Exception ex) {
                    isSuccess = false;
                    z = "Hata";
                }
            }
            return z;
        }


    }

    @RequiresApi(api = Build.VERSION_CODES.CUPCAKE)
    public class WareHouseExist2 extends AsyncTask<String, String, String> {
        String z = "";
        String query4 = "";

        @Override
        protected void onPreExecute() {
            pbbar.setVisibility(View.VISIBLE);
            isSuccess = false;


        }

        @TargetApi(Build.VERSION_CODES.LOLLIPOP)
        @Override
        protected void onPostExecute(String r) {
            pbbar.setVisibility(View.GONE);

            if (isSuccess) {
                way = false;
                ExistInOther existInOther = new ExistInOther();
                existInOther.execute();
            } else {
                FillList fillList = new FillList();
                String q = "";
                if (PorB.equals("P")) {
                    q = "Select * from VW_PALETBARCODE where PALETBARCODES = '" + edtCode.getText().toString() + "'  and COMPANIESID='" + Companiesid + "'";
                }
                if (PorB.equals("B")) {
                    q = "Select * from VW_PALETBARCODE where PRODUCTBARCODE = '" + edtCode.getText().toString() + "'  and COMPANIESID='" + Companiesid + "'";
                }
                fillList.execute(q);
            }
        }

        @Override
        protected String doInBackground(String... params) {
            if (PorB.equals("P")) {
                try {
                    Connection con = connectionClass.CONN();
                    if (con == null) {
                        z = "Bağlantı Hatası";

                    } else {
                        query4 = "Select * from VW_WAREHOUSEPRODUCT where PALETBARCODES='" + edtCode.getText().toString() + "'";
                        Statement stmt = con.createStatement();
                        ResultSet rs = stmt.executeQuery(query4);
                        if (rs.next()) {
                            z = "Giriş Başarılı";
                            isSuccess = true;
                        } else {
                            z = "Hata";
                            isSuccess = false;
                        }
                    }
                } catch (Exception ex) {
                    isSuccess = false;
                    z = "Hata";
                }
            }

            if (PorB.equals("B")) {
                try {
                    Connection con = connectionClass.CONN();
                    if (con == null) {
                        z = "Bağlantı Hatası";
                    } else {
                        query4 = "Select * from VW_WAREHOUSEPRODUCT where  BARCODENO='" + edtCode.getText().toString() + "'";
                        Statement stmt = con.createStatement();
                        ResultSet rs = stmt.executeQuery(query4);

                        if (rs.next()) {
                            z = "Giriş Başarılı";
                            isSuccess = true;
                        } else {
                            z = "Hata";
                            isSuccess = false;
                        }
                    }
                } catch (Exception ex) {
                    isSuccess = false;
                    z = "Hata";
                }
            }
            return z;
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.CUPCAKE)
    public class FindPlaetBarcodeID extends AsyncTask<String, String, String> {
        String z = "";
        String query5 = "";

        @Override
        protected void onPreExecute() {
            pbbar.setVisibility(View.VISIBLE);
            isSuccess = false;
            findP = "";
            findPArray.clear();

        }

        @TargetApi(Build.VERSION_CODES.LOLLIPOP)
        @Override
        protected void onPostExecute(String r) {
            pbbar.setVisibility(View.GONE);

            if (isSuccess) {
                AlertDialog.Builder builder2 = new AlertDialog.Builder(MalKabulOku.this);
                builder2.setTitle("UYARI!");
                builder2.setMessage("DEPOLARDA AYNI ÜRÜN BULUNDU  SİLİNSİN Mİ?");
                builder2.setNegativeButton("EVET", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        DeletePro deletePro = new DeletePro();
                        deletePro.execute("");
                    }
                });

                builder2.setPositiveButton("HAYIR", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {

                    }
                });

                builder2.show();

            } else {
                FillList fillList = new FillList();
                String q = null;
                try {
                    uid2 = UUID.fromString(codeid);
                    q = "Select * from VW_PALETBARCODE where (" + "(BARCODEID='" + codeid + "' " + "or PALETID='" + codeid + "' or PALETBARCODES='" + codeidfake + "' or " + "BARCODENO='" + codeidfake + "') and COMPANIESID='" + Companiesid + "'" + " )";
                } catch (Exception ex) {
                    if (PorB.equals("P")) {
                        q = "Select * from VW_PALETBARCODE where PALETBARCODES = '" + edtCode.getText().toString() + "'  and COMPANIESID='" + Companiesid + "'";
                    }
                    if (PorB.equals("B")) {
                        q = "Select * from VW_PALETBARCODE where PRODUCTBARCODE = '" + edtCode.getText().toString() + "'  and COMPANIESID='" + Companiesid + "'";
                    }
                }
                fillList.execute(q);
            }

        }

        @Override
        protected String doInBackground(String... params) {

            try {
                Connection con = connectionClass.CONN();
                if (con == null) {
                    z = "Bağlantı Hatası";

                } else {

                    if (!way) {
                        if (PorB.equals("P")) {
                            query5 = "Select * from VW_WAREHOUSEPRODUCT where WAREHOUSEID ='" + depoid + "' and PALETBARCODES='" + edtCode.getText().toString() + "'";
                        }
                        if (PorB.equals("B")) {
                            query5 = "Select * from VW_WAREHOUSEPRODUCT where WAREHOUSEID ='" + depoid + "' and BARCODENO='" + edtCode.getText().toString() + "'";
                        }
                    } else {
                        if (PorB2.equals("P")) {
                            query5 = "Select * from VW_WAREHOUSEPRODUCT where WAREHOUSEID ='" + depoid + "' and  PALETBARCODES='" + codeidfake + "'";
                        }

                        if (PorB2.equals("B")) {
                            query5 = "Select * from VW_WAREHOUSEPRODUCT where WAREHOUSEID ='" + depoid + "' and BARCODENO='" + codeidfake + "'";
                        }
                        if (PorB2.equals("G")) {
                            uid2 = UUID.fromString(codeid);
                            query5 = "Select * from VW_WAREHOUSEPRODUCT where WAREHOUSEID ='" + depoid + "' and " + "(BARCODEID='" + uid2 + "' " + "or PALETID='" + uid2 + "')";
                        }
                    }

                    Statement stmt = con.createStatement();
                    ResultSet rs = stmt.executeQuery(query5);

                    while (rs.next()) {
                        findPArray.add(rs.getString("BARCODEID"));
                        z = "Giriş Başarılı";
                        isSuccess = true;
                    }

                }
            } catch (Exception ex) {
                isSuccess = false;
                z = "Hata";
            }

            return z;
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.CUPCAKE)
    public class ExistInOther extends AsyncTask<String, String, String> {
        String query6 = "";
        String z = "";


        @TargetApi(Build.VERSION_CODES.LOLLIPOP)
        @Override
        protected void onPostExecute(String r) {
            pbbar.setVisibility(View.GONE);
            if (isSuccess) {
                Toast.makeText(getApplicationContext(), "Seçilen barkod başka bir depoda. " +
                        "Lütfen depolar arası transfar yapınız", Toast.LENGTH_LONG).show();

            } else {
                FindPlaetBarcodeID findPlaetBarcodeID = new FindPlaetBarcodeID();
                findPlaetBarcodeID.execute("");
            }

        }


        @Override
        protected String doInBackground(String... strings) {
            try {
                Connection con = connectionClass.CONN();
                if (con == null) {
                    z = "Bağlantı Hatası";

                } else {
                    if (!way) {
                        if (PorB.equals("P")) {
                            query6 = "Select * from VW_WAREHOUSEPRODUCT where WAREHOUSEID != '" + depoid + "' and PALETBARCODES='" + edtCode.getText().toString() + "'";
                        }
                        if (PorB.equals("B")) {
                            query6 = "Select * from VW_WAREHOUSEPRODUCT where WAREHOUSEID != '" + depoid + "' and BARCODENO='" + edtCode.getText().toString() + "'";
                        }
                    } else {
                        if (PorB2.equals("P")) {
                            query6 = "Select * from VW_WAREHOUSEPRODUCT where WAREHOUSEID != '" + depoid + "' and PALETBARCODES='" + codeidfake + "'";
                        }

                        if (PorB2.equals("B")) {
                            query6 = "Select * from VW_WAREHOUSEPRODUCT where WAREHOUSEID != '" + depoid + "' and BARCODENO='" + codeidfake + "'";
                        }
                        if (PorB2.equals("G")) {
                            uid2 = UUID.fromString(codeid);
                            query6 = "Select * from VW_WAREHOUSEPRODUCT where (BARCODEID = '" + uid2 + "'  or PALETID='" + uid2 + "')  and WAREHOUSEID != '" + depoid + "'";

                        }
                    }

                    Statement stmt = con.createStatement();
                    ResultSet rs = stmt.executeQuery(query6);

                    if (rs.next()) {
                        z = "Giriş Başarılı";
                        isSuccess = true;
                    } else {
                        z = "Hata";
                        isSuccess = false;
                    }
                }
            } catch (Exception ex) {
                isSuccess = false;
                z = "Hata";
            }

            return z;
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.CUPCAKE)
    public class AddPalet extends AsyncTask<String, String, String> {


        String z = "";
        Boolean isSuccess = false;


        @Override
        protected void onPreExecute() {
            pbbar.setVisibility(View.VISIBLE);
        }
        @SuppressLint("SetTextI18n")
        @Override
        protected void onPostExecute(String r) {
            pbbar.setVisibility(View.GONE);
            lstBarcode.setAdapter(null);

            if (isSuccess) {
                Toast.makeText(MalKabulOku.this, "AKTARILDI", Toast.LENGTH_SHORT).show();
                edtName.setText("TOPLAM ÜRÜN SAYISI: " + 0);
            }
            else Toast.makeText(MalKabulOku.this, "HATA OLUŞTU", Toast.LENGTH_SHORT).show();
        }

        @Override
        protected String doInBackground(String... params) {

            try {
                Connection con = connectionClass.CONN();
                if (con == null) {
                    z = "Bağlantı Hatası";
                } else {
                    for (int i = 0; i < arraysize; i++) {
                        Map<String, Object> map2 = (Map<String, Object>) lstBarcode.getItemAtPosition(i);
                        paletid = (String) map2.get("F");
                        paletsil = (String) map2.get("F");
                        barcodesil = (String) map2.get("E");
                        add = new AddPalet();
                        UUID uid = UUID.randomUUID();
                        String query = "insert into WAREHOUSEPRODUCT (ID,BARCODEID,PALETID,WAREHOUSEID," +
                                "MEMBERID,ISRETURN) " +
                                "values('" + uid + "','" + barcodesil + "','" + paletsil + "','" + depoid
                                + "','" + memberid + "','0')";
                        PreparedStatement preparedStatement = con.prepareStatement(query);
                        preparedStatement.executeUpdate();
                    }
                    z = "Palet Oluştu!";
                    isSuccess = true;

                }
            } catch (Exception ex) {
                isSuccess = false;
                ex.printStackTrace();
                z = "SQL Hatası!!";
            }
            return z;
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.CUPCAKE)
    public class FillList extends AsyncTask<String, String, String> {
        String z = "";
        Boolean isSuccess = false;
        List<Map<String, String>> prolist = new ArrayList<Map<String, String>>();

        @Override
        protected void onPreExecute() {

            arraysize = 0;
            pbbar.setVisibility(View.VISIBLE);
            FillType fillType = new FillType();
            fillType.execute("");
            first = 0;
            second = 0;
        }

        @Override
        protected void onPostExecute(String r) {

            if (arraysize <= 0) {
                Toast.makeText(MalKabulOku.this, "Ürün Kodu " + "Bulunamadı!", Toast.LENGTH_SHORT).show();
            }
            pbbar.setVisibility(View.GONE);
            String[] from = {"A", "B", "C", "D", "E", "F", "G"};
            int[] views = {R.id.procode, R.id.proname, R.id.proweight, R.id.proamount, R.id.BarcodeID, R.id.ProBarcode};
            edtName.setText("TOPLAM ÜRÜN SAYISI: " + arraysize);
            ADA = new SpecialAdapter(MalKabulOku.this, prolist, R.layout.listbarcode, from, views);
            lstBarcode.setAdapter(ADA);
            lstBarcode.setOnItemClickListener(new AdapterView.OnItemClickListener() {

                @Override
                public void onItemClick(AdapterView<?> arg0, View arg1,
                                        int arg2, long arg3) {
                    HashMap<String, Object> obj = (HashMap<String, Object>) ADA.getItem(arg2);
                    String code = (String) obj.get("A");
                    String name = (String) obj.get("B");
                    String amount = (String) obj.get("C");
                    String weight = (String) obj.get("D");
                    barcodeid = (String) obj.get("E");
                    paletid = (String) obj.get("F");
                    edtKod.setText(code);
                    edtName.setText(name);


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
                        datanum = new HashMap<String, String>();
                        datanum.put("A", rs.getString("PRODUCTCODE"));
                        datanum.put("B", rs.getString("PRODUCTNAME"));
                        first += Float.parseFloat(rs.getString("FIRSTUNITAMOUNT"));
                        ftype = rs.getString("FIRSTUNITNAME");
                        datanum.put("C", rs.getString("FIRSTUNITAMOUNT") + " " + rs.getString("FIRSTUNITNAME"));
                        second += Float.parseFloat(rs.getString("SECONDUNITAMOUNT"));
                        stype = rs.getString("SECONDUNITNAME");
                        datanum.put("D", rs.getString("SECONDUNITAMOUNT") + " " + rs.getString("SECONDUNITNAME"));
                        datanum.put("E", rs.getString("BARCODEID"));
                        datanum.put("F", rs.getString("PALETID"));
                        datanum.put("G", rs.getString("PRODUCTBARCODE"));
                        arraysize++;
                        sayma++;
                        prolist.add(datanum);
                        if (datanum.get("D").equals("0.00 null")) {
                            datanum.put("D", "");
                            isSuccess = true;
                            stype = "";
                        }


                    }


                    z = "Eklendi!";

                }
            } catch (Exception ex) {
                z = "Veri Çekme Hatası";

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
            pbbar.setVisibility(View.VISIBLE);
            silinenürün = 0;
        }

        @Override
        protected void onPostExecute(String r) {
            pbbar.setVisibility(View.GONE);
            if (isSuccess) {
                silinenürün++;
                edtName.setText("SİLİNEN ÜRÜN SAYISI: " + silinenürün);
            }
            lstBarcode.setAdapter(null);

        }

        @Override
        protected String doInBackground(String... params) {

            try {
                Connection con = connectionClass.CONN();
                if (con == null) {
                    z = "Error in connection with SQL server";
                } else {
                    for (int i = 0; i < findPArray.size() ; i++) {
                        String query = "Delete  from WAREHOUSEPRODUCT where  (BARCODEID='" + findPArray.get(i) + "')";
                        PreparedStatement preparedStatement = con.prepareStatement(query);
                        preparedStatement.executeUpdate();
                        z = "Başarıyla Silindi!";
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

    @RequiresApi(api = Build.VERSION_CODES.CUPCAKE)
    public class FillType extends AsyncTask<String, String, String> {
        String z = "";
        List<Map<String, String>> prolist = new ArrayList<Map<String, String>>();

        @Override
        protected void onPreExecute() {

            pbbar.setVisibility(View.VISIBLE);
        }

        @Override
        protected void onPostExecute(String r) {

            pbbar.setVisibility(View.GONE);
        }


        @Override
        protected String doInBackground(String... params) {

            try {
                Connection con = connectionClass.CONN();
                if (con == null) {
                    z = "Error in connection with SQL server";
                } else {
                    query = "Select FIRSTUNITNAME,SECONDUNITNAME from VW_BARCODE where ID='" + codeid + "'";
                    PreparedStatement ps = con.prepareStatement(query);
                    ResultSet rs = ps.executeQuery();


                    while (rs.next()) {

                        firstype = rs.getString("FIRSTUNITNAME");
                        secondtype = rs.getString("SECONDUNITNAME");

                    }
                    if (firstype.equals(null)) firstype = "";
                    if (secondtype.equals(null)) secondtype = "";


                    z = "Eklendi!";

                }
            } catch (Exception ex) {
                z = "Veri Çekme Hatası";

            }

            return z;
        }
    }

}
