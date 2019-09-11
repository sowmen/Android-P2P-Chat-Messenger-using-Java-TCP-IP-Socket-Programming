package com.example.chatfull;

import android.widget.Toast;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class MessageListenerThread extends Thread {

    @Override
    public void run() {
//        if(MainActivity.socket != null){
//            while(true){
//                System.out.println("INSIDE MESSAGE THREAD");
//                try {
//                    BufferedReader input = new BufferedReader(new InputStreamReader(MainActivity.socket.getInputStream()));
//                    String line = input.readLine();
//                    if(line != null){
//                        ChatActivity.receivedMsg = line;
//                        ChatActivity.messageDisplay.append("Received : " + line);
//                    }
//                } catch (IOException e) {
//                    e.printStackTrace();
//                    System.out.println("<=============Error in MessageListenerThread=============>");
//                }
//            }
//        }
    }
}
