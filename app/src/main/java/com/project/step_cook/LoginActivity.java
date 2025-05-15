package com.project.step_cook;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;

public class LoginActivity extends AppCompatActivity {

    private EditText userEmail;
    private EditText userPass;
    private Button loginButton;
    private TextView signupRedirect;

    private UserManager userManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // Force Light Mode
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        userEmail = findViewById(R.id.userEmail);
        userPass = findViewById(R.id.userPassword);
        loginButton = findViewById(R.id.loginButton);
        signupRedirect = findViewById(R.id.signupRedirect);

        userManager = UserManager.getInstance();

        if (userManager.isUserLoggedIn()) {
            startActivity(new Intent(LoginActivity.this, MainActivity.class));
            finish();
        }

        loginButton.setOnClickListener(v -> validate());

        signupRedirect.setOnClickListener(v -> startActivity(new Intent(LoginActivity.this, RegisterActivity.class)));
    }

    private void validate(){
        String email = userEmail.getText().toString();
        String password = userPass.getText().toString();

        if (email.isEmpty()) {
            userEmail.setError("Please enter an email");
            userEmail.requestFocus();
            return;
        }
        if (password.isEmpty()) {
            userPass.setError("Please enter a password");
            userPass.requestFocus();
        }
        else if (password.length() < 6) {
            userPass.setError("Password is too short, minimum 6 chars");
            userPass.requestFocus();
        }
        else{
            userManager.loginUser(email, password, new UserManager.UserDataCallback() {
                @Override
                public void onUserDataLoaded(User user) {
                    Toast.makeText(LoginActivity.this, "Welcome " + user.getUserName() + "!", Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(LoginActivity.this, MainActivity.class));
                    finish();
                }

                @Override
                public void onError(Exception e) {
                    Toast.makeText(LoginActivity.this, "Login failed: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        }
    }
}
