package com.example.chatfull;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.google.android.gms.vision.barcode.Barcode;
import com.notbytes.barcode_reader.BarcodeReaderActivity;

public class ConnectToUserActivity extends AppCompatActivity {

    private static final int BARCODE_READER_ACTIVITY_REQUEST = 1208;

    private EditText ipInput, portInput;
    private Button connectBtn, scanBtn;
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
        scanBtn = findViewById(R.id.scan_button);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (myClient != null && !myClient.isCancelled())
            myClient.cancel(true);
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (myClient != null && !myClient.isCancelled())
            myClient.cancel(true);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (myClient != null && !myClient.isCancelled())
            myClient.cancel(true);
    }

    public void connectBtnListener(View view) {
        myClient = new Client(ipInput.getText().toString(), Integer.parseInt(portInput.getText().toString()), this);
        myClient.execute();
    }

    public void onScanBtnClick(View view) {
        FragmentManager supportFragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = supportFragmentManager.beginTransaction();
        Fragment fragmentById = supportFragmentManager.findFragmentById(R.id.fm_container);
        if (fragmentById != null) {
            fragmentTransaction.remove(fragmentById);
        }
        fragmentTransaction.commitAllowingStateLoss();
        Intent launchIntent = BarcodeReaderActivity.getLaunchIntent(this, true, false);
        startActivityForResult(launchIntent, BARCODE_READER_ACTIVITY_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode != Activity.RESULT_OK) {
            Toast.makeText(this, "error in  scanning", Toast.LENGTH_SHORT).show();
            return;
        }

        if (requestCode == BARCODE_READER_ACTIVITY_REQUEST && data != null) {
            Barcode barcode = data.getParcelableExtra(BarcodeReaderActivity.KEY_CAPTURED_BARCODE);
            Toast.makeText(this, barcode.rawValue, Toast.LENGTH_SHORT).show();
        }

    }
}
