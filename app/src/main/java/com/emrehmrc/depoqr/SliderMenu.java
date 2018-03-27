package com.emrehmrc.depoqr;

import android.annotation.TargetApi;
import android.app.ActivityOptions;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

public class SliderMenu extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {


    TextView txtName, txtMail;
    ActionBar ab;
    Intent i;
    NavigationView nv;
    Bundle bundle;
    LinearLayout ll;
    CardView first, second, third, fourth;
    Animation fadein, rotate, scale,scale2, fadeout, reverse,rotate2,fadeout2,fadein2,reverse2;
    ImageView i1, i2, i3, i4;
    private DrawerLayout dl;
    private ActionBarDrawerToggle abt;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_slider_menu);
        setNavigationViewListner();

        ab = getSupportActionBar();
        ab.setTitle("İŞLEM MENÜSÜ");
        //ab.setSubtitle("GİRİŞ");
        ll = (LinearLayout) findViewById(R.id.ll);

        i1 = (ImageView) findViewById(R.id.i1);
        i2 = (ImageView) findViewById(R.id.i2);
        i3 = (ImageView) findViewById(R.id.i3);
        i4 = (ImageView) findViewById(R.id.i4);
        first = (CardView) findViewById(R.id.firstitem);
        second = (CardView) findViewById(R.id.seconditem);
        third = (CardView) findViewById(R.id.thirditem);
        fourth = (CardView) findViewById(R.id.fourthtitem);

        fadein = AnimationUtils.loadAnimation(this, R.anim.fade_in);
        fadein2 = AnimationUtils.loadAnimation(this, R.anim.fade_in);
        fadeout = AnimationUtils.loadAnimation(this, R.anim.fade_out);
        fadeout2 = AnimationUtils.loadAnimation(this, R.anim.fade_out);
        rotate = AnimationUtils.loadAnimation(this, R.anim.rotate);
        rotate2 = AnimationUtils.loadAnimation(this, R.anim.rotate);
        reverse = AnimationUtils.loadAnimation(this, R.anim.rotatereverse);
        reverse2 = AnimationUtils.loadAnimation(this, R.anim.rotatereverse);
        scale = AnimationUtils.loadAnimation(this, R.anim.scale);
        scale2 = AnimationUtils.loadAnimation(this, R.anim.scale);

        ab.setBackgroundDrawable(getResources().getDrawable(R.drawable.arkaplan));

        dl = (DrawerLayout) findViewById(R.id.drawerslider);
        abt = new ActionBarDrawerToggle(this, dl, R.string.open, R.string.close);
        dl.addDrawerListener(abt);
        abt.syncState();
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        dl.closeDrawer(GravityCompat.START);
        nv = (NavigationView) findViewById(R.id.navigationid);
        nv.setItemIconTintList(null);


        SharedPreferences sharedpreferences = getSharedPreferences(AnaSayfa.MyPREFERENCES, Context.MODE_PRIVATE);
        View header = nv.getHeaderView(0);
        txtName = (TextView) header.findViewById(R.id.txtName);
        txtMail = (TextView) header.findViewById(R.id.txtMail);
        txtName.setText(sharedpreferences.getString("Name", null));
        txtMail.setText(sharedpreferences.getString("Email", null));
        dl.addDrawerListener(new DrawerLayout.DrawerListener() {
            @Override
            public void onDrawerSlide(View drawerView, float slideOffset) {

            }

            @Override
            public void onDrawerOpened(View drawerView) {

            }

            @Override
            public void onDrawerClosed(View drawerView) {
                /*
                i1.startAnimation(rotate);
                i2.startAnimation(reverse);
                i3.startAnimation(rotate);
                i4.startAnimation(reverse);
                //
                 i1.startAnimation(scale);
                i2.startAnimation(scale);
                i3.startAnimation(scale);
                i4.startAnimation(scale);
                //
                i1.startAnimation(fadeout);
                i2.startAnimation(fadeout);
                i3.startAnimation(fadeout);
                i4.startAnimation(fadeout);
                //
                i1.startAnimation(fadein);
                i2.startAnimation(fadein);
                i3.startAnimation(fadein);
                i4.startAnimation(fadein);
                  //
                 i1.startAnimation(scale);
                i2.startAnimation(scale);
                i3.startAnimation(scale);
                i4.startAnimation(scale);
                */
                i1.startAnimation(rotate);
                i2.startAnimation(scale);
                i3.startAnimation(fadeout);
                i4.startAnimation(reverse);


            }

            @Override
            public void onDrawerStateChanged(int newState) {

            }
        });
        first.setOnClickListener(new View.OnClickListener() {

            @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
            @Override
            public void onClick(View v) {
                //Card tıklama
                i1.startAnimation(rotate2);

            }
        });
        second.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                //Card tıklama
                i2.startAnimation(scale2);

            }
        });
        third.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                //Card tıklama
                i3.startAnimation(fadeout2);

            }
        });
        fourth.setOnClickListener(new View.OnClickListener() {

            @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
            @Override
            public void onClick(View v) {
                //Card tıklama
                i4.startAnimation(reverse2);


            }
        });
        rotate2.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                i = new Intent(SliderMenu.this, MalKabul.class);
                startActivity(i);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
        scale2.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                i = new Intent(SliderMenu.this, Sevkiyat.class);
                startActivity(i);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
        fadeout2.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {

                i = new Intent(SliderMenu.this, PlasiyerSatis.class);
                startActivity(i);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
        reverse2.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                i = new Intent(SliderMenu.this, DepoTransfer.class);
                startActivity(i);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });


    }


    @Override
    public boolean onOptionsItemSelected(MenuItem menuItem) {
        int id = menuItem.getItemId();
        if (abt.onOptionsItemSelected(menuItem)) {
            return true;
        } else if (id == R.id.exit) {
            finish();
            Intent i = new Intent(SliderMenu.this, AnaSayfa.class);
            startActivity(i);

        }
        return super.onOptionsItemSelected(menuItem);
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.nav_menuG://etiket basımı  Kullanilmiyor

                i = new Intent(SliderMenu.this, GrupBarkod.class);
                bundle = ActivityOptions.makeSceneTransitionAnimation(SliderMenu.this).toBundle();
                startActivity(i, bundle);
                break;


            case R.id.nav_menu1://etiket basımı  Kullanilmiyor

                i = new Intent(SliderMenu.this, Products.class);
                bundle = ActivityOptions.makeSceneTransitionAnimation(SliderMenu.this).toBundle();

                startActivity(i, bundle);
                break;
            case R.id.nav_menu2://mal kabul

                i = new Intent(SliderMenu.this, MalKabul.class);
                bundle = ActivityOptions.makeSceneTransitionAnimation(SliderMenu.this).toBundle();

                startActivity(i, bundle);
                break;
            case R.id.nav_menu3://sevkiyat
                i = new Intent(SliderMenu.this, Sevkiyat.class);
                bundle = ActivityOptions.makeSceneTransitionAnimation(SliderMenu.this).toBundle();

                startActivity(i, bundle);
                break;
            case R.id.nav_menu4://iade

                i = new Intent(SliderMenu.this, SliderMenu.class);
                bundle = ActivityOptions.makeSceneTransitionAnimation(SliderMenu.this).toBundle();
                startActivity(i, bundle);
                break;
            case R.id.nav_menu5://depo transfer

                i = new Intent(SliderMenu.this, DepoTransfer.class);
                bundle = ActivityOptions.makeSceneTransitionAnimation(SliderMenu.this).toBundle();

                startActivity(i, bundle);
                break;
            case R.id.nav_menu6://sayım

                i = new Intent(SliderMenu.this, SliderMenu.class);
                bundle = ActivityOptions.makeSceneTransitionAnimation(SliderMenu.this).toBundle();

                startActivity(i, bundle);
                break;
            case R.id.nav_menu7://çıkış

                finish();
                i = new Intent(SliderMenu.this, AnaSayfa.class);
                bundle = ActivityOptions.makeSceneTransitionAnimation(SliderMenu.this).toBundle();

                startActivity(i, bundle);
                break;
            case R.id.nav_menu8://ayarlar

                i = new Intent(SliderMenu.this, EtiketTercih.class);
                bundle = ActivityOptions.makeSceneTransitionAnimation(SliderMenu.this)
                        .toBundle();
                startActivity(i, bundle);
                break;

            default:
        }
        dl.closeDrawer(GravityCompat.START);
        return true;

    }

    @Override
    protected void onStart() {
        super.onStart();

    }

    private void setNavigationViewListner() {
        NavigationView navigationView = (NavigationView) findViewById(R.id.navigationid);
        navigationView.setNavigationItemSelectedListener(this);
    }

    @Override
    protected void onResume() {
     //   dl.openDrawer(GravityCompat.START);
       // i1.startAnimation(rotate);
      //  i2.startAnimation(scale);
      //  i3.startAnimation(fadeout);
     //   i4.startAnimation(reverse);
        super.onResume();
    }

    @Override
    public void onBackPressed() {

        super.onBackPressed();
    }

    @Override
    protected void onDestroy() {
        Log.d("mesaj", "kapandı");
        super.onDestroy();
    }

    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.anasayfa, menu);
        return super.onCreateOptionsMenu(menu);
    }


}
