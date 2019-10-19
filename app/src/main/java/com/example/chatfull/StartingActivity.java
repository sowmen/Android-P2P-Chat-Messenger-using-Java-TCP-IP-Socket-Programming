package com.example.chatfull;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;

import com.google.gson.Gson;

public class StartingActivity extends AppCompatActivity {

    private final static String SHARED_PREFERENCES_KEY_USER_SELF = "ME";
    private static String PREFERENCE_FILE_KEY = "SELF_INFO";
    SharedPreferences sharedPref;
    SharedPreferences.Editor editor;
    Gson gson;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_starting);

        Intent intent;

        gson = new Gson();
        sharedPref = this.getSharedPreferences(
                PREFERENCE_FILE_KEY, Context.MODE_PRIVATE);
        editor = sharedPref.edit();

        String jsonDataString = sharedPref.getString(SHARED_PREFERENCES_KEY_USER_SELF, "");
        Log.e("START",jsonDataString);
        if (jsonDataString.length() > 0) {
            intent = new Intent(this, DialogViewActivity.class);
        }
        else {
            intent = new Intent(this, MainActivity.class);
        }

        startActivity(intent);
        finish();
    }
}
