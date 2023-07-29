package com.app.camscan.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import com.app.camscan.R;

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

                //val intent = Intent(this, WelcomeActivity::class.java)
                //startActivity(intent)
            }
        },600);
    }
}
