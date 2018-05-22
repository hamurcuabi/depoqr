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
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.Toast;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static com.emrehmrc.depoqr.AnaSayfa.MyPREFERENCES;

public class GrupAnaBarkod extends AppCompatActivity {


    EditText edtCode;
    ActionBar ab;
    Button btnbarcoderead, btnonayla, btnDevam;
    ProgressBar pbbar;
    String barcodeid, barcodeidlist = "", productnamelist, barcodenolist;
    ListView lstBarcode;
    ConnectionClass connectionClass;
    SpecialAdapter ADA;
    String codeid, anabarkod;
    String depoid;
    int arraysize;
    String query;
    String Companiesid, memberid;
    Map<String, String> datanum;
    Bundle bundle;
    ToneGenerator toneG;
    MalKabulOku.AddPalet add;
    Spinner spn;
    float first, second;
    Boolean isSuccess;
    Button btnRead;
    SharedPreferences sharedPreferences;
    RecyclerView recyclerView;
    DependedBarcodesAdaptor adaptor;
    ArrayList<DependedBarcodes> datalist;
    DependedBarcodes dependedBarcodes;
    ArrayList<String> findPArray = new ArrayList<>();
    List<Map<String, String>> prolist = new ArrayList<Map<String, String>>();
    String secilenBarkod;
String depoName;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_grup_ana_barkod);
        connectionClass = new ConnectionClass();
        ab = getSupportActionBar();
        ab.setTitle("GRUP  BARKOD");
        ab.setSubtitle("Ana Barkod Seçiniz");
        ab.setBackgroundDrawable(getResources().getDrawable(R.drawable.arkaplan));
        toneG = new ToneGenerator(AudioManager.STREAM_ALARM, 100);
        sharedPreferences = getApplicationContext().getSharedPreferences(MyPREFERENCES, Context.MODE_PRIVATE);
        memberid = sharedPreferences.getString("ID", null);
        Companiesid = sharedPreferences.getString("Companiesid", null);

        datalist = new ArrayList<DependedBarcodes>();
        recyclerView = (RecyclerView) findViewById(R.id.recyclerview);

        pbbar = (ProgressBar) findViewById(R.id.pbarloading);
        spn = (Spinner) findViewById(R.id.spnPB);
        edtCode = (EditText) findViewById(R.id.edtCode);
        edtCode.setOnTouchListener(new View.OnTouchListener() {

            @Override
            public boolean onTouch(View v, MotionEvent event) {

                v.setFocusable(true);
                v.setFocusableInTouchMode(true);
                return false;
            }
        });
        edtCode.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

                try {
                    ADA.getFilter().filter(edtCode.getText());


                } catch (Exception ex) {

                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        btnRead = (Button) findViewById(R.id.btnRead);
        btnDevam = (Button) findViewById(R.id.btndevam);
        btnbarcoderead = (Button) findViewById(R.id.btnbarcoderead);
        btnonayla = (Button) findViewById(R.id.btnonay);
        lstBarcode = (ListView) findViewById(R.id.lstproductsinfo);
        connectionClass = new ConnectionClass();

        btnbarcoderead.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //Barcode okut aşağıda görünsün
                Intent i = new Intent(GrupAnaBarkod.this, CodeReaderGrupBarcode.class);
                startActivityForResult(i, 1);
            }
        });

        btnDevam.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!datalist.isEmpty()) {
                    anabarkod = datalist.get(0).getCode();
                    if (!anabarkod.equals("")) {
                        Intent i = new Intent(getApplicationContext(), GrupBarkod.class);

                        i.putExtra("anabarkod", anabarkod);
                        if(findPArray.isEmpty()){
                            i.putExtra("depoName", "ggwp");
                            i.putExtra("depoid", "ggwp");
                        }else{
                            i.putExtra("depoName", depoName);
                            i.putExtra("depoid", depoid);
                        }


                        startActivity(i);
                    } else {

                    }

                } else {
                    Intent intent = new Intent(getBaseContext(), UyariBildirim.class);
                    intent.putExtra("UYARI", "ÜRÜN SEÇİLMEDİ!");
                    startActivity(intent);
                }


            }
        });
        btnRead.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (edtCode.getText().toString() != "") {
                    secilenBarkod = edtCode.getText().toString();
                    CheckExist checkExist = new CheckExist();
                    checkExist.execute("");

                 /*   FillAnaBarkod fillAnaBarkod = new FillAnaBarkod();
                    String query = "select * from VW_BARCODE where ISDELETE = '0' and COMPANIESID = '"+Companiesid+"' and BARCODENO = '"+edtCode.getText().toString()+"'";
                    fillAnaBarkod.execute(query); */
                }

            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (requestCode == 1) {
            if (resultCode == Activity.RESULT_OK) {
                codeid = data.getStringExtra("codeid");
                secilenBarkod = codeid;
                CheckExist checkExist = new CheckExist();
                checkExist.execute("");
             /*   FillAnaBarkod fillAnaBarkod = new FillAnaBarkod();
                String query = "select * from VW_BARCODE where ISDELETE = '0' and COMPANIESID = '"+Companiesid+"' and BARCODENO = '"+codeid+"'";
                fillAnaBarkod.execute(query); */
            }
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

            Intent i = new Intent(getApplicationContext(), SliderMenu.class);
            bundle = ActivityOptions.makeSceneTransitionAnimation(GrupAnaBarkod.this).toBundle();
            finish();
        } else if (id == R.id.geri) {
            finish();
            onBackPressed();

        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressLint("NewApi")
    public class FillAnaBarkod extends AsyncTask<String, String, String> {
        String z = "";
        boolean isEmpty;

        @Override
        protected void onPreExecute() {

            arraysize = 0;
            isEmpty = true;
            pbbar.setVisibility(View.VISIBLE);
            datalist.clear();


        }

        @Override
        protected void onPostExecute(String r) {
            pbbar.setVisibility(View.GONE);


            if (!isEmpty) {
                toneG.startTone(ToneGenerator.TONE_CDMA_ALERT_CALL_GUARD, 200);
                adaptor = new DependedBarcodesAdaptor(getApplicationContext(), datalist);
                recyclerView.setAdapter(adaptor);
                LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getApplicationContext());
                linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
                recyclerView.setLayoutManager(linearLayoutManager);


            } else {

                toneG.startTone(ToneGenerator.TONE_CDMA_CALL_SIGNAL_ISDN_PING_RING, 200);
                Intent intent = new Intent(getApplicationContext(), UyariBildirim.class);
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
                        arraysize++;
                        dependedBarcodes = new DependedBarcodes();
                        dependedBarcodes.setCheck(true);
                        dependedBarcodes.setCode(rs.getString("ID"));
                        dependedBarcodes.setName(rs.getString("PRODUCTNAME"));
                        dependedBarcodes.setCodeNo(rs.getString("BARCODENO"));
                        dependedBarcodes.setFirstAmount(rs.getString("FIRSTUNITAMOUNT"));
                        dependedBarcodes.setFirstUnit(rs.getString("FIRSTUNITNAME"));
                        dependedBarcodes.setSecondAmount(rs.getString("SECONDUNITAMOUNT"));
                        dependedBarcodes.setSecondUnit(rs.getString("SECONDUNITNAME"));
                        dependedBarcodes.setProductCode(rs.getString("PRODUCTCODE"));

                        datalist.add(dependedBarcodes);
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
    public class CheckExist extends AsyncTask<String, String, String> {
        String z = "";
        boolean isEmpty;

        @Override
        protected void onPreExecute() {
            isEmpty = true;
            pbbar.setVisibility(View.VISIBLE);
            findPArray.clear();
        }

        @Override
        protected void onPostExecute(String r) {
            pbbar.setVisibility(View.GONE);


            if (!isEmpty) {
                AlertDialog.Builder builder2 = new AlertDialog.Builder(GrupAnaBarkod.this);
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
                        FillAnaBarkod fillAnaBarkod = new FillAnaBarkod();
                        String query = "select * from VW_BARCODE where ISDELETE = '0' and COMPANIESID = '" + Companiesid + "' and BARCODENO = '" + edtCode.getText().toString() + "'";
                        fillAnaBarkod.execute(query);
                    }
                });
                builder2.show();

            } else {

                FillAnaBarkod fillAnaBarkod = new FillAnaBarkod();
                String query = "select * from VW_BARCODE where ISDELETE = '0' and COMPANIESID = '" + Companiesid + "' and BARCODENO = '" + edtCode.getText().toString() + "'";
                fillAnaBarkod.execute(query);
            }


        }

        @Override
        protected String doInBackground(String... params) {

            try {
                Connection con = connectionClass.CONN();
                if (con == null) {
                    z = "Error in connection with SQL server";
                } else {
                    String query = "SELECT * from VW_GROUPBARCODE  where PARENTID IN(Select ID from VW_BARCODE where BARCODENO='" + secilenBarkod + "')  ";
                    PreparedStatement ps = con.prepareStatement(query);
                    ResultSet rs = ps.executeQuery();

                    while (rs.next()) {
                        findPArray.add(rs.getString("ID"));
                        depoid = rs.getString("WAREHOUSEID");
                        depoName = rs.getString("PARENTWAREHOUSENAME");
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
                Toast.makeText(GrupAnaBarkod.this, "BAŞARIYLA SİLİNDİ", Toast.LENGTH_SHORT).show();
            }
        }

        @Override
        protected String doInBackground(String... params) {

            try {
                Connection con = connectionClass.CONN();
                if (con == null) {
                    z = "Error in connection with SQL server";
                } else {
                    for (int j = 0; j < findPArray.size(); j++) {
                        String query = "Delete  from GROUPBARCODE  where  ID='" + findPArray.get(j) + "' ";
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

}
