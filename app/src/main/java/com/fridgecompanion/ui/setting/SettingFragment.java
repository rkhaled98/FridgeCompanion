package com.fridgecompanion.ui.setting;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.fridgecompanion.R;
import com.fridgecompanion.UiUtils;
import com.google.firebase.auth.FirebaseAuth;

public class SettingFragment extends Fragment {

    private SettingViewModel notificationsViewModel;

    private FirebaseAuth mFirebaseAuth;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_setting, container, false);
        final TextView textView = root.findViewById(R.id.text_notifications);

        mFirebaseAuth = FirebaseAuth.getInstance();

        final Button btnLogout = root.findViewById(R.id.button_logout);

        btnLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mFirebaseAuth.signOut();
                UiUtils.loadLogInView(getContext());
            }
        });

        return root;
    }
}