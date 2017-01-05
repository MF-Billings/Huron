package vresky.billings.huron;

import android.util.Log;

import java.io.BufferedInputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

class DatabaseInterface {

    private String userName;	    // userName will have to be stored somewhere on the phone once set
    private int userPrimaryKey;	    // primary key associated with user in database, should also be stored on the phone for fast db lookup

    private static final String TAG = "Http Connection";


    // default constructor if no userName is set
    public DatabaseInterface() {

    }

    // use once the user has added himself to the database
    public DatabaseInterface(String userName, String password) {
        this.userName = userName;
        //authUser(userName, password);
    }

    // this function will return an arraylist of InfoBundles
    public ArrayList<InfoBundle> getContactLocationsAndStatuses() {
        InputStream inputStream = null;
        HttpURLConnection urlConnection = null;
        boolean result = false;
        ArrayList<InfoBundle> array = new ArrayList<>();

        try {

            String address = "http://pv.gotdns.ch/android/getcontactlocationsandstatuses.php?userprimarykey=" + userPrimaryKey;
            URL url = new URL(address);
            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setRequestMethod("GET");
            int statusCode = urlConnection.getResponseCode();
            if (statusCode ==  200) {
                inputStream = new BufferedInputStream(urlConnection.getInputStream());
                String response = inputStream.toString();

                // to do: parse response as json and populate array

            }
            urlConnection.disconnect();

        } catch (Exception e) {
            Log.d(TAG, e.getLocalizedMessage());
        }
        return array;   // returns empty array for now
    }

    // sets user location and status
    public boolean setUserLocationAndStatus(Double latitude, Double longitude, String status) {	//and timestamp as long int
        InputStream inputStream = null;
        HttpURLConnection urlConnection = null;
        boolean result = false;

        try {

            String address = "http://pv.gotdns.ch/android/setuserlocationandstatus.php?userprimarykey=" + userPrimaryKey
                    + "&latitude=" + latitude
                    + "&longitude=" + longitude
                    + "&status=" + status;
            URL url = new URL(address);
            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setRequestMethod("GET");
            int statusCode = urlConnection.getResponseCode();
            if (statusCode ==  200) {
                inputStream = new BufferedInputStream(urlConnection.getInputStream());
                String response = inputStream.toString();
                result = true;
            }
            else {
                result = false;
            }
            urlConnection.disconnect();

        } catch (Exception e) {
            Log.d(TAG, e.getLocalizedMessage());
        }
        return result;
    }

//    // returns list of InfoBundles -> unnecessary, you can get contacts list through getContactLocationsAndStatuses()
//    public ArrayList<InfoBundle> getContactsList() {
//
//    }

    // adds a user's contact to the database
    public boolean addContact(String contactName) {
        InputStream inputStream = null;
        HttpURLConnection urlConnection = null;
        boolean result = false;

        try {

            String address = "http://pv.gotdns.ch/android/addcontact.php?userprimarykey=" + userPrimaryKey + "&contactname=" + contactName;
            URL url = new URL(address);
            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setRequestMethod("GET");
            int statusCode = urlConnection.getResponseCode();
            if (statusCode ==  200) {
                inputStream = new BufferedInputStream(urlConnection.getInputStream());
                String response = inputStream.toString();
                result = true;
            }
            else {
                result = false;
            }
            urlConnection.disconnect();

        } catch (Exception e) {
            Log.d(TAG, e.getLocalizedMessage());
        }
        return result;
    }

    // removes a user's contact from the database
    public boolean removeContact(String contactName) {
        InputStream inputStream = null;
        HttpURLConnection urlConnection = null;
        boolean result = false;

        try {

            String address = "http://pv.gotdns.ch/android/removecontact.php?userprimarykey=" + userPrimaryKey + "&contactname=" + contactName;
            URL url = new URL(address);
            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setRequestMethod("GET");
            int statusCode = urlConnection.getResponseCode();
            if (statusCode ==  200) {
                inputStream = new BufferedInputStream(urlConnection.getInputStream());
                String response = inputStream.toString();
                result = true;
            }
            else {
                result = false;
            }
            urlConnection.disconnect();

        } catch (Exception e) {
            Log.d(TAG, e.getLocalizedMessage());
        }
        return result;
    }

    // adds a user to the database, returns true on success and false on failure
    public boolean addUser(String userName, String password) {
        InputStream inputStream = null;
        HttpURLConnection urlConnection = null;
        boolean result = false;

        try {

            String address = "http://pv.gotdns.ch/android/adduser.php?username=" + userName + "&password=" + password;
            URL url = new URL(address);
            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setRequestMethod("GET");
            int statusCode = urlConnection.getResponseCode();
            if (statusCode ==  200) {
                inputStream = new BufferedInputStream(urlConnection.getInputStream());
                String response = inputStream.toString();
                result = true;
            }
            else {
                result = false;
            }
            urlConnection.disconnect();

        } catch (Exception e) {
            Log.d(TAG, e.getLocalizedMessage());
        }
        return result;
    }

    // removes user and all associated data from the database
    public boolean removeUser() {
        InputStream inputStream = null;
        HttpURLConnection urlConnection = null;
        boolean result = false;

        try {

            String address = "http://pv.gotdns.ch/android/removeuser.php?userprimarykey=" + userPrimaryKey;
            URL url = new URL(address);
            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setRequestMethod("GET");
            int statusCode = urlConnection.getResponseCode();
            if (statusCode ==  200) {
                inputStream = new BufferedInputStream(urlConnection.getInputStream());
                String response = inputStream.toString();
                result = true;
            }
            else {
                result = false;
            }
            urlConnection.disconnect();

        } catch (Exception e) {
            Log.d(TAG, e.getLocalizedMessage());
        }
        return result;
    }

    // authenticates the user
    boolean authUser(String userName, String password) {
        return false;
    }
}

