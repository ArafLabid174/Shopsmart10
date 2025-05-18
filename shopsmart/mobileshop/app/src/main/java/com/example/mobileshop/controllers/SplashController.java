package com.example.mobileshop.controllers;


import android.content.Context;
import android.content.Intent;
import com.example.mobileshop.views.LoginActivity;

public class SplashController {

    public void navigateToLogin(Context context) {
        Intent intent = new Intent(context, LoginActivity.class);
        context.startActivity(intent);
    }
}
