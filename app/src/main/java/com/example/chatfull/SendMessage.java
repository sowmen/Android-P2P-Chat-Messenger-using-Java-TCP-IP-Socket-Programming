package com.example.chatfull;

import android.os.AsyncTask;
import android.util.Log;
import java.io.PrintWriter;
import java.net.Socket;

public class SendMessage extends AsyncTask<Void, Void, String> {

    ChatActivity activity;
    private String dstAddress, message;
    private int dstPort;
    private Socket clientSocket = null;

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
            Log.e("SEND_MSG","Connected, Sending: " + message);

            if(clientSocket != null) {
                PrintWriter out = new PrintWriter(clientSocket.getOutputStream(),true);
                out.println(message);
                Log.e("SEND_MSG","DONE: " + message);
                activity.stopSender();
            }
        } catch (Exception e) {
            Log.e("SEND_MSG","ConnectHoyNai "+ message);
            e.printStackTrace();
        }
        return null;
    }
}
