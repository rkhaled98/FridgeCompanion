package com.fridgecompanion;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.InputFilter;
import android.text.Spanned;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.cloudinary.Transformation;
import com.cloudinary.android.MediaManager;
import com.cloudinary.android.UploadRequest;
import com.cloudinary.android.callback.ErrorInfo;
import com.cloudinary.android.callback.UploadCallback;
import com.cloudinary.android.preprocess.BitmapEncoder;
import com.cloudinary.android.preprocess.DimensionsValidator;
import com.cloudinary.android.preprocess.ImagePreprocessChain;
import com.soundcloud.android.crop.Crop;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.io.IOException;
import java.util.Calendar;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class ItemEntryActivity extends AppCompatActivity {
    private final String formatDate = "MM.dd.yyyy";
    public static final int LAUNCH_BARCODE = 0;
    public static final String FOODNAME = "name";
    public static final String IMAGE = "image";
    public static final String CALORIES = "calories";
    public static final String NUTRITION = "nutrition";

    //For Image
    private String tempImgFileName = "profile.jpg";
    private String tempImgFileName2 = "temp.jpg";
    private String tempImgFileName3 = "crop.jpg";
    private Uri tempImgUri;
    private Uri profileImgUri;
    private Uri cropImgUri;
    private int cropStatus = 0;
    public static final String TAG = "test";
    public static final int CAMERA_REQUEST_CODE = 1;
    public static final int GALLERY_REQUEST_CODE = 2;
    public static final String LOCATION_KEY = "location key";
    private String location;

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
    public String tempImageUrl;
    public Food tempItem = new Food();

    private String fridgeID;
    private int editMode = 0;
    // editMode == 0 if adding new item and editMode == 1 if editing an item

    Calendar expireDate = Calendar.getInstance();
    Calendar purchaseDate = Calendar.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_item_entry);
        UiUtils.checkPermission(this);
        File profileImgFile = new File(getExternalFilesDir(null), tempImgFileName);
        File tempImgFile = new File(getExternalFilesDir(null), tempImgFileName2);
        File cropImgFile = new File(getExternalFilesDir(null), tempImgFileName3);
        profileImgUri = FileProvider.getUriForFile(this, "com.fridgecompanion", profileImgFile);
        tempImgUri = FileProvider.getUriForFile(this, "com.fridgecompanion", tempImgFile);
        cropImgUri = FileProvider.getUriForFile(this, "com.fridgecompanion", cropImgFile);

        nameEdit = (EditText)findViewById(R.id.name_edit);
        quantityEdit = (EditText)findViewById(R.id.quantity_edit);

        quantityEdit.setFilters(new InputFilter[]{new DecimalDigitsInputFilter(5,1)});

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

        Bundle b = getIntent().getExtras();
        if (b != null){
            fridgeID = b.getString("FRIDGE_KEY");
            food.setFirebaseFridgeId(fridgeID);

            Food f = (Food)b.getSerializable(BundleKeys.FOOD_OBJECT_KEY);
            if (f != null){
                editMode = 1;
                food = f;
                fridgeID = food.getFirebaseFridgeId();
                nameEdit.setText(food.getFoodName());
                quantityEdit.setText(String.valueOf(food.getQuantity()));
                purchaseEdit.setText(food.getEnteredDateString());
                expireEdit.setText(food.getExpireDateString());
                calorieEdit.setText(String.valueOf(food.getCalories()));
                nutritionEdit.setText(food.getNutrition());
                noteEdit.setText(food.getFoodDescription());
                spinner.setSelection(food.getUnit());
                imageUrl = food.getImage();
                expireDate.setTimeInMillis(food.getExpireDate());
                purchaseDate.setTimeInMillis(food.getEnteredDate());
                //Picasso.get().load(imageUrl).into(imageView);
            }
        }
        Log.d("test", food.getImage());
        Picasso.get().load(food.getImage()).into(imageView);

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
//                        Toast.makeText(getApplicationContext()
//                                , "Searching..."
//                                , Toast.LENGTH_SHORT).show();
                        nameEdit.setText(tempItem.getFoodName());
                        int data_cal = (int) tempItem.getCalories();
                        calorieEdit.setText(String.valueOf(data_cal));
                        nutritionEdit.setText(tempItem.getNutrition());
                        imageUrl = tempItem.getImage();
                        imageView.setRotation(0);
                        Picasso.get().load(imageUrl).into(imageView);
                        cropStatus=0;
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
                imageView.setRotation(0);
                Picasso.get().load(imageUrl).into(imageView);
                cropStatus=0;
            }
            if (resultCode == Activity.RESULT_CANCELED) {
                //Write your code if there's no result
            }
        }
        //For image
        else{
            if (resultCode != RESULT_OK) return;
            if (requestCode == CAMERA_REQUEST_CODE) {
                Crop.of(tempImgUri, cropImgUri).asSquare().start(this);
                Log.d("hz","CropStarted");
            } else if((requestCode == GALLERY_REQUEST_CODE)){
                this.tempImgUri = data.getData();
                Crop.of(tempImgUri, cropImgUri).asSquare().start(this);
                Log.d("hz","CropStarted_Gallery");

            }else if (requestCode == Crop.REQUEST_CROP) {
                Uri tempUri = Crop.getOutput(data);
                imageView.setRotation(90);
                imageView.setImageURI(null);
                imageView.setImageURI(tempUri);
                cropStatus = 1;
                location =tempUri.getPath();
            }
        }

    }

    public void onClickLeftButton(View view){
        finish();
    }

    public void onClickSaveButton(View view) throws Exception {
        if(validEntry()){
            if(cropStatus==0){
                Log.d("hz",cropStatus+"");
                saveFoodFromEntry();
                //attach food to firebase here
                FirebaseDatasource firebaseDatasource = new FirebaseDatasource(getApplicationContext());
                if(editMode == 0){
                    //adding new fridge mode
                    if (!fridgeID.isEmpty()){
                        firebaseDatasource.addItemToFridgeId(food, fridgeID);
                        Toast.makeText(getApplicationContext(), "Item added", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(getApplicationContext(), "Could not add item", Toast.LENGTH_SHORT).show();
                    }

                }else{
                    //editing fridge mode
                    if(!food.getFirebaseFridgeId().isEmpty() && !food.getFirebaseKey().isEmpty()){
                        firebaseDatasource.editItemToFridgeId(food, food.getFirebaseFridgeId(), food.getFirebaseKey());
                    }else {
                        Toast.makeText(getApplicationContext(), "Could not edit item", Toast.LENGTH_SHORT).show();
                    }
                }
            }
            else{
                Log.d("hz","imageupload");
                imageUpload();
            }
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
        food.setNeedsNotification(true);

        if(imageUrl!= null && !imageUrl.isEmpty()){
                food.setImage(imageUrl);
                Log.d("checkstore1", imageUrl);
        }

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


    }

    /** Methods for adding images: taking pictures, selecting images from gallery, and cropping */
//When clicking on Change to take a picture
    public void OnClickNewImage(View view) {
        cropStatus=0;
        tempImageUrl = null;
        CameraDialog dialog = new CameraDialog();
        dialog.show(getSupportFragmentManager(), TAG);
    }

    public void choosePhotoMethod(int which){
        if(which==0) {
            Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            intent.putExtra(MediaStore.EXTRA_OUTPUT, tempImgUri);
            try {
                // Trigger the cropping activity
                startActivityForResult(intent, CAMERA_REQUEST_CODE);

            } catch (ActivityNotFoundException e) {
                e.printStackTrace();
            }
            //startActivityForResult(intent, CAMERA_REQUEST_CODE);
        }
        else if(which==1){
            Intent photoPickerIntent = new Intent(Intent.ACTION_PICK);
            photoPickerIntent.setType("image/*");
            photoPickerIntent.putExtra(MediaStore.EXTRA_OUTPUT, tempImgUri);
            photoPickerIntent.putExtra("return-data", true);

            try {
                // Trigger the cropping activity
                startActivityForResult(photoPickerIntent, GALLERY_REQUEST_CODE);
            } catch (ActivityNotFoundException e) {
                e.printStackTrace();
            }
            //startActivityForResult(photoPickerIntent, CAMERA_REQUEST_CODE);
        }
    }

    public void imageUpload(){
        //Save Image
        File profileImgFile = new File(getExternalFilesDir(null), tempImgFileName);
        File tempImgFile = new File(getExternalFilesDir(null), tempImgFileName2);
        File cropImgFile = new File(getExternalFilesDir(null), tempImgFileName3);
        if (cropStatus == 1) {
            profileImgFile.delete();
            tempImgFile.delete();
            profileImgFile = new File(getExternalFilesDir(null), tempImgFileName);
            cropImgFile.renameTo(profileImgFile);
            location = profileImgUri.getPath();
        }
        MediaManager.get().upload(location).unsigned("fridgeupload").preprocess(
                ImagePreprocessChain.limitDimensionsChain(300,300).
                        addStep(new DimensionsValidator(10,10,300,300)).saveWith(
                        new BitmapEncoder(BitmapEncoder.Format.JPEG, 50))).callback(
                                new UploadCallback() {
            @Override
            public void onStart(String requestId) {

            }

            @Override
            public void onProgress(String requestId, long bytes, long totalBytes) {

            }

            @Override
            public void onSuccess(String requestId, Map resultData) {
                String tempUrl = (String) resultData.get("url");

                tempUrl = "https://res.cloudinary.com/foreverzhang98/image/upload/a_90/"+tempUrl.substring(54);
                imageUrl = tempUrl;
                Log.d("checkstore",tempUrl);
                //food.setImage(imageUrl);
                saveFoodFromEntry();
                //attach food to firebase here
                FirebaseDatasource firebaseDatasource = null;
                try {
                    firebaseDatasource = new FirebaseDatasource(getApplicationContext());
                } catch (Exception e) {
                    e.printStackTrace();
                }
                if(editMode == 0){
                    //adding new fridge mode
                    if (!fridgeID.isEmpty()){
                        firebaseDatasource.addItemToFridgeId(food, fridgeID);
                        Log.d("checkstore11",food.getImage());
                        Toast.makeText(getApplicationContext(), "Item added", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(getApplicationContext(), "Could not add item", Toast.LENGTH_SHORT).show();
                    }

                }else{
                    //editing fridge mode
                    if(!food.getFirebaseFridgeId().isEmpty() && !food.getFirebaseKey().isEmpty()){
                        firebaseDatasource.editItemToFridgeId(food, food.getFirebaseFridgeId(), food.getFirebaseKey());
                    }else {
                        Toast.makeText(getApplicationContext(), "Could not edit item", Toast.LENGTH_SHORT).show();
                    }
                }
            }

            @Override
            public void onError(String requestId, ErrorInfo error) {

            }

            @Override
            public void onReschedule(String requestId, ErrorInfo error) {

            }
        }).startNow(getApplicationContext());
    }

//    https://stackoverflow.com/questions/17423483/how-to-limit-edittext-length-to-7-integers-and-2-decimal-places/21802109
    class DecimalDigitsInputFilter implements InputFilter {

        Pattern mPattern;

        public DecimalDigitsInputFilter(int digitsBeforeZero,int digitsAfterZero) {
            mPattern=Pattern.compile("[0-9]{0," + (digitsBeforeZero-1) + "}+((\\.[0-9]{0," + (digitsAfterZero-1) + "})?)||(\\.)?");
        }

        @Override
        public CharSequence filter(CharSequence source, int start, int end, Spanned dest, int dstart, int dend) {

            Matcher matcher=mPattern.matcher(dest);
            if(!matcher.matches())
                return "";
            return null;
        }

    }


}