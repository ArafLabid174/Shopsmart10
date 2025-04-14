package com.example.mobileshop.controllers;

import android.content.Context;
import android.content.Intent;
import android.widget.Toast;
import com.example.mobileshop.views.DashboardActivity;

public class LoginController {
    public void login(String email, String password, Context context) {
        // Check if email or password is empty
        if(email.isEmpty() || password.isEmpty()) {
//            Toast.makeText(context, "Please enter email and password", Toast.LENGTH_SHORT).show();
            Toast.makeText(context, "Login success!", Toast.LENGTH_SHORT).show();
            // Redirect to DashboardActivity
            Intent intent = new Intent(context, DashboardActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(intent);
        } else {
            // On successful login
            Toast.makeText(context, "Login success!", Toast.LENGTH_SHORT).show();
            // Redirect to DashboardActivity
            Intent intent = new Intent(context, DashboardActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(intent);
        }
    }
}
