package com.fridgecompanion.ui.home;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
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
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.preference.PreferenceManager;

import com.firebase.ui.database.FirebaseListAdapter;
import com.fridgecompanion.BundleKeys;
import com.fridgecompanion.FirebaseDatasource;
import com.fridgecompanion.Food;
import com.fridgecompanion.Fridge;
import com.fridgecompanion.FridgeNotifications;
import com.fridgecompanion.ItemViewActivity;
import com.fridgecompanion.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.stream.IntStream;
import java.util.Timer;
import java.util.TimerTask;

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
//    private ImageButton copyLinkButton;
//    private ImageButton leaveButton;
    private String fridgeID;
    private String fridgeName;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        viewButton = (ImageButton) view.findViewById(R.id.view_button);

//        copyLinkButton = (ImageButton) view.findViewById(R.id.link_button);

//        leaveButton = (ImageButton) view.findViewById(R.id.leave_button);

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
            fridgeName = b.getString("FRIDGE_NAME");
            Log.d("test", fridgeID);
        }


        try {
            firebaseDatasource = new FirebaseDatasource(getContext());

            firebaseDatasource.getItemsReferenceByFridgeId(fridgeID).addChildEventListener(new ChildEventListener() {
                @Override
                public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                    Food foodToAdd = snapshot.getValue(Food.class);
                    foodToAdd.setFirebaseKey(snapshot.getKey());
                    foods.add(foodToAdd);

                    foodAdapter.notifyDataSetChanged();

                    foodAdapter2.notifyDataSetChanged();

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

                    foodAdapter.notifyDataSetChanged();
                    foodAdapter2.notifyDataSetChanged();

                }

                @Override
                public void onChildRemoved(@NonNull DataSnapshot snapshot) {
                    // coming from firebase
                    Food foodToRemove = snapshot.getValue(Food.class);
//                    foods.remove(foodToRemove);

                    foods.removeIf(item -> item.getFirebaseKey().equals(snapshot.getKey()));

//                    Log.d(TAG, "attempting remove from adapter:" + foodToRemove.toString() + foods.contains(foodToRemove) + foods.get(0).toString());

                   foodAdapter.notifyDataSetChanged();
                   foodAdapter2.notifyDataSetChanged();

                }

                @Override
                public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });

            backButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    getActivity().finish();
                }
            });

//            copyLinkButton.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//                    if (fridgeID == null || fridgeID.isEmpty()) {
//                        Toast.makeText(getActivity(), "Could not copy invite code!", Toast.LENGTH_SHORT).show();
//                    } else {
//                        ClipboardManager clipboard = (ClipboardManager) getActivity().getSystemService(Context.CLIPBOARD_SERVICE);
//                        ClipData clip = ClipData.newPlainText("not sure what this label does", fridgeID);
//                        clipboard.setPrimaryClip(clip);
//
//                        Toast.makeText(getActivity(), "Fridge invite code copied to clipboard", Toast.LENGTH_SHORT).show();
//                    }
//
//                }
//            });

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

//            leaveButton.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//                    firebaseDatasource.removeFridgeFromUserlist(fridgeID);
//                    getActivity().finish();
//                }
//            });
        } catch (Exception e) {

        }



        startNotificationTimer();
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

    /**
     * Method: startNotificationTimer
     * Notes: Checks specific requirements to send notifications on a timer
     * Source: https://stackoverflow.com/questions/10748212/how-to-call-function-every-hour-also-how-can-i-loop-this
     */
    public void startNotificationTimer() {
        Log.d(TAG, "In Timer");
        Timer timer = new Timer();
        TimerTask hourlyTask = new TimerTask() {
            @Override
            public void run() {
                // Check for null context
                Context context = getContext();
                if (null == context) {
                    return;
                }
                // Check if notifications are on or off
                if (!FridgeNotifications.areNotificationsOn(getContext())) {
                    Log.d(TAG, "NOTIFS OFF");
                    return; // Return if notifications are off. No need to continue
                }

                timerIterateFood();
            }
        };
        // Schedule the task to run starting now and then every hour...
        timer.schedule(hourlyTask, 01, 1000*60);
    }

    public void timerIterateFood() {
        // Iterate through all the food items in the fridge
        for (int i = 0; i < foods.size(); i++) {
            Food foodItem = foods.get(i);

            // Check if we have already notified user about particular food item
            if (!foodItem.getNeedsNotification()) {
                continue;
            }

            // If a food item is almost expiring, fire notification, no need to further check...
            Date expirationDate = new Date(foodItem.getExpireDate());
            Date now = Calendar.getInstance().getTime();
            if (null ==  expirationDate || 0 == foodItem.getExpireDate()) {
                continue;
            }
            int difference = (int) ( (now.getTime() - expirationDate.getTime()) / (1000 * 60 *60 *24));
            SharedPreferences sharedPreferences =
                    PreferenceManager.getDefaultSharedPreferences(getContext());
            String val = sharedPreferences.getString("key_notification_exp_list_pref", "1");
            int days;
            switch (val) {
                case "2":
                    days = 2;
                    break;
                case "3":
                    days = 3;
                    break;
                default:
                    days = 1;
                    break;
            }

            // Check hit
            if (Math.abs(difference) < days) {
                // Fire notification
                FridgeNotifications.showNotification(getContext(), FridgeNotifications.MSG_EXPIRING_SOON, foodItem, fridgeName, fridgeID);
                foodItem.setNeedsNotification(false);
                firebaseDatasource.editItemToFridgeId(foodItem, foodItem.getFirebaseFridgeId(), foodItem.getFirebaseKey());
                return;
            }
        }
    }

}