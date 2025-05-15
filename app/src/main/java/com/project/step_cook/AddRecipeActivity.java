package com.project.step_cook;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.core.content.ContextCompat;

public class AddRecipeActivity extends AppCompatActivity implements TimerDialog.OnTimeSetListener {

    private ImageView backButton;
    private LinearLayout stepsContainer;
    private LayoutInflater inflater;
    private ImageView addStepButton;
    private ImageButton recipeImageButton;
    private Button saveRecipeButton;
    private EditText recipeTitleEditText;
    private TextView cookTimeText;
    private AutoCompleteTextView difficultySelect;
    private String selectedDifficulty;

    private DialogManager dialogManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // Force Light Mode
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_recipe);

        inflater = LayoutInflater.from(this);

        backButton = findViewById(R.id.backButton);
        stepsContainer = findViewById(R.id.stepsContainer);
        addStepButton = findViewById(R.id.addStepButton);
        recipeImageButton = findViewById(R.id.recipeImageButton);
        recipeImageButton.setAdjustViewBounds(true);
        recipeImageButton.setScaleType(ImageView.ScaleType.CENTER_CROP);
        recipeImageButton.setBackgroundResource(R.drawable.rounded_image_background);
        saveRecipeButton = findViewById(R.id.saveRecipeButton);
        recipeTitleEditText = findViewById(R.id.recipeTitle);
        cookTimeText = findViewById(R.id.cookTime);

        dialogManager = new DialogManager(this);

        backButton.setOnClickListener(view -> finish());

        addStepButton.setOnClickListener(view -> addNewStep());

        recipeImageButton.setOnClickListener(v -> dialogManager.showImageSourceDialog());

        saveRecipeButton.setOnClickListener(view -> saveRecipe());

        difficultySelect = findViewById(R.id.difficultySelect);
        String[] difficulties = getResources().getStringArray(R.array.difficulties);

        // Create an adapter with the predefined dropdown layout
        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                this,
                R.layout.dropdown_item,
                difficulties
        );

        // Set the adapter to the AutoCompleteTextView
        difficultySelect.setAdapter(adapter);

        difficultySelect.setOnItemClickListener((parent, view, position, id) -> {
            selectedDifficulty = difficulties[position];
            difficultySelect.setError(null);
        });

        difficultySelect.setOnClickListener(v -> difficultySelect.setError(null));

        // Add a first step by default
        addNewStep();
    }

    private void saveRecipe(){
        String title = recipeTitleEditText.getText().toString().trim();
        selectedDifficulty = difficultySelect.getText().toString().trim();

        if (title.isEmpty()) {
            recipeTitleEditText.setError("Please enter a recipe title");
            recipeTitleEditText.requestFocus();
            return;
        }

        if (selectedDifficulty.isEmpty()) {
            difficultySelect.setError("Please select a difficulty level");
            difficultySelect.requestFocus();
            return;
        }

        // Check if we have at least one step
        if (getStepCount() == 0) {
            Toast.makeText(this, "Please add at least one step", Toast.LENGTH_SHORT).show();
            return;
        }

        // Show loading indicator
        Toast.makeText(this, "Saving recipe...", Toast.LENGTH_SHORT).show();
    }

    // This method is triggered by the XML android:onClick="clickTimer"
    public void clickTimer(View view) {
        // Find the parent view (step_field LinearLayout)
        View stepView = (View) view.getParent();

        // Show the timer dialog
        TimerDialog dialog = new TimerDialog(this, stepView, this);
        dialog.show();
    }

    @Override
    public void onTimeSet(int hours, int minutes, View stepView) {
        // Only process if there's actually a time set (hours + minutes > 0)
        if (hours == 0 && minutes == 0) {
            // If user set both to zero, clear the timer
            clearTimer(stepView);
            return;
        }

        // Format the time string
        String timeText = formatTime(0, hours, minutes);

        // Find the timer icon in the step view and update its state
        ImageView timerIcon = stepView.findViewById(R.id.timerIcon);

        // Add a tag to the timer icon to store the time
        timerIcon.setTag(hours * 60 + minutes); // Store total minutes as a tag;

        // Add or update the time label
        addTimeLabel(stepView, timeText);

        updateTotalCookTime();
    }

    private void clearTimer(View stepView) {
        // Find the timer container
        LinearLayout timerContainer = (LinearLayout) stepView.findViewWithTag("timerContainer");

        if (timerContainer != null) {
            // Get the parent layout
            LinearLayout parentLayout = (LinearLayout) timerContainer.getParent();
            int containerIndex = parentLayout.indexOfChild(timerContainer);

            // Find the timer icon inside the container
            ImageView timerIcon = timerContainer.findViewById(R.id.timerIcon);

            // Reset timer icon properties
            timerIcon.setTag(null);
            timerIcon.setImageResource(R.drawable.timer);

            // Remove the timer icon from the container
            timerContainer.removeView(timerIcon);

            // Remove the container from the parent
            parentLayout.removeView(timerContainer);

            // Add the timer icon back to the parent at the same position
            parentLayout.addView(timerIcon, containerIndex);

            // Make sure the timer icon has the click listener
            timerIcon.setOnClickListener(this::clickTimer);

            updateTotalCookTime();

            Toast.makeText(this, "Timer cleared", Toast.LENGTH_SHORT).show();
        }
    }

    public String formatTime(int days, int hours, int minutes) {
        StringBuilder sb = new StringBuilder();

        if (days > 0) {
            sb.append(days).append("d ");
        }

        if (hours > 0 || days > 0) {
            sb.append(hours).append("h ");
        }

        if (minutes > 0 || (hours == 0 && days == 0)) {
            // Show minutes if there are any or if both hours and days are 0
            sb.append(minutes).append("m");
        }

        return sb.toString().trim();
    }

    // Format total minutes into a readable string with hours and minutes
    public String formatTotalTime(int totalMinutes) {
        // Calculate days
        int days = totalMinutes / (24 * 60);

        // Calculate remaining hours after removing days
        int remainingMinutes = totalMinutes % (24 * 60);
        int hours = remainingMinutes / 60;

        // Calculate remaining minutes
        int minutes = remainingMinutes % 60;

        return formatTime(days, hours, minutes);
    }

    private void addTimeLabel(View stepView, String timeText) {
        // Check if time label already exists
        TextView timeLabel = stepView.findViewWithTag("timeLabel");
        ImageView timerIcon = stepView.findViewById(R.id.timerIcon);

        if (timeLabel == null) {
            // Create a new time label
            timeLabel = new TextView(this);
            timeLabel.setTag("timeLabel");
            timeLabel.setGravity(android.view.Gravity.CENTER);

            // Create a vertical container for the timer icon and label if it doesn't exist
            LinearLayout timerContainer = new LinearLayout(this);
            timerContainer.setOrientation(LinearLayout.VERTICAL);
            timerContainer.setGravity(android.view.Gravity.CENTER);
            timerContainer.setLayoutParams(new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.WRAP_CONTENT,
                    LinearLayout.LayoutParams.MATCH_PARENT));
            timerContainer.setTag("timerContainer");

            // Set the timer icon to match parent width so it centers properly
            LinearLayout.LayoutParams iconParams = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.WRAP_CONTENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT);
            iconParams.gravity = android.view.Gravity.CENTER;

            // Save the original parent and position
            LinearLayout originalParent = (LinearLayout) timerIcon.getParent();
            int originalIndex = originalParent.indexOfChild(timerIcon);

            // Remove the timer icon from its current position
            originalParent.removeView(timerIcon);

            // Add the timer icon to the container with proper layout
            timerIcon.setLayoutParams(iconParams);
            timerContainer.addView(timerIcon);

            // Add the label to the container below the timer icon
            LinearLayout.LayoutParams labelParams = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.WRAP_CONTENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT);

            timeLabel.setLayoutParams(labelParams);
            timerContainer.addView(timeLabel);

            // Add the container to the original parent at the original position
            originalParent.addView(timerContainer, originalIndex);
        } else {
            // Just update the text if label already exists
            timeLabel.setText(timeText);
        }

        // Set or update the time text
        timeLabel.setText(timeText);
        timeLabel.setTextColor(ContextCompat.getColor(this, R.color.orange));
        timeLabel.setTextSize(12);
    }

    private void addNewStep() {
        View stepView = inflater.inflate(R.layout.step_field, stepsContainer, false);

        // Calculate the step number (count existing step fields + 1)
        int stepNumber = getStepCount() + 1;

        // Set the hint with step number
        EditText stepDetail = stepView.findViewById(R.id.stepDetail);
        stepDetail.setHint("Step " + stepNumber + ": " + getString(R.string.step_description));

        // Store the step number as a tag
        stepView.setTag("step_" + stepNumber);

        // Add listener for remove button
        ImageView removeButton = stepView.findViewById(R.id.removeStepButton);
        removeButton.setOnClickListener(v -> {
            // Remove the step view
            stepsContainer.removeView(stepView);

            // Update all remaining step numbers
            updateStepNumbers();

            updateTotalCookTime();
        });

        stepsContainer.addView(stepView);
    }

    // Recalculate and update the total cooking time displayed
    private void updateTotalCookTime() {
        int totalMinutes = calculateTotalTime();
        String formattedTime = formatTotalTime(totalMinutes);
        cookTimeText.setText(getString(R.string.cook_time) + " " + formattedTime);
    }

    // Calculate the total time from all step timers
    private int calculateTotalTime() {
        int totalMinutes = 0;

        for (int i = 0; i < stepsContainer.getChildCount(); i++) {
            View stepView = stepsContainer.getChildAt(i);

            // Find timer container or icon
            LinearLayout timerContainer = stepView.findViewWithTag("timerContainer");
            if (timerContainer != null) {
                ImageView timerIcon = timerContainer.findViewById(R.id.timerIcon);
                if (timerIcon != null && timerIcon.getTag() != null) {
                    totalMinutes += (int) timerIcon.getTag();
                }
            } else {
                ImageView timerIcon = stepView.findViewById(R.id.timerIcon);
                if (timerIcon != null && timerIcon.getTag() != null) {
                    totalMinutes += (int) timerIcon.getTag();
                }
            }
        }

        return totalMinutes;
    }

    // Count the actual step views (not headers or other views)
    private int getStepCount() {
        int count = 0;
        for (int i = 0; i < stepsContainer.getChildCount(); i++) {
            View child = stepsContainer.getChildAt(i);
            if (child.getTag() != null && child.getTag().toString().startsWith("step_")) {
                count++;
            }
        }
        return count;
    }

    // Update step numbers after removing a step
    private void updateStepNumbers() {
        int stepCount = 0;

        for (int i = 0; i < stepsContainer.getChildCount(); i++) {
            View child = stepsContainer.getChildAt(i);

            if (child.getTag() != null && child.getTag().toString().startsWith("step_")) {
                stepCount++;
                child.setTag("step_" + stepCount);

                // Update the hint text
                EditText stepDetail = child.findViewById(R.id.stepDetail);
                stepDetail.setHint("Step " + stepCount + ": " + getString(R.string.step_description));
            }
        }
    }
}