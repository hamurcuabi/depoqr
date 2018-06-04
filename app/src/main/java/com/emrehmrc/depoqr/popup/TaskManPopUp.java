package com.emrehmrc.depoqr.popup;

import android.annotation.TargetApi;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.DisplayMetrics;

import com.emrehmrc.depoqr.R;
import com.emrehmrc.depoqr.adapter.TaskManPopUpAdapter;
import com.emrehmrc.depoqr.connection.ConnectionClass;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;

public class TaskManPopUp extends AppCompatActivity {

    RecyclerView recyclerView;
    TaskManPopUpAdapter adapter;
    ArrayList<String> datalist;
    String id="";
    ConnectionClass connectionClass;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_task_man_pop_up);
        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);
        Intent intent = getIntent();
         id = intent.getExtras().getString("id");
        connectionClass = new ConnectionClass();

        int width = dm.widthPixels;
        int height = dm.heightPixels;
        getWindow().setLayout((int) (width * .8), (int) (height * .6));
        recyclerView = (RecyclerView) findViewById(R.id.popuptaskmans);
        datalist = new ArrayList<>();
        MainTasks mainTasks= new MainTasks();
        mainTasks.execute(id);


    }
    @RequiresApi(api = Build.VERSION_CODES.CUPCAKE)
    public class MainTasks extends AsyncTask<String, String, String> {
        String z = "";
        Boolean isSuccess = false;

        @Override
        protected void onPreExecute() {

            datalist = new ArrayList<>();


        }

        @TargetApi(Build.VERSION_CODES.LOLLIPOP)
        @Override
        protected void onPostExecute(String r) {

            adapter = new TaskManPopUpAdapter(getApplicationContext(), datalist);
            recyclerView.setAdapter(adapter);
            LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getApplicationContext());
            linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
            recyclerView.setLayoutManager(linearLayoutManager);

        }

        @Override
        protected String doInBackground(String... params) {

            try {

                Connection con = connectionClass.CONN("ArGeMerkezi");
                if (con == null) {
                    z = "Bağlantı Hatası";
                } else {


                    String query = "select MEMBERFULLNAME from VW_TASKMEMBER where " +
                            "ID='"+params[0]+"'";
                    Statement stmt = con.createStatement();
                    ResultSet rs = stmt.executeQuery(query);

                    while (rs.next()) {

                        String  gecici=(rs.getString("MEMBERFULLNAME"));
                        datalist.add(gecici);
                        isSuccess = true;
                    }


                }
            } catch (Exception ex) {
                isSuccess = false;
                z = "Hata";
            }

            return z;
        }
    }
}
