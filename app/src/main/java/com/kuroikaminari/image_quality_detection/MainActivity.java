package com.kuroikaminari.image_quality_detection;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import org.opencv.android.OpenCVLoader;
import org.opencv.android.Utils;
import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.MatOfDouble;
import org.opencv.core.Size;
import org.opencv.imgproc.Imgproc;

import java.io.File;
import java.io.IOException;

public class MainActivity extends AppCompatActivity {

    final String TAG = "MainActivity";
    Button btn_camera;
    Button btn_process;

    String imagePath;
    Uri imageUri;
    ImageView imageView;

    Bitmap imageBitmap;
    TextView txthasil;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        OpenCVLoader.initDebug();
//        ActivityCompat.requestPermissions(MainActivity.this,new String[]{Manifest.permission.CAMERA},1);
        ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
        ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 1);

        imageView = findViewById(R.id.main_image_view);
        txthasil = findViewById(R.id.main_text_view);

        btn_camera = findViewById(R.id.btnSelectPicture);
        btn_process = findViewById(R.id.btnProcess);

        btn_camera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //untuk mengambil gambar dari kamera dengan zoom 3.7x
                startActivityForResult(new Intent(MainActivity.this,CameraActivity.class),100);
                //untuk mengambil gambar dari storage
//                openImage();
            }
        });

        btn_process.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (imageBitmap != null) {
                    String label;
                    String nilai_blur = hitungBlur(imageBitmap);
                    String cerah = hitungBrightness(imageBitmap);
                    label = nilai_blur+"\n"+cerah;
                    txthasil.setText(label);
                } else {
                    Toast.makeText(MainActivity.this, "Gambar kosong", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    String hitungBlur(Bitmap bitmap) {

        Mat imageMat = new Mat();
        Mat grayMat = new Mat();
        Mat destination = new Mat();
        Utils.bitmapToMat(bitmap, imageMat);
        Imgproc.cvtColor(imageMat, grayMat, Imgproc.COLOR_BGR2GRAY);
        Imgproc.Laplacian(grayMat, destination, 3);

        MatOfDouble median = new MatOfDouble();
        MatOfDouble std = new MatOfDouble();
        Core.meanStdDev(destination, median, std);

        String label;
        double result = Math.pow(std.get(0, 0)[0], 2);
        Log.d(TAG, "hasil laplacian = " + result);
        if (result < 100.0) {
            label = "Blur";
        } else {
            label = "Tidak Blur";
        }
        return label;
    }

    String hitungBrightness(Bitmap bitmap) {
        float darkThreshold = bitmap.getWidth() * bitmap.getHeight() * 0.25f;
        int darkPixel = 0;

        for(int x = 0; x<bitmap.getWidth();x++){
            for(int y = 0; y<bitmap.getHeight();y++){
                int color = bitmap.getPixel(x,y);
                int r = Color.red(color);
                int g = Color.green(color);
                int b = Color.blue(color);
                double luminance = ((0.299 * r + 0.0f) + (0.587 * g + 0.0f) + (0.114 * b + 0.0f));
                if (luminance < 20) {
                    darkPixel++;
                }
            }
        }

        Log.d(TAG, "nilai dark = " + darkPixel);
        Log.d(TAG, "nilai threshold = " + darkThreshold);
        String label;
        if (darkPixel <= darkThreshold) {
            label = "Cerah";
        } else {
            label = "Gelap";
        }
        return label;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 100) {
            if (resultCode == RESULT_OK && data != null) {
                String returnedResult = data.getData().toString();
                imagePath = returnedResult;

                File image = new File(imagePath);
                BitmapFactory.Options bmOptions = new BitmapFactory.Options();
                Bitmap bitmap = BitmapFactory.decodeFile(image.getAbsolutePath(), bmOptions);
                bitmap = Bitmap.createScaledBitmap(bitmap, 400, 400, true);
                imageBitmap = bitmap;
                imageView.setImageBitmap(bitmap);
                txthasil.setText("");
                Log.d(TAG, "nama file : " + returnedResult);
            }
        }

        if (requestCode == 200 && resultCode == RESULT_OK && data != null) {
            imageUri = data.getData();
            try {
                imageBitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), imageUri);
            } catch (IOException e) {
                e.printStackTrace();
            }
            imageView.setImageBitmap(imageBitmap);

            imagePath = getRealPathFromUri(this, imageUri);
            txthasil.setText("");
            Log.d(TAG, "nama File : " + imagePath);
        }
    }

    public static String getRealPathFromUri(Context context, Uri imageUri) {
        Cursor cursor = null;
        try {
            String[] proj = {MediaStore.Images.Media.DATA};
            cursor = context.getContentResolver().query(imageUri, proj, null, null, null);
            int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
            cursor.moveToFirst();
            return cursor.getString(column_index);
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
    }

    public void openImage() {
        Intent myIntent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(myIntent, 200);
    }
}
