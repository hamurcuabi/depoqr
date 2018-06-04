package com.emrehmrc.depoqr;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.ActivityOptions;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.AudioManager;
import android.media.ToneGenerator;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Vibrator;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;

import com.emrehmrc.depoqr.adapter.CariAdapter;
import com.emrehmrc.depoqr.adapter.PersonalsAdapter;
import com.emrehmrc.depoqr.connection.ConnectionClass;
import com.emrehmrc.depoqr.model.ModelCari;
import com.emrehmrc.depoqr.model.ModelPersonals;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;

import static com.emrehmrc.depoqr.AnaSayfa.MyPREFERENCES;

public class Sarf extends AppCompatActivity {
    ActionBar ab;
    ConnectionClass connectionClass;
    SharedPreferences sharedPreferences;
    Bundle bundle;
    Vibrator vibrator;
    ToneGenerator toneG;
    String memberid, comid;
    Spinner depo,spnPB;
    String secilendepoId,secilenad,secilenId,secaciklama,secilenTibi ="else";
    EditText carisec,aciklama;
    private CariAdapter adapter;
    private PersonalsAdapter adapter2;
    Button moveto2;
    ListView newlist;
    TextView secilen2,fkod,fadi;
    View line;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sarf);
        this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);
        vibrator = (Vibrator) getSystemService(VIBRATOR_SERVICE);
        sharedPreferences = getApplicationContext().getSharedPreferences(MyPREFERENCES, Context.MODE_PRIVATE);
        memberid = sharedPreferences.getString("ID", null);
        comid = sharedPreferences.getString("Companiesid", null);
        connectionClass = new ConnectionClass();
        ab = getSupportActionBar();
        ab.setTitle("SARF");
        //ab.setSubtitle("Etiket Okut");
        ab.setBackgroundDrawable(getResources().getDrawable(R.drawable.arkaplan));
        toneG = new ToneGenerator(AudioManager.STREAM_ALARM, 100);
        secilen2 = (TextView) findViewById(R.id.secilen2);
        carisec=(EditText) findViewById(R.id.carisec);
        depo = (Spinner) findViewById(R.id.depoSec);
        spnPB = (Spinner) findViewById(R.id.spnPB);
        newlist = (ListView) findViewById(R.id.lstcari);
        fkod =  (TextView) findViewById(R.id.fkod);
        line = (View) findViewById(R.id.line);
        fadi = (TextView) findViewById(R.id.fadi);
        moveto2 = (Button) findViewById(R.id.moveto2);
        aciklama = (EditText) findViewById(R.id.aciklama);

        spnPB.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                switch (position) {
                    case 0:
                        carisec.setHint("CARI ARA...");
                        fadi.setVisibility(View.VISIBLE);
                        line.setVisibility(View.VISIBLE);
                        FillCari fillCari = new FillCari();
                        fillCari.execute("");

                        break;
                    case 1:
                        carisec.setHint("PEROSONAL ARA...");
                        fadi.setVisibility(View.GONE);
                        line.setVisibility(View.GONE);
                        FillPersonel fillPersonel = new FillPersonel();
                        fillPersonel.execute("");

                        break;
                    default:
                        ;
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }

        });
        FillList fillList = new FillList();
        fillList.execute("");
        moveto2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Sarf.this, SarfSec.class);
                intent.putExtra("secilendepoId", secilendepoId);
                intent.putExtra("secilenId", secilenId);
                intent.putExtra("aciklama", secaciklama);
                intent.putExtra("secilenTibi", secilenTibi);
                finish();
                startActivity(intent);
            }
        });
        aciklama.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openDialog();
            }
        });
    }



    @SuppressLint("NewApi")
    public class FillList extends AsyncTask<String, String, String> {
        String z = "";
        ArrayList<Depolar> depolars = new ArrayList<>();

        @Override
        protected void onPreExecute() {


        }

        @Override
        protected void onPostExecute(String r) {

            ArrayAdapter<Depolar> adapter = new ArrayAdapter<Depolar>(getApplicationContext(), R.layout.support_simple_spinner_dropdown_item, depolars);
            adapter.setDropDownViewResource(R.layout.support_simple_spinner_dropdown_item);
            depo.setAdapter(adapter);
            depo.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                    ((TextView) parent.getChildAt(0)).setGravity(Gravity.CENTER);
                    ((TextView) parent.getChildAt(0)).setTextColor(getResources().getColor(R.color.white));
                    Depolar depolar1;
                    depolar1 = (Depolar) depo.getItemAtPosition(position);
                    secilendepoId = depolar1.getDepono();

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

    public class FillCari extends AsyncTask<String, String, String> {
        String w = "";
        ArrayList<ModelCari> carilers = new ArrayList<>();

        @Override
        protected void onPreExecute() {

        }

        @Override
        protected void onPostExecute(String s) {
            adapter = new CariAdapter(getApplicationContext(), carilers);
            newlist.setAdapter(adapter);
            carisec.addTextChangedListener(new TextWatcher() {
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
            newlist.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                    ModelCari modelCari = new ModelCari();
                    modelCari = (ModelCari) newlist.getItemAtPosition(position);
                    secilenad = modelCari.getCariadi();
                    secilenId = modelCari.getCariId();
                    secilen2.setText(secilenad);
                    secilenTibi = "cari";
                }
            });
        }

        @Override
        protected String doInBackground(String... strings) {

            try {
                Connection con = connectionClass.CONN();
                if (con == null) {
                    w = "Error in connection with SQL server";
                } else {
                    String query = "SELECT CODE , NAME , ID FROM " +
                            "VW_CURRENTDETAIL where COMPANIESID='" + comid + "'";
                    PreparedStatement ps = con.prepareStatement(query);
                    ResultSet rs = ps.executeQuery();

                    while (rs.next()) {
                        carilers.add(new ModelCari(rs.getString
                                ("NAME"), rs.getString("CODE"), rs.getString("ID")));
                    }
                    w = "Başarılı";
                }
            } catch (Exception ex) {
                w = "Veri Çekme Hatası";

            }
            return w;
        }
    }


    public class FillPersonel extends AsyncTask<String, String, String> {
        String w = "";
        ArrayList<ModelPersonals> personalsArrayList = new ArrayList<>();

        @Override
        protected void onPreExecute() {

        }

        @Override
        protected void onPostExecute(String s) {
            adapter2 = new PersonalsAdapter(getApplicationContext(), personalsArrayList);
            newlist.setAdapter(adapter2);
            carisec.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {

                    adapter2.getFilter().filter(s.toString());
                }

                @Override
                public void afterTextChanged(Editable s) {


                }
            });
            newlist.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                    ModelPersonals modelCari2;
                    modelCari2 = (ModelPersonals) newlist.getItemAtPosition(position);
                    secilenad = modelCari2.getPersonalName();
                    secilenId = modelCari2.getPersonalId();
                    secilen2.setText(secilenad);
                    secilenTibi = "personel";
                }
            });
        }

        @Override
        protected String doInBackground(String... strings) {

            try {
                Connection con = connectionClass.CONN();
                if (con == null) {
                    w = "Error in connection with SQL server";
                } else {
                    String query = "SELECT NAME ,SURNAME, ID from MEMBER where COMPANIESID = '"+comid+"' and ISACTIVE = '1' and ISDELETE = '0'";
                    PreparedStatement ps = con.prepareStatement(query);
                    ResultSet rs = ps.executeQuery();

                    while (rs.next()) {
                        personalsArrayList.add(new ModelPersonals(rs.getString
                                ("ID"), rs.getString("NAME")+"  "+rs.getString("SURNAME")));
                    }
                    w = "Başarılı";
                }
            } catch (Exception ex) {
                w = "Veri Çekme Hatası";

            }
            return w;
        }
    }

    private void openDialog(){
        LayoutInflater inflater = LayoutInflater.from(Sarf.this);
        View subView = inflater.inflate(R.layout.dialog_sarf, null);
        final EditText subEditText = (EditText)subView.findViewById(R.id.et2);
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("AÇIKLAMA");
        builder.setMessage("ZORUNLU DEĞIL");
        builder.setView(subView);
        AlertDialog alertDialog = builder.create();

        builder.setPositiveButton("Kaydet", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if(!subEditText.getText().toString().isEmpty()){
                    secaciklama=subEditText.getText().toString();
                    aciklama.setText(secaciklama);
                   // aciklama.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.yes, 0);
                  //  aciklama.setBackgroundResource(R.drawable.deneme);
                }else{
                    aciklama.setText("Açıklama yazınız..");
                   // aciklama.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.pen, 0);
                   // aciklama.setBackgroundResource(R.drawable.deneme3);
                }

            }
        });

        builder.setNegativeButton("Iptal", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
            }
        });

        builder.show();
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
    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.plasiyersatis, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.anasayfa) {
            finish();
            Intent i = new Intent(Sarf.this, SliderMenu.class);
            bundle = ActivityOptions.makeSceneTransitionAnimation(Sarf.this).toBundle();
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
}
