package com.project.step_cook;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.res.ColorStateList;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;
import android.graphics.Color;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.core.content.ContextCompat;

public class MainActivity extends AppCompatActivity implements PopupMenu.OnMenuItemClickListener {

    private ImageView popupMenuButton;
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
    private final String DIFFICULTY_FILTER = "difficultyFilter";
    private final String COOK_TIME_FILTER = "cookTimeFilter";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        popupMenuButton = findViewById(R.id.popupMenuButton);
        searchInput = findViewById(R.id.searchInput);
        difficultyFilter = findViewById(R.id.difficultyFilter);
        favoriteFilter = findViewById(R.id.favoriteFilter);
        cookTimeFilter = findViewById(R.id.cookTimeFilter);
        addRecipeButton = findViewById(R.id.addRecipeButton);

        searchInput.clearFocus();

        popupMenuButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showPopupMenu(view);
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
                changeButtonColor(true, difficultyFilter);
                showDifficultyDialog();
            }
        });

        favoriteFilter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Toggle the state
                isFavoriteFilterActive = !isFavoriteFilterActive;
                // Change the color based on the state
                changeButtonColor(isFavoriteFilterActive, favoriteFilter);
                Toast.makeText(MainActivity.this,
                        isFavoriteFilterActive ? "Favorites filter activated" : "Favorites filter deactivated",
                        Toast.LENGTH_SHORT).show();

                applyFavoriteFilter();
            }
        });

        cookTimeFilter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                changeButtonColor(true, cookTimeFilter);
                showCookTimeDialog();
            }
        });
    }

    private void showPopupMenu(View view) {
        PopupMenu popupMenu = new PopupMenu(this, view);
        popupMenu.setOnMenuItemClickListener(this);
        popupMenu.inflate(R.menu.popup_menu);
        popupMenu.show();
    }

    @Override
    public boolean onMenuItemClick(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.menu_settings) {
             startActivity(new Intent(MainActivity.this, SettingsActivity.class));
            return true;
        } else if (id == R.id.menu_about) {
            showAboutDialog();
            return true;
        } else if (id == R.id.menu_exit) {
            showExitConfirmationDialog();
            return true;
        }

        return false;
    }

    private void showAboutDialog() {
        AboutUsDialog dialog = new AboutUsDialog(this);
        dialog.show();
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
        ImageView closeButton = difficultyDialog.findViewById(R.id.closeButton);

        // Highlight the currently selected difficulty
        highlightSelectedFilter(allDifficulties, easyDifficulty, mediumDifficulty, hardDifficulty, DIFFICULTY_FILTER);

        allDifficulties.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                selectedDifficulty = "All";

                // Update the highlight
                highlightSelectedFilter(allDifficulties, easyDifficulty, mediumDifficulty, hardDifficulty, DIFFICULTY_FILTER);

                // Apply the filter immediately
                applyDifficultyFilter(selectedDifficulty);

                difficultyDialog.dismiss();
                changeButtonColor(false, difficultyFilter);
                Toast.makeText(MainActivity.this, "All difficulties clicked", Toast.LENGTH_SHORT).show();
            }
        });

        easyDifficulty.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                selectedDifficulty = "Easy";
                highlightSelectedFilter(allDifficulties, easyDifficulty, mediumDifficulty, hardDifficulty, DIFFICULTY_FILTER);
                applyDifficultyFilter(selectedDifficulty);
                difficultyDialog.dismiss();
                changeButtonColor(false, difficultyFilter);
                Toast.makeText(MainActivity.this, "Easy difficulty clicked", Toast.LENGTH_SHORT).show();
            }
        });

        mediumDifficulty.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                selectedDifficulty = "Medium";
                highlightSelectedFilter(allDifficulties, easyDifficulty, mediumDifficulty, hardDifficulty, DIFFICULTY_FILTER);
                applyDifficultyFilter(selectedDifficulty);
                difficultyDialog.dismiss();
                changeButtonColor(false, difficultyFilter);
                Toast.makeText(MainActivity.this, "Medium difficulty clicked", Toast.LENGTH_SHORT).show();
            }
        });

        hardDifficulty.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                selectedDifficulty = "Hard";
                highlightSelectedFilter(allDifficulties, easyDifficulty, mediumDifficulty, hardDifficulty, DIFFICULTY_FILTER);
                applyDifficultyFilter(selectedDifficulty);
                difficultyDialog.dismiss();
                changeButtonColor(false, difficultyFilter);
                Toast.makeText(MainActivity.this, "Hard difficulty clicked", Toast.LENGTH_SHORT).show();
            }
        });

        closeDialog.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                difficultyDialog.dismiss();
                changeButtonColor(false, difficultyFilter);
            }
        });

        closeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                difficultyDialog.dismiss();
                changeButtonColor(false, difficultyFilter);
            }
        });

        difficultyDialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialog) {
                // Reset button color when dialog is dismissed by clicking outside
                changeButtonColor(false, difficultyFilter);
            }
        });

        difficultyDialog.show();
    }

    private void showCookTimeDialog(){
        if (cookTimeDialog != null) {
            if (!cookTimeDialog.isShowing()) {
                cookTimeDialog.show();
            }
            return;
        }

        cookTimeDialog = new Dialog(this);
        cookTimeDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        cookTimeDialog.setContentView(R.layout.time_filter_layout);
        
        Window window = cookTimeDialog.getWindow();
        if (window != null) {
            window.setLayout(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
            window.setGravity(android.view.Gravity.BOTTOM);
            window.setBackgroundDrawableResource(android.R.color.transparent);
            window.setWindowAnimations(R.style.DialogAnimation);
        }
        
        LinearLayout allCookTimes = cookTimeDialog.findViewById(R.id.allCookTimes);
        LinearLayout fastCookTime = cookTimeDialog.findViewById(R.id.fastCookTime);
        LinearLayout mediumCookTime = cookTimeDialog.findViewById(R.id.mediumCookTime);
        LinearLayout longCookTime = cookTimeDialog.findViewById(R.id.longCookTime);
        ImageView closeDialog = cookTimeDialog.findViewById(R.id.closeDialog);
        ImageView closeButton = cookTimeDialog.findViewById(R.id.closeButton);
        
        highlightSelectedFilter(allCookTimes, fastCookTime, mediumCookTime, longCookTime, COOK_TIME_FILTER);

        allCookTimes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                selectedCookTime = "All";
                highlightSelectedFilter(allCookTimes, fastCookTime, mediumCookTime, longCookTime, COOK_TIME_FILTER);
                applyCookTimeFilter(selectedCookTime);
                cookTimeDialog.dismiss();
                changeButtonColor(false, cookTimeFilter);
                Toast.makeText(MainActivity.this, "All cook times clicked", Toast.LENGTH_SHORT).show();
            }
        });

        fastCookTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                selectedCookTime = "Fast";
                highlightSelectedFilter(allCookTimes, fastCookTime, mediumCookTime, longCookTime, COOK_TIME_FILTER);
                applyCookTimeFilter(selectedCookTime);
                cookTimeDialog.dismiss();
                changeButtonColor(false, cookTimeFilter);
                Toast.makeText(MainActivity.this, "Fast cook time clicked", Toast.LENGTH_SHORT).show();
            }
        });

        mediumCookTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                selectedCookTime = "Medium";
                highlightSelectedFilter(allCookTimes, fastCookTime, mediumCookTime, longCookTime, COOK_TIME_FILTER);
                applyCookTimeFilter(selectedCookTime);
                cookTimeDialog.dismiss();
                changeButtonColor(false, cookTimeFilter);
                Toast.makeText(MainActivity.this, "Medium cook time clicked", Toast.LENGTH_SHORT).show();
            }
        });

        longCookTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                selectedCookTime = "Long";
                highlightSelectedFilter(allCookTimes, fastCookTime, mediumCookTime, longCookTime, COOK_TIME_FILTER);
                applyCookTimeFilter(selectedCookTime);
                cookTimeDialog.dismiss();
                changeButtonColor(false, cookTimeFilter);
                Toast.makeText(MainActivity.this, "Long cook time clicked", Toast.LENGTH_SHORT).show();
            }
        });

        closeDialog.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                cookTimeDialog.dismiss();
                changeButtonColor(false, cookTimeFilter);
            }
        });

        closeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                cookTimeDialog.dismiss();
                changeButtonColor(false, cookTimeFilter);
            }
        });

        cookTimeDialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialog) {
                // Reset button color when dialog is dismissed by clicking outside
                changeButtonColor(false, cookTimeFilter);
            }
        });

        cookTimeDialog.show();
    }

    // Highlight the selected difficulty
    private void highlightSelectedFilter(LinearLayout allLayout, LinearLayout easyLayout, LinearLayout mediumLayout, LinearLayout hardLayout, String fromWhere) {
        // Reset all backgrounds first
        allLayout.setBackgroundColor(ContextCompat.getColor(this, android.R.color.transparent));
        easyLayout.setBackgroundColor(ContextCompat.getColor(this, android.R.color.transparent));
        mediumLayout.setBackgroundColor(ContextCompat.getColor(this, android.R.color.transparent));
        hardLayout.setBackgroundColor(ContextCompat.getColor(this, android.R.color.transparent));

        // Highlight the selected one
        LinearLayout selectedLayout = null;

        if (fromWhere.equals("difficultyFilter")){
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
        }
        
        if (fromWhere.equals("cookTimeFilter")){
            switch (selectedCookTime) {
                case "All":
                    selectedLayout = allLayout;
                    break;
                case "Fast":
                    selectedLayout = easyLayout;
                    break;
                case "Medium":
                    selectedLayout = mediumLayout;
                    break;
                case "Long":
                    selectedLayout = hardLayout;
                    break;
            }
        }
        
        if (selectedLayout != null) {
            // Create a highlight background
            int highlightColor = ContextCompat.getColor(this, R.color.orange);
            highlightColor = Color.argb(50, Color.red(highlightColor), Color.green(highlightColor), Color.blue(highlightColor));
            selectedLayout.setBackgroundColor(highlightColor);
        }
    }

    private void changeButtonColor(boolean isClicked, Button filter){
        if (isClicked){
            filter.setBackgroundTintList(ColorStateList.valueOf(
                    ContextCompat.getColor(MainActivity.this, R.color.orange)));
            filter.setTextColor(ContextCompat.getColor(
                    MainActivity.this, android.R.color.white));
        } else {
            // Change back to gray when inactive
            filter.setBackgroundTintList(ColorStateList.valueOf(
                    ContextCompat.getColor(MainActivity.this, R.color.gray)));
            filter.setTextColor(ContextCompat.getColor(
                    MainActivity.this, R.color.black));
        }
    }

    // This method would contain your actual filtering logic
    private void applyDifficultyFilter(String difficulty) {}

    private void applyFavoriteFilter(){}

    private void applyCookTimeFilter(String cookTime){}

    // Show confirmation dialog before exiting the app
    private void showExitConfirmationDialog() {
        androidx.appcompat.app.AlertDialog.Builder builder = new androidx.appcompat.app.AlertDialog.Builder(this);
        builder.setTitle("Exit App");
        builder.setMessage("Are you sure you want to exit?");

        // Add the buttons
        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // User clicked Yes button
                finish(); // Close the app
            }
        });

        builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // User cancelled the dialog
                dialog.dismiss();
            }
        });

        // Create and show the AlertDialog
        androidx.appcompat.app.AlertDialog dialog = builder.create();
        dialog.show();
    }
}
