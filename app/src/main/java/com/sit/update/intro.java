package com.sit.update;

import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.RelativeLayout;

import java.util.Objects;

public class intro extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_intro);
        Objects.requireNonNull(getSupportActionBar()).hide();
        com.google.android.material.button.MaterialButton btn=findViewById(R.id.btn);
        new AnimationUtils();
        Animation animation= AnimationUtils.loadAnimation(intro.this,android.R.anim.fade_in);
        animation.setDuration(2000);
        RelativeLayout relativeLayout =findViewById(R.id.l);
        relativeLayout.startAnimation(animation);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                intro.this.startActivity(new Intent(intro.this,MainActivity.class));
            }
        });
    }
    public void onBackPressed()
    {
        Intent a = new Intent(Intent.ACTION_MAIN);
        a.addCategory(Intent.CATEGORY_HOME);
        a.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(a);
    }
}
