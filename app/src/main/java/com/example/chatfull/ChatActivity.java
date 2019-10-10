package com.example.chatfull;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.OpenableColumns;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.squareup.picasso.Picasso;
import com.stfalcon.chatkit.commons.ImageLoader;
import com.stfalcon.chatkit.messages.MessagesList;
import com.stfalcon.chatkit.messages.MessagesListAdapter;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
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

        ImageLoader imageLoader = new ImageLoader() {
            @Override
            public void loadImage(ImageView imageView, @Nullable String url, @Nullable Object payload) {
                byte[] byteArray = Base64.decode(url,Base64.DEFAULT);
                Glide.with(getApplicationContext())
                        .asBitmap()
                        .load(byteArray)
                        .into(imageView);
            }
        };
        adapter = new MessagesListAdapter<>(senderId, imageLoader);
        messagesList.setAdapter(adapter);

        input = findViewById(R.id.et_message);
        btnSend = findViewById(R.id.bt_send);
        btnAttachement = findViewById(R.id.bt_attachment);
        btnImage = findViewById(R.id.bt_image);
    }

    public void onBtnSendClick(View view) {
        if(input.getText().toString() == null) return;

        Message message = new Message(Integer.toString(++cnt), me, input.getText().toString(), Calendar.getInstance().getTime());
        message.setIsImage(false);
        message.setFilename(null);
        adapter.addToStart(message, true);

        sender = new SendMessage(user.getIpAddress(), user.getPort(), message,this);
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
                Message message = new Message(Integer.toString(++cnt), me, null, Calendar.getInstance().getTime());
                message.setFilename(getFileName(file));
                try {
                    message.setFile(getBytes(this,file));
                } catch (IOException e) {
                    e.printStackTrace();
                    Log.e("SEND_FILE","COULD NOT CONVERT TO BYTE");
                }
                message.setIsImage(true);

                adapter.addToStart(message,true);
                sender = new SendMessage(user.getIpAddress(), user.getPort(), message,this);
                sender.execute();
//                Toast.makeText(this, file.getPath() + "IMAGE", Toast.LENGTH_SHORT).show();
            }
        }
    }

    public String getFileName(Uri uri) {
        String result = null;
        if (uri.getScheme().equals("content")) {
            Cursor cursor = getContentResolver().query(uri, null, null, null, null);
            try {
                if (cursor != null && cursor.moveToFirst()) {
                    result = cursor.getString(cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME));
                }
            } finally {
                cursor.close();
            }
        }
        if (result == null) {
            result = uri.getPath();
            int cut = result.lastIndexOf('/');
            if (cut != -1) {
                result = result.substring(cut + 1);
            }
        }
        return result;
    }

    /**
     * get bytes array from Uri.
     *
     * @param context current context.
     * @param uri uri fo the file to read.
     * @return a bytes array.
     * @throws IOException
     */
    public static byte[] getBytes(Context context, Uri uri) throws IOException {
        InputStream iStream = context.getContentResolver().openInputStream(uri);
        try {
            return getBytes(iStream);
        } finally {
            // close the stream
            try {
                iStream.close();
            } catch (IOException ignored) { /* do nothing */ }
        }
    }



    /**
     * get bytes from input stream.
     *
     * @param inputStream inputStream.
     * @return byte array read from the inputStream.
     * @throws IOException
     */
    public static byte[] getBytes(InputStream inputStream) throws IOException {

        byte[] bytesResult = null;
        ByteArrayOutputStream byteBuffer = new ByteArrayOutputStream();
        int bufferSize = 1024;
        byte[] buffer = new byte[bufferSize];
        try {
            int len;
            while ((len = inputStream.read(buffer)) != -1) {
                byteBuffer.write(buffer, 0, len);
            }
            bytesResult = byteBuffer.toByteArray();
        } finally {
            // close the stream
            try{ byteBuffer.close(); } catch (IOException ignored){ /* do nothing */ }
        }
        return bytesResult;
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

    public void setMessage(final Message msg){
        Log.e("IN_SET",msg.toString());
        if(msg.getText()!= null && msg.getText().equalsIgnoreCase("OFFLINE")){
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
                if(msg.getText() != null) {
                    Message message = new Message(Integer.toString(++cnt), user, msg.getText(), Calendar.getInstance().getTime());
                    adapter.addToStart(message, true);
                } else if(msg.isImage()){
                    msg.setUser(user);
                    adapter.addToStart(msg,true);
                }
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
        sender = new SendMessage(user.getIpAddress(), user.getPort(), new Message(Integer.toString(++cnt), me, "OFFLINE"),this);
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
