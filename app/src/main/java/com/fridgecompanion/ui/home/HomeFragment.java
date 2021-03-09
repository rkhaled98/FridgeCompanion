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
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.firebase.ui.database.FirebaseListAdapter;
import com.fridgecompanion.BundleKeys;
import com.fridgecompanion.FirebaseDatasource;
import com.fridgecompanion.Food;
import com.fridgecompanion.Fridge;
import com.fridgecompanion.ItemViewActivity;
import com.fridgecompanion.R;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.IntStream;

public class HomeFragment extends Fragment {

    private HomeViewModel homeViewModel;
    private FoodAdapter adapter;
    private FoodAdapter foodAdapter;
    private FoodAdapter foodAdapter2;
    private GridView lv;
    private GridView gv;
    private ImageView backgroundImg;
    FirebaseDatasource firebaseDatasource;
    FirebaseListAdapter firebaseListAdapter;
    private String TAG = "firebasehomefragment";
    private FridgeAdapter fridgeAdapter;
    private List<Fridge> fridges;
    private List<Food> foods;
    private ImageButton viewButton;
    private String fridgeID;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        viewButton = (ImageButton) view.findViewById(R.id.view_button);

        ImageButton backButton = (ImageButton) view.findViewById(R.id.back_button);

        backgroundImg = (ImageView)view.findViewById(R.id.new_item_background);


        fridges = new ArrayList<Fridge>();

        foods = new ArrayList<Food>();

        foodAdapter = new FoodAdapter(getActivity(), R.layout.layout_food_item, foods, FoodAdapter.LIST_MODE);

        foodAdapter2 = new FoodAdapter(getActivity(), R.layout.layout_food_item_v2, foods, FoodAdapter.GALLERY_MODE);

        fridgeAdapter = new FridgeAdapter(getActivity(), R.layout.layout_fridge_item, fridges);

        Bundle b = getActivity().getIntent().getExtras();
        if (b != null){
            fridgeID = b.getString("FRIDGE_KEY");
            TextView tv = (TextView)view.findViewById(R.id.fridge_name_text);
            tv.setText(b.getString("FRIDGE_NAME"));
        }
        Log.d("test", fridgeID);

        try {
            firebaseDatasource = new FirebaseDatasource(getContext());

            firebaseDatasource.getItemsReferenceByFridgeId(fridgeID).addChildEventListener(new ChildEventListener() {
                @Override
                public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                    Food foodToAdd = snapshot.getValue(Food.class);
                    foodToAdd.setFirebaseKey(snapshot.getKey());
                    foods.add(foodToAdd);
                    if (gv.getVisibility() == GridView.GONE){
                        foodAdapter.notifyDataSetChanged();
                    }else {
                        foodAdapter2.notifyDataSetChanged();
                    }

//                    if(foods.size() != 0){
//                        backgroundImg.setVisibility(ImageView.GONE);
//                    }else{
//                        backgroundImg.setVisibility(ImageView.VISIBLE);
//                    }

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
                    Food food = snapshot.getValue(Food.class);
                    int index = IntStream.range(0, foods.size())
                            .filter(i -> foods.get(i).getFirebaseKey().equals(food.getFirebaseKey()))
                            .findFirst().orElse(-1);
                    if (index != -1){
                        foods.set(index, food);
                    }else{

                    }

                    if (gv.getVisibility() == GridView.GONE){
                        foodAdapter.notifyDataSetChanged();
                    }else {
                        foodAdapter2.notifyDataSetChanged();
                    }


//                    if(foods.size() != 0){
//                        backgroundImg.setVisibility(ImageView.GONE);
//                    }else{
//                        backgroundImg.setVisibility(ImageView.VISIBLE);
//                    }
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
                    // coming from firebase
                    Food foodToRemove = snapshot.getValue(Food.class);
//                    foods.remove(foodToRemove);

                    foods.removeIf(item -> item.getFirebaseKey().equals(snapshot.getKey()));

//                    Log.d(TAG, "attempting remove from adapter:" + foodToRemove.toString() + foods.contains(foodToRemove) + foods.get(0).toString());

                    if (gv.getVisibility() == GridView.GONE){
                        foodAdapter.notifyDataSetChanged();
                    }else {
                        foodAdapter2.notifyDataSetChanged();
                    }

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
            backButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    getActivity().finish();
                }
            });
            viewButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
////                    Item item = new Item("test name" , 544);
//                    firebaseDatasource.addItemToUser(food);
//                    Fridge fridge = new Fridge("test fridge");
//                    firebaseDatasource.createFridge(fridge);
//                    Log.d(TAG, ""+firebaseListAdapter.getCount());
//                    firebaseDatasource.createFridge(fridge);

//                    mDatabase.child("users").child(mUserId).child("items").push().setValue(item);
                    if (gv.getVisibility() == GridView.GONE){
                        gv.setVisibility(GridView.VISIBLE);
                        lv.setVisibility(GridView.GONE);
                        viewButton.setImageResource(R.drawable.ic_baseline_view_list_24);
                    }else{
                        lv.setVisibility(GridView.VISIBLE);
                        gv.setVisibility(GridView.GONE);
                        viewButton.setImageResource(R.drawable.ic_baseline_grid_on_24);
                    }
                }
            });

        } catch (Exception e) {

        }


        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);



        lv = (GridView) view.findViewById(R.id.fridgelistview);
        gv = (GridView) view.findViewById(R.id.fridgegallery);
        gv.setAdapter(foodAdapter2);
        lv.setAdapter(foodAdapter);

        lv.setOnItemClickListener(
                new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                        Intent intent = new Intent(getContext(), ItemViewActivity.class);
                        Food food = foodAdapter.getItem(position);
                        Bundle b = new Bundle();
                        Log.d(TAG, "creating new bundle for the food with the key" + food.getFirebaseKey());
                        b.putSerializable(BundleKeys.FOOD_OBJECT_KEY, food);
                        b.putString(BundleKeys.FOOD_NAME_KEY, food.getFoodName());
                        b.putString(BundleKeys.FOOD_IMAGE_KEY, food.getImage());
                        b.putInt(BundleKeys.FOOD_QUANTITY_KEY, food.getQuantity());
                        b.putString(BundleKeys.FOOD_DESCRIPTION_KEY, food.getFoodDescription());
                        intent.putExtras(b);
                        startActivity(intent);
                    }
                }
        );

        gv.setOnItemClickListener(
                new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                        Intent intent = new Intent(getContext(), ItemViewActivity.class);
                        Food food = foodAdapter2.getItem(position);
                        Bundle b = new Bundle();
                        b.putSerializable(BundleKeys.FOOD_OBJECT_KEY, food);
                        b.putString(BundleKeys.FOOD_NAME_KEY, food.getFoodName());
                        b.putString(BundleKeys.FOOD_IMAGE_KEY, food.getImage());
                        b.putInt(BundleKeys.FOOD_QUANTITY_KEY, food.getQuantity());
                        b.putString(BundleKeys.FOOD_DESCRIPTION_KEY, food.getFoodDescription());
                        intent.putExtras(b);
                        startActivity(intent);
                    }
                }
        );

//        if(foods.size() != 0){
//            backgroundImg.setVisibility(ImageView.GONE);
//        }else{
//            backgroundImg.setVisibility(ImageView.VISIBLE);
//            backgroundImg.setImageResource(R.drawable.rectangle_1_4);
//        }


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
}