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
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class SettingsActivity extends AppCompatActivity {
    private static final String TAG = "SettingsActivity";
    private ImageView backButton;
    private AppCompatButton editProfileButton;
    private SwitchCompat notificationSwitch;
    private SwitchCompat autoPlayNextStepSwitch;
    private RelativeLayout aboutUsButton;
    private RelativeLayout logoutButton;
    private TextView userNameTextView;
    private TextView userEmailTextView;

    private FirebaseAuth auth;
    private FirebaseFirestore db;
    private String userId;

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
        userNameTextView = findViewById(R.id.userNameTextView);
        userEmailTextView = findViewById(R.id.userEmailTextView);

        auth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        // Get current user
        FirebaseUser currentUser = auth.getCurrentUser();
        if (currentUser != null) {
            userId = currentUser.getUid();
            loadUserData();
        }

        dialogManager = new DialogManager(this);

        backButton.setOnClickListener(v -> finish());

        editProfileButton.setOnClickListener(v -> dialogManager.showEditProfileDialog());

        notificationSwitch.setOnCheckedChangeListener((buttonView, isChecked) -> {
            updateUserPreference("notificationsEnabled", isChecked);
        });

        autoPlayNextStepSwitch.setOnCheckedChangeListener((buttonView, isChecked) -> {
            updateUserPreference("autoPlayNextStep", isChecked);
        });

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

    private void loadUserData() {
        DocumentReference userRef = db.collection("Users").document(userId);
        userRef.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                DocumentSnapshot document = task.getResult();
                if (document.exists()) {
                    // Set user info
                    String userName = document.getString("userName");
                    String email = document.getString("email");
                    userNameTextView.setText(userName);
                    userEmailTextView.setText(email);

                    // Set switch states based on stored preferences
                    Boolean notificationsEnabled = document.getBoolean("notificationsEnabled");
                    Boolean autoPlayNextStep = document.getBoolean("autoPlayNextStep");

                    // If preferences don't exist yet (for existing users before this feature), set defaults
                    if (notificationsEnabled == null) notificationsEnabled = true;
                    if (autoPlayNextStep == null) autoPlayNextStep = true;

                    notificationSwitch.setChecked(notificationsEnabled);
                    autoPlayNextStepSwitch.setChecked(autoPlayNextStep);
                } else {
                    Log.d(TAG, "No such document");
                    Toast.makeText(SettingsActivity.this, "User data not found", Toast.LENGTH_SHORT).show();
                }
            } else {
                Log.d(TAG, "get failed with ", task.getException());
                Toast.makeText(SettingsActivity.this, "Failed to load user data", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void updateUserPreference(String preferenceKey, boolean value) {
        if (userId == null) return;

        db.collection("Users").document(userId).update(preferenceKey, value)
                .addOnSuccessListener(aVoid -> {
                    Log.d(TAG, "Preference " + preferenceKey + " updated successfully");
                })
                .addOnFailureListener(e -> {
                    Log.w(TAG, "Error updating preference", e);
                    Toast.makeText(SettingsActivity.this, "Failed to update setting", Toast.LENGTH_SHORT).show();

                    // Revert the switch state if update fails
                    if (preferenceKey.equals("notificationsEnabled")) {
                        notificationSwitch.setChecked(!value);
                    } else if (preferenceKey.equals("autoPlayNextStep")) {
                        autoPlayNextStepSwitch.setChecked(!value);
                    }
                });
    }
}