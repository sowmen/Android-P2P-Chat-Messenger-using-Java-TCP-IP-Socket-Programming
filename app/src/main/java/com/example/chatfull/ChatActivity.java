package com.example.chatfull;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.stfalcon.chatkit.messages.MessageInput;
import com.stfalcon.chatkit.messages.MessagesList;
import com.stfalcon.chatkit.messages.MessagesListAdapter;

import java.util.Calendar;

public class ChatActivity extends AppCompatActivity implements MessageInput.InputListener {

    private User user;
    private SendMessage sender;
    private MessageReceiveServer messageReceiveServer;

    MessagesList messagesList;
    MessageInput input;
    protected final String senderId = "1";
    private final User me = new User("1","Sowmen");
    MessagesListAdapter<Message> adapter;
    int cnt = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_message_list);

        user = (User) getIntent().getSerializableExtra("user");
        user.setId("0");
        user.setName("ABAL");

        messageReceiveServer = new MessageReceiveServer(ShowInfoActivity.getSelfIpAddress(), ShowInfoActivity.getSelfPort(),this);

        this.messagesList = findViewById(R.id.messagesList);
        adapter = new MessagesListAdapter<>(senderId, null);
        messagesList.setAdapter(adapter);

        input = findViewById(R.id.input);
        input.setInputListener(this);
    }


    @Override
    public boolean onSubmit(CharSequence input) {
        Message message = new Message(Integer.toString(++cnt), me, input.toString(), Calendar.getInstance().getTime());
        adapter.addToStart(message, true);

        sender = new SendMessage(user.getIpAddress(), user.getPort(), input.toString(),this);
        sender.execute();
        return true;
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
                Message message = new Message(Integer.toString(++cnt), user, msg, Calendar.getInstance().getTime());
                adapter.addToStart(message, true);
            }
        });
    }

    @Override
    protected void onDestroy() {
        Log.e("CHAT_ACTIVITY","DESTROY");
        if(sender != null)
            sender.cancel(true);
        if(messageReceiveServer != null)
            messageReceiveServer.onDestroy();
        super.onDestroy();
    }

    @Override
    protected void onPause() {
        Log.e("CHAT_ACTIVITY", "PAUSE");
        sender = new SendMessage(user.getIpAddress(), user.getPort(), "OFFLINE",this);
        sender.execute();
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        super.onPause();
        if(sender != null && !sender.isCancelled())
            sender.cancel(true);
        if(messageReceiveServer != null)
            messageReceiveServer.onDestroy();

    }
}
