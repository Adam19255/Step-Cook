package com.project.step_cook;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewParent;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.core.content.FileProvider;
import android.app.Dialog;
import android.graphics.Bitmap;
import android.os.Environment;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import android.Manifest;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import java.util.ArrayList;
import java.util.List;

public class AddRecipeActivity extends AppCompatActivity implements TimerDialog.OnTimeSetListener {

    private ImageView backButton;
    private LinearLayout stepsContainer;
    private LayoutInflater inflater;
    private ImageButton recipeImageButton;
    private Button saveRecipeButton;
    private EditText recipeTitleEditText;
    private Uri selectedImageUri;
    private RecipeManager recipeManager;
    private static final int STORAGE_PERMISSION_CODE = 101;
    private static final int CAMERA_PERMISSION_CODE = 102;
    private Uri photoURI;
    private String currentPhotoPath;

    private final ActivityResultLauncher<Intent> pickImageLauncher =
            registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
                if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                    selectedImageUri = result.getData().getData();
                    if (selectedImageUri != null) {
                        recipeImageButton.setImageURI(selectedImageUri);
                        // Change scaleType to show the selected image properly
                        recipeImageButton.setScaleType(ImageView.ScaleType.CENTER_CROP);
                    }
                }
            });

    // Activity result launcher for image picking
    private final ActivityResultLauncher<Intent> takePictureLauncher =
            registerForActivityResult(new ActivityResultContracts.StartActivityForResult(),
                    result -> {
                        if (result.getResultCode() == RESULT_OK) {
                            selectedImageUri = photoURI;
                            if (selectedImageUri != null) {
                                recipeImageButton.setImageURI(selectedImageUri);
                                recipeImageButton.setScaleType(ImageView.ScaleType.CENTER_CROP);
                            }
                        }
                    });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // Force Light Mode
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_recipe);

        recipeManager = RecipeManager.getInstance();

        inflater = LayoutInflater.from(this);

        backButton = findViewById(R.id.backButton);
        stepsContainer = findViewById(R.id.stepsContainer);
        ImageView addStepButton = findViewById(R.id.addStepButton);
        recipeImageButton = findViewById(R.id.recipeImageButton);
        recipeImageButton.setAdjustViewBounds(true);
        recipeImageButton.setScaleType(ImageView.ScaleType.CENTER_CROP);
        recipeImageButton.setBackgroundResource(R.drawable.rounded_image_background);
        saveRecipeButton = findViewById(R.id.saveRecipeButton);
        recipeTitleEditText = findViewById(R.id.recipeTitle);

        backButton.setOnClickListener(view -> finish());

        addStepButton.setOnClickListener(view -> addNewStep());

        // Set click listener for recipe image button
        recipeImageButton.setOnClickListener(view -> openImagePicker());

        // Set click listener for save button
        saveRecipeButton.setOnClickListener(view -> saveRecipe());

        // Add a first step by default
        addNewStep();
    }

    private void openImagePicker() {
        Dialog imageSourceDialog = new Dialog(this);
        imageSourceDialog.setContentView(R.layout.dialog_image_source);
        imageSourceDialog.setCancelable(true);

        // Set up button for Gallery option
        LinearLayout galleryOption = imageSourceDialog.findViewById(R.id.galleryOption);
        galleryOption.setOnClickListener(v -> {
            checkStoragePermissionAndPickImage();
            imageSourceDialog.dismiss();
        });

        // Set up button for Camera option
        LinearLayout cameraOption = imageSourceDialog.findViewById(R.id.cameraOption);
        cameraOption.setOnClickListener(v -> {
            checkCameraPermissionAndTakePicture();
            imageSourceDialog.dismiss();
        });

        imageSourceDialog.show();
    }

    private void checkStoragePermissionAndPickImage() {
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                    STORAGE_PERMISSION_CODE);
        } else {
            // Launch gallery
            Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            pickImageLauncher.launch(intent);
        }
    }

    private void checkCameraPermissionAndTakePicture() {
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.CAMERA},
                    CAMERA_PERMISSION_CODE);
        } else {
            dispatchTakePictureIntent();
        }
    }

    private File createImageFile() throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",         /* suffix */
                storageDir      /* directory */
        );

        // Save a file: path for use with ACTION_VIEW intents
        currentPhotoPath = image.getAbsolutePath();
        return image;
    }

    private void dispatchTakePictureIntent() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        // Ensure that there's a camera activity to handle the intent
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            // Create the File where the photo should go
            File photoFile = null;
            try {
                photoFile = createImageFile();
            } catch (IOException ex) {
                // Error occurred while creating the File
                Toast.makeText(this, "Error creating image file", Toast.LENGTH_SHORT).show();
            }
            // Continue only if the File was successfully created
            if (photoFile != null) {
                photoURI = FileProvider.getUriForFile(this,
                        "com.project.step_cook.fileprovider",
                        photoFile);
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                takePictureLauncher.launch(takePictureIntent);
            }
        } else {
            Toast.makeText(this, "No camera app available", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == STORAGE_PERMISSION_CODE && grantResults.length > 0
                && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            pickImageLauncher.launch(intent);
        } else if (requestCode == CAMERA_PERMISSION_CODE && grantResults.length > 0
                && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            dispatchTakePictureIntent();
        } else {
            Toast.makeText(this, "Permission denied", Toast.LENGTH_SHORT).show();
        }
    }

    // Save the recipe to Firebase
    private void saveRecipe() {
        String title = recipeTitleEditText.getText().toString().trim();

        // Validation
        if (title.isEmpty()) {
            recipeTitleEditText.setError("Please enter a recipe title");
            recipeTitleEditText.requestFocus();
            return;
        }

        // Check if we have at least one step
        if (getStepCount() == 0) {
            Toast.makeText(this, "Please add at least one step", Toast.LENGTH_SHORT).show();
            return;
        }

        // Show loading indicator
        Toast.makeText(this, "Saving recipe...", Toast.LENGTH_SHORT).show();
        saveRecipeButton.setEnabled(false);

        // If no image is selected, create recipe without image
        if (selectedImageUri == null) {
            createRecipeInFirebase(title, "");
            return;
        }

        // Upload image first, then create recipe
        recipeManager.uploadRecipeImage(selectedImageUri, this, new RecipeManager.ImageUploadCallback() {
            @Override
            public void onSuccess(String imageUrl) {
                createRecipeInFirebase(title, imageUrl);
            }

            @Override
            public void onError(Exception e) {
                Toast.makeText(AddRecipeActivity.this,
                        "Image upload failed: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                saveRecipeButton.setEnabled(true);
            }
        });
    }

    // Create recipe in Firebase after image upload (if any)
    private void createRecipeInFirebase(String title, String imageUrl) {
        // Calculate total cook time by adding up all step timers
        int totalCookTimeSeconds = calculateTotalCookTime();

        // Collect all steps
        List<RecipeManager.RecipeStep> steps = collectSteps();

        // Save recipe to Firebase
        recipeManager.saveRecipe(title, totalCookTimeSeconds, imageUrl, steps,
                new RecipeManager.RecipeOperationCallback() {
                    @Override
                    public void onSuccess(String recipeId) {
                        Toast.makeText(AddRecipeActivity.this,
                                "Recipe saved successfully!", Toast.LENGTH_SHORT).show();
                        // Redirect to recipe list or detail
                        finish();
                    }

                    @Override
                    public void onError(Exception e) {
                        Toast.makeText(AddRecipeActivity.this,
                                "Failed to save recipe: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                        saveRecipeButton.setEnabled(true);
                    }
                });
    }

    // Calculate total cook time from all steps with timers
    private int calculateTotalCookTime() {
        int totalMinutes = 0;

        for (int i = 0; i < stepsContainer.getChildCount(); i++) {
            View stepView = stepsContainer.getChildAt(i);

            // Look for timer icon - it might be directly in the step view or in a container
            ImageView timerIcon = findTimerIconInStepView(stepView);

            if (timerIcon != null && timerIcon.getTag() != null) {
                try {
                    Integer minutes = (Integer) timerIcon.getTag();
                    totalMinutes += minutes;
                } catch (ClassCastException e) {
                    // Handle case where tag is not an Integer
                }
            }
        }

        // Return total time in seconds
        return totalMinutes * 60;
    }

    // Helper to find timer icon in a step view
    private ImageView findTimerIconInStepView(View stepView) {
        // First try to find directly
        ImageView timerIcon = stepView.findViewById(R.id.timerIcon);

        // If found within a container, return it
        return timerIcon;
    }

    // Collect all steps from the UI
    private List<RecipeManager.RecipeStep> collectSteps() {
        List<RecipeManager.RecipeStep> steps = new ArrayList<>();

        for (int i = 0; i < stepsContainer.getChildCount(); i++) {
            View stepView = stepsContainer.getChildAt(i);

            // Get step description
            EditText stepDetail = stepView.findViewById(R.id.stepDetail);
            String description = stepDetail.getText().toString().trim();

            // Skip empty steps
            if (description.isEmpty()) {
                continue;
            }

            // Get timer value (if any)
            int timerMinutes = 0;
            ImageView timerIcon = findTimerIconInStepView(stepView);
            if (timerIcon != null && timerIcon.getTag() != null) {
                try {
                    timerMinutes = (Integer) timerIcon.getTag();
                } catch (ClassCastException e) {
                    // Handle case where tag is not an Integer
                }
            }

            // Create step object (using empty ID as it will be assigned by Firestore)
            steps.add(new RecipeManager.RecipeStep("", description, timerMinutes, i + 1));
        }

        return steps;
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
        } else if (parent instanceof LinearLayout) {
            // If it's in a LinearLayout (from the XML layout), get the LinearLayout's parent
            return (View) parent;
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