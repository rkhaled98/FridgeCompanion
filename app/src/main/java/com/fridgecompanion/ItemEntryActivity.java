package com.fridgecompanion;

import androidx.appcompat.app.AppCompatActivity;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.Toast;

import java.util.Calendar;

public class ItemEntryActivity extends AppCompatActivity {
    private final String formatDate = "MM.dd.yyyy";


    EditText nameEdit;
    EditText quantityEdit;
    EditText purchaseEdit;
    EditText expireEdit;
    EditText calorieEdit;
    EditText nutritionEdit;
    EditText noteEdit;
    Spinner spinner;

    Food food;

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

    public void onClickRightButton(View view){
        //code for barcode stuff - forever :)
    }

    public void onClickLeftButton(View view){
        finish();
    }

    public void onClickSaveButton(View view){
        if(validEntry()){
            saveFoodFromEntry();
            //attach food to firebase here
            Toast.makeText(getApplicationContext(), "Item Added", Toast.LENGTH_SHORT).show();
            finish();
        }
    }

    private boolean validEntry(){
        if (isEmpty(nameEdit)){
            nameEdit.requestFocus();
            Toast.makeText(getApplicationContext(), "Product name is required!", Toast.LENGTH_SHORT).show();
            return false;
        }

        if(isEmpty(quantityEdit)){
            quantityEdit.requestFocus();
            Toast.makeText(getApplicationContext(), "Quantity is required!", Toast.LENGTH_SHORT).show();
            return false;
        }else if(spinner.getSelectedItemPosition() == 1 && Integer.parseInt(quantityEdit.getText().toString()) >100){
            quantityEdit.requestFocus();
            Toast.makeText(getApplicationContext(), "Quantity(%) over 100!", Toast.LENGTH_SHORT).show();
            return false;
        }

        if(isEmpty(expireEdit)){
            expireEdit.requestFocus();
            Toast.makeText(getApplicationContext(), "Expire date is required!", Toast.LENGTH_SHORT).show();
            return false;
        }else if (expireDate.getTimeInMillis() - purchaseDate.getTimeInMillis() < 0){
            expireEdit.requestFocus();
            Toast.makeText(getApplicationContext(), "Bought expired food??", Toast.LENGTH_SHORT).show();
            return false;
        }

        return true;
    }
    private boolean isEmpty(EditText etText) {
        return etText.getText().toString().trim().length() == 0;
    }
    private void saveFoodFromEntry(){
        //must have entry
        food.setFoodName(nameEdit.getText().toString());
        food.setEnteredDate(purchaseDate.getTimeInMillis());
        food.setExpireDate(expireDate.getTimeInMillis());
        food.setUnit(spinner.getSelectedItemPosition());
        food.setQuantity(Integer.parseInt(quantityEdit.toString()));


        //optional entry
        if(!isEmpty(noteEdit)){
            food.setFoodDescription(noteEdit.getText().toString());
        }
        if(!isEmpty(nutritionEdit)){
            food.setNutrition(nutritionEdit.getText().toString());
        }
        if(!isEmpty(calorieEdit)){
            food.setCalories(Double.parseDouble(calorieEdit.getText().toString()));
        }
    }


}