package com.project.step_cook;

import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import android.view.View;
import android.widget.Toast;

public class AddRecipeActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_recipe);
    }

    // This method is triggered by the XML android:onClick="clickTimer"
    public void clickTimer(View view) {
        // Handle the click event here
        Toast.makeText(this, "Timer icon clicked!", Toast.LENGTH_SHORT).show();

        // You can use view.getId() if you need to distinguish between multiple buttons
    }
}