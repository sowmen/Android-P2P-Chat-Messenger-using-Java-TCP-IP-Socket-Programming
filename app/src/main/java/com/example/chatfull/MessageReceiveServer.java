package com.example.chatfull;

import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;

public class MessageReceiveServer{
    String ip_address;
    int port;
    ChatActivity activity;
    ServerSocket serverSocket;
    int cnt = 0;

    MessageReceiveServer(String ip_address, int port, ChatActivity activity){
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
                while (true) {
                    cnt++;
                    Log.e("Abar",""+cnt);

                    Socket received_userSocket = serverSocket.accept();
                    Log.e("Receive","Connected");

                    try {
                        BufferedReader input = new BufferedReader(new InputStreamReader(received_userSocket.getInputStream()));
                        String message;
                        message = input.readLine();
                        Log.e("Receive",message);
                        activity.setMessage(message);
                        System.out.println("BAIRE=============================================>");
                    } catch (IOException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }

                }
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
    }

}
