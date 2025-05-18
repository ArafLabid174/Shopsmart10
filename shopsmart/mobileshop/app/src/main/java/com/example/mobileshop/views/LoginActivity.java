package com.example.mobileshop.views;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;

import androidx.appcompat.app.AppCompatActivity;

import com.example.mobileshop.R;
import com.example.mobileshop.controllers.LoginController;
import com.example.mobileshop.models.User;
import com.example.mobileshop.utils.SessionManager;
import com.google.android.material.textfield.TextInputEditText;

public class LoginActivity extends BaseActivity {

    private TextInputEditText etEmail, etPassword;
    private LoginController controller;
    private Button btnLogin;

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
        
        setContentView(R.layout.activity_login);

        controller = new LoginController();

        etEmail = findViewById(R.id.etEmail);
        etPassword = findViewById(R.id.etPassword);
        btnLogin = findViewById(R.id.btnLogin);

        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Show loading indicator and disable button
                btnLogin.setEnabled(false);
                showLoading("Signing in...");
                
                // Grab input values
                String email = etEmail.getText().toString();
                String password = etPassword.getText().toString();

                // Validate and proceed with login via controller
                controller.login(email, password, LoginActivity.this, new LoginController.LoginCallback() {
                    @Override
                    public void onSuccess(User user) {
                        // Hide loading indicator (if not already navigated away)
                        runOnUiThread(() -> {
                            hideLoading();
                            btnLogin.setEnabled(true);
                        });
                    }

                    @Override
                    public void onError(String message) {
                        // Hide loading indicator and re-enable button
                        runOnUiThread(() -> {
                            hideLoading();
                            btnLogin.setEnabled(true);
                        });
                    }
                });
            }
        });

        // Link to sign-up
        findViewById(R.id.tvSignUpLink).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(LoginActivity.this, SignUpActivity.class));
            }
        });
    }
}
