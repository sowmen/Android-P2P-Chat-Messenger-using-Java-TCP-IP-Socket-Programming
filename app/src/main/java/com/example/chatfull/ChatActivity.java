package com.example.chatfull;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class ChatActivity extends AppCompatActivity {

    private TextView messageDisplay;
    private String clientMsg = null;
    private EditText msgInput;
    private User user;
    private SendMessage sender;
    private MessageReceiveServer messageReceiveServer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        user = (User) getIntent().getSerializableExtra("user");

        messageDisplay = findViewById(R.id.messageDisplay);
        msgInput = findViewById(R.id.msgInput);

        messageDisplay.setText("Connected to: " + user.getIpAddress() + ":" + user.getPort() + "\n");
        messageDisplay.append("Self: " + ShowInfoActivity.getSelfIpAddress() + ":" + ShowInfoActivity.getSelfPort());

        messageReceiveServer = new MessageReceiveServer(ShowInfoActivity.getSelfIpAddress(), ShowInfoActivity.getSelfPort(),this);
    }

    public void OnMsgSendBtnClick(View view){
        clientMsg = msgInput.getText().toString();
        msgInput.setText("");
        messageDisplay.append("\nSent===>" + clientMsg);

        sender = new SendMessage(user.getIpAddress(), user.getPort(),clientMsg,this);
        sender.execute();
    }
    public void stopSender(){
        if(sender != null)
            sender.cancel(true);
    }

    public void setMessage(final String msg){
        Log.e("IN_SET",msg);
        if(msg.equalsIgnoreCase("OFFLINE")){
            if(sender != null)
                sender.cancel(true);
            if(messageReceiveServer != null)
                messageReceiveServer.onDestroy();
            this.finish();
            return;
        }
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                messageDisplay.append("\nReceived===>" + msg);
            }
        });
    }

    @Override
    protected void onDestroy() {
        Log.e("CHAT_ACTIVITY","DESTROY");
        sender = new SendMessage(user.getIpAddress(), user.getPort(), "OFFLINE",this);
        sender.execute();
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        if(sender != null)
            sender.cancel(true);
        if(messageReceiveServer != null)
            messageReceiveServer.onDestroy();
        super.onDestroy();
    }

    @Override
    protected void onPause() {
        Log.e("CHAT_ACTIVITY", "PAUSE");
        super.onPause();
        if(sender != null && !sender.isCancelled())
            sender.cancel(true);
        if(messageReceiveServer != null)
            messageReceiveServer.onDestroy();

    }
}
