package com.fridgecompanion;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.firebase.ui.database.FirebaseListAdapter;
import com.firebase.ui.database.FirebaseListOptions;
import com.fridgecompanion.ui.home.FoodAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.List;

public class FirebaseDatasource {

    private FirebaseAuth mFirebaseAuth;
    private FirebaseUser mFirebaseUser;
    private DatabaseReference mDatabase;
    private String mUserId;
    private FirebaseListAdapter<Fridge> myAdapter;
    private String TAG = "firebasehomefragment";


    public FirebaseDatasource(Context context) throws Exception {
        mFirebaseAuth = FirebaseAuth.getInstance();
        mFirebaseUser = mFirebaseAuth.getCurrentUser();
        mDatabase = FirebaseDatabase.getInstance().getReference();

        if (mFirebaseUser == null) {
            throw new Exception("no user logged in");
        } else {
            mUserId = mFirebaseUser.getUid();


//            FirebaseListOptions<Fridge> options = new FirebaseListOptions.Builder<Fridge>()
//                    .setLayout(R.layout.layout_fridge_item)
//                    .setQuery(mDatabase.child("users").child(mUserId).child("fridges"), Fridge.class)
//                    .build();
//
//            Log.d(TAG, mUserId);
//
//            Log.d(TAG, "about to populate");
//
//            myAdapter = new FirebaseListAdapter<Fridge>(options) {
//                @Override
//                protected void populateView(@NonNull View view, Fridge model, int position) {
////                if (view == null) {
////                    final LayoutInflater layoutInflater = LayoutInflater.from(mContext);
////                    view = layoutInflater.inflate(resourceLayout, null);
////                }
//                    Log.d(TAG, "populating");
//
//                    final ListView listViewOfFood = (ListView) view.findViewById(R.id.listViewFood);
//                    final List<Food> foodList = model.getItems();
//
//                    Log.d(TAG, foodList.toString());
//
//                    FoodAdapter adapter = new FoodAdapter(context, R.layout.layout_food_item, foodList);
//                    listViewOfFood.setAdapter(adapter);
//
//
//                }
//            };
        }
    }

    public void addItemToUser(Food food){
        mDatabase.child("users").child(mUserId).child("items").push().setValue(food);

    }


    public void addItemToFridge(Food food) {
        mDatabase.child("users").child(mUserId).child("fridges").orderByKey().limitToFirst(1)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        dataSnapshot.getChildren().iterator().next().getRef().child("items").push().setValue(food);
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

    public FirebaseListAdapter<Fridge> getMyAdapter() {
        return myAdapter;
    }

    public DatabaseReference getFridgesReference() {
        return mDatabase.child("users").child(mUserId).child("fridges");
    }

    public DatabaseReference getItemsReference() {
        return mDatabase.child("users").child(mUserId).child("items");
    }
}
