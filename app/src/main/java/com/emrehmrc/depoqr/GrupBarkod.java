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
import android.os.Bundle;
import android.os.Vibrator;
import android.support.annotation.Nullable;
import android.support.v7.app.ActionBar;
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
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;

import static com.emrehmrc.depoqr.AnaSayfa.MyPREFERENCES;

/**
 * Created by cenah on 3/26/2018.
 */

public class GrupBarkod extends AppCompatActivity {
    ActionBar ab;
    ConnectionClass connectionClass;
    SharedPreferences sharedPreferences;
    Bundle bundle;
    Vibrator vibrator;
    String memberid, comid, incomingReader;
    EditText tx_bagli;
    AutoCompleteTextView tx_anabarkod;
    Button btn_koduara, btn_qrAna, btn_qrBagli, btn_tamamla;
    ImageView btn_ekle;
    ListView lst_grup;
    ArrayList<String> BarkodArray = new ArrayList<>();
    ArrayList<GrupBarkod.Depolar> depolars = new ArrayList<>();
    String anabarkod, secilendepoId;
    Spinner spndepo;

    RecyclerView recyclerView;
    DependedBarcodesAdaptor adaptor;
    ArrayList<DependedBarcodes> datalist;
    DependedBarcodes dependedBarcodes;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_grupbarkod);
        this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        vibrator = (Vibrator) getSystemService(VIBRATOR_SERVICE);

        datalist = new ArrayList<DependedBarcodes>();
        recyclerView = (RecyclerView) findViewById(R.id.recyclerview);
        adaptor = new DependedBarcodesAdaptor(getApplicationContext(), datalist);
        recyclerView.setAdapter(adaptor);


        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getApplicationContext());
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(linearLayoutManager);


        sharedPreferences = getApplicationContext().getSharedPreferences(MyPREFERENCES, Context.MODE_PRIVATE);
        memberid = sharedPreferences.getString("ID", null);
        comid = sharedPreferences.getString("Companiesid", null);
        connectionClass = new ConnectionClass();
        ab = getSupportActionBar();
        ab.setTitle("Grup Barkod");
        ab.setBackgroundDrawable(getResources().getDrawable(R.drawable.arkaplan));
        tx_anabarkod = (AutoCompleteTextView) findViewById(R.id.tx_anabarkod);
        btn_koduara = (Button) findViewById(R.id.btn_kodara);
        spndepo = (Spinner) findViewById(R.id.spn_depo);
        FillBarkod fillBarkod = new FillBarkod();
        fillBarkod.execute("");


        btn_koduara.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                anabarkod = tx_anabarkod.getText().toString();
                if (anabarkod.isEmpty()) {
                    Toast.makeText(getApplicationContext(), "Barkod Giriniz..", Toast.LENGTH_SHORT).show();
                } else {
                    if (BarkodArray.contains(anabarkod)) {
                        CheckifExist checkifExist = new CheckifExist();
                        checkifExist.execute("");
                    } else
                        Toast.makeText(getApplicationContext(), "Barkod Bulunamadı..", Toast.LENGTH_SHORT).show();

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
                incomingReader = data.getStringExtra("codeid");
            }
        }
    }

    public class FillBarkod extends AsyncTask<String, String, String> {
        String z = "";

        @Override
        protected void onPostExecute(String r) {

            ArrayAdapter<String> adapter = new ArrayAdapter<String>(getApplicationContext(), android.R.layout.simple_dropdown_item_1line, BarkodArray);
            tx_anabarkod.setAdapter(adapter);
        }

        @Override
        protected String doInBackground(String... strings) {
            try {
                Connection con = connectionClass.CONN();
                if (con == null) {
                    z = "Error in connection with SQL server";
                } else {
                    String query = "Select BARCODEID,BARCODENO from VW_WAREHOUSESTOCKMOVEMENT where COMPANIESID='" + comid + "' group by BARCODEID,BARCODENO having SUM(WDIRECTION * FIRSTAMOUNT) != 0 or SUM(WDIRECTION * SECONDAMOUNT) != 0\n";
                    PreparedStatement ps = con.prepareStatement(query);
                    ResultSet rs = ps.executeQuery();
                    while (rs.next()) {
                        BarkodArray.add(rs.getString("BARCODENO"));
                    }
                    z = "Başarılı";
                }
            } catch (Exception ex) {
                z = "Veri Çekme Hatası";

            }
            return z;
        }
    }

    public class CheckifExist extends AsyncTask<String, String, String> {

        String z = "";
        Boolean test = true;

        @Override
        protected void onPostExecute(String r) {
            if (test) {
                Toast.makeText(getApplicationContext(), "Ana Barkod Boş..", Toast.LENGTH_SHORT).show();
                FillDepo fillDepo = new FillDepo();
                fillDepo.execute("");
            } else {

            }
        }

        @Override
        protected String doInBackground(String... strings) {
            try {
                Connection con = connectionClass.CONN();
                if (con == null) {
                    z = "Error in connection with SQL server";
                } else {
                    String query = "Select * from GROUPBARCODE where PARENTID = '" + anabarkod + "'";
                    PreparedStatement ps = con.prepareStatement(query);
                    ResultSet rs = ps.executeQuery();
                    while (rs.next()) {
                        test = false;

                       dependedBarcodes=new DependedBarcodes();

                       dependedBarcodes.setCheck(true);
                        dependedBarcodes.setName(rs.getString("PRODUCTNAME"));



                    }
                }
                z = "Başarılı";

            } catch (Exception ex) {
                test = true;
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

        }

        @Override
        protected void onPostExecute(String r) {

            ArrayAdapter<GrupBarkod.Depolar> adapter = new ArrayAdapter<GrupBarkod.Depolar>(getApplicationContext(), R.layout.support_simple_spinner_dropdown_item, depolars);
            adapter.setDropDownViewResource(R.layout.support_simple_spinner_dropdown_item);
            spndepo.setAdapter(adapter);
            spndepo.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                    ((TextView) parent.getChildAt(0)).setGravity(Gravity.CENTER);
                    ((TextView) parent.getChildAt(0)).setTextColor(getResources().getColor(R.color.white));
                    GrupBarkod.Depolar depolar1;
                    depolar1 = (GrupBarkod.Depolar) spndepo.getItemAtPosition(position);
                    secilendepoId = depolar1.getDepono();


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
                        depolars.add(new GrupBarkod.Depolar(rs.getString("NAME"), rs.getString("WAREHOUSEID")));

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


}


