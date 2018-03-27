package com.emrehmrc.depoqr;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.journeyapps.barcodescanner.BarcodeEncoder;

import java.io.File;
import java.io.FileOutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;

public class ProductEtiket extends AppCompatActivity {


    ImageView imgQr, imgBarcode;
    TextView txtAd, txtMiktar, txtAgirlik, txtDate;
    Bitmap bitmap;
    String text2Qr;
    LinearLayout lnrSs;
    ActionBar ab;
    //
    LinearLayout lnrSs2;
    String standartetiket;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.etiketdefault);


        SharedPreferences sharedpreferences = getSharedPreferences(AnaSayfa.MyPREFERENCES, Context.MODE_PRIVATE);
        standartetiket = sharedpreferences.getString("StandartEtiket","0");


        ArrayList<String> myList = (ArrayList<String>) getIntent().getSerializableExtra("mylist");

        ab = getSupportActionBar();
        ab.setTitle("ETİKET YAZDIR");
        ab.setSubtitle("YAZDIR");
        ab.setBackgroundDrawable(getResources().getDrawable(R.drawable.arkaplan));
        lnrSs = (LinearLayout) findViewById(R.id.lnrSs);
        lnrSs2 = (LinearLayout) findViewById(R.id.lnrSs2);

        if (standartetiket.trim().equals("0")) {
            setContentView(R.layout.etiket2);
            imgQr = (ImageView) findViewById(R.id.imgQR);
            imgBarcode = (ImageView) findViewById(R.id.imgBar);

            txtAd = (TextView) findViewById(R.id.txtAd);
            txtAgirlik = (TextView) findViewById(R.id.txtAgirlik);
            txtMiktar = (TextView) findViewById(R.id.txtMiktar);
            txtDate = (TextView) findViewById(R.id.txtDate);
        }

        //
        else {

            imgQr = (ImageView) findViewById(R.id.imgQR2);
            imgBarcode = (ImageView) findViewById(R.id.imgBar2);

            txtAd = (TextView) findViewById(R.id.txtAd2);
            txtAgirlik = (TextView) findViewById(R.id.txtAgirlik2);
            txtMiktar = (TextView) findViewById(R.id.txtMiktar2);
            txtDate = (TextView) findViewById(R.id.txtDate2);


        }


        txtAd.setText(myList.get(0));
        txtAgirlik.setText(myList.get(2) + " " + myList.get(4));

        if (!myList.get(1).equals("0")) {

            txtMiktar.setText(myList.get(1) + " " + myList.get(5));
        } else txtMiktar.setVisibility(View.GONE);

        text2Qr = (myList.get(3));

        txtDate.setText(new SimpleDateFormat("MM/dd/yyyy", Locale.ENGLISH)
                .format(Calendar.getInstance().getTime()));


        //  ViewGroup.LayoutParams params = lnrSs.getLayoutParams();
        //Changes the height and width to the specified *pixels*
        //  params.height = 100;
        //  params.width = 100;
        // lnrSs.setLayoutParams(params);

        //
        try {

            MultiFormatWriter multiFormatWriter = new MultiFormatWriter();
            BitMatrix bitMatrix = multiFormatWriter.encode(text2Qr, BarcodeFormat.QR_CODE, 100, 100);
            BarcodeEncoder barcodeEncoder = new BarcodeEncoder();
            bitmap = barcodeEncoder.createBitmap(bitMatrix);
            imgQr.setImageBitmap(bitmap);

            bitMatrix = multiFormatWriter.encode(text2Qr, BarcodeFormat.CODE_128, 50, 50);
            barcodeEncoder = new BarcodeEncoder();
            bitmap = barcodeEncoder.createBitmap(bitMatrix);
            imgBarcode.setImageBitmap(bitmap);


        } catch (WriterException e) {
            e.printStackTrace();
        }
        //
        ab.hide();
        View v;


        if (standartetiket.trim().equals("0")) v = findViewById(R.id.lnrSs);
        else v = findViewById(R.id.lnrSs2);
        //View v= getWindow().getDecorView().getRootView();
        v.setDrawingCacheEnabled(true);
        v.measure(View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED),
                View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED));
        v.layout(0, 0, v.getMeasuredWidth(), v.getMeasuredHeight());
        v.buildDrawingCache(true);
        Bitmap b = Bitmap.createBitmap(v.getDrawingCache());
        v.setDrawingCacheEnabled(false);
        imgBarcode.setVisibility(View.INVISIBLE);
        imgQr.setVisibility(View.INVISIBLE);
        // imgSs.setVisibility(View.VISIBLE);
        // imgSs.setImageBitmap(b);
        //   Bitmap resized = Bitmap.createScaledBitmap(b,200,400,true);
        //  Toast.makeText(this, "Yazıcıya Gönderiniz", Toast.LENGTH_SHORT).show();

        try {
            File file = new File(this.getExternalCacheDir(), "ss.png");
            FileOutputStream fOut = new FileOutputStream(file);

            b.compress(Bitmap.CompressFormat.PNG, 100, fOut);
            fOut.flush();
            fOut.close();
            file.setReadable(true, false);

            final Intent intent = new Intent(android.content.Intent.ACTION_SEND);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.putExtra(Intent.EXTRA_STREAM, Uri.fromFile(file));
            intent.setType("image/png");
            startActivity(Intent.createChooser(intent, "Etiket Paylaşımı"));
        } catch (Exception e) {

            e.printStackTrace();

        }
        finish();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.diger, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @TargetApi(Build.VERSION_CODES.GINGERBREAD)
    @RequiresApi(api = Build.VERSION_CODES.DONUT)
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.anasayfa) {

            finish();
            Intent i = new Intent(ProductEtiket.this, SliderMenu.class);
            startActivity(i);
            /*
            ab.hide();
            View v = findViewById(R.id.lnrSs);

            //View v= getWindow().getDecorView().getRootView();
            v.setDrawingCacheEnabled(true);
            v.measure(View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED),
                    View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED));
            v.layout(0, 0, v.getMeasuredWidth(), v.getMeasuredHeight());
            v.buildDrawingCache(true);
            Bitmap b = Bitmap.createBitmap(v.getDrawingCache());
            v.setDrawingCacheEnabled(false);
            imgBarcode.setVisibility(View.INVISIBLE);
            imgQr.setVisibility(View.INVISIBLE);
            // imgSs.setVisibility(View.VISIBLE);
            // imgSs.setImageBitmap(b);
            //   Bitmap resized = Bitmap.createScaledBitmap(b,200,400,true);
          //  Toast.makeText(this, "Yazıcıya Gönderiniz", Toast.LENGTH_SHORT).show();

            try {
                File file = new File(this.getExternalCacheDir(), "ss.png");
                FileOutputStream fOut = new FileOutputStream(file);

                b.compress(Bitmap.CompressFormat.PNG, 100, fOut);
                fOut.flush();
                fOut.close();
                file.setReadable(true, false);
                final Intent intent = new Intent(android.content.Intent.ACTION_SEND);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                intent.putExtra(Intent.EXTRA_STREAM, Uri.fromFile(file));
                intent.setType("image/png");
                startActivity(Intent.createChooser(intent, "Etiket Paylaşımı"));
            } catch (Exception e) {
                e.printStackTrace();
            }
            */
        } else if (id == R.id.geri) {
            onBackPressed();

        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }
}
