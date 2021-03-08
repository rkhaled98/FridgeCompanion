package com.fridgecompanion.ui.home;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.fridgecompanion.Food;
import com.fridgecompanion.R;
import com.squareup.picasso.Picasso;

import org.w3c.dom.Text;

import java.util.Calendar;
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
        if(viewMode == LIST_MODE){
            return foods.size();
        }
        return Math.max(12, foods.size()+(3-foods.size()%3)%3);
    }

    @Override
    public boolean isEnabled(int position) {
        if(position < foods.size()){
            return true;
        }
        return false;
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
        if (view == null) {
            final LayoutInflater layoutInflater = LayoutInflater.from(mContext);
            view = layoutInflater.inflate(resourceLayout, null);
        }

        final ImageView foodImage = (ImageView) view.findViewById(R.id.food_image);

        if (position < foods.size()){
            Food food = this.foods.get(position);
            if (!food.getImage().isEmpty()) {
                Picasso.get().load(food.getImage()).into(foodImage);
            }else{
                ;
            }

            if (viewMode == LIST_MODE){
                final TextView foodName = (TextView) view.findViewById(R.id.food_title);
                final TextView foodDate = (TextView) view.findViewById(R.id.expire_date);
                final TextView foodLeftOver = (TextView)view.findViewById(R.id.amount_left);
                foodName.setText(food.getFoodName());

                long currentTime =Calendar.getInstance().getTimeInMillis();
                foodDate.setText(food.getDaysFromExpirationString(currentTime));
                foodLeftOver.setText(food.getQuantityString());

                if(food.getDaysFromExpiration(currentTime)<4){
                    foodDate.setTextColor(Color.parseColor("#ff0000"));
                }else{
                    foodDate.setTextColor(Color.parseColor("#808080"));
                }
            }
        }else{
            ;
        }

        return view;
    }
}
