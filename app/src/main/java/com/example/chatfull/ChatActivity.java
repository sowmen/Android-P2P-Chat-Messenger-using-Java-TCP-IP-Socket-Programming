package com.example.chatfull;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.stfalcon.chatkit.messages.MessagesList;
import com.stfalcon.chatkit.messages.MessagesListAdapter;

import java.util.Calendar;

public class ChatActivity extends AppCompatActivity {

    private static final int PICK_FILE_REQUEST = 1;
    private static final int PICK_IMAGE_REQUEST = 2;

    private User user;
    private SendMessage sender;
    private MessageReceiveServer messageReceiveServer;

    MessagesList messagesList;
//    MessageInput input;
    protected final String senderId = "1";
    private final User me = new User("1","SELF"); // Assign Self Username
    MessagesListAdapter<Message> adapter;
    int cnt = 0;

    Button btnSend;
    ImageButton btnAttachement, btnImage;
    EditText input;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_alternate);

        user = (User) getIntent().getSerializableExtra("user");
        user.setId("0");
        user.setName("ABAL"); // Assign user username

        messageReceiveServer = new MessageReceiveServer(ShowInfoActivity.getSelfIpAddress(), ShowInfoActivity.getSelfPort(),this);

        this.messagesList = findViewById(R.id.messagesList);
        adapter = new MessagesListAdapter<>(senderId, null);
        messagesList.setAdapter(adapter);

        input = findViewById(R.id.et_message);
        btnSend = findViewById(R.id.bt_send);
        btnAttachement = findViewById(R.id.bt_attachment);
        btnImage = findViewById(R.id.bt_image);
    }

    public void onBtnSendClick(View view) {
        Message message = new Message(Integer.toString(++cnt), me, input.getText().toString(), Calendar.getInstance().getTime());
        adapter.addToStart(message, true);

        sender = new SendMessage(user.getIpAddress(), user.getPort(), input.getText().toString(),this);
        sender.execute();
        Log.e("SEND",input.getText().toString());
        input.setText("");
    }

    public void onBtnAttachmentClick(View view) {
        Intent intent = new Intent();
        intent.setType("*/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        // Always show the chooser (if there are multiple options available)
        startActivityForResult(Intent.createChooser(intent, "Select File"), PICK_FILE_REQUEST);
    }

    public void onBtnImageClick(View view) {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        // Always show the chooser (if there are multiple options available)
        startActivityForResult(Intent.createChooser(intent, "Select Image"), PICK_IMAGE_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_FILE_REQUEST && data!=null) {
            if (resultCode == RESULT_OK) {
                Uri file = data.getData();
                Toast.makeText(this, file.getPath() + "FILE", Toast.LENGTH_SHORT).show();
            }
        }else if (requestCode == PICK_IMAGE_REQUEST && data!=null) {
            if (resultCode == RESULT_OK) {
                Uri file = data.getData();
                Toast.makeText(this, file.getPath() + "IMAGE", Toast.LENGTH_SHORT).show();
            }
        }
    }

//    @Override
//    public boolean onSubmit(CharSequence input) {
//        Message message = new Message(Integer.toString(++cnt), me, input.toString(), Calendar.getInstance().getTime());
//        adapter.addToStart(message, true);
//
//        sender = new SendMessage(user.getIpAddress(), user.getPort(), input.toString(),this);
//        sender.execute();
//        return true;
//    }

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
    public void onBackPressed() {
        Log.e("CHAT_ACTIVITY", "PAUSE");
        sender = new SendMessage(user.getIpAddress(), user.getPort(), "OFFLINE",this);
        sender.execute();
        try {
            Thread.sleep(500);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        super.onBackPressed();
        if(sender != null && !sender.isCancelled())
            sender.cancel(true);
        if(messageReceiveServer != null)
            messageReceiveServer.onDestroy();

    }
}
