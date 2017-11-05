package com.example.youngseok.syscall;

import android.content.Intent;
import android.os.Handler;
import android.provider.Settings;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Window;
import android.view.WindowManager;

public class SplashActivity extends AppCompatActivity {

    private final int SPLASH_DISPLAY_LENGTH = 3000;

    private String androidID = null;
    private int isEnrolled = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_splash);

        getAndroidID();

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent intent;

                switch (isEnrolled) {
                    case 0:
                        intent = new Intent(SplashActivity.this, MainActivity.class);
                        SplashActivity.this.startActivity(intent);
                        SplashActivity.this.finish();
                        break;

                    case 1:
                        intent = new Intent(SplashActivity.this, RunActivity.class);
                        SplashActivity.this.startActivity(intent);
                        SplashActivity.this.finish();
                        break;

                    default :
                        break;


                }
            }
        }, SPLASH_DISPLAY_LENGTH);
    }

    private void getAndroidID() {
        try {
            androidID = Settings.Secure.getString(this.getContentResolver(), Settings.Secure.ANDROID_ID);
        } catch(Exception e) {
            e.printStackTrace();
        }
    }
}
