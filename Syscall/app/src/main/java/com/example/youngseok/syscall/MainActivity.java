package com.example.youngseok.syscall;

import android.content.DialogInterface;
import android.content.Intent;
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
    int wearGlasses = 0;

    TextView textViewIsEnroll;

    boolean isEnroll = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

//        userDBManager.delete();/* */
//        userDBManager.dropTable();

        /*getAndroidID();*/
        makeBrandList();
        makeClassificationList();
        makeColorList();

        textViewIsEnroll = (TextView) findViewById(R.id.textview_isEnroll);

        Spinner spinnerBrand = (Spinner) findViewById(R.id.spinner_brand);
        Spinner spinnerClassification = (Spinner) findViewById(R.id.spinner_classification);
        Spinner spinnerColor = (Spinner) findViewById(R.id.spinner_color);

        Button buttonHelp = (Button) findViewById(R.id.button_help);
        Button buttonCamera = (Button) findViewById(R.id.button_camera);
//        Button buttonEnroll = (Button) findViewById(R.id.button_enroll);
        Button buttonNext = (Button) findViewById(R.id.button_next);

        CheckBox checkBoxStarter = (CheckBox) findViewById(R.id.checkbox_starter);
        CheckBox checkBoxGlasses = (CheckBox) findViewById(R.id.checkbox_glasses);

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
//        buttonEnroll.setOnClickListener(this);
        buttonNext.setOnClickListener(this);

        spinnerBrand.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                brand = String.valueOf((int) brandSpinnerAdapter.getItemId(position));
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });

        spinnerClassification.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                classification = String.valueOf((int) classificationSpinnerAdapter.getItemId(position));
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });

        spinnerColor.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                color = String.valueOf((int) colorSpinnerAdapter.getItemId(position));
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

        checkBoxGlasses.setOnClickListener(new CheckBox.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(((CheckBox)v).isChecked()) {
                    wearGlasses = 1;
                } else {
                    wearGlasses = 0;
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

//            case R.id.button_enroll:
//                try {
//                    userDBManager.insert(Integer.valueOf(SplashActivity.serverID), wearGlasses, CameraActivity.eye_radius);
//                } catch(Exception e) {
//                    Log.d("DB Insert Error", "Already enrolled");
//                }

                //Set Information
//                SetInformation task = new SetInformation();
//                task.execute("http://52.79.165.228/syscall/setInformation.php",
//                        SplashActivity.serverID, brand, classification, color, String.valueOf(isStartDriver));
//                break;

            case R.id.button_next:
                userDBManager.delete();

                try {
                    userDBManager.insert(Integer.valueOf(SplashActivity.serverID), wearGlasses, CameraActivity.eye_radius);
                } catch(Exception e) {
                    Log.d("DB Insert Error", "Already enrolled");
                }

                SetInformation task = new SetInformation();
                task.execute("http://52.79.165.228/syscall/setInformation.php",
                        SplashActivity.serverID, brand, classification, color, String.valueOf(isStartDriver));

                intent = new Intent(getApplicationContext(), RunActivity.class);
                startActivity(intent);
                break;

            default:
                break;
        }
    }

    private void makeBrandList() {
        brandList = new ArrayList<>();

        brandList.add(new Brand("Hyundai", R.drawable.hyundai_logo, 0));
        brandList.add(new Brand("KIA", R.drawable.kia_logo, 1));
        brandList.add(new Brand("Ssang Yong", R.drawable.ssangyong_logo, 2));
        brandList.add(new Brand("Renault", R.drawable.renault_logo, 3));
        brandList.add(new Brand("Chevrolet", R.drawable.chevrolet_logo, 4));
        brandList.add(new Brand("Honda", R.drawable.honda_logo, 5));
        brandList.add(new Brand("Toyota", R.drawable.toyota_logo, 6));
        brandList.add(new Brand("Lexus", R.drawable.lexus_logo, 7));
        brandList.add(new Brand("Nissan", R.drawable.nissan_logo, 8));
        brandList.add(new Brand("Infinity", R.drawable.infinity_logo, 9));
        brandList.add(new Brand("BMW", R.drawable.bmw_logo, 10));
        brandList.add(new Brand("Audi", R.drawable.audi_logo, 11));
        brandList.add(new Brand("Benz", R.drawable.benz_logo, 12));
        brandList.add(new Brand("Porsche", R.drawable.porsche_logo, 13));
        brandList.add(new Brand("Jaguar", R.drawable.jaguar_logo, 14));
        brandList.add(new Brand("Ford", R.drawable.ford_logo, 15));
        brandList.add(new Brand("Peugeot", R.drawable.peugeot_logo, 16));
        brandList.add(new Brand("Cadillac", R.drawable.cadillac_logo, 17));
        brandList.add(new Brand("Volkswagen", R.drawable.volkswagen_logo, 18));
        brandList.add(new Brand("Tesla", R.drawable.tesla_logo, 19));
    }

    private void makeClassificationList() {
        classificationList = new ArrayList<>();

        classificationList.add(new Classification("경형", 0));
        classificationList.add(new Classification("소형", 1));
        classificationList.add(new Classification("중형", 2));
        classificationList.add(new Classification("대형", 3));
        classificationList.add(new Classification("트럭", 4));
        classificationList.add(new Classification("버스", 5));
    }

    private void makeColorList() {
        colorList = new ArrayList<>();

        colorList.add(new Color("검정", R.drawable.black, 0));
        colorList.add(new Color("흰색", R.drawable.white, 1));
        colorList.add(new Color("회색", R.drawable.gray, 2));
        colorList.add(new Color("빨강", R.drawable.red, 3));
        colorList.add(new Color("주황", R.drawable.orange, 4));
        colorList.add(new Color("파랑", R.drawable.blue, 5));
        colorList.add(new Color("보라", R.drawable.violet, 6));
        colorList.add(new Color("분홍", R.drawable.pink, 7));
        colorList.add(new Color("초록", R.drawable.green, 8));
        colorList.add(new Color("노랑", R.drawable.yellow, 9));
        colorList.add(new Color("갈색", R.drawable.brown, 10));
    }

    //Set Information
    class SetInformation extends AccessServerDB {

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
