package com.fridgecompanion;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.Collections;

import androidx.preference.Preference;
import androidx.preference.PreferenceViewHolder;

public class myPreference extends Preference {

    public myPreference(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public myPreference(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public myPreference(Context context) {
        super(context);
    }

    public myPreference(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    @Override
    public void onBindViewHolder(PreferenceViewHolder holder) {
        super.onBindViewHolder(holder);
        ImageView iv = (ImageView) holder.findViewById(R.id.profile_pic);
        TextView tv = (TextView) holder.findViewById(R.id.username_text);
        try{
            FirebaseDatasource firebaseDatasource = new FirebaseDatasource(getContext());
            firebaseDatasource.getUserReference().addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    if(dataSnapshot.exists()) {
                        String welcome = "Welcome, " + dataSnapshot.child("name").getValue(String.class).split("\\s+")[0] +"!";
                        tv.setText(welcome);
                        Picasso.get().load(dataSnapshot.child("profilePic").getValue(String.class)).into(iv);
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                    System.out.println("The read failed: " + databaseError.getCode());
                }
            });
        }catch(Exception e){

        }

    }

    @Override
    public void setOnPreferenceChangeListener(OnPreferenceChangeListener onPreferenceChangeListener) {
        super.setOnPreferenceChangeListener(onPreferenceChangeListener);
    }
}
