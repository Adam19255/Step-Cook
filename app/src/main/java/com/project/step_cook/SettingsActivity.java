package com.project.step_cook;

import android.app.AlertDialog;
import android.os.Build;
import android.os.Bundle;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toast;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.appcompat.widget.SwitchCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

public class SettingsActivity extends AppCompatActivity {
    private ImageView backButton;
    private AppCompatButton editProfileButton;
    private SwitchCompat notificationSwitch;
    private SwitchCompat autoPlayNextStepSwitch;
    private RelativeLayout aboutUsButton;
    private RelativeLayout logoutButton;

    private FirebaseAuth auth;
    private FirebaseFirestore db;

    private DialogManager dialogManager;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        backButton = findViewById(R.id.backButton);
        editProfileButton = findViewById(R.id.editProfileButton);
        notificationSwitch = findViewById(R.id.notificationSwitch);
        autoPlayNextStepSwitch = findViewById(R.id.autoPlayNextStepSwitch);
        aboutUsButton = findViewById(R.id.aboutUsButton);
        logoutButton = findViewById(R.id.logoutButton);

        auth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        dialogManager = new DialogManager(this);

        backButton.setOnClickListener(v -> finish());

        editProfileButton.setOnClickListener(v -> dialogManager.showEditProfileDialog());

        aboutUsButton.setOnClickListener(v -> dialogManager.showAboutDialog());

        logoutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                auth.signOut();
                Intent intent = new Intent(SettingsActivity.this, LoginActivity.class);
                // Clear the entire activity stack and make LoginActivity the new root
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
                finish();
            }
        });
    }
}