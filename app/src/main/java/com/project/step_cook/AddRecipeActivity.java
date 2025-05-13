package com.project.step_cook;

import android.content.Intent;
import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

public class AddRecipeActivity extends AppCompatActivity {

    private ImageView backButton;
    private LinearLayout stepsContainer;
    private LayoutInflater inflater;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // Force Light Mode
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_recipe);

        inflater = LayoutInflater.from(this); // <-- ADD THIS LINE

        backButton = findViewById(R.id.backButton);
        stepsContainer = findViewById(R.id.stepsContainer);
        ImageView addStepButton = findViewById(R.id.addStepButton);

        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                startActivity(new Intent(AddRecipeActivity.this, MainActivity.class));
                finish();
            }
        });

        addStepButton.setOnClickListener(view -> {
            Toast.makeText(AddRecipeActivity.this, "Clicked on add step", Toast.LENGTH_LONG).show();
            addNewStep();
        });
    }

    // This method is triggered by the XML android:onClick="clickTimer"
    public void clickTimer(View view) {
        // Handle the click event here
        Toast.makeText(this, "Timer icon clicked!", Toast.LENGTH_SHORT).show();


        // You can use view.getId() if you need to distinguish between multiple buttons
    }

    private void addNewStep() {
        View stepView = inflater.inflate(R.layout.step_field, stepsContainer, false);


        // Optionally add listener for remove button
        ImageView removeButton = stepView.findViewById(R.id.removeStepButton);
        removeButton.setOnClickListener(v -> stepsContainer.removeView(stepView));

        stepsContainer.addView(stepView);
    }
}