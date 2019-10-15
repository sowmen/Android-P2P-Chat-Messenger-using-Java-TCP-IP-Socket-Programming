package com.example.chatfull;

import android.util.Log;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.ServerSocket;
import java.net.Socket;

public class MessageReceiveServer {
    private String ip_address;
    private int port;
    ChatActivity activity;
    private ServerSocket serverSocket;
    private boolean stop = false;

    MessageReceiveServer(String ip_address, int port, ChatActivity activity) {
        this.ip_address = ip_address;
        this.port = port;
        this.activity = activity;

        Thread socketServerThread = new Thread(new MessageSocketServerThread());
        socketServerThread.start();
    }

    private class MessageSocketServerThread extends Thread {
        @Override
        public void run() {
            try {
                serverSocket = new ServerSocket(port);
                while (stop == false) {
                    Socket received_userSocket = serverSocket.accept();
                    Log.e("RECEIVE", "Connected");

                    try {
                        ObjectInputStream in = new ObjectInputStream(received_userSocket.getInputStream());
                        Message message = (Message) in.readObject();

                        Log.e("RECEIVE", "RECEIVED ==>" + message);
                        activity.setMessage(message);

                    } catch (IOException e) {
                        e.printStackTrace();
                    } catch (ClassNotFoundException e) {
                        e.printStackTrace();
                    }

                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void onDestroy() {
        if (serverSocket != null) {
            try {
                serverSocket.close();
                stop = true;
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

}
