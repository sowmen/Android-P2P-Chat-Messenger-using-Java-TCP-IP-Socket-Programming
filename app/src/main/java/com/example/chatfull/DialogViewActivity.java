package com.example.chatfull;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.github.clans.fab.FloatingActionButton;
import com.github.clans.fab.FloatingActionMenu;
import com.google.gson.Gson;
import com.squareup.picasso.Picasso;
import com.stfalcon.chatkit.commons.ImageLoader;
import com.stfalcon.chatkit.dialogs.DialogsList;
import com.stfalcon.chatkit.dialogs.DialogsListAdapter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class DialogViewActivity extends AppCompatActivity
        implements DialogsListAdapter.OnDialogClickListener<Dialog> {

    private static final int SHOW_INFO = 100;
    private static final int ENTER_INFO = 200;
    private static final int CHAT_ACTIVITY = 300;
    protected ImageLoader imageLoader;
    static DialogsListAdapter<Dialog> dialogsAdapter;
    DialogsList dialogsList;
    FloatingActionButton fab1, fab2, fab3;
    boolean isFABOpen;

    static User me, user;
    private final static String SHARED_PREFERENCES_KEY_USER_SELF = "ME";
    private final static String SHARED_PREFERENCES_KEY_DIALOG = "DIALOG_INFO";
    private static String PREFERENCE_FILE_KEY_SELF = "SELF_INFO";
    private static String PREFERENCE_FILE_KEY_DIALOGS = "DIALOG_LIST";
    SharedPreferences sharedPrefSelf, sharedPrefDialog;
    SharedPreferences.Editor editorUser, editorDialog;
    Gson gson;


    private FloatingActionMenu fam;
    private FloatingActionButton fabShowInfo, fabEnterInfo;

    static List<Dialog> dialogArrayList;
    boolean loaded = false, saved = false;
    TextView overlay;

    String image;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dialog_view);

        image = "https://cdn1.imggmi.com/uploads/2019/10/19/5bf1857add4ee9b72b31257e2adb9030-full.png";

        imageLoader = new ImageLoader() {
            @Override
            public void loadImage(ImageView imageView, String url, Object payload) {
                Picasso.get().load(url).into(imageView);
//                Glide.with(getApplicationContext()).asBitmap().load(R.drawable.dialog).into(imageView);
//                Glide.with(getApplicationContext())
//                        .load(Uri.parse("android.resource://"+R.class.getPackage().getName()+"/" +R.drawable.dialog).toString())
//                        .into(imageView);
            }
        };

        dialogsList = (DialogsList) findViewById(R.id.dialogsList);
        dialogsAdapter = new DialogsListAdapter<>(imageLoader);
        dialogsAdapter.setOnDialogClickListener(this);
        dialogsList.setAdapter(dialogsAdapter);

        gson = new Gson();
        sharedPrefSelf = this.getSharedPreferences(PREFERENCE_FILE_KEY_SELF, Context.MODE_PRIVATE);
        editorUser = sharedPrefSelf.edit();
        String jsonDataStringSelfUser = sharedPrefSelf.getString(SHARED_PREFERENCES_KEY_USER_SELF, "");
        me = gson.fromJson(jsonDataStringSelfUser, User.class);


        overlay = findViewById(R.id.overlay);
        if (dialogsAdapter.isEmpty())
            overlay.setVisibility(View.VISIBLE);

        fabShowInfo = findViewById(R.id.showInfoFab);
        fabEnterInfo = findViewById(R.id.enterInfoFab);
        fam = findViewById(R.id.fab_menu);
        fabShowInfo.setOnClickListener(onButtonClick());
        fabEnterInfo.setOnClickListener(onButtonClick());

        loaded = false;
        saved = false;
        dialogArrayList = new ArrayList<>();

    }


    @Override
    protected void onResume() {
        super.onResume();

        if (loaded == false) {
            sharedPrefDialog = this.getSharedPreferences(PREFERENCE_FILE_KEY_DIALOGS, MODE_PRIVATE);
            editorDialog = sharedPrefDialog.edit();
            String jsonDataStringDialogArray = sharedPrefDialog.getString(SHARED_PREFERENCES_KEY_DIALOG, "");
            Log.e("DialogArrar", jsonDataStringDialogArray);
            if ((jsonDataStringDialogArray != null || jsonDataStringDialogArray != "null") && jsonDataStringDialogArray.length() > 2) {
                Dialog dialodArray[] = gson.fromJson(jsonDataStringDialogArray, Dialog[].class);
                if (dialodArray != null) {
                    for (Dialog d : dialodArray) {
                        dialogArrayList.add(d);
                    }
                    dialogsAdapter.addItems(dialogArrayList);
                    overlay.setVisibility(View.INVISIBLE);
                }
            }
            loaded = true;
        }
    }

    @Override

    protected void onPause() {
        if (saved == false) {
            Log.e("PAUSE", "SAVE");
            String jsonDataString = gson.toJson(dialogArrayList);
            Log.e("PAUSE", jsonDataString);
            editorDialog.putString(SHARED_PREFERENCES_KEY_DIALOG, jsonDataString);
            editorDialog.commit();
            saved = true;
        }
        super.onPause();
    }

    @Override
    public void onBackPressed() {
        if (saved == false) {
            Log.e("BACK", "SAVE");
            String jsonDataString = gson.toJson(dialogArrayList);
            Log.e("PAUSE", jsonDataString);
            editorDialog.putString(SHARED_PREFERENCES_KEY_DIALOG, jsonDataString);
            editorDialog.commit();
            saved = true;
        }
        loaded = false;
        super.onBackPressed();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (saved == false) {
            Log.e("DESTROY", "SAVE");
            String jsonDataString = gson.toJson(dialogArrayList);
            editorDialog.putString(SHARED_PREFERENCES_KEY_DIALOG, jsonDataString);
            editorDialog.commit();
            saved = true;
        }
    }

    private View.OnClickListener onButtonClick() {
        return new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (view == fabShowInfo) {
                    Intent intent = new Intent(getApplicationContext(), ShowInfoActivity.class);
                    startActivityForResult(intent, SHOW_INFO);
                } else if (view == fabEnterInfo) {
                    Intent intent = new Intent(getApplicationContext(), ConnectToUserActivity.class);
                    startActivityForResult(intent, ENTER_INFO);
                }
                fam.close(true);
            }
        };
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if ((requestCode == SHOW_INFO || requestCode == ENTER_INFO) && data != null) {
            if (resultCode == RESULT_OK) {
                user = (User) data.getSerializableExtra("user");
                Dialog dialog = dialogsAdapter.getItemById(user.getName());
                if (dialog == null) {
                    dialog = new Dialog(user.getName(), user.getName(), image, new ArrayList<User>(Arrays.asList(user)), null, 0);
                    dialogsAdapter.addItem(0, dialog);

//                    dialogArrayList = new ArrayList<>();
                    dialogArrayList.add(dialog);

                    String jsonDataStringDialog = gson.toJson(dialogArrayList);
                    editorDialog.putString(SHARED_PREFERENCES_KEY_DIALOG, jsonDataStringDialog);
                    editorDialog.commit();

                    overlay.setVisibility(View.INVISIBLE);
                }
                onDialogClick(dialog);
            }
        }
    }

    @Override
    public void onDialogClick(Dialog dialog) {
        Intent intent = new Intent(getApplicationContext(), ChatActivity.class);
        intent.putExtra("user", dialog.getUsers().get(0));
        intent.putExtra("dialog", dialog);
        startActivity(intent);
    }
}
