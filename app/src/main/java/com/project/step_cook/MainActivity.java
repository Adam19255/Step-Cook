package com.project.step_cook;

import android.app.Dialog;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.Toast;
import android.graphics.Color;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

public class MainActivity extends AppCompatActivity {

    private ImageView settingsButton;
    private EditText searchInput;
    private Button difficultyFilter;
    private Button favoriteFilter;
    private Button cookTimeFilter;
    private ImageView addRecipeButton;
    private Dialog difficultyDialog = null;
    private Dialog cookTimeDialog = null;
    private String selectedDifficulty = "All"; // Default option
    private String selectedCookTime = "All";
    private boolean isFavoriteFilterActive = false;


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

        difficultyFilter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showDifficultyDialog();
            }
        });

        favoriteFilter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Toggle the state
                isFavoriteFilterActive = !isFavoriteFilterActive;

                // Change the color based on the state
                if (isFavoriteFilterActive) {
                    // Change to orange when active - use MainActivity.this instead of just this
                    favoriteFilter.setBackgroundTintList(ColorStateList.valueOf(
                            ContextCompat.getColor(MainActivity.this, R.color.orange)));
                    favoriteFilter.setTextColor(ContextCompat.getColor(
                            MainActivity.this, android.R.color.white));
                } else {
                    // Change back to gray when inactive
                    favoriteFilter.setBackgroundTintList(ColorStateList.valueOf(
                            ContextCompat.getColor(MainActivity.this, R.color.gray)));
                    favoriteFilter.setTextColor(ContextCompat.getColor(
                            MainActivity.this, R.color.black));
                }

                Toast.makeText(MainActivity.this,
                        isFavoriteFilterActive ? "Favorites filter activated" : "Favorites filter deactivated",
                        Toast.LENGTH_SHORT).show();

                applyFavoriteFilter();
            }
        });

        cookTimeFilter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showCookTimeDialog();
            }
        });
    }

    private void showDifficultyDialog(){
        // If we already have a dialog, just show it if not showing
        if (difficultyDialog != null) {
            if (!difficultyDialog.isShowing()) {
                difficultyDialog.show();
            }
            return;
        }

        // Create a new dialog if we don't have one
        difficultyDialog = new Dialog(this);
        difficultyDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        difficultyDialog.setContentView(R.layout.difficulty_filter_layout);

        // Set up dialog window properties
        Window window = difficultyDialog.getWindow();
        if (window != null) {
            window.setLayout(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
            window.setGravity(android.view.Gravity.BOTTOM);
            window.setBackgroundDrawableResource(android.R.color.transparent);
            window.setWindowAnimations(R.style.DialogAnimation);
        }

        // Find UI elements
        LinearLayout allDifficulties = difficultyDialog.findViewById(R.id.allDifficulties);
        LinearLayout easyDifficulty = difficultyDialog.findViewById(R.id.easyDifficulty);
        LinearLayout mediumDifficulty = difficultyDialog.findViewById(R.id.mediumDifficulty);
        LinearLayout hardDifficulty = difficultyDialog.findViewById(R.id.hardDifficulty);
        ImageView closeDialog = difficultyDialog.findViewById(R.id.closeDialog);

        // Highlight the currently selected difficulty
        highlightSelectedDifficulty(allDifficulties, easyDifficulty, mediumDifficulty, hardDifficulty);

        allDifficulties.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                selectedDifficulty = "All";

                // Update the highlight
                highlightSelectedDifficulty(allDifficulties, easyDifficulty, mediumDifficulty, hardDifficulty);

                // Apply the filter immediately
                applyDifficultyFilter(selectedDifficulty);

                difficultyDialog.dismiss();
                Toast.makeText(MainActivity.this, "All difficulties clicked", Toast.LENGTH_SHORT).show();
            }
        });

        easyDifficulty.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                selectedDifficulty = "Easy";

                // Update the highlight
                highlightSelectedDifficulty(allDifficulties, easyDifficulty, mediumDifficulty, hardDifficulty);

                // Apply the filter immediately
                applyDifficultyFilter(selectedDifficulty);

                difficultyDialog.dismiss();
                Toast.makeText(MainActivity.this, "Easy difficulty clicked", Toast.LENGTH_SHORT).show();
            }
        });

        mediumDifficulty.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                selectedDifficulty = "Medium";

                // Update the highlight
                highlightSelectedDifficulty(allDifficulties, easyDifficulty, mediumDifficulty, hardDifficulty);

                // Apply the filter immediately
                applyDifficultyFilter(selectedDifficulty);

                difficultyDialog.dismiss();
                Toast.makeText(MainActivity.this, "Medium difficulty clicked", Toast.LENGTH_SHORT).show();
            }
        });

        hardDifficulty.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                selectedDifficulty = "Hard";

                // Update the highlight
                highlightSelectedDifficulty(allDifficulties, easyDifficulty, mediumDifficulty, hardDifficulty);

                // Apply the filter immediately
                applyDifficultyFilter(selectedDifficulty);

                difficultyDialog.dismiss();
                Toast.makeText(MainActivity.this, "Hard difficulty clicked", Toast.LENGTH_SHORT).show();
            }
        });

        closeDialog.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                difficultyDialog.dismiss();
            }
        });

        difficultyDialog.show();
    }

    private void showCookTimeDialog(){
        // If we already have a dialog, just show it if not showing
        if (cookTimeDialog != null) {
            if (!cookTimeDialog.isShowing()) {
                cookTimeDialog.show();
            }
            return;
        }

        // Create a new dialog if we don't have one
        cookTimeDialog = new Dialog(this);
        cookTimeDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        cookTimeDialog.setContentView(R.layout.time_filter_layout);

        // Set up dialog window properties
        Window window = cookTimeDialog.getWindow();
        if (window != null) {
            window.setLayout(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
            window.setGravity(android.view.Gravity.BOTTOM);
            window.setBackgroundDrawableResource(android.R.color.transparent);
            window.setWindowAnimations(R.style.DialogAnimation);
        }

        // Find UI elements
        LinearLayout allCookTimes = cookTimeDialog.findViewById(R.id.allCookTimes);
        LinearLayout fastCookTime = cookTimeDialog.findViewById(R.id.fastCookTime);
        LinearLayout mediumCookTime = cookTimeDialog.findViewById(R.id.mediumCookTime);
        LinearLayout longCookTime = cookTimeDialog.findViewById(R.id.longCookTime);
        ImageView closeDialog = cookTimeDialog.findViewById(R.id.closeDialog);

        // Highlight the currently selected difficulty
        highlightSelectedCookTime(allCookTimes, fastCookTime, mediumCookTime, longCookTime);

        allCookTimes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                selectedCookTime = "All";

                // Update the highlight
                highlightSelectedCookTime(allCookTimes, fastCookTime, mediumCookTime, longCookTime);

                // Apply the filter immediately
                applyCookTimeFilter(selectedCookTime);

                cookTimeDialog.dismiss();
                Toast.makeText(MainActivity.this, "All cook times clicked", Toast.LENGTH_SHORT).show();
            }
        });

        fastCookTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                selectedCookTime = "Fast";

                // Update the highlight
                highlightSelectedCookTime(allCookTimes, fastCookTime, mediumCookTime, longCookTime);

                // Apply the filter immediately
                applyCookTimeFilter(selectedCookTime);

                cookTimeDialog.dismiss();
                Toast.makeText(MainActivity.this, "Fast cook time clicked", Toast.LENGTH_SHORT).show();
            }
        });

        mediumCookTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                selectedCookTime = "Medium";

                // Update the highlight
                highlightSelectedCookTime(allCookTimes, fastCookTime, mediumCookTime, longCookTime);

                // Apply the filter immediately
                applyCookTimeFilter(selectedCookTime);

                cookTimeDialog.dismiss();
                Toast.makeText(MainActivity.this, "Medium cook time clicked", Toast.LENGTH_SHORT).show();
            }
        });

        longCookTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                selectedCookTime = "Long";

                // Update the highlight
                highlightSelectedCookTime(allCookTimes, fastCookTime, mediumCookTime, longCookTime);

                // Apply the filter immediately
                applyCookTimeFilter(selectedCookTime);

                cookTimeDialog.dismiss();
                Toast.makeText(MainActivity.this, "Long cook time clicked", Toast.LENGTH_SHORT).show();
            }
        });

        closeDialog.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                cookTimeDialog.dismiss();
            }
        });

        cookTimeDialog.show();
    }

    // Highlight the selected difficulty
    private void highlightSelectedDifficulty(LinearLayout allLayout, LinearLayout easyLayout, LinearLayout mediumLayout, LinearLayout hardLayout) {
        // Reset all backgrounds first
        allLayout.setBackgroundColor(ContextCompat.getColor(this, android.R.color.transparent));
        easyLayout.setBackgroundColor(ContextCompat.getColor(this, android.R.color.transparent));
        mediumLayout.setBackgroundColor(ContextCompat.getColor(this, android.R.color.transparent));
        hardLayout.setBackgroundColor(ContextCompat.getColor(this, android.R.color.transparent));

        // Highlight the selected one
        LinearLayout selectedLayout = null;

        switch (selectedDifficulty) {
            case "All":
                selectedLayout = allLayout;
                break;
            case "Easy":
                selectedLayout = easyLayout;
                break;
            case "Medium":
                selectedLayout = mediumLayout;
                break;
            case "Hard":
                selectedLayout = hardLayout;
                break;
        }

        if (selectedLayout != null) {
            // Create a highlight background
            int highlightColor = ContextCompat.getColor(this, R.color.orange);
            highlightColor = Color.argb(50, Color.red(highlightColor), Color.green(highlightColor), Color.blue(highlightColor));
            selectedLayout.setBackgroundColor(highlightColor);
        }
    }

    // Highlight the selected cook time
    private void highlightSelectedCookTime(LinearLayout allLayout, LinearLayout fastLayout, LinearLayout mediumLayout, LinearLayout longLayout) {
        // Reset all backgrounds first
        allLayout.setBackgroundColor(ContextCompat.getColor(this, android.R.color.transparent));
        fastLayout.setBackgroundColor(ContextCompat.getColor(this, android.R.color.transparent));
        mediumLayout.setBackgroundColor(ContextCompat.getColor(this, android.R.color.transparent));
        longLayout.setBackgroundColor(ContextCompat.getColor(this, android.R.color.transparent));

        // Highlight the selected one
        LinearLayout selectedLayout = null;

        switch (selectedCookTime) {
            case "All":
                selectedLayout = allLayout;
                break;
            case "Fast":
                selectedLayout = fastLayout;
                break;
            case "Medium":
                selectedLayout = mediumLayout;
                break;
            case "Long":
                selectedLayout = longLayout;
                break;
        }

        if (selectedLayout != null) {
            // Create a highlight background
            int highlightColor = ContextCompat.getColor(this, R.color.orange);
            highlightColor = Color.argb(50, Color.red(highlightColor), Color.green(highlightColor), Color.blue(highlightColor));
            selectedLayout.setBackgroundColor(highlightColor);
        }
    }

    // This method would contain your actual filtering logic
    private void applyDifficultyFilter(String difficulty) {}

    private void applyFavoriteFilter(){}

    private void applyCookTimeFilter(String cookTime){}
}
