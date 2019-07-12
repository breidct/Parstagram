package com.example.parstagram;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.PopupMenu;
import android.util.Log;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import com.parse.ParseFile;
import com.parse.ParseUser;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;

public class ProfileEditActivity extends AppCompatActivity {


    private ImageButton ibProfile;
    private Button btnProfilePic;
    private EditText etFirst;
    private EditText etLast;
    private EditText etUsername;
    private EditText etBio;
    private EditText etEmail;
    private Button btnSave;
    private File photoFile;
    public String photoFileName = "photo.jpg";
    public final static int CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE = 1034;
    public final static int PICK_IMAGE_ACTIVITY_REQUEST_CODE = 1035;
    private ParseFile parseFile;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_edit);

        ibProfile = findViewById(R.id.ibProfile);
        btnProfilePic = findViewById(R.id.btnProfilePic);
        etFirst = findViewById(R.id.etFirst);
        etLast = findViewById(R.id.etLast);
        etUsername = findViewById(R.id.etUsername);
        etBio = findViewById(R.id.etBio);
        etEmail = findViewById(R.id.etEmail);
        btnSave = findViewById(R.id.btnSave);



        btnProfilePic.setOnClickListener(setProfilePic());

        ibProfile.setOnClickListener(setProfilePic());



        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(etUsername.getText().toString().equals("")) {
                    ParseUser.getCurrentUser().setUsername(etUsername.getText().toString());
                }
                if (etEmail.getText().toString().equals("")) {
                    ParseUser.getCurrentUser().setEmail(etEmail.getText().toString());
                }
                if(etFirst.getText().toString().equals("") && etLast.getText().toString().equals("")){
                    ParseUser.getCurrentUser().put("name",etFirst.getText().toString() +
                            " " + etLast.getText().toString());
                }
                if(etBio.getText().toString().equals("")){
                    ParseUser.getCurrentUser().put("bio", etBio.getText().toString());
                }
                if(parseFile != null){
                    ParseUser.getCurrentUser().put("profilePic", parseFile);
                }
                ParseUser.getCurrentUser().saveInBackground();
                Intent intent = new Intent(ProfileEditActivity.this, HomeActivity.class);
                startActivity(intent);
            }
        });


    }

    private View.OnClickListener setProfilePic() {
        return new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PopupMenu popup = new PopupMenu(ProfileEditActivity.this, v);
                MenuInflater inflater = popup.getMenuInflater();
                inflater.inflate(R.menu.menu_compose, popup.getMenu());
                popup.show();
                popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem menuItem) {
                        switch (menuItem.getItemId()){
                            case R.id.miTakePhoto:
                                launchCamera();
                                return true;
                            case R.id.miChoosePhoto:
                                //Toast.makeText(getContext(), "Choose photo from camera roll", Toast.LENGTH_SHORT).show();
                                launchCameraroll();
                                return true;
                            default:
                                return false;
                        }
                    }
                });
            }
        };
    }

    private void launchCamera() {
        // create Intent to take a picture and return control to the calling application
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        // Create a File reference to access to future access
        photoFile = getPhotoFileUri(photoFileName);
        ParseFile parseFile = new ParseFile(photoFile);

        // wrap File object into a content provider
        // required for API >= 24
        // See https://guides.codepath.com/android/Sharing-Content-with-Intents#sharing-files-with-api-24-or-higher
        Uri fileProvider = FileProvider.getUriForFile(ProfileEditActivity.this, "com.codepath.fileprovider", photoFile);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, fileProvider);

        // If you call startActivityForResult() using an intent that no app can handle, your app will crash.
        // So as long as the resquult is not null, it's safe to use the intent.
        if (intent.resolveActivity(getPackageManager()) != null) {
            // Start the image capture intent to take photo
            startActivityForResult(intent, CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE);
        }
    }

    private void launchCameraroll(){
        //create intent to pick picture from camera roll
        Intent pickPhoto = new Intent(Intent.ACTION_PICK,
                android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);

        photoFile = getPhotoFileUri(photoFileName);
        parseFile = new ParseFile(photoFile);

        Uri fileProvider = FileProvider.getUriForFile(ProfileEditActivity.this, "com.codepath.fileprovider", photoFile);
        pickPhoto.putExtra(MediaStore.EXTRA_OUTPUT, fileProvider);

        if(pickPhoto.resolveActivity(getPackageManager()) != null) {
            startActivityForResult(pickPhoto, PICK_IMAGE_ACTIVITY_REQUEST_CODE);
        }
    }

    public File getPhotoFileUri(String fileName) {
        // Get safe storage directory for photos
        // Use `getExternalFilesDir` on Context to access package-specific directories.
        // This way, we don't need to request external read/write runtime permissions.
        File mediaStorageDir = new File(getExternalFilesDir
                (Environment.DIRECTORY_PICTURES), "ProfileEditActivity");

        // Create the storage directory if it does not exist
        if (!mediaStorageDir.exists() && !mediaStorageDir.mkdirs()){
            Log.d("ProfileEditActivity", "failed to create directory");
        }

        // Return the file target for the photo based on filename
        File file = new File(mediaStorageDir.getPath() + File.separator + fileName);

        return file;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                // by this point we have the camera photo on disk
                Bitmap takenImage = BitmapFactory.decodeFile(photoFile.getAbsolutePath());
                // RESIZE BITMAP, see section below
                // Load the taken image into a preview
                ibProfile.setImageBitmap(takenImage);
                // parseFile = new ParseFile()
            } else { // Result was a failure
                Toast.makeText(ProfileEditActivity.this, "Picture wasn't taken!", Toast.LENGTH_SHORT).show();
            }
        }else if (requestCode == PICK_IMAGE_ACTIVITY_REQUEST_CODE){
            if (data != null && resultCode == RESULT_OK){
                Uri photoUri = data.getData();

                // Do something with the photo based on Uri
                Bitmap selectedImage = null;
                try {
                    selectedImage = MediaStore.Images.Media.getBitmap(getContentResolver(), photoUri);
                } catch (IOException e) {
                    e.printStackTrace();
                }

//                selectedImage =  BitmapFactory.decodeFile(photoFile.getAbsolutePath());
                ByteArrayOutputStream stream = new ByteArrayOutputStream();
//                 Compress image to lower quality scale 1 - 100
                selectedImage.compress(Bitmap.CompressFormat.PNG, 100, stream);
                byte[] image = stream.toByteArray();

                // Create the ParseFile
                parseFile = new ParseFile(image);
                parseFile.saveInBackground();
                //photoFile = new ParseFile("androidbegin.png", image);
                // Upload the image into Parse Cloud
                //photoFile.saveInBackground();

                // Load the selected image into a preview
                ibProfile.setImageBitmap(selectedImage);
            }
        }
    }
}
