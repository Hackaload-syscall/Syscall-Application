package com.example.youngseok.syscall;

import android.content.DialogInterface;
import android.content.Intent;
import android.provider.Settings;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.Spinner;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    UserDBManager userDBManager = new UserDBManager(this, "User.db", null, 1);

    BrandSpinnerAdapter brandSpinnerAdapter;
    ColorSpinnerAdapter colorSpinnerAdapter;
    ClassificationSpinnerAdapter classificationSpinnerAdapter;

    List<Brand> brandList;
    List<Classification> classificationList;
    List<Color> colorList;

    String /*androidID,*/ brand, classification, color;
    int isStartDriver = 0;

    TextView textViewIsEnroll;

    boolean isEnroll = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        /*getAndroidID();*/
        makeBrandList();
        makeClassificationList();
        makeColorList();

        TextView textViewAndroidID = (TextView) findViewById(R.id.textView_androidID);
        textViewIsEnroll = (TextView) findViewById(R.id.textview_isEnroll);

        Spinner spinnerBrand = (Spinner) findViewById(R.id.spinner_brand);
        Spinner spinnerClassification = (Spinner) findViewById(R.id.spinner_classification);
        Spinner spinnerColor = (Spinner) findViewById(R.id.spinner_color);

        Button buttonHelp = (Button) findViewById(R.id.button_help);
        Button buttonCamera = (Button) findViewById(R.id.button_camera);
        Button buttonEnroll = (Button) findViewById(R.id.button_enroll);
        Button buttonNext = (Button) findViewById(R.id.button_next);

        CheckBox checkBoxStarter = (CheckBox) findViewById(R.id.checkbox_starter);

        textViewAndroidID.setText(SplashActivity.serverID);

        if(isEnroll) {
            textViewIsEnroll.setText("등록");
        } else {
            textViewIsEnroll.setText("미등록");
        }

        brandSpinnerAdapter = new BrandSpinnerAdapter(this, brandList);
        classificationSpinnerAdapter = new ClassificationSpinnerAdapter(this, classificationList);
        colorSpinnerAdapter = new ColorSpinnerAdapter(this, colorList);

        spinnerBrand.setAdapter(brandSpinnerAdapter);
        spinnerClassification.setAdapter(classificationSpinnerAdapter);
        spinnerColor.setAdapter(colorSpinnerAdapter);

        buttonHelp.setOnClickListener(this);
        buttonCamera.setOnClickListener(this);
        buttonEnroll.setOnClickListener(this);
        buttonNext.setOnClickListener(this);

        spinnerBrand.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                brand = brandSpinnerAdapter.getItem(position).toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });

        spinnerClassification.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                classification = classificationSpinnerAdapter.getItem(position).toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });

        spinnerColor.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                color = colorSpinnerAdapter.getItem(position).toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });

        checkBoxStarter.setOnClickListener(new CheckBox.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(((CheckBox)v).isChecked()) {
                    isStartDriver = 1;
                } else {
                    isStartDriver = 0;
                }
            }
        });
    }

    @Override
    protected void onRestart() {
        super.onRestart();

        if(CameraActivity.eye_radius != 0) {
            textViewIsEnroll.setText("등록");
        }
    }

    public void onClick(View view) {
        Intent intent;

        switch(view.getId()) {
            case R.id.button_help:
                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);

                alertDialogBuilder.setTitle("자동차 분류기준");
                alertDialogBuilder
                        .setMessage("경형 : 1000cc 미만\n소형 : 1600cc 미만\n중형 : 1600cc 이상\n대형 : 2000cc 이상")
                        .setCancelable(false)
                        .setNegativeButton("확인", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.cancel();
                            }
                        });

                AlertDialog alertDialog = alertDialogBuilder.create();

                alertDialog.show();

                break;

            case R.id.button_camera:
                intent = new Intent(getApplicationContext(), CameraActivity.class);
                startActivity(intent);
                break;

            case R.id.button_enroll:
                try {
                    userDBManager.insert(10, CameraActivity.eye_radius);
//                    InsertData task = new InsertData();
//                    task.execute("http://52.79.165.228/syscall/enrollInfo.php", SplashActivity.serverID ,brand, classification, color, String.valueOf(isStartDriver));

                } catch(Exception e) {
                    Log.d("DB Insert Error", "Already enrolled");
                    InsertData task = new InsertData();
                    task.execute("http://52.79.165.228/syscall/enrollInfo.php", SplashActivity.serverID ,brand, classification, color, String.valueOf(isStartDriver));
                }
                break;

            case R.id.button_next:
                intent = new Intent(getApplicationContext(), RunActivity.class);
                startActivity(intent);
                break;

            default:
                break;
        }
    }

    /*
    private void getAndroidID() {
        try {
            androidID = Settings.Secure.getString(this.getContentResolver(), Settings.Secure.ANDROID_ID);
        } catch(Exception e) {
            e.printStackTrace();
        }
    }
    */

    private void makeBrandList() {
        brandList = new ArrayList<>();

        brandList.add(new Brand("Hyundai", R.drawable.hyundai_logo));
        brandList.add(new Brand("KIA", R.drawable.kia_logo));
        brandList.add(new Brand("Ssang Yong", R.drawable.ssangyong_logo));
        brandList.add(new Brand("Renault", R.drawable.renault_logo));
        brandList.add(new Brand("Chevrolet", R.drawable.chevrolet_logo));
        brandList.add(new Brand("Honda", R.drawable.honda_logo));
        brandList.add(new Brand("Toyota", R.drawable.toyota_logo));
        brandList.add(new Brand("Lexus", R.drawable.lexus_logo));
        brandList.add(new Brand("Nissan", R.drawable.nissan_logo));
        brandList.add(new Brand("Infinity", R.drawable.infinity_logo));
        brandList.add(new Brand("BMW", R.drawable.bmw_logo));
        brandList.add(new Brand("Audi", R.drawable.audi_logo));
        brandList.add(new Brand("Benz", R.drawable.benz_logo));
        brandList.add(new Brand("Porsche", R.drawable.porsche_logo));
        brandList.add(new Brand("Jaguar", R.drawable.jaguar_logo));
        brandList.add(new Brand("Ford", R.drawable.ford_logo));
        brandList.add(new Brand("Peugeot", R.drawable.peugeot_logo));
        brandList.add(new Brand("Cadillac", R.drawable.cadillac_logo));
        brandList.add(new Brand("Volkswagen", R.drawable.volkswagen_logo));
        brandList.add(new Brand("Tesla", R.drawable.tesla_logo));
    }

    private void makeClassificationList() {
        classificationList = new ArrayList<>();

        classificationList.add(new Classification("경형"));
        classificationList.add(new Classification("소형"));
        classificationList.add(new Classification("중형"));
        classificationList.add(new Classification("대형"));
        classificationList.add(new Classification("트럭"));
        classificationList.add(new Classification("버스"));
    }

    private void makeColorList() {
        colorList = new ArrayList<>();

        colorList.add(new Color("검정", R.drawable.black));
        colorList.add(new Color("흰색", R.drawable.white));
        colorList.add(new Color("회색", R.drawable.gray));
        colorList.add(new Color("빨강", R.drawable.red));
        colorList.add(new Color("주황", R.drawable.orange));
        colorList.add(new Color("파랑", R.drawable.blue));
        colorList.add(new Color("보라", R.drawable.violet));
        colorList.add(new Color("분홍", R.drawable.pink));
        colorList.add(new Color("초록", R.drawable.green));
        colorList.add(new Color("노랑", R.drawable.yellow));
        colorList.add(new Color("갈색", R.drawable.brown));
    }

    class InsertData extends AccessServerDB {

        @Override
        protected void onPostExecute(String result) {

            super.onPostExecute(result);
        }

        @Override
        protected String getPostParameters(String... params) {

            return "ServerID=" + params[1] + "&Brand=" + params[2] + "&Classification=" + params[3] + "&Color=" + params[4] + "&Beginner=" + params[5];
        }
    }

}
