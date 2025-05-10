package com.project.step_cook;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    private ImageView settingsButton;
    private EditText searchInput;
    private Button difficultyFilter;
    private Button favoriteFilter;
    private Button cookTimeFilter;
    private ImageView addRecipeButton;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        settingsButton = findViewById(R.id.settingsButton);
        searchInput = findViewById(R.id.searchInput);
        difficultyFilter = findViewById(R.id.difficultyFilter);
        favoriteFilter = findViewById(R.id.favoriteFilter);
        cookTimeFilter = findViewById(R.id.cookTimeFilter);
        addRecipeButton = findViewById(R.id.addRecipeButton);

        searchInput.clearFocus();

        settingsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(MainActivity.this, SettingsActivity.class));
            }
        });

        addRecipeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(MainActivity.this, AddRecipeActivity.class));
            }
        });
        }
}
