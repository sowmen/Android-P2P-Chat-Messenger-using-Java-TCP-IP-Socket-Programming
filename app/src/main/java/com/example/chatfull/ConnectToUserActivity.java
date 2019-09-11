package com.example.chatfull;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

public class ConnectToUserActivity extends AppCompatActivity {

    EditText ipInput, portInput;
    Button connectBtn;
    Client myClient;
    User user;
    boolean paused = false;

    public void setUser(User user) {
        this.user = user;
        Log.e("BeforeChat", myClient.user.toString());
        Intent intent = new Intent(getApplicationContext(), ChatActivity.class);
        intent.putExtra("user", myClient.user);
        startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_connect_to_user);

        ipInput = findViewById(R.id.ipInput);
        portInput = findViewById(R.id.portInput);
        connectBtn = findViewById(R.id.connectBtn);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if(paused) {
            myClient.cancel(true);
            recreate();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        paused = true;
    }

    public void connectBtnListener(View view){
        myClient = new Client(ipInput.getText().toString(),Integer.parseInt(portInput.getText().toString()),this);
        myClient.execute();
    }
}
