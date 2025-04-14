package com.example.mobileshop.views;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.example.mobileshop.R;
import com.example.mobileshop.controllers.SplashController;
import com.example.mobileshop.databinding.ActivitySplashBinding;

public class SplashActivity extends AppCompatActivity {

    private ActivitySplashBinding binding;
    private SplashController controller;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Using ViewBinding if enabled - otherwise just setContentView(R.layout.activity_splash)
        binding = ActivitySplashBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        controller = new SplashController();

        // If using Glide or another library to load gif from res/raw or drawable
        Glide.with(this)
                .load(R.raw.splash_gif) // or your gif resource / file location
                .into(binding.imgGif);

        // Delay 5 seconds then move to LoginActivity
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                controller.navigateToLogin(SplashActivity.this);
            }
        }, 5000);
    }
}
