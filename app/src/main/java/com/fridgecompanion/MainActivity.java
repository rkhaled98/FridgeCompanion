package com.fridgecompanion;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.fridgecompanion.ui.home.HomeFragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

public class MainActivity extends AppCompatActivity {

    private FirebaseAuth mFirebaseAuth;
    private FirebaseUser mFirebaseUser;
    private DatabaseReference mDatabase;
    private String mUserId;
    private String fridgeID = "";

    private LinearLayout layoutFabSave;
    private LinearLayout layoutFabCopy;
    private LinearLayout layoutFabDelete;
    ImageView helperlayer;
    FloatingActionButton fab;
    private Boolean fabOpened = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mFirebaseAuth = FirebaseAuth.getInstance();
        mFirebaseUser = mFirebaseAuth.getCurrentUser();
        mDatabase = FirebaseDatabase.getInstance().getReference();

        Bundle b = getIntent().getExtras();
        if(b != null){
            fridgeID = b.getString("FRIDGE_KEY");
        }

        if (mFirebaseUser == null) {
            loadLogInView();
        } else {
            setContentView(R.layout.activity_main);
            BottomNavigationView navView = findViewById(R.id.nav_view);
            // Passing each menu ID as a set of Ids because each
            // menu should be considered as top level destinations.
            AppBarConfiguration appBarConfiguration = new AppBarConfiguration.Builder(
                    R.id.navigation_home, R.id.navigation_history, R.id.navigation_setting)
                    .build();
            NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
//            NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration);
            NavigationUI.setupWithNavController(navView, navController);
            navView.setOnNavigationItemReselectedListener(new BottomNavigationView.OnNavigationItemReselectedListener() {
                @Override
                public void onNavigationItemReselected(@NonNull MenuItem item) {
                    // Nothing here to disable reselect
                }
            });

            layoutFabSave = (LinearLayout)findViewById(R.id.layoutFabSave);
            layoutFabCopy = (LinearLayout)findViewById(R.id.layoutFabCopy);
            layoutFabDelete = (LinearLayout)findViewById(R.id.layoutFabDelete);
            helperlayer = (ImageView) findViewById(R.id.helper_layer);

            fab = findViewById(R.id.fab);
            fab.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    //Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                    //.setAction("Action", null).show();
                    if(fabOpened){
                        closeSubMenusFab();
                    }else{
                        openSubMenusFab();
                    }
                }
            });
        }
    }



    private void loadLogInView() {
        Intent intent = new Intent(this, LogInActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
    }

    private void closeSubMenusFab(){
        layoutFabSave.setVisibility(View.INVISIBLE);
        layoutFabCopy.setVisibility(View.INVISIBLE);
        layoutFabDelete.setVisibility(View.INVISIBLE);
        helperlayer.setVisibility(View.INVISIBLE);
        fab.setImageResource(R.drawable.ic_baseline_fastfood_24);
        fabOpened = false;
    }

    private void openSubMenusFab(){
        layoutFabSave.setVisibility(View.VISIBLE);
        layoutFabCopy.setVisibility(View.VISIBLE);
        layoutFabDelete.setVisibility(View.VISIBLE);
        helperlayer.setVisibility(View.VISIBLE);
        fab.setImageResource(R.drawable.ic_baseline_close_24);
        fabOpened = true;
    }

    public void onClickGreyScreen(View view){
        closeSubMenusFab();
    }



//    @Override
//    public boolean onCreateOptionsMenu(Menu menu) {
//        // Inflate the menu; this adds items to the action bar if it is present.
//        getMenuInflater().inflate(R.menu.menu_main, menu);
//        return true;
//    }
//
//    @Override
//    public boolean onOptionsItemSelected(MenuItem item) {
//        // Handle action bar item clicks here. The action bar will
//        // automatically handle clicks on the Home/Up button, so long
//        // as you specify a parent activity in AndroidManifest.xml.
//        int id = item.getItemId();
//
//        //noinspection SimplifiableIfStatement
//        if (id == R.id.action_logout) {
//            mFirebaseAuth.signOut();
//            loadLogInView();
//        }
//
//        return super.onOptionsItemSelected(item);
//    }

    public void onClickBack(View view){
        finish();
    }

    public void onClickCopy(View v) {
        if (fridgeID == null || fridgeID.isEmpty()) {
            Toast.makeText(this, "Could not copy invite code!", Toast.LENGTH_SHORT).show();
        } else {
            ClipboardManager clipboard = (ClipboardManager) this.getSystemService(Context.CLIPBOARD_SERVICE);
            ClipData clip = ClipData.newPlainText("not sure what this label does", fridgeID);
            clipboard.setPrimaryClip(clip);

            Toast.makeText(this, "Fridge invite code copied to clipboard", Toast.LENGTH_SHORT).show();
        }
        closeSubMenusFab();

    }

    public void onClickDelete(View v) {
        try {
            FirebaseDatasource firebaseDatasource = new FirebaseDatasource(this);
            firebaseDatasource.removeFridgeFromUserlist(fridgeID);
            finish();
        }catch (Exception e){

        }
        closeSubMenusFab();
    }

    public void onClickSave(View view) {
        //Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
        //.setAction("Action", null).show();
        closeSubMenusFab();
        Bundle fridgeBundle = getIntent().getExtras();

        Intent intent = new Intent(getApplicationContext(), ItemEntryActivity.class);

        intent.putExtras(fridgeBundle);
        startActivity(intent);
    }

}