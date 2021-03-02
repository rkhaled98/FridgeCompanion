package com.fridgecompanion.ui.home;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.fridgecompanion.Food;
import com.fridgecompanion.R;
import com.squareup.picasso.Picasso;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

public class FoodAdapter extends ArrayAdapter<Food> {

    private final Context mContext;
    List<Food> foods;
    private int resourceLayout;


    public FoodAdapter(@NonNull Context context, int resource, @NonNull List<Food> objects) {
        super(context, resource, objects);
        this.mContext = context;
        this.foods = objects;
        this.resourceLayout = resource;
    }

    @Override
    public int getCount() {
        return foods.size();
    }

    @Override
    public Food getItem(int i) {
        return foods.get(i);
    }
//
//    @Override
//    public long getItemId(int i) {
//        return i;
//    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View view, @NonNull ViewGroup parent) {
        Food food = this.foods.get(position);
        if (view == null) {
            final LayoutInflater layoutInflater = LayoutInflater.from(mContext);
            view = layoutInflater.inflate(resourceLayout, null);
        }
        final TextView foodName = (TextView) view.findViewById(R.id.food_title);
        final TextView foodDes = (TextView) view.findViewById(R.id.food_description);
        final ImageView foodImage = (ImageView) view.findViewById(R.id.food_image);
        final TextView foodDate = (TextView) view.findViewById(R.id.expire_date);

        if (!food.getImage().isEmpty()) {
            Picasso.get().load(food.getImage()).into(foodImage);
        } else {
            foodImage.setImageResource(R.drawable.beef);
        }


        foodName.setText(food.getFoodName());
        foodDes.setText(food.getFoodDescription());
        foodDate.setText("Expired");
//        foodImage.setImageResource(R.drawable.beef);

        return view;
    }
}
