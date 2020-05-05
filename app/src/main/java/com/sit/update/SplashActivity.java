package com.sit.update;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Objects;

public class SplashActivity extends AppCompatActivity {

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (!(checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE)
                    == PackageManager.PERMISSION_GRANTED)) {
                Toast.makeText(getApplicationContext(), "This app requires File Access Permission", Toast.LENGTH_SHORT).show();
                try {
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            startActivity(new Intent(SplashActivity.this, SplashActivity.class));
                        }
                    }, 1000);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            else {
                startActivity(new Intent(SplashActivity.this,intro.class));
                finish();
            }
        }
    }
    public void isStoragePermissionGranted() {
        if (Build.VERSION.SDK_INT >= 23) {
            if (!(checkSelfPermission(android.Manifest.permission.WRITE_EXTERNAL_STORAGE)
                    == PackageManager.PERMISSION_GRANTED)) {

                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
            }
        }
    }

    @SuppressLint("ShowToast")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        Objects.requireNonNull(getSupportActionBar()).hide();
        isStoragePermissionGranted();
        try {
            ImageView i=findViewById(R.id.im);
            TextView t=findViewById(R.id.te);
            TextView t1=findViewById(R.id.te1);
            new AnimationUtils();
            Animation animation= AnimationUtils.loadAnimation(SplashActivity.this,android.R.anim.slide_in_left);
            animation.setDuration(2000);
            t.startAnimation(animation);
            t1.startAnimation(animation);
            i.startAnimation(animation);
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                        if ((checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE)
                                == PackageManager.PERMISSION_GRANTED)) {

                            Intent mainIntent = new Intent(SplashActivity.this, intro.class);
                            Toast.makeText(SplashActivity.this,"not working",Toast.LENGTH_LONG);
                            startActivity(mainIntent);
                            finish();
                            overridePendingTransition(android.R.anim.fade_in,android.R.anim.fade_out);
                        }
                    }
                }
            }, 3000);
        }
        catch (Exception e){
            Toast.makeText(this,"splash not working",Toast.LENGTH_LONG);
        }
    }
}
