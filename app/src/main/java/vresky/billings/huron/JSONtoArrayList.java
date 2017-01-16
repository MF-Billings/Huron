package vresky.billings.huron;

import android.location.Location;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;


/**
 * Created by Patrick on 04/01/2017
 * Parses JSON, populates InfoBundle objects, and adds them to an arraylist.
 */
public class JSONtoArrayList {

    /**
     * Converts result string from getUserInfo method to an InfoBundle object.
     * This allows access to the pertinent user data in a easy-to-manipulate format
     * @param result return string from getUserInfo method from DatabaseInterface class
     * @return ArrayList containing all the user's contacts and their data
     */
    public static ArrayList<InfoBundle> convert(String result) {
        ArrayList<InfoBundle> arrayList = new ArrayList<>();

        try {
            JSONArray jsonArray = new JSONArray(result);
            JSONObject contact;
            InfoBundle infoBundle;
            Location location;

            for (int i = 0; i < jsonArray.length(); i++) {
                contact = jsonArray.getJSONObject(i);

                int contactID = contact.getInt("userid");
                String contactName = contact.getString("username");
                location = new Location("contact");
                // replace with regex if there's time
                if (contact.get("latitude") != JSONObject.NULL) {
                    location.setLatitude(contact.getDouble("latitude"));
                }
                if (contact.get("longitude") != JSONObject.NULL) {
                    location.setLongitude(contact.getDouble("longitude"));
                }
                if (contact.get("tstamp") != JSONObject.NULL) {
                    location.setTime(contact.getLong("tstamp"));
                }
                String status = contact.getString("status");

                infoBundle = new InfoBundle(contactID, contactName, location, status);

                arrayList.add(infoBundle);
            }

        } catch (JSONException e){
            e.printStackTrace();
        }

        return arrayList;
    }
}
