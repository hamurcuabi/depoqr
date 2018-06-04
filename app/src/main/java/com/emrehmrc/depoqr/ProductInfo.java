package com.emrehmrc.depoqr;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
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
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.emrehmrc.depoqr.adapter.SpecialAdapter;
import com.emrehmrc.depoqr.connection.ConnectionClass;
import com.emrehmrc.depoqr.popup.EtiketTercih;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.UUID;

public class ProductInfo extends AppCompatActivity {


    EditText edtMiktar, edtAgirlik, edtKod, edtName,edtAdet;
    TextView txtUrunAdi,txtAdet;
    ActionBar ab;
    Button btnadd, btnpalet, btndelete;
    ProgressBar pbbar;
    String edtM;
    String edtN;
    String edtK;
    String edtA;
    String ID, MID, CID;
    String barcodeid;

    ListView lstBarcode;
    ConnectionClass connectionClass;
    SpecialAdapter ADA;
    FillList fillList;
    ArrayList<String> myListsend;
    UUID idpalet;
    UUID idpro;
    String typeunitone = "", typeunitsecond = "";
    TextView edttypeunit;
    Spinner spinnertypes;
    boolean issecondtype = false;
    LinearLayout lnrsecondtypes;
    ArrayList<String> listPro;
    int MaxBarcodeNo;
    LinearLayout layout;
    @RequiresApi(api = Build.VERSION_CODES.CUPCAKE)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_productinfo);
        layout = (LinearLayout) findViewById(R.id.layout2);
        layout.setRotation(-90);
        onWindowFocusChanged(true);
        rotate(0);
        this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);//edit Focus

        connectionClass = new ConnectionClass();

        ab = getSupportActionBar();
        ab.setTitle("ÜRÜNLER");
        ab.setSubtitle("Etiket Oluşturma");
        ab.setBackgroundDrawable(getResources().getDrawable(R.drawable.arkaplan));

        listPro = new ArrayList<String>();

        edtAgirlik = (EditText) findViewById(R.id.edtAgirlikinfo);
        edtAgirlik.setOnTouchListener(new View.OnTouchListener() {

            @Override
            public boolean onTouch(View v, MotionEvent event) {

                v.setFocusable(true);
                v.setFocusableInTouchMode(true);
                return false;
            }
        });
        edtMiktar = (EditText) findViewById(R.id.edtMiktarinfo);
        edtMiktar.setOnTouchListener(new View.OnTouchListener() {

            @Override
            public boolean onTouch(View v, MotionEvent event) {

                v.setFocusable(true);
                v.setFocusableInTouchMode(true);
                return false;
            }
        });

        edtKod = (EditText) findViewById(R.id.edtKodinfo);
        edtName = (EditText) findViewById(R.id.edtNameinfo);
        edttypeunit = (TextView) findViewById(R.id.typeunit);



        btnadd = (Button) findViewById(R.id.btnadd);
        btnpalet = (Button) findViewById(R.id.btnpalet);
        btndelete = (Button) findViewById(R.id.btndeleteinfo);
        pbbar = (ProgressBar) findViewById(R.id.pbbar);
        pbbar.setVisibility(View.GONE);
        spinnertypes = (Spinner) findViewById(R.id.spinnertypes);


        lstBarcode = (ListView) findViewById(R.id.lstproductsinfo);

        lnrsecondtypes = (LinearLayout) findViewById(R.id.lnrsecondtype);

        SharedPreferences sharedpreferences = getSharedPreferences(AnaSayfa.MyPREFERENCES, Context.MODE_PRIVATE);
        // SharedPreferences.Editor editor = sharedpreferences.edit();
        MID = sharedpreferences.getString("ID", null);
        CID = sharedpreferences.getString("Companiesid", null);
        // CID=sharedpreferences.getString("Companiesid", null);

        //Tek ürün
        ArrayList<String> myList = (ArrayList<String>) getIntent().getSerializableExtra("mylist");
        edtName.setText(myList.get(1));
        edtKod.setText(myList.get(0));
        ID = myList.get(2);
        //Tek Ürün
        connectionClass = new ConnectionClass();
        fillList = new FillList();
        String query = "select * from VW_PALETBARCODE where PALETID='" + idpalet + "'";
        fillList.execute(query);
        myListsend = new ArrayList<String>();

        //paleti ekle
        AddPalet addPalet = new AddPalet();
        idpalet = UUID.randomUUID();
        query = "insert into PALET (ID,MEMBERID,COMPANIESID) values ('" + idpalet + "','" + MID +
                "','" + CID + "')";
        addPalet.execute(query);

        FillType filltype = new FillType();
        query = "Select TYPEUNITNAME from VW_PRODUCTTYPEUNIT where PRODUCTID='" + ID + "' and " +
                "ISFIRST='TRUE'";
        filltype.execute(query);

        FillSecondTypes fillSecondTypes = new FillSecondTypes();
        query = "Select TYPEUNITNAME from VW_PRODUCTTYPEUNIT where PRODUCTID='" + ID + "' and " +
                "ISFIRST='FALSE'";
        fillSecondTypes.execute(query);

        spinnertypes.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                edtAgirlik.setHint("ÜRÜN " + parent.getItemAtPosition(position).toString() + " " +
                        "GİRİNİZ...");
                typeunitsecond = parent.getItemAtPosition(position).toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {


                typeunitsecond = parent.getItemAtPosition(0).toString();
            }
        });

        btnpalet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                final AlertDialog.Builder builder = new AlertDialog.Builder(ProductInfo.this);
                // builder.setTitle("UYARI");
                builder.setMessage("PALET OLUŞTURMA TAMAMLANSIN MI?");
                builder.setNegativeButton("HAYIR", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {

                        //İptal butonuna basılınca yapılacaklar.Sadece kapanması isteniyorsa boş bırakılacak

                    }
                });

                builder.setPositiveButton("EVET", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {

                        Intent i = new Intent(ProductInfo.this, ProductsinPalet.class);
                        i.putExtra("paletid", String.valueOf(idpalet));
                        startActivity(i);
                    }
                });
                builder.show();
                CleanEditText();


            }
        });

        btndelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                /*
                edtM = edtMiktar.getText().toString();
                edtA = edtAgirlik.getText().toString();
                edtK = edtKod.getText().toString();
                edtN = edtName.getText().toString();*/
                DeletePro del = new DeletePro();
                String query = "Delete From PALETBARCODE where BARCODEID='" + idpro + "'";
                del.execute(query);
                DeletePro del2 = new DeletePro();
                String query2 = "Delete From BARCODE where ID='" + barcodeid + "'";
                del2.execute(query2);
                CleanEditText();


            }
        });

        btnadd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                edtM = edtMiktar.getText().toString();
                edtA = edtAgirlik.getText().toString();
                edtK = edtKod.getText().toString();
                edtN = edtName.getText().toString();

                if (edtM.trim().equals("")) {


                    Toast.makeText(ProductInfo.this, "Boşlukları Doldurunuz!", Toast.LENGTH_SHORT).show();
                } else {

                    if (edtA.trim().equals("") && lnrsecondtypes.getVisibility() != View.INVISIBLE)
                        Toast.makeText(ProductInfo.this, "Boşlukları Doldurunuz !", Toast
                                .LENGTH_SHORT).show();
                    else {
                        if (edtA.trim().equals("")) edtA = "0";


                            FindMaxBarcodeNo findMaxBarcodeNo = new FindMaxBarcodeNo();
                            findMaxBarcodeNo.execute("");
                            idpro = UUID.randomUUID();
                            AddPro add = new AddPro();
                            add.execute("");

                            listPro.add(0, edtN);
                            listPro.add(1, edtA);
                            listPro.add(2, edtM);
                            listPro.add(3, String.valueOf(idpro));
                            listPro.add(4, typeunitone);
                            listPro.add(5, typeunitsecond);

                            CleanEditText();
                            Intent i = new Intent(ProductInfo.this, ProductEtiket.class);
                            i.putExtra("mylist", listPro
                            );
                            startActivity(i);


                    }

                }
            }


        });


    }

    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.anasayfa) {
            finish();
            Intent i = new Intent(ProductInfo.this, SliderMenu.class);
            startActivity(i);

        } else if (id == R.id.geri) {
            finish();
            onBackPressed();

        } else if (id == R.id.etikettercih) {

            Intent i = new Intent(ProductInfo.this, EtiketTercih.class);
            startActivity(i);

        }

        return super.onOptionsItemSelected(item);
    }

    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.productsinfo, menu);
        return super.onCreateOptionsMenu(menu);
    }
    @Override
    public void onWindowFocusChanged(boolean hasFocus) {

        layout.setPivotX(layout.getWidth() / 2);
        layout.setPivotY(layout.getHeight());
    }

    public void rotate(int rot) {
        layout.animate()
                .setDuration(500)
                .rotation(rot);
    }
    @Override
    protected void onPostResume() {
        fillList = new FillList();
        String query = "select * from VW_PALETBARCODE where PALETID='" + idpalet + "'";
        fillList.execute(query);
        super.onPostResume();
    }

    @Override
    public void onBackPressed() {

        super.onBackPressed();
    }

    public void CleanEditText() {

        edtAgirlik.setText("");
        edtMiktar.setText("");
    }

    @RequiresApi(api = Build.VERSION_CODES.CUPCAKE)
    public class AddPalet extends AsyncTask<String, String, String> {


        String z = "";
        Boolean isSuccess = false;


        @Override
        protected void onPreExecute() {

        }

        @Override
        protected void onPostExecute(String r) {

            //  Toast.makeText(ProductInfo.this, r, Toast.LENGTH_SHORT).show();
            if (isSuccess == true) {


                FillList fill = new FillList();
                fill.execute("");
            }

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
        List<Map<String, String>> prolist = new ArrayList<Map<String, String>>();

        @Override
        protected void onPreExecute() {

            pbbar.setVisibility(View.VISIBLE);
        }

        @Override
        protected void onPostExecute(String r) {

            pbbar.setVisibility(View.GONE);
            String[] from = {"A", "B", "C", "D", "E"};
            int[] views = {R.id.procode, R.id.proname, R.id.proweight, R.id.proamount, R.id.BarcodeID};
            ADA = new SpecialAdapter(ProductInfo.this,
                    prolist, R.layout.listbarcode, from,
                    views);
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

                    edtKod.setText(code);
                    edtName.setText(name);
                    edtMiktar.setText(amount);
                    edtAgirlik.setText(weight);


                    // Intent i = new Intent(ProductInfo.this, ProductInfo.class);

                    myListsend.add(0, name);
                    myListsend.add(1, weight);
                    myListsend.add(2, amount);
                    myListsend.add(3, barcodeid);


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

                    // String query = "select * from VW_BARCODE where ISDELETE='False' ORDER BY DATE DESC ";
                    PreparedStatement ps = con.prepareStatement(params[0]);
                    ResultSet rs = ps.executeQuery();


                    while (rs.next()) {
                        Map<String, String> datanum = new HashMap<String, String>();
                        datanum.put("A", rs.getString("PRODUCTCODE"));
                        datanum.put("B", rs.getString("PRODUCTNAME"));
                        datanum.put("C", rs.getString("FIRSTUNITAMOUNT")+" "+typeunitone);
                        datanum.put("D", rs.getString("SECONDUNITAMOUNT")+" "+typeunitsecond);
                        datanum.put("E", rs.getString("ID"));
                        prolist.add(datanum);
                    }


                    z = "BARCODE GELDİ";
                }
            } catch (Exception ex) {
                z = "Veri Çekme Hatası";

            }

            return z;
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.CUPCAKE)
    public class FillType extends AsyncTask<String, String, String> {
        String z = "";

        @Override
        protected void onPreExecute() {

            pbbar.setVisibility(View.VISIBLE);
        }

        @Override
        protected void onPostExecute(String r) {

            pbbar.setVisibility(View.GONE);
            edttypeunit.setText(typeunitone);
            edtMiktar.setHint("ÜRÜN " + typeunitone + " GİRİNİZ...");

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

                        typeunitone = rs.getString("TYPEUNITNAME");
                    }


                    z = "Doğru";
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
        }

        @Override
        protected void onPostExecute(String r) {
            pbbar.setVisibility(View.GONE);
            //Toast.makeText(Products.this, r, Toast.LENGTH_SHORT).show();
            if (isSuccess == true) {
                FillList fillList = new FillList();
                fillList.execute("");
            }

        }

        @Override
        protected String doInBackground(String... params) {

            try {
                Connection con = connectionClass.CONN();
                if (con == null) {
                    z = "Error in connection with SQL server";
                } else {
                    isSuccess = true;
                    String dates = new SimpleDateFormat("MM/dd/yyyy", Locale.ENGLISH)
                            .format(Calendar.getInstance().getTime());


                    PreparedStatement preparedStatement = con.prepareStatement(params[0]);
                    preparedStatement.executeUpdate();
                    z = "Başarıyla Silindi!";
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

    @RequiresApi(api = Build.VERSION_CODES.CUPCAKE)
    public class UpdatePro extends AsyncTask<String, String, String> {


        String z = "";
        Boolean isSuccess = false;


        @Override
        protected void onPreExecute() {
            pbbar.setVisibility(View.VISIBLE);
        }

        @Override
        protected void onPostExecute(String r) {
            pbbar.setVisibility(View.GONE);
            // Toast.makeText(Products.this, r, Toast.LENGTH_SHORT).show();
            if (isSuccess == true) {
                FillList fillList = new FillList();
                fillList.execute("");
            }

        }

        @Override
        protected String doInBackground(String... params) {

            try {
                Connection con = connectionClass.CONN();
                if (con == null) {
                    z = "Error in connection with SQL server";
                } else {
                    isSuccess = true;
                    String dates = new SimpleDateFormat("MM/dd/yyyy", Locale.ENGLISH)
                            .format(Calendar.getInstance().getTime());


                    PreparedStatement preparedStatement = con.prepareStatement(params[0]);
                    preparedStatement.executeUpdate();
                    z = "Updated Successfully";

                    isSuccess = true;
                }
            } catch (Exception ex) {
                isSuccess = false;
                z = "Exceptions";
            }

            return z;
        }
    }

    public class FindMaxBarcodeNo extends AsyncTask<String, String, String> {
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

                    String query = "select MAX(BARCODENO) from BARCODE where " +
                            "COMPANIESID='" + CID + "' ";
                    PreparedStatement ps = con.prepareStatement(query);
                    ResultSet rs = ps.executeQuery();
                    try {
                        MaxBarcodeNo = rs.getInt("BARCODENO");
                    }
                    catch (Exception e){
                        MaxBarcodeNo=0;
                    }



                }
            } catch (Exception ex) {
                z = "Veri Çekme Hatası";

            }

            return z;
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.CUPCAKE)
    public class AddPro extends AsyncTask<String, String, String> {


        String z = "";
        Boolean isSuccess = false;


        @Override
        protected void onPreExecute() {

        }

        @Override
        protected void onPostExecute(String r) {

            Toast.makeText(ProductInfo.this, r, Toast.LENGTH_SHORT).show();
            if (isSuccess == true) {


                FillList fill = new FillList();
                fill.execute("");
            }

        }

        @Override
        protected String doInBackground(String... params) {

            try {
                Connection con = connectionClass.CONN();
                if (con == null) {
                    z = "Bağlantı Hatası";
                } else {

                    MaxBarcodeNo+=1;
                    String query = "insert into BARCODE (ID,PRODUCTID,MEMBERID,FIRSTUNITAMOUNT," +
                            "SECONDUNITAMOUNT,COMPANIESID,BARCODENO) values" +
                            " ('" + idpro + "','" + ID + "','" + MID + "','" + edtA + "','" +
                            edtM + "','" + CID + "','"+MaxBarcodeNo+"')";
                    PreparedStatement preparedStatement = con.prepareStatement(query);
                    preparedStatement.executeUpdate();
                    z = "Başarıyla Eklendi!";
                    isSuccess = true;
                    //ürünleri palete ekle
                    AddPalet addProtoPalet = new AddPalet();
                    UUID id2 = UUID.randomUUID();
                    String query2 = "insert into PALETBARCODE (ID,PALETID,BARCODEID) values ('" + id2 + "','" + idpalet + "','" + idpro + "')";
                    addProtoPalet.execute(query2);

                }

            } catch (Exception ex) {
                ex.printStackTrace();
                isSuccess = false;
                z = "SQL Hatası!!";
            }


            return z;
        }
    }

    public class FillSecondTypes extends AsyncTask<String, String, String> {
        String z = "";
        List<String> list = new ArrayList<String>();

        @Override
        protected void onPreExecute() {

            pbbar.setVisibility(View.VISIBLE);
        }

        @Override
        protected void onPostExecute(String r) {

            pbbar.setVisibility(View.GONE);

            if (issecondtype) {
                lnrsecondtypes.setVisibility(View.VISIBLE);
            }
            ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(ProductInfo.this,
                    android.R.layout.simple_spinner_item, list);
            dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            spinnertypes.setAdapter(dataAdapter);


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

                        list.add(rs.getString("TYPEUNITNAME"));
                        issecondtype = true;

                    }


                    z = "BARCODE GELDİ";
                }
            } catch (Exception ex) {
                z = "Veri Çekme Hatası";
                ex.printStackTrace();

            }

            return z;
        }
    }


}
