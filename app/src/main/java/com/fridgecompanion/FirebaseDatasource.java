package com.fridgecompanion;

import android.content.Context;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.firebase.ui.database.FirebaseListAdapter;
import com.firebase.ui.database.FirebaseListOptions;
import com.fridgecompanion.ui.home.FoodAdapter;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserInfo;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.Calendar;
import java.util.HashMap;
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

    public String getDisplayName() {
        return mFirebaseAuth.getCurrentUser().getDisplayName();
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

    public String createFridge(Fridge fridge) {
        fridge.setPrimOwner(mUserId);
        DatabaseReference ref = mDatabase.child("fridges").push();
        ref.setValue(fridge);
        mDatabase.child("users").child(mUserId).child("fridgelist").child(ref.getKey()).setValue(fridge.getName());
        return ref.getKey();
    }

    public DatabaseReference getFridgeListReference() {
        return mDatabase.child("users").child(mUserId).child("fridgelist");
    }

    public FirebaseListAdapter<Fridge> getMyAdapter() {
        return myAdapter;
    }

    public DatabaseReference getFridgesReference() {
        return mDatabase.child("fridges");
    }


    public DatabaseReference getItemsReference() {
        return mDatabase.child("users").child(mUserId).child("items");
    }

    public DatabaseReference getFridgeReferenceById(String id){
        return mDatabase.child("fridges").child(id);
    }

    public DatabaseReference getItemsReferenceByFridgeId(String id){
        return mDatabase.child("fridges").child(id).child("items");
    }

    public void addSecondaryOwner(String FridgeId, String FridgeName){
        //mDatabase.child("fridges").child(FridgeId).child("secOwner").child(mUserId).setValue("Some Name");
        mDatabase.child("users").child(mUserId).child("fridgelist").child(FridgeId).setValue(FridgeName);
    }

    public void addItemToFridgeId(Food food, String id) {
        mDatabase.child("fridges").child(id)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        String key =  dataSnapshot.getRef().child("items").push().getKey();
                        dataSnapshot.getRef().child("items").child(key).setValue(food);
                        Action addAction = new Action( mUserId, key,  "added"
                                ,  Calendar.getInstance().getTimeInMillis());
                        addAction.setFoodName(food.getFoodName());
                        dataSnapshot.getRef().child("history").push().setValue(addAction);
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
    }

    public void editItemToFridgeId(Food food, String fridgeId, String foodId) {
        mDatabase.child("fridges").child(fridgeId)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        if(dataSnapshot.child("items").hasChild(foodId)){
                            dataSnapshot.child("items").child(foodId).getRef().setValue(food);
                            Action editAction = new Action( mUserId, foodId,  "edited"
                                    ,  Calendar.getInstance().getTimeInMillis());
                            editAction.setFoodName(food.getFoodName());
                            dataSnapshot.getRef().child("history").push().setValue(editAction);
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
    }

    public void removeItemByFridgeId(Food food, String fridgeId) {
        try {
            Log.d(TAG, "attempting delete: " + food.getFirebaseKey());
            mDatabase.child("fridges").child(fridgeId).child("items").child(food.getFirebaseKey()).removeValue();
            Action deleteAction = new Action( mUserId, food.getFirebaseKey(),  "deleted"
                    ,  Calendar.getInstance().getTimeInMillis());
            deleteAction.setFoodName(food.getFoodName());
            mDatabase.child("fridges").child(fridgeId).child("history").push().setValue(deleteAction);
        } catch (Exception e) {
        }

//        mDatabase.child("fridges").child(fridgeId)
//                .addListenerForSingleValueEvent(new ValueEventListener() {
//                    @Override
//                    public void onDataChange(DataSnapshot dataSnapshot) {
//                        dataSnapshot.getRef().child("items").push().setValue(food);
//                    }
//
//                    @Override
//                    public void onCancelled(DatabaseError databaseError) {
//
//                    }
//                });
    }

    public void addActionByFridgeId(Action action, String fridgeId){
        mDatabase.child("fridges").child(fridgeId)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        dataSnapshot.getRef().child("history").push().setValue(action);
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });

    }

    public void setUserNames(String firstName, String lastName){
        mDatabase.child("users").child(mUserId)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        dataSnapshot.getRef().child("name").setValue(firstName+" "+lastName);
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });

    }

    public String getUserId(){
        return this.mUserId;
    }

    //Save and get profile pic url
    public void saveProfilePicToUser(String ProfilePicUrl){
        mDatabase.child("users").child(mUserId)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        dataSnapshot.getRef().child("profilePic").setValue(ProfilePicUrl);
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
    }
    public DatabaseReference getProfilePicUrlReference(){
        return mDatabase.child("users").child(mUserId).child("profilePic");
    }


    public void saveUserProfileInfo(String userName, String photoURL){
        mDatabase.child("users").child(mUserId)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        dataSnapshot.getRef().child("name").setValue(userName);
                        dataSnapshot.getRef().child("profilePic").setValue(photoURL);
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
    }

    public DatabaseReference getReferenceByUserId(String userID) {
        return mDatabase.child("users").child(userID);
    }

    public DatabaseReference getUserReference(){
        return mDatabase.child("users").child(mUserId);
    }
    public void removeFridgeFromUserlist(String fridgeId) {
        mDatabase.child("users").child(mUserId).child("fridgelist").child(fridgeId).removeValue();
    }
}
