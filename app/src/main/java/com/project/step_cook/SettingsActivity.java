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

        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        editProfileButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showEditProfileDialog();
            }
        });

        aboutUsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showAboutDialog();
            }
        });

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

    private void showEditProfileDialog() {
        EditProfileDialog dialog = new EditProfileDialog(this);
        dialog.show();
    }

    private void showAboutDialog() {
        // Inflate the custom layout
        View dialogView = LayoutInflater.from(this).inflate(R.layout.about_us_dialog_layout, null);

        // Get references to TextView fields
        TextView appIdTextView = dialogView.findViewById(R.id.app_id);
        TextView appVersionTextView = dialogView.findViewById(R.id.app_version);
        TextView osInfoTextView = dialogView.findViewById(R.id.os_info);
        AppCompatButton okButton = dialogView.findViewById(R.id.ok_button);

        // Get application package name and version
        String packageName = getApplicationContext().getPackageName();
        String versionName = "1.0";

        // Try to get the actual version name
        try {
            PackageInfo packageInfo = getPackageManager().getPackageInfo(packageName, 0);
            versionName = packageInfo.versionName;
        } catch (PackageManager.NameNotFoundException e) {
            Log.e("SettingsActivity", "Package name not found", e);
        }

        // Get OS information
        String osInfo = "Android " + Build.VERSION.RELEASE + " (API level " + Build.VERSION.SDK_INT + ")";

        // Set values to TextViews
        appIdTextView.setText(getString(R.string.application_id_format, packageName));
        appVersionTextView.setText(getString(R.string.version_format, versionName));
        osInfoTextView.setText(getString(R.string.os_info_format, osInfo));

        // Create the Dialog
        AlertDialog.Builder builder = new AlertDialog.Builder(this, R.style.CustomAlertDialog);
        builder.setView(dialogView);

        // Create and show the dialog
        final AlertDialog dialog = builder.create();

        // Set window animation and remove default background
        if (dialog.getWindow() != null) {
            dialog.getWindow().getAttributes().windowAnimations = android.R.style.Animation_Dialog;
            dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        }

        // Set click listener for OK button
        okButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        dialog.show();
    }
}