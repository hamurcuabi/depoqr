package com.emrehmrc.depoqr;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.AnticipateInterpolator;
import android.widget.ImageView;

public class SplashScreen extends AppCompatActivity {
    Animation animation;
    ImageView ımageView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);

         ımageView=(ImageView) findViewById(R.id.imagsplash);
         animation= AnimationUtils.loadAnimation(getApplicationContext(),R.anim.splash);

         ımageView.startAnimation(animation);

         animation.setAnimationListener(new Animation.AnimationListener() {
             @Override
             public void onAnimationStart(Animation animation) {

             }

             @Override
             public void onAnimationEnd(Animation animation) {
                 Intent i= new Intent(SplashScreen.this,AnaSayfa.class);
                 startActivity(i);
                 finish();
             }

             @Override
             public void onAnimationRepeat(Animation animation) {

             }
         });




    }
}
