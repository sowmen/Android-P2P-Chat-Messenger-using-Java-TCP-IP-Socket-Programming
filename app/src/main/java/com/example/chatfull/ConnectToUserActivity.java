package com.example.chatfull;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;

public class ConnectToUserActivity extends AppCompatActivity {

    private EditText ipInput, portInput;
    private Button connectBtn;
    private Client myClient;
    private User user;

    public void setUser(User user) {
        this.user = user;
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
        if(myClient != null && !myClient.isCancelled())
            myClient.cancel(true);
    }

    @Override
    protected void onPause() {
        super.onPause();
        if(myClient != null && !myClient.isCancelled())
            myClient.cancel(true);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(myClient != null && !myClient.isCancelled())
            myClient.cancel(true);
    }

    public void connectBtnListener(View view){
        myClient = new Client(ipInput.getText().toString(),Integer.parseInt(portInput.getText().toString()),this);
        myClient.execute();
    }
}
