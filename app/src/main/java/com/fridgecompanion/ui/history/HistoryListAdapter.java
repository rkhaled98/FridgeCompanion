package com.fridgecompanion.ui.history;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.fridgecompanion.Action;
import com.fridgecompanion.Food;
import com.fridgecompanion.R;
import com.squareup.picasso.Picasso;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import androidx.annotation.NonNull;

public class HistoryListAdapter extends ArrayAdapter<Action> {

    private List<Action> actionList;
    private Context context;
    private int resourceLayout;

    /**
     * Method: HistoryListAdapter
     * Notes: Constructor
     */

    public HistoryListAdapter(@NonNull Context context, int resource, @NonNull List<Action> objects) {
        super(context, resource, objects);
        this.context = context;
        this.actionList = objects;
        this.resourceLayout = resource;
    }


    @Override
    public int getCount() {
        return actionList.size();
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public Action getItem(int position) {
        return actionList.get(position);
    }

    @Override
    public View getView(int position, View view, ViewGroup parent) {
        if (null == view) {
            final LayoutInflater layoutInflater = LayoutInflater.from(context);
            view = layoutInflater.inflate(resourceLayout, null);
        }
        Action action = actionList.get(position);
        // Get the first row from the custom list layout

        TextView firstRow = (TextView) view.findViewById(R.id.item_text);
        TextView secondRow = (TextView) view.findViewById(R.id.date_text);
        ImageView imgView = (ImageView) view.findViewById(R.id.profile_img);

        // Load the text into the text view
        long millisecond = actionList.get(position).getActionTime();
        String date = new SimpleDateFormat("MM/dd HH:mm").format(new Date(millisecond));
        firstRow.setText(String.format("%s %s %s", action.getUserName(), action.getActionType(), action.getFoodName()));
        secondRow.setText(date);
        Picasso.get().load(action.getPhotoURL()).into(imgView);

        return view;
    }

}
