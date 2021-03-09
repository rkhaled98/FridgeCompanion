package com.fridgecompanion;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.res.ResourcesCompat;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;

public class SignUpActivity extends AppCompatActivity {

    protected EditText passwordEditText;
    protected EditText emailEditText;
    protected Button signUpButton;
    protected EditText firstNameEditText;
    protected EditText lastNameEditText;
    private FirebaseAuth mFirebaseAuth;
    private Context mContext;

    private String TAG = "firebasesignin";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        mContext = this;

        getWindow().setNavigationBarColor(ResourcesCompat.getColor(getResources(), R.color.fridge_blue, null));

        // Initialize FirebaseAuth
        mFirebaseAuth = FirebaseAuth.getInstance();

        passwordEditText = (EditText)findViewById(R.id.passwordField);
        emailEditText = (EditText)findViewById(R.id.emailField);
        firstNameEditText = (EditText)findViewById(R.id.firstNameField);
        lastNameEditText = (EditText)findViewById(R.id.lastNameField);
        signUpButton = (Button)findViewById(R.id.signupButton);

        signUpButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String password = passwordEditText.getText().toString();
                String email = emailEditText.getText().toString();
                String firstName = firstNameEditText.getText().toString();
                String lastName =lastNameEditText.getText().toString();

                password = password.trim();
                email = email.trim();
                firstName = firstName.trim();
                lastName= lastName.trim();

                if (password.isEmpty() || email.isEmpty() || firstName.isEmpty() || lastName.isEmpty()) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(SignUpActivity.this);
                    builder.setMessage(R.string.signup_error_message)
                            .setTitle(R.string.signup_error_title)
                            .setPositiveButton(android.R.string.ok, null);
                    AlertDialog dialog = builder.create();
                    dialog.show();
                } else {
                    Log.d(TAG, password + email);
                    String finalFirstName = firstName;
                    String finalLastName = lastName;
                    mFirebaseAuth.createUserWithEmailAndPassword(email, password)
                            .addOnCompleteListener(SignUpActivity.this, new OnCompleteListener<AuthResult>() {
                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task) {
                                    if (task.isSuccessful()) {
                                        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

                                        UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                                                .setDisplayName(finalFirstName + " " + finalLastName)
                                                .setPhotoUri(Uri.parse("https://example.com/jane-q-user/profile.jpg"))
                                                .build();

                                        if (user != null) {
                                            user.updateProfile(profileUpdates)
                                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                        @Override
                                                        public void onComplete(@NonNull Task<Void> task) {
                                                            if (task.isSuccessful()) {
                                                                Intent intent = new Intent(SignUpActivity.this, AfterSignInActivity.class);
                                                                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                                                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                                                startActivity(intent);

                                                                Log.d(TAG, user.getDisplayName());
                                                            }
                                                        }
                                                    });
                                        } else {
                                            Toast.makeText(mContext, "Could not make user!", Toast.LENGTH_SHORT).show();
                                        }

                                    } else {
                                        Log.d(TAG, task.getException().toString() + "," + task.getException().getMessage());
                                        AlertDialog.Builder builder = new AlertDialog.Builder(SignUpActivity.this);
                                        builder.setMessage(task.getException().getMessage())
                                                .setTitle(R.string.login_error_title)
                                                .setPositiveButton(android.R.string.ok, null);
                                        AlertDialog dialog = builder.create();
                                        dialog.show();
                                    }
                                }
                            });
                }
            }
        });
    }

}

