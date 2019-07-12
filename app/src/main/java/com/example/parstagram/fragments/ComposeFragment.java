package com.example.parstagram.fragments;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.FileProvider;
import android.support.v7.widget.PopupMenu;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.parstagram.R;
import com.example.parstagram.model.Post;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;

import static android.app.Activity.RESULT_OK;

public class ComposeFragment extends Fragment {

    private EditText descriptionInput;
    private Button createButton;
    private Button cameraButton;
    private ImageView ivPreview;
    public final String APP_TAG = "ComposeFragment";

    public final static int CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE = 1034;
    public final static int PICK_IMAGE_ACTIVITY_REQUEST_CODE = 1035;
    public String photoFileName = "photo.jpg";
    private File photoFile;
    private ParseFile parseFile;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_compose,container,false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        descriptionInput = view.findViewById(R.id.etDescription);
        createButton = view.findViewById(R.id.btnCreate);
        cameraButton = view.findViewById(R.id.btnCamera);
        ivPreview = (ImageView) getView().findViewById(R.id.ivPreview);


        cameraButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PopupMenu popup = new PopupMenu(getContext(), v);
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
        });

        //loadTopPosts();

        createButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                 String description = descriptionInput.getText().toString();
                 ParseUser user = ParseUser.getCurrentUser();
                 if(photoFile == null || ivPreview.getDrawable() == null){
                     Log.e(APP_TAG,"No photo to submit");
                     Toast.makeText(getContext(), "No photo to submit", Toast.LENGTH_SHORT).show();
                     return;
                 }
                 if (parseFile == null) {
                     parseFile = new ParseFile(photoFile);
                 }
                 savePost(description,user, parseFile);
                Toast.makeText(getContext(), "Photo created!", Toast.LENGTH_SHORT).show();

                //final File file = new File TODO
            }


        });
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
        Uri fileProvider = FileProvider.getUriForFile(getActivity(), "com.codepath.fileprovider", photoFile);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, fileProvider);

        // If you call startActivityForResult() using an intent that no app can handle, your app will crash.
        // So as long as the resquult is not null, it's safe to use the intent.
        if (intent.resolveActivity(getContext().getPackageManager()) != null) {
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

        Uri fileProvider = FileProvider.getUriForFile(getActivity(), "com.codepath.fileprovider", photoFile);
        pickPhoto.putExtra(MediaStore.EXTRA_OUTPUT, fileProvider);

        if(pickPhoto.resolveActivity(getContext().getPackageManager()) != null) {
            startActivityForResult(pickPhoto, PICK_IMAGE_ACTIVITY_REQUEST_CODE);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                // by this point we have the camera photo on disk
                Bitmap takenImage = BitmapFactory.decodeFile(photoFile.getAbsolutePath());
                // RESIZE BITMAP, see section below
                // Load the taken image into a preview
                ivPreview.setImageBitmap(takenImage);
               // parseFile = new ParseFile()
            } else { // Result was a failure
                Toast.makeText(getContext(), "Picture wasn't taken!", Toast.LENGTH_SHORT).show();
            }
        }else if (requestCode == PICK_IMAGE_ACTIVITY_REQUEST_CODE){
            if (data != null && resultCode == RESULT_OK){
                Uri photoUri = data.getData();

                // Do something with the photo based on Uri
                Bitmap selectedImage = null;
                try {
                    selectedImage = MediaStore.Images.Media.getBitmap(getContext().getContentResolver(), photoUri);
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
                ivPreview.setImageBitmap(selectedImage);
            }
        }
    }

    // Returns the File for a photo stored on disk given the fileName
    public File getPhotoFileUri(String fileName) {
        // Get safe storage directory for photos
        // Use `getExternalFilesDir` on Context to access package-specific directories.
        // This way, we don't need to request external read/write runtime permissions.
        File mediaStorageDir = new File(getContext().getExternalFilesDir(Environment.DIRECTORY_PICTURES), APP_TAG);

        // Create the storage directory if it does not exist
        if (!mediaStorageDir.exists() && !mediaStorageDir.mkdirs()){
            Log.d(APP_TAG, "failed to create directory");
        }

        // Return the file target for the photo based on filename
        File file = new File(mediaStorageDir.getPath() + File.separator + fileName);

        return file;
    }


    private void savePost(String description, ParseUser user, ParseFile photoFile) {
        Post post = new Post();
        post.setDescription(description);
        post.setUser(user);
        post.setImage(photoFile);
        post.saveInBackground(new SaveCallback() {
            @Override
            public void done(ParseException e) {
                if (e != null){
                    Log.d("ComposeFragment", "Error while saving");
                    e.printStackTrace();
                    return;
                }
                Log.d("ComposeFragment", "Saving succesful");
                descriptionInput.setText("");
                ivPreview.setImageResource(0);
            }
        });
    }

    private void createPosts(String description, ParseFile imageFile, ParseUser user){
        //Todo
    }


//    private void loadTopPosts() {
//        final Post.Query postsQuery = new Post.Query();
//        postsQuery.getTop().withUser();
//
//        postsQuery.findInBackground(new FindCallback<Post>() {
//            @Override
//            public void done(List<Post> objects, ParseException e) {
//                if(e == null){
//                    for(int i = 0; i < objects.size(); i++) {
//                        Log.d("HomeActivity", "Post[" + i +"] = "
//                                + objects.get(i).getDescription()
//                                + "\nusername = " + objects.get(i).getUser().getUsername());
//                    }
//                }else{
//                    e.printStackTrace();
//                }
//            }
//        });
//    }
}
