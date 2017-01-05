package vresky.billings.huron;

/**
 * Created by Matt on 04/01/2017.
 * bundles additional information with the contact to display on map
 */
public class ContactWrapper {
    Contact contact;
    InfoBundle infoBundle;

    public ContactWrapper(Contact contact, InfoBundle infoBundle) {
        this.contact = contact;
        this.infoBundle = infoBundle;
    }
}
