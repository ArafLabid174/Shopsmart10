package com.example.mobileshop.views;


import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.mobileshop.R;
import com.example.mobileshop.controllers.SignUpController;
import com.google.android.material.textfield.TextInputEditText;

public class SignUpActivity extends AppCompatActivity {

    private TextInputEditText etFullName, etSignUpEmail, etSignUpPassword, etSignUpConfirmPassword;
    private SignUpController controller;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        controller = new SignUpController();

        etFullName = findViewById(R.id.etFullName);
        etSignUpEmail = findViewById(R.id.etSignUpEmail);
        etSignUpPassword = findViewById(R.id.etSignUpPassword);
        etSignUpConfirmPassword = findViewById(R.id.etSignUpConfirmPassword);

        findViewById(R.id.btnSignUp).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String fullName = etFullName.getText().toString();
                String email = etSignUpEmail.getText().toString();
                String password = etSignUpPassword.getText().toString();
                String confirmPassword = etSignUpConfirmPassword.getText().toString();

                controller.signUp(fullName, email, password, confirmPassword, SignUpActivity.this);
            }
        });

        TextView tvLoginRedirect = findViewById(R.id.tvLoginRedirect);
        tvLoginRedirect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(SignUpActivity.this, LoginActivity.class));
                finish(); // Optional: remove SignUpActivity from backstack
            }
        });

    }
}
