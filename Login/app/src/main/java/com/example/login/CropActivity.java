package com.example.login;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Rect;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

public class CropActivity extends AppCompatActivity {
    CropImageView cropImageView;
    Button crop;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_crop);

        cropImageView = findViewById(R.id.cropImageView);
        crop = findViewById(R.id.crop);

        cropImageView.setAspectRatio(10, 10);
        cropImageView.setGuidelines(CropImageView.Guidelines.ON);
        cropImageView.setFixedAspectRatio(true);
        cropImageView.setCropShape(CropImageView.CropShape.OVAL);
        cropImageView.setScaleType(CropImageView.ScaleType.FIT_CENTER);
        cropImageView.setAutoZoomEnabled(true);
        cropImageView.setShowProgressBar(true);
        cropImageView.setCropRect(new Rect(0, 0, 800, 500));


        Uri uri = (Uri) (getIntent().getExtras().get("Uri"));
        cropImageView.setImageUriAsync(uri);


        crop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.e("Result", "1");
                Bitmap cropped = cropImageView.getCroppedImage();

                new ImageSaver(getApplicationContext()).
                        setExternal(false).
                        setFileName("profile.png").
                        setDirectoryName("images").
                        save(cropped);

                EditProfileActivity.changePfpMethod1(getApplicationContext());
                finish();
            }
        });
    }
}
