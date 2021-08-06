package org.geek.profiledash.dashUi;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import androidx.appcompat.app.AppCompatActivity;

import org.geek.profiledash.R;

import maes.tech.intentanim.CustomIntent;


public class SplashActivity extends AppCompatActivity {

    Handler handler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

//        Delay Request
        handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent intent = new Intent(SplashActivity.this, SignInActivity.class);
                startActivity(intent);

//                Activity transition animation
                CustomIntent.customType(SplashActivity.this, getString(R.string.fadein_to_fadeout));
                finish();
            }
        }, 1500);

    }

}