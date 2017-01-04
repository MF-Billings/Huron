package vresky.billings.huron;

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
 */
public class Contact implements Serializable {

    private static final String TAG = "Contact";

    // FIELDS

    private String firstName;

    // METHODS

    // create a new contact where every field is set individually;
    public Contact() { }

    public Contact(String firstName) {
        this.firstName = firstName;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    @Override
    public String toString() {
        return firstName;
    }
}
