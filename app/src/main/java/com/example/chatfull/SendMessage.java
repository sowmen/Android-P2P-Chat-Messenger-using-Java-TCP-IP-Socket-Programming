package com.example.chatfull;

import android.os.AsyncTask;
import android.util.Log;

import java.io.ObjectOutputStream;
import java.net.Socket;

public class SendMessage extends AsyncTask<Void, Void, String> {

    ChatActivity activity;
    private String dstAddress;
    private Message message;
    private int dstPort;
    private Socket clientSocket = null;

    SendMessage(String addr, int port, Message message, ChatActivity activity) {
        this.dstAddress = addr;
        this.dstPort = port;
        this.message = message;
        this.activity = activity;
    }

    @Override
    protected String doInBackground(Void... arg0) {
        try {
            clientSocket = new Socket(dstAddress, dstPort);
            Log.e("SEND_MSG", "Connected, Sending: " + message.getText());

            if (clientSocket != null) {
                ObjectOutputStream out = new ObjectOutputStream(clientSocket.getOutputStream());
                out.writeObject(message);
                out.flush();

                Log.e("SEND_MSG", "DONE: " + message.getText());
                activity.stopSender();
            }
        } catch (Exception e) {
            Log.e("SEND_MSG", "ConnectHoyNai " + message.getText());
            e.printStackTrace();
        }
        return null;
    }
}
