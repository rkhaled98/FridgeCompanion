package com.fridgecompanion.ui.setting;

import android.content.ActivityNotFoundException;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.cloudinary.android.MediaManager;
import com.cloudinary.android.callback.ErrorInfo;
import com.cloudinary.android.callback.UploadCallback;
import com.cloudinary.android.preprocess.BitmapEncoder;
import com.cloudinary.android.preprocess.DimensionsValidator;
import com.cloudinary.android.preprocess.ImagePreprocessChain;
import com.fridgecompanion.BundleKeys;
import com.fridgecompanion.FirebaseDatasource;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.soundcloud.android.crop.Crop;

import com.fridgecompanion.R;
import com.squareup.picasso.Picasso;

import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Map;

public class ProfileActivity extends AppCompatActivity {

    private final String TAG = "Profile Activity";

    // Profile picture stuff
    private Uri ProfilePicUri;
    private ImageView ProfilePicView;
    private ImageButton ProfilePicButton;
    private boolean isTakenFromCamera;
    private static final int REQUEST_CODE_TAKE_PICTURE = 0;
    private static final int REQUEST_CODE_FROM_GALLERY = 1;
    private static final String URI_INSTANCE_STATE_KEY = "saved_uri";
    private final String[] CAMERA_OPTIONS = {
            "Open Camera",
            "Select from Gallery"
    };
    private static final int OPTIONS_TAKE_PHOTO = 0;
    private static final int OPTIONS_FROM_GALLERY = 1;
    EditText last;
    EditText first;

    //For cloud storage
    private String ProfilePicUrl;
    private String location;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        ProfilePicButton = (ImageButton) findViewById(R.id.profilePictureImageButton);
        if (savedInstanceState != null) {
            ProfilePicUri = savedInstanceState.getParcelable(URI_INSTANCE_STATE_KEY);
        }
        loadProfilePicture();
        ProfilePicButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                displayCameraOptions(view);
            }
        });
        first = (EditText) findViewById(R.id.first_name_text_id);
        last = (EditText) findViewById(R.id.last_name_text_id);

        try {
            FirebaseDatasource firebaseDatasource = new FirebaseDatasource(getApplicationContext());
            firebaseDatasource.getUserReference().child("name").addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    if(dataSnapshot.exists()){
                        String[] strings = dataSnapshot.getValue(String.class).split("\\s+");
                        if(strings.length == 2){
                            first.setText(strings[0]);
                            last.setText(strings[1]);
                        }
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelable(URI_INSTANCE_STATE_KEY, ProfilePicUri);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode != RESULT_OK) {
            return;
        }

        switch (requestCode) {
            case REQUEST_CODE_TAKE_PICTURE:
                beginCrop(ProfilePicUri);
                break;
            case REQUEST_CODE_FROM_GALLERY:
                ProfilePicUri = data.getData();
                beginCrop(ProfilePicUri);
                break;
            case Crop.REQUEST_CROP:
                handleCrop(resultCode, data);
                if (isTakenFromCamera) {
                   // File f = new File(ProfilePicUri.getPath());
//                    if (f.exists()) {
//                        f.delete();
                    //}
                }
                break;
        }
    }

    /**
     * Method: loadProfilePicture
     * Notes: Method to load a user's profile picture
     * TODO: load profile picture from cloud storage
     */
    private void loadProfilePicture() {
        String profilePicFileName;
        profilePicFileName = getString(R.string.profile_photo_file_name);

        //Load Profile pic from Cloud
        FirebaseDatasource firebaseDatasource = null;
        try {
            firebaseDatasource = new FirebaseDatasource(getApplicationContext());
        } catch (Exception e) {
            e.printStackTrace();
        }

        firebaseDatasource.getProfilePicUrlReference().get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DataSnapshot> task) {
                if (!task.isSuccessful()) {
                }
                else {
                    String url = (String) task.getResult().getValue();
                    if(url==null){
                        // Default profile photo if no photo saved before
                        ProfilePicButton.setImageResource(R.drawable.stock_user_picture);
                        Log.d(TAG, "ERROR: Couldn't find the saved image");
                    }
                    else{
                        // Load saved profile photo
                        Picasso.get().load(url).into(ProfilePicButton);

                    }
                }
            }
        });


//        try {
//            // Load saved profile photo
//            //FileInputStream fis = openFileInput(profilePicFileName);
//            //Bitmap bmap = BitmapFactory.decodeStream(fis);
//            if(ProfilePicUri!=null){
//                Bitmap bmap = MediaStore.Images.Media.getBitmap(this.getContentResolver() , ProfilePicUri);
//                ProfilePicButton.setImageBitmap(bmap);
//            }
//
//            //fis.close();
//        } catch (IOException e) {
//            // Default profile photo if no photo saved before
//            ProfilePicButton.setImageResource(R.drawable.stock_user_picture);
//            e.printStackTrace();
//            Log.d(TAG, "ERROR: Couldn't find the saved image");
//        }
    }

    /**
     * Method: saveProfilePicture
     * Notes: Method to save a user's profile picture
     * TODO: save image into cloud storage
     */
    private void saveProfilePicture() {
        // Save profile photo to internal storage
//        ProfilePicButton.buildDrawingCache();
//        Bitmap bmap = ProfilePicButton.getDrawingCache();
//        try {
//            FileOutputStream fos = openFileOutput(getString(R.string.profile_photo_file_name), MODE_PRIVATE);
//            bmap.compress(Bitmap.CompressFormat.JPEG, 100, fos);
//            fos.flush();
//            fos.close();
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
        // Save to Cloud
        MediaManager.get().upload(ProfilePicUri).unsigned("fridgeupload").preprocess(
                ImagePreprocessChain.limitDimensionsChain(300,300)
                        .addStep(new DimensionsValidator(10,10,300,300))
                        .saveWith(new BitmapEncoder(BitmapEncoder.Format.JPEG, 50)))
                .callback(new UploadCallback() {
                    @Override
                    public void onStart(String requestId) {}
                    @Override
                    public void onProgress(String requestId, long bytes, long totalBytes) {}
                    @Override
                    public void onSuccess(String requestId, Map resultData) {
                        String tempUrl = (String) resultData.get("url");

                        tempUrl = "https://res.cloudinary.com/foreverzhang98/image/upload/a_90/"+tempUrl.substring(54);
                        ProfilePicUrl = tempUrl;
                        Log.d("hz",ProfilePicUrl);
                        Log.d("checkstore",tempUrl);
                        //attach food to firebase here
                        FirebaseDatasource firebaseDatasource = null;
                        try {
                            firebaseDatasource = new FirebaseDatasource(getApplicationContext());
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        firebaseDatasource.saveProfilePicToUser(ProfilePicUrl);
                    }
                    @Override
                    public void onError(String requestId, ErrorInfo error) {}
                    @Override
                    public void onReschedule(String requestId, ErrorInfo error) {}
                }).startNow(getApplicationContext());
        String last_name = last.getText().toString();
        String first_name = first.getText().toString();
        saveUserName(first_name, last_name);
        Toast.makeText(this, "Saved", Toast.LENGTH_SHORT).show();
        finish();

    }

    /**
     * Method: displayCameraOptions
     * Notes: When the user clicks on the image button to change profile picture,
     *        present the options they have to choose a profile pic from
     */
    public void displayCameraOptions(View view) {
        // Setup alert for dialog
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Pick Profile Picture:");
        builder.setItems(CAMERA_OPTIONS, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // Determine which options the user has clicked on
                switch (which) {
                    case OPTIONS_TAKE_PHOTO:
                        takePicture();
                        break;
                    case OPTIONS_FROM_GALLERY:
                        fromGallery();
                        break;
                    default:
                        break;
                }
            }
        });

        AlertDialog dialog = builder.create();
        dialog.show();
    }

    private void beginCrop(Uri source) {
        Uri destination = Uri.fromFile(new File(getCacheDir(), "cropped"));
        Crop.of(source, destination).asSquare().start(this);
    }

    private void handleCrop(int resultCode, Intent result) {
        if (RESULT_OK == resultCode) {
            ProfilePicUri = Crop.getOutput(result);
            ProfilePicButton.setImageURI(Crop.getOutput(result));
        } else if (Crop.RESULT_ERROR == resultCode) {
            Toast.makeText(this, Crop.getError(result).getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

//    private void beginGallery(Uri source,Intent data) {
//        try {
//            ProfilePicUri = data.getData();
//            InputStream inputStream = getContentResolver().openInputStream(ProfilePicUri);
//            Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
//            ProfilePicButton.setImageBitmap(bitmap);
//        } catch (FileNotFoundException e) {
//            e.printStackTrace();
//        }
//    }

    private void takePicture() {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        // Construct temporary image path and name to save the taken photo
        ContentValues values = new ContentValues(1);
        values.put(MediaStore.Images.Media.MIME_TYPE, "image/jpeg");
        ProfilePicUri = getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, ProfilePicUri);
        intent.putExtra("return_data", true);
        try {
            startActivityForResult(intent, REQUEST_CODE_TAKE_PICTURE);
        } catch (ActivityNotFoundException e) {
            e.printStackTrace();
        }
        isTakenFromCamera = true;
    }

    private void fromGallery() {
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");
        Intent photoPickerIntent = new Intent(Intent.ACTION_PICK);
        photoPickerIntent.setType("image/*");
        intent.putExtra(MediaStore.EXTRA_OUTPUT, ProfilePicUri);
        intent.putExtra("return-data", true);

        try {
            startActivityForResult(intent, REQUEST_CODE_FROM_GALLERY);
        } catch (ActivityNotFoundException e) {
            e.printStackTrace();
        }
        isTakenFromCamera = false;
    }
    //Save names
    public void saveUserName(String first_name, String last_name){
        FirebaseDatasource firebaseDatasource = null;
        try {
            firebaseDatasource = new FirebaseDatasource(getApplicationContext());
        } catch (Exception e) {
            e.printStackTrace();
        }
        firebaseDatasource.setUserNames(first_name,last_name);

    }
    public void onProfileSaveButtonClicked(View view) {
        String last_name = last.getText().toString();
        String first_name = first.getText().toString();
        if(!first_name.trim().isEmpty() && !last_name.trim().isEmpty()){
            saveProfilePicture();
        }else{
            Toast.makeText(this, "Missing Names", Toast.LENGTH_SHORT).show();
        }
    }

    public void onProfileCancelButtonClicked(View view) {
        Toast.makeText(this, "Canceled", Toast.LENGTH_SHORT).show();
        finish();
    }
}
