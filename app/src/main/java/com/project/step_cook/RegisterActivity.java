package com.project.step_cook;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.util.HashMap;
import java.util.Map;

import com.google.firebase.Firebase;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

public class RegisterActivity extends AppCompatActivity {

    private EditText userName;
    private EditText userEmail;
    private EditText userPassword;
    private EditText confirmPassword;
    private Button signupButton;
    private ImageView backButton;

    private FirebaseAuth auth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        userName = findViewById(R.id.userName);
        userEmail = findViewById(R.id.userEmail);
        userPassword = findViewById(R.id.userPassword);
        confirmPassword = findViewById(R.id.confirmPassword);
        signupButton = findViewById(R.id.signupButton);
        backButton = findViewById(R.id.backButton);

        auth = FirebaseAuth.getInstance();

        signupButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String txt_user = userName.getText().toString();
                String txt_email = userEmail.getText().toString();
                String txt_pass = userPassword.getText().toString();
                String txt_confirm_pass = confirmPassword.getText().toString();

                if (TextUtils.isEmpty(txt_user) || TextUtils.isEmpty(txt_email) || TextUtils.isEmpty(txt_pass) || TextUtils.isEmpty(txt_confirm_pass)){
                    Toast.makeText(RegisterActivity.this, "Missing Fields.", Toast.LENGTH_LONG).show();
                } else if (txt_pass.length() < 6) {
                    Toast.makeText(RegisterActivity.this, "Password is to short.", Toast.LENGTH_LONG).show();
                } else if (!txt_pass.equals(txt_confirm_pass)) {
                    Toast.makeText(RegisterActivity.this, "Passwords don't match.", Toast.LENGTH_LONG).show();
                } else {
                    registerUser(txt_user, txt_email, txt_pass);
                }
            }
        });

        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }

    private void registerUser(String userName, String email, String password) {
        auth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                FirebaseUser firebaseUser = auth.getCurrentUser();
                String userId = firebaseUser.getUid();

                // Prepare user data
                Map<String, Object> userData = new HashMap<>();
                userData.put("userName", userName);
                userData.put("email", email);

                // Save to Firestore
                FirebaseFirestore db = FirebaseFirestore.getInstance();
                db.collection("Users").document(userId).set(userData)
                        .addOnSuccessListener(aVoid -> {
                            Toast.makeText(RegisterActivity.this, "User registered and data saved!", Toast.LENGTH_SHORT).show();
                            startActivity(new Intent(RegisterActivity.this, MainActivity.class));
                            finish();
                        })
                        .addOnFailureListener(e -> {
                            Toast.makeText(RegisterActivity.this, "Failed to save user data: " + e.getMessage(), Toast.LENGTH_LONG).show();
                        });
            } else {
                Toast.makeText(RegisterActivity.this, "Registration failed: " + task.getException().getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }

}