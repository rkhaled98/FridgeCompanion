package com.fridgecompanion.ui.history;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.fridgecompanion.Action;
import com.fridgecompanion.BundleKeys;
import com.fridgecompanion.FirebaseDatasource;
import com.fridgecompanion.Food;
import com.fridgecompanion.FridgeNotifications;
import com.fridgecompanion.ItemViewActivity;
import com.fridgecompanion.R;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.List;
import java.util.Stack;
import java.util.stream.IntStream;

public class HistoryFragment extends Fragment{


    private HistoryViewModel historyViewModel;
    private Stack<Action> actionList;
    private Context context;
    private GridView listView;
    private Calendar DateAndTime;
    private String[] foodNames = new String[]{"apple", "orange", "chocolate"};
    private final String TAG = "HistoryFragment";
    private HistoryListAdapter adapter;
    private String fridgeID;
    private Bundle b = new Bundle();
    private Intent intent;
    FirebaseDatasource firebaseDatasource;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        actionList = new Stack<Action>();

        Bundle b = getActivity().getIntent().getExtras();
        if (b != null){
            fridgeID = b.getString("FRIDGE_KEY");
        }

    }

    @Override
    public View onCreateView(LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_history, container, false);
        listView = (GridView) view.findViewById(R.id.history_list);
        context = view.getContext();
        adapter = new HistoryListAdapter(getActivity(), R.layout.item_history, actionList);
        intent = new Intent(getContext(), ItemViewActivity.class);
        listView.setAdapter(adapter);
        try {
            firebaseDatasource = new FirebaseDatasource(getContext());

            firebaseDatasource.getFridgeReferenceById(fridgeID).child("history").limitToLast(20).addChildEventListener(new ChildEventListener() {
                @Override
                public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                    Action action = snapshot.getValue(Action.class);
                    action.setUserName("User");
                    action.setPhotoURL("https://i.pinimg.com/originals/08/61/b7/0861b76ad6e3b156c2b9d61feb6af864.jpg");
                    firebaseDatasource.getReferenceByUserId(action.getFirebaseUserID()).addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            if(dataSnapshot.exists()) {
                                action.setUserName(dataSnapshot.child("name").getValue(String.class));
                                action.setPhotoURL(dataSnapshot.child("profilePic").getValue(String.class));
                                if(actionList.size()==20){
                                    actionList.pop();
                                }
                                if (!action.getActionType().equals("deleted")){
                                    Log.d("test", " otuside");
                                    firebaseDatasource.getItemsReferenceByFridgeId(fridgeID).child(action.getFirebaseItemKey()).addValueEventListener(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(DataSnapshot dataSnapshot) {
                                            if(dataSnapshot.exists()) {
                                                Log.d("test", " inside");
                                                action.setFoodName(dataSnapshot.child("foodName").getValue(String.class));
                                            }
                                            actionList.push(action);
                                            Collections.sort(actionList,
                                                    (o1, o2) -> Long.compare(o2.getActionTime(), o1.getActionTime()));
                                            adapter.notifyDataSetChanged();
                                        }

                                        @Override
                                        public void onCancelled(DatabaseError databaseError) {
                                            System.out.println("The read failed: " + databaseError.getCode());
                                        }
                                    });
                                }else{
                                    actionList.push(action);
                                    adapter.notifyDataSetChanged();
                                }
                            }
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {
                            System.out.println("The read failed: " + databaseError.getCode());
                        }
                    });
                }

                @Override
                public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
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

        } catch (Exception e) {

        }

        listView.setOnItemClickListener(
                new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                        if(adapter.getItem(position).getActionType().equals("deleted")){
                            Toast.makeText(getContext(), "Can't open! Deleted Item", Toast.LENGTH_SHORT).show();
                        }else{
                            Food food = new Food();
                            food.setFirebaseFridgeId(fridgeID);
                            food.setFirebaseKey(adapter.getItem(position).getFirebaseItemKey());
                            b.putSerializable(BundleKeys.FOOD_OBJECT_KEY, food);

                            try {
                                firebaseDatasource = new FirebaseDatasource(getContext());
                                firebaseDatasource.getItemsReferenceByFridgeId(food.getFirebaseFridgeId()).child(food.getFirebaseKey()).addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(DataSnapshot dataSnapshot) {
                                        Log.d("sjlee","From here");
                                        if(dataSnapshot.exists()) {
                                            intent.putExtras(b);
                                            startActivity(intent);
                                        }else{
                                            Toast.makeText(getActivity().getApplicationContext(), "Can't open! Deleted Item", Toast.LENGTH_SHORT).show();
                                        }
                                    }

                                    @Override
                                    public void onCancelled(DatabaseError databaseError) {
                                        System.out.println("The read failed: " + databaseError.getCode());
                                    }
                                });

                            } catch (Exception e) {
                                Log.d("failed",e.toString());

                            }
                        }
                    }
                }
        );


        return view;
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
//        FridgeNotifications.cancelNotification();
    }
}