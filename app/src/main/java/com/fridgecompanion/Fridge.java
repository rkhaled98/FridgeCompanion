package com.fridgecompanion;

import java.util.ArrayList;
import java.util.List;

public class Fridge {
    private String name;
    private List<Food> items;
    private String primOwner;
    private List<String> secOwners;
    private List<FridgeSetting> settings;

    public Fridge() {
        this.name = "test fridge";
        this.items = new ArrayList<Food>();
        this.secOwners = new ArrayList<String>();
        this.settings = new ArrayList<FridgeSetting>();
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
        this.settings.add(new FridgeSetting(owner));
    }

    public String getPrimOwner(){return this.primOwner;}

    public void addSecOwner(String owner){
        this.secOwners.add(owner);
        this.settings.add(new FridgeSetting(owner));
    }

    public void setSecOwners(List<String> owners){this.secOwners = owners;}

    public void removeSecOwner(String owner){this.secOwners.remove(owner);}


}
