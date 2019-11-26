package com.kuroikaminari.image_quality_detection;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class MainActivity extends AppCompatActivity {

    final String TAG = "MainActivity";
    Button btn_camera;
    Button btn_process;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
//        OpenCVLoader.initDebug();
//        ActivityCompat.requestPermissions(MainActivity.this,new String[]{Manifest.permission.CAMERA},1);
        ActivityCompat.requestPermissions(MainActivity.this,new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},1);
        ActivityCompat.requestPermissions(MainActivity.this,new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},1);
        btn_camera = findViewById(R.id.btnSelectPicture);
        btn_process = findViewById(R.id.btnProcess);

        btn_camera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                startActivityForResult(new Intent(MainActivity.this,Camera2.class),100);
                startActivity(new Intent(MainActivity.this,CameraActivity.class));
            }
        });

        btn_process.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            }
        });
    }

//    @Override
//    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
//        super.onActivityResult(requestCode, resultCode, data);
//        if(requestCode == 100){
//            if(resultCode == RESULT_OK && data!=null){
//                String returnedResult = data.getData().toString();
//
//                Log.d(TAG,"nama file : "+returnedResult);
//            }
//        }
//    }
}
