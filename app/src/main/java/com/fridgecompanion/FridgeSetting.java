package com.fridgecompanion;

public class FridgeSetting {

    private String ownerId;
    private int notifyDates;
    private boolean notifyOn;

    public FridgeSetting(String owner){
        ownerId = owner;
        notifyDates = 2;
        notifyOn = false;
    }

    public void setOwnerId(String ownerId) {
        ownerId = ownerId;
    }

    public String getOwnerId() {
        return ownerId;
    }

    public int getNotifyDates() {
        return notifyDates;
    }

    public void setNotifyDates(int notifyDates) {
        this.notifyDates = notifyDates;
    }

    public boolean isNotifyOn() {
        return notifyOn;
    }

    public void setNotifyOn(boolean notifyOn) {
        this.notifyOn = notifyOn;
    }

}
