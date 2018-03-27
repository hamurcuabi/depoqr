package com.emrehmrc.depoqr;


import android.Manifest;
import android.annotation.TargetApi;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Toast;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;

public class AnaSayfa extends AppCompatActivity {


    public static final String MyPREFERENCES = "MyPrefs";
    public static final String ID = "IdKey";
    public static final String Name = "nameKey";
    public static final String Companiesid = "CIKey";
    public static final String Email = "emailKey";
    public static final String AnaDepoId = "depoId";
    public static final String HedefDepoId = "hedefdepoId";
    public static final String AnaDepoName = "depoName";
    public static final String HedefDepoName = "hedefdepoName";
    public static final String StandartEtiket = "0";//standartEtiket
    public static final String OzelEtiket = "1";//ozeltEtiket
    public static final String PaletID = "paletid";
    public static final String BarcodeCodeEnter = "barcodecodeenter";
    public static final String SevkiyatForwadingID = "forwadingid";
    public static final String SevkiyatDepoID = "depoid";

    static private final int MY_REQUEST_CODE = 707;
    ConnectionClass connectionClass;
    EditText edtuserid, edtpass;
    Button btnlogin;
    ProgressBar pbbar;
    ActionBar ab;
    String memberid = "", username, password;
    SharedPreferences sharedpreferences;
    LinearLayout layout;
    Animation scale;
    private CheckBox saveLoginCheckBox;
    private SharedPreferences loginPreferences;
    private SharedPreferences.Editor loginPrefsEditor;
    private Boolean saveLogin;

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ana_sayfa);
        layout = (LinearLayout) findViewById(R.id.layout);
        layout.setRotation(-90);
        onWindowFocusChanged(true);
        rotate(0);
        scale = AnimationUtils.loadAnimation(this, R.anim.fade_out);

        sharedpreferences = getApplicationContext().getSharedPreferences(MyPREFERENCES, Context.MODE_PRIVATE);

        loginPreferences = getSharedPreferences("loginPrefs", MODE_PRIVATE);
        loginPrefsEditor = loginPreferences.edit();

        saveLogin = loginPreferences.getBoolean("saveLogin", false);

        ab = getSupportActionBar();
        ab.setTitle("ANASAYFA");
        ab.setSubtitle("GİRİŞ");
        ab.setBackgroundDrawable(getResources().getDrawable(R.drawable.arkaplan));

        connectionClass = new ConnectionClass();
        edtuserid = (EditText) findViewById(R.id.edtuserid);
        edtpass = (EditText) findViewById(R.id.edtpass);
        btnlogin = (Button) findViewById(R.id.btnlogin);
        pbbar = (ProgressBar) findViewById(R.id.pbarloading);
        saveLoginCheckBox = (CheckBox) findViewById(R.id.rme);
        saveLoginCheckBox.setChecked(saveLogin);
        if (saveLogin == true) {
            edtuserid.setText(loginPreferences.getString("username", ""));
            edtpass.setText(loginPreferences.getString("password", ""));
            saveLoginCheckBox.setChecked(true);
        }


        saveLoginCheckBox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(edtuserid.getWindowToken(), 0);
                username = edtuserid.getText().toString();
                password = edtpass.getText().toString();
                if (saveLoginCheckBox.isChecked()) {
                    loginPrefsEditor.putBoolean("saveLogin", true);
                    loginPrefsEditor.putString("username", username);
                    loginPrefsEditor.putString("password", password);
                    loginPrefsEditor.commit();
                } else {
                    loginPrefsEditor.clear();
                    loginPrefsEditor.commit();
                }
            }
        });

        btnlogin.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.CUPCAKE)
            @Override
            public void onClick(View v) {

                btnlogin.startAnimation(scale);


            }
        });
        scale.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
                DoLogin doLogin = new DoLogin();
                doLogin.execute("");
            }

            @Override
            public void onAnimationEnd(Animation animation) {


            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
        //  edtuserid.setText("erenyildiz92@outlook.com");
        //   edtpass.setText("gsw23.nba");
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (checkSelfPermission(Manifest.permission.CAMERA)
                    != PackageManager.PERMISSION_GRANTED) {

                requestPermissions(new String[]{Manifest.permission.CAMERA},
                        MY_REQUEST_CODE);
            }
        }

    }
    @Override
    public void onWindowFocusChanged(boolean hasFocus) {

        layout.setPivotX(layout.getWidth() / 2);
        layout.setPivotY(layout.getHeight());
    }

    public void rotate(int rot) {
        layout.animate()
                .setDuration(500)
                .rotation(rot);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == MY_REQUEST_CODE) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Now user should be able to use camera
            } else {
                // Your app will not have this permission. Turn off all functions
                // that require this permission or it will force close like your
                // original question
            }
            super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }

    private String md5(String in) {
        MessageDigest digest;
        try {
            digest = MessageDigest.getInstance("MD5");
            digest.reset();
            digest.update(in.getBytes());
            byte[] a = digest.digest();
            int len = a.length;
            StringBuilder sb = new StringBuilder(len << 1);
            for (int i = 0; i < len; i++) {
                sb.append(Character.forDigit((a[i] & 0xf0) >> 4, 16));
                sb.append(Character.forDigit(a[i] & 0x0f, 16));
            }
            return sb.toString();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return null;
    }

    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.anasayfa, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.exit) {
            finish();
        }


        return super.onOptionsItemSelected(item);
    }

    @RequiresApi(api = Build.VERSION_CODES.CUPCAKE)
    public class DoLogin extends AsyncTask<String, String, String> {
        String z = "";
        Boolean isSuccess = false;
        String n, ci, e, id;

        @Override
        protected void onPreExecute() {
            pbbar.setVisibility(View.VISIBLE);
            username = edtuserid.getText().toString();
            password = edtpass.getText().toString();

        }

        @TargetApi(Build.VERSION_CODES.LOLLIPOP)
        @Override
        protected void onPostExecute(String r) {
           pbbar.setVisibility(View.GONE);
            Toast.makeText(AnaSayfa.this, r, Toast.LENGTH_SHORT).show();

            if (isSuccess) {

                if (saveLoginCheckBox.isChecked()) {
                    loginPrefsEditor.putBoolean("saveLogin", true);
                    loginPrefsEditor.putString("username", username);
                    loginPrefsEditor.putString("password", password);
                    loginPrefsEditor.commit();
                }

                SharedPreferences.Editor editor = sharedpreferences.edit();
                editor.putString("Name", n);
                editor.putString("Companiesid", ci);
                editor.putString("Email", e);
                editor.putString("ID", id);
                editor.putString("PaletID", "0");
                editor.putString("BarcodeCodeEnter", "");
                editor.commit();

                Intent i = new Intent(AnaSayfa.this, SliderMenu.class);
               startActivity(i);
              finish();
            }

        }

        @Override
        protected String doInBackground(String... params) {
            if (username.trim().equals("") || password.trim().equals(""))
                z = "Lütfen Kullanıcı Adı ve Şifreyi Giriniz!";
            else {
                try {
                    Connection con = connectionClass.CONN();
                    if (con == null) {
                        z = "Bağlantı Hatası";
                    } else {


                        String query = "select * from MEMBER where EMAIL='" + username + "' and " +
                                "PASSWORD='" + md5(password) + "'";
                        Statement stmt = con.createStatement();
                        ResultSet rs = stmt.executeQuery(query);

                        if (rs.next()) {

                            id = rs.getString("ID");
                            n = rs.getString("NAME");
                            e = rs.getString("EMAIL");
                            ci = rs.getString("COMPANIESID");
                            z = "Giriş Başarılı";
                            isSuccess = true;
                        } else {
                            z = "Hata";
                            isSuccess = false;
                        }

                    }
                } catch (Exception ex) {
                    isSuccess = false;
                    z = "Hata";
                }
            }
            return z;
        }
    }
}