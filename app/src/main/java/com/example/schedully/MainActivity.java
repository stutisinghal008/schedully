package com.example.schedully;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Handler;

import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {

    private static int SPLASH_SCREEN=5000;
    Animation logoAnim, titleAnim;
    ImageView logo;
    TextView title;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_main);

        //animations
        logoAnim = AnimationUtils.loadAnimation(this,R.anim.logo_animation);
        titleAnim = AnimationUtils.loadAnimation(this,R.anim.title_animation);

        //hooks
        logo = findViewById(R.id.imageView);
        title = findViewById(R.id.textView2);
        logo.setAnimation(logoAnim);
        title.setAnimation(titleAnim);

        new Handler().postDelayed(new Runnable() {
            public void run() {
                Intent intent = new Intent(MainActivity.this, activity_center.class);
                startActivity(intent);
                finish();
            }
        }, SPLASH_SCREEN);
    }
}