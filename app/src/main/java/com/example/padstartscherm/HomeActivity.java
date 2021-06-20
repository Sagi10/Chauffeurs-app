package com.example.padstartscherm;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;

import java.util.HashMap;
import java.util.Map;

public class HomeActivity extends AppCompatActivity {

    private Button button;
    private RelativeLayout buttonContainer;
    public EditText pas, usr;
    private final String API_URL = "http://52.157.237.247/Login.php";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState); // Call AppCompatActivity's onCreate
        setContentView(R.layout.activity_main); //

        // Get the view's elements' properties
        usr = findViewById(R.id.Username);
        pas = findViewById(R.id.UserPassword);
        button = findViewById(R.id.buttonLogin);

        // Make an attempt to login user
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Get values from textfields
                final String user = usr.getText().toString();
                final String pass = pas.getText().toString();

                Map<String, String> dataList = new HashMap<>(); // Reserve a place for data

                // Store the needed data in the data list
                dataList.put("license_plate", user);
                dataList.put("password", pass);

                // Attempt to login current user
                Request.makeRequest(dataList, API_URL, HomeActivity.this, user, "Gebruiker is ingelogd");

            }
        });

        // Get button container
        buttonContainer = findViewById(R.id.buttonContainer);

        // Let button container click on the actual button
        buttonContainer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                button.performClick();
            }
        });
    }

    // Override this method from its superclass to prevent users pressing the back button when they logged out
    @Override
    public void onBackPressed(){
    }

    // When the Main Activity gets closed, stop the listener
    @Override
    protected void onStop() {
        super.onStop();

        // Reset all listeners as we don't need it anymore
        button.setOnClickListener(null);
        buttonContainer.setOnClickListener(null);
    }
}


