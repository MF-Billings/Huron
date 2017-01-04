package vresky.billings.huron;

import android.os.Parcel;
import android.util.Log;

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
 * Parcelable allows objects of this class to be passes to other activities and fragments (testing removal)
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

    // NOTE this will be removed in the next commit. Kept temporarily as reference
    // existence of Parcelable and Serializable implementations causes ambiguity. Testing removal of Parcelable
    // inflates/unmarshals Contact from Parcel
//    private Contact(Parcel in) {
//        this.firstName = in.readString();
//    }


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

    // NOTE code below will be removed in the next commit. Kept temporarily as reference
    // required Parcelable methods

    // returning 0 is usually fine
    public int describeContents() {
        return 0;
    }

    public void writeToParcel(Parcel out, int flags) {
        Log.v(TAG, "Creating parcel from Contact object");
        out.writeString(this.firstName);
    }

//    public static final Parcelable.Creator<Contact> CREATOR = new Parcelable.Creator<Contact>() {
//        // ensure data is read in the same order as it was written
//        @Override
//        public Contact createFromParcel(Parcel in) {
//            return new Contact(in);
//        }
//
//        @Override
//        public Contact[] newArray(int size) {
//            return new Contact[size];
//        }
//    };
}
