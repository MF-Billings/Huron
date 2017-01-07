package vresky.billings.huron;

import java.io.Serializable;

/**
 * Created by Matt on 06/01/2017.
 * Contains necessary data about app user
 */
public class User implements Serializable {
    public final static int USER_ID_NOT_FOUND = -1;

    private int userId = USER_ID_NOT_FOUND;
    private boolean userIsRegistered = false;           // if this is false then the data is worthless
    private String status;
    private String username;

    public boolean isRegistered() {
        return this.userIsRegistered;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
        userIsRegistered = true;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }
}
