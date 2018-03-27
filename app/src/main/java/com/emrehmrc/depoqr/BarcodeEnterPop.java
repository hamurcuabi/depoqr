package com.emrehmrc.depoqr;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.DisplayMetrics;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import static com.emrehmrc.depoqr.AnaSayfa.MyPREFERENCES;

/**
 * Created by Emre Hmrc on 24.01.2018.
 */

public class BarcodeEnterPop extends Activity {

    Button btnstandart;
    EditText edtBarcode;
    SharedPreferences sharedpreferences;



    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.barcodeenterpop);
        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);

        sharedpreferences = getApplicationContext().getSharedPreferences(MyPREFERENCES, Context.MODE_PRIVATE);
        int width = dm.widthPixels;
        int height = dm.heightPixels;
        getWindow().setLayout((int) (width * .8), (int) (height * .2));

        edtBarcode=(EditText)findViewById(R.id.edtCode);
        edtBarcode.setOnTouchListener(new View.OnTouchListener() {

            @Override
            public boolean onTouch(View v, MotionEvent event) {

                v.setFocusable(true);
                v.setFocusableInTouchMode(true);
                return false;
            }
        });
        btnstandart = (Button) findViewById(R.id.btnstandart);
        final SharedPreferences.Editor editor = sharedpreferences.edit();
        btnstandart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Barcode Girilince

                finish();
            }
        });

        edtBarcode.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

                try {

                    editor.putString("BarcodeCodeEnter",edtBarcode.getText().toString());
                    editor.commit();

                } catch (Exception ex) {

                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

    }
}
