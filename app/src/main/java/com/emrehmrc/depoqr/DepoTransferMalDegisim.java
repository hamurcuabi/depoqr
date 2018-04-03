package com.emrehmrc.depoqr;

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
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
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
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.UUID;


public class DepoTransferMalDegisim extends AppCompatActivity {


    public Boolean same;
    EditText edtKod, edtName, edtCode;
    ActionBar ab;
    Button btnbarcoderead, btnonayla, btnbarcodewrite;
    ProgressBar pbbar;
    String barcodeid;
    ListView lstBarcode;
    ConnectionClass connectionClass;
    SpecialAdapter ADA;
    FillList fillList;
    String codeid, paletid;
    String hedefdepoid, anadepoid;
    int arraysize;
    String query;
    String firstype, secondtype;
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
    AddPalet add;
    private Menu menu;
    Spinner spn;
    String filter;
    TextView txtana, txthedef;
    SharedPreferences sharedpreferences;


    @RequiresApi(api = Build.VERSION_CODES.CUPCAKE)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_depo_transfer_mal_degisim);


        arraysize = 0;
        connectionClass = new ConnectionClass();

        ab = getSupportActionBar();
        ab.setTitle("MAL KABUL");
        ab.setSubtitle("Etiket Okut");
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
        txtana = (TextView) findViewById(R.id.txtana);
        txthedef = (TextView) findViewById(R.id.txthedef);
        sharedpreferences = getSharedPreferences(AnaSayfa.MyPREFERENCES, Context.MODE_PRIVATE);
        txtana.setText(sharedpreferences.getString("AnaDepoName", null));
        txthedef.setText(sharedpreferences.getString("HedefDepoName", null));
        lstBarcode = (ListView) findViewById(R.id.lstproductsinfo);
        connectionClass = new ConnectionClass();
        sharedpreferences = getSharedPreferences(AnaSayfa.MyPREFERENCES, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedpreferences.edit();
        hedefdepoid = sharedpreferences.getString("HedefDepoId", null);
        anadepoid = sharedpreferences.getString("AnaDepoId", null);
        Companiesid = sharedpreferences.getString("Companiesid", null);
        memberid = sharedpreferences.getString("ID", null);
        ilksecim = "1";
        same = false;
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
                filter = "ORDER BY CODE";

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
                ilkgiris = 1;
                fillList = new FillList();

                if (PorB.equals("P")) {
                    query = "Select BARCODEID,BARCODENO,PRODUCTNAME,PALETID,PALETBARCODES,PRODUCTID,PRODUCTCODE, FIRSTUNITNAME,SECONDUNITNAME,\n" +
                            "SUM(WDIRECTION * FIRSTAMOUNT)  AS FIRSTAMOUNT, \n" +
                            "SUM(WDIRECTION * SECONDAMOUNT) AS SECONDAMOUNT \n" +
                            "from VW_WAREHOUSESTOCKMOVEMENT where\n" +
                            "(DESTINATIONWAREHOUSEID = '" + anadepoid + "' or SOURCEWAREHOUSEID = '" + anadepoid + "') and COMPANIESID ='" + Companiesid + "'\n" +
                            "and PALETBARCODES = '" + edtCode.getText() + "'\n" +
                            "group by BARCODEID,BARCODENO,PALETBARCODES,PRODUCTNAME,PALETID,PRODUCTID,PRODUCTCODE,FIRSTUNITNAME,SECONDUNITNAME\n" +
                            "having SUM(WDIRECTION * FIRSTAMOUNT) != 0 or \n" +
                            "SUM(WDIRECTION * SECONDAMOUNT) != 0";
                    fillList.execute(query);
                } else if ((PorB.equals("B"))) {
                    query = "Select BARCODEID,BARCODENO,PRODUCTNAME,PALETID,PALETBARCODES,PRODUCTID,PRODUCTCODE, FIRSTUNITNAME,SECONDUNITNAME,\n" +
                            "SUM(WDIRECTION * FIRSTAMOUNT)  AS FIRSTAMOUNT, \n" +
                            "SUM(WDIRECTION * SECONDAMOUNT) AS SECONDAMOUNT \n" +
                            "from VW_WAREHOUSESTOCKMOVEMENT where\n" +
                            "(DESTINATIONWAREHOUSEID = '" + anadepoid + "' or SOURCEWAREHOUSEID = '" + anadepoid + "') and COMPANIESID ='" + Companiesid + "'\n" +
                            "and BARCODENO = '" + edtCode.getText() + "'\n" +
                            "group by BARCODEID,BARCODENO,PALETBARCODES,PRODUCTNAME,PALETID,PRODUCTID,PRODUCTCODE,FIRSTUNITNAME,SECONDUNITNAME\n" +
                            "having SUM(WDIRECTION * FIRSTAMOUNT) != 0 or \n" +
                            "SUM(WDIRECTION * SECONDAMOUNT) != 0";
                    fillList.execute(query);

                } else {
                    Toast.makeText(DepoTransferMalDegisim.this, "ÜRÜN BULUNAMADI!", Toast.LENGTH_SHORT).show();
                }
            }
        });
        btnbarcoderead.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ilkgiris = 1;
                //finish();
                //Barcode okut aşağıda görünsün
                Intent i = new Intent(DepoTransferMalDegisim.this, CodeReader.class);
                startActivityForResult(i, 1);


            }
        });


    } // takt to the fillist

    public void DepoyaAktar() {
        if (arraysize != 0) {

            try {

                if (arraysize == 1) {
                    Map<String, Object> map1 = (Map<String, Object>) lstBarcode.getItemAtPosition(0);
                    paletid = (String) map1.get("F");
                    paletsil = (String) map1.get("F");
                    barcodesil = (String) map1.get("E");
                    isSame isSame = new isSame();
                    isSame.execute("");


                } else if (arraysize > 1) {
                    Map<String, Object> map2 = (Map<String, Object>) lstBarcode.getItemAtPosition(0);
                    paletid = (String) map2.get("F");
                    paletsil = (String) map2.get("F");
                    barcodesil = (String) map2.get("E");
                    isSameLoop isSameLoop = new isSameLoop();
                    isSameLoop.execute("");

                }

            } catch (Exception e) {
                e.printStackTrace();
                Toast.makeText(DepoTransferMalDegisim.this, "HATA OLUŞTU!", Toast.LENGTH_SHORT)
                        .show();
            }
        } else
            Toast.makeText(DepoTransferMalDegisim.this, "LİSTEDE ÜRÜN BULUNAMADI :(", Toast.LENGTH_SHORT)
                    .show();

        edtName.setText("TOPLAM ÜRÜN SAYISI: " + arraysize);

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
            Intent i = new Intent(DepoTransferMalDegisim.this, SliderMenu.class);
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

                fillList = new FillList();
                try {
                    uid2 = UUID.fromString(codeid);
                    PorB ="G";

                } catch (Exception ex) {
                    PorB = codeid.substring(0, 1);
                    codeidfake = codeid.substring(1, codeid.length());
                }
                if (PorB.equals("P")) {
                    query = "Select BARCODEID,BARCODENO,PRODUCTNAME,PALETID,PALETBARCODES,PRODUCTID,PRODUCTCODE, FIRSTUNITNAME,SECONDUNITNAME,\n" +
                            "SUM(WDIRECTION * FIRSTAMOUNT)  AS FIRSTAMOUNT, \n" +
                            "SUM(WDIRECTION * SECONDAMOUNT) AS SECONDAMOUNT \n" +
                            "from VW_WAREHOUSESTOCKMOVEMENT where\n" +
                            "(DESTINATIONWAREHOUSEID = '" + anadepoid + "' or SOURCEWAREHOUSEID = '" + anadepoid + "') and COMPANIESID ='" + Companiesid + "'\n" +
                            "and PALETBARCODES = '" + codeidfake + "'\n" +
                            "group by BARCODEID,BARCODENO,PALETBARCODES,PRODUCTNAME,PALETID,PRODUCTID,PRODUCTCODE,FIRSTUNITNAME,SECONDUNITNAME\n" +
                            "having SUM(WDIRECTION * FIRSTAMOUNT) != 0 or \n" +
                            "SUM(WDIRECTION * SECONDAMOUNT) != 0";
                } else if ((PorB.equals("B"))) {
                    query = "Select BARCODEID,BARCODENO,PRODUCTNAME,PALETID,PALETBARCODES,PRODUCTID,PRODUCTCODE, FIRSTUNITNAME,SECONDUNITNAME,\n" +
                            "SUM(WDIRECTION * FIRSTAMOUNT)  AS FIRSTAMOUNT, \n" +
                            "SUM(WDIRECTION * SECONDAMOUNT) AS SECONDAMOUNT \n" +
                            "from VW_WAREHOUSESTOCKMOVEMENT where\n" +
                            "(DESTINATIONWAREHOUSEID = '" + anadepoid + "' or SOURCEWAREHOUSEID = '" + anadepoid + "') and COMPANIESID ='" + Companiesid + "'\n" +
                            "and BARCODENO = '" + codeidfake + "'\n" +
                            "group by BARCODEID,BARCODENO,PALETBARCODES,PRODUCTNAME,PALETID,PRODUCTID,PRODUCTCODE,FIRSTUNITNAME,SECONDUNITNAME\n" +
                            "having SUM(WDIRECTION * FIRSTAMOUNT) != 0 or \n" +
                            "SUM(WDIRECTION * SECONDAMOUNT) != 0";
                } else {
                    query ="Select BARCODEID,BARCODENO,PRODUCTNAME,PALETID,PALETBARCODES,PRODUCTID,PRODUCTCODE, FIRSTUNITNAME,SECONDUNITNAME,\n" +
                        "SUM(WDIRECTION * FIRSTAMOUNT)  AS FIRSTAMOUNT, \n" +
                        "SUM(WDIRECTION * SECONDAMOUNT) AS SECONDAMOUNT \n" +
                        "from VW_WAREHOUSESTOCKMOVEMENT where\n" +
                        "(DESTINATIONWAREHOUSEID = '" + anadepoid + "' or SOURCEWAREHOUSEID = '" + anadepoid + "') and COMPANIESID ='" + Companiesid + "'\n" +
                        "and (PALETID='" + codeid + "' or BARCODEID='" + codeid + "')\n" +
                        "group by BARCODEID,BARCODENO,PALETBARCODES,PRODUCTNAME,PALETID,PRODUCTID,PRODUCTCODE,FIRSTUNITNAME,SECONDUNITNAME\n" +
                        "having SUM(WDIRECTION * FIRSTAMOUNT) != 0 or \n" +
                        "SUM(WDIRECTION * SECONDAMOUNT) != 0";
            }
                fillList.execute(query);


            }
            if (resultCode == Activity.RESULT_CANCELED) {
                //Write your code if there's no result
            }
        }
    }//onActivityResult

    @RequiresApi(api = Build.VERSION_CODES.CUPCAKE)
    public class AddPalet extends AsyncTask<String, String, String> {

        String z = "";
        Boolean isSuccess = false;

        @Override
        protected void onPostExecute(String r) {
          if(isSuccess.equals(true)){
              Toast.makeText(DepoTransferMalDegisim.this, "Tamamlandı", Toast.LENGTH_SHORT).show();
          }
          else  Toast.makeText(DepoTransferMalDegisim.this, "Hata Oluştu", Toast.LENGTH_SHORT).show();
        }

        @Override
        protected String doInBackground(String... params) {

            try {
                Connection con = connectionClass.CONN();
                if (con == null) {
                    z = "Bağlantı Hatası";
                } else {
                    PreparedStatement preparedStatement = con.prepareStatement(params[0]);
                    preparedStatement.executeUpdate();
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
        }

        @Override
        protected void onPostExecute(String r) {

            edtName.setText("TOPLAM ÜRÜN SAYISI: " + arraysize);
            if (!isSuccess)
                Toast.makeText(DepoTransferMalDegisim.this, "Ürün Kodu " + "Bulunamadı!", Toast.LENGTH_SHORT).show();
            pbbar.setVisibility(View.GONE);
            String[] from = {"A", "B", "C", "D", "E", "F", "G"};
            int[] views = {R.id.procode, R.id.proname, R.id.proweight, R.id.proamount, R.id.BarcodeID, R.id.ProBarcode};
            ADA = new SpecialAdapter(DepoTransferMalDegisim.this, prolist, R.layout.listbarcode, from, views);
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
                        datanum.put("C", rs.getString("FIRSTAMOUNT") + " " + rs.getString("FIRSTUNITNAME"));
                        datanum.put("D", rs.getString("SECONDAMOUNT") + " " + rs.getString("SECONDUNITNAME"));
                        datanum.put("E", rs.getString("BARCODEID"));
                        datanum.put("F", rs.getString("PALETID"));
                        datanum.put("G", rs.getString("PALETBARCODES"));
                        Float firstamountCheck = rs.getFloat("FIRSTAMOUNT");
                        if(firstamountCheck>0){
                            arraysize++;
                            prolist.add(datanum);
                            isSuccess = true;
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
    public class isSame extends AsyncTask<String, String, String> {
        String z = "";
        Boolean isSuccess = false;

        @Override
        protected void onPreExecute() {
            pbbar.setVisibility(View.VISIBLE);
        }

        @Override
        protected void onPostExecute(String r) {
            pbbar.setVisibility(View.GONE);
            if (isSuccess.equals(true)) same = true;
            else same = false;
                add = new AddPalet();
                UUID uid = UUID.randomUUID();
                String query2 = "insert into WAREHOUSETRANSFER" +
                        " values ('" + uid + "','" + memberid + "','" + barcodesil + "','" + paletsil + "','" + anadepoid + "','" + hedefdepoid + "',(Select SUM(WDIRECTION * FIRSTAMOUNT)  AS FIRSTAMOUNT from VW_WAREHOUSESTOCKMOVEMENT where (DESTINATIONWAREHOUSEID = '" + anadepoid + "' or SOURCEWAREHOUSEID = '" + anadepoid + "') and BARCODEID = '" + barcodesil + "' group by BARCODEID,BARCODENO,PALETBARCODES,PRODUCTNAME,PALETID,PRODUCTID,PRODUCTCODE,FIRSTUNITNAME,SECONDUNITNAME having SUM(WDIRECTION * FIRSTAMOUNT) != 0 or  SUM(WDIRECTION * SECONDAMOUNT) != 0),(Select SUM(WDIRECTION * SECONDAMOUNT)  AS SECONDAMOUNT from VW_WAREHOUSESTOCKMOVEMENT where (DESTINATIONWAREHOUSEID = '" + anadepoid + "' or SOURCEWAREHOUSEID = '" + anadepoid + "') and BARCODEID = '" + barcodesil + "' group by BARCODEID,BARCODENO,PALETBARCODES,PRODUCTNAME,PALETID,PRODUCTID,PRODUCTCODE,FIRSTUNITNAME,SECONDUNITNAME having SUM(WDIRECTION * FIRSTAMOUNT) != 0 or  SUM(WDIRECTION * SECONDAMOUNT) != 0),GETDATE())";
                add.execute(query2);
        }

        @Override
        protected String doInBackground(String... params) {
            try {
                Connection con = connectionClass.CONN();
                if (con == null) {
                    z = "Bağlantı Hatası";
                } else {
                    String query = "select * from WAREHOUSEPRODUCT where " + "BARCODEID='" + barcodesil + "' and " + "PALETID='" + paletsil + "' and WAREHOUSEID='" + hedefdepoid + "'" ;
                    Statement stmt = con.createStatement();
                    ResultSet rs = stmt.executeQuery(query);

                    if (rs.next()) {
                        rs.getString("PALETID");
                        z = "AYNI!";
                        same = true;
                        isSuccess = true;
                    } else {
                        isSuccess = false;
                        same = false;
                    }
                }
            } catch (Exception ex) {
                isSuccess = false;
                z = "SQL HATASI";
            }

            return z;
        }
    }  // bu sorgu yok edildi
    @RequiresApi(api = Build.VERSION_CODES.CUPCAKE)
    public class isSameLoop extends AsyncTask<String, String, String> {
        String z = "";
        Boolean isSuccess = false;

        @Override
        protected void onPreExecute() {
            pbbar.setVisibility(View.VISIBLE);
        }

        @Override
        protected void onPostExecute(String r) {
            pbbar.setVisibility(View.GONE);
            if (isSuccess.equals(true)) same = true;
            else same = false;
            if (same.equals(false)) {
                for (int i = 0; i < arraysize; i++) {
                    Map<String, Object> map4 = (Map<String, Object>) lstBarcode.getItemAtPosition(i);
                    paletid = (String) map4.get("F");
                    paletsil = (String) map4.get("F");
                    barcodesil = (String) map4.get("E");
                    AddPalet add2 = new AddPalet();
                    UUID uid = UUID.randomUUID();
                    String query = "insert into WAREHOUSETRANSFER" +
                            " values ('" + uid + "','" + memberid + "','" + barcodesil + "','" + paletsil + "','" + anadepoid + "','" + hedefdepoid + "',(Select SUM(WDIRECTION * FIRSTAMOUNT)  AS FIRSTAMOUNT from VW_WAREHOUSESTOCKMOVEMENT where (DESTINATIONWAREHOUSEID = '" + anadepoid + "' or SOURCEWAREHOUSEID = '" + anadepoid + "') and BARCODEID = '" + barcodesil + "' group by BARCODEID,BARCODENO,PALETBARCODES,PRODUCTNAME,PALETID,PRODUCTID,PRODUCTCODE,FIRSTUNITNAME,SECONDUNITNAME having SUM(WDIRECTION * FIRSTAMOUNT) != 0 or  SUM(WDIRECTION * SECONDAMOUNT) != 0),(Select SUM(WDIRECTION * SECONDAMOUNT)  AS SECONDAMOUNT from VW_WAREHOUSESTOCKMOVEMENT where (DESTINATIONWAREHOUSEID = '" + anadepoid + "' or SOURCEWAREHOUSEID = '" + anadepoid + "') and BARCODEID = '" + barcodesil + "' group by BARCODEID,BARCODENO,PALETBARCODES,PRODUCTNAME,PALETID,PRODUCTID,PRODUCTCODE,FIRSTUNITNAME,SECONDUNITNAME having SUM(WDIRECTION * FIRSTAMOUNT) != 0 or  SUM(WDIRECTION * SECONDAMOUNT) != 0),GETDATE())";
                    add2.execute(query);
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
                    String query = "select * from WAREHOUSEPRODUCT where PALETID='" + paletsil +
                            "' and WAREHOUSEID='" + hedefdepoid + "'";
                    Statement stmt = con.createStatement();
                    ResultSet rs = stmt.executeQuery(query);

                    if (rs.next()) {

                        rs.getString("PALETID");
                        z = "AYNI!";

                        same = true;
                        isSuccess = true;
                    } else {
                        isSuccess = false;
                        same = false;
                    }

                }
            } catch (Exception ex) {
                isSuccess = false;
                z = "SQL HATASI";
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
