package com.emrehmrc.depoqr.popup;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.emrehmrc.depoqr.R;

import static com.emrehmrc.depoqr.AnaSayfa.MyPREFERENCES;

/**
 * Created by Emre Hmrc on 24.11.2017.
 */

public class EtiketTercih extends Activity {

    Button btnstandart, btntozel;
    SharedPreferences sharedpreferences;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_etiket_tercih);
        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);

        int width = dm.widthPixels;
        int height = dm.heightPixels;
        getWindow().setLayout((int) (width * .8), (int) (height * .4));

        sharedpreferences = getApplicationContext().getSharedPreferences(MyPREFERENCES, Context.MODE_PRIVATE);


        btnstandart = (Button) findViewById(R.id.btnstandart);
        btntozel = (Button) findViewById(R.id.btnozel);
        btnstandart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //standart
                SharedPreferences.Editor  editor = sharedpreferences.edit();
                editor.putString("StandartEtiket","0");
                editor.commit();
                Toast.makeText(EtiketTercih.this,"SEÇİM YAPILDI!",Toast.LENGTH_SHORT).show();
                finish();
            }
        });
        btntozel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //ozell
                SharedPreferences.Editor editor = sharedpreferences.edit();
                editor.putString("StandartEtiket", "1");
                editor.commit();
                Toast.makeText(EtiketTercih.this,"SEÇİM YAPILDI!",Toast.LENGTH_SHORT).show();
                finish();
            }
        });
    }
}
