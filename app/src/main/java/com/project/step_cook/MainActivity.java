package com.project.step_cook;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class MainActivity extends AppCompatActivity {

    EditText userEmail;
    EditText userPass;
    Button loginButton;
    TextView signupText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        userEmail = findViewById(R.id.userEmail);
        userPass = findViewById(R.id.userPassword);
        loginButton = findViewById(R.id.loginButton);
        signupText = findViewById(R.id.signupText);

    }

    public void login(View v){
        if (userEmail.getText().toString().equals("adam@gmail.com") && userPass.getText().toString().equals("123456")){
            Toast.makeText(this, "Login Successful!", Toast.LENGTH_LONG).show();
        }
        else{
            Toast.makeText(this, "Login Failed!", Toast.LENGTH_LONG).show();
        }
    }
}