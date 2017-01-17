package vresky.billings.huron;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Matt on 06/01/2017.
 * Contains necessary data about app user
 */
public class User implements Serializable {
    private static User instance;
    public final static int USER_ID_NOT_FOUND = -1;

    private static int userId = USER_ID_NOT_FOUND;
    private static boolean userIsRegistered = false;           // if this is false then the data is worthless
    private static String status;
    private static String username;
    List<Contact> contacts = new ArrayList<>();

    protected User() { }

    // DEBUG delete when done testing
    public User(int id, String username, String status) {
        this.userId = id;
        this.username = username;
        this.status = status;
        this.userIsRegistered = true;
        this.contacts = new ArrayList<>();
    }

    public static User getInstance() {
        if (instance == null)
            instance = new User();
        return instance;
    }

    public boolean isRegistered() {
        return this.userIsRegistered;
    }

    public int getUserId() {
        return instance.userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
        userIsRegistered = true;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        User.status = status;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public List<Contact> getContacts() {
        return contacts;
    }

    public void setContacts(List<Contact> contactsList) {
        this.contacts = contactsList;
    }

    public String toString() {
        return String.format("Username: %s%nId: %d%nStatus: %s", username, userId, status);
    }
}
