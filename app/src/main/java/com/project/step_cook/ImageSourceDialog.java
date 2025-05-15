package com.project.step_cook;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.LinearLayout;
import android.widget.Toast;

public class ImageSourceDialog extends Dialog {
    private Context context;
    private LinearLayout cameraOption;
    private LinearLayout galleryOption;

    public ImageSourceDialog(Context context) {
        super(context);
        this.context = context;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.image_source_dialog);

        // Make the dialog background transparent
        if (getWindow() != null) {
            getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        }

        // Initialize views
        cameraOption = findViewById(R.id.cameraOption);
        galleryOption = findViewById(R.id.galleryOption);

        // Set click listeners
        cameraOption.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(context, "Camera option selected", Toast.LENGTH_SHORT).show();
                dismiss();
            }
        });

        galleryOption.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(context, "Gallery option selected", Toast.LENGTH_SHORT).show();
                dismiss();
            }
        });
    }
}