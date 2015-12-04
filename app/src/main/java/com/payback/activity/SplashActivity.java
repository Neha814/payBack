package com.payback.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;

import com.payback.R;
import com.payback.functions.Methods;

public class SplashActivity extends AppCompatActivity {

    private static int SPLASH_TIME_OUT = 3000;
    Methods sp;
    boolean homeStatus = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        sp = new Methods(this);
        homeStatus = sp.getLogin();

        initUI();
    }

    private void initUI() {
        // TODO Auto-generated method stub
        startTimer();
    }

    private void startTimer() {

        new Handler().postDelayed(new Runnable() {

            @Override
            public void run() {

                if (homeStatus) {
                    Intent i = new Intent(SplashActivity.this, MainActivity.class);
                    startActivity(i);
                    finish();
                } else {
                    Intent i = new Intent(SplashActivity.this, LoginActivity.class);
                    startActivity(i);
                    finish();
                }

            }

        }, SPLASH_TIME_OUT);

    }

}
