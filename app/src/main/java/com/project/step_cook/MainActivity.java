package com.project.step_cook;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity implements PopupMenu.OnMenuItemClickListener {

    private ImageView popupMenuButton;
    private EditText searchInput;
    private Button difficultyFilter;
    private Button favoriteFilter;
    private Button cookTimeFilter;
    private ImageView addRecipeButton;

    // Filter managers
    private FilterManager difficultyFilterManager;
    private FilterManager cookTimeFilterManager;
    private FavoriteFilterManager favoriteFilterManager;

    // UI utils
    private DialogManager dialogManager;
    private UIHelper uiHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initializeViews();
        initializeManagers();
        setupClickListeners();
    }

    private void initializeViews() {
        popupMenuButton = findViewById(R.id.popupMenuButton);
        searchInput = findViewById(R.id.searchInput);
        difficultyFilter = findViewById(R.id.difficultyFilter);
        favoriteFilter = findViewById(R.id.favoriteFilter);
        cookTimeFilter = findViewById(R.id.cookTimeFilter);
        addRecipeButton = findViewById(R.id.addRecipeButton);

        searchInput.clearFocus();
    }

    private void initializeManagers() {
        uiHelper = new UIHelper(this);
        dialogManager = new DialogManager(this);

        difficultyFilterManager = new DifficultyFilterManager(this, difficultyFilter);
        cookTimeFilterManager = new CookTimeFilterManager(this, cookTimeFilter);
        favoriteFilterManager = new FavoriteFilterManager(this, favoriteFilter);
    }

    private void setupClickListeners() {
        popupMenuButton.setOnClickListener(v -> showPopupMenu(v));

        addRecipeButton.setOnClickListener(v ->
                startActivity(new Intent(MainActivity.this, AddRecipeActivity.class)));

        difficultyFilter.setOnClickListener(v -> difficultyFilterManager.showFilterDialog());

        favoriteFilter.setOnClickListener(v -> favoriteFilterManager.toggleFilter());

        cookTimeFilter.setOnClickListener(v -> cookTimeFilterManager.showFilterDialog());
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
            dialogManager.showAboutDialog();
            return true;
        } else if (id == R.id.menu_exit) {
            dialogManager.showExitConfirmationDialog();
            return true;
        }

        return false;
    }
}