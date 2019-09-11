package com.example.chatfull;

import android.os.AsyncTask;
import android.util.Log;
import java.io.PrintWriter;
import java.net.Socket;

public class SendMessage extends AsyncTask<Void, Void, String> {

    ChatActivity activity;
    String dstAddress, message;
    int dstPort;
    Socket clientSocket = null;

    SendMessage(String addr, int port, String message, ChatActivity activity) {
        this.dstAddress = addr;
        this.dstPort = port;
        this.message = message;
        this.activity = activity;
    }

    @Override
    protected String doInBackground(Void... arg0) {
        try {
            clientSocket = new Socket(dstAddress, dstPort);
            clientSocket.setKeepAlive(true);
            Log.e("SendMSG","Connect Hoise"+message);
            if(clientSocket != null) {
                PrintWriter out = new PrintWriter(clientSocket.getOutputStream(),true);
                out.println(message);
                Log.e("SendMSG","Pathaise"+message);
                activity.stopSender();
            }
        } catch (Exception e) {
            Log.e("SendMSG","ConnectHoyNai"+message);
            e.printStackTrace();
        }
        return null;
    }

}
