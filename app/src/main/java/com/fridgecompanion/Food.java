package com.fridgecompanion;

import java.util.Calendar;

public class Food {

    public static int UNIT_COUNT = 0;
    public static int UNIT_PERCENTAGE = 1;
    public static int UNIT_GRAMS = 2;
    public static int UNIT_POUND = 3;
    public static int UNIT_MILLILITER = 4;

    private int quantity;
    private int unit;
    private String foodName;
    private long enteredDate;
    private long expireDate;
    private String foodDescription;

    public String getFoodDescription() {
        return foodDescription;
    }

    public void setFoodDescription(String foodDescription) {
        this.foodDescription = foodDescription;
    }

    public Food() {
        this.setDefaultValues();
    }

    private void setDefaultValues(){
        quantity = 0;
        unit = UNIT_COUNT;
        foodName = "";
        enteredDate = Calendar.getInstance().getTimeInMillis();
        expireDate = 0;
    }

    //getter and setter
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
}
