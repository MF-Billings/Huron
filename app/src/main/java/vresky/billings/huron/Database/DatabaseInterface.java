package vresky.billings.huron.Database;

import java.io.Serializable;

/**
 * Created by Patrick on 12/31/2016
 * Database interface class.
 * Uses DatabaseAsyncTask to push and pull data from web service.
 * Web service built by Patrick.
 *
 **/

public class DatabaseInterface implements Serializable {

    private static DatabaseInterface instance = null;
    private int userID;
    private String userName;
    private String password;
    private String sessionID;

    DatabaseAsyncTask dbat;

    // prevents instantiation
    protected DatabaseInterface() {
        userID = 0;
        userName = "";
    }

    // default constructor if no userName is set
    public static DatabaseInterface getInstance() {
        if (instance == null)
            instance = new DatabaseInterface();
        return instance;
    }

    // returns userid if addUser operation successful
    // returns "error" on error
    public String addUser(String userName, String password) {
        this.userName = userName;
        this.password = password;
        dbat = new DatabaseAsyncTask();
        try {
            String url = "http://pv.gotdns.ch/android2/adduser.php?username=" + userName + "&password=" + password;
            String result = dbat.execute(url).get();
            String[] parts = result.split(" ");
            userID = Integer.valueOf(parts[0]);
            sessionID = parts[1];
            //Log.d("ADDUSER", "" + userID);
            //Log.d("ADDUSER", sessionID);
            return parts[0];
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "error";
    }

    // checks user password and updates and returns session key, returns "error" otherwise
    // returns userName if it works or "unauth" if wrong password or "error" otherwise
    public String authUser(int userID, String password) {
        this.userID = userID;
        this.password = password;
        this.sessionID = "";
        dbat = new DatabaseAsyncTask();
        try {
            String url = "http://pv.gotdns.ch/android2/authuser.php?userid=" + userID + "&password=" + password;
            String result = dbat.execute(url).get();
            if (result.equals("error") || result.equals("unauth")) return "error";
            else {
                String[] parts = result.split(" ");
                userName = parts[0];
                sessionID = parts[1];
                //Log.d("AUTHUSER", "" + userName);
                //Log.d("AUTHUSER", sessionID);
                return userName;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "error";
    }

    // returns "success" if remove user operation successful
    // returns "error" on error
/*    public String removeUser(int userID) {
        dbat = new DatabaseAsyncTask();
        try {
            String url = "http://pv.gotdns.ch/android/removeuser.php?userid=" + userID;
            return dbat.execute(url).get();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "error";
    }
*/

    // returns "success" if add contact operation successful
    // returns "error" on error
    public String addContact(int contactID) {
        dbat = new DatabaseAsyncTask();
        try {
            String url = "http://pv.gotdns.ch/android2/addcontact.php?userid=" + userID +
                    "&sessionid=" + sessionID +
                    "&contactid=" + contactID;
            return dbat.execute(url).get();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "error";
    }

    // returns "success" if remove contact operation successful
    // returns "error" on error
    public String removeContact(int contactID) {
        dbat = new DatabaseAsyncTask();
        try {
            String url = "http://pv.gotdns.ch/android2/removecontact.php?userid=" + userID +
                    "&sessionid=" + sessionID +
                    "&contactid=" + contactID;
            return dbat.execute(url).get();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "error";
    }

    // returns "success" if update operation successful
    // returns "error" on error
    public String setUserInfo(double latitude, double longitude, long timestamp, String status) {
        dbat = new DatabaseAsyncTask();
        try {
            String url = "http://pv.gotdns.ch/android2/setuserinfo.php?userid=" + userID +
                    "&sessionid=" + sessionID +
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

    // returns JSON encoded contact information
    // returns "null" if result set from ddatabase is empty
    // returns "error" on error
    public String getContactsInfo() {
        dbat = new DatabaseAsyncTask();
        try {
            String url = "http://pv.gotdns.ch/android2/getcontactinfo.php?userid=" + userID + "&sessionid=" + sessionID;
            return dbat.execute(url).get();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "error";
    }
}
