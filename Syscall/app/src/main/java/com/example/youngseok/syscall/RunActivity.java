package com.example.youngseok.syscall;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Message;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.LinkedList;
import java.util.List;

public class RunActivity extends AppCompatActivity {

    CalculateVelocity calculateVelocity;
    DirectionVector directionVector;

    LocationManager locationManager;

    List<OtherCar> otherCarsList = new LinkedList<>();

    String myJSON;

    double prevLat, prevLon, curLat, curLon;
    double curVelocity;
    double prevVelocity = -19;

    private boolean flag = true;

    TextView textViewLatitude, textViewLongitude, textViewAccel, textViewVelocity, textViewDirection;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_run);

        calculateVelocity = new CalculateVelocity();
        directionVector = new DirectionVector();

        locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);

        textViewLatitude = (TextView) findViewById(R.id.textView_latitude);
        textViewLongitude = (TextView) findViewById(R.id.textView_longitude);
        textViewAccel = (TextView) findViewById(R.id.textView_accel);
        textViewVelocity = (TextView) findViewById(R.id.textView_velocity);
        textViewDirection = (TextView) findViewById(R.id.textView_direction);

        Button buttonSetting = (Button) findViewById(R.id.button_setting);
        buttonSetting.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(RunActivity.this, MainActivity.class);
                startActivity(intent);
                finish();
            }
        });

        runThreads();
    }

    @Override
    protected void onStart() {
        super.onStart();

    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
        flag = false;

        UpdateLocationInformation task = new UpdateLocationInformation();
        task.execute("http://52.79.165.228/syscall/updateLocationInformation.php",
                SplashActivity.serverID,
                Double.toString(0),
                Double.toString(0),
                Double.toString(0),
                String.valueOf(0),
                String.valueOf(0));
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        flag = false;

        UpdateLocationInformation task = new UpdateLocationInformation();
        task.execute("http://52.79.165.228/syscall/updateLocationInformation.php",
                SplashActivity.serverID,
                Double.toString(0),
                Double.toString(0),
                Double.toString(0),
                String.valueOf(0),
                String.valueOf(0));
    }

    private final LocationListener locationListener = new LocationListener() {
        @Override
        public void onLocationChanged(Location location) {
            curLat = location.getLatitude();
            curLon = location.getLongitude();
        }

        @Override
        public void onStatusChanged(String s, int i, Bundle bundle) {
        }

        @Override
        public void onProviderEnabled(String s) {
        }

        @Override
        public void onProviderDisabled(String s) {
        }
    };

    private void startLocationService () {
        long minTime = 1000;
        float minDistance = 1;

        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION, android.Manifest.permission.ACCESS_COARSE_LOCATION}, 1);
        }
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, minTime, minDistance, locationListener);
        locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, minTime, minDistance, locationListener);
    }

    public void runThreads() {
        MyRunnable myRunnable = new MyRunnable();
        Thread myThread = new Thread(myRunnable);
        myThread.setDaemon(true);
        myThread.start();
    }

    android.os.Handler mainHandler = new android.os.Handler() {
        public void handleMessage(Message msg) {
            startLocationService();

            textViewLatitude.setText(Double.toString(curLat));
            textViewLongitude.setText(Double.toString(curLon));

            if(prevLat != 0 && prevLon != 0) {
                directionVector.getDirection(prevLat, prevLon, curLat, curLon);
                curVelocity = calculateVelocity.getVelocity(prevLat, prevLon, curLat, curLon);

                Log.d("Velocity", prevVelocity + " " + curVelocity);

                if(curVelocity < prevVelocity + 20) {
                    textViewVelocity.setText(String.format("%.2f", curVelocity) + "km/h");
                    textViewAccel.setText(String.format("%.2f", curVelocity - prevVelocity) + "km/h^2");
                    textViewDirection.setText("(" + String.format("%.2f", directionVector.getDirectionLat() * 100000)
                            + ", " + String.format("%.2f", directionVector.getDirectionLon() * 100000) + ")");

                    //Update Latitude & Longitude & Acceleration
                    UpdateLocationInformation task = new UpdateLocationInformation();
                    task.execute("http://52.79.165.228/syscall/updateLocationInformation.php",
                            SplashActivity.serverID,
                            Double.toString(curLat),
                            Double.toString(curLon),
                            Double.toString(curVelocity - prevVelocity),
                            String.valueOf(directionVector.getDirectionLat() * 100000),
                            String.valueOf(directionVector.getDirectionLon() * 100000));

                    GetOtherCarInfo getOtherCarInfo = new GetOtherCarInfo();
                    getOtherCarInfo.execute("http://52.79.165.228/syscall/prac.php", SplashActivity.serverID);

                    prevVelocity = curVelocity;
                }
            }

            prevLat = curLat; prevLon = curLon;
        }
    };

    public class MyRunnable implements Runnable {
        @Override
        public void run() {

            while(flag) {
                Message msg = Message.obtain();
                mainHandler.sendMessage(msg);

                try {
                    Thread.sleep(1000);
                } catch(Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

    //Update Location Information
    class UpdateLocationInformation extends AccessServerDB {

        @Override
        protected void onPostExecute(String result) {

            super.onPostExecute(result);
        }

        @Override
        protected String getPostParameters(String... params) {

            return "ServerID=" + params[1] + "&Latitude=" + params[2] + "&Longitude=" + params[3] + "&Acceleration=" + params[4] + "&Direction_X=" + params[5] + "&Direction_Y=" + params[6];
        }
    }

    class GetOtherCarInfo extends AccessServerDB {

        @Override
        protected void onPostExecute(String result) {

            super.onPostExecute(result);

            myJSON = result;
            getData();
        }

        @Override
        protected String getPostParameters(String... params) { return "ServerID=" + params[1]; }
    }

    private void getData() {

        try {

            JSONObject jsonObj = new JSONObject(myJSON);
            JSONArray jsonArray = jsonObj.getJSONArray("Info");

            otherCarsList = new LinkedList<>();

            for (int i = 0; i < jsonArray.length(); i++) {

                JSONObject c = jsonArray.getJSONObject(i);

                otherCarsList.add(new OtherCar(
                        Integer.parseInt(c.getString("Brand")),
                        Integer.parseInt(c.getString("Classification")),
                        Integer.parseInt(c.getString("Color")),
                        Double.parseDouble(c.getString("Latitude")),
                        Double.parseDouble(c.getString("Longitude")),
                        Double.parseDouble(c.getString("Direction_X")),
                        Double.parseDouble(c.getString("Direction_Y")),
                        Integer.parseInt(c.getString("Beginner")),
                        Integer.parseInt(c.getString("Drowsy"))
                    )
                );
            }

            for (OtherCar oc : otherCarsList) {
                
                Log.e("checkOtherCarList", String.valueOf(oc.getBrand()));
            }

        } catch(Exception e) {

            e.printStackTrace();
        }
    }
}
