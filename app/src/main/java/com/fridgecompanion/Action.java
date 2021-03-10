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
    private String foodName="";
    private String photoURL;
    private String userName;



    public Action() {

    }
//Need to add name and picUrl in constructor
    public Action(String firebaseUserID, String firebaseItemKey, String actionType, long actionTime) {
        this.actionTime =actionTime;
        this.firebaseItemKey = firebaseItemKey;
        this.firebaseUserID = firebaseUserID;
        this.actionType = actionType;

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

    public String getPhotoURL() {
        return photoURL;
    }

    public void setPhotoURL(String photoURL) {
        this.photoURL = photoURL;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

}
