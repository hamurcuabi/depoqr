package com.emrehmrc.depoqr;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.ActivityOptions;
import android.content.Intent;
import android.media.AudioManager;
import android.media.ToneGenerator;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
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
import android.widget.Toast;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class GrupAnaBarkod extends AppCompatActivity {


    EditText edtKod, edtCode;
    ActionBar ab;
    Button btnbarcoderead, btnonayla, btnDevam;
    ProgressBar pbbar;
    String barcodeid,barcodeidlist="",productnamelist,barcodenolist;
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


    ArrayList<String> findPArray = new ArrayList<>();
    List<Map<String, String>> prolist = new ArrayList<Map<String, String>>();

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

        lstBarcode = (ListView) findViewById(R.id.lstproductsinfo);

        pbbar = (ProgressBar) findViewById(R.id.pbarloading);
        spn = (Spinner) findViewById(R.id.spnPB);
        edtKod = (EditText) findViewById(R.id.edtKodinfo);
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


        btnDevam = (Button) findViewById(R.id.btndevam);
        btnbarcoderead = (Button) findViewById(R.id.btnbarcoderead);
        btnonayla = (Button) findViewById(R.id.btnonay);
        lstBarcode = (ListView) findViewById(R.id.lstproductsinfo);
        connectionClass = new ConnectionClass();
        FillAnaBarkod fillAnaBarkod = new FillAnaBarkod();
        String query = "Select BARCODEID,BARCODENO,PRODUCTNAME from VW_WAREHOUSESTOCKMOVEMENT   group by BARCODEID,BARCODENO,PRODUCTNAME having SUM(WDIRECTION * FIRSTAMOUNT) !='0' or SUM(WDIRECTION * SECONDAMOUNT) != '0'";
        fillAnaBarkod.execute(query);

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

                if (!barcodeidlist.equals("")) {
                    Intent i = new Intent(getApplicationContext(), GrupBarkod.class);
                    i.putExtra("anabarkod", barcodeidlist);
                    startActivity(i);
                } else {

                    Intent intent = new Intent(getBaseContext(), UyariBildirim.class);
                    intent.putExtra("UYARI", "LİSTEDE ÜRÜN BULUNAMADI!");
                    startActivity(intent);
                }


            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (requestCode == 1) {
            if (resultCode == Activity.RESULT_OK) {
                codeid = data.getStringExtra("codeid");
                try {
                    ADA.getFilter().filter(codeid);

                } catch (Exception ex) {

                }

                /*
                FillAnaBarkod fillAnaBarkod = new FillAnaBarkod();
                String query = "SELECT distinct BARCODEID,PRODUCTNAME,BARCODENO from " +
                        "VW_WAREHOUSESTOCKMOVEMENT where BARCODEID='" + codeid + "'";
                fillAnaBarkod.execute(query);
                */
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


        }

        @Override
        protected void onPostExecute(String r) {
            pbbar.setVisibility(View.GONE);


            if (!isEmpty) {
                toneG.startTone(ToneGenerator.TONE_CDMA_ALERT_CALL_GUARD, 200);
                String[] from = {"A", "B", "C"};
                int[] views = {R.id.procode, R.id.proname, R.id.proweight};

                ADA = new SpecialAdapter(GrupAnaBarkod.this, prolist, R.layout.grupbarkodlist, from, views);
                lstBarcode.setAdapter(ADA);
                lstBarcode.setOnItemClickListener(new AdapterView.OnItemClickListener() {

                    @Override
                    public void onItemClick(AdapterView<?> arg0, View arg1,
                                            int arg2, long arg3) {
                        HashMap<String, Object> obj = (HashMap<String, Object>) ADA.getItem(arg2);
                         barcodeidlist   = (String) obj.get("A");
                         productnamelist  = (String) obj.get("B");
                         barcodenolist   = (String) obj.get("C");

                        Intent intent = new Intent(GrupAnaBarkod.this, UyariBildirim.class);
                        intent.putExtra("UYARI", productnamelist+" - "+barcodenolist);
                        startActivity(intent);



                    }
                });


            } else {

                toneG.startTone(ToneGenerator.TONE_CDMA_CALL_SIGNAL_ISDN_PING_RING, 200);
                Intent intent = new Intent(getBaseContext(), UyariBildirim.class);
                intent.putExtra("UYARI", "ANA GRUP BULUNAMADI!");
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
                        datanum = new HashMap<String, String>();
                        datanum.put("A", rs.getString("BARCODEID"));
                        datanum.put("B", rs.getString("PRODUCTNAME"));
                        datanum.put("C", rs.getString("BARCODENO"));
                        prolist.add(datanum);
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

}
