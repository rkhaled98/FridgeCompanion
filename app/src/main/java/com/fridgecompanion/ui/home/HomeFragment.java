package com.fridgecompanion.ui.home;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.firebase.ui.database.FirebaseListAdapter;
import com.fridgecompanion.FirebaseDatasource;
import com.fridgecompanion.Food;
import com.fridgecompanion.Fridge;
import com.fridgecompanion.R;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;

import java.util.ArrayList;
import java.util.List;

public class HomeFragment extends Fragment {

    private HomeViewModel homeViewModel;
    private FoodAdapter adapter;
    private FoodAdapter foodAdapter;
    private ListView lv;
    FirebaseDatasource firebaseDatasource;
    FirebaseListAdapter firebaseListAdapter;
    private String TAG = "firebasehomefragment";
    private FridgeAdapter fridgeAdapter;
    private List<Fridge> fridges;
    private List<Food> foods;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        final Button btnAddItem = view.findViewById(R.id.button_add_item);

        fridges = new ArrayList<Fridge>();

        foods = new ArrayList<Food>();

        foodAdapter = new FoodAdapter(getActivity(), R.layout.layout_food_item, foods);

        fridgeAdapter = new FridgeAdapter(getActivity(), R.layout.layout_fridge_item, fridges);

        try {
            firebaseDatasource = new FirebaseDatasource(getContext());

            firebaseDatasource.getItemsReference().addChildEventListener(new ChildEventListener() {
                @Override
                public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                    foods.add(snapshot.getValue(Food.class));
                    foodAdapter.notifyDataSetChanged();

//                    Fridge fridge = new Fridge();
//
//                    DataSnapshot items = snapshot.child("items");
//                    Log.d(TAG, "before getting all items");
//
//                    for (DataSnapshot ds : items.getChildren()) {
//                        Food food = ds.getValue(Food.class);
//                        Log.d(TAG, food.toString());
//                        fridge.addItem(food);
//                    }
//
//
//                    Log.d(TAG, "after getting all items");
////                    fridgeAdapter.add(fridge);
//                    fridges.add(fridge);
//
//                    fridgeAdapter.notifyDataSetChanged();
                }

                @Override
                public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                    foods.add(snapshot.getValue(Food.class));
                    foodAdapter.notifyDataSetChanged();
//                    Fridge fridge = new Fridge();
//
//                    fridges.clear();
//
//                    DataSnapshot items = snapshot.child("items");
//                    Log.d(TAG, "before getting all items");
//
//                    for (DataSnapshot ds : items.getChildren()) {
//                        Food food = ds.getValue(Food.class);
//                        Log.d(TAG, food.toString());
//                        fridge.addItem(food);
//                    }
//
//                    Log.d(TAG, "after getting all items");
////                    fridgeAdapter.add(fridge);
//                    fridges.add(fridge);
//
//                    fridgeAdapter.notifyDataSetChanged();
                }

                @Override
                public void onChildRemoved(@NonNull DataSnapshot snapshot) {

                }

                @Override
                public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });

//            firebaseListAdapter = firebaseDatasource.getMyAdapter();
//            firebaseListAdapter.startListening();
//            lv.setAdapter(firebaseDatasource.getMyAdapter());

            btnAddItem.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Log.d(TAG, "adding item");
//                    Item item = new Item("test name" , 544);
                    Food food = new Food();
                    food.setFoodName("Beef Dish");
                    firebaseDatasource.addItemToUser(food);
//                    Fridge fridge = new Fridge("test fridge");
//                    firebaseDatasource.createFridge(fridge);
//                    Log.d(TAG, ""+firebaseListAdapter.getCount());
//                    firebaseDatasource.createFridge(fridge);

//                    mDatabase.child("users").child(mUserId).child("items").push().setValue(item);
                }
            });

        } catch (Exception e) {

        }


        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);



        lv = (ListView) view.findViewById(R.id.fridgelistview);
        lv.setAdapter(foodAdapter);

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
    public void onStart() {
        super.onStart();
    }

    @Override
    public void onResume() {
        super.onResume();
//        lv.setAdapter(firebaseDatasource.getMyAdapter());
//        DBReader dbreader = new DBReader();
//        dbreader.start();
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