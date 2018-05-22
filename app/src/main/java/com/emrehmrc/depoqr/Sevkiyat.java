package com.emrehmrc.depoqr;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.emrehmrc.depoqr.AnaSayfa.MyPREFERENCES;

public class Sevkiyat extends AppCompatActivity {

    Button bntsevkiyat, btnstart, tamamla,btnstart2;
    Spinner spndepo;
    ActionBar ab;
    ConnectionClass connectionClass;
    ListView lstProduct;
    SpecialAdapter ADA;
    SharedPreferences sharedpreferences;
    String memberid, fordid;
    Animation animation;
    boolean empty, depook = false;
    List<Map<String, String>> prolist = new ArrayList<Map<String, String>>();
    float firstamunt;
    String compId;
    AutoCompleteTextView sevkiyetNo;
    ImageView dropDown;
    TextView sevkiyetPlaka,sevkiyetCari;
    ArrayList<Depolar> depolars = new ArrayList<>();
    ArrayList<String> maxArray = new ArrayList<>();
     ProgressBar pbbar;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sevkiyat);
        sharedpreferences = getApplicationContext().getSharedPreferences(MyPREFERENCES, Context.MODE_PRIVATE);
        this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        memberid = sharedpreferences.getString("ID", null);
        compId = sharedpreferences.getString("Companiesid", null);
        animation = AnimationUtils.loadAnimation(this, R.anim.scale);
        ab = getSupportActionBar();
        ab.setTitle("SEVKİYAT");
        ab.setSubtitle("Sevkiyat İşlemleri");
        ab.setBackgroundDrawable(getResources().getDrawable(R.drawable.arkaplan));
        animation = AnimationUtils.loadAnimation(this, R.anim.rotatereverse);
        connectionClass = new ConnectionClass();
        bntsevkiyat = (Button) findViewById(R.id.btnsevkiyat);
        lstProduct = (ListView) findViewById(R.id.lstproductsinfo);
        tamamla = (Button) findViewById(R.id.btn_tamamla2);
        spndepo = (Spinner) findViewById(R.id.spnDepo);
        btnstart = (Button) findViewById(R.id.btnstart);
        btnstart2 = (Button) findViewById(R.id.btnstart2);
        sevkiyetPlaka = (TextView) findViewById(R.id.sevkiyetPlaka);
        sevkiyetCari = (TextView) findViewById(R.id.sevkiyetCari);
        sevkiyetNo = (AutoCompleteTextView) findViewById(R.id.edtsevkiyatno2);
        pbbar = (ProgressBar) findViewById(R.id.pbbar);
        dropDown = (ImageView) findViewById(R.id.btn_drop2);
        SevkiyetList sevkiyetList = new SevkiyetList();
        sevkiyetList.execute("");
        btnstart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (depook && !empty) {
                    Intent i = new Intent(Sevkiyat.this, SevkiyatSecondStep.class);
                    startActivityForResult(i, 1);
                } else {

                    Intent intent = new Intent(getBaseContext(), UyariBildirim.class);
                    intent.putExtra("UYARI", "SEVK EDİLECEK ÜRÜN BULUNAMDI!");
                    startActivity(intent);
                }


            }
        });
        btnstart2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (depook && !empty) {
                    Intent i = new Intent(Sevkiyat.this, SevkiyatTarih.class);
                    startActivity(i);
                } else {

                    Intent intent = new Intent(getBaseContext(), UyariBildirim.class);
                    intent.putExtra("UYARI", "SEVK EDİLECEK ÜRÜN BULUNAMDI!");
                    startActivity(intent);
                }


            }
        });
        bntsevkiyat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                prolist.clear();
                FillProducts fillProducts = new FillProducts();
                fillProducts.execute();

            }
        });

        tamamla.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (depolars.isEmpty()) {
                    if (sevkiyetNo.getText().toString().isEmpty()) {
                        Toast.makeText(getApplicationContext(), "Sevkiyat Seçiniz!", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    if (!maxArray.isEmpty()) {
                        String tempString = "";
                        for (int i = 0; i < maxArray.size(); i++) {
                            if (i == 0) {
                                tempString = tempString + maxArray.get(i);
                            } else tempString = tempString + ", " + maxArray.get(i);

                        }
                        Intent intent = new Intent(getBaseContext(), UyariBildirim.class);
                        intent.putExtra("UYARI", "BU Ürünlerin Miktari Aştınız: " + tempString);
                        startActivity(intent);
                    }
                    AlertDialog.Builder builder2 = new AlertDialog.Builder(Sevkiyat.this);
                    builder2.setTitle("UYARI!");
                    builder2.setMessage("Sevkiyatı tamamlamak istediğinizden emin misiniz?");
                    builder2.setNegativeButton("EVET", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            TamamlaC tamamlaC = new TamamlaC();
                            tamamlaC.execute("");
                        }
                    });

                    builder2.setPositiveButton("HAYIR", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {

                        }
                    });

                    builder2.show();

                }
            }
        });
        dropDown.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sevkiyetNo.showDropDown();
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == 1) {

          /*  denemeArray= data.getStringArrayListExtra("list");
            denemeArray.size(); */
        }

    }

    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.sevkiyat, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.geri) {
            finish();
            onBackPressed();

        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressLint("NewApi")
    public class FillDepo extends AsyncTask<String, String, String> {
        String z = "";


        @Override
        protected void onPreExecute() {
            spndepo.setAdapter(null);
            depolars.clear();
            pbbar.setVisibility(View.VISIBLE);
        }

        @Override
        protected void onPostExecute(String r) {
            pbbar.setVisibility(View.GONE);

            ArrayAdapter<Depolar> adapter = new ArrayAdapter<Depolar>(getApplicationContext(), R.layout.specialspinner, depolars);
            adapter.setDropDownViewResource(R.layout.support_simple_spinner_dropdown_item);
            spndepo.setAdapter(adapter);
            spndepo.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                    Depolar depolar = (Depolar) spndepo.getItemAtPosition(position);
                    ((TextView) parent.getChildAt(0)).setGravity(Gravity.CENTER);
                    SharedPreferences.Editor editor = sharedpreferences.edit();
                    editor.putString("SevkiyatDepoID", depolar.getDepono());
                    editor.putString("SevkiyatDepoAdi",depolar.getDepoadi());
                    editor.commit();
                    bntsevkiyat.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.yes, 0);
                    depook = true;

                }

                @Override
                public void onNothingSelected(AdapterView<?> parent) {
                    depook = false;
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

    @SuppressLint("NewApi")
    public class FillProducts extends AsyncTask<String, String, String> {
        String z = "";


        @Override
        protected void onPreExecute() {
            empty = true;
            pbbar.setVisibility(View.VISIBLE);

        }

        @Override
        protected void onPostExecute(String r) {
            pbbar.setVisibility(View.GONE);

            if (!empty) {
                SharedPreferences.Editor editor = sharedpreferences.edit();
                editor.putString("SevkiyatForwadingID", fordid);
                editor.putString("sevkNo", sevkiyetNo.getText().toString());
                editor.commit();
                FillDepo fillDepo = new FillDepo();
                fillDepo.execute();
                FillMiktar fillMiktar = new FillMiktar();
                fillMiktar.execute();
                FillPlaka fillPlaka = new FillPlaka();
                fillPlaka.execute("");
            } else {
                bntsevkiyat.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.action_search, 0);
                btnstart.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);
                Intent intent = new Intent(getBaseContext(), UyariBildirim.class);
                intent.putExtra("UYARI", "SEVKİYAT BULUNAMADI!");
                startActivity(intent);
            }
        }

        @Override
        protected String doInBackground(String... params) {
            sharedpreferences = getApplicationContext().getSharedPreferences(MyPREFERENCES, Context.MODE_PRIVATE);
            String companiesid = sharedpreferences.getString("Companiesid", null);
            String deneme = sevkiyetNo.getText().toString();
            try {
                Connection con = connectionClass.CONN();
                if (con == null) {
                    z = "Error in connection with SQL server";
                } else {
                    String query = "Select * from VW_FORWARDINGPRODUCTPLAN where ( " +
                            "FORWARDINGNO='" + sevkiyetNo.getText().toString() + "'and " +
                            "ISSAVE='TRUE' and ISDELETE ='FALSE' and COMPANIESID='" + companiesid + "' " + " )";

                    PreparedStatement ps = con.prepareStatement(query);
                    ResultSet rs = ps.executeQuery();

                    while (rs.next()) {
                        Map<String, String> datanum = new HashMap<String, String>();
                        datanum.put("A", rs.getString("PRODUCTCODE"));
                        datanum.put("B", rs.getString("PRODUCTNAME"));
                        datanum.put("C", rs.getString("FORWARDINGAMOUNT"));
                        datanum.put("D", rs.getString("PRODUCTID"));
                        fordid = rs.getString("FORWARDINGID");
                        prolist.add(datanum);
                        empty = false;
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
    public class FillMiktar extends AsyncTask<String, String, String> {
        String z = "";
        Map<String, String> datanum = new HashMap<String, String>();

        @Override
        protected void onPreExecute() {
            pbbar.setVisibility(View.VISIBLE);

        }

        @Override
        protected void onPostExecute(String r) {
            pbbar.setVisibility(View.GONE);

            FillMiktarType fillMiktarType = new FillMiktarType();
            fillMiktarType.execute("");

        }

        @Override
        protected String doInBackground(String... strings) {
            try {
                Connection con = connectionClass.CONN();
                if (con == null) {
                    z = "Error in connection with SQL server";
                } else {
                    for (int i = 0; i < prolist.size(); i++) {
                        firstamunt = 0;
                        datanum = prolist.get(i);
                        String val1 = datanum.get("D");
                        String query = "Select FIRSTUNITAMOUNT from SENTFORWARDING where FORWARDINGID = '" + fordid + "' and PRODUCTID = '" + val1 + "'";
                        PreparedStatement ps = con.prepareStatement(query);
                        ResultSet rs = ps.executeQuery();
                        while (rs.next()) {
                            firstamunt = firstamunt + (rs.getFloat("FIRSTUNITAMOUNT"));
                        }
                        datanum.put("E", String.valueOf(firstamunt));
                        z = "Başarılı";
                    }
                }
            } catch (Exception ex) {
                z = "Veri Çekme Hatası";
            }
            return z;
        }
    }

    public class FillMiktarType extends AsyncTask<String, String, String> {
        String z = "";
        Map<String, String> datanum = new HashMap<String, String>();

        @Override
        protected void onPreExecute() {
            pbbar.setVisibility(View.VISIBLE);

        }

        @Override
        protected void onPostExecute(String r) {
            pbbar.setVisibility(View.GONE);
            maxArray.clear();
            String[] from = {"A", "B", "C", "E", "T"};
            int[] views = {R.id.pcode, R.id.pname, R.id.pfirst, R.id.psecond, R.id.pType};
            ADA = new SpecialAdapter(Sevkiyat.this, prolist, R.layout.lstsevkiyat, from, views);
            lstProduct.setAdapter(ADA);
            lstProduct.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    HashMap<String, Object> obj = (HashMap<String, Object>) ADA.getItem(position);
                }
            });

            //Değişiklik yapılacak
            for (int i = 0; i < prolist.size(); i++) {

                Map<String, Object> map2 = (Map<String, Object>) lstProduct.getItemAtPosition(i);
                float c = Float.parseFloat((String) map2.get("C"));
                float e = Float.parseFloat((String) map2.get("E"));
                String maxName = (String) map2.get("B");
                if (e > c) {
                    // lstProduct.setBackgroundColor(0xFF00FF00);
                    //Toast.makeText(getApplicationContext(), "Miktari Aştınız!", Toast.LENGTH_LONG).show();
                    maxArray.add(maxName);
                }

            }
            if (!maxArray.isEmpty()) {
                String tempString = "";
                for (int i = 0; i < maxArray.size(); i++) {
                    if (i == 0) {
                        tempString = tempString + maxArray.get(i);
                    } else tempString = tempString + ", " + maxArray.get(i);

                }
                Toast.makeText(getApplicationContext(), "Miktari Aştınız!", Toast.LENGTH_LONG).show();
            }

        }

        @Override
        protected String doInBackground(String... strings) {
            try {
                Connection con = connectionClass.CONN();
                if (con == null) {
                    z = "Error in connection with SQL server";
                } else {
                    for (int i = 0; i < prolist.size(); i++) {
                        datanum = prolist.get(i);
                        String val1 = datanum.get("D");
                        String query = "select NAME from TYPEUNIT where ID = (select TYPEUNITID from PRODUCTTYPEUNIT where PRODUCTID = '" + val1 + "' and ISFIRST = 'TRUE' ) and COMPANIESID ='" + compId + "'";
                        PreparedStatement ps = con.prepareStatement(query);
                        ResultSet rs = ps.executeQuery();
                        while (rs.next()) {
                            datanum.put("T", rs.getString("NAME"));
                        }

                        z = "Başarılı";
                    }
                }
            } catch (Exception ex) {
                z = "Veri Çekme Hatası";
            }
            return z;
        }
    }

    @SuppressLint("NewApi")
    public class TamamlaC extends AsyncTask<String, String, String> {
        String z = "";

        @Override
        protected void onPreExecute() {
            pbbar.setVisibility(View.VISIBLE);

        }

        @Override
        protected void onPostExecute(String r) {
            pbbar.setVisibility(View.GONE);

            if (z.equals("Başarılı")) {
                TamamlaCS tamamlaCS = new TamamlaCS();
                tamamlaCS.execute("");
            }
            if (z.equals("update hatali")) {
                Toast.makeText(getApplicationContext(), "Hata!", Toast.LENGTH_SHORT).show();

            }

        }

        @Override
        protected String doInBackground(String... strings) {
            try {
                Connection con = connectionClass.CONN();
                if (con == null) {
                    z = "Error in connection with SQL server";
                } else {
                    String q = "update SENTFORWARDING set ISOKEY = '1' where FORWARDINGID ='" + fordid + "' and ISOKEY = '0' ";
                    PreparedStatement ps = con.prepareStatement(q);
                    ps.executeUpdate();
                    z = "Başarılı";
                }

            } catch (Exception ex) {
                z = "update hatali";
                ex.printStackTrace();
            }
            return z;
        }
    }

    @SuppressLint("NewApi")
    public class TamamlaCS extends AsyncTask<String, String, String> {
        String z = "";

        @Override
        protected void onPreExecute() {
            pbbar.setVisibility(View.VISIBLE);

        }

        @Override
        protected void onPostExecute(String r) {
            pbbar.setVisibility(View.GONE);

            if (z.equals("Başarılı")) {
                sevkiyetNo.setText("");
                Toast.makeText(getApplicationContext(), "Sevkiyat Tamamlandı!", Toast.LENGTH_SHORT).show();
                SevkiyetList sevkiyetList = new SevkiyetList();
                sevkiyetList.execute("");
            } else Toast.makeText(getApplicationContext(), "Hata!", Toast.LENGTH_SHORT).show();
        }

        @Override
        protected String doInBackground(String... strings) {
            try {
                Connection con = connectionClass.CONN();
                if (con == null) {
                    z = "Error in connection with SQL server";
                } else {
                    String q = "update VW_FORWARDINGPLAN set STATU = '1' where ID ='" + fordid + "' and COMPANIESID ='" + compId + "'";
                    PreparedStatement ps = con.prepareStatement(q);
                    ps.executeUpdate();
                    z = "Başarılı";
                }

            } catch (Exception ex) {
                z = "update hatali";
                ex.printStackTrace();

            }
            return z;
        }
    }

    public class SevkiyetList extends AsyncTask<String, String, String> {
        ArrayList<String> sevkiyetnoArray = new ArrayList<>();
        String z = "";

        @Override
        protected void onPreExecute() {
            pbbar.setVisibility(View.VISIBLE);

        }

        @Override
        protected void onPostExecute(String r) {
            pbbar.setVisibility(View.GONE);

            ArrayAdapter<String> adapter = new ArrayAdapter<String>(getApplicationContext(), android.R.layout.simple_dropdown_item_1line, sevkiyetnoArray);
            sevkiyetNo.setAdapter(adapter);
        }

        @Override
        protected String doInBackground(String... strings) {
            sharedpreferences = getApplicationContext().getSharedPreferences(MyPREFERENCES, Context.MODE_PRIVATE);
            String companiesid = sharedpreferences.getString("Companiesid", null);
            try {
                Connection con = connectionClass.CONN();
                if (con == null) {
                    z = "Error in connection with SQL server";
                } else {
                    String query = "SELECT FORWARDINGNO from VW_FORWARDINGPLAN where ISSAVE='TRUE' and STATU ='FALSE' and ISDELETE ='FALSE' and COMPANIESID='" + companiesid + "' order by FORWARDINGNO ";
                    PreparedStatement ps = con.prepareStatement(query);
                    ResultSet rs = ps.executeQuery();
                    while (rs.next()) {
                        sevkiyetnoArray.add(rs.getString("FORWARDINGNO"));
                    }
                    z = "Başarılı";
                }
            } catch (Exception ex) {
                z = "Veri Çekme Hatası";

            }
            return z;
        }
    }

    public class FillPlaka extends AsyncTask<String, String, String> {
        String z = "";
        String palaka,cari;

        @Override
        protected void onPreExecute() {
            pbbar.setVisibility(View.VISIBLE);

        }

        @Override
        protected void onPostExecute(String r) {
            pbbar.setVisibility(View.GONE);
            sevkiyetPlaka.setText("P: " + palaka);
            sevkiyetCari.setText("C: "+ cari);
        }

        @Override
        protected String doInBackground(String... strings) {
            sharedpreferences = getApplicationContext().getSharedPreferences(MyPREFERENCES, Context.MODE_PRIVATE);
            String companiesid = sharedpreferences.getString("Companiesid", null);
            try {
                Connection con = connectionClass.CONN();
                if (con == null) {
                    z = "Error in connection with SQL server";
                } else {
                    String query = "SELECT CURRENTNAME, PLAKA from VW_FORWARDINGPLAN where ISSAVE='TRUE' and ISDELETE ='FALSE' and COMPANIESID='" + companiesid + "' and ID = '" + fordid + "'";
                    PreparedStatement ps = con.prepareStatement(query);
                    ResultSet rs = ps.executeQuery();
                    while (rs.next()) {
                        palaka = rs.getString("PLAKA");
                        cari = rs.getString("CURRENTNAME");
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

