package com.fridgecompanion;

import java.util.ArrayList;
import java.util.List;

public class Fridge {
    private String name;
    private List<Food> items;
    private String primOwner;

    public Fridge() {
        this.name = "test fridge";
        this.items = new ArrayList<Food>();
    }

    public Fridge(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<Food> getItems() {
        return items;
    }

    public void setItems(List<Food> items) {
        this.items = items;
    }

    public void addItem(Food item) {
        this.items.add(item);
    }

    public void setPrimOwner(String owner){
        this.primOwner = owner;
    }
    public String getPrimOwner(){return this.primOwner;}



}
