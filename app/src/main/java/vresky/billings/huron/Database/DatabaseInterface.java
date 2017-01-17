package vresky.billings.huron.Database;

import java.io.Serializable;

/**
 * Created by Patrick on 12/31/2016
 * Database interface class.
 * Uses DatabaseAsyncTask to push and pull data from web service.
 * Web service built by Patrick.
 *
 * Simple singleton. Not thread safe or rigorous.  This was implemented largely to alleviate the
 * need to pass references to the same database object between activities.
 */

public class DatabaseInterface implements Serializable {

    private static DatabaseInterface instance = null;       // do not access directly
    private int userID;
    private String userName;

    DatabaseAsyncTask dbat;
    /**
     * default constructor if no userName is set
     */
//    public DatabaseInterface() { }
//
//    // constructor that sets the userID
//    public DatabaseInterface(int userID) {
//        this.userID = userID;
//    }

//    /**
//     * use once the user has added himself to the database (this will be implemented soon)
//     * @param userID
//     * @param password
//     */
//    public DatabaseInterface(int userID, String password) {
//        this.userID = userID;
//        //authUser(userName, password);
//    }
//
    // prevents instantiation
    protected DatabaseInterface() {}

    public static DatabaseInterface getInstance() {
        if (instance == null)
            instance = new DatabaseInterface();
        return instance;
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
     * @return Use to return "null" if result set from database is empty, "error" on error.  Now
     * location data should return 0 if there is no value provided in the database for it.
     */
    public String getContactsInfo(int userID) {
        dbat = new DatabaseAsyncTask();
        try {
            String url = "http://pv.gotdns.ch/android/getcontactinfo.php?userid=" + userID;
            return dbat.execute(url).get();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "error";
    }
}
