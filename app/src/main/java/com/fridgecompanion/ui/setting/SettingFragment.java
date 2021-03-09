package com.fridgecompanion.ui.setting;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import androidx.annotation.NonNull;
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
}