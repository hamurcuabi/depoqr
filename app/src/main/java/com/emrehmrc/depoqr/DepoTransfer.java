package com.emrehmrc.depoqr;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.Layout;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
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

public class DepoTransfer extends AppCompatActivity {

    ActionBar ab;
    ConnectionClass connectionClass;
    ListView lstdepoana,lstdepohedef;
    SpecialAdapter ADA,ADA2;
    SharedPreferences sharedpreferences;
    ProgressBar pbbar;
    String depoid,memberid;
    TextView hedefdepoadi,anadepoadi;
    Button btnok,btnok2;
    boolean anadepook=false,hedefdepook=false;
    EditText edtanadepo,edthedef;
    LinearLayout layout;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_depo_transfer);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);

        sharedpreferences =getApplicationContext().getSharedPreferences(MyPREFERENCES, Context.MODE_PRIVATE);
        ab = getSupportActionBar();
        ab.setTitle("DEPOLAR ARASI TRANSFER");
        ab.setSubtitle("DEPO LİSTESİ");
        ab.setBackgroundDrawable(getResources().getDrawable(R.drawable.arkaplan));

        connectionClass = new ConnectionClass();

        hedefdepoadi=(TextView)findViewById(R.id.hedefdepoadi);
        anadepoadi=(TextView)findViewById(R.id.anadepoadi);
        btnok2=(Button)findViewById(R.id.btnok2);
        btnok=(Button)findViewById(R.id.btnok);
        layout=(LinearLayout) findViewById(R.id.layoutdepo);
        pbbar = (ProgressBar) findViewById(R.id.pbbar);
        pbbar.setVisibility(View.GONE);
        lstdepoana = (ListView) findViewById(R.id.lstdepoana);
        lstdepohedef = (ListView) findViewById(R.id.lstdepohedef);

        edtanadepo=(EditText)findViewById(R.id.edtana) ;
        edtanadepo.setOnTouchListener(new View.OnTouchListener() {

            @Override
            public boolean onTouch(View v, MotionEvent event) {

                v.setFocusable(true);
                v.setFocusableInTouchMode(true);
                return false;
            }
        });
        edtanadepo.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

                try {
                    ADA.getFilter().filter(edtanadepo.getText());


                } catch (Exception ex) {

                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        edthedef=(EditText)findViewById(R.id.edthedef) ;
        edthedef.setOnTouchListener(new View.OnTouchListener() {

            @Override
            public boolean onTouch(View v, MotionEvent event) {

                v.setFocusable(true);
                v.setFocusableInTouchMode(true);
                return false;
            }
        });
        edthedef.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

                try {
                    ADA2.getFilter().filter(edthedef.getText());


                } catch (Exception ex) {

                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        layout.setOnTouchListener(new View.OnTouchListener()
        {
            @Override
            public boolean onTouch(View view, MotionEvent ev)
            {
                hideKeyboard(view);
                return false;
            }
        });

        memberid = sharedpreferences.getString("ID", null);
        FillListAnaDepo fillList= new FillListAnaDepo();
        String query = "SELECT distinct WAREHOUSEID , NAME FROM " +
                "VW_WAREHOUSEPERMISSION where MEMBERID='"+memberid+"' and WAREHOUSEISDELETE = '0' and ISACTIVE='1' and " +
                "ISSHOW='1'  and WAREHOUSEMENUID='"+5+"'  order by NAME";
        fillList.execute(query);

        btnok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(anadepook==true && hedefdepook==true) {
                    Intent i = new Intent(DepoTransfer.this, DepoTransferMalDegisim.class);
                    startActivity(i);
                }
                else Toast.makeText(DepoTransfer.this,"DEPO SEÇİMİNİ YAPINIZ",Toast.LENGTH_SHORT).show();
            }
        });
        btnok2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(anadepook==true && hedefdepook==true) {
                    Intent i = new Intent(DepoTransfer.this, DepoTransferUrun.class);
                    startActivity(i);
                }
                else Toast.makeText(DepoTransfer.this,"DEPO SEÇİMİNİ YAPINIZ",Toast.LENGTH_SHORT).show();
            }
        });


    }
    protected void hideKeyboard(View view)
    {
        InputMethodManager in = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        in.hideSoftInputFromWindow(view.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
    }
    public class FillListAnaDepo extends AsyncTask<String, String, String> {
        String z = "";
        Boolean isSuccess = false;
        List<Map<String, String>> prolist = new ArrayList<Map<String, String>>();

        @Override
        protected void onPreExecute() {

            pbbar.setVisibility(View.VISIBLE);
        }

        @Override
        protected void onPostExecute(String r) {

            pbbar.setVisibility(View.GONE);

            if (isSuccess) {


            }
            String[] from = {"A", "B"};
            int[] views = {R.id.depoid, R.id.deponame};
            ADA = new SpecialAdapter(DepoTransfer.this,
                    prolist, R.layout.depolist, from,
                    views);

            lstdepoana.setAdapter(ADA);

            lstdepoana.setOnItemClickListener(new AdapterView.OnItemClickListener() {


                @Override
                public void onItemClick(AdapterView<?> arg0, View arg1,
                                        int arg2, long arg3) {
                    HashMap<String, Object> obj = (HashMap<String, Object>) ADA
                            .getItem(arg2);
                     depoid = (String) obj.get("A");
                   // String companiesid = (String) obj.get("B");
                    String deponame = (String) obj.get("B");


                    SharedPreferences.Editor editor = sharedpreferences.edit();
                    editor.putString("AnaDepoId", depoid);
                    editor.putString("AnaDepoName", deponame);
                    editor.commit();
                    anadepoadi.setText(deponame);
                    hedefdepoadi.setText("HEDEF DEPO SEÇİNİZ");
                    FillListHedefDepo fillList= new FillListHedefDepo();
                    String query = "SELECT distinct WAREHOUSEID , NAME FROM " +
                            "VW_WAREHOUSEPERMISSION where  MEMBERID='"+memberid+"' and " +
                            "ISACTIVE='1' and WAREHOUSEISDELETE = '0' and " +
                            "ISSHOW='1'  and WAREHOUSEMENUID='"+5+"' and not " +
                            "WAREHOUSEID='"+depoid+"'  order by " +
                            "NAME";
                 //   String query = "select * from WAREHOUSE where not ID='"+depoid+"' ";
                    fillList.execute(query);
                    anadepook=true;




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
                        Map<String, String> datanum = new HashMap<String, String>();
                        datanum.put("A", rs.getString("WAREHOUSEID"));
                        datanum.put("B", rs.getString("NAME"));
                     //   datanum.put("C", rs.getString("NAME"));
                        prolist.add(datanum);
                        isSuccess = true;

                    }


                    z = "Başarılı";
                }
            } catch (Exception ex) {
                z = "Veri Çekme Hatası";

            }
            return z;
        }
    }
    public class FillListHedefDepo extends AsyncTask<String, String, String> {
        String z = "";
        Boolean issucces=false;
        List<Map<String, String>> prolist = new ArrayList<Map<String, String>>();

        @Override
        protected void onPreExecute() {

            pbbar.setVisibility(View.VISIBLE);
        }

        @Override
        protected void onPostExecute(String r) {

            pbbar.setVisibility(View.GONE);
            if(issucces){



            }
            String[] from = {"A", "B"};
            int[] views = {R.id.depoid, R.id.deponame};
            ADA2 = new SpecialAdapter(DepoTransfer.this,
                    prolist, R.layout.depolist, from,
                    views);

            lstdepohedef.setAdapter(ADA2);

            lstdepohedef.setOnItemClickListener(new AdapterView.OnItemClickListener() {


                @Override
                public void onItemClick(AdapterView<?> arg0, View arg1,
                                        int arg2, long arg3) {
                    HashMap<String, Object> obj = (HashMap<String, Object>) ADA2
                            .getItem(arg2);
                    String hedefdepoid = (String) obj.get("A");
                   // String companiesid = (String) obj.get("B");
                    String deponame = (String) obj.get("B");

                    hedefdepoadi.setText(deponame);
                    SharedPreferences.Editor editor = sharedpreferences.edit();
                    editor.putString("HedefDepoId", hedefdepoid);
                    editor.putString("HedefDepoName", deponame);
                    editor.commit();
                    hedefdepook=true;




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
                        Map<String, String> datanum = new HashMap<String, String>();
                        datanum.put("A", rs.getString("WAREHOUSEID"));
                        datanum.put("B", rs.getString("NAME"));
                        //datanum.put("C", rs.getString("NAME"));
                        issucces=true;
                        prolist.add(datanum);

                    }


                    z = "Başarılı";
                }
            } catch (Exception ex) {
                z = "Veri Çekme Hatası";

            }
            return z;
        }
    }
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.anasayfa) {
            finish();
            Intent i= new Intent(DepoTransfer.this,SliderMenu.class);
            startActivity(i);

        }
        else if (id == R.id.geri) {
            finish();
            onBackPressed();

        }

        return super.onOptionsItemSelected(item);
    }
    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.diger, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }
}
