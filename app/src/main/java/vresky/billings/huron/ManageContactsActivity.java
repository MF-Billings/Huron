package vresky.billings.huron;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.HeaderViewListAdapter;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import vresky.billings.huron.Database.DatabaseInterface;

/**
 * Created by Matt on 16/12/2016.
 * Main interface for interacting with and viewing user contacts
 * Button events triggered within the rows are handled by the adapter class
 */
// TODO more testing with new contacts
public class ManageContactsActivity extends AppCompatActivity {

    static final String TAG = ManageContactsActivity.class.getSimpleName();
    static final int ADD_CONTACT_REQUEST = 1;

    private DatabaseInterface db;
    private User user;
    private ListView lvContacts;
    private ContactsAdapter contactsArrayAdapter;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manage_contacts);

        lvContacts = (ListView)findViewById(R.id.lv_contacts);
        Button btnAddContact = (Button)findViewById(R.id.btn_add_contact);

        db = DatabaseInterface.getInstance();
        user = User.getInstance();

        // makes accessing the content list directly easier; would need to deal with HeaderListView otherwise
        contactsArrayAdapter = new ContactsAdapter(this, user.getContacts());

        // create textview for header in code and add it to the listview
        TextView contactsHeader = new TextView(this);
        contactsHeader.setTextSize(TypedValue.COMPLEX_UNIT_SP, 22);
        contactsHeader.setText(getResources().getString(R.string.contacts_literal));
        lvContacts.addHeaderView(contactsHeader);           // must be called before setAdapter if pre-Kitkat (Android 4.4)
        lvContacts.setAdapter(contactsArrayAdapter);

        retrieveContacts();

        // LISTENERS

        btnAddContact.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // add contact with the server's help
                Intent intent = new Intent(ManageContactsActivity.this, AddContactActivity.class);
                startActivityForResult(intent, ADD_CONTACT_REQUEST);
            }
        });
    }

    // change may have occurred from delete as well
    @Override
    protected void onPause() {
        super.onPause();
        // save contacts
        user.setContacts(contactsArrayAdapter.getContacts());
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        // add existing contact (ie. on the server)
        if (requestCode == ADD_CONTACT_REQUEST) {
            if (resultCode == RESULT_OK) {
                // replace contacts list (no method for returning added contact and adding individually)
                String getContactsInfoResult = db.getContactsInfo(user.getUserId());
                if (getContactsInfoResult.equals("null")) {
                    Log.d(TAG, "No registered user with id " + user.getUserId());
                }
                else if (getContactsInfoResult.equals("error")) {
                    Log.d(TAG, "db.getContactsInfo(user.getUserId()) returned 'error'");
                }
                // success
                else {
                    ArrayList<InfoBundle> contactInfoBundle = JSONtoArrayList.convert(getContactsInfoResult);
                    // convert InfoBundles to contacts to populate list for adapter
                    List<Contact> contactsList = new ArrayList<>();
                    for (InfoBundle i : contactInfoBundle) {
                        contactsList.add(new Contact(i));
                    }
                    contactsArrayAdapter.replace(contactsList);
                    // update the listview to reflect the changes in the backing data-set
                    /* when addHeaderView is used, the adapter is wrapped in a HeaderViewListAdapter
                     * which can't be cast to a BaseAdapter
                     */
                    HeaderViewListAdapter headerViewListAdapter = (HeaderViewListAdapter) lvContacts.getAdapter();
                    ((BaseAdapter)headerViewListAdapter.getWrappedAdapter()).notifyDataSetChanged();
                }
            } else if (resultCode == RESULT_CANCELED) { }
        }
    }

    private void retrieveContacts() {
        String getContactsInfoResult = db.getContactsInfo(user.getUserId());
        if (getContactsInfoResult.equals("null")) {
            Log.d(TAG, "No registered user with id " + user.getUserId() + ". No contacts to retrieve.");
        }
        else if (getContactsInfoResult.equals("error")) {
            Log.d(TAG, "db.getContactsInfo(user.getUserId()) returned 'error'");
        }
        else {
            ArrayList<InfoBundle> contactInfoBundle = JSONtoArrayList.convert(getContactsInfoResult);
            // convert InfoBundles to contacts to populate list for adapter
            ArrayList<Contact> contactsList = new ArrayList<>();
            for (InfoBundle i : contactInfoBundle) {
                contactsList.add(new Contact(i));
            }
            contactsArrayAdapter.replace(contactsList);
            // update the listview to reflect the changes in the backing data-set
            /* when addHeaderView is used, the adapter is wrapped in a HeaderViewListAdapter
             * which can't be cast to a BaseAdapter
             */
            HeaderViewListAdapter headerViewListAdapter = (HeaderViewListAdapter) lvContacts.getAdapter();
            ((BaseAdapter)headerViewListAdapter.getWrappedAdapter()).notifyDataSetChanged();
        }
    }
 }
