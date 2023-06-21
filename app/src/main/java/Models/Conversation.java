package Models;

import java.util.ArrayList;

import Models.Message;
import Models.User;

public class Conversation {
    private String user1ID;
    private String user2ID;
    private ArrayList<Message> messageArrayList;
    private String lastMessageTime;
    private String id;
    private boolean isBlocked;
    private String userWhoBlockedID;

    public String getUserWhoBlockedID() {
        return userWhoBlockedID;
    }

    public void setUserWhoBlockedID(String userWhoBlockedID) {
        this.userWhoBlockedID = userWhoBlockedID;
    }

    public boolean isBlocked() {
        return isBlocked;
    }

    public void setBlocked(boolean blocked) {
        isBlocked = blocked;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getLastMessageTime() {
        return lastMessageTime;
    }

    public void setLastMessageTime(String lastMessageTime) {
        this.lastMessageTime = lastMessageTime;
    }

    public String getUser1ID() {
        return user1ID;
    }

    public void setUser1ID(String user1ID) {
        this.user1ID = user1ID;
    }

    public String getUser2ID() {
        return user2ID;
    }

    public void setUser2ID(String user2ID) {
        this.user2ID = user2ID;
    }

    public ArrayList<Message> getMessageArrayList() {
        return messageArrayList;
    }

    public void setMessageArrayList(ArrayList<Message> messageArrayList) {
        this.messageArrayList = messageArrayList;
    }
}
