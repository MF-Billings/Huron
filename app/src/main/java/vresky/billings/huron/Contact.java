package vresky.billings.huron;

import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;

/**
 * Created by Matt on 16/12/2016.
 * May want to store other information like phone number, but I figured this is a decent entry point
 * A list of contacts for this user will be fetched from the database at the beginning of each session
 * there should probably be some method for updating the database when the list of contacts is
 * modified
 */
public class Contact implements Parcelable {

    private static final String TAG = "Contact";

    // fields

    private String firstName;

    // methods

    public Contact(String firstName) {
        this.firstName = firstName;
    }

    // inflates/unmarshals Contact from Parcel
    private Contact(Parcel in) {
        this.firstName = in.readString();
    }


    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    @Override
    public String toString() {
        String s = firstName;
//        if (lastName != null) {
//            s += " " + lastName;
//        }
        return s;
    }

    // required Parcelable methods

    // returning 0 is usually fine
    public int describeContents() {
        return 0;
    }

    public void writeToParcel(Parcel out, int flags) {
        Log.v(TAG, "Creating parcel from Contact object");
        out.writeString(this.firstName);
    }

    public static final Parcelable.Creator<Contact> CREATOR = new Parcelable.Creator<Contact>() {
        // ensure data is read in the same order as it was written
        @Override
        public Contact createFromParcel(Parcel in) {
            return new Contact(in);
        }

        @Override
        public Contact[] newArray(int size) {
            return new Contact[size];
        }
    };
}
