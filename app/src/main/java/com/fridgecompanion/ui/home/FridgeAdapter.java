package com.fridgecompanion.ui.home;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.fridgecompanion.Food;
import com.fridgecompanion.Fridge;
import com.fridgecompanion.R;
import com.squareup.picasso.Picasso;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

public class FridgeAdapter extends ArrayAdapter<Fridge> {

    private final Context mContext;
    List<Fridge> fridges;
    private int resourceLayout;

    private String TAG = "firebasehomefragment";


    public FridgeAdapter(@NonNull Context context, int resource, @NonNull List<Fridge> objects) {
        super(context, resource, objects);
        Log.d(TAG, "THE FOOD LIST CREATING");
        this.mContext = context;
        this.fridges = objects;
        this.resourceLayout = resource;
    }

    @Override
    public int getCount() {
        return fridges.size();
    }

    @Override
    public Fridge getItem(int i) {
        return fridges.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View view, @NonNull ViewGroup parent) {
        Fridge fridge = getItem(position);
        if (view == null) {
            LayoutInflater layoutInflater = LayoutInflater.from(mContext);
            view = layoutInflater.inflate(R.layout.layout_fridge_item, null);
        }

        LinearLayout linearLayout = (LinearLayout) view.findViewById(R.id.linearLayoutFood);
        List<Food> foodList = fridge.getItems();

        for (Food food: foodList) {
            final LayoutInflater layoutInflater = LayoutInflater.from(mContext);
            View currView = layoutInflater.inflate(R.layout.layout_food_item, null);

            final TextView foodName = (TextView) currView.findViewById(R.id.food_title);
            //final TextView foodDes = (TextView) currView.findViewById(R.id.food_description);
            final ImageView foodImage = (ImageView) currView.findViewById(R.id.food_image);
            final TextView foodDate = (TextView) currView.findViewById(R.id.expire_date);



            foodName.setText(food.getFoodName());
            //foodDes.setText(food.getFoodDescription());
            foodDate.setText("Expired");

            linearLayout.addView(view);
        }

        Log.d(TAG, "THE FOOD LIST" + foodList.toString());
//
//        FoodAdapter adapter = new FoodAdapter(getContext(), R.layout.layout_food_item, foodList);
//        listViewOfFood.setAdapter(adapter);

//        final TextView foodName = (TextView) view.findViewById(R.id.food_title);
//        final TextView foodDes = (TextView) view.findViewById(R.id.food_description);
//        final ImageView foodImage = (ImageView) view.findViewById(R.id.food_image);
//        final TextView foodDate = (TextView) view.findViewById(R.id.expire_date);
//
//        foodName.setText(fridge.getFoodName());
//        foodDes.setText(fridge.getFoodDescription());
//        foodDate.setText("Expired");
//        foodImage.setImageResource(R.drawable.beef);

        return view;
    }
}
