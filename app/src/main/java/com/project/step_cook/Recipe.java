package com.project.step_cook;

public class Recipe {
    private String id;
    private String title;
    private int cookTime;
    private String imageUrl;
    public Recipe() {}
    public Recipe(String id, String title, String description, int cookTimeSeconds, String imageUrl) {
        this.id = id;
        this.title = title;
        this.cookTime = cookTimeSeconds;
        this.imageUrl = imageUrl;
    }
}
