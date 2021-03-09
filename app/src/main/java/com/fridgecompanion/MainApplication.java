package com.fridgecompanion;

import android.app.Application;

import com.cloudinary.android.MediaManager;

public class MainApplication extends Application {
    // Called when the application is starting, before any other application objects have been created.
    // Overriding this method is totally optional!
    @Override
    public void onCreate() {
        super.onCreate();
        MediaManager.init(this);
        // Required initialization logic here!
    }
}
