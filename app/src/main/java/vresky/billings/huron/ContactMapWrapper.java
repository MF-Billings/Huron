package vresky.billings.huron;

import com.google.android.gms.maps.model.Marker;

/**
 * Created by Matt on 11/01/2017.
 * Links Contact with data supporting the display of the Contact on the map
 */
public class ContactMapWrapper {
    private Contact contact;
    private Marker marker;

    public ContactMapWrapper(Contact contact) {
        this.contact = contact;
        this.marker = null;
    }

    public Contact getContact() {
        return contact;
    }

    public void setContact(Contact contact) {
        this.contact = contact;
    }

    public Marker getMarker() {
        return marker;
    }

    public void setMarker(Marker marker) {
        this.marker = marker;
    }
}
