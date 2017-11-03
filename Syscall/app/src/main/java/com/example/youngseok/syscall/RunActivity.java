package com.example.youngseok.syscall;

import android.content.Context;
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
import android.widget.TextView;

public class RunActivity extends AppCompatActivity {

    CalculateVelocity caculateVelocity;
    LocationManager locationManager;
    SensorManager sensorManager;

    Sensor sensor;
    SensorEventListener sensorEventListener;

    double accX, accY, accZ;
    double prevLat, prevLon, curLat, curLon;

    TextView textViewLatitude, textViewLongitude, textViewAccel, textViewVelocity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_run);

        caculateVelocity = new CalculateVelocity();
        locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);

        sensor = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);

        sensorEventListener = new AccelometerListener();
        sensorManager.registerListener(sensorEventListener, sensor, SensorManager.SENSOR_DELAY_UI);

        textViewLatitude = (TextView) findViewById(R.id.textView_latitude);
        textViewLongitude = (TextView) findViewById(R.id.textView_longitude);
        textViewAccel = (TextView) findViewById(R.id.textView_accel);
        textViewVelocity = (TextView) findViewById(R.id.textView_velocity);

        runThreads();
    }

    @Override
    protected void onStart() {
        super.onStart();
        sensorManager.registerListener(sensorEventListener, sensor, SensorManager.SENSOR_DELAY_UI);

    }

    @Override
    public void onResume() {
        super.onResume();
        sensorManager.registerListener(sensorEventListener, sensor, SensorManager.SENSOR_DELAY_UI);
    }

    @Override
    public void onPause() {
        super.onPause();
        sensorManager.unregisterListener(sensorEventListener);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        sensorManager.unregisterListener(sensorEventListener);
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

    private class AccelometerListener implements SensorEventListener {

        @Override
        public void onSensorChanged(SensorEvent event) {
            if(event.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
                accX = event.values[0];
                accY = event.values[1];
                accZ = event.values[2];

                new Thread() {
                    public void run() {
                        try {
                            textViewAccel.setText("X : " + Double.toString(accX) + "\nY : " + Double.toString(accY) + "\nZ : " + Double.toString(accZ));
                            Thread.sleep(1000);
                        } catch(Exception e) {
                            e.printStackTrace();
                        }

                    }
                }.start();

                Log.i("ACCELEROMETER", event.values[0] + " " + event.values[1] + " " + event.values[2]);
            }
        }

        @Override
        public void onAccuracyChanged(Sensor sensor, int accuracy) {

        }
    }

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
                textViewVelocity.setText(String.format("%.2f", caculateVelocity.getVelocity(prevLat, prevLon, curLat, curLon)) + "m/s");
            }

            prevLat = curLat; prevLon = curLon;
        }
    };

    public class MyRunnable implements Runnable {
        @Override
        public void run() {

            while(true) {
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
}
