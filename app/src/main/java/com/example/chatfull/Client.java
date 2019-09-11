package com.example.chatfull;

import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class Client extends AsyncTask<Void, Void, String> {

    ConnectToUserActivity connectToUserActivity;
    String dstAddress, serverResponse = "";
    int dstPort;
    Socket clientSocket = null;
    public User user;

    Client(String addr, int port, ConnectToUserActivity connectToUserActivity) {
        this.dstAddress = addr;
        this.dstPort = port;
        this.connectToUserActivity = connectToUserActivity;
    }

    @Override
    protected String doInBackground(Void... arg0) {
        try {
            Log.e("Client","Before");
            clientSocket = new Socket(dstAddress, dstPort);
            if(clientSocket != null) {

                PrintWriter out = new PrintWriter(clientSocket.getOutputStream(),true);
                out.println(ShowInfoActivity.getSelfIpAddress()+":"+ShowInfoActivity.getSelfPort());

                Log.e("Client","After");
                user = new User(dstAddress, dstPort);
                connectToUserActivity.setUser(user);
//                MainActivity.userArrayList.add(user);
            }

            BufferedReader input = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
            serverResponse = input.readLine();

        } catch (Exception e) {
            e.printStackTrace();
            serverResponse = "UnknownHostException: " + e.toString();
        }
        finally {
            System.out.println("Final");
            if (clientSocket != null) {
                try {
                    clientSocket.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return serverResponse;
    }

    @Override
    protected void onPostExecute(String result) {
        connectToUserActivity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(connectToUserActivity.getApplicationContext(),serverResponse,Toast.LENGTH_SHORT).show();
            }
        });
        super.onPostExecute(result);
    }

}
