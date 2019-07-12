package com.example.parstagram.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.CenterCrop;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.bumptech.glide.request.RequestOptions;
import com.example.parstagram.MainActivity;
import com.example.parstagram.ProfileEditActivity;
import com.example.parstagram.R;
import com.parse.ParseFile;
import com.parse.ParseUser;

public class ProfileFragment extends Fragment {

    private BottomNavigationView bottomNavigationView;
    private Button btnLogout;
    private Button btnEditProfile;
    private ImageView ivProfileImage;
    private TextView tvHandle;
    private TextView tvName;
    private TextView tvBio;
    ParseUser user;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_profile, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        final FragmentManager fragmentManager = getChildFragmentManager();


        bottomNavigationView = view.findViewById(R.id.bottomNavigationView);
        btnLogout = view.findViewById(R.id.btnLogout);
        ivProfileImage = view.findViewById(R.id.ivProfileImage);
        tvHandle = view.findViewById(R.id.tvHandle);
        tvName = view.findViewById(R.id.tvName);
        tvBio = view.findViewById(R.id.tvBio);
        btnEditProfile = view.findViewById(R.id.btnEditProfile);



        String name = ParseUser.getCurrentUser().getString("name");
        String bio = ParseUser.getCurrentUser().getString("bio");
        tvHandle.setText(ParseUser.getCurrentUser().getUsername());
//        Log.d("ProfileFragment", ParseUser.getCurrentUser().toString());
//        Log.d("ProfileFragment", ParseUser.getCurrentUser().getUsername());
//        Log.d("ProfileFragment", ParseUser.getCurrentUser().getString("username"));





        tvName.setText(name);
        tvBio.setText(bio);
        user = ParseUser.getCurrentUser();
        ParseFile imageProfile = user.getParseFile("profilePic");

        String test = user.getString("name");
        //Log.d("ProfileFragment", test);
        RequestOptions requestOptions = new RequestOptions();
        requestOptions = requestOptions.transforms(new CenterCrop(), new RoundedCorners(200));
        if(imageProfile != null){
            Glide.with(getContext())
                    .load(imageProfile.getUrl())
                    .apply(requestOptions)
                    .into(ivProfileImage);
        }
        btnEditProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getContext(), ProfileEditActivity.class);
                startActivity(intent);
            }
        });

        setupLogoutButton();
        createFragment(fragmentManager);

    }

    private void setupLogoutButton() {
        btnLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ParseUser currentUser = ParseUser.getCurrentUser();
                currentUser.logOut();
                Intent intent = new Intent(getContext(), MainActivity.class);
                intent.putExtra("username",currentUser.getUsername());
                startActivity(intent);
            }
        });
    }

    private void createFragment(final FragmentManager fragmentManager) {
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                Fragment fragment;
                switch (menuItem.getItemId()) {
                    case R.id.action_grid:
                        fragment = new GridFragment();
                        //Toast.makeText(getContext(), "You did it", Toast.LENGTH_SHORT).show();
                        break;
                    case R.id.action_scroll:
                        fragment = new ScrollFragment();
                        break;
                    case R.id.action_tagged:
                        fragment = new TaggedFragment();
                        break;
                    default:
                        return true;
                }
                fragmentManager.beginTransaction().replace(R.id.flContainer2, fragment).commit();
                return true;
            }
        });
    }
}



