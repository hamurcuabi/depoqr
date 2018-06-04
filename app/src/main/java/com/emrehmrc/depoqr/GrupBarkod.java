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
import android.os.Vibrator;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.emrehmrc.depoqr.adapter.DependedBarcodesAdaptor;
import com.emrehmrc.depoqr.codereader.CodeReaderGrupBarcode;
import com.emrehmrc.depoqr.connection.ConnectionClass;
import com.emrehmrc.depoqr.model.DependedBarcodes;
import com.emrehmrc.depoqr.popup.UyariBildirim;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.UUID;

import static com.emrehmrc.depoqr.AnaSayfa.MyPREFERENCES;

public class GrupBarkod extends AppCompatActivity {
    ActionBar ab;
    ConnectionClass connectionClass;
    SharedPreferences sharedPreferences;
    Bundle bundle;
    Vibrator vibrator;
    String memberid, comid, codeid;
    Button btnQr, btnDevam;
    ImageView btn_ekle;
    String anabarkod;
    ImageView btnEkle;
    EditText edtKod;
    ProgressBar pbbar;
    ArrayList<Depo> depolar = new ArrayList<>();
    RecyclerView recyclerView;
    DependedBarcodesAdaptor adaptor;
    ArrayList<DependedBarcodes> datalist;
    DependedBarcodes dependedBarcodes;
    String selectedDepo = "";
    Spinner spnDepo;
    String secilenBarkod, sililinecekBarkod;
    ToneGenerator toneG;
    boolean idWay;
    String depoName,depoid;
    TextView tx_depoName;
    LinearLayout linearLayout;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_grupbarkod);
        this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        vibrator = (Vibrator) getSystemService(VIBRATOR_SERVICE);


        datalist = new ArrayList<DependedBarcodes>();


        recyclerView = (RecyclerView) findViewById(R.id.recyclerview);
 /*
        Bundle extras = getIntent().getExtras();
        anabarkod = extras.getString("anabarkod");
        */
        Intent i = getIntent();
        anabarkod = i.getStringExtra("anabarkod");
        depoName = i.getStringExtra("depoName");
        depoid = i.getStringExtra("depoid");



        pbbar = (ProgressBar) findViewById(R.id.pbarloading);
        edtKod = (EditText) findViewById(R.id.edtKodEnter);
        btn_ekle = (ImageView) findViewById(R.id.btnEkle);
        linearLayout = (LinearLayout) findViewById(R.id.linearLayout);
        btnQr = (Button) findViewById(R.id.btnQr);
        btnDevam = (Button) findViewById(R.id.btndevam);
        tx_depoName = (TextView) findViewById(R.id.tx_depoName);
        sharedPreferences = getApplicationContext().getSharedPreferences(MyPREFERENCES, Context.MODE_PRIVATE);
        memberid = sharedPreferences.getString("ID", null);
        comid = sharedPreferences.getString("Companiesid", null);
        connectionClass = new ConnectionClass();
        ab = getSupportActionBar();
        ab.setTitle("GRUP BARKOD");
        ab.setSubtitle("Barkod Okut");
        ab.setBackgroundDrawable(getResources().getDrawable(R.drawable.arkaplan));
        spnDepo = (Spinner) findViewById(R.id.spn_grup);
        if(!depoName.equals("ggwp")){
            linearLayout.setVisibility(View.VISIBLE);
            spnDepo.setVisibility(View.GONE);
            selectedDepo = depoid;
            tx_depoName.setText(depoName);
        }
else{
            final FillDepo filldepo = new FillDepo();
            filldepo.execute("");
        }


        toneG = new ToneGenerator(AudioManager.STREAM_ALARM, 100);


        btn_ekle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                secilenBarkod = edtKod.getText().toString();
                idWay = false;
                CheckExist checkExist = new CheckExist();
                checkExist.execute("");

            }
        });
        btnQr.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                Intent i = new Intent(GrupBarkod.this, CodeReaderGrupBarcode.class);
                startActivityForResult(i, 1);

            }
        });
        btnDevam.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                SendGrup sendGrup = new SendGrup();
                sendGrup.execute("");

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

            Intent i = new Intent(GrupBarkod.this, SliderMenu.class);
            bundle = ActivityOptions.makeSceneTransitionAnimation(GrupBarkod.this).toBundle();
            finish();
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
                idWay = true;
                codeid = data.getStringExtra("codeid");
                secilenBarkod = codeid;
                CheckExist checkExist = new CheckExist();
                checkExist.execute("");

            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();

    }

    @SuppressLint("NewApi")
    public class FillProducts extends AsyncTask<String, String, String> {
        String z = "";
        boolean isEmpty;

        @Override
        protected void onPreExecute() {

            isEmpty = true;
            pbbar.setVisibility(View.VISIBLE);
        }

        @Override
        protected void onPostExecute(String r) {
            pbbar.setVisibility(View.GONE);
            if (datalist.size() > 1) {
                for (int i = 0; i < datalist.size() - 1; i++) {

                    for (int j = i + 1; j < datalist.size(); j++) {
                        if (datalist.get(i).getCode().equals(datalist.get(j).getCode())) {
                            datalist.remove(j);
                        }


                    }
                }
            }


            if (!isEmpty) {
                toneG.startTone(ToneGenerator.TONE_CDMA_ALERT_CALL_GUARD, 200);
                adaptor = new DependedBarcodesAdaptor(getApplicationContext(), datalist);
                recyclerView.setAdapter(adaptor);
                LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getApplicationContext());
                linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
                recyclerView.setLayoutManager(linearLayoutManager);

            } else {

                toneG.startTone(ToneGenerator.TONE_CDMA_CALL_SIGNAL_ISDN_PING_RING, 200);
                Intent intent = new Intent(getBaseContext(), UyariBildirim.class);
                intent.putExtra("UYARI", "ÜRÜN BULUNAMDI!!");
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
                        dependedBarcodes = new DependedBarcodes();
                        dependedBarcodes.setCheck(true);
                        dependedBarcodes.setCode(rs.getString("BARCODEID"));
                        dependedBarcodes.setName(rs.getString("PRODUCTNAME"));
                        dependedBarcodes.setCodeNo(rs.getString("BARCODENO"));
                        dependedBarcodes.setFirstAmount(rs.getString("FIRSTUNITAMOUNT"));
                        dependedBarcodes.setFirstUnit(rs.getString("FIRSTUNITNAME"));
                        dependedBarcodes.setSecondAmount(rs.getString("SECONDUNITAMOUNT"));
                        dependedBarcodes.setSecondUnit(rs.getString("SECONDUNITNAME"));
                        dependedBarcodes.setProductCode(rs.getString("PRODUCTCODE"));
                        if (!datalist.contains(dependedBarcodes)) {
                            datalist.add(dependedBarcodes);
                        }
                        isEmpty = false;

                    }


                    z = "Başarılı";
                }
            } catch (Exception ex) {
                ex.printStackTrace();
                z = "Veri Çekme Hatası";

            }
            return z;
        }
    }

    @SuppressLint("NewApi")
    public class FillDepo extends AsyncTask<String, String, String> {
        String z = "";


        @Override
        protected void onPreExecute() {

            spnDepo.setAdapter(null);
            pbbar.setVisibility(View.VISIBLE);
        }

        @Override
        protected void onPostExecute(String r) {

            pbbar.setVisibility(View.GONE);
            ArrayAdapter<Depo> adapter = new ArrayAdapter<Depo>(getApplicationContext
                    (), R.layout.specialspinner, depolar);
            adapter.setDropDownViewResource(R.layout.support_simple_spinner_dropdown_item);
            spnDepo.setAdapter(adapter);

            spnDepo.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                    Depo depo = (Depo) spnDepo.getItemAtPosition(position);
                    ((TextView) parent.getChildAt(0)).setGravity(Gravity.CENTER);
                    ((TextView) parent.getChildAt(0)).setTextColor(getResources().getColor(R.color.white));
                    selectedDepo = depo.getDepoId();


                }

                @Override
                public void onNothingSelected(AdapterView<?> parent) {
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
                            "VW_WAREHOUSEPERMISSION where MEMBERID='" + memberid + "' and WAREHOUSEISDELETE = '0' and ISACTIVE='1' and " +
                            "ISSHOW='1'  and WAREHOUSEMENUID='" + 3 + "'  order by NAME";
                    PreparedStatement ps = con.prepareStatement(query);
                    ResultSet rs = ps.executeQuery();


                    while (rs.next()) {
                        depolar.add(new Depo(rs.getString("NAME"), rs.getString
                                ("WAREHOUSEID")));

                    }


                    z = "Başarılı";
                }
            } catch (Exception ex) {
                ex.printStackTrace();
                z = "Veri Çekme Hatası";

            }
            return z;
        }
    }

    @SuppressLint("NewApi")
    public class SendGrup extends AsyncTask<String, String, String> {
        String z = "";
        boolean hata;
        int aktarılan = 0;


        @Override
        protected void onPreExecute() {

            hata = false;
            pbbar.setVisibility(View.VISIBLE);

        }

        @Override
        protected void onPostExecute(String r) {
            pbbar.setVisibility(View.GONE);
            if (!hata) {
                Toast.makeText(getApplicationContext(), aktarılan + " ÜRÜN AKTARILDI", Toast
                        .LENGTH_SHORT).show();
                Intent i = new Intent(getApplicationContext(), GrupAnaBarkod.class);
                startActivity(i);
                finish();


            }


        }

        @Override
        protected String doInBackground(String... params) {
            try {
                Connection con = connectionClass.CONN();
                if (con == null) {
                    z = "Error in connection with SQL server";
                } else {

                    for (int i = 0; i < datalist.size(); i++) {

                        aktarılan++;
                        UUID uuıd = UUID.randomUUID();
                        String q = "Insert into GROUPBARCODE (MEMBERID,ID,PARENTID,CHILDID,WAREHOUSEID," +
                                "EXWAREHOUSEID,FIRSTAMOUNT,SECONDAMOUNT) values ('" + memberid + "','" + uuıd + "','" + anabarkod + "','" + datalist.get
                                (i).getCode()
                                + "','" + selectedDepo + "',(Select TOP 1 WAREHOUSEID from " +
                                "VW_WAREHOUSESTOCKMOVEMENT  where BARCODEID='" + datalist.get(i)
                                .getCode() + "'" +
                                "  group " +
                                "by " +
                                "BARCODEID,BARCODENO,WAREHOUSEID,DATE having SUM(WDIRECTION * FIRSTAMOUNT)" +
                                " !='0' or SUM(WDIRECTION * SECONDAMOUNT) != '0' order by DATE),'" + datalist.get(i).getFirstAmount() + "','" + datalist.get(i).getSecondAmount() + "')";


                        PreparedStatement preparedStatement = con.prepareStatement(q);
                        preparedStatement.executeUpdate();

                    }
                }


            } catch (Exception ex) {
                z = "Veri Çekme Hatası";
                aktarılan--;
                ex.printStackTrace();


            }
            return z;
        }
    }

    @SuppressLint("NewApi")
    public class CheckExist extends AsyncTask<String, String, String> {
        String z = "";
        boolean isEmpty;
        String query;

        @Override
        protected void onPreExecute() {
            isEmpty = true;
            pbbar.setVisibility(View.VISIBLE);

        }

        @Override
        protected void onPostExecute(String r) {
            pbbar.setVisibility(View.GONE);


            if (!isEmpty) {
                AlertDialog.Builder builder2 = new AlertDialog.Builder(GrupBarkod.this);
                builder2.setTitle("UYARI!");
                builder2.setMessage("GEUP BULUNDU, SİLİNSİN Mİ?");
                builder2.setNegativeButton("EVET", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        DeletePro deletePro = new DeletePro();
                        deletePro.execute("");
                    }
                });
                builder2.setPositiveButton("HAYIR, Eklemek istiyorum", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        if (idWay) {
                            FillProducts fillProducts = new FillProducts();
                            String query = "Select BARCODEID,BARCODENO,PRODUCTNAME,FIRSTUNITNAME," +
                                    "FIRSTUNITAMOUNT,SECONDUNITAMOUNT,SECONDUNITNAME,PRODUCTCODE from " +
                                    "VW_WAREHOUSESTOCKMOVEMENT where BARCODEID='" + secilenBarkod + "' and COMPANIESID = '" + comid + "' group by  BARCODENO," +
                                    "PRODUCTNAME,BARCODEID,FIRSTUNITNAME,FIRSTUNITAMOUNT,SECONDUNITAMOUNT," +
                                    "SECONDUNITNAME,PRODUCTCODE  having SUM(WDIRECTION * FIRSTAMOUNT) !='0' or SUM(WDIRECTION * SECONDAMOUNT) != '0'";
                            fillProducts.execute(query);
                        } else {
                            FillProducts fillProducts = new FillProducts();
                            String query = "Select BARCODEID,BARCODENO,PRODUCTNAME,FIRSTUNITNAME," +
                                    "FIRSTUNITAMOUNT,SECONDUNITAMOUNT,SECONDUNITNAME,PRODUCTCODE from " +
                                    "VW_WAREHOUSESTOCKMOVEMENT where BARCODENO='" + secilenBarkod + "' and COMPANIESID = '" + comid + "' " +
                                    "group by  " +
                                    "BARCODENO," +
                                    "PRODUCTNAME,BARCODEID,FIRSTUNITNAME,FIRSTUNITAMOUNT,SECONDUNITAMOUNT," +
                                    "SECONDUNITNAME,PRODUCTCODE  having SUM(WDIRECTION * FIRSTAMOUNT) !='0' or SUM(WDIRECTION * SECONDAMOUNT) != '0'";


                            fillProducts.execute(query);
                        }
                    }
                });
                builder2.show();

            } else {
                if (idWay) {
                    FillProducts fillProducts = new FillProducts();
                    String query = "Select BARCODEID,BARCODENO,PRODUCTNAME,FIRSTUNITNAME," +
                            "FIRSTUNITAMOUNT,SECONDUNITAMOUNT,SECONDUNITNAME,PRODUCTCODE from " +
                            "VW_WAREHOUSESTOCKMOVEMENT where BARCODEID='" + secilenBarkod + "' and COMPANIESID = '" + comid + "' group by  BARCODENO," +
                            "PRODUCTNAME,BARCODEID,FIRSTUNITNAME,FIRSTUNITAMOUNT,SECONDUNITAMOUNT," +
                            "SECONDUNITNAME,PRODUCTCODE  having SUM(WDIRECTION * FIRSTAMOUNT) !='0' or SUM(WDIRECTION * SECONDAMOUNT) != '0'";
                    fillProducts.execute(query);
                } else {
                    FillProducts fillProducts = new FillProducts();
                    String query = "Select BARCODEID,BARCODENO,PRODUCTNAME,FIRSTUNITNAME," +
                            "FIRSTUNITAMOUNT,SECONDUNITAMOUNT,SECONDUNITNAME,PRODUCTCODE from " +
                            "VW_WAREHOUSESTOCKMOVEMENT where BARCODENO='" + secilenBarkod + "' and COMPANIESID = '" + comid + "' " +
                            "group by  " +
                            "BARCODENO," +
                            "PRODUCTNAME,BARCODEID,FIRSTUNITNAME,FIRSTUNITAMOUNT,SECONDUNITAMOUNT," +
                            "SECONDUNITNAME,PRODUCTCODE  having SUM(WDIRECTION * FIRSTAMOUNT) !='0' or SUM(WDIRECTION * SECONDAMOUNT) != '0'";


                    fillProducts.execute(query);
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
                    if (idWay) {
                        query = "SELECT ID from GROUPBARCODE  where CHILDID = '" + secilenBarkod + "'  ";

                    } else {
                        query = "SELECT ID from GROUPBARCODE  where CHILDID IN(Select ID from VW_BARCODE where BARCODENO='" + secilenBarkod + "')  ";

                    }
                    PreparedStatement ps = con.prepareStatement(query);
                    ResultSet rs = ps.executeQuery();

                    while (rs.next()) {
                        sililinecekBarkod = rs.getString("ID");
                        isEmpty = false;

                    }
                    z = "Başarılı";
                }
            } catch (Exception ex) {
                ex.printStackTrace();
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
        }

        @Override
        protected void onPostExecute(String r) {
            pbbar.setVisibility(View.GONE);
            if (isSuccess) {
                datalist.clear();
                Toast.makeText(GrupBarkod.this, "BAŞARIYLA SİLİNDİ", Toast.LENGTH_SHORT).show();
            }
        }

        @Override
        protected String doInBackground(String... params) {

            try {
                Connection con = connectionClass.CONN();
                if (con == null) {
                    z = "Error in connection with SQL server";
                } else {

                    String query = "Delete  from GROUPBARCODE  where  ID='" + sililinecekBarkod + "' ";
                    PreparedStatement preparedStatement = con.prepareStatement(query);
                    preparedStatement.executeUpdate();
                    isSuccess = true;


                }
            } catch (Exception ex) {
                ex.printStackTrace();
                isSuccess = false;
                z = "SQL HATASI!";
            }

            return z;
        }

    }

    private class Gruplar {

        private String grubAdi;
        private String grubId;

        public Gruplar(String grubAdi) {
            this.grubAdi = grubAdi;
        }

        public Gruplar() {
        }

        public Gruplar(String grubAdi, String grubId) {
            this.grubAdi = grubAdi;
            this.grubId = grubId;
        }

        public String toString() {
            return grubAdi;
        }

        public String getGrubId() {
            return grubId;
        }

        public void setGrubId(String grubId) {
            this.grubId = grubId;
        }

        public String getGrubAdi() {
            return grubAdi;
        }

        public void setGrubAdi(String grubAdi) {
            this.grubAdi = grubAdi;
        }
    }

    public class Depo {

        String depoName;
        String depoId;

        public Depo(String depoName, String depoId) {
            this.depoName = depoName;
            this.depoId = depoId;
        }

        public Depo() {
        }

        public Depo(String depoName) {
            this.depoName = depoName;
        }

        public String toString() {
            return depoName;
        }

        public String getDepoName() {

            return depoName;
        }

        public void setDepoName(String depoName) {
            this.depoName = depoName;
        }

        public String getDepoId() {
            return depoId;
        }

        public void setDepoId(String depoId) {
            this.depoId = depoId;
        }
    }
}


