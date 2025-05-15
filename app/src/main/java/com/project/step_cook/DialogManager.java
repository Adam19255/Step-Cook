package com.project.step_cook;

import android.content.Context;
import android.content.DialogInterface;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

/**
 * Class to manage dialogs in the application
 */
public class DialogManager {
    private Context context;

    public DialogManager(Context context) {
        this.context = context;
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
        dialog.show();
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