package vresky.billings.huron;

import java.io.Serializable;

/**
 * Created by Patrick on 12/31/2016
 * Database interface class.
 * Uses DatabaseAsyncTask to push and pull data from web service.
 * Web service built by Patrick.
 *
 */
// TODO singleton?
public class DatabaseInterface implements Serializable {

    private int userID;
    private String userName;

    DatabaseAsyncTask dbat;
    /**
     * default constructor if no userName is set
     */
    public DatabaseInterface() { }

    // constructor that sets the userID
    public DatabaseInterface(int userID) {
        this.userID = userID;
    }

    /**
     * use once the user has added himself to the database (this will be implemented soon)
     * @param userID
     * @param password
     */
    public DatabaseInterface(int userID, String password) {
        this.userID = userID;
        //authUser(userName, password);
    }

    /**
     * Add a user to the database.  The username cannot contain spaces or it returns "server error"
     * @param userName
     * @param password
     * @return the user id if success and "server error" otherwise
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

    /**
     *
     * @param userID
     * @return "success" or "error" depending upon result
     */
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

    /**
     *
     * @param userID
     * @param contactID
     * @return "success" or "error" depending upon result
     */
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

    /**
     *
     * @param userID
     * @param contactID
     * @return "success" or "error" depending upon result
     */
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

    /**
     *
     * @param userID
     * @param latitude
     * @param longitude
     * @param timestamp
     * @param status
     * @return "success" or "error"
     */
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


    /**
     * returns JSON encoded contact information
     * @param userID
     * @return "null" if result set from database is empty, "error" on error
     */
    public String getContactsInfo(int userID) {
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
