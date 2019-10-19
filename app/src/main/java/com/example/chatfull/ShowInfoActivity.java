package com.example.chatfull;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import net.glxn.qrgen.android.QRCode;

import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Enumeration;

public class ShowInfoActivity extends AppCompatActivity {

    private static final int selfPort = 8080;
    private Server myServer;

    public void setConnected(User user) {
        Intent data = new Intent();
        data.putExtra("user",user);
        setResult(RESULT_OK,data);
        finish();
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_info);

        TextView ipView = findViewById(R.id.ipDisplay);
        TextView portView = findViewById(R.id.portDisplay);

        String ip_address = getSelfIpAddress();
        ipView.setText(ip_address);
        portView.setText(Integer.toString(selfPort));

        Bitmap myBitmap = QRCode.from(ip_address+":"+selfPort).bitmap();
        ImageView myImage = (ImageView) findViewById(R.id.qr_view);
        myImage.setImageBitmap(myBitmap);
    }

    @Override
    protected void onResume() {
        super.onResume();
        myServer = new Server(this, getSelfIpAddress(), getSelfPort());
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (myServer != null)
            myServer.onDestroy();
    }

    public static int getSelfPort() {
        return selfPort;
    }

    // Returns device IP Address
    public static String getSelfIpAddress() {
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
            e.printStackTrace();
            Log.e("GET_IP", "IP NOT FOUND");
        }
        return self_ip;
    }
}
