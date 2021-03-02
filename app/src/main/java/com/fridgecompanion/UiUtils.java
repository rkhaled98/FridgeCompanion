package com.fridgecompanion;

import android.content.Context;
import android.content.Intent;

public class UiUtils {
    public static void loadLogInView(Context context) {
        Intent intent = new Intent(context, LogInActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
        context.startActivity(intent);
    }
}
