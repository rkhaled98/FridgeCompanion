package com.fridgecompanion;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import me.dm7.barcodescanner.zxing.ZXingScannerView;

import android.Manifest;
import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.google.zxing.Result;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;
import java.io.IOException;

public class BarcodeScanner extends AppCompatActivity implements ZXingScannerView.ResultHandler {
    int PERMISSIONS_REQUEST_CAMERA = 0;
    public static String barcode_text;
    ZXingScannerView scannerView;
    public Food item;
    String key = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        barcode_text = new String();
        setContentView(R.layout.activity_barcode);
        scannerView = new ZXingScannerView(this);
        setContentView(scannerView);

        Bundle b = getIntent().getExtras();
        if(b!= null){
            key = b.getString("FRIDGE_KEY");
        }
    }

    @Override
    public void handleResult(Result result) {
        barcode_text = result.getText();
        //search barcode in EDAMAM
        EdamamService.searchBarcode(barcode_text, new Callback() {
            //Notify user to check Internet if getting response failed.
            @Override
            public void onFailure(Call call, IOException e) {
                runOnUiThread(new Runnable() {
                    public void run() {
                        Toast.makeText(getApplicationContext()
                                , "No Internet! Please check connections"
                                , Toast.LENGTH_SHORT).show();
                    }
                });
                finish();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                item = EdamamService.processResults(response);
                try {

//                    Food food = new Food();
//                    food.setFoodName(item.getFoodName());
//                    food.setCalories(item.getCalories());
//                    food.setImage(item.getImage());
//                    food.setNutrition(item.getNutrition());
                    Intent returnIntent = new Intent();
                    returnIntent.putExtra(ItemEntryActivity.FOODNAME,item.getFoodName());
                    returnIntent.putExtra(ItemEntryActivity.CALORIES,item.getCalories());
                    returnIntent.putExtra(ItemEntryActivity.IMAGE,item.getImage());
                    returnIntent.putExtra(ItemEntryActivity.NUTRITION,item.getNutrition());
                    setResult(Activity.RESULT_OK,returnIntent);
                    Log.d("checkk","added" + item.getFoodName() + item.getImage()+item.getCalories()+item.getNutrition());
                    Log.d("check",item.getId());
                    Log.d("check",item.getFoodName());
                } catch (Exception e) {
                    e.printStackTrace();
                }
                //Check if the database returns item info. Use manual entry if not.
                if(item.getFoodName().isEmpty()){
                    runOnUiThread(new Runnable() {
                        public void run() {
                            Toast.makeText(getApplicationContext()
                                    , "Items Not Found in database. Please enter manually."
                                    , Toast.LENGTH_LONG).show();
                        }
                    });
                }
                finish();
            }
        });
    }
    @Override
    protected void onPause() {
        super.onPause();
        scannerView.stopCamera();
    }
    @Override
    protected void onPostResume() {
        super.onPostResume();
        if (ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.CAMERA)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA},
                    PERMISSIONS_REQUEST_CAMERA);
        }
        scannerView.setResultHandler(this);
        scannerView.startCamera();
    }
}
