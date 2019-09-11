package com.example.chatfull;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ViewAnimator;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.ArrayList;

public class ChatActivity extends AppCompatActivity {

    public static TextView messageDisplay;
    public static String receivedMsg = null, clientMsg = null;
    EditText msgInput;
    User user;
    ArrayList<String> received_message = new ArrayList<>();
    SendMessage sender;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);


        user = (User) getIntent().getSerializableExtra("user");

        messageDisplay = findViewById(R.id.messageDisplay);
        msgInput = findViewById(R.id.msgInput);

        messageDisplay.setText("Connected to: " + user.getIpAddress() + ":" + user.getPort() + "\n");
        messageDisplay.append("Self: " + ShowInfoActivity.getSelfIpAddress() + ":" + ShowInfoActivity.getSelfPort());

        new MessageReceiveServer(ShowInfoActivity.getSelfIpAddress(),ShowInfoActivity.getSelfPort(),this);
    }

    public void OnMsgSendBtnClick(View view){
        clientMsg = msgInput.getText().toString();
        msgInput.setText("");
        messageDisplay.append("\nSent===>" + clientMsg);

        SendMessage sender = new SendMessage(user.getIpAddress(), user.getPort(),clientMsg,this);
        sender.execute();
    }
    public void stopSender(){
        sender.cancel(true);
    }
    public void setMessage(final String msg){
        Log.e("INSET",msg);
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                messageDisplay.append("\nReceived===>" + msg);
            }
        });
    }
}
