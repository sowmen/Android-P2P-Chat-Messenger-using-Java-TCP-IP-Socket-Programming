package com.example.chatfull;

import android.util.Log;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;

public class Server {
    ShowInfoActivity activity;
    ServerSocket serverSocket;
    String self_ip_address;
    int self_port;
    public User user;

    public Server(ShowInfoActivity activity, String self_ip_address, int self_port) {
        this.activity = activity;
        this.self_port = self_port;
        this.self_ip_address = self_ip_address;

        Thread socketServerThread = new SocketServerThread();
        socketServerThread.start();
    }

    private class SocketServerThread extends Thread {
        @Override
        public void run() {
            try {
                // create ServerSocket using specified port
                serverSocket = new ServerSocket(self_port);

                while (true) {
                    // block the call until connection is created and return
                    // Socket object
                    Log.e("Server","InsideServer");
                    Socket received_userSocket = serverSocket.accept();
                    Log.e("Server","Connected");

                    BufferedReader input = new BufferedReader(new InputStreamReader(received_userSocket.getInputStream()));
                    String client_cred = input.readLine();
                    String client_ip = client_cred.substring(0,client_cred.indexOf(':'));
                    String client_port = client_cred.substring(client_cred.indexOf(':')+1);

                    user = new User(client_ip,Integer.parseInt(client_port));
//                    MainActivity.userArrayList.add(user);

                    final String selfMsg = received_userSocket.getInetAddress().toString() +":"+ received_userSocket.getPort();

                    try {
                        String response_message = self_ip_address + ":" + self_port;
                        PrintWriter out = new PrintWriter(received_userSocket.getOutputStream(),true);
                        out.println(response_message);

                    } catch (IOException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }

                    activity.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(activity.getApplicationContext(), "Connected to: "+selfMsg ,Toast.LENGTH_SHORT).show();
                            activity.setConnected(true);
                        }
                    });
                    break;
                }
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            if (serverSocket != null) {
                try {
                    Log.e("Server","Close Server");
                    serverSocket.close();
                } catch (IOException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }
        }
    }

}
