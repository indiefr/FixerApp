package com.creatio.fixer;

import android.animation.ObjectAnimator;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.util.DisplayMetrics;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.androidnetworking.AndroidNetworking;
import com.google.firebase.FirebaseApp;
import com.google.firebase.iid.FirebaseInstanceId;

public class Splash extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        FirebaseApp.initializeApp(this);
        AndroidNetworking.initialize(getApplicationContext());
        LinearLayout ly_gral = (LinearLayout) findViewById(R.id.ly_gral);
        ImageView img = (ImageView) findViewById(R.id.img);
        DisplayMetrics metrics = this.getResources().getDisplayMetrics();
        int mid = (metrics.heightPixels - img.getHeight()) / 3;
        ObjectAnimator animation = ObjectAnimator.ofFloat(img, "y", mid);
        animation.setDuration(500);
        animation.start();
        new CountDownTimer(3000, 1500) {

            public void onTick(long millisUntilFinished) {

            }

            public void onFinish() {
                Intent intent = new Intent(Splash.this, Login.class);
                SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(Splash.this);
                Boolean login = pref.getBoolean("login", false);
                Boolean login_spe = pref.getBoolean("login_spe", false);
                if (login_spe) {
                    intent = new Intent(Splash.this, MainActivityPlo.class);

                }else if (login) {
                    intent = new Intent(Splash.this, MainActivity.class);

                } else {
                    intent = new Intent(Splash.this, MainActivity.class);


                }
//                 else {
//                    intent = new Intent(Splash.this, Login.class);
//                    startActivity(intent);
//                }
                String refreshedToken = FirebaseInstanceId.getInstance().getToken();
                startActivity(intent);
                finish();


            }
        }.start();

    }
}
