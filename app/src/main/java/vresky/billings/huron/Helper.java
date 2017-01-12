package vresky.billings.huron;

import android.util.Log;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Matt on 11/01/2017.
 */
public class Helper {
    /**
     *
     * @param tag
     * @param user
     * @param db
     * @return
     */
    public static List<Contact> retrieveContacts(String tag, User user, DatabaseInterface db) {
        ArrayList<Contact> contactsList = new ArrayList<>();
        String getContactsInfoResult = db.getContactsInfo(user.getUserId());
        if (getContactsInfoResult.equals("null")) {
            Log.d(tag, "No registered user with id " + user.getUserId());
        } else if (getContactsInfoResult.equals("error")) {
            Log.d(tag, "db.getContactsInfo(user.getUserId()) returned 'error'");
        } else {
            ArrayList<InfoBundle> contactInfoBundle = JSONtoArrayList.convert(getContactsInfoResult);
            // convert InfoBundles to contacts to populate list for adapter
            for (InfoBundle i : contactInfoBundle) {
                contactsList.add(new Contact(i));
            }
        }
        return contactsList;
    }
}
