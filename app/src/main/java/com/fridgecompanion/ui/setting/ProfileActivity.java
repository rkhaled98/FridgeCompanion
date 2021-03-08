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
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import com.soundcloud.android.crop.Crop;

import com.fridgecompanion.R;

import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

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
                beginGallery(ProfilePicUri, data);
                break;
            case Crop.REQUEST_CROP:
                handleCrop(resultCode, data);
                if (isTakenFromCamera) {
                    File f = new File(ProfilePicUri.getPath());
                    if (f.exists()) {
                        f.delete();
                    }
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

        try {
            // Load saved profile photo
            FileInputStream fis = openFileInput(profilePicFileName);
            Bitmap bmap = BitmapFactory.decodeStream(fis);
            ProfilePicButton.setImageBitmap(bmap);
            fis.close();
        } catch (IOException e) {
            // Default profile photo if no photo saved before
            ProfilePicButton.setImageResource(R.drawable.stock_user_picture);
            e.printStackTrace();
            Log.d(TAG, "ERROR: Couldn't find the saved image");
        }
    }

    /**
     * Method: saveProfilePicture
     * Notes: Method to save a user's profile picture
     * TODO: save image into cloud storage
     */
    private void saveProfilePicture() {
        // Save profile photo to internal storage
        ProfilePicButton.buildDrawingCache();
        Bitmap bmap = ProfilePicButton.getDrawingCache();
        try {
            FileOutputStream fos = openFileOutput(getString(R.string.profile_photo_file_name), MODE_PRIVATE);
            bmap.compress(Bitmap.CompressFormat.JPEG, 100, fos);
            fos.flush();
            fos.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
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
            ProfilePicButton.setImageURI(Crop.getOutput(result));
        } else if (Crop.RESULT_ERROR == resultCode) {
            Toast.makeText(this, Crop.getError(result).getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    private void beginGallery(Uri source,Intent data) {
        try {
            ProfilePicUri = data.getData();
            InputStream inputStream = getContentResolver().openInputStream(ProfilePicUri);
            Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
            ProfilePicButton.setImageBitmap(bitmap);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    private void takePicture() {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        // Construct temporary image path and name to save the taken photo
        ContentValues values = new ContentValues(1);
        values.put(MediaStore.Images.Media.MIME_TYPE, "image/jpg");
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
        try {
            startActivityForResult(intent, REQUEST_CODE_FROM_GALLERY);
        } catch (ActivityNotFoundException e) {
            e.printStackTrace();
        }
        isTakenFromCamera = false;
    }

    public void onProfileSaveButtonClicked(View view) {
        saveProfilePicture();
        Toast.makeText(this, "Saved", Toast.LENGTH_SHORT).show();
        finish();
    }

    public void onProfileCancelButtonClicked(View view) {
        Toast.makeText(this, "Canceled", Toast.LENGTH_SHORT).show();
        finish();
    }
}
