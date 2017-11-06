package com.example.youngseok.syscall;

import android.content.Intent;
import android.os.Handler;
import android.provider.Settings;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class SplashActivity extends AppCompatActivity {

    private final int SPLASH_DISPLAY_LENGTH = 3000;

    private String androidID = null;
    private int isEnrolled = 0; // Default = 0

    private String myJSON;
    public static String serverID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_splash);

        getAndroidID();

        //Get 'serverID' & 'isEnrolled'
        GetServerID task = new GetServerID();
        task.execute("http://52.79.165.228/syscall/getServerID.php",
                String.valueOf(Long.parseLong(androidID.substring(2), 16)));

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

    //Connect Server & Get ID
    class GetServerID extends AccessServerDB {

        @Override
        protected void onPostExecute(String result) {

            super.onPostExecute(result);

            myJSON = result;
            getData();
        }

        @Override
        protected String getPostParameters(String... params) { return "AndroidID=" + params[1]; }
    }

    //JSONData to 'isEnrolled' & 'serverID'
    private void getData() {

        try {

            JSONObject jsonObj = new JSONObject(myJSON);
            JSONArray jsonArray = jsonObj.getJSONArray("Result");

            for (int i = 0; i < jsonArray.length(); i++) {

                JSONObject c = jsonArray.getJSONObject(i);

                isEnrolled = Integer.parseInt(c.getString("IsEnrolled"));
                serverID = c.getString("ServerID");
            }

        } catch(JSONException e) {

            e.printStackTrace();
        }
    }
}
