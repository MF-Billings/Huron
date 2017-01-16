package vresky.billings.huron;

import android.util.Log;

import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;
import java.util.List;

import vresky.billings.huron.Database.DatabaseInterface;

/**
 * Created by Matt on 11/01/2017.
 * Contains convenience methods
 */

public class Helper {

    // LOCATIONS FOR DEBUG
    public static final LatLng ALPHIES_TROUGH = new LatLng(43.121159, -79.250452);
    public static final LatLng ALUMNI_FIELD = new LatLng(43.11917, -79.252973);
    public static final LatLng OLD_BUS_DROPOFF = new LatLng(43.119248, -79.248975);
    public static final LatLng HOTEL_DIEU_SHAVER = new LatLng(43.122146, -79.242913);
    public static final LatLng THE_LOFTS = new LatLng(43.115567, -79.238632);
    public static final LatLng ZONE_2_LOT = new LatLng(43.115582, -79.245766);

    /**
     * Retrieves a list of the user's contacts
     * @param tag the tag to use when logging unintended results from contact retrieval
     * @param user the user to retrieve contacts for
     * @param db the database object used to query the server for data
     * @return a list of Contacts cast as an ArrayList
     */
    public static List<Contact> retrieveContacts(String tag, User user, DatabaseInterface db) {
        ArrayList<Contact> contactsList = new ArrayList<>();
        String getContactsInfoResult = db.getContactsInfo(user.getUserId());
        if (getContactsInfoResult.equals("null")) {
            Log.d(tag, "No registered user with id " + user.getUserId());
        } else if (getContactsInfoResult.equals("error")) {
            Log.d(tag, "db.getContactsInfo(user.getUserId()) returned 'error'");
        }
        // success
        else {
            ArrayList<InfoBundle> contactInfoBundle = JSONtoArrayList.convert(getContactsInfoResult);
            // convert InfoBundles to contacts to populate list for adapter
            for (InfoBundle i : contactInfoBundle) {
                contactsList.add(new Contact(i));
            }
        }
        return contactsList;
    }
}
