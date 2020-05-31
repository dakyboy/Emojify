package com.dakiiii.emojify;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.io.File;

public class MainActivity extends AppCompatActivity {

    private static final int REQUEST_IMAGE_CAPTURE = 177;
    private static final int REQUEST_STORAGE_PERMISSION = 5;
    private static final String FILE_PROVIDER_AUTHORITY = "com.dakiiii.emojify.fileprovider";
    private ImageView eImageView;
    private Button eEmojifyButton;
    private FloatingActionButton eShareFloatingActionButton;
    private FloatingActionButton eSaveFloatingActionButton;
    private FloatingActionButton eClearFloatingActionButton;
    private String eTempPhotoPath;
    private Bitmap eResultsBitmap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        eImageView = findViewById(R.id.imageView);
        eShareFloatingActionButton = findViewById(R.id.floatingActionButtonShare);
        eEmojifyButton = findViewById(R.id.button_take_pic);
        eSaveFloatingActionButton = findViewById(R.id.floatingActionButtonSave);
        eClearFloatingActionButton = findViewById(R.id.floatingActionButtonClose);

    }

    public void openEmojify(View view) {

        launchCamera();
    }

    private void launchCamera() {
        Intent emojifyIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (emojifyIntent.resolveActivity(getPackageManager()) != null) {
            File photoFile = null;
            try {
                photoFile = BitmapUtils.createTempImageFile(this);
            } catch (Exception e) {
                e.printStackTrace();
            }

            if (photoFile != null) {
                eTempPhotoPath = photoFile.getAbsolutePath();

                Uri photoUri = FileProvider.getUriForFile(this,
                        FILE_PROVIDER_AUTHORITY, photoFile);

                emojifyIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoUri);
                startActivityForResult(emojifyIntent, REQUEST_IMAGE_CAPTURE);
            }

        }
    }

    private void checkPermissions() {
        String[] permissionStrings = new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE};
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, permissionStrings, REQUEST_STORAGE_PERMISSION);
        } else
            launchCamera();


    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        switch (requestCode) {
            case REQUEST_STORAGE_PERMISSION: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    launchCamera();
                } else {
                    Toast.makeText(this, "Permission Denied", Toast.LENGTH_SHORT).show();
                }
                break;
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            processAndSetImage();
        } else {
            BitmapUtils.deleteImageFile(this, eTempPhotoPath);
        }
    }

    private void processAndSetImage() {
        setFloatingButtonsVisible();
        eResultsBitmap = BitmapUtils.resamplePic(this, eTempPhotoPath);
        eImageView.setImageBitmap(eResultsBitmap);

    }

    private void setFloatingButtonsVisible() {
        eEmojifyButton.setVisibility(View.GONE);
        eClearFloatingActionButton.setVisibility(View.VISIBLE);
        eSaveFloatingActionButton.setVisibility(View.VISIBLE);
        eShareFloatingActionButton.setVisibility(View.VISIBLE);

    }

    public void clearImage(View view) {
        setButtonsInvisible();
        BitmapUtils.deleteImageFile(this, eTempPhotoPath);
    }

    private void setButtonsInvisible() {
        eImageView.setImageResource(R.drawable.ic_baseline_camera_48);
        eClearFloatingActionButton.setVisibility(View.GONE);
        eSaveFloatingActionButton.setVisibility(View.GONE);
        eShareFloatingActionButton.setVisibility(View.GONE);
        eEmojifyButton.setVisibility(View.VISIBLE);
    }

    public void shareImage(View view) {
        BitmapUtils.deleteImageFile(this, eTempPhotoPath);
        BitmapUtils.saveImage(this, eResultsBitmap);
        BitmapUtils.shareImage(this, eTempPhotoPath);

    }

    public void saveImage(View view) {
        BitmapUtils.deleteImageFile(this, eTempPhotoPath);
        BitmapUtils.saveImage(this, eResultsBitmap);

    }
}