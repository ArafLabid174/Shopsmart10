package com.example.mobileshop.views;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.mobileshop.R;
import com.example.mobileshop.controllers.SignUpController;
import com.example.mobileshop.models.User;
import com.example.mobileshop.utils.SessionManager;
import com.google.android.material.textfield.TextInputEditText;

public class SignUpActivity extends BaseActivity {

    private TextInputEditText etFullName, etSignUpEmail, etSignUpPassword, etSignUpConfirmPassword;
    private SignUpController controller;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        // Check if user is already logged in
        SessionManager sessionManager = new SessionManager(this);
        if (sessionManager.isLoggedIn()) {
            // User is already logged in, redirect to Dashboard
            startActivity(new Intent(this, DashboardActivity.class));
            finish();
            return;
        }
        
        setContentView(R.layout.activity_sign_up);

        controller = new SignUpController();

        etFullName = findViewById(R.id.etFullName);
        etSignUpEmail = findViewById(R.id.etSignUpEmail);
        etSignUpPassword = findViewById(R.id.etSignUpPassword);
        etSignUpConfirmPassword = findViewById(R.id.etSignUpConfirmPassword);

        Button btnSignUp = findViewById(R.id.btnSignUp);
        btnSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Show loading indicator and disable button
                btnSignUp.setEnabled(false);
                showLoading("Creating account...");
                
                String fullName = etFullName.getText().toString();
                String email = etSignUpEmail.getText().toString();
                String password = etSignUpPassword.getText().toString();
                String confirmPassword = etSignUpConfirmPassword.getText().toString();

                controller.signUp(fullName, email, password, confirmPassword, SignUpActivity.this, new SignUpController.SignUpCallback() {
                    @Override
                    public void onSuccess(com.example.mobileshop.models.User user) {
                        // Hide loading and navigate directly to dashboard on successful registration
                        runOnUiThread(() -> {
                            hideLoading();
                            btnSignUp.setEnabled(true);
                            // Navigation happens after hiding the loading indicator
                            startActivity(new Intent(SignUpActivity.this, DashboardActivity.class));
                            finish();
                        });
                    }

                    @Override
                    public void onError(String message) {
                        // Hide loading indicator and re-enable button
                        runOnUiThread(() -> {
                            hideLoading();
                            btnSignUp.setEnabled(true);
                        });
                    }
                });
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
