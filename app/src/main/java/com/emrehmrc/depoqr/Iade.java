package com.emrehmrc.depoqr;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.ActivityOptions;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Build;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.ProgressBar;

import com.emrehmrc.depoqr.adapter.SpecialAdapter;
import com.emrehmrc.depoqr.connection.ConnectionClass;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.emrehmrc.depoqr.AnaSayfa.MyPREFERENCES;

public class Iade extends AppCompatActivity {

    ProgressBar pbbar;
    ActionBar ab;
    ConnectionClass connectionClass;
    ListView lstDepo;
    SpecialAdapter ADA;
    SharedPreferences sharedpreferences;
    Bundle bundle;
    String memberid;
    EditText edtsearch;

    CheckBox ctv;
    Animation animation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_iade);

        sharedpreferences = getApplicationContext().getSharedPreferences(MyPREFERENCES, Context.MODE_PRIVATE);
        memberid = sharedpreferences.getString("ID", null);

        animation = AnimationUtils.loadAnimation(Iade.this, R.anim.scale);
        ab = getSupportActionBar();
        ab.setTitle("IADE");
        ab.setSubtitle("DEPO LİSTESİ");
        ab.setBackgroundDrawable(getResources().getDrawable(R.drawable.arkaplan));

        connectionClass = new ConnectionClass();
        pbbar = (ProgressBar) findViewById(R.id.pbarloading);
        pbbar.setVisibility(View.GONE);

        lstDepo = (ListView) findViewById(R.id.lstdepo);
        edtsearch = (EditText) findViewById(R.id.edtDepoSearch);
        edtsearch.setOnTouchListener(new View.OnTouchListener() {

            @Override
            public boolean onTouch(View v, MotionEvent event) {

                v.setFocusable(true);
                v.setFocusableInTouchMode(true);
                return false;
            }
        });
        edtsearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

                try {
                    ADA.getFilter().filter(edtsearch.getText());


                } catch (Exception ex) {

                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        FillList filldepo = new FillList();
        filldepo.execute("");


    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.anasayfa) {

            Intent i = new Intent(Iade.this, SliderMenu.class);
            bundle = ActivityOptions.makeSceneTransitionAnimation(Iade.this)
                    .toBundle();
            finish();
        } else if (id == R.id.geri) {
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

    @SuppressLint("NewApi")
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
            String[] from = {"A", "B"};
            int[] views = {R.id.depoid, R.id.deponame};
            ADA = new SpecialAdapter(Iade.this,
                    prolist, R.layout.depolist, from,
                    views);

            lstDepo.setAdapter(ADA);


            lstDepo.setOnItemClickListener(new AdapterView.OnItemClickListener() {


                @Override
                public void onItemClick(AdapterView<?> arg0, View arg1,
                                        int arg2, long arg3) {
                    HashMap<String, Object> obj = (HashMap<String, Object>) ADA.getItem(arg2);
                    String depoid = (String) obj.get("A");
                    String deponame = (String) obj.get("B");

                    SharedPreferences.Editor editor = sharedpreferences.edit();
                    editor.putString("AnaDepoId", depoid);


                    Intent i = new Intent(Iade.this, IadeOku.class);
                    i.putExtra("secilenDepo", deponame);

                    editor.commit();
                    bundle = ActivityOptions.makeSceneTransitionAnimation(Iade.this).toBundle();
                    startActivity(i, bundle);


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
                        Map<String, String> datanum = new HashMap<String, String>();
                        datanum.put("A", rs.getString("WAREHOUSEID"));
                        datanum.put("B", rs.getString("NAME"));
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

}


