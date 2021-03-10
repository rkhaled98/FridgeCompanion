package com.fridgecompanion;

import android.text.format.DateFormat;

import java.io.Serializable;
import java.util.Calendar;
import java.util.Date;

public class Action implements Serializable {

    private long actionTime = Calendar.getInstance().getTimeInMillis();
    private String firebaseUserID = "";
    private String firebaseItemKey = "";
    private String actionType = "";
    private String foodName = "";
    private String name="";
    private String profilePicUrl="";



    public Action() {

    }

    public Action(String firebaseUserID, String firebaseItemKey, String actionType, long actionTime, String profilePicUrl, String name) {
        this.actionTime =actionTime;
        this.firebaseItemKey = firebaseItemKey;
        this.firebaseUserID = firebaseUserID;
        this.actionType = actionType;
        this.profilePicUrl = profilePicUrl;
        this.name = name;
    }

    public long getActionTime() {
        return actionTime;
    }

    public void setActionTime(long actionTime) {
        this.actionTime = actionTime;
    }

    public String getFirebaseUserID() {
        return firebaseUserID;
    }

    public void setFirebaseUserID(String firebaseUserID) {
        this.firebaseUserID = firebaseUserID;
    }

    public String getFirebaseItemKey() {
        return firebaseItemKey;
    }

    public void setFirebaseItemKey(String firebaseItemKey) {
        this.firebaseItemKey = firebaseItemKey;
    }

    public String getActionType() {
        return actionType;
    }

    public void setActionType(String actionType) {
        this.actionType = actionType;
    }

    public String getFoodName() {
        return foodName;
    }

    public void setFoodName(String foodName) {
        this.foodName = foodName;
    }

    public void setProfilePicUrl(String Url){this.profilePicUrl = Url;}
    public String getProfilePicUrl(){return this.profilePicUrl;}

    public void setName(String name){this.name = name;}
    public String getName(){return this.name;}
}
