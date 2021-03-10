package com.fridgecompanion.ui.setting;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.preference.CheckBoxPreference;
import androidx.preference.Preference;
import androidx.preference.PreferenceFragmentCompat;
import androidx.preference.PreferenceManager;

import com.fridgecompanion.R;
import com.fridgecompanion.UiUtils;
import com.fridgecompanion.ui.home.HomeFragment;
import com.google.firebase.auth.FirebaseAuth;

public class SettingFragment extends PreferenceFragmentCompat {

    private FirebaseAuth firebaseAuth;

    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        setPreferencesFromResource(R.xml.preferences, rootKey);
        firebaseAuth = FirebaseAuth.getInstance();
        if (getContext() != null) {
            PreferenceManager.setDefaultValues(getContext(), R.xml.preferences, false);
        }


        Preference myPref = (Preference) findPreference("user_profile");
        myPref.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                Intent intent = new Intent(getActivity(), ProfileActivity.class);
                startActivity(intent);
                return true;
            }
        });


//        Preference p = findPreference("custom_header");
//        ImageView iv = (ImageView) getView().findViewById(R.id.profile_pic);
//        TextView tv = (TextView)getView().findViewById(R.id.username_text);
//        tv.setText("test");


        // Logout button
        Preference button = findPreference(getString(R.string.action_logout));
        button.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                firebaseAuth.signOut();
                UiUtils.loadLogInView(getContext());
                return true;
            }
        });
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view =  super.onCreateView(inflater, container, savedInstanceState);
        view.setBackgroundColor(Color.WHITE);
        return view;

    }
}