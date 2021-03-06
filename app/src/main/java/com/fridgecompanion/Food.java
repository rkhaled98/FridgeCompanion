package com.fridgecompanion;

import java.util.Calendar;

public class Food {

    public static int UNIT_COUNT = 0;
    public static int UNIT_PERCENTAGE = 1;
    public static int UNIT_GRAMS = 2;
    public static int UNIT_POUND = 3;
    public static int UNIT_MILLILITER = 4;
    public static int UNIT_NONE = 5;

    private int quantity;
    private int unit;
    private String foodName;
    private long enteredDate;
    private long expireDate;
    //Food ID, image url, and calories from database
    private String id;
    private String image;
    private double calories;
    private String foodDescription;
    private String nutrition;




    public Food() {
        this.setDefaultValues();
    }

    private void setDefaultValues() {
        image = "https://www.edamam.com/food-img/63f/63fefaf1b9dc3674e7b105a3d91b2ba0.png";
        quantity = 0;
        unit = UNIT_COUNT;
        foodName = "";
        enteredDate = Calendar.getInstance().getTimeInMillis();
        expireDate = 0;
    }

    public String getFoodDescription() {
        return foodDescription;
    }

    public void setFoodDescription(String foodDescription) {
        this.foodDescription = foodDescription;
    }

    //getter and setter
    public String getId(){return this.id;}

    public String getImage(){return this.image;}

    public double getCalories(){return this.calories;}


    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public int getUnit() {
        return unit;
    }

    public void setUnit(int unit) {
        this.unit = unit;
    }

    public String getFoodName() {
        return foodName;
    }

    public void setFoodName(String foodName) {
        this.foodName = foodName;
    }

    public long getEnteredDate() {
        return enteredDate;
    }

    public void setEnteredDate(long enteredDate) {
        this.enteredDate = enteredDate;
    }

    public long getExpireDate() {
        return expireDate;
    }

    public void setExpireDate(long expireDate) {
        this.expireDate = expireDate;
    }

    public void setId(String id){this.id = id;}

    public void setImage(String image){this.image = image;}

    public void setCalories(double calories){this.calories = calories;}

    @Override
    public String toString() {
        return "Food{" +
                "quantity=" + quantity +
                ", unit=" + unit +
                ", foodName='" + foodName + '\'' +
                ", enteredDate=" + enteredDate +
                ", expireDate=" + expireDate +
                ", id='" + id + '\'' +
                ", image='" + image + '\'' +
                ", calories=" + calories +
                ", foodDescription='" + foodDescription + '\'' +
                '}';
    }
    public String getNutrition() {
        return nutrition;
    }

    public void setNutrition(String nutrition) {
        this.nutrition = nutrition;
    }
}
