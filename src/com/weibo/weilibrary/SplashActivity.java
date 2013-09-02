package com.weibo.weilibrary;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import com.weibo.share.R;


public class SplashActivity extends Activity {
    private Context mContext = null;
    private boolean active = true;
    private int splashTime = 1500;
    private String activity;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (this.getIntent() != null) {
            this.activity = this.getIntent().getStringExtra("activity");
        }
        setContentView(R.layout.activity_splash);

        this.mContext = this.getApplicationContext();

        new Thread() {
            @Override
            public void run() {
                try {
                    int waited = 0;
                    while (active && (waited < splashTime)) {
                        sleep(100);

                        if (active) {
                            waited += 100;
                        }
                    }
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } finally {
                    finish();
                    if (activity == null || !activity.equals("HomeActivity")) {
                        Intent intent = new Intent();
                        intent.setClass(mContext, LoginActivity.class);
                        startActivity(intent);
                    }
                }
            }
        }.start();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }
}