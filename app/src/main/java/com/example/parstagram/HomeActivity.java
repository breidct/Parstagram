package com.example.parstagram;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;

import com.example.parstagram.fragments.ComposeFragment;
import com.example.parstagram.fragments.PostsFragment;
import com.example.parstagram.fragments.ProfileFragment;

public class HomeActivity extends AppCompatActivity {

    private BottomNavigationView bottomNavigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        final FragmentManager fragmentManager = getSupportFragmentManager();


        bottomNavigationView = findViewById(R.id.bottom_navigation);

        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                Fragment fragment;
                switch (menuItem.getItemId()) {
                    case R.id.action_home:
                        //TODo Swap fragment
                        fragment = new PostsFragment();
                        //Toast.makeText(HomeActivity.this, "Home!", Toast.LENGTH_SHORT).show();
                        break;
                    case R.id.action_compose:
                        fragment = new ComposeFragment();
                        //Toast.makeText(HomeActivity.this, "Compose!", Toast.LENGTH_SHORT).show();
                        break;
                    case R.id.action_profile:
                        //TODo Swap fragment
                        fragment = new ProfileFragment();
                        //Toast.makeText(HomeActivity.this, "Profile!", Toast.LENGTH_SHORT).show();
                        break;
                    default: return true;
                }
                fragmentManager.beginTransaction().replace(R.id.flContainer2, fragment).commit();
                return true;
            }
        });
        // Set default selection
        bottomNavigationView.setSelectedItemId(R.id.action_home);
    }
}
