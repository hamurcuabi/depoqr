package com.emrehmrc.depoqr;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.ActivityOptions;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Vibrator;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.ContextThemeWrapper;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
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
import java.util.Calendar;
import java.util.Date;

import static com.emrehmrc.depoqr.AnaSayfa.MyPREFERENCES;

public class PlasiyerList extends AppCompatActivity implements PopupMenu.OnMenuItemClickListener {
    ActionBar ab;
    ConnectionClass connectionClass;
    SharedPreferences sharedPreferences;
    Bundle bundle;
    Vibrator vibrator;
    String memberid, comid;
    EditText cariArama;
    ListView lst_Cari;
    TextView tx_toplam;
    Button btn_satisbasla, btn_yenisatis;
    ProgressBar progressBar;
    ArrayAdapter<Depolar> adapterDepo;
    private PlasiyerListAdapter adapter;
    float toplam;
    ImageView btn_drop, btn_filtre;
    AutoCompleteTextView tx_deposec;
    String secilenDepoName, getSecilenDepoId;
    TextView btn_tarih;
    private DatePickerDialog.OnDateSetListener mDateSetListener;
    String date;
    String secilenPlasiyerCode,secilenPlasiyerId;
    ArrayList<String> silinecekArray = new ArrayList<>();
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_plasiyerlist);
        this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        vibrator = (Vibrator) getSystemService(VIBRATOR_SERVICE);
        sharedPreferences = getApplicationContext().getSharedPreferences(MyPREFERENCES, Context.MODE_PRIVATE);
        memberid = sharedPreferences.getString("ID", null);
        comid = sharedPreferences.getString("Companiesid", null);
        connectionClass = new ConnectionClass();
        ab = getSupportActionBar();
        ab.setTitle("Plasiyer Satiş");
        ab.setSubtitle("Satiş Listesi");
        ab.setBackgroundDrawable(getResources().getDrawable(R.drawable.arkaplan));
        progressBar = (ProgressBar) findViewById(R.id.pbbarP);
        btn_satisbasla = (Button) findViewById(R.id.btn_satisbasla);
        btn_yenisatis = (Button) findViewById(R.id.btn_yeniSatis);
        btn_satisbasla.setEnabled(false);
        tx_toplam = (TextView) findViewById(R.id.tx_toplam);
        cariArama = (EditText) findViewById(R.id.CariArama);
        lst_Cari = (ListView) findViewById(R.id.lst_Cari);
        btn_drop = (ImageView) findViewById(R.id.btn_drop);
        btn_filtre = (ImageView) findViewById(R.id.btn_filtre);
        tx_deposec = (AutoCompleteTextView) findViewById(R.id.tx_deposec);
        btn_tarih = (TextView) findViewById(R.id.btn_tarih);
        FillList2 fillList = new FillList2();
        fillList.execute("");
        FillListDepo fillListDepo = new FillListDepo();
        fillListDepo.execute("");
        btn_yenisatis.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(PlasiyerList.this, PlasiyerSatis.class);
                startActivityForResult(intent,1);
            }
        });
        btn_satisbasla.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String disable = "disable";
                Intent intent = new Intent(PlasiyerList.this, PlasiyerProduct.class);
                intent.putExtra("disable", disable);
                startActivityForResult(intent,1);
            }
        });
        btn_drop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                tx_deposec.showDropDown();
            }
        });
        btn_filtre.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (tx_deposec.getText().toString().isEmpty()) {
                    FillList2 fillList2 = new FillList2();
                    fillList2.execute("");
                } else {
                    if(tx_deposec.getText().toString().equals("Hepsi..")){
                        FillList2 fillList2 = new FillList2();
                        fillList2.execute("");
                    }else{
                        FillList fillList1 = new FillList();
                        fillList1.execute("");
                    }

                }

            }
        });
        btn_tarih.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Calendar cal = Calendar.getInstance();
                int year = cal.get(Calendar.YEAR);
                int month = cal.get(Calendar.MONTH);
                int day = cal.get(Calendar.DAY_OF_MONTH);

                DatePickerDialog dialog = new DatePickerDialog(PlasiyerList.this, AlertDialog.THEME_DEVICE_DEFAULT_LIGHT, mDateSetListener, year,month,day);
               // dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                dialog.show();
            }
        });
        mDateSetListener = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker datePicker, int year, int month, int day) {
                month = month + 1;
                date = day + "." + month + "." + year;
                if(month<10){
                    String str1 = String.format("%02d", month);
                    date = day + "." + str1 + "." + year;
                }
                if(day<10){
                    String str2 = String.format("%02d", day);
                    date = str2 + "." + month + "." + year;
                }
                btn_tarih.setText(date);
                if(!(lst_Cari==null)){
                    adapter.getFilter().filter(btn_tarih.getText().toString());
                }
            }
        };

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
            Intent i = new Intent(PlasiyerList.this, SliderMenu.class);
            bundle = ActivityOptions.makeSceneTransitionAnimation(PlasiyerList.this).toBundle();
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
                android.support.v7.app.AlertDialog.Builder builder2 = new android.support.v7.app.AlertDialog.Builder(PlasiyerList.this);
                builder2.setTitle("UYARI!");
                builder2.setMessage("Satışı silmek istediğinizden emin misiniz?");
                builder2.setNegativeButton("EVET", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        DeleteChild deleteChild = new DeleteChild();
                        deleteChild.execute("");
                    }
                });

                builder2.setPositiveButton("IPTAL", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {

                    }
                });

                builder2.show();


                return true;
            case R.id.menu_duzenle:
                Intent intent = new Intent(PlasiyerList.this, PlasiyerProductView.class);
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putString("secilenPlasiyerId", secilenPlasiyerId);
                editor.commit();
                startActivityForResult(intent,1);
                return true;
            default:
                return false;
        }
    }

    public class FillList extends AsyncTask<String, String, String> {
        String w = "";
        ArrayList<PlasiyerListModel> plasiyerArray = new ArrayList<PlasiyerListModel>();
        boolean exist = false;

        @Override
        protected void onPreExecute() {
            progressBar.setVisibility(View.VISIBLE);
            lst_Cari.setAdapter(null);
            plasiyerArray.clear();
            toplam=0;
        }

        @Override
        protected void onPostExecute(String s) {
            progressBar.setVisibility(View.GONE);
            if (exist) {
                adapter = new PlasiyerListAdapter(getApplicationContext(), plasiyerArray);
                lst_Cari.setAdapter(adapter);
                tx_toplam.setText("" + new DecimalFormat("##.##").format(toplam));
                lst_Cari.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        PlasiyerList.PlasiyerListModel plasiyerListModel;
                        plasiyerListModel = (PlasiyerListModel) lst_Cari.getItemAtPosition(position);
                        btn_satisbasla.setEnabled(true);
                    }
                });
                cariArama.addTextChangedListener(new TextWatcher() {
                    @Override
                    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                    }

                    @Override
                    public void onTextChanged(CharSequence s, int start, int before, int count) {
                        adapter.getFilter().filter(s.toString());
                    }

                    @Override
                    public void afterTextChanged(Editable s) {

                    }
                });
                lst_Cari.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
                    @Override
                    public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                        PlasiyerListModel plasiyerListModel;
                        plasiyerListModel = (PlasiyerListModel) lst_Cari.getItemAtPosition(position);
                        secilenPlasiyerCode = plasiyerListModel.getPlasiyercode();
                        secilenPlasiyerId = plasiyerListModel.getId();
                        Context wrapper = new ContextThemeWrapper(PlasiyerList.this, R.style.YOURSTYLE);
                        PopupMenu popup = new PopupMenu(wrapper, view);

                        popup.setOnMenuItemClickListener((PopupMenu.OnMenuItemClickListener) PlasiyerList.this);
                        popup.inflate(R.menu.poupup);
                        popup.show();
                        return true;
                    }
                });
            } else {
                Toast.makeText(getApplicationContext(), "Satış Bulunamadı.", Toast.LENGTH_LONG).show();
            }
        }

        @Override
        protected String doInBackground(String... strings) {
            try {
                Connection con = connectionClass.CONN();
                if (con == null) {
                    w = "Error in connection with SQL server";
                } else {
                    String query = "SELECT *,CONVERT(NVARCHAR(10),DATE,104) as DATE1 FROM VW_WAREHOUSEPLASIER where WAREHOUSENAME = '" + tx_deposec.getText().toString() + "' and COMPANIESID='"+comid+"' and ISDELETE = '0' order by DATE ";
                    PreparedStatement ps = con.prepareStatement(query);
                    ResultSet rs = ps.executeQuery();

                    while (rs.next()) {
                        exist = true;
                        toplam += rs.getFloat("TOTAL");
                        plasiyerArray.add(new PlasiyerListModel(rs.getString("ID"),rs.getString("PLASIERCODE"),
                                rs.getString("DATE1"),
                                rs.getString("CURRENTNAME"),
                                rs.getFloat("TOTALPRICE"),
                                rs.getFloat("TOTALKDV"),
                                rs.getFloat("TOTAL"),
                                rs.getString("WAREHOUSENAME")));
                    }
                    w = "Başarılı";
                }
            } catch (Exception ex) {
                w = "Veri Çekme Hatası";

            }
            return w;
        }
    }

    public class FillList2 extends AsyncTask<String, String, String> {
        String w = "";
        ArrayList<PlasiyerListModel> plasiyerArray = new ArrayList<PlasiyerListModel>();
        boolean exist = false;

        @Override
        protected void onPreExecute() {
            progressBar.setVisibility(View.VISIBLE);
            lst_Cari.setAdapter(null);
            plasiyerArray.clear();
            toplam=0;
        }

        @Override
        protected void onPostExecute(String s) {
            progressBar.setVisibility(View.GONE);
            if (exist) {
                adapter = new PlasiyerListAdapter(getApplicationContext(), plasiyerArray);
                lst_Cari.setAdapter(adapter);
                tx_toplam.setText("" + new DecimalFormat("##.##").format(toplam));
                lst_Cari.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        PlasiyerList.PlasiyerListModel plasiyerListModel;
                        plasiyerListModel = (PlasiyerListModel) lst_Cari.getItemAtPosition(position);
                        btn_satisbasla.setEnabled(true);
                    }
                });
                cariArama.addTextChangedListener(new TextWatcher() {
                    @Override
                    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                    }

                    @Override
                    public void onTextChanged(CharSequence s, int start, int before, int count) {
                        adapter.getFilter().filter(s.toString());
                    }

                    @Override
                    public void afterTextChanged(Editable s) {

                    }
                });
                lst_Cari.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
                    @Override
                    public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                        PlasiyerListModel plasiyerListModel;
                        plasiyerListModel = (PlasiyerListModel) lst_Cari.getItemAtPosition(position);
                        secilenPlasiyerCode = plasiyerListModel.getPlasiyercode();
                        secilenPlasiyerId = plasiyerListModel.getId();
                        Context wrapper = new ContextThemeWrapper(PlasiyerList.this, R.style.YOURSTYLE);
                        PopupMenu popup = new PopupMenu(wrapper, view);

                        popup.setOnMenuItemClickListener((PopupMenu.OnMenuItemClickListener) PlasiyerList.this);
                        popup.inflate(R.menu.poupup);
                        popup.show();
                        return true;
                    }
                });
            } else {
                Toast.makeText(getApplicationContext(), "Satış Bulunamadı.", Toast.LENGTH_LONG).show();
            }

        }

        @Override
        protected String doInBackground(String... strings) {
            try {
                Connection con = connectionClass.CONN();
                if (con == null) {
                    w = "Error in connection with SQL server";
                } else {
                    String query = "SELECT *,CONVERT(NVARCHAR(10),DATE,104) as DATE1 FROM VW_WAREHOUSEPLASIER where COMPANIESID='"+comid+"' and ISDELETE = '0' order by DATE";
                    PreparedStatement ps = con.prepareStatement(query);
                    ResultSet rs = ps.executeQuery();

                    while (rs.next()) {
                        exist = true;
                        toplam += rs.getFloat("TOTAL");
                        plasiyerArray.add(new PlasiyerListModel(rs.getString("ID"),rs.getString("PLASIERCODE"),
                                rs.getString("DATE1"),
                                rs.getString("CURRENTNAME"),
                                rs.getFloat("TOTALPRICE"),
                                rs.getFloat("TOTALKDV"),
                                rs.getFloat("TOTAL"),
                                rs.getString("WAREHOUSENAME")));
                    }
                    w = "Başarılı";
                }
            } catch (Exception ex) {
                w = "Veri Çekme Hatası";

            }
            return w;
        }
    }

    public static class PlasiyerListModel {
        String id;
        String plasiyercode;
        String cariTarih;
        String cariAdi;
        float toplamTutar;
        float kdv;
        float genelTutar;
        String depoName;

        public PlasiyerListModel(String id, String plasiyercode, String cariTarih, String cariAdi, float toplamTutar, float kdv, float genelTutar, String depoName) {
            this.id = id;
            this.plasiyercode = plasiyercode;
            this.cariTarih = cariTarih;
            this.cariAdi = cariAdi;
            this.toplamTutar = toplamTutar;
            this.kdv = kdv;
            this.genelTutar = genelTutar;
            this.depoName = depoName;
        }

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public String getPlasiyercode() {
            return plasiyercode;
        }

        public void setPlasiyercode(String plasiyercode) {
            this.plasiyercode = plasiyercode;
        }

        public String getDepoName() {
            return depoName;
        }

        public void setDepoName(String depoName) {
            this.depoName = depoName;
        }

        public String getCariTarih() {
            return cariTarih;
        }

        public void setCariTarih(String cariTarih) {
            this.cariTarih = cariTarih;
        }

        public String getCariAdi() {
            return cariAdi;
        }

        public void setCariAdi(String cariAdi) {
            this.cariAdi = cariAdi;
        }

        public float getToplamTutar() {
            return toplamTutar;
        }

        public void setToplamTutar(float toplamTutar) {
            this.toplamTutar = toplamTutar;
        }

        public float getKdv() {
            return kdv;
        }

        public void setKdv(float kdv) {
            this.kdv = kdv;
        }

        public float getGenelTutar() {
            return genelTutar;
        }

        public void setGenelTutar(float genelTutar) {
            this.genelTutar = genelTutar;
        }
    }

    @SuppressLint("NewApi")
    public class FillListDepo extends AsyncTask<String, String, String> {
        String z = "";
        ArrayList<Depolar> depolars = new ArrayList<>();

        @Override
        protected void onPreExecute() {
            depolars.add(new Depolar("Hepsi..","Hepsi.."));

        }

        @Override
        protected void onPostExecute(String r) {

            adapterDepo = new ArrayAdapter<Depolar>(getApplicationContext(), android.R.layout.simple_dropdown_item_1line, depolars);
            tx_deposec.setAdapter(adapterDepo);
            tx_deposec.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                    Depolar depolar = (Depolar) parent.getItemAtPosition(position);
                    secilenDepoName = depolar.getDepoadi();
                    getSecilenDepoId = depolar.getDepono();
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
                            "VW_WAREHOUSEPERMISSION where MEMBERID='" + memberid + "' and ISACTIVE='1' and " +
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
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == 1) {
            FillList2 fillList = new FillList2();
            fillList.execute("");
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
            if(silinecekArray.isEmpty()){
                DeleteParent deleteParent = new DeleteParent();
                deleteParent.execute("");
            }else{
                DeleteChild2 deletePro = new DeleteChild2();
                deletePro.execute("");
            }

        }

        @Override
        protected String doInBackground(String... params) {

            try {
                Connection con = connectionClass.CONN();
                if (con == null) {
                    z = "Error in connection with SQL server";
                } else {
                    String query = "select ID from WAREHOUSEPLASIERDETAIL where WAREHOUSEPLASIERID='"+secilenPlasiyerId+"'";
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

                DeleteParent fillList = new DeleteParent();
                fillList.execute("");



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
    @RequiresApi(api = Build.VERSION_CODES.CUPCAKE)
    public class DeleteParent extends AsyncTask<String, String, String> {


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
                Toast.makeText(PlasiyerList.this, "Silindi", Toast.LENGTH_SHORT).show();
                FillList2 fillList = new FillList2();
                fillList.execute("");
            }else{
                Toast.makeText(PlasiyerList.this, "Hata", Toast.LENGTH_SHORT).show();

            }
        }

        @Override
        protected String doInBackground(String... params) {

            try {
                Connection con = connectionClass.CONN();
                if (con == null) {
                    z = "Error in connection with SQL server";
                } else {
                        String query = "UPDATE WAREHOUSEPLASIER set ISDELETE = '1' where ID ='"+secilenPlasiyerId+"' ";
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
}
