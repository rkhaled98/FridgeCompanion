package com.fridgecompanion;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.os.Bundle;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

public class ItemViewActivity extends AppCompatActivity {

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

        //retrieve from bundle and store in food
        Food food = null;
        Bundle b = getIntent().getExtras();
        if (b!= null){
            food = retrieveFromBundle(b);
        }else{
            food = new Food();
        }

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