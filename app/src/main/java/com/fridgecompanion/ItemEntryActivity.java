package com.fridgecompanion;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;
import com.squareup.picasso.Picasso;

import java.io.IOException;
import java.util.Calendar;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class ItemEntryActivity extends AppCompatActivity {
    private final String formatDate = "MM.dd.yyyy";
    public static final int LAUNCH_BARCODE = 1;
    public static final String FOODNAME = "name";
    public static final String IMAGE = "image";
    public static final String CALORIES = "calories";
    public static final String NUTRITION = "nutrition";

    EditText nameEdit;
    EditText quantityEdit;
    EditText purchaseEdit;
    EditText expireEdit;
    EditText calorieEdit;
    EditText nutritionEdit;
    EditText noteEdit;
    Spinner spinner;
    ImageView imageView;

    public Food food;
    public String imageUrl;
    public Food tempItem = new Food();

    Calendar expireDate = Calendar.getInstance();
    Calendar purchaseDate = Calendar.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_item_entry);

        nameEdit = (EditText)findViewById(R.id.name_edit);
        quantityEdit = (EditText)findViewById(R.id.quantity_edit);
        purchaseEdit = (EditText)findViewById(R.id.purchase_edit);
        expireEdit = (EditText)findViewById(R.id.expire_edit);
        calorieEdit = (EditText)findViewById(R.id.calories_edit);
        nutritionEdit = (EditText)findViewById(R.id.nutrition_edit);
        noteEdit = (EditText)findViewById(R.id.note_edit);
        spinner = (Spinner)findViewById(R.id.unit_spinner);
        imageView = (ImageView)findViewById((R.id.image_edit));

        setDefaultTime();
        purchaseEdit.setText(DateFormat.format(formatDate, purchaseDate.getTime()).toString());

        food = new Food();

    }

    private void setDefaultTime(){
        expireDate.set(Calendar.SECOND, 0);
        expireDate.set(Calendar.MINUTE, 0);
        expireDate.set(Calendar.HOUR, 0);
        expireDate.set(Calendar.MILLISECOND, 0);
        purchaseDate.set(Calendar.SECOND, 0);
        purchaseDate.set(Calendar.MINUTE, 0);
        purchaseDate.set(Calendar.HOUR, 0);
        purchaseDate.set(Calendar.MILLISECOND, 0);
    }

    //Search item name in the Edamam database
    public void onClickButtonSearch(View view){
        searchDatabasebyName(nameEdit.getText().toString());
        nameEdit.setText(tempItem.getFoodName());
        int data_cal = (int) tempItem.getCalories();
        calorieEdit.setText(String.valueOf(data_cal));
        nutritionEdit.setText(tempItem.getNutrition());
        imageUrl = tempItem.getImage();
        Picasso.get().load(imageUrl).into(imageView);
    }

    public void searchDatabasebyName(String name) {
        EdamamService.searchName(name, new Callback() {
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
                tempItem = EdamamService.processResults(response);
                runOnUiThread(new Runnable() {
                    public void run() {
                        Toast.makeText(getApplicationContext()
                                , "No Internet! Please check connections"
                                , Toast.LENGTH_SHORT).show();
                    }
                });
                //Check if the database returns item info. Use manual entry if not.
                if(tempItem.getFoodName().isEmpty()){
                    runOnUiThread(new Runnable() {
                        public void run() {
                            Toast.makeText(getApplicationContext()
                                    , "Items Not Found in database. Please enter manually."
                                    , Toast.LENGTH_LONG).show();
                        }
                    });
                }
            }
        });
    }

    public void onClickExpireDate(View view){
        DatePickerDialog.OnDateSetListener myDateListener = new DatePickerDialog.OnDateSetListener() {
            public void onDateSet(DatePicker view, int year, int monthOfYear,
                                  int dayOfMonth) {
                expireDate.set(Calendar.YEAR, year);
                expireDate.set(Calendar.MONTH, monthOfYear);
                expireDate.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                expireEdit.setText(DateFormat.format(formatDate, expireDate.getTime()).toString());
            }
        };

        DatePickerDialog D = new DatePickerDialog(this, myDateListener,
                expireDate.get(Calendar.YEAR),
                expireDate.get(Calendar.MONTH),
                expireDate.get(Calendar.DAY_OF_MONTH));
        D.show();
    }

    public void onClickPurchaseDate(View view){
        DatePickerDialog.OnDateSetListener myDateListener = new DatePickerDialog.OnDateSetListener() {
            public void onDateSet(DatePicker view, int year, int monthOfYear,
                                  int dayOfMonth) {
                purchaseDate.set(Calendar.YEAR, year);
                purchaseDate.set(Calendar.MONTH, monthOfYear);
                purchaseDate.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                purchaseEdit.setText(DateFormat.format(formatDate, purchaseDate.getTime()).toString());
            }
        };

        DatePickerDialog D = new DatePickerDialog(this, myDateListener,
                purchaseDate.get(Calendar.YEAR),
                purchaseDate.get(Calendar.MONTH),
                purchaseDate.get(Calendar.DAY_OF_MONTH));
        D.show();
    }
    //Display results from Edamam database
    public void onClickRightButton(View view){
        //code for barcode stuff - forever :)
        Intent intent = new Intent(getApplicationContext(), BarcodeScanner.class);
        startActivityForResult(intent,LAUNCH_BARCODE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == LAUNCH_BARCODE) {
            if(resultCode == Activity.RESULT_OK){
                Bundle bundle = data.getExtras();
                nameEdit.setText(bundle.getString(FOODNAME));
                int data_cal = (int) bundle.getDouble(CALORIES,0);
                calorieEdit.setText(String.valueOf(data_cal));
                nutritionEdit.setText(bundle.getString(NUTRITION));
                imageUrl = bundle.getString(IMAGE);
                Picasso.get().load(imageUrl).into(imageView);
            }
            if (resultCode == Activity.RESULT_CANCELED) {
                //Write your code if there's no result
            }
        }
    }

    public void onClickLeftButton(View view){
        finish();
    }

    public void onClickSaveButton(View view) throws Exception {
        if(validEntry()){
            saveFoodFromEntry();
            //attach food to firebase here
            FirebaseDatasource firebaseDatasource = new FirebaseDatasource(getApplicationContext());
            firebaseDatasource.addItemToUser(food);
            Toast.makeText(getApplicationContext(), "Item Added", Toast.LENGTH_SHORT).show();
            finish();
        }
    }

    private boolean validEntry(){
        if (isEmptyEditText(nameEdit)){
            nameEdit.requestFocus();
            Toast.makeText(getApplicationContext(), "Product name is required!", Toast.LENGTH_SHORT).show();
            return false;
        }

        if(isEmptyEditText(quantityEdit)){
            quantityEdit.requestFocus();
            Toast.makeText(getApplicationContext(), "Quantity is required!", Toast.LENGTH_SHORT).show();
            return false;
        }else if(spinner.getSelectedItemPosition() == 1 && Integer.parseInt(quantityEdit.getText().toString()) >100){
            quantityEdit.requestFocus();
            Toast.makeText(getApplicationContext(), "Quantity(%) over 100!", Toast.LENGTH_SHORT).show();
            return false;
        }

        if(isEmptyEditText(expireEdit)){
            expireEdit.requestFocus();
            Toast.makeText(getApplicationContext(), "Expiration date is required!", Toast.LENGTH_SHORT).show();
            return false;
        }else if (expireDate.getTimeInMillis() - purchaseDate.getTimeInMillis() < 0){
            expireEdit.requestFocus();
            Toast.makeText(getApplicationContext(), "Bought expired food??", Toast.LENGTH_SHORT).show();
            return false;
        }

        return true;
    }
    private boolean isEmptyEditText(EditText etText) {
        return etText.getText().toString().trim().length() == 0;
    }
    private void saveFoodFromEntry(){
        //must have entry
        food.setFoodName(nameEdit.getText().toString());
        food.setEnteredDate(purchaseDate.getTimeInMillis());
        food.setExpireDate(expireDate.getTimeInMillis());
        food.setUnit(spinner.getSelectedItemPosition());
        food.setQuantity(Integer.parseInt(quantityEdit.getText().toString()));


        //optional entry
        if(!isEmptyEditText(noteEdit)){
            food.setFoodDescription(noteEdit.getText().toString());
        }
        if(!isEmptyEditText(nutritionEdit)){
            food.setNutrition(nutritionEdit.getText().toString());
        }
        if(!isEmptyEditText(calorieEdit)){
            food.setCalories(Double.parseDouble(calorieEdit.getText().toString()));
        }
        if(!imageUrl.isEmpty()){
            food.setImage(imageUrl);
        }

    }




}