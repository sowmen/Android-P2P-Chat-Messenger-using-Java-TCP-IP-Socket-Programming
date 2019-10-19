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
    private ServerSocket serverSocket;
    private String self_ip_address;
    private int self_port;
    User user;
    private boolean stop = false;

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

                while (stop == false) {

                    // block the call until connection is created and return Socket object
                    Log.e("SERVER", "WAITING");
                    Socket received_userSocket = serverSocket.accept();
                    Log.e("SERVER", "CONNECTED");

                    //Receive user credentials
                    BufferedReader input = new BufferedReader(new InputStreamReader(received_userSocket.getInputStream()));
                    final String client_cred = input.readLine();
                    if (client_cred == null)
                        continue;

                    String client_ip = client_cred.substring(0, client_cred.indexOf(':'));
                    String client_port = client_cred.substring(client_cred.indexOf(':') + 1, client_cred.indexOf('_'));
                    String client_name = client_cred.substring(client_cred.indexOf('_')+1);

                    user = new User(client_ip, Integer.parseInt(client_port));
                    user.setName(client_name);
                    user.setId(client_cred);
                    //MainActivity.userArrayList.add(user);

                    //---Sending self name---
                    try {
                        String response_message = self_ip_address + ":" + self_port + "_" + DialogViewActivity.me.getName();
                        PrintWriter out = new PrintWriter(received_userSocket.getOutputStream(), true);
                        out.println(response_message);

                    } catch (IOException e) {
                        Log.e("INSIDE SERVER", "Could Not Send Self Credentials");
                        e.printStackTrace();
                    }
                    //----------------------------------------------//

                    activity.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(activity.getApplicationContext(), "Connected To: " + client_cred, Toast.LENGTH_LONG).show();

                            //Move to next activity from main thread
                            activity.setConnected(user);
                        }
                    });

                    stop = true;
                    break;
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            onDestroy();
        }
    }

    void onDestroy() {
        if (serverSocket != null) {
            try {
                Log.e("SERVER", "Closing Server");
                serverSocket.close();
                stop = true;
                Thread.interrupted();
            } catch (IOException e) {
                Log.e("SERVER", "Could Not Close Server");
                e.printStackTrace();
            }
        }
    }

}
