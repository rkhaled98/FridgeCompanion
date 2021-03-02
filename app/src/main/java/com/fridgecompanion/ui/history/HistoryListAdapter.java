package com.fridgecompanion.ui.history;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.fridgecompanion.Food;
import com.fridgecompanion.R;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class HistoryListAdapter extends BaseAdapter {

    private ArrayList<Food> foodList;
    private Context context;

    /**
     * Method: HistoryListAdapter
     * Notes: Constructor
     */
    public HistoryListAdapter(Context context, ArrayList<Food> foodList) {
        this.context = context;
        this.foodList = foodList;
    }

    @Override
    public int getCount() {
        return foodList.size();
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public Object getItem(int position) {
        return foodList.get(position);
    }

    @Override
    public View getView(int position, View view, ViewGroup parent) {
        if (null == view) {
            view = LayoutInflater.from(context).inflate(R.layout.my_custom_list_item,
                    parent, false);
        }

        // Get the first row from the custom list layout
        TextView firstRow = (TextView) view.findViewById(R.id.id_history_first_row);

        // Load the text into the text view
        String foodName = foodList.get(position).getFoodName();
        long millisecond = foodList.get(position).getEnteredDate();
        String date = new SimpleDateFormat("MM/dd/yyy").format(new Date(millisecond));
        firstRow.setText("User added " + foodName + " to fridge. " + date);

        return view;
    }

}
