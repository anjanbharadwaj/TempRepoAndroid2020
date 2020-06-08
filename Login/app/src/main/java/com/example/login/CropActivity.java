package com.example.login;

import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Bitmap;
import android.graphics.Rect;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.theartofdev.edmodo.cropper.CropImageView;

public class CropActivity extends AppCompatActivity {
    CropImageView cropImageView;
    Button crop;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_crop_personal);

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
