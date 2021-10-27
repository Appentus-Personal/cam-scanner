package com.appentus.camscanner.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import com.appentus.camscanner.R;

public class SplashActivity extends BaseActivity {

    @Override
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        setContentView(R.layout.splash_acvtivity);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                startActivity(new Intent(SplashActivity.this, WelcomeActivity.class));
                finish();
            }
        },1000);
    }
}
