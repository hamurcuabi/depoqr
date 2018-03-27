package com.emrehmrc.depoqr;

import android.annotation.SuppressLint;
import android.app.ActivityOptions;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.TransitionDrawable;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Spinner;

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

public class Products extends AppCompatActivity {

    EditText edtproname;
    Button btnpalet;
    ProgressBar pbbar;
    String proid;
    String filter;
    String ID, CID;

    ConnectionClass connectionClass;
    ListView lstpro;
    FillList fillList;
    SpecialAdapter ADA;
    ActionBar ab;
    Spinner spn;
    Animation animation;



    @RequiresApi(api = Build.VERSION_CODES.CUPCAKE)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_products);

        connectionClass = new ConnectionClass();


        //Session
        SharedPreferences sharedpreferences = getSharedPreferences(AnaSayfa.MyPREFERENCES, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedpreferences.edit();
        ID = sharedpreferences.getString("ID", null);
        CID = sharedpreferences.getString("Companiesid", null);

        //bitiş session

        ab = getSupportActionBar();
        ab.setTitle("ÜRÜNLER");
        ab.setSubtitle("Ürün Listesi");
        ab.setBackgroundDrawable(getResources().getDrawable(R.drawable.arkaplan));


        edtproname = (EditText) findViewById(R.id.edtproname);
        edtproname.setOnTouchListener(new View.OnTouchListener() {

            @Override
            public boolean onTouch(View v, MotionEvent event) {

                v.setFocusable(true);
                v.setFocusableInTouchMode(true);
                return false;
            }
        });


        btnpalet = (Button) findViewById(R.id.btnpalet);
        pbbar = (ProgressBar) findViewById(R.id.pbbar);
        pbbar.setVisibility(View.GONE);
        lstpro = (ListView) findViewById(R.id.lstproducts);
        spn = (Spinner) findViewById(R.id.spn);
        spn.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                switch (position) {
                    case 0:
                        filter = "ORDER BY CODE";
                        fillList = new FillList();
                        fillList.execute("");
                        break;
                    case 1:
                        filter = "ORDER BY CATEGORYID";
                        fillList = new FillList();
                        fillList.execute("");
                        break;
                    case 2:
                        filter = "ORDER BY NAME";
                        fillList = new FillList();
                        fillList.execute("");
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
        proid = "";
        fillList = new FillList();
        fillList.execute("");

        //Toast.makeText(Products.this, session.memberid(), Toast.LENGTH_SHORT).show();
        btnpalet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(Products.this, ProductInfo.class));

            }
        });


        edtproname.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

                try {
                    ADA.getFilter().filter(edtproname.getText());


                } catch (Exception ex) {

                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });


    }

    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.diger, menu);
        return super.onCreateOptionsMenu(menu);
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.anasayfa) {
            finish();
            Intent i = new Intent(Products.this, SliderMenu.class);
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

                    String query = "delete from EMRE where Id=" + proid;
                    PreparedStatement preparedStatement = con.prepareStatement(query);
                    preparedStatement.executeUpdate();
                    z = "Deleted Successfully";
                    isSuccess = true;
                }
            } catch (Exception ex) {
                isSuccess = false;
                z = "Exceptions";
            }

            return z;
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.CUPCAKE)
    public class UpdatePro extends AsyncTask<String, String, String> {


        String z = "";
        Boolean isSuccess = false;


        String proname = edtproname.getText().toString();


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

                    String query = "Update EMRE set Name='" + proname + "' where Id=" + proid;
                    PreparedStatement preparedStatement = con.prepareStatement(query);
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

    @RequiresApi(api = Build.VERSION_CODES.CUPCAKE)
    public class AddPro extends AsyncTask<String, String, String> {


        String z = "";
        Boolean isSuccess = false;
        String proname = edtproname.getText().toString();

        @Override
        protected void onPreExecute() {
            pbbar.setVisibility(View.VISIBLE);
        }

        @Override
        protected void onPostExecute(String r) {
            pbbar.setVisibility(View.GONE);
            //   Toast.makeText(Products.this, r, Toast.LENGTH_SHORT).show();
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

                    String dates = new SimpleDateFormat("MM/dd/yyyy", Locale.ENGLISH)
                            .format(Calendar.getInstance().getTime());
                    String query = "insert into EMRE (Name) values ('" + proname + "')";
                    PreparedStatement preparedStatement = con.prepareStatement(query);
                    preparedStatement.executeUpdate();
                    z = "Added Successfully";
                    isSuccess = true;
                }
            } catch (Exception ex) {
                isSuccess = false;
                z = "Exceptions";
            }

            return z;
        }
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
            //  Toast.makeText(Products.this, r, Toast.LENGTH_SHORT).show();

            String[] from = {"A", "B", "C"};
            int[] views = {R.id.lblproid, R.id.lblproname, R.id.lblprodesc};
            ADA = new SpecialAdapter(Products.this,
                    prolist, R.layout.lsttemplate, from,
                    views);

            lstpro.setAdapter(ADA);
             animation = AnimationUtils.loadAnimation(Products.this, R.anim.scale);

            lstpro.setOnItemClickListener(new AdapterView.OnItemClickListener() {


                @Override
                public void onItemClick(AdapterView<?> arg0, View arg1,
                                        int arg2, long arg3) {
                    ColorDrawable[] AnimationColors = {
                            new ColorDrawable(Color.parseColor("#63c75c")),
                            new ColorDrawable(Color.parseColor("#5cbec7")),

                    };

                    ColorDrawable[] DefaultColors = {
                            new ColorDrawable(Color.parseColor("#5cbec7")),
                            new ColorDrawable(Color.parseColor("#01fee3")),

                    };
                    TransitionDrawable transitiondrawable1,transitiondrawable2;
                    transitiondrawable1 = new TransitionDrawable(AnimationColors);
                    arg1.setBackground(transitiondrawable1);
                    transitiondrawable1.startTransition(1000);

                    transitiondrawable2 = new TransitionDrawable(DefaultColors);

                    arg1.setBackground(transitiondrawable2);

                    transitiondrawable2.startTransition(1000);

                    HashMap<String, Object> obj = (HashMap<String, Object>) ADA
                            .getItem(arg2);
                    String proid = (String) obj.get("A");
                    String procode = (String) obj.get("B");
                    String proname = (String) obj.get("C");

                    ArrayList<String> myList = new ArrayList<String>();
                    myList.add(0, procode);
                    myList.add(1, proname);
                    myList.add(2, proid);
                    arg1.startAnimation(animation);
                    Intent i = new Intent(Products.this, ProductInfo.class);
                    i.putExtra("mylist", myList);
                    Bundle bundle = ActivityOptions.makeSceneTransitionAnimation(Products.this)
                            .toBundle();

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
                    String query = "select * from PRODUCT where  COMPANIESID='" + CID + "' and " +
                            "ISDELETE='FALSE'" + filter;
                    PreparedStatement ps = con.prepareStatement(query);
                    ResultSet rs = ps.executeQuery();


                    while (rs.next()) {
                        Map<String, String> datanum = new HashMap<String, String>();
                        datanum.put("A", rs.getString("ID"));
                        datanum.put("B", rs.getString("CODE"));
                        datanum.put("C", rs.getString("NAME"));
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
