package com.project.step_cook;

import android.content.Context;
import android.net.Uri;
import android.util.Log;
import android.webkit.MimeTypeMap;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Singleton class to manage recipe data and operations throughout the application.
 * Handles retrieving, storing, and updating recipe information in Firestore.
 */
public class RecipeManager {
    private static final String TAG = "RecipeManager";
    private static final String RECIPES_COLLECTION = "Recipes";
    private static final String RECIPE_STEPS_COLLECTION = "Steps";
    private static final String RECIPE_IMAGES_STORAGE = "recipe_images";

    private static RecipeManager instance;
    private FirebaseFirestore db;
    private FirebaseStorage storage;
    private StorageReference storageRef;
    private UserManager userManager;

    // Interface for callbacks when recipe data is loaded
    public interface RecipeDataCallback {
        void onRecipeDataLoaded(Recipe recipe);
        void onError(Exception e);
    }

    // Interface for callbacks when recipe list is loaded
    public interface RecipeListCallback {
        void onRecipeListLoaded(List<Recipe> recipes);
        void onError(Exception e);
    }

    // Interface for simpler operations with success/failure
    public interface RecipeOperationCallback {
        void onSuccess(String recipeId);
        void onError(Exception e);
    }

    // Interface for image upload operations
    public interface ImageUploadCallback {
        void onSuccess(String imageUrl);
        void onError(Exception e);
    }

    // Class to represent a recipe step
    public static class RecipeStep {
        private String stepId;
        private String description;
        private int timerMinutes;
        private int stepNumber;

        public RecipeStep() {}

        public RecipeStep(String stepId, String description, int timerMinutes, int stepNumber) {
            this.stepId = stepId;
            this.description = description;
            this.timerMinutes = timerMinutes;
            this.stepNumber = stepNumber;
        }

        public String getStepId() {
            return stepId;
        }

        public String getDescription() {
            return description;
        }

        public int getTimerMinutes() {
            return timerMinutes;
        }

        public int getStepNumber() {
            return stepNumber;
        }

        public Map<String, Object> toMap() {
            HashMap<String, Object> result = new HashMap<>();
            result.put("description", description);
            result.put("timerMinutes", timerMinutes);
            result.put("stepNumber", stepNumber);
            return result;
        }
    }

    // Private constructor to enforce singleton pattern
    private RecipeManager() {
        db = FirebaseFirestore.getInstance();
        storage = FirebaseStorage.getInstance();
        storageRef = storage.getReference();
        userManager = UserManager.getInstance();
    }

    // Get the singleton instance
    public static synchronized RecipeManager getInstance() {
        if (instance == null) {
            instance = new RecipeManager();
        }
        return instance;
    }

    /**
     * Upload a recipe image to Firebase Storage
     * @param imageUri URI of the image file to upload
     * @param context Application context
     * @param callback Callback to receive the uploaded image URL or error
     */
    public void uploadRecipeImage(Uri imageUri, Context context, final ImageUploadCallback callback) {
        String userId = userManager.getCurrentUserId();
        if (userId == null) {
            callback.onError(new Exception("No user is logged in"));
            return;
        }

        // Create a unique filename
        String timestamp = String.valueOf(System.currentTimeMillis());
        String fileExtension = getFileExtension(imageUri, context);
        String fileName = userId + "_" + timestamp + "." + fileExtension;

        // Create a reference to the location where we'll store the file
        final StorageReference fileRef = storageRef.child(RECIPE_IMAGES_STORAGE + "/" + fileName);

        // Upload the file
        UploadTask uploadTask = fileRef.putFile(imageUri);

        // After upload is complete, get the download URL
        Task<Uri> urlTask = uploadTask.continueWithTask(task -> {
            if (!task.isSuccessful()) {
                throw task.getException();
            }
            // Continue with the task to get the download URL
            return fileRef.getDownloadUrl();
        }).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                Uri downloadUri = task.getResult();
                callback.onSuccess(downloadUri.toString());
            } else {
                callback.onError(task.getException());
            }
        });
    }

    /**
     * Get file extension from URI
     */
    private String getFileExtension(Uri uri, Context context) {
        String extension;
        if (uri.getScheme().equals("content")) {
            extension = MimeTypeMap.getSingleton().getExtensionFromMimeType(
                    context.getContentResolver().getType(uri));
        } else {
            extension = MimeTypeMap.getFileExtensionFromUrl(uri.toString());
        }
        return extension != null ? extension : "jpg"; // Default to jpg if can't determine
    }

    /**
     * Save a new recipe to Firestore
     * @param title Recipe title
     * @param cookTimeSeconds Total cooking time in seconds
     * @param imageUrl URL of the recipe image
     * @param steps List of recipe steps
     * @param callback Callback for operation result
     */
    public void saveRecipe(String title, int cookTimeSeconds, String imageUrl,
                           List<RecipeStep> steps, final RecipeOperationCallback callback) {
        // Validate inputs
        if (title == null || title.trim().isEmpty()) {
            callback.onError(new Exception("Recipe title cannot be empty"));
            return;
        }

        // Use default empty string if imageUrl is null
        if (imageUrl == null) {
            imageUrl = "";
        }

        String userId = userManager.getCurrentUserId();
        if (userId == null) {
            callback.onError(new Exception("No user is logged in"));
            return;
        }

        // Create recipe document
        Map<String, Object> recipeData = new HashMap<>();
        recipeData.put("title", title);
        recipeData.put("cookTime", cookTimeSeconds);
        recipeData.put("imageUrl", imageUrl);
        recipeData.put("userId", userId); // Associate recipe with user
        recipeData.put("createdAt", System.currentTimeMillis());

        // Log key data points before saving
        Log.d(TAG, "Saving recipe: " + title + ", Cook time: " + cookTimeSeconds + "s, Steps: " + steps.size());

        // Add recipe to Firestore
        db.collection(RECIPES_COLLECTION)
                .add(recipeData)
                .addOnSuccessListener(documentReference -> {
                    String recipeId = documentReference.getId();
                    Log.d(TAG, "Recipe document created with ID: " + recipeId);

                    // Now save the steps as a subcollection
                    saveRecipeSteps(recipeId, steps, task -> {
                        if (task.isSuccessful()) {
                            Log.d(TAG, "Recipe steps saved successfully");
                            callback.onSuccess(recipeId);
                        } else {
                            Log.e(TAG, "Failed to save recipe steps", task.getException());

                            // If steps fail to save, we should delete the recipe document to avoid orphaned records
                            db.collection(RECIPES_COLLECTION).document(recipeId).delete()
                                    .addOnCompleteListener(deleteTask -> {
                                        callback.onError(new Exception("Failed to save recipe steps: " +
                                                (task.getException() != null ? task.getException().getMessage() : "Unknown error")));
                                    });
                        }
                    });
                })
                .addOnFailureListener(e -> {
                    Log.e(TAG, "Error adding recipe", e);
                    callback.onError(e);
                });
    }

    /**
     * Helper method to save recipe steps as a subcollection
     */
    private void saveRecipeSteps(String recipeId, List<RecipeStep> steps,
                                 OnCompleteListener<Void> completeListener) {
        // Skip if no steps to save
        if (steps == null || steps.isEmpty()) {
            Log.w(TAG, "No steps to save for recipe: " + recipeId);
            completeListener.onComplete(Tasks.forResult(null));
            return;
        }

        // Use a batch write to save all steps at once
        com.google.firebase.firestore.WriteBatch batch = db.batch();

        CollectionReference stepsRef = db.collection(RECIPES_COLLECTION)
                .document(recipeId)
                .collection(RECIPE_STEPS_COLLECTION);

        for (RecipeStep step : steps) {
            DocumentReference newStepRef = stepsRef.document();
            batch.set(newStepRef, step.toMap());
        }

        batch.commit().addOnCompleteListener(completeListener);
    }

    /**
     * Get user recipes
     * @param callback Callback to receive the recipe list or error
     */
    public void getUserRecipes(final RecipeListCallback callback) {
        String userId = userManager.getCurrentUserId();
        if (userId == null) {
            callback.onError(new Exception("No user is logged in"));
            return;
        }

        db.collection(RECIPES_COLLECTION)
                .whereEqualTo("userId", userId)
                .orderBy("createdAt", Query.Direction.DESCENDING)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    List<Recipe> recipes = new ArrayList<>();
                    for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
                        String id = document.getId();
                        String title = document.getString("title");
                        int cookTime = document.getLong("cookTime").intValue();
                        String imageUrl = document.getString("imageUrl");

                        Recipe recipe = new Recipe(id, title, cookTime, imageUrl);
                        recipes.add(recipe);
                    }
                    callback.onRecipeListLoaded(recipes);
                })
                .addOnFailureListener(e -> {
                    Log.w(TAG, "Error getting user recipes", e);
                    callback.onError(e);
                });
    }

    /**
     * Get a specific recipe by ID
     * @param recipeId ID of the recipe to retrieve
     * @param callback Callback to receive the recipe or error
     */
    public void getRecipe(String recipeId, final RecipeDataCallback callback) {
        db.collection(RECIPES_COLLECTION)
                .document(recipeId)
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        String id = documentSnapshot.getId();
                        String title = documentSnapshot.getString("title");
                        int cookTime = documentSnapshot.getLong("cookTime").intValue();
                        String imageUrl = documentSnapshot.getString("imageUrl");

                        Recipe recipe = new Recipe(id, title, cookTime, imageUrl);
                        callback.onRecipeDataLoaded(recipe);
                    } else {
                        callback.onError(new Exception("Recipe not found"));
                    }
                })
                .addOnFailureListener(e -> {
                    Log.w(TAG, "Error getting recipe", e);
                    callback.onError(e);
                });
    }

    /**
     * Delete a recipe and all its steps
     * @param recipeId ID of the recipe to delete
     * @param callback Callback for operation result
     */
    public void deleteRecipe(String recipeId, final RecipeOperationCallback callback) {
        // First delete the steps subcollection
        db.collection(RECIPES_COLLECTION)
                .document(recipeId)
                .collection(RECIPE_STEPS_COLLECTION)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    com.google.firebase.firestore.WriteBatch batch = db.batch();

                    // Add each step document to the deletion batch
                    for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
                        batch.delete(document.getReference());
                    }

                    // Commit the batch to delete all steps
                    batch.commit().addOnSuccessListener(aVoid -> {
                        // Now delete the recipe document itself
                        db.collection(RECIPES_COLLECTION)
                                .document(recipeId)
                                .delete()
                                .addOnSuccessListener(v -> callback.onSuccess(recipeId))
                                .addOnFailureListener(e -> {
                                    Log.w(TAG, "Error deleting recipe", e);
                                    callback.onError(e);
                                });
                    }).addOnFailureListener(e -> {
                        Log.w(TAG, "Error deleting recipe steps", e);
                        callback.onError(e);
                    });
                })
                .addOnFailureListener(e -> {
                    Log.w(TAG, "Error getting recipe steps for deletion", e);
                    callback.onError(e);
                });
    }
}