package com.example.parstagram;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.parse.LogInCallback;
import com.parse.ParseException;
import com.parse.ParseUser;

public class MainActivity extends AppCompatActivity {

    private EditText usernameInput;
    private EditText passwordInput;
    private Button loginBtn;
    private Button btnSignup;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ParseUser currentUser = ParseUser.getCurrentUser();
        if (currentUser != null) {
            Intent intent = new Intent(MainActivity.this, HomeActivity.class);
            startActivity(intent);
            finish();
        }

        usernameInput = findViewById(R.id.usernameInput);
        passwordInput = findViewById(R.id.passwordInput);
        loginBtn = findViewById(R.id.loginBtn);
        btnSignup = findViewById(R.id.btnConfirm);

        Intent intent = getIntent();
        usernameInput.setText(intent.getStringExtra("username"));

        btnSignup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, SignupActivity.class);
                intent.putExtra("username",usernameInput.getText().toString());
                intent.putExtra("password",passwordInput.getText().toString());
                startActivity(intent);
            }
        });

        loginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String username = usernameInput.getText().toString();
                final String password = passwordInput.getText().toString();

                login(username,password);
            }
        });
    }

    private void login(String username, String password){
        ParseUser.logInInBackground(username, password, new LogInCallback() {
            @Override
            public void done(ParseUser user, ParseException e) {
                if( e == null){
                    Log.d("LoginActivity", "Login successful!");
                    final Intent intent= new Intent(MainActivity.this,HomeActivity.class);
                    startActivity(intent);
                    finish();
                }else{
                    Log.d("LoginActivity", "Login failure");
                    e.printStackTrace();
                }
            }
        });
    }

}
