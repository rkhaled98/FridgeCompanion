package com.fridgecompanion;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Toast;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.res.ResourcesCompat;

public class AfterSignInActivity extends AppCompatActivity {

    ListView mListView;
    ArrayList<String> fridgeNames = new ArrayList<String>();
    HashMap<Integer, String> fridgeIds = new HashMap<Integer, String>();
    LinearLayout ll;
    EditText fridgeNameEdit;
    EditText fridgeCodeEdit;
    String fridgeNameFromCode = "";
    Bundle bundle = new Bundle();
    Intent intent;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start);

        mListView = (ListView) findViewById(R.id.listview);
        ArrayAdapter<String> optionAdapter = new ArrayAdapter<String>(this,
                R.layout.item_listview,fridgeNames);

        ll = (LinearLayout)findViewById(R.id.select_fridge_container);
        fridgeNameEdit = (EditText)findViewById(R.id.fridge_name_edit);
        fridgeCodeEdit = (EditText)findViewById(R.id.fridge_code_edit);


        getWindow().setNavigationBarColor(ResourcesCompat.getColor(getResources(), R.color.fridge_blue, null));
        intent = new Intent(this, MainActivity.class);

        // Set up listener
        AdapterView.OnItemClickListener mListener = new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Log.d("sjlee", "tested");
                String fridgeName = fridgeNames.get(position);
                String key = fridgeIds.get(position);
                bundle.putString("FRIDGE_NAME", fridgeName);
                bundle.putString("FRIDGE_KEY", key);
                intent.putExtras(bundle);
                startActivity(intent);
            }
        };
        mListView.setOnItemClickListener(mListener);

        try{
            FirebaseDatasource fbData = new FirebaseDatasource(this);
            fbData.getFridgeListReference().addChildEventListener(new ChildEventListener() {
                @Override
                public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                    fridgeNames.add(snapshot.getValue(String.class));
                    fridgeIds.put(fridgeNames.size()-1, snapshot.getKey());
                    int delta = getResources().getDimensionPixelSize(R.dimen.fridge_list_height);
                    int height = getResources().getDimensionPixelSize(R.dimen.fridge_list_default_height) + fridgeNames.size()*delta;
                    ll.getLayoutParams().height = height;
                    ll.requestLayout();
                    optionAdapter.notifyDataSetChanged();
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
            mListView.setAdapter(optionAdapter);

        }catch(Exception e){
            ;
        }

    }



    public void onClickNewFridgeButton(View view){
//        Intent intent = new Intent(this, MainActivity.class);
//        startActivity(intent);
        String fridgeName = fridgeNameEdit.getText().toString().trim();
        if(fridgeName.isEmpty()){
            Toast.makeText(this, "Need a Title!", Toast.LENGTH_SHORT).show();
        }else{
            try {
                FirebaseDatasource fbData = new FirebaseDatasource(AfterSignInActivity.this);
                Fridge f = new Fridge();
                f.setName(fridgeName);
                String key = fbData.createFridge(f);
                bundle.putString("FRIDGE_NAME", fridgeName);
                bundle.putString("FRIDGE_KEY", key);
                intent.putExtras(bundle);
                startActivity(intent);

            } catch (Exception e) {}
        }
    }
    public void onClickAddFridgeButton(View view){
        String fridgeCode = fridgeCodeEdit.getText().toString().trim();
        if(fridgeCode.isEmpty()){
            Toast.makeText(getApplicationContext(), "Need a Code!", Toast.LENGTH_SHORT).show();
            Log.d("testing", "here2");
        }else if(fridgeIds.containsValue(fridgeCode)){
            Toast.makeText(getApplicationContext(), "Already Added Fridge!", Toast.LENGTH_SHORT).show();
            Log.d("testing", "here");
        }else{
            try {
                FirebaseDatasource fbData = new FirebaseDatasource(AfterSignInActivity.this);
                fbData.getFridgesReference().addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot snapshot) {
                        if (snapshot.hasChild(fridgeCode)) {
                            fridgeNameFromCode = snapshot.child(fridgeCode).child("name").getValue(String.class);
                            fbData.addSecondaryOwner(fridgeCode, fridgeNameFromCode);
                            Log.d("testing","working");
                            bundle.putString("FRIDGE_NAME", snapshot.child(fridgeCode).child("name").getValue(String.class));
                            bundle.putString("FRIDGE_KEY", fridgeCode);
                            intent.putExtras(bundle);
                            startActivity(intent);
                        }else{
                            Toast.makeText(getApplicationContext(), "Not a valid key!", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
//                Bundle b = new Bundle();
//                b.putString("FRIDGE_KEY", fridgeCode);
//                b.putString("FRIDGE_NAME", fridgeNameFromCode);
//                Intent intent = new Intent(this, MainActivity.class);
//                intent.putExtras(b);
//                startActivity(intent);
            } catch (Exception e) {}
        }
    }
}