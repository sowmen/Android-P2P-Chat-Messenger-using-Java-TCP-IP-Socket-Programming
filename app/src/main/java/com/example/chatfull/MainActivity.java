package com.example.chatfull;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.snackbar.Snackbar;

public class MainActivity extends AppCompatActivity {

    Button showInfoBtn, enterInfoBtn;
    EditText nameInput;
    static User me; // Assign Self Username
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        showInfoBtn = findViewById(R.id.showInfo);
        enterInfoBtn = findViewById(R.id.enterInfo);
        nameInput = findViewById(R.id.nameInput);


        showInfoBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(nameInput.getText().toString().length() < 1){
                    Snackbar snackbar = Snackbar
                            .make(nameInput, "Please Enter Username", Snackbar.LENGTH_LONG);
                    snackbar.show();
                    return;
                }
                me = new User("1", nameInput.getText().toString());
                Intent intent = new Intent(getApplicationContext(), ShowInfoActivity.class);
                startActivity(intent);
            }
        });

        enterInfoBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(nameInput.getText().toString().length() < 1){
                    Snackbar snackbar = Snackbar
                            .make(nameInput, "Please Enter Username", Snackbar.LENGTH_LONG);
                    snackbar.show();
                    return;
                }
                me = new User("1", nameInput.getText().toString());
                Intent intent = new Intent(getApplicationContext(), ConnectToUserActivity.class);
                startActivity(intent);
            }
        });
    }
}
