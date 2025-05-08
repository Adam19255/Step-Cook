package com.project.step_cook;

import android.os.Bundle;
import android.view.MenuItem;
import android.widget.ImageButton;
import android.widget.PopupMenu;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    ImageButton filterButton, settingsButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        filterButton = findViewById(R.id.filterButton);
        settingsButton = findViewById(R.id.settingsButton);

        filterButton.setOnClickListener(v -> {
            PopupMenu popup = new PopupMenu(MainActivity.this, v);
            popup.getMenu().add(0, 1, 0, "Filter by Date");
            popup.getMenu().add(0, 2, 1, "Favorites Only");
            popup.getMenu().add(0, 3, 2, "By Recipe Type");

            popup.setOnMenuItemClickListener(item -> {
                Toast.makeText(MainActivity.this, "Selected: " + item.getTitle(), Toast.LENGTH_SHORT).show();
                return true;
            });

            popup.show();
        });

        settingsButton.setOnClickListener(v ->
                Toast.makeText(this, "Settings button clicked", Toast.LENGTH_SHORT).show()
        );
    }
}
