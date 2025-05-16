package com.project.step_cook;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import androidx.core.content.FileProvider;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import android.Manifest;

/**
 * Class to manage dialogs in the application
 */
public class DialogManager {
    private Context context;
    public static final int REQUEST_IMAGE_CAPTURE = 1;
    public static final int REQUEST_PICK_IMAGE = 2;
    public static final int REQUEST_CAMERA_PERMISSION = 100;
    public static final int REQUEST_STORAGE_PERMISSION = 101;

    private Uri currentPhotoUri;
    private ImageDeleteListener imageDeleteListener;

    public DialogManager(Context context) {
        this.context = context;
    }

    public interface ImageDeleteListener {
        void onImageDeleted();
    }

    public void setImageDeleteListener(ImageDeleteListener listener) {
        this.imageDeleteListener = listener;
    }

    public void showEditProfileDialog(){
        EditProfileDialog dialog = new EditProfileDialog(context);
        dialog.show();
    }

    public void showAboutDialog() {
        AboutUsDialog dialog = new AboutUsDialog(context);
        dialog.show();
    }

    public void showImageSourceDialog(){
        ImageSourceDialog dialog = new ImageSourceDialog(context);

        // Set callbacks for camera and gallery options
        dialog.setCameraClickListener(v -> {
            dialog.dismiss();
            requestCameraPermission();
        });

        dialog.setGalleryClickListener(v -> {
            dialog.dismiss();
            requestStoragePermission();
        });

        dialog.setDeleteImageClickListener(v -> {
            dialog.dismiss();
            if (imageDeleteListener != null) {
                imageDeleteListener.onImageDeleted();
            }
        });

        dialog.show();
    }

    private void requestCameraPermission() {
        if (ContextCompat.checkSelfPermission(context, Manifest.permission.CAMERA)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions((Activity) context,
                    new String[]{Manifest.permission.CAMERA},
                    REQUEST_CAMERA_PERMISSION);
        } else {
            launchCamera();
        }
    }

    private void requestStoragePermission() {
        if (ContextCompat.checkSelfPermission(context, Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions((Activity) context,
                    new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                    REQUEST_STORAGE_PERMISSION);
        } else {
            openGallery();
        }
    }

    public void launchCamera() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

        // Ensure that there's a camera activity to handle the intent
        if (takePictureIntent.resolveActivity(context.getPackageManager()) != null) {
            // Create the File where the photo should go
            File photoFile = null;
            try {
                photoFile = createImageFile();
            } catch (IOException ex) {
                // Error occurred while creating the File
                ex.printStackTrace();
            }

            // Continue only if the File was successfully created
            if (photoFile != null) {
                // Get URI from FileProvider to grant temporary permissions
                currentPhotoUri = FileProvider.getUriForFile(context,
                        context.getPackageName() + ".fileprovider",
                        photoFile);

                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, currentPhotoUri);
                ((Activity) context).startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
            }
        }
    }

    private File createImageFile() throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = context.getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",         /* suffix */
                storageDir      /* directory */
        );

        return image;
    }

    public void openGallery() {
        Intent pickPhoto = new Intent(Intent.ACTION_PICK,
                android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        ((Activity) context).startActivityForResult(pickPhoto, REQUEST_PICK_IMAGE);
    }

    public Uri getCurrentPhotoUri() {
        return currentPhotoUri;
    }

    /**
     * Show confirmation dialog before exiting the app
     */
    public void showExitConfirmationDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("Exit App");
        builder.setMessage("Are you sure you want to exit?");

        // Add the buttons
        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // User clicked Yes button
                if (context instanceof AppCompatActivity) {
                    ((AppCompatActivity) context).finish(); // Close the app
                }
            }
        });

        builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // User cancelled the dialog
                dialog.dismiss();
            }
        });

        // Create and show the AlertDialog
        AlertDialog dialog = builder.create();
        dialog.show();
    }
}