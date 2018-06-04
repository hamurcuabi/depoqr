package com.emrehmrc.depoqr.popup;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.emrehmrc.depoqr.AnaSayfa;
import com.emrehmrc.depoqr.connection.ConnectionClass;
import com.emrehmrc.depoqr.R;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.util.ArrayList;
import java.util.UUID;

/**
 * Created by Emre Hmrc on 9.11.2017.
 */

public class Popup extends Activity {

    EditText edtMiktarpop, edtAgirlikpop, edtKodpop, edtNamepop;
    Button btnaddpop, btncancelpop;
    String ID,MID;
    String edtM;
    String edtN;
    String edtK;
    String edtA;
    ConnectionClass connectionClass;



    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.popup);
        connectionClass = new ConnectionClass();


        edtAgirlikpop = (EditText) findViewById(R.id.edtAgirlikpop);
        edtMiktarpop = (EditText) findViewById(R.id.edtMiktarpop);
        edtKodpop = (EditText) findViewById(R.id.edtKodpop);
        edtNamepop = (EditText) findViewById(R.id.edtNamepop);
        btnaddpop = (Button) findViewById(R.id.btnaddpop);
        btncancelpop = (Button) findViewById(R.id.btncancelpop);

        ArrayList<String> myList = (ArrayList<String>) getIntent().getSerializableExtra("mylist");
        edtNamepop.setText(myList.get(1));
        edtKodpop.setText(myList.get(0));
        ID = myList.get(2);

        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);

        int width = dm.widthPixels;
        int height = dm.heightPixels;
        getWindow().setLayout((int) (width * .8), (int) (height * .4));


        SharedPreferences sharedpreferences = getSharedPreferences(AnaSayfa.MyPREFERENCES, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedpreferences.edit();
        MID=sharedpreferences.getString("ID", null);

        btnaddpop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                edtM = edtMiktarpop.getText().toString();
                edtA = edtAgirlikpop.getText().toString();
                edtK = edtKodpop.getText().toString();
                edtN = edtNamepop.getText().toString();
                AddPro add = new AddPro();
                if (add.doInBackground("") == "Başarıyla Eklendi!") {
                    Toast.makeText(Popup.this, "Başarıyla Eklendi!", Toast.LENGTH_SHORT).show();
                    finish();
                }
                else {
                    Toast.makeText(Popup.this, "MİKTAR VE AĞIRLIK GİRİNİZ!", Toast.LENGTH_SHORT).show();
                }


            }


        });
        btncancelpop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
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

            Toast.makeText(Popup.this, r, Toast.LENGTH_SHORT).show();


        }

        @Override
        protected String doInBackground(String... params) {
            if (edtA.trim().equals("") || edtM.trim().equals(""))
                z = "Lütfen Miktar ve Ağırlık Giriniz!";
            else {
                try {
                    Connection con = connectionClass.CONN();
                    if (con == null) {
                        z = "Bağlantı Hatası";
                    } else {

                        UUID id = UUID.randomUUID();
                        String query = "insert into BARCODE (ID,PRODUCTID,MEMBERID,WEIGHT,AMOUNT) values ('" + id + "','" + ID + "','" +MID + "','" + edtA + "','" + edtM + "')";
                        PreparedStatement preparedStatement = con.prepareStatement(query);
                        preparedStatement.executeUpdate();
                        z = "Başarıyla Eklendi!";
                        isSuccess = true;

                    }

                } catch (Exception ex) {
                    isSuccess = false;
                    z = "SQL Hatası!!";
                }
            }

            return z;
        }
    }
}
