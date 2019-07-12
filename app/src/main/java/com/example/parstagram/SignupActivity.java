package com.example.parstagram;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.parse.ParseException;
import com.parse.ParseUser;
import com.parse.SignUpCallback;

public class SignupActivity extends AppCompatActivity {

    private String username;
    private String password;
    private String email;
    private String handle;
    private String firstName;
    private String lastName;
    private EditText etInputUsername;
    private EditText etInputPassword;
    private EditText etInputEmail;
    private EditText etHandle;
    private EditText etFName;
    private EditText etLName;
    private Button btnConfirm;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        etInputUsername = findViewById(R.id.etInputUsername);
        etInputPassword = findViewById(R.id.etInputPassword);
        etInputEmail = findViewById(R.id.etInputEmail);
        etFName = findViewById(R.id.etFName);
        etLName = findViewById(R.id.etLName);
        etHandle = findViewById(R.id.etHandle);
        btnConfirm = findViewById(R.id.btnConfirm);

        Intent intent = getIntent();
        username = intent.getStringExtra("username");
        password = intent.getStringExtra("password");

        etInputUsername.setText(username);
        etInputPassword.setText(password);

        btnConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                username = etInputUsername.getText().toString();
                password = etInputPassword.getText().toString();
                email = etInputEmail.getText().toString();
                handle = etHandle.getText().toString();
                firstName = etFName.getText().toString();
                lastName = etLName.getText().toString();

                createUser();
                //signupLogin();
                finish();
            }
        });


    }

    private void createUser(){
        // Create the ParseUser
        ParseUser user = new ParseUser();
        // Set core properties
        user.setUsername(username);
        user.setPassword(password);
        user.setEmail(email);
        // Set custom properties
         user.put("handle", handle);
         user.put("name",firstName + " " + lastName);
         // Invoke signUpInBackground
        user.signUpInBackground(new SignUpCallback() {
            @Override
            public void done(ParseException e) {
                if (e == null) {
                    Toast.makeText(SignupActivity.this, "New user created", Toast.LENGTH_SHORT).show();
                } else {
                    e.printStackTrace();
                }
            }
        });
    }

//    private void signupLogin(){
//        ParseUser.logInInBackground(username, password, new LogInCallback() {
//            public void done(ParseUser user, ParseException e) {
//                if (user != null) {
//                    Log.d("LoginActivity", "Login successful!");
//                    final Intent intent= new Intent(SignupActivity.this,HomeActivity.class);
//                    startActivity(intent);
//                    finish();
//                } else {
//                    Log.d("LoginActivity", "Login failure");
//                    e.printStackTrace();
//                }
//            }
//        });
//    }
}
