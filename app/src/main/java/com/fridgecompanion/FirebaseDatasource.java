package com.fridgecompanion;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class FirebaseDatasource {

    private FirebaseAuth mFirebaseAuth;
    private FirebaseUser mFirebaseUser;
    private DatabaseReference mDatabase;
    private String mUserId;

    public FirebaseDatasource() throws Exception {
        mFirebaseAuth = FirebaseAuth.getInstance();
        mFirebaseUser = mFirebaseAuth.getCurrentUser();
        mDatabase = FirebaseDatabase.getInstance().getReference();

        if (mFirebaseUser == null) {
            throw new Exception("no user logged in");
        } else {
            mUserId = mFirebaseUser.getUid();
        }
    }

    public void addItemToCurrentUser(Item item) {
        mDatabase.child("users").child(mUserId).child("fridges").orderByKey().limitToFirst(1)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        dataSnapshot.getChildren().iterator().next().getRef().child("items").push().setValue(item);
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
//        mDatabase.child("users").child(mUserId).child("items").push().setValue(item);
    }

    public void createFridge(Fridge fridge) {
        mDatabase.child("users").child(mUserId).child("fridges").push().setValue(fridge);
    }
}
