package com.example.mobileshop.controllers;


import android.content.Context;
import android.widget.Toast;

public class SignUpController {

    public void signUp(String fullName, String email, String password, String confirmPassword, Context context) {
        // Perform your signup logic, possibly call an API, etc.
        if(fullName.isEmpty() || email.isEmpty() || password.isEmpty() || confirmPassword.isEmpty()) {
            Toast.makeText(context, "All fields are required", Toast.LENGTH_SHORT).show();
        } else if(!password.equals(confirmPassword)) {
            Toast.makeText(context, "Passwords do not match", Toast.LENGTH_SHORT).show();
        } else {
            // Proceed with sign-up
            Toast.makeText(context, "Sign-up success!", Toast.LENGTH_SHORT).show();
        }
    }
}
