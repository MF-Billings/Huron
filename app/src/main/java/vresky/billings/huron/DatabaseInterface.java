package vresky.billings.huron;

import java.io.Serializable;

// TODO comments are out of date
public class DatabaseInterface implements Serializable {

    private int userID;
    private String userName;

    DatabaseAsyncTask dbat;

    // default constructor if no userName is set
    public DatabaseInterface() {

    }

    // constructor that sets the userID
    public DatabaseInterface(int userID) {
        this.userID = userID;
    }

    // use once the user has added himself to the database
    public DatabaseInterface(int userID, String password) {
        this.userID = userID;
        //authUser(userName, password);
    }

    /**
     * adds a user to the database, returns user id on success and false on failure
     */
    public String addUser(String userName, String password) {
        dbat = new DatabaseAsyncTask();
        try {
            String url = "http://pv.gotdns.ch/android/adduser.php?username=" + userName + "&password=" + password;
            return dbat.execute(url).get();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "error";
    }

    public String removeUser(int userID) {
        dbat = new DatabaseAsyncTask();
        try {
            String url = "http://pv.gotdns.ch/android/removeuser.php?userid=" + userID;
            return dbat.execute(url).get();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "error";
    }

    public String addContact(int userID, int contactID) {
        dbat = new DatabaseAsyncTask();
        try {
            String url = "http://pv.gotdns.ch/android/addcontact.php?userid=" + userID + "&contactid=" + contactID;
            return dbat.execute(url).get();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "error";
    }

    public String removeContact(int userID, int contactID) {
        dbat = new DatabaseAsyncTask();
        try {
            String url = "http://pv.gotdns.ch/android/removecontact.php?userid=" + userID + "&contactid=" + contactID;
            return dbat.execute(url).get();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "error";
    }

    public String setUserInfo(int userID, double latitude, double longitude, long timestamp, String status) {
        dbat = new DatabaseAsyncTask();
        try {
            String url = "http://pv.gotdns.ch/android/setuserinfo.php?userid=" + userID +
                    "&latitude=" + latitude +
                    "&longitude=" + longitude +
                    "&timestamp=" + timestamp +
                    "&status=" + status;
            return dbat.execute(url).get();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "error";
    }

    public String getUserInfo(int userID) {
        dbat = new DatabaseAsyncTask();
        try {
            String url = "http://pv.gotdns.ch/android/getcontactinfo.php?userid=" + userID;
            return dbat.execute(url).get();
        } catch (Exception e) {
            e.printStackTrace();
        }
/*
        try {
            this.wait(10000);
        } catch (Exception e) {
            e.printStackTrace();
        }
*/
        return "error";
    }
}
