package com.project.step_cook;

import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.widget.Button;
import android.widget.LinearLayout;

import androidx.core.content.ContextCompat;

/**
 * Helper class to manage UI-related operations
 */
public class UIHelper {
    private Context context;

    public UIHelper(Context context) {
        this.context = context;
    }

    /**
     * Change the color of a filter button based on its state
     */
    public void changeButtonColor(boolean isActive, Button filterButton) {
        if (isActive) {
            filterButton.setBackgroundTintList(ColorStateList.valueOf(
                    ContextCompat.getColor(context, R.color.orange)));
            filterButton.setTextColor(ContextCompat.getColor(
                    context, android.R.color.white));
        } else {
            filterButton.setBackgroundTintList(ColorStateList.valueOf(
                    ContextCompat.getColor(context, R.color.gray)));
            filterButton.setTextColor(ContextCompat.getColor(
                    context, R.color.black));
        }
    }

    /**
     * Highlight the selected filter option in the dialog
     */
    public void highlightSelectedOption(LinearLayout allLayout, LinearLayout option1Layout,
                                        LinearLayout option2Layout, LinearLayout option3Layout,
                                        String selectedOption, String[] options) {
        // Reset all backgrounds first
        allLayout.setBackgroundColor(ContextCompat.getColor(context, android.R.color.transparent));
        option1Layout.setBackgroundColor(ContextCompat.getColor(context, android.R.color.transparent));
        option2Layout.setBackgroundColor(ContextCompat.getColor(context, android.R.color.transparent));
        option3Layout.setBackgroundColor(ContextCompat.getColor(context, android.R.color.transparent));

        // Determine which layout to highlight
        LinearLayout selectedLayout = null;

        if (selectedOption.equals(options[0])) {
            selectedLayout = allLayout;
        } else if (selectedOption.equals(options[1])) {
            selectedLayout = option1Layout;
        } else if (selectedOption.equals(options[2])) {
            selectedLayout = option2Layout;
        } else if (selectedOption.equals(options[3])) {
            selectedLayout = option3Layout;
        }

        // Apply highlight if found
        if (selectedLayout != null) {
            int highlightColor = ContextCompat.getColor(context, R.color.orange);
            highlightColor = Color.argb(50, Color.red(highlightColor),
                    Color.green(highlightColor),
                    Color.blue(highlightColor));
            selectedLayout.setBackgroundColor(highlightColor);
        }
    }
}