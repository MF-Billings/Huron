package vresky.billings.huron;

import java.io.Serializable;

/**
 * Created by Matt on 16/12/2016.
 * Designed as wrapper for contact information relevant to the user, ie. name, location, image, etc.
 * Re-purposed to use InfoBundle due to time constraints
 */
// consider including different pin colors for different contacts
public class Contact implements Serializable {

    private static final String TAG = "Contact";

    // FIELDS

    private InfoBundle bundle;

    // METHODS

    // call to set fields individually
    public Contact() { }

    // delete this later
    public Contact(InfoBundle bundle) {
        this.bundle = bundle;
    }

    // TODO remove
    public String getName() {
        return bundle.getContactName();
    }

    public InfoBundle getInfoBundle() {
        return this.bundle;
    }

    @Override
    public String toString() {
        return bundle.toString();
    }
}
