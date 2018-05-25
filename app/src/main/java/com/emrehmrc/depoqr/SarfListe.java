package com.emrehmrc.depoqr;

import android.annotation.TargetApi;
import android.app.ActivityOptions;
import android.app.DatePickerDialog;
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
import android.text.Editable;
import android.text.TextWatcher;
import android.view.ContextThemeWrapper;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.ListView;
import android.widget.PopupMenu;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Calendar;

import static com.emrehmrc.depoqr.AnaSayfa.MyPREFERENCES;

public class SarfListe extends AppCompatActivity implements PopupMenu.OnMenuItemClickListener {
    ActionBar ab;
    ConnectionClass connectionClass;
    SharedPreferences sharedPreferences;
    Bundle bundle;
    Vibrator vibrator;
    String memberid, comid;
    ProgressBar pbbarP;
    ListView lst_sarf;
    SarfAdapter adapter;
    String secilenSarf;
    ArrayList<String> silinecekArray = new ArrayList<String>();
    Button btn_ekle;
    TextView btn_tarih;
    private DatePickerDialog.OnDateSetListener mDateSetListener;
    String date;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sarf_liste);
        this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        vibrator = (Vibrator) getSystemService(VIBRATOR_SERVICE);
        sharedPreferences = getApplicationContext().getSharedPreferences(MyPREFERENCES, Context.MODE_PRIVATE);
        memberid = sharedPreferences.getString("ID", null);
        comid = sharedPreferences.getString("Companiesid", null);
        connectionClass = new ConnectionClass();
        ab = getSupportActionBar();
        ab.setTitle("Sarf Listesi");
        // ab.setSubtitle("Satiş Listesi");
        ab.setBackgroundDrawable(getResources().getDrawable(R.drawable.arkaplan));
        pbbarP = (ProgressBar) findViewById(R.id.pbbarP);
        lst_sarf = (ListView) findViewById(R.id.lst_Cari);
        btn_ekle = (Button) findViewById(R.id.btn_yeniSatis);
        btn_tarih = (TextView) findViewById(R.id.btn_tarih);
        FillList fillList = new FillList();
        fillList.execute("");

        btn_ekle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SarfListe.this, Sarf.class);
                startActivityForResult(intent,1);
            }
        });
        btn_tarih.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Calendar cal = Calendar.getInstance();
                int year = cal.get(Calendar.YEAR);
                int month = cal.get(Calendar.MONTH);
                int day = cal.get(Calendar.DAY_OF_MONTH);

                DatePickerDialog dialog = new DatePickerDialog(SarfListe.this, android.app.AlertDialog.THEME_DEVICE_DEFAULT_LIGHT, mDateSetListener, year,month,day);
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
                if(!(lst_sarf==null)){
                    adapter.getFilter().filter(btn_tarih.getText().toString());
                }
            }
        };
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == 1) {
            FillList fillList = new FillList();
            fillList.execute("");
        }
    }

    public class FillList extends AsyncTask<String, String, String> {
        String w = "";
        ArrayList<SarfAnaListeModel> plasiyerArray = new ArrayList<SarfAnaListeModel>();
        boolean exist = false;

        @Override
        protected void onPreExecute() {
            pbbarP.setVisibility(View.VISIBLE);
            lst_sarf.setAdapter(null);
        }

        @Override
        protected void onPostExecute(String s) {
            pbbarP.setVisibility(View.GONE);
            if (exist) {
                adapter = new SarfAdapter(getApplicationContext(), plasiyerArray);
                lst_sarf.setAdapter(adapter);
                lst_sarf.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
                    @Override
                    public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                        SarfAnaListeModel plasiyerListModel;
                        plasiyerListModel = (SarfAnaListeModel) lst_sarf.getItemAtPosition(position);
                        secilenSarf = plasiyerListModel.getSarfNo();
                        Context wrapper = new ContextThemeWrapper(SarfListe.this, R.style.YOURSTYLE);
                        PopupMenu popup = new PopupMenu(wrapper, view);

                        popup.setOnMenuItemClickListener((PopupMenu.OnMenuItemClickListener) SarfListe.this);
                        popup.inflate(R.menu.poupup);
                        popup.show();
                        return true;
                    }
                });
            } else {
                Toast.makeText(getApplicationContext(), "Bulunamadı.", Toast.LENGTH_LONG).show();
            }
        }

        @Override
        protected String doInBackground(String... strings) {
            try {
                Connection con = connectionClass.CONN();
                if (con == null) {
                    w = "Error in connection with SQL server";
                } else {
                    String query = " select CODE,MEMBEREMPLOYEENAME,MEMBERNAME,CURRENTNAME,CONVERT(nvarchar(10),DATE,104) AS DATE from VW_CONSUMPTION where COMPANIESID = '" + comid + "' GROUP BY CODE,MEMBEREMPLOYEENAME,MEMBERNAME,CURRENTNAME,CONVERT(nvarchar(10),DATE,104)";
                    PreparedStatement ps = con.prepareStatement(query);
                    ResultSet rs = ps.executeQuery();

                    while (rs.next()) {
                        exist = true;
                        String deneme = rs.getString("MEMBERNAME");
                        String deneme2 = rs.getString("CURRENTNAME");
                        if (deneme == null) {
                            plasiyerArray.add(new SarfAnaListeModel(rs.getString("CODE"), rs.getString("MEMBEREMPLOYEENAME"), rs.getString("CURRENTNAME"), rs.getString("DATE")));
                        } else if (deneme2 == null)  {
                            plasiyerArray.add(new SarfAnaListeModel(rs.getString("CODE"), rs.getString("MEMBEREMPLOYEENAME"), rs.getString("MEMBERNAME"), rs.getString("DATE")));
                        }else{
                            plasiyerArray.add(new SarfAnaListeModel(rs.getString("CODE"), rs.getString("MEMBEREMPLOYEENAME"), "YOKTUR", rs.getString("DATE")));
                        }
                    }
                    w = "Başarılı";
                }
            } catch (Exception ex) {
                w = "Veri Çekme Hatası";

            }
            return w;
        }
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
            Intent i = new Intent(SarfListe.this, SliderMenu.class);
            bundle = ActivityOptions.makeSceneTransitionAnimation(SarfListe.this).toBundle();
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

    public static class SarfAnaListeModel {
        String sarfNo;
        String userName;
        String currentName;
        String date;

        public SarfAnaListeModel(String sarfNo, String userName, String currentName, String date) {
            this.sarfNo = sarfNo;
            this.userName = userName;
            this.currentName = currentName;
            this.date = date;
        }

        public String getSarfNo() {
            return sarfNo;
        }

        public void setSarfNo(String sarfNo) {
            this.sarfNo = sarfNo;
        }

        public String getUserName() {
            return userName;
        }

        public void setUserName(String userName) {
            this.userName = userName;
        }

        public String getCurrentName() {
            return currentName;
        }

        public void setCurrentName(String currentName) {
            this.currentName = currentName;
        }

        public String getDate() {
            return date;
        }

        public void setDate(String date) {
            this.date = date;
        }
    }

    @Override
    public boolean onMenuItemClick(MenuItem menuItem) {
        switch (menuItem.getItemId()) {
            case R.id.menu_Sil:
                Silenecekler silenecekler = new Silenecekler();
                silenecekler.execute("");
                return true;
            case R.id.menu_duzenle:
                Intent intent = new Intent(SarfListe.this,SarfDuzenle.class);
                intent.putExtra("secilenSarf",secilenSarf);
                startActivityForResult(intent,1);
                return true;
            default:
                return false;
        }
    }

    public class Silenecekler extends AsyncTask<String, String, String> {
        String w = "";
        boolean exist = false;

        @Override
        protected void onPreExecute() {
            pbbarP.setVisibility(View.VISIBLE);
            silinecekArray.clear();
        }

        @Override
        protected void onPostExecute(String s) {
            pbbarP.setVisibility(View.GONE);
            if (exist) {
                AlertDialog.Builder builder2 = new AlertDialog.Builder(SarfListe.this);
                builder2.setTitle("UYARI!");
                builder2.setMessage("Sevkiyatı tamamlamak istediğinizden emin misiniz?");
                builder2.setNegativeButton("EVET", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        DeletePro deletePro = new DeletePro();
                        deletePro.execute("");
                    }
                });

                builder2.setPositiveButton("HAYIR", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {

                    }
                });

                builder2.show();

            } else {
                Toast.makeText(getApplicationContext(), "HATA.", Toast.LENGTH_LONG).show();
            }
        }

        @Override
        protected String doInBackground(String... strings) {
            try {
                Connection con = connectionClass.CONN();
                if (con == null) {
                    w = "Error in connection with SQL server";
                } else {
                    String query = "select ID from VW_CONSUMPTION where COMPANIESID = '" + comid + "' and CODE = '" + secilenSarf + "' ";
                    PreparedStatement ps = con.prepareStatement(query);
                    ResultSet rs = ps.executeQuery();

                    while (rs.next()) {
                        silinecekArray.add(rs.getString("ID"));
                        exist=true;
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
            pbbarP.setVisibility(View.VISIBLE);
        }

        @Override
        protected void onPostExecute(String r) {
            pbbarP.setVisibility(View.GONE);
            if (isSuccess) {
                Toast.makeText(SarfListe.this, "BAŞARIYLA SİLİNDİ", Toast.LENGTH_SHORT).show();
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
                    for (int j = 0; j < silinecekArray.size(); j++) {
                        String query = "Delete  from CONSUMPTION where ID = '" + silinecekArray.get(j) + "'";
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

}
