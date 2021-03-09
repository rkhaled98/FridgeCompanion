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
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.fridgecompanion.Food;
import com.fridgecompanion.FridgeNotifications;
import com.fridgecompanion.R;

import java.util.ArrayList;
import java.util.Calendar;

public class HistoryFragment extends Fragment {

    private HistoryViewModel historyViewModel;
    private ArrayList<Food> foodList;
    private Context context;
    private ListView listView;
    private Calendar DateAndTime;
    private String[] foodNames = new String[]{"apple", "orange", "chocolate"};
    private final String TAG = "HistoryFragment";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        foodList = new ArrayList<Food>(3);
        // TODO: update based on cloud data
//        for (int i = 0; i < foodNames.length; i++) {
//            Food food = new Food();
//            food.setFoodName(foodNames[i]);
//            DateAndTime = Calendar.getInstance();
//            long now = DateAndTime.getTimeInMillis();
//            food.setEnteredDate(now);
//            foodList.add(food);
//        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_history, container, false);
        setHasOptionsMenu(true);
        listView = (ListView) view.findViewById(R.id.history_list);
        context = view.getContext();
        updateHistory();

        return view;
    }

    /**
     * @Override Method: onCreateOptionsMenu
     * Notes: Create the clickable menu item to allow the user to delete all history entries
     */
    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.delete_all, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    /**
     * @Override Method: onOptionsItemSelected
     * Notes: When the menu item is clicked, delete all history entries
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_delete:
                new Thread(() -> {
                    // Delete history

                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(getContext(), "Deleted history", Toast.LENGTH_SHORT).show();
                        }
                    });
                }).start();
                break;
            default:
                // Not supposed to be here
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * Method: updateHistory
     * Notes: Update the history entries displayed to the user
     */
    public void updateHistory() {
        new Thread(() -> {
            // Do stuff

            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    HistoryListAdapter historyListAdapter = new HistoryListAdapter(getContext(), foodList);
                    listView.setAdapter(historyListAdapter);
                    listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                        @Override
                        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                            // Do something when clicked
                        }
                    });
                }
            });
        }).start();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        FridgeNotifications.cancelNotification();
    }
}