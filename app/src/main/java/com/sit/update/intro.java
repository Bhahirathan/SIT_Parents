package com.sit.update;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.Drawable;
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
        final Animation animation= AnimationUtils.loadAnimation(intro.this,android.R.anim.fade_in);
        animation.setDuration(2000);
        final RelativeLayout relativeLayout =findViewById(R.id.l);
        relativeLayout.startAnimation(animation);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(intro.this,MainActivity.class));
                finish();
            }
        });
        if(getIntent().getExtras()!=null && getIntent().getExtras().get("back").equals("true"))
        {
            btn.setText("BACK  ");
            Drawable drawable=btn.getContext().getResources().getDrawable(android.R.drawable.ic_media_previous);
            btn.setCompoundDrawablesWithIntrinsicBounds(drawable,null,null,null);
        }
    }
}
