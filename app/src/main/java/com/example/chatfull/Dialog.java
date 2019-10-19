package com.example.chatfull;

import com.stfalcon.chatkit.commons.models.IDialog;

import java.io.Serializable;
import java.util.ArrayList;

public class Dialog implements IDialog<Message>, Serializable {

    private String id;
    private String dialogPhoto;
    private String dialogName;
    private ArrayList<User> users;
    private Message lastMessage;

    private int unreadCount;

    public Dialog(String id, String name, String digitalPhoto,
                  ArrayList<User> users, Message lastMessage, int unreadCount) {


        this.id = id;
        this.dialogName = name;
        this.dialogPhoto = digitalPhoto;
        this.users = users;
        this.lastMessage = lastMessage;
        this.unreadCount = unreadCount;
    }

    @Override
    public String getId() {
        return id;
    }

    @Override
    public String getDialogPhoto() {

        return dialogPhoto;
    }

    @Override
    public String getDialogName() {
        return dialogName;
    }

    @Override
    public ArrayList<User> getUsers() {
        return users;
    }

    @Override
    public Message getLastMessage() {
        return lastMessage;
    }

    @Override
    public void setLastMessage(Message lastMessage) {
        this.lastMessage = lastMessage;
    }

    @Override
    public int getUnreadCount() {
        return unreadCount;
    }

    public void setUnreadCount(int unreadCount) {
        this.unreadCount = unreadCount;
    }
}
