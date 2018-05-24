package com.emrehmrc.depoqr;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.ActivityOptions;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Vibrator;
import android.support.annotation.RequiresApi;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.ContextThemeWrapper;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupMenu;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.UUID;

import static com.emrehmrc.depoqr.AnaSayfa.MyPREFERENCES;

public class PlasiyerProductView extends AppCompatActivity implements PopupMenu.OnMenuItemClickListener {
    ActionBar ab;
    ConnectionClass connectionClass;
    SharedPreferences sharedPreferences;
    Bundle bundle;
    Vibrator vibrator;
    String memberid, comid;
    ProgressBar progressBar;
    TextView cariAdi, depo;
    ListView lst_plasiyer;
    String secilenSatis, secilenSatisAdi,incomingPlasiyerId;
    PlasiyerProductAdapter adapter;
    UUID uuid ;
    TextView tx_toplamToplam,tx_kdvToplam,tx_genelToplam;
    float toplamToplam,kdvToplam,genelToplam;
    ArrayList<String> silinecekArray = new ArrayList<>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_plasiyer_product_view);
        this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        vibrator = (Vibrator) getSystemService(VIBRATOR_SERVICE);
        sharedPreferences = getApplicationContext().getSharedPreferences(MyPREFERENCES, Context.MODE_PRIVATE);
        progressBar = (ProgressBar) findViewById(R.id.pbbarP);
        cariAdi = (TextView) findViewById(R.id.tx_cariAdi);
        depo = (TextView) findViewById(R.id.depoProduct);
        lst_plasiyer = (ListView) findViewById(R.id.lst_plasiyer);
        tx_toplamToplam = (TextView) findViewById(R.id.tx_toplamToplam);
        tx_kdvToplam = (TextView) findViewById(R.id.tx_kdvToplam);
        tx_genelToplam = (TextView) findViewById(R.id.tx_genelToplam);
        memberid = sharedPreferences.getString("ID", null);
        comid = sharedPreferences.getString("Companiesid", null);
        incomingPlasiyerId = sharedPreferences.getString("secilenPlasiyerId", null);
        uuid = UUID.randomUUID();
        connectionClass = new ConnectionClass();
        ab = getSupportActionBar();
        ab.setTitle("Plasiyer Satiş");
        ab.setSubtitle("");
        ab.setBackgroundDrawable(getResources().getDrawable(R.drawable.arkaplan));
        FillList fillList = new FillList();
        fillList.execute("");
        FillListNames fillListNames = new FillListNames();
        fillListNames.execute("");
    }

    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.plasiyersatis, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.anasayfa) {
            finish();
            Intent i = new Intent(PlasiyerProductView.this, SliderMenu.class);
            bundle = ActivityOptions.makeSceneTransitionAnimation(PlasiyerProductView.this).toBundle();
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
    public boolean onMenuItemClick(MenuItem menuItem) {
        switch (menuItem.getItemId()) {
            case R.id.menu_Sil:
                DeleteChild silenecekler = new DeleteChild();
                silenecekler.execute("");
                return true;
            case R.id.menu_duzenle:
                Intent intent = new Intent(PlasiyerProductView.this, PlasiyerProductDetails.class);
                intent.putExtra("secilenSatis", secilenSatis);
                intent.putExtra("secilenSatisAdi", secilenSatisAdi);

                startActivityForResult(intent, 1);
                return true;
            default:
                return false;
        }
    }
    public class FillListNames extends AsyncTask<String, String, String> {
        String w = "";
        String cariAdix,depox;
        @Override
        protected void onPostExecute(String s) {
           cariAdi.setText(cariAdix);
           depo.setText(depox);
        }

        @Override
        protected String doInBackground(String... strings) {
            try {
                Connection con = connectionClass.CONN();
                if (con == null) {
                    w = "Error in connection with SQL server";
                } else {
                    String query = "select CURRENTNAME,WAREHOUSENAME from VW_WAREHOUSEPLASIER where ID ='"+incomingPlasiyerId+"'";
                    PreparedStatement ps = con.prepareStatement(query);
                    ResultSet rs = ps.executeQuery();

                    if (rs.next()) {
                      cariAdix = rs.getString("CURRENTNAME");
                      depox = rs.getString("WAREHOUSENAME");
                    }
                    w = "Başarılı";
                }
            } catch (Exception ex) {
                w = "Veri Çekme Hatası";

            }
            return w;
        }
    }
    public class FillList extends AsyncTask<String, String, String> {
        String w = "";
        ArrayList<PlasiyerProductModel> plasiyerArray = new ArrayList<PlasiyerProductModel>();
        boolean exist = false;

        @Override
        protected void onPreExecute() {
            plasiyerArray.clear();
            lst_plasiyer.setAdapter(null);
            progressBar.setVisibility(View.VISIBLE);
            toplamToplam=0;
            genelToplam=0;
            kdvToplam=0;
        }

        @Override
        protected void onPostExecute(String s) {
            tx_genelToplam.setText("" + new DecimalFormat("##.##").format(genelToplam));
            tx_kdvToplam.setText("" + new DecimalFormat("##.##").format(kdvToplam));
            tx_toplamToplam.setText("" + new DecimalFormat("##.##").format(toplamToplam));
            progressBar.setVisibility(View.GONE);
            if (exist) {
                adapter = new PlasiyerProductAdapter(getApplicationContext(), plasiyerArray);
                lst_plasiyer.setAdapter(adapter);
                lst_plasiyer.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
                    @Override
                    public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                        PlasiyerProductModel plasiyerListModel;
                        plasiyerListModel = (PlasiyerProductModel) lst_plasiyer.getItemAtPosition(position);
                        secilenSatis = plasiyerListModel.getKod();
                        secilenSatisAdi = plasiyerListModel.getProductName();
                        Context wrapper = new ContextThemeWrapper(PlasiyerProductView.this, R.style.YOURSTYLE);
                        PopupMenu popup = new PopupMenu(wrapper, view);

                        popup.setOnMenuItemClickListener((PopupMenu.OnMenuItemClickListener) PlasiyerProductView.this);
                        popup.inflate(R.menu.poupup);
                        popup.show();
                        return true;
                    }
                });
            }
        }

        @Override
        protected String doInBackground(String... strings) {
            try {
                Connection con = connectionClass.CONN();
                if (con == null) {
                    w = "Error in connection with SQL server";
                } else {
                    String query = "select CODE,PRODUCTNAME,UNITPRICE,SUM(FIRSTAMOUNT) as AMOUNT,SUM(TOTAL) as TOTAL, SUM(KDVTOTAL) as KDVTOTAL , SUM(GENERALTOTAL) as GENERALTOTAL  from vw_WAREHOUSEPLASIERDETAIL where COMPANIESID = '" + comid + "' and WAREHOUSEPLASIERID = '"+incomingPlasiyerId+"' group by CODE,PRODUCTNAME,UNITPRICE";
                    PreparedStatement ps = con.prepareStatement(query);
                    ResultSet rs = ps.executeQuery();

                    while (rs.next()) {
                        exist = true;
                        toplamToplam = toplamToplam + rs.getFloat("TOTAL");
                        kdvToplam = kdvToplam + rs.getFloat("KDVTOTAL");
                        genelToplam = genelToplam + rs.getFloat("GENERALTOTAL");
                        plasiyerArray.add(new PlasiyerProductModel(rs.getString("CODE")
                                , rs.getString("PRODUCTNAME")
                                , rs.getFloat("UNITPRICE")
                                , rs.getFloat("AMOUNT")
                                , rs.getFloat("TOTAL"), rs.getFloat("KDVTOTAL"), rs.getFloat("GENERALTOTAL")));
                    }
                    w = "Başarılı";
                }
            } catch (Exception ex) {
                w = "Veri Çekme Hatası";

            }
            return w;
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.CUPCAKE)
    public class DeletePro extends AsyncTask<String, String, String> {


        String z = "";
        Boolean isSuccess = false;

        @Override
        protected void onPreExecute() {
            progressBar.setVisibility(View.VISIBLE);

        }

        @Override
        protected void onPostExecute(String r) {
            progressBar.setVisibility(View.GONE);
        }

        @Override
        protected String doInBackground(String... params) {

            try {
                Connection con = connectionClass.CONN();
                if (con == null) {
                    z = "Error in connection with SQL server";
                } else {

                    String query = "Delete  from WAREHOUSEPLASIERDETAIL where COMPANIESID = '" + comid + "' and ISOKEY='0' and MEMBERID='"+memberid+"' ";
                    PreparedStatement preparedStatement = con.prepareStatement(query);
                    preparedStatement.executeUpdate();
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
    public class DeleteChild extends AsyncTask<String, String, String> {
        String z = "";
        Boolean isSuccess = false;

        @Override
        protected void onPreExecute() {
            progressBar.setVisibility(View.VISIBLE);
            silinecekArray.clear();
        }

        @Override
        protected void onPostExecute(String r) {
            progressBar.setVisibility(View.GONE);
            if (isSuccess) {
                DeleteChild2 deletePro = new DeleteChild2();
                deletePro.execute("");

            } else {
                Toast.makeText(PlasiyerProductView.this, "HATA", Toast.LENGTH_SHORT).show();

            }
        }

        @Override
        protected String doInBackground(String... params) {

            try {
                Connection con = connectionClass.CONN();
                if (con == null) {
                    z = "Error in connection with SQL server";
                } else {

                    String query = "select ID from WAREHOUSEPLASIERDETAIL where CODE='"+secilenSatis+"'";
                    PreparedStatement ps = con.prepareStatement(query);
                    ResultSet rs = ps.executeQuery();

                    while (rs.next()) {
                        silinecekArray.add(rs.getString("ID"));
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
    @RequiresApi(api = Build.VERSION_CODES.CUPCAKE)
    public class DeleteChild2 extends AsyncTask<String, String, String> {


        String z = "";
        Boolean isSuccess = false;

        @Override
        protected void onPreExecute() {
            progressBar.setVisibility(View.VISIBLE);

        }

        @Override
        protected void onPostExecute(String r) {
            progressBar.setVisibility(View.GONE);
            if(isSuccess){
                Toast.makeText(PlasiyerProductView.this, "Silindi", Toast.LENGTH_SHORT).show();
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
                    for (int i = 0; i <silinecekArray.size() ; i++) {
                        String query = "Delete  from WAREHOUSEPLASIERDETAIL where ID ='"+silinecekArray.get(i)+"' ";
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
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == 1) {
            FillList fillList = new FillList();
            fillList.execute("");
        }
    }

}
