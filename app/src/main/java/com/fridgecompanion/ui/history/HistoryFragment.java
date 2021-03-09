package com.fridgecompanion.ui.history;

import android.content.Context;
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
import com.fridgecompanion.FirebaseDatasource;
import com.fridgecompanion.Food;
import com.fridgecompanion.FridgeNotifications;
import com.fridgecompanion.R;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;

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
        listView.setAdapter(adapter);
        try {
            firebaseDatasource = new FirebaseDatasource(getContext());

            firebaseDatasource.getFridgeReferenceById(fridgeID).child("history").limitToLast(20).addChildEventListener(new ChildEventListener() {
                @Override
                public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                    Action action = snapshot.getValue(Action.class);
                    actionList.push(action);
                    Collections.reverse(actionList);
                    adapter.notifyDataSetChanged();
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
        return view;
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
//        FridgeNotifications.cancelNotification();
    }
}