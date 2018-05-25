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
import android.os.Bundle;
import android.os.Vibrator;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
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

public class PlasiyerProduct extends AppCompatActivity implements PopupMenu.OnMenuItemClickListener {
    ActionBar ab;
    ConnectionClass connectionClass;
    SharedPreferences sharedPreferences;
    Bundle bundle;
    Vibrator vibrator;
    String memberid, comid;
    ProgressBar progressBar;
    String secilendepo, secilendepoId, disable, incomingCariAd, incomingCariKod, incomingCariId;
    TextView cariAdi, depo;
    LinearLayout relativeLayout3, lastGG;
    Button btn_direkSatis, btn_barkodSatis, btn_tamamla;
    ListView lst_plasiyer;
    String secilenSatis, secilenSatisAdi;
    PlasiyerProductAdapter adapter;
    UUID uuid ;
    String plasiyerCode;
    ArrayList<String> updateArray = new ArrayList<>();
    String memberCount;
    TextView tx_toplamToplam,tx_kdvToplam,tx_genelToplam;
    float toplamToplam,kdvToplam,genelToplam;
    ArrayList<String> silinecekArray = new ArrayList<>();
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_plasiyer_product);
        this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        vibrator = (Vibrator) getSystemService(VIBRATOR_SERVICE);
        sharedPreferences = getApplicationContext().getSharedPreferences(MyPREFERENCES, Context.MODE_PRIVATE);
        progressBar = (ProgressBar) findViewById(R.id.pbbarP);
        cariAdi = (TextView) findViewById(R.id.tx_cariAdi);
        depo = (TextView) findViewById(R.id.depoProduct);
        btn_barkodSatis = (Button) findViewById(R.id.btn_barkodSatis);
        btn_direkSatis = (Button) findViewById(R.id.btn_direkSatis);
        relativeLayout3 = (LinearLayout) findViewById(R.id.relativeLayout3);
        lastGG = (LinearLayout) findViewById(R.id.lastGG);
        lst_plasiyer = (ListView) findViewById(R.id.lst_plasiyer);
        btn_tamamla = (Button) findViewById(R.id.btn_tamamla);
        tx_toplamToplam = (TextView) findViewById(R.id.tx_toplamToplam);
        tx_kdvToplam = (TextView) findViewById(R.id.tx_kdvToplam);
        tx_genelToplam = (TextView) findViewById(R.id.tx_genelToplam);

        memberid = sharedPreferences.getString("ID", null);
        comid = sharedPreferences.getString("Companiesid", null);
        Intent incomingIntent = getIntent();
        incomingCariAd = sharedPreferences.getString("plasiyerCariAd", null);
        incomingCariKod = sharedPreferences.getString("plasiyerCariKod", null);
        incomingCariId = sharedPreferences.getString("plasiyerCariId", null);
        secilendepo = sharedPreferences.getString("plasiyerDepoAd", null);
        secilendepoId = sharedPreferences.getString("plasiyerDepoId", null);
        disable = incomingIntent.getStringExtra("disable");
        cariAdi.setText(incomingCariAd);
        depo.setText(secilendepo);
        uuid = UUID.randomUUID();
        if (disable.equals("disable")) {
           // relativeLayout3.setVisibility(View.GONE);
            lastGG.setVisibility(View.GONE);
        }
        connectionClass = new ConnectionClass();
        ab = getSupportActionBar();
        ab.setTitle("Plasiyer Satiş");
        ab.setSubtitle("");
        ab.setBackgroundDrawable(getResources().getDrawable(R.drawable.arkaplan));

        btn_barkodSatis.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(PlasiyerProduct.this, PlasiyerSatisSec.class);

                startActivityForResult(intent,1);
            }
        });
        btn_direkSatis.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(PlasiyerProduct.this, PlasiyerSatisSec.class);
                startActivityForResult(intent,1);
            }
        });
        btn_tamamla.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (lst_plasiyer == null) {
                    Toast.makeText(getApplicationContext(), "Satış Bulunamadı.", Toast.LENGTH_LONG).show();
                } else {
                    AlertDialog.Builder builder2 = new AlertDialog.Builder(PlasiyerProduct.this);
                    builder2.setTitle("UYARI!");
                    builder2.setMessage("Satışı tamamlamak istediğinizden emin misiniz?");
                    builder2.setNegativeButton("EVET", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            PlasiyerCodeGenerater tamamla = new PlasiyerCodeGenerater();
                            tamamla.execute("");
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
        DeletePro deletePro = new DeletePro();
        deletePro.execute("");
        CountGetir countGetir = new CountGetir();
        countGetir.execute("");
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
            Intent i = new Intent(PlasiyerProduct.this, SliderMenu.class);
            bundle = ActivityOptions.makeSceneTransitionAnimation(PlasiyerProduct.this).toBundle();
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
                Intent intent = new Intent(PlasiyerProduct.this, PlasiyerProductDetails.class);
                intent.putExtra("secilenSatis", secilenSatis);
                intent.putExtra("secilenSatisAdi", secilenSatisAdi);

                startActivityForResult(intent, 1);
                return true;
            default:
                return false;
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
                        Context wrapper = new ContextThemeWrapper(PlasiyerProduct.this, R.style.YOURSTYLE);
                        PopupMenu popup = new PopupMenu(wrapper, view);

                        popup.setOnMenuItemClickListener((PopupMenu.OnMenuItemClickListener) PlasiyerProduct.this);
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
                    String query = "select CODE,PRODUCTNAME,UNITPRICE,SUM(FIRSTAMOUNT) as AMOUNT,SUM(TOTAL) as TOTAL, SUM(KDVTOTAL) as KDVTOTAL , SUM(GENERALTOTAL) as GENERALTOTAL  from vw_WAREHOUSEPLASIERDETAIL where COMPANIESID = '" + comid + "' and ISOKEY = '0' group by CODE,PRODUCTNAME,UNITPRICE";
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

    public class PlasiyerCodeGenerater extends AsyncTask<String, String, String> {
        String w = "";
        String count;
        @Override
        protected void onPreExecute() {
            progressBar.setVisibility(View.VISIBLE);
        }

        @Override
        protected void onPostExecute(String s) {
            progressBar.setVisibility(View.GONE);
            plasiyerCode = incomingCariKod +"-"+ count +""+ memberCount;
            SendProducts sendProducts = new SendProducts();
            sendProducts.execute("");
        }

        @Override
        protected String doInBackground(String... strings) {
            try {
                Connection con = connectionClass.CONN();
                if (con == null) {
                    w = "Error in connection with SQL server";
                } else {
                    String query = "select ISNULL(count(*),1) + 1 as COUNT from WAREHOUSEPLASIER where CURRENTID='"+incomingCariId+"'";
                    PreparedStatement ps = con.prepareStatement(query);
                    ResultSet rs = ps.executeQuery();

                    while (rs.next()) {
                        count = rs.getString("COUNT");

                    }
                    w = "Başarılı";
                }
            } catch (Exception ex) {
                w = "Veri Çekme Hatası";

            }
            return w;
        }
    }

    @SuppressLint("NewApi")
    public class SendProducts extends AsyncTask<String, String, String> {
        String z = "";
        boolean hata;
        int i = 0;

        @Override
        protected void onPreExecute() {
            progressBar.setVisibility(View.VISIBLE);
            hata = false;

        }

        @Override
        protected void onPostExecute(String r) {
            progressBar.setVisibility(View.GONE);
            if (!hata) {
                UpdatePlasiyer updatePlasiyer = new UpdatePlasiyer();
                updatePlasiyer.execute("");
            } else {
                Toast.makeText(getApplicationContext(), "HATA!", Toast.LENGTH_SHORT).show();

            }

        }

        @Override
        protected String doInBackground(String... params) {
            try {
                Connection con = connectionClass.CONN();
                if (con == null) {
                    z = "Error in connection with SQL server";
                } else {
                        String query = "insert into WAREHOUSEPLASIER (ID,PLASIERCODE,WAREHOUSEID,MEMBERID,CURRENTID,ISDELETE,DATE) " +
                                "values ('"+uuid+"','"+plasiyerCode+"','"+secilendepoId+"','"+memberid+"','"+incomingCariId+"',0,GETDATE())";
                        PreparedStatement preparedStatement = con.prepareStatement(query);
                        preparedStatement.executeUpdate();
                    hata = false;

                }
            } catch (Exception ex) {
                z = "Veri Çekme Hatası";
                ex.printStackTrace();
            }
            return z;
        }
    }

    @SuppressLint("NewApi")
    public class UpdatePlasiyer extends AsyncTask<String, String, String> {
        String z = "";
        boolean hata;
        int i = 0;

        @Override
        protected void onPreExecute() {
            progressBar.setVisibility(View.VISIBLE);
            hata = false;

        }

        @Override
        protected void onPostExecute(String r) {
            progressBar.setVisibility(View.GONE);

            if (!hata) {
                UpdatePlasiyerSon updatePlasiyerSon = new UpdatePlasiyerSon();
                updatePlasiyerSon.execute("");

            } else {
                Toast.makeText(getApplicationContext(), "HATA!", Toast.LENGTH_SHORT).show();

            }

        }

        @Override
        protected String doInBackground(String... params) {
            try {
                Connection con = connectionClass.CONN();
                if (con == null) {
                    z = "Error in connection with SQL server";
                } else {
                    String query = "select ID from WAREHOUSEPLASIERDETAIL where MEMBERID = '"+memberid+"' and ISOKEY = 0";
                    PreparedStatement ps = con.prepareStatement(query);
                    ResultSet rs = ps.executeQuery();
                    while (rs.next()) {
                        updateArray.add(rs.getString("ID"));

                    }
                }
            } catch (Exception ex) {
                z = "Veri Çekme Hatası";
                ex.printStackTrace();
            }
            return z;
        }
    }
    @SuppressLint("NewApi")
    public class UpdatePlasiyerSon extends AsyncTask<String, String, String> {
        String z = "";
        boolean hata;
        int i = 0;

        @Override
        protected void onPreExecute() {
            progressBar.setVisibility(View.VISIBLE);
            hata = false;

        }

        @Override
        protected void onPostExecute(String r) {
            progressBar.setVisibility(View.GONE);

            if (!hata) {
                Toast.makeText(getApplicationContext(), "TAMANLANDI!", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(PlasiyerProduct.this,PlasiyerList.class);
                startActivity(intent);
                finish();
            } else {
                Toast.makeText(getApplicationContext(), "HATA!", Toast.LENGTH_SHORT).show();

            }

        }

        @Override
        protected String doInBackground(String... params) {
            try {
                Connection con = connectionClass.CONN();
                if (con == null) {
                    z = "Error in connection with SQL server";
                } else {
                    for (int j = 0; j <updateArray.size() ; j++) {
                        String query = "UPDATE WAREHOUSEPLASIERDETAIL SET WAREHOUSEPLASIERID='"+uuid+"', ISOKEY = 1 where ID = '"+updateArray.get(j)+"'";
                        PreparedStatement preparedStatement = con.prepareStatement(query);
                        preparedStatement.executeUpdate();
                    }

                }
            } catch (Exception ex) {
                z = "Veri Çekme Hatası";
                ex.printStackTrace();
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
            progressBar.setVisibility(View.VISIBLE);

        }

        @Override
        protected void onPostExecute(String r) {
            progressBar.setVisibility(View.GONE);
            FillList fillList = new FillList();
            fillList.execute("");
        }

        @Override
        protected String doInBackground(String... params) {

            try {
                Connection con = connectionClass.CONN();
                if (con == null) {
                    z = "Error in connection with SQL server";
                } else {

                    String query = "Delete  from WAREHOUSEPLASIERDETAIL where ISOKEY='0' and MEMBERID='"+memberid+"' ";
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
                Toast.makeText(PlasiyerProduct.this, "HATA", Toast.LENGTH_SHORT).show();

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
                Toast.makeText(PlasiyerProduct.this, "Silindi", Toast.LENGTH_SHORT).show();
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
    public class CountGetir extends AsyncTask<String, String, String> {
        String w = "";
        boolean exist = false;

        @Override
        protected void onPreExecute() {
            progressBar.setVisibility(View.VISIBLE);

        }

        @Override
        protected void onPostExecute(String s) {
            progressBar.setVisibility(View.GONE);
        }

        @Override
        protected String doInBackground(String... strings) {
            try {
                Connection con = connectionClass.CONN();
                if (con == null) {
                    w = "Error in connection with SQL server";
                } else {
                    String query = "select BARKODCOUNT from MEMBER where ID = '"+memberid+"' ";
                    PreparedStatement ps = con.prepareStatement(query);
                    ResultSet rs = ps.executeQuery();

                    if (rs.next()) {
                        memberCount = rs.getString("BARKODCOUNT");

                    }
                    w = "Başarılı";
                }
            } catch (Exception ex) {
                w = "Veri Çekme Hatası";

            }
            return w;
        }
    }
}

