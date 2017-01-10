package vresky.billings.huron;

import android.location.Location;

import java.io.Serializable;

/**
 * Created by Patrick on 21/12/2016.
 * InfoBundle bundles contact name, location, and status into a single object
 */

class InfoBundle implements Serializable {
    private int contactID;
    private String contactName;
    private Location location;
    private String status;

    public int getContactID() {
        return contactID;
    }

    public void setContactID(int contactID) {
        this.contactID = contactID;
    }

    public String getContactName() {
        return contactName;
    }

    public void setContactName(String contactName) {
        this.contactName = contactName;
    }

    public Location getLocation() {
        return location;
    }

    public void setLocation(Location location) {
        this.location = location;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public InfoBundle(int contactID, String contactName, Location location, String status) {
        this.contactID = contactID;
        this.contactName = contactName;
        this.location = location;
        this.status = status;
    }

    public String toString() {
        return String.format(
                "cId: %1$d %ncName: %2$s %nlat/long: %3$f/%4$f %ntime(ms): %5$d %nstatus: %6$s",
                contactID, contactName, location.getLatitude(), location.getLongitude(),
                location.getTime(), status);
    }
}