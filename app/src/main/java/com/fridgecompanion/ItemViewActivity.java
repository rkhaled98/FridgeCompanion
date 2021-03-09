package com.fridgecompanion;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.Calendar;

public class ItemViewActivity extends AppCompatActivity {

    private ImageButton buttonDelete;
    FirebaseDatasource firebaseDatasource;
    private String TAG = "firebasehomefragment";
    Food food;
    TextView foodName;
    TextView foodDes;
    ImageView foodImage;
    TextView foodExpireDate;
    TextView foodPurchaseDate;
    TextView foodCal;
    TextView foodQuantity;
    TextView foodNutrition;
    TextView foodLeftDays;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_item_view);


        //set up text
        foodName = (TextView) findViewById(R.id.food_item_title);
        foodDes = (TextView) findViewById(R.id.food_notes);
        foodImage = (ImageView) findViewById(R.id.food_item_image);
        foodExpireDate = (TextView) findViewById(R.id.food_expire_date);
        foodPurchaseDate = (TextView) findViewById(R.id.food_purchase_date);
        foodCal = (TextView) findViewById((R.id.food_calories));
        foodQuantity = (TextView) findViewById((R.id.amount_left));
        foodNutrition = (TextView) findViewById(R.id.food_nutrition);
        foodLeftDays=  (TextView) findViewById(R.id.leftover_days);

        buttonDelete = (ImageButton) findViewById(R.id.trash_button);

        Bundle b = getIntent().getExtras();
        if (b!= null){
            food = (Food) b.getSerializable(BundleKeys.FOOD_OBJECT_KEY);
        }else{
            food = new Food();
        }
    }

    @Override
    protected void onResume() {
        try {
            firebaseDatasource = new FirebaseDatasource(this);
            firebaseDatasource.getItemsReferenceByFridgeId(food.getFirebaseFridgeId()).child(food.getFirebaseKey()).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    if(dataSnapshot.exists()) {
                        food = dataSnapshot.getValue(Food.class);
                        food.setFirebaseKey(dataSnapshot.getKey());
                        updateInputWithFood();
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                    System.out.println("The read failed: " + databaseError.getCode());
                }
            });

        } catch (Exception e) {
            Log.d("failed",e.toString());

        }
        super.onResume();
    }

    private void updateInputWithFood(){
        //retrieve from bundle and update UI
        foodName.setText(food.getFoodName());

        String foodNotes = "Notes: " ;
        if(food.getFoodDescription() != null &&!food.getFoodDescription().isEmpty()) {
            foodNotes = "Notes: " + food.getFoodDescription();
        }
        foodDes.setText(foodNotes);

        String calories = "Calories: ";
        if(food.getCalories() != 0) {
            calories = "Calories: " + food.getCalories();
        }
        foodCal.setText(calories);

        foodQuantity.setText(food.getQuantityString());

        String expireDateString = "Expiration Date: "+ food.getExpireDateString();
        foodExpireDate.setText(expireDateString);

        String purchaseDateString = "Purchase Date: "+ food.getEnteredDateString();
        foodPurchaseDate.setText(purchaseDateString);

        String nutrition = "Nutrition: ";
        if(food.getNutrition() != null &&!food.getNutrition().isEmpty()){
            nutrition = "Nutrition: " + food.getNutrition();
        }
        foodNutrition.setText(nutrition);

        long currentTime =Calendar.getInstance().getTimeInMillis();
        foodLeftDays.setText(food.getDaysFromExpirationString(currentTime));

        if(food.getDaysFromExpiration(currentTime)<4){
            foodLeftDays.setTextColor(Color.parseColor("#ff0000"));
        }else{
            foodLeftDays.setTextColor(Color.parseColor("#808080"));
        }

        if (!food.getImage().isEmpty()) {
            Picasso.get().load(food.getImage()).into(foodImage);
        }
    }

    @Override
    protected void onStart() {
        super.onStart();

        buttonDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    Log.d(TAG, "deleting this food: " + food.getFirebaseKey() + food.toString());
                    firebaseDatasource.removeItemByFridgeId(food, food.getFirebaseFridgeId());
                    finish();
                } catch (Exception e){
                    Toast.makeText(getApplicationContext(), "could not remove the item", Toast.LENGTH_SHORT).show();
                }
                finish();
            }
        });
    }


    public void onClickBack(View view){
        finish();
    }

    public void onClickEdit(View view){
        Intent intent = new Intent(this, ItemEntryActivity.class);
        Bundle b = getIntent().getExtras();
        b.putSerializable(BundleKeys.FOOD_OBJECT_KEY, food);
        intent.putExtras(b);
        startActivity(intent);
    }
}