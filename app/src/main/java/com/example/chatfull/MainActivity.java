package com.example.chatfull;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.snackbar.Snackbar;
import com.google.gson.Gson;

public class MainActivity extends AppCompatActivity {

    Button showInfoBtn, enterInfoBtn, continueBtn;
    EditText nameInput;
    User me; // Assign Self Username

    private final static String SHARED_PREFERENCES_KEY_USER_SELF = "ME";
    private static String PREFERENCE_FILE_KEY = "SELF_INFO";
    SharedPreferences sharedPref;
    SharedPreferences.Editor editor;
    Gson gson;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

//        showInfoBtn = findViewById(R.id.showInfo);
//        enterInfoBtn = findViewById(R.id.enterInfo);
        nameInput = findViewById(R.id.nameInput);
        continueBtn = findViewById(R.id.continue_btn);

        gson = new Gson();
        sharedPref = this.getSharedPreferences(
                PREFERENCE_FILE_KEY, Context.MODE_PRIVATE);
        editor = sharedPref.edit();
        String jsonDataString = sharedPref.getString(SHARED_PREFERENCES_KEY_USER_SELF, "");
        me = gson.fromJson(jsonDataString, User.class);

        continueBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(nameInput.getText().toString().length() < 1){
                    Snackbar snackbar = Snackbar
                            .make(nameInput, "Please Enter Username", Snackbar.LENGTH_LONG);
                    snackbar.show();
                    return;
                }
                me = new User("1", nameInput.getText().toString());
                String jsonDataString = gson.toJson(me);
                editor.putString(SHARED_PREFERENCES_KEY_USER_SELF, jsonDataString);
                editor.commit();

                Intent intent = new Intent(getApplicationContext(), DialogViewActivity.class);
                startActivity(intent);
                finish();
            }
        });

//        showInfoBtn.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                if(nameInput.getText().toString().length() < 1){
//                    Snackbar snackbar = Snackbar
//                            .make(nameInput, "Please Enter Username", Snackbar.LENGTH_LONG);
//                    snackbar.show();
//                    return;
//                }
//                me = new User("1", nameInput.getText().toString());
//                Intent intent = new Intent(getApplicationContext(), ShowInfoActivity.class);
//                startActivity(intent);
//            }
//        });
//
//        enterInfoBtn.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                if(nameInput.getText().toString().length() < 1){
//                    Snackbar snackbar = Snackbar
//                            .make(nameInput, "Please Enter Username", Snackbar.LENGTH_LONG);
//                    snackbar.show();
//                    return;
//                }
//                me = new User("1", nameInput.getText().toString());
//                Intent intent = new Intent(getApplicationContext(), ConnectToUserActivity.class);
//                startActivity(intent);
//            }
//        });
    }
}
