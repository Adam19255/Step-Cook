package com.project.step_cook;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.ArrayList;
import java.util.List;

/**
 * Adapter for displaying recipes in a RecyclerView
 */
public class RecipeAdapter extends RecyclerView.Adapter<RecipeAdapter.RecipeViewHolder> {

    private Context context;
    private List<Recipe> recipes;
    private OnRecipeClickListener listener;
    private boolean isActive = false;
    // Interface for handling recipe click events
    public interface OnRecipeClickListener {
        void onRecipeClick(Recipe recipe);
        void onFavoriteClick(Recipe recipe, int position);
    }

    public RecipeAdapter(Context context) {
        this.context = context;
        this.recipes = new ArrayList<>();
    }

    public void setOnRecipeClickListener(OnRecipeClickListener listener) {
        this.listener = listener;
    }

    @NonNull
    @Override
    public RecipeViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.dish_card, parent, false);
        return new RecipeViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecipeViewHolder holder, int position) {
        Recipe recipe = recipes.get(position);
        holder.bind(recipe, position);
    }

    @Override
    public int getItemCount() {
        return recipes.size();
    }

    // Update the recipes list and refresh the view
    public void setRecipes(List<Recipe> recipes) {
        this.recipes = recipes;
        notifyDataSetChanged();
    }

    // ViewHolder class for recipe items
    class RecipeViewHolder extends RecyclerView.ViewHolder {
        private ImageView recipeImage;
        private TextView recipeTitle;
        private TextView recipeInfo;
        private ImageView favoriteIcon;

        public RecipeViewHolder(@NonNull View itemView) {
            super(itemView);
            recipeImage = itemView.findViewById(R.id.recipeImage);
            recipeTitle = itemView.findViewById(R.id.recipeTitle);
            recipeInfo = itemView.findViewById(R.id.recipeInfo);
            favoriteIcon = itemView.findViewById(R.id.favoriteRecipe);

            // Set click listener for the whole item
            itemView.setOnClickListener(v -> {
                int position = getAdapterPosition();
                if (position != RecyclerView.NO_POSITION && listener != null) {
                    listener.onRecipeClick(recipes.get(position));
                }
            });

            // Set click listener for the favorite icon
            favoriteIcon.setOnClickListener(v -> {
                int position = getAdapterPosition();
                if (position != RecyclerView.NO_POSITION && listener != null) {
                    listener.onFavoriteClick(recipes.get(position), position);
                    isActive = !isActive;
                    if (isActive){
                        favoriteIcon.setImageResource(R.drawable.favorite_pressed_svg);
                    }
                    else {
                        favoriteIcon.setImageResource(R.drawable.favorite_unpressed_svg);
                    }
                }
            });
        }

        public void bind(Recipe recipe, int position) {
            // Set recipe title
            recipeTitle.setText(recipe.getTitle());

            // Set recipe info (cook time and difficulty)
            String recipeInfoText = recipe.getFormattedCookTime();
            if (recipe.getDifficulty() != null && !recipe.getDifficulty().isEmpty()) {
                recipeInfoText += " | " + recipe.getDifficulty();
            }
            recipeInfo.setText(recipeInfoText);

            // Load recipe image using Glide if available
            if (recipe.getImageUrl() != null && !recipe.getImageUrl().isEmpty()) {
                Glide.with(context)
                        .load(recipe.getImageUrl())
                        .placeholder(R.drawable.image_placeholder)
                        .into(recipeImage);
            } else {
                recipeImage.setImageResource(R.drawable.image_placeholder);
            }

            // For now, just use the unpressed favorite icon
            // In a real implementation, this would check if the recipe is in favorites
            favoriteIcon.setImageResource(R.drawable.favorite_unpressed_svg);
        }
    }
}