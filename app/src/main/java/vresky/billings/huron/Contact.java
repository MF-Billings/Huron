package vresky.billings.huron;

import android.location.Location;

import java.io.Serializable;

/**
 * Created by Matt on 16/12/2016.
 * Should be expandable to include other potentially useful information, such as a nickname, a photo, a specialized map marker, etc.
 * NOTE Currently refers to firstName as name when presented to user
 *
 * May want to store other information like phone number, but I figured this is a decent entry point
 * A list of contacts for this user will be fetched from the database at the beginning of each session
 * there should probably be some method for updating the database when the list of contacts is
 * modified
 *
 * could just make this contain an infobundle and that's it
 */
// consider including different pin colors for different contacts
public class Contact implements Serializable {

    private static final String TAG = "Contact";

    // FIELDS

    private int id;                     // ties user to their database entry
    private String name;
    private Location location;

    // METHODS

    // create a new contact where every field is set individually;
    public Contact() { }

    // delete this later
    public Contact(String firstName) {
        this.name = firstName;
    }

    public Contact(int id, String firstName) {
        this.id = id;
        this.name = firstName;
    }


    public String getName() {
        return name;
    }

    public void setName(String firstName) {
        this.name = firstName;
    }

    @Override
    public String toString() {
        return name;
    }
}
