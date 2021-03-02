package com.fridgecompanion.ui.home;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.ListFragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.fridgecompanion.FirebaseDatasource;
import com.fridgecompanion.Food;
import com.fridgecompanion.Fridge;
import com.fridgecompanion.Item;
import com.fridgecompanion.R;

import java.util.ArrayList;
import java.util.List;

public class HomeFragment extends Fragment {

    private HomeViewModel homeViewModel;
    private FoodAdapter adapter;
    private GridView lv;
    FirebaseDatasource firebaseDatasource;
    private String TAG = "firebasehomefragment";


    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_home, container, false);

        final Button btnAddItem = root.findViewById(R.id.button_add_item);

        try {
            firebaseDatasource = new FirebaseDatasource();

            btnAddItem.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Log.d(TAG, "adding item");
                    Item item = new Item("test name" , 544);
                    firebaseDatasource.addItemToCurrentUser(item);
                    Fridge fridge = new Fridge("test fridge");
//                    firebaseDatasource.createFridge(fridge);

//                    mDatabase.child("users").child(mUserId).child("items").push().setValue(item);
                }
            });

        } catch (Exception e) {

        }


        return root;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        lv = (GridView)view.findViewById(R.id.gridview);
        lv.setOnItemClickListener(
                new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                        Log.d("sjlee", "Clicked Item");
                    }
                }
        );


    }

    @Override
    public void onResume() {
        super.onResume();
        DBReader dbreader = new DBReader();
        dbreader.start();
    }

    class DBReader extends Thread{
        //thread class for fetching all entries in db and setting up Listview
        private Handler handler = new Handler(Looper.getMainLooper());
        private Runnable extractData = new Runnable(){
            @Override
            public void run() {
                List<Food> foods = fetchFridgeList();
                adapter = new FoodAdapter(getActivity(), R.layout.layout_food_item, foods);
                lv.setAdapter(adapter);
            }
        };
        @Override
        public void run() {
            handler.post(extractData);
        }


        private List<Food> fetchFridgeList(){
            Food food1 = new Food();
            food1.setFoodName("Apple");
            food1.setFoodDescription("Super tasty apple");
            food1.setUnit(Food.UNIT_COUNT);
            food1.setQuantity(2);

            Food food2 = new Food();
            food2.setFoodName("Orange");
            food2.setFoodDescription("Super deceent orange");
            food2.setUnit(Food.UNIT_COUNT);
            food2.setQuantity(10);
            List<Food> foods = new ArrayList<Food>();
            foods.add(food1);
            foods.add(food2);
            foods.add(food1);
            foods.add(food2);
            return foods;
        }
    }
}