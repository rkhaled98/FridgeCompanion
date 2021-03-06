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

    public static int LIST_MODE = 0;
    public static int GALLERY_MODE = 1;
    private final Context mContext;
    List<Food> foods;
    private int resourceLayout;
    private int viewMode;


    public FoodAdapter(@NonNull Context context, int resource, @NonNull List<Food> objects, int viewMode) {
        super(context, resource, objects);
        this.mContext = context;
        this.foods = objects;
        this.resourceLayout = resource;
        this.viewMode = viewMode;
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

        final ImageView foodImage = (ImageView) view.findViewById(R.id.food_image);

        if (!food.getImage().isEmpty()) {
            Picasso.get().load(food.getImage()).into(foodImage);
        }else{
            foodImage.setImageResource(R.drawable.beef);
        }

        if (viewMode == LIST_MODE){
            final TextView foodName = (TextView) view.findViewById(R.id.food_title);
            final TextView foodDate = (TextView) view.findViewById(R.id.expire_date);
            foodName.setText(food.getFoodName());
            foodDate.setText("Expired");
        }
        return view;
    }
}
