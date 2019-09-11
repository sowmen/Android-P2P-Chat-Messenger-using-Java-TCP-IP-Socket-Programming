package com.example.chatfull;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Enumeration;

public class ShowInfoActivity extends AppCompatActivity {

    static final int selfPort = 8080;
    Server myServer;
    boolean paused = false;

    public void setConnected(boolean connected) {
        this.connected = connected;
        Intent intent = new Intent(getApplicationContext(), ChatActivity.class);
        intent.putExtra("user",myServer.user);
        startActivity(intent);
    }

    public boolean connected = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_info);

        TextView ipView = findViewById(R.id.ipDisplay);
        TextView portView = findViewById(R.id.portDisplay);

        String ipport = getSelfIpAddress();
        ipView.setText(ipport);

        portView.setText(""+ selfPort);

        myServer = new Server(this, getSelfIpAddress(), getSelfPort());
        Log.e("ShowActivity","CreatedServer");
    }

    @Override
    protected void onResume() {
        super.onResume();
        if(paused) {
            myServer.onDestroy();
            recreate();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        paused = true;
    }

    public static int getSelfPort() {
        return selfPort;
    }

    static public String getSelfIpAddress() {
        String self_ip = "";
        try {
            Enumeration<NetworkInterface> enumNetworkInterfaces = NetworkInterface
                    .getNetworkInterfaces();
            while (enumNetworkInterfaces.hasMoreElements()) {
                NetworkInterface networkInterface = enumNetworkInterfaces
                        .nextElement();
                Enumeration<InetAddress> enumInetAddress = networkInterface
                        .getInetAddresses();
                while (enumInetAddress.hasMoreElements()) {
                    InetAddress inetAddress = enumInetAddress
                            .nextElement();

                    if (inetAddress.isSiteLocalAddress()) {
                        self_ip = inetAddress.getHostAddress();
                    }
                }
            }

        } catch (SocketException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            Log.e("in_get_ip","IP NOT FOUND");
        }
        return self_ip;
    }
}
