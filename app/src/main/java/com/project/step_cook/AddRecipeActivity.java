package com.project.step_cook;

import android.content.Intent;
import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

public class AddRecipeActivity extends AppCompatActivity implements TimerDialog.OnTimeSetListener {

    private ImageView backButton;
    private LinearLayout stepsContainer;
    private LayoutInflater inflater;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // Force Light Mode
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_recipe);

        inflater = LayoutInflater.from(this);

        backButton = findViewById(R.id.backButton);
        stepsContainer = findViewById(R.id.stepsContainer);
        ImageView addStepButton = findViewById(R.id.addStepButton);

        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        addStepButton.setOnClickListener(view -> addNewStep());
    }

    // This method is triggered by the XML android:onClick="clickTimer"
    public void clickTimer(View view) {
        // Find the parent view (step_field LinearLayout)
        View stepView = (View) view.getParent();

        // Show the timer dialog
        TimerDialog dialog = new TimerDialog(this, stepView, this);
        dialog.show();
    }

    // Helper method to find the step view from a timer icon
    private View findStepViewFromTimerIcon(View timerIcon) {
        // Check if the timer is already in a container
        View parent = (View) timerIcon.getParent();
        if (parent.getTag() != null && "timerContainer".equals(parent.getTag())) {
            // If it's in a container, get the container's parent which is the step view
            return (View) parent.getParent();
        } else if (parent instanceof FrameLayout) {
            // If it's in a FrameLayout (from the XML layout), get the FrameLayout's parent
            return (View) parent.getParent();
        }

        // Fallback: look for the step view by tag prefix
        ViewParent currentParent = (ViewParent) parent;
        while (currentParent != null) {
            if (currentParent instanceof View) {
                View currentView = (View) currentParent;
                if (currentView.getTag() != null &&
                        currentView.getTag().toString().startsWith("step_")) {
                    return currentView;
                }
            }
            currentParent = currentParent.getParent();
        }

        // If we can't find a proper parent, use the direct parent
        return parent;
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
        String timeText = formatTime(hours, minutes);

        // Find the timer icon in the step view and update its state
        ImageView timerIcon = stepView.findViewById(R.id.timerIcon);

        // Add a tag to the timer icon to store the time
        timerIcon.setTag(hours * 60 + minutes); // Store total minutes as a tag;

        // Add or update the time label
        addTimeLabel(stepView, timeText);
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

            Toast.makeText(this, "Timer cleared", Toast.LENGTH_SHORT).show();
        }
    }

    private String formatTime(int hours, int minutes) {
        StringBuilder sb = new StringBuilder();
        if (hours > 0) {
            sb.append(hours).append("h ");
        }
        if (minutes > 0 || hours == 0) { // Show minutes if there are any or if hours is 0
            sb.append(minutes).append("m");
        }
        return sb.toString().trim();
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
        });

        stepsContainer.addView(stepView);
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