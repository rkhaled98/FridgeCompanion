package com.fridgecompanion;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.os.Bundle;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

public class ItemViewActivity extends AppCompatActivity {

    private ImageButton buttonDelete;
    FirebaseDatasource firebaseDatasource;
    private String TAG = "firebasehomefragment";
    Food food;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_item_view);


        //set up text
        final TextView foodName = (TextView) findViewById(R.id.food_item_title);
        final TextView foodDes = (TextView) findViewById(R.id.food_description);
        final ImageView foodImage = (ImageView) findViewById(R.id.food_item_image);
        final TextView foodDate = (TextView) findViewById(R.id.expire_date);
        final TextView foodCal = (TextView) findViewById((R.id.food_calories));
        final TextView foodQuantity = (TextView) findViewById((R.id.amount_left));

        buttonDelete = (ImageButton) findViewById(R.id.trash_button);

        Bundle b = getIntent().getExtras();
        if (b!= null){
            food = (Food) b.getSerializable(BundleKeys.FOOD_OBJECT_KEY);
            Log.d(TAG, "food has been set with the id: " + food.getFirebaseKey());
        }else{
            food = new Food();
        }



        try {
            firebaseDatasource = new FirebaseDatasource(this);
        } catch (Exception e) {
            e.printStackTrace();
        }




        //retrieve from bundle and store in food



        foodName.setText(food.getFoodName());
        foodDes.setText(food.getFoodDescription());
        String calories = "Calories: "+food.getCalories();
        foodCal.setText(calories);
        String quantity = food.getQuantity()+" Left";
        foodQuantity.setText(quantity);

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
            }
        });
    }

    private Food retrieveFromBundle(Bundle b){
        Food food = new Food();
        food.setFoodName(b.getString(BundleKeys.FOOD_NAME_KEY, "" ));
        food.setFoodDescription(b.getString(BundleKeys.FOOD_DESCRIPTION_KEY, "" ));
        food.setQuantity(b.getInt(BundleKeys.FOOD_QUANTITY_KEY));
        food.setImage(b.getString(BundleKeys.FOOD_IMAGE_KEY));
        return food;
    }

    public void onClickBack(View view){
        finish();
    }
}