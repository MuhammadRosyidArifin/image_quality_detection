package com.kuroikaminari.image_quality_detection;

import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

public class CameraActivity extends AppCompatActivity{
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_camera);
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.camera_container,CameraFragment.newInstance())
                .commit();
    }
}