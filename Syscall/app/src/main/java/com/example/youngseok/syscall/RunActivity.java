package com.example.youngseok.syscall;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.AssetManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Environment;
import android.os.Message;
import android.speech.tts.TextToSpeech;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.SurfaceView;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONObject;
import org.opencv.android.BaseLoaderCallback;
import org.opencv.android.CameraBridgeViewBase;
import org.opencv.android.LoaderCallbackInterface;
import org.opencv.android.OpenCVLoader;
import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.imgproc.Imgproc;
import org.tensorflow.contrib.android.TensorFlowInferenceInterface;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.LinkedList;
import java.util.List;

import static com.example.youngseok.syscall.CameraActivity.PERMISSIONS_REQUEST_CODE;

public class RunActivity extends AppCompatActivity
        implements TextToSpeech.OnInitListener, CameraBridgeViewBase.CvCameraViewListener2 {

    String []brandArr = { "현대", "기아", "쌍용", "르노", "쉐보레", "혼다", "도요타", "렉서스", "닛싼", "인피니티",
                          "비엠떠블유", "아우디", "벤츠", "포르쉐", "재규어", "포드", "푸조", "캐딜락", "폭스바겐", "테슬라" };
    String []classificationArr = { "경형", "소형", "중형", "대형", "트럭", "버스" };
    String []colorArr = { "검정", "흰색", "회색", "빨강", "주황", "파랑", "보라", "분홍", "초록", "노랑", "갈색" };


    CalculateVelocity calculateVelocity;
    DirectionVector directionVector;

    LocationManager locationManager;

    List<OtherCar> otherCarsList = new LinkedList<>();

    String myJSON;
    /* Hwancheol : declaring variables and functions */
    static {
        System.loadLibrary("opencv_java3");
        System.loadLibrary("native-lib");
    }
    private CameraBridgeViewBase mOpenCvCameraView;
    private Mat matInput;
    private Mat matResult;
    /* Hwancheol End */


    double prevLat, prevLon, curLat, curLon, dirLat, dirLon;
    double curVelocity;
    double prevVelocity = -19;

    private boolean flag = true;

    private TextToSpeech myTTS;

    TextView textViewLatitude, textViewLongitude, textViewAccel, textViewVelocity, textViewDirection;

    public void onInit(int status) {

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_run);
        /* Hwancheol : Connecting Camera to CV */
        if (!hasPermissions(PERMISSIONS)) {
            requestPermissions(PERMISSIONS, PERMISSIONS_REQUEST_CODE);
        }
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON, WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        mOpenCvCameraView = (CameraBridgeViewBase)findViewById(R.id.realtime_camera_view);
        mOpenCvCameraView.setVisibility(SurfaceView.VISIBLE);
        mOpenCvCameraView.setCvCameraViewListener(this);
        mOpenCvCameraView.setCameraIndex(1); // front-camera(1),  back-camera(0)
        //mOpenCvCameraView.setMaxFrameSize(270, 480);
        mLoaderCallback.onManagerConnected(LoaderCallbackInterface.SUCCESS);
        /* Hwancheol End */
        flag = true;

        myTTS = new TextToSpeech(this, this);

        calculateVelocity = new CalculateVelocity();
        directionVector = new DirectionVector();

        locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);

        textViewLatitude = (TextView) findViewById(R.id.textView_latitude);
        textViewLongitude = (TextView) findViewById(R.id.textView_longitude);
//        textViewAccel = (TextView) findViewById(R.id.textView_accel);
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
        flag = true;
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        flag = true;
    }

    @Override
    public void onResume() {
        super.onResume();
        if (!OpenCVLoader.initDebug()) {
            //Log.d(TAG, "onResume :: Internal OpenCV library not found.");
            OpenCVLoader.initAsync(OpenCVLoader.OPENCV_VERSION_3_2_0, this, mLoaderCallback);
        } else {
            //Log.d(TAG, "onResum :: OpenCV library found inside package. Using it!");
            mLoaderCallback.onManagerConnected(LoaderCallbackInterface.SUCCESS);
        }
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
        myTTS.shutdown();

        UpdateLocationInformation task = new UpdateLocationInformation();
        task.execute("http://52.79.165.228/syscall/updateLocationInformation.php",
                SplashActivity.serverID,
                Double.toString(0),
                Double.toString(0),
                Double.toString(0),
                String.valueOf(0),
                String.valueOf(0));
        /* Hwancheol */
        if (mOpenCvCameraView != null)
            mOpenCvCameraView.disableView();
        /* Hwancheol End */
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
        SendRunnable sendRunnable = new SendRunnable();
        Thread sendThread = new Thread(sendRunnable);
        sendThread.setDaemon(true);
        sendThread.start();

        ReceiveRunnable receiveRunnable = new ReceiveRunnable();
        Thread receiveThread = new Thread(receiveRunnable);
        receiveThread.setDaemon(true);
        receiveThread.start();
    }

    android.os.Handler sendHandler = new android.os.Handler() {
        public void handleMessage(Message msg) {
            startLocationService();

            textViewLatitude.setText(Double.toString(curLat));
            textViewLongitude.setText(Double.toString(curLon));

            if(prevLat != 0 && prevLon != 0) {
                directionVector.getDirection(prevLat, prevLon, curLat, curLon);
                curVelocity = calculateVelocity.getVelocity(prevLat, prevLon, curLat, curLon);

                Log.d("Velocity", prevVelocity + " " + curVelocity);

                if(curVelocity < prevVelocity + 0) {
                    dirLat = directionVector.getDirectionLat() * 100000;
                    dirLon = directionVector.getDirectionLon() * 100000;

                    textViewVelocity.setText(String.format("%.2f", curVelocity) + "km/h");
//                    textViewAccel.setText(String.format("%.2f", curVelocity - prevVelocity) + "km/h^2");
                    textViewDirection.setText("(" + String.format("%.2f", dirLat) + ", " + String.format("%.2f", dirLon) + ")");

                    //Update Latitude & Longitude & Acceleration
                    UpdateLocationInformation task = new UpdateLocationInformation();
                    task.execute("http://52.79.165.228/syscall/updateLocationInformation.php",
                            SplashActivity.serverID,
                            Double.toString(curLat),
                            Double.toString(curLon),
                            Double.toString(curVelocity - prevVelocity),
                            String.valueOf(directionVector.getDirectionLat() * 100000),
                            String.valueOf(directionVector.getDirectionLon() * 100000));

                    prevVelocity = curVelocity;
                }
            }

            prevLat = curLat; prevLon = curLon;
        }
    };

    android.os.Handler receiveHandler = new android.os.Handler() {
        public void handleMessage(Message msg) {
            GetOtherCarInfo getOtherCarInfo = new GetOtherCarInfo();
            getOtherCarInfo.execute("http://52.79.165.228/syscall/prac.php", SplashActivity.serverID);
        }
    };

    public class SendRunnable implements Runnable {
        @Override
        public void run() {

            while(flag) {
                Message msg = Message.obtain();
                sendHandler.sendMessage(msg);

                try {
                    Thread.sleep(1000);
                } catch(Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public class ReceiveRunnable implements Runnable {
        @Override
        public void run() {

            while(flag) {
                Message msg = Message.obtain();
                receiveHandler.sendMessage(msg);

                try {
                    Thread.sleep(7000);
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

            int count = 0;
            String tts = "";

            for (OtherCar oc : otherCarsList) {
                String ocBrand = brandArr[oc.getBrand()];
                String ocClassification = classificationArr[oc.getClassification()];
                String ocColor = colorArr[oc.getColor()];
                String ocBeginner = (oc.getBeginner() == 0 ? "" : "초보운전");
                String ocDrowsy = (oc.getDrowsy() == 0 ? "" : "졸음운전");

                if(oc.getLatitude() == 0.0 || oc.getLongitude() == 0.0) {
                    break;
                }

                count++;

                MyCar myCar = new MyCar(curLat, curLon, dirLat, dirLon);
                OtherCar otherCar = new OtherCar(oc.getBrand(), oc.getClassification(), oc.getColor(), oc.getLatitude(), oc.getLongitude(),
                                                oc.getVectorLat(), oc.getVectorLon(), oc.getBeginner(), oc.getDrowsy());

                Angle angle = new Angle(myCar, otherCar);

                Log.e("Other Car Info", ocBrand + " " + ocClassification + " " + ocColor + " " + ocBeginner + " " + ocDrowsy + " " + angle.getAngleWithOtherCar());

                if(count > 2) break;

                tts = tts + angle.getAngleWithOtherCar() + ocBrand + ocClassification + ocColor + ocBeginner + ocDrowsy + "  ";
            }

            if(count > 2) {
                String str = "";
                switch(count-2) {
                    case 1: str = "한대"; break;
                    case 2: str = "두대"; break;
                    case 3: str = "세대"; break;
                    case 4: str = "네대"; break;
                    case 5: str = "다섯대"; break;

                    default: str = "다량의"; break;
                }

                tts = tts + "외 " + str + " 차량이 접근중 입니다.";

            } else if(count != 0){
                tts = tts + "차량이 접근중 입니다.";
            } else {

            }

            myTTS.speak(tts, TextToSpeech.QUEUE_ADD, null);

        } catch(Exception e) {
            e.printStackTrace();
        }
    }
    /* Hwancheol : Implementing Functions */
    @Override
    public void onCameraViewStarted(int width, int height) {

    }

    @Override
    public void onCameraViewStopped() {

    }

    @Override
    public Mat onCameraFrame(CameraBridgeViewBase.CvCameraViewFrame inputFrame) {

        matInput = inputFrame.rgba();

        if ( matResult != null ) matResult.release();
        matResult = new Mat(matInput.rows(), matInput.cols(), matInput.type());
        Mat matInputT = new Mat(matInput.rows(), matInput.cols(), matInput.type());
        Mat matInputF = new Mat(matInput.rows(), matInput.cols(), matInput.type());

        Core.transpose(matInput, matInputT);
        Imgproc.resize(matInputT, matInputF, matInputF.size(), 270, 480, 0);
        Core.flip(matInputF, matInput, -1);

        return matInput;
    }
    public BaseLoaderCallback mLoaderCallback = new BaseLoaderCallback(this) {
        @Override
        public void onManagerConnected(int status) {
            switch (status) {
                case LoaderCallbackInterface.SUCCESS:

                {
                    mOpenCvCameraView.enableView();
                } break;
                default:
                {
                    super.onManagerConnected(status);
                } break;
            }
        }
    };
    /* Hwancheol End*/

    /* Request Permissions */
    static final int PERMISSIONS_REQUEST_CODE = 1000;
    //String[] PERMISSIONS  = {"android.permission.CAMERA"};
    String[] PERMISSIONS  = {"android.permission.CAMERA",
            "android.permission.WRITE_EXTERNAL_STORAGE"};

    private boolean hasPermissions(String[] permissions) {
        int result;

        for (String perms : permissions){

            result = ContextCompat.checkSelfPermission(this, perms);

            if (result == PackageManager.PERMISSION_DENIED){

                return false;
            }
        }

        return true;
    }



    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        switch(requestCode){

            case PERMISSIONS_REQUEST_CODE:
                if (grantResults.length > 0) {
                    boolean cameraPermissionAccepted = grantResults[0]
                            == PackageManager.PERMISSION_GRANTED;

                    boolean writePermissionAccepted = grantResults[1]
                            == PackageManager.PERMISSION_GRANTED;

                    if (!cameraPermissionAccepted || !writePermissionAccepted) {
                        showDialogForPermission("앱을 실행하려면 퍼미션을 허가하셔야합니다.");
                        return;
                    }
                }
                break;
        }
    }


    @TargetApi(Build.VERSION_CODES.M)
    private void showDialogForPermission(String msg) {

        AlertDialog.Builder builder = new AlertDialog.Builder( RunActivity.this);
        builder.setTitle("Alert");
        builder.setMessage(msg);
        builder.setCancelable(false);
        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id){
                requestPermissions(PERMISSIONS, PERMISSIONS_REQUEST_CODE);
            }
        });
        builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface arg0, int arg1) {
                finish();
            }
        });
        builder.create().show();
    }
    /* Hwancheol End */
}
