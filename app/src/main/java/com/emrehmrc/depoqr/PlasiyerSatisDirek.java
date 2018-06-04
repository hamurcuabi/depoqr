package com.emrehmrc.depoqr;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.ActivityOptions;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.AudioManager;
import android.media.ToneGenerator;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Vibrator;
import android.support.annotation.RequiresApi;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.emrehmrc.depoqr.adapter.SevkiyetTarihAdapter;
import com.emrehmrc.depoqr.connection.ConnectionClass;
import com.emrehmrc.depoqr.model.SevkiyatÜrünleriRecyclerView;
import com.emrehmrc.depoqr.popup.UyariBildirim;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.UUID;

import static com.emrehmrc.depoqr.AnaSayfa.MyPREFERENCES;

public class PlasiyerSatisDirek extends AppCompatActivity {
    ActionBar ab;
    ConnectionClass connectionClass;
    SharedPreferences sharedPreferences;
    Bundle bundle;
    String incomingKod, incomingAd, incomingDepo, memberid, comid, incomingDepoId, incomingCariId,secilenUrunAdi;
    Vibrator vibrator;
    String birimFiyat, kdv, secilenUrun,kdvDahil;
    ArrayList<SevkiyatÜrünleriRecyclerView> datalist;
    ArrayList<Float> firstAmount = new ArrayList<>();
    ArrayList<Float> secondAmount = new ArrayList<>();
    ArrayList<String> ExistArray = new ArrayList<>();
    ArrayList<String> NotExistArray = new ArrayList<>();
    ToneGenerator toneG;
    TextView gelendepo,gelenad,tx_urunAdi;
    TextView tx_birinciBirim,tx_ikinciBirim,tx_barkodSayisi;
    float birinciBirim,ikinciBirim;
    ImageView btn_calculate;
    CheckBox checkBoxall;
    SevkiyetTarihAdapter emreAdaptor;
    RecyclerView recyclerview;
    ProgressBar pbbarS;
    int memberCount;
    SevkiyatÜrünleriRecyclerView gecici;
    Button btnsend;
    int recode,code;
    String codelast;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_plasiyer_satis_direk);
        this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        vibrator = (Vibrator) getSystemService(VIBRATOR_SERVICE);
        sharedPreferences = getApplicationContext().getSharedPreferences(MyPREFERENCES, Context.MODE_PRIVATE);
        memberid = sharedPreferences.getString("ID", null);
        comid = sharedPreferences.getString("Companiesid", null);
        connectionClass = new ConnectionClass();
        ab = getSupportActionBar();
        ab.setTitle("Plasiyer Satiş");
        ab.setSubtitle("Barkod Satiş");
        ab.setBackgroundDrawable(getResources().getDrawable(R.drawable.arkaplan));
        incomingAd = sharedPreferences.getString("plasiyerCariAd", null);
        incomingKod = sharedPreferences.getString("plasiyerCariKod", null);
        incomingCariId = sharedPreferences.getString("plasiyerCariId", null);
        incomingDepo = sharedPreferences.getString("plasiyerDepoAd", null);
        incomingDepoId = sharedPreferences.getString("plasiyerDepoId", null);
        Intent incomingIntent = getIntent();
        birimFiyat = incomingIntent.getStringExtra("BirimFiyat");
        kdv = incomingIntent.getStringExtra("KDV");
        kdvDahil = incomingIntent.getStringExtra("KDVDAHIL");
        secilenUrun = incomingIntent.getStringExtra("secilenUrun");
        secilenUrunAdi = incomingIntent.getStringExtra("secilenUrunAdi");
        datalist = new ArrayList<>();
        toneG = new ToneGenerator(AudioManager.STREAM_ALARM, 100);

        gelenad = (TextView) findViewById(R.id.gelenad2);
        gelendepo = (TextView) findViewById(R.id.gelendepo2);
        tx_urunAdi = (TextView) findViewById(R.id.tx_urunAdi);
        tx_birinciBirim = (TextView) findViewById(R.id.tx_birinciBirim);
        tx_ikinciBirim = (TextView) findViewById(R.id.tx_ikinciBirim);
        tx_barkodSayisi = (TextView) findViewById(R.id.tx_barkodSayisi);
        btn_calculate = (ImageView) findViewById(R.id.btn_calculate);
        checkBoxall = (CheckBox) findViewById(R.id.checkBoxall);
        pbbarS = (ProgressBar) findViewById(R.id.pbbarS);
        recyclerview = (RecyclerView) findViewById(R.id.recyclerview);
        btnsend = (Button) findViewById(R.id.btnsend);

        gelendepo.setText("DEPO: "+ incomingDepo);
        gelenad.setText("CARI: "+ incomingAd);
        tx_urunAdi.setText("ÜRÜN: "+ secilenUrunAdi);
        checkBoxall.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

                int a = datalist.size();
                if (isChecked) {
                    for (int i = 0; i < a; i++) {
                        datalist.get(i).setChecked(true);
                    }

                } else {
                    for (int i = 0; i < a; i++) {
                        datalist.get(i).setChecked(false);
                    }
                }

                emreAdaptor = new SevkiyetTarihAdapter(getApplicationContext(), datalist, firstAmount, secondAmount);
                recyclerview.setAdapter(emreAdaptor);

            }
        });

        btn_calculate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                birinciBirim = 0;
                ikinciBirim = 0;
                tx_barkodSayisi.setText(datalist.size()+"");
                for (int i = 0; i < datalist.size(); i++) {
                    if(datalist.get(i).isChecked()){
                        birinciBirim = birinciBirim+ firstAmount.get(i);
                        ikinciBirim = ikinciBirim + secondAmount.get(i);
                    }
                }
                tx_birinciBirim.setText(birinciBirim+"");
                tx_ikinciBirim.setText(ikinciBirim+"");
            }
        });
        CountGetir countGetir = new CountGetir();
        countGetir.execute("");
        FillProductsSec fillProductsSec = new FillProductsSec();
        fillProductsSec.execute("");
        btnsend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!datalist.isEmpty()) {
                    ExistArray.clear();
                    NotExistArray.clear();
                    IsSame ısSame = new IsSame();
                    ısSame.execute("");
                } else {
                    Toast.makeText(getApplicationContext(), "LİSTEDE ÜRÜN BULUNAMADI :(", Toast.LENGTH_SHORT).show();

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
            finish();
            Intent i = new Intent(PlasiyerSatisDirek.this, SliderMenu.class);
            bundle = ActivityOptions.makeSceneTransitionAnimation(PlasiyerSatisDirek.this).toBundle();
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
    public class CountGetir extends AsyncTask<String, String, String> {
        String w = "";
        boolean exist = false;

        @Override
        protected void onPreExecute() {
            pbbarS.setVisibility(View.VISIBLE);

        }

        @Override
        protected void onPostExecute(String s) {
            pbbarS.setVisibility(View.GONE);
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
                        memberCount = rs.getInt("BARKODCOUNT");

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
    public class FillProductsSec extends AsyncTask<String, String, String> {
        String z = "";
        boolean empty;
        @Override
        protected void onPreExecute() {

            empty = true;
            pbbarS.setVisibility(View.VISIBLE);
        }

        @Override
        protected void onPostExecute(String r) {
            pbbarS.setVisibility(View.GONE);
            if (!empty) {
                toneG.startTone(ToneGenerator.TONE_CDMA_ALERT_CALL_GUARD, 200);
                emreAdaptor = new SevkiyetTarihAdapter(getApplicationContext(), datalist, firstAmount, secondAmount);
                recyclerview.setAdapter(emreAdaptor);
                LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getApplicationContext());
                linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
                recyclerview.setLayoutManager(linearLayoutManager);

            } else {
                toneG.startTone(ToneGenerator.TONE_CDMA_CALL_SIGNAL_ISDN_PING_RING, 200);
                Intent intent = new Intent(getBaseContext(), UyariBildirim.class);
                intent.putExtra("UYARI", "HATA!");
                startActivity(intent);
            }

        }


        @Override
        protected String doInBackground(String... params) {

            try {
                Connection con = connectionClass.CONN();
                if (con == null) {
                    z = "Error in connection with SQL server";
                } else {
                    String g2 = "Select PRODUCTIONDATE,BARCODEID,BARCODENO,PRODUCTNAME,PALETID,PALETBARCODES,PRODUCTID," +
                            "PRODUCTCODE, " +
                            "FIRSTUNITNAME,SECONDUNITNAME,\n" +
                            "SUM(WDIRECTION * FIRSTAMOUNT)  AS FIRSTAMOUNT, \n" +
                            "SUM(WDIRECTION * SECONDAMOUNT) AS SECONDAMOUNT \n" +
                            "from VW_WAREHOUSESTOCKMOVEMENT where (DESTINATIONWAREHOUSEID = '" + incomingDepoId + "' or SOURCEWAREHOUSEID = '" + incomingDepoId + "') and \n" +
                            "PRODUCTCODE ='"+secilenUrun+"' and COMPANIESID = '" + comid + "'\n" +
                            "group by PRODUCTIONDATE,BARCODEID,BARCODENO,PALETBARCODES,PRODUCTNAME,PALETID,PRODUCTID," +
                            "PRODUCTCODE," +
                            "FIRSTUNITNAME,SECONDUNITNAME\n" +
                            "having SUM(WDIRECTION * FIRSTAMOUNT) != 0 or \n" +
                            "SUM(WDIRECTION * SECONDAMOUNT) != 0 order by PRODUCTIONDATE";
                    PreparedStatement ps = con.prepareStatement(g2);
                    ResultSet rs = ps.executeQuery();

                    while (rs.next()) {
                        gecici = new SevkiyatÜrünleriRecyclerView();
                        boolean same = false;
                        gecici.setChecked(true);
                        gecici.setName(rs.getString("PRODUCTNAME"));
                        gecici.setFirstUnit(rs.getString("FIRSTUNITNAME"));
                        gecici.setSecondUnit(rs.getString("SECONDUNITNAME"));
                        gecici.setFirstamount(Float.parseFloat(rs.getString("FIRSTAMOUNT")));
                        gecici.setSecondamount(Float.parseFloat(rs.getString("SECONDAMOUNT")));
                        gecici.setPaletid(rs.getString("PALETID"));
                        gecici.setUniqCode(rs.getString("BARCODENO"));
                        gecici.setProductid(rs.getString("PRODUCTID"));
                        gecici.setBarcodeid(rs.getString("BARCODEID"));
                        gecici.setPrductionDate(rs.getString("PRODUCTIONDATE"));
                        gecici.setUyari1("");
                        gecici.setUyari2("");
                        for (int j = 0; j < datalist.size(); j++) {
                            if (datalist.get(j).getBarcodeid().equals(gecici.getBarcodeid())) {
                                same = true;
                                break;
                            }
                        }
                        if (!same) {
                            datalist.add(gecici);
                        }
                        empty = false;
                    }
                    z = "Başarılı";
                }
            } catch (Exception ex) {
                z = "Veri Çekme Hatası";
                ex.printStackTrace();

            }
            return z;
        }

    }
    @SuppressLint("NewApi")
    public class IsSame extends AsyncTask<String, String, String> {
        String z = "";


        @Override
        protected void onPreExecute() {
            pbbarS.setVisibility(View.VISIBLE);
        }

        @Override
        protected void onPostExecute(String r) {
            pbbarS.setVisibility(View.GONE);

            if (!ExistArray.isEmpty()) {
                AlertDialog.Builder builder2 = new AlertDialog.Builder(PlasiyerSatisDirek.this);
                builder2.setTitle("UYARI!");
                builder2.setMessage("SATIŞTA AYNI BARKOD BULUNDU SİLİNSİN Mİ?");
                builder2.setNegativeButton("EVET", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        DeletePro deletePro = new DeletePro();
                        deletePro.execute("");
                    }
                });
                builder2.setPositiveButton("HAYIR, Eklemek istiyorum", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        SayiGetir sayiGetir = new SayiGetir();
                        sayiGetir.execute("");

                    }
                });
                builder2.show();
            } else {
                SayiGetir sayiGetir = new SayiGetir();
                sayiGetir.execute("");
            }
        }

        @Override
        protected String doInBackground(String... params) {
            try {
                Connection con = connectionClass.CONN();
                if (con == null) {
                    z = "Error in connection with SQL server";
                } else {
                    for (int j = 0; j < datalist.size(); j++) {
                        if (datalist.get(j).isChecked()) {
                            String q = "Select * from VW_WAREHOUSEPLASIERDETAIL where BARCODEID='" + datalist.get(j).getBarcodeid() + "' and ISOKEY ='0' ";
                            PreparedStatement ps = con.prepareStatement(q);
                            ResultSet rs = ps.executeQuery();
                            if (rs.next()) {
                                ExistArray.add(datalist.get(j).getBarcodeid());

                            } else {
                                NotExistArray.add(datalist.get(j).getBarcodeid());
                            }
                            z = "Başarılı";
                        }
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
            pbbarS.setVisibility(View.VISIBLE);
        }

        @Override
        protected void onPostExecute(String r) {
            pbbarS.setVisibility(View.GONE);
            if (isSuccess) {
                datalist.clear();
                firstAmount.clear();
                secondAmount.clear();
                recyclerview.setAdapter(null);
                FillProductsSec fillProductsSec = new FillProductsSec();
                fillProductsSec.execute("");

                Toast.makeText(PlasiyerSatisDirek.this, "BAŞARIYLA SİLİNDİ", Toast.LENGTH_SHORT).show();
            }
        }

        @Override
        protected String doInBackground(String... params) {

            try {
                Connection con = connectionClass.CONN();
                if (con == null) {
                    z = "Error in connection with SQL server";
                } else {
                    for (int j = 0; j < ExistArray.size(); j++) {
                        String query = "Delete  from SENTFORWARDING where  BARCODEID='" + ExistArray.get(j) + "'";
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
    public class SayiGetir extends AsyncTask<String, String, String> {
        String w = "";
        boolean exist = false;

        @Override
        protected void onPreExecute() {
            pbbarS.setVisibility(View.VISIBLE);
            recode=0;
            code =0;

        }

        @Override
        protected void onPostExecute(String s) {
            pbbarS.setVisibility(View.GONE);
            recode = code;
            codelast = memberCount +""+code;
            //code = memberCount+code;
            SendProductss sendProducts2 = new SendProductss();
            sendProducts2.execute("");
        }

        @Override
        protected String doInBackground(String... strings) {
            try {
                Connection con = connectionClass.CONN();
                if (con == null) {
                    w = "Error in connection with SQL server";
                } else {
                    String query = "select ISNULL(MAX(CONVERT(int,RECODE)),1000)+1 as RECODE from WAREHOUSEPLASIERDETAIL";
                    PreparedStatement ps = con.prepareStatement(query);
                    ResultSet rs = ps.executeQuery();

                    if (rs.next()) {
                        code = rs.getInt("RECODE");

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
    public class SendProductss extends AsyncTask<String, String, String> {
        String z = "";
        boolean hata;
        int i = 0;
        boolean deneme = true;

        @Override
        protected void onPreExecute() {
            pbbarS.setVisibility(View.VISIBLE);
            hata = false;

        }

        @Override
        protected void onPostExecute(String r) {
            pbbarS.setVisibility(View.GONE);
            if (deneme)
                Toast.makeText(getApplicationContext(), "AKTARILACAK ÜRÜN YOK!", Toast.LENGTH_SHORT).show();
            else {
                Toast.makeText(getApplicationContext(), "AKTARILDI!", Toast.LENGTH_SHORT).show();
                datalist.clear();
                recyclerview.setAdapter(null);
                firstAmount.clear();
                secondAmount.clear();
               /* Intent intent = new Intent(PlasiyerSatisThree.this,PlasiyerList.class);
                finish();
                startActivity(intent);*/
                finish();
                onBackPressed();
            }
            // if (hata) Toast.makeText(getApplicationContext(), "HATA!", Toast.LENGTH_SHORT).show();
        }

        @Override
        protected String doInBackground(String... params) {
            try {
                Connection con = connectionClass.CONN();
                if (con == null) {
                    z = "Error in connection with SQL server";
                } else {
                    if (!ExistArray.isEmpty()) {

                        for (int i = 0; i < datalist.size(); i++) {
                            if (datalist.get(i).isChecked()) {
                                if (NotExistArray.contains(datalist.get(i).getBarcodeid())) {
                                    UUID uuıd = UUID.randomUUID();
                                    String query = "insert into WAREHOUSEPLASIERDETAIL (ID,WAREHOUSEID,FIRSTAMOUNT,SECONDAMOUNT,ISKDV,KDVRATE,UNITPRICE,DATE,ISOKEY,BARCODEID,CODE,MEMBERID,RECODE,PALETID)" +
                                            "values('"+uuıd+"','"+incomingDepoId+"','"+firstAmount.get(i)+"','"+secondAmount.get(i)+"','"+kdvDahil+"','"+kdv+"','"+birimFiyat+"',GETDATE(),0,'"+datalist.get(i).getBarcodeid()+"','"+codelast+"','"+memberid+"','"+recode+"','"+datalist.get(i).getPaletid()+"')";

                                    PreparedStatement preparedStatement = con.prepareStatement(query);
                                    preparedStatement.executeUpdate();
                                    deneme = false;
                                }
                            }
                        }
                    }else{

                        for (int i = 0; i < datalist.size(); i++) {
                            if (datalist.get(i).isChecked()) {
                                UUID uuıd = UUID.randomUUID();
                                String query = "insert into WAREHOUSEPLASIERDETAIL (ID,WAREHOUSEID,FIRSTAMOUNT,SECONDAMOUNT,ISKDV,KDVRATE,UNITPRICE,DATE,ISOKEY,BARCODEID,CODE,MEMBERID,RECODE,PALETID)" +
                                        "values('"+uuıd+"','"+incomingDepoId+"','"+firstAmount.get(i)+"','"+secondAmount.get(i)+"','"+kdvDahil+"','"+kdv+"','"+birimFiyat+"',GETDATE(),0,'"+datalist.get(i).getBarcodeid()+"','"+codelast+"','"+memberid+"','"+recode+"','"+datalist.get(i).getPaletid()+"')";
                                PreparedStatement preparedStatement = con.prepareStatement(query);
                                preparedStatement.executeUpdate();
                                deneme = false;
                            }
                        }
                    }
                }
            } catch (Exception ex) {
                z = "Veri Çekme Hatası";
                hata = true;
                ex.printStackTrace();


            }
            return z;
        }
    }
}
