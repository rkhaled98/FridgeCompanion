package com.fridgecompanion;

public class Item {

    private String name;
    private Integer calories;

    public Item() {}

    public Item(String name, Integer calories) {
        this.calories = calories;
        this.name = name;
    }

    public String getTitle() {
        return name;
    }

    public void setTitle(String title) {
        this.name = title;
    }

    public Integer getCalories() {
        return calories;
    }

    public void setCalories(Integer calories) {
        this.calories = calories;
    }
}