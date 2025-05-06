package com.project.step_cook;

public class Recipe {
    private String id;
    private String title;

    private int cookTime; // Cook time in seconds
    private String imageUrl;
    public Recipe() {}
    public Recipe(String id, String title, String description, int cookTimeSeconds, String imageUrl) {
        this.id = id;
        this.title = title;
        this.cookTime = cookTimeSeconds;
        this.imageUrl = imageUrl;
    }

    public String getId() {
        return id;
    }
    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public int getCookTime() {
        return cookTime;
    }

    public void setCookTime(int cookTime) {
        this.cookTime = cookTime;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }
}
