package com.example.chatfull;

import android.Manifest;
import android.app.DownloadManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.TypedArray;
import android.database.Cursor;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.provider.OpenableColumns;
import android.util.Base64;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;

import com.bumptech.glide.Glide;
import com.flask.colorpicker.ColorPickerView;
import com.flask.colorpicker.OnColorSelectedListener;
import com.flask.colorpicker.builder.ColorPickerClickListener;
import com.flask.colorpicker.builder.ColorPickerDialogBuilder;
import com.google.gson.Gson;
import com.stfalcon.chatkit.commons.ImageLoader;
import com.stfalcon.chatkit.messages.MessageHolders;
import com.stfalcon.chatkit.messages.MessagesList;
import com.stfalcon.chatkit.messages.MessagesListAdapter;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class ChatActivity extends AppCompatActivity
        implements MessageHolders.ContentChecker<Message>,
        MessagesListAdapter.OnMessageLongClickListener<Message>,
        MessagesListAdapter.OnLoadMoreListener{

    private static final int PICK_FILE_REQUEST = 1;
    private static final int PICK_IMAGE_REQUEST = 2;
    private static final byte CONTENT_TYPE_FILE = 1;
    private static final int REQUEST_WRITE_EXTERNAL_STORAGE = 200;
    private static String PREFERENCE_FILE_KEY;
    private final static String SHARED_PREFERENCES_KEY_MESSAGE_LIST = "User_Info_List";
    SharedPreferences sharedPref;
    SharedPreferences.Editor editor;
    Gson gson;

    private User user;
    private SendMessage sender;
    private MessageReceiveServer messageReceiveServer;

    MessagesList messagesList;
    protected final String senderId = "1";
    private static final int TOTAL_MESSAGES_COUNT = 20;
    private Date lastLoadedDate;

    MessagesListAdapter<Message> adapter;
    int cnt = 0; //Sets message counter id

    Button btnSend;
    ImageButton btnAttachment, btnImage;
    EditText input;

    RelativeLayout back_view;
    int[] colors;

    List<Message> messageArrayList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_alternate);
        isStoragePermissionGranted();

        user = (User) getIntent().getSerializableExtra("user");

        messageReceiveServer = new MessageReceiveServer(ShowInfoActivity.getSelfIpAddress(), ShowInfoActivity.getSelfPort(), this);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(user.getName());

        this.messagesList = findViewById(R.id.messagesList);

        ImageLoader imageLoader = new ImageLoader() {
            @Override
            public void loadImage(ImageView imageView, @Nullable String url, @Nullable Object payload) {
                byte[] byteArray = Base64.decode(url, Base64.DEFAULT);
                imageView.setAdjustViewBounds(true);
//                imageView.setMaxHeight(700);
//                imageView.setScaleType(ImageView.ScaleType.FIT_CENTER);
                Glide.with(getApplicationContext())
                        .asBitmap()
                        .load(byteArray)
                        .error(R.drawable.baseline_insert_drive_file_24)
                        .thumbnail(0.25f)
                        .sizeMultiplier(.60f)
                        .into(imageView);
            }
        };
        MessageHolders holders = new MessageHolders()
                .registerContentType(
                        CONTENT_TYPE_FILE,
                        IncomingFileMessageViewHolder.class,
                        R.layout.custom_incoming_file_layout,
                        OutcomingFileMessageViewHolder.class,
                        R.layout.custom_outcoming_file_layout,
                        this);
        adapter = new MessagesListAdapter<>(senderId, holders, imageLoader);
        messagesList.setAdapter(adapter);

        input = findViewById(R.id.et_message);
        btnSend = findViewById(R.id.bt_send);
        btnAttachment = findViewById(R.id.bt_attachment);
        btnImage = findViewById(R.id.bt_image);

        //Initialize color picker
        back_view = findViewById(R.id.background_view);

        TypedArray ta = getApplicationContext().getResources().obtainTypedArray(R.array.colors);
        colors = new int[ta.length()];
        for (int i = 0; i < ta.length(); i++) {
            colors[i] = ta.getColor(i, 0);
        }
        ta.recycle();

        adapter.setOnMessageLongClickListener(this);

        messageArrayList = new ArrayList<Message>();
        gson = new Gson();

        PREFERENCE_FILE_KEY = user.getId();
        sharedPref = this.getSharedPreferences(
                PREFERENCE_FILE_KEY, Context.MODE_PRIVATE);
        editor = sharedPref.edit();

        String jsonDataString = sharedPref.getString(SHARED_PREFERENCES_KEY_MESSAGE_LIST,"");
        if(jsonDataString.length() > 0) {
            Message messageArray[] = gson.fromJson(jsonDataString, Message[].class);
            for (Message msg : messageArray) {
                messageArrayList.add(msg);
            }
            adapter.addToEnd(messageArrayList,false);
            Log.e("MESSAGE_SIZE", messageArrayList.size() + "");
        }
    }

    public  boolean isStoragePermissionGranted() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (checkSelfPermission(android.Manifest.permission.WRITE_EXTERNAL_STORAGE)
                    == PackageManager.PERMISSION_GRANTED) {
                return true;
            } else {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, REQUEST_WRITE_EXTERNAL_STORAGE );
                return false;
            }
        }
        else { //permission is automatically granted on sdk<23 upon installation
            return true;
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if(grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
            //resume tasks needing this permission
        }
    }

    private void setClipboard(Context context, String text) {
        if (android.os.Build.VERSION.SDK_INT < android.os.Build.VERSION_CODES.HONEYCOMB) {
            android.text.ClipboardManager clipboard = (android.text.ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);
            clipboard.setText(text);
        } else {
            android.content.ClipboardManager clipboard = (android.content.ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);
            android.content.ClipData clip = android.content.ClipData.newPlainText("Copied Text", text);
            clipboard.setPrimaryClip(clip);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case R.id.select_all:
                Toast.makeText(getApplicationContext(), "Select All Clicked", Toast.LENGTH_SHORT).show();
                break;
            case R.id.background_button:
                ColorPickerDialogBuilder
                        .with(this)
                        .setTitle("Choose color")
                        .initialColor(Color.WHITE)
                        .wheelType(ColorPickerView.WHEEL_TYPE.FLOWER)
                        .density(12)
                        .setOnColorSelectedListener(new OnColorSelectedListener() {
                            @Override
                            public void onColorSelected(int selectedColor) {
                                Toast.makeText(getApplicationContext(), "onColorSelected: 0x" + Integer.toHexString(selectedColor), Toast.LENGTH_SHORT).show();
                            }
                        })
                        .setPositiveButton("ok", new ColorPickerClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int selectedColor, Integer[] allColors) {
                                Message message = new Message(Integer.toString(++cnt), MainActivity.me, null, Calendar.getInstance().getTime());
                                message.setColor(selectedColor);
                                message.setIsColor(true);

                                sender = new SendMessage(user.getIpAddress(), user.getPort(), message, ChatActivity.this);
                                sender.execute();
                                back_view.setBackgroundColor(selectedColor);
                            }
                        })
                        .setNegativeButton("cancel", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                            }
                        })
                        .build()
                        .show();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    public void onBtnSendClick(View view) {
        if (input.getText().toString() == null) return;

        Message message = new Message(Integer.toString(++cnt), MainActivity.me, input.getText().toString(), Calendar.getInstance().getTime());
        message.setIsImage(false);
        message.setFilename(null);
        adapter.addToStart(message, true);

        messageArrayList.add(message);

        sender = new SendMessage(user.getIpAddress(), user.getPort(), message, this);
        sender.execute();
        Log.e("SEND", input.getText().toString());
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
        if (requestCode == PICK_FILE_REQUEST && data != null) {
            if (resultCode == RESULT_OK) {
                Uri file = data.getData();
                Message message = new Message(Integer.toString(++cnt), MainActivity.me, null, Calendar.getInstance().getTime());
                message.setFilename(getFileName(file));
                try {
                    message.setFile(getBytes(this, file));
                } catch (IOException e) {
                    e.printStackTrace();
                    Log.e("SEND_IMAGE", "COULD NOT CONVERT TO BYTE");
                }
                message.setIsImage(false);
                message.setIsFile(true);

                adapter.addToStart(message, true);

                messageArrayList.add(message);

                sender = new SendMessage(user.getIpAddress(), user.getPort(), message, this);
                sender.execute();
            }
        } else if (requestCode == PICK_IMAGE_REQUEST && data != null) {
            if (resultCode == RESULT_OK) {
                Uri file = data.getData();
                Message message = new Message(Integer.toString(++cnt), MainActivity.me, null, Calendar.getInstance().getTime());
                message.setFilename(getFileName(file));
                try {
                    message.setFile(getBytes(this, file));
                } catch (IOException e) {
                    e.printStackTrace();
                    Log.e("SEND_IMAGE", "COULD NOT CONVERT TO BYTE");
                }
                message.setIsImage(true);
                message.setIsFile(false);

                adapter.addToStart(message, true);

                messageArrayList.add(message);

                sender = new SendMessage(user.getIpAddress(), user.getPort(), message, this);
                sender.execute();
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
     * @param uri     uri fo the file to read.
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
            try {
                byteBuffer.close();
            } catch (IOException ignored) { /* do nothing */ }
        }
        return bytesResult;
    }

    public void stopSender() {
        if (sender != null)
            sender.cancel(true);
    }

    public void setMessage(final Message msg) {
        Log.e("IN_SET", msg.toString());
        if (msg.isOffline()) {
            if (sender != null)
                sender.cancel(true);
            if (messageReceiveServer != null)
                messageReceiveServer.onDestroy();
            this.finish();
            return;
        }

        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (msg.getText() != null) {
                    msg.setUser(user);
                    adapter.addToStart(msg, true);
                } else if (msg.isImage()) {
                    msg.setUser(user);
                    adapter.addToStart(msg, true);
                } else if (msg.isFile()) {
                    msg.setUser(user);
                    adapter.addToStart(msg, true);
                } else if (msg.isColor()) {
                    back_view.setBackgroundColor(msg.getColor());
                }

                if(!msg.isColor())
                    messageArrayList.add(msg);
            }
        });
    }

    @Override
    protected void onDestroy() {
        Log.e("CHAT_ACTIVITY", "DESTROY");
        if (sender != null)
            sender.cancel(true);
        if (messageReceiveServer != null)
            messageReceiveServer.onDestroy();
        super.onDestroy();
    }

    @Override
    protected void onPause() {
        super.onPause();

        String jsonDataString = gson.toJson(messageArrayList);
        editor.putString(SHARED_PREFERENCES_KEY_MESSAGE_LIST, jsonDataString);
        editor.commit();
    }

    @Override
    public void onBackPressed() {
        Log.e("CHAT_ACTIVITY", "PAUSE");
        Message message = new Message(Integer.toString(++cnt), MainActivity.me, null);
        message.setOffline(true);
        sender = new SendMessage(user.getIpAddress(), user.getPort(), message, this);
        sender.execute();
        try {
            Thread.sleep(500);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        super.onBackPressed();
        if (sender != null && !sender.isCancelled())
            sender.cancel(true);
        if (messageReceiveServer != null)
            messageReceiveServer.onDestroy();

    }

    //Used for file messages
    @Override
    public boolean hasContentFor(Message message, byte type) {
        switch (type) {
            case CONTENT_TYPE_FILE:
                return message.getFile() != null
                        && message.getText() == null
                        && message.isFile();
        }
        return false;
    }

    @Override
    public void onMessageLongClick(Message message) {
        Log.e("CLICK", "MSG CLICK");
        if (message.getText() != null) {
            setClipboard(getApplicationContext(), message.getText());
            Toast.makeText(getApplicationContext(), "Text Copied", Toast.LENGTH_SHORT).show();
        } else if (message.isFile()) {
            try {
//                String uriString = new String(message.getFile(),"UTF-8");
//                Uri downloadUri = Uri.parse(uriString);

                File file = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS), message.getId() + message.getFilename());
                Log.e("FILE", file.getAbsolutePath());
                try {
                    file.createNewFile();
                    FileOutputStream fileOuputStream = new FileOutputStream(file);
                    fileOuputStream.write(message.getFile());
                    fileOuputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                String fileType = message.getFilename().substring(message.getFilename().indexOf('.') + 1);
                DownloadManager downloadManager = (DownloadManager) getApplicationContext().getSystemService(DOWNLOAD_SERVICE);
                downloadManager.addCompletedDownload(message.getId() + message.getFilename(), message.getId() + message.getFilename(), true, (fileType.equalsIgnoreCase("txt") ? "text/*" : "*/*"), file.getAbsolutePath(), file.length(), true);

            } catch (Exception e) {
                e.printStackTrace();
            }
        } else if (message.isImage()) {
            File file = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS), message.getId() + message.getFilename());
            try {
                file.createNewFile();
                FileOutputStream fileOuputStream = new FileOutputStream(file);
                fileOuputStream.write(message.getFile());
                fileOuputStream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            DownloadManager downloadManager = (DownloadManager) getApplicationContext().getSystemService(DOWNLOAD_SERVICE);
            downloadManager.addCompletedDownload(message.getId() + message.getFilename(), message.getId() + message.getFilename(), true, "image/*", file.getAbsolutePath(), file.length(), true);
        }
    }

    @Override
    public void onLoadMore(int page, int totalItemsCount) {
        Log.i("TAG", "onLoadMore: " + page + " " + totalItemsCount);
        if (totalItemsCount < TOTAL_MESSAGES_COUNT) {
            loadMessages();
        }
    }

    protected void loadMessages() {
        new Handler().postDelayed(new Runnable() { //imitation of internet connection
            @Override
            public void run() {
                Log.e("load","Ashche");
                ArrayList<Message> more_messages = new ArrayList<>();
                for(int i=0, j=0; i<messageArrayList.size() && j<10; i++){
                    if(messageArrayList.get(i).getCreatedAt().before(lastLoadedDate)){
                        more_messages.add(messageArrayList.get(i));
                        j++;
                    }
                }
                lastLoadedDate = more_messages.get(more_messages.size() - 1).getCreatedAt();
                adapter.addToEnd(more_messages, false);
            }
        }, 500);
    }
}
