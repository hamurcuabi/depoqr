package com.emrehmrc.depoqr;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.ActivityOptions;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.emrehmrc.depoqr.adapter.DependedBarcodesAdaptor;
import com.emrehmrc.depoqr.adapter.SpecialAdapter;
import com.emrehmrc.depoqr.connection.ConnectionClass;
import com.emrehmrc.depoqr.model.DependedBarcodes;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.UUID;

import static com.emrehmrc.depoqr.AnaSayfa.MyPREFERENCES;

public class GrupBarkodSecondStep extends AppCompatActivity {

    Spinner spnDepo, spnAnaBarcode;
    Button btnDone;
    ActionBar ab;
    ArrayList<Depo> depolar = new ArrayList<>();
    ArrayList<AnaBarkod> anabarkods = new ArrayList<>();
    String selectedDepo, selectedAnaBarkod;
    ConnectionClass connectionClass;
    SharedPreferences sharedpreferences;
    String memberid, comid;
    String parentId;
    SpecialAdapter ADA;
    ListView lstpro;
    ProgressBar pbbar;

    RecyclerView recyclerView;
    DependedBarcodesAdaptor adaptor;
    ArrayList<DependedBarcodes> datalist;
    DependedBarcodes dependedBarcodes;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_grup_barkod_second_step);

        ab = getSupportActionBar();
        ab.setTitle("GRUP BARKOD");
        ab.setSubtitle("Grup Barkod Aktarım");
        ab.setBackgroundDrawable(getResources().getDrawable(R.drawable.arkaplan));


        connectionClass = new ConnectionClass();

        btnDone = (Button) findViewById(R.id.btndevam);
        spnDepo = (Spinner) findViewById(R.id.spn_depo);

        pbbar = (ProgressBar) findViewById(R.id.pbarloading);
        btnDone = (Button) findViewById(R.id.btndevam);


        sharedpreferences = getApplicationContext().getSharedPreferences(MyPREFERENCES, Context.MODE_PRIVATE);
        memberid = sharedpreferences.getString("ID", null);
        comid = sharedpreferences.getString("Companiesid", null);

        pbbar.setVisibility(View.GONE);
        recyclerView = (RecyclerView) findViewById(R.id.recyclerview);

        Bundle bundle = getIntent().getExtras();
        datalist = (ArrayList<DependedBarcodes>) bundle.getSerializable("MyClass");
        adaptor = new DependedBarcodesAdaptor(getApplicationContext(), datalist);
        recyclerView.setAdapter(adaptor);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getApplicationContext());
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(linearLayoutManager);

        final FillDepo fillDepo = new FillDepo();
        fillDepo.execute("");
        if (datalist.size() > 0) {
            btnDone.setEnabled(true);
        }


        btnDone.setOnClickListener(new View.OnClickListener() {
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

            Intent i = new Intent(GrupBarkodSecondStep.this, SliderMenu.class);
            Bundle bundle = ActivityOptions.makeSceneTransitionAnimation(GrupBarkodSecondStep.this)
                    .toBundle();
            finish();
        } else if (id == R.id.geri) {
            finish();
            onBackPressed();

        }

        return super.onOptionsItemSelected(item);
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
                Intent i= new Intent(getApplicationContext(),GrupAnaBarkod.class);
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
                        parentId= datalist.get(0).getCodeNo();
                        aktarılan++;
                        UUID uuıd = UUID.randomUUID();
                        String q = "Insert into GROUPBARCODE (ID,PARENTID,CHILDID,WAREHOUSEID," +
                                "EXWAREHOUSEID) values ('" + uuıd + "','" + parentId + "','" + datalist.get
                                (i).getCode()
                                + "','" + selectedDepo + "',(Select WAREHOUSEID from " +
                                "VW_WAREHOUSESTOCKMOVEMENT  where BARCODEID='" + datalist.get(i)
                                .getCode() + "'" +
                                "  group " +
                                "by " +
                                "BARCODEID,BARCODENO,WAREHOUSEID having SUM(WDIRECTION * FIRSTAMOUNT)" +
                                " !='0' or SUM(WDIRECTION * SECONDAMOUNT) != '0'))";



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
    public class FillDepo extends AsyncTask<String, String, String> {
        String z = "";


        @Override
        protected void onPreExecute() {
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

    public class AnaBarkod {

        String anaBarkod;
        String anaBarkodID;

        public AnaBarkod(String anaBarkod, String anaBarkodID) {
            this.anaBarkod = anaBarkod;
            this.anaBarkodID = anaBarkodID;
        }

        public AnaBarkod(String anaBarkod) {
            this.anaBarkod = anaBarkod;
        }

        public String getAnaBarkodID() {

            return anaBarkodID;
        }

        public void setAnaBarkodID(String anaBarkodID) {
            this.anaBarkodID = anaBarkodID;
        }

        public String getAnaBarkod() {
            return anaBarkod;
        }

        public void setAnaBarkod(String anaBarkod) {
            this.anaBarkod = anaBarkod;
        }
    }

}
