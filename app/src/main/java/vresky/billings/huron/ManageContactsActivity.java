package vresky.billings.huron;

import android.content.Intent;
import android.content.SharedPreferences;
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

/**
 * Created by Matt on 16/12/2016.
 * Main interface for interacting with and viewing user contacts
 */
// TODO more testing with new contacts
public class ManageContactsActivity extends AppCompatActivity {

    static final String TAG = ManageContactsActivity.class.getSimpleName();
    static final int ADD_CONTACT_REQUEST = 1;

//    List<Contact> contacts;
    DatabaseInterface db;
    public User user;
    ListView lvContacts;
    ContactsAdapter contactsArrayAdapter; SharedPreferences prefs;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manage_contacts);

        lvContacts = (ListView) findViewById(R.id.lv_contacts);
        Button btnAddContact = (Button) findViewById(R.id.btn_add_contact);

        // DEBUG
        // determine if user is registered
        user = new User();
        // get user id if one exists
        prefs = getSharedPreferences(getResources().getString(R.string.APP_TAG), MODE_PRIVATE);
        int userId = prefs.getInt(getResources().getString(R.string.KEY_USER_ID), User.USER_ID_NOT_FOUND);
        // user has already registered
        // it matters what constructor you call
        if (userId == User.USER_ID_NOT_FOUND) {
            db = new DatabaseInterface();
        } else {
            user.setUserId(prefs.getInt(getResources().getString(R.string.KEY_USER_ID), -1));
            user.setUsername(prefs.getString(getResources().getString(R.string.KEY_USERNAME), ""));
            user.setStatus("");
            db = new DatabaseInterface(user.getUserId(), "GNDN");
        }

        // create adapter that will store contact list
        contactsArrayAdapter = new ContactsAdapter(this, new ArrayList<Contact>());

        // create textview for header in code and add it to the listview
        TextView contactsHeader = new TextView(this);
        contactsHeader.setTextSize(TypedValue.COMPLEX_UNIT_SP, 22);
        contactsHeader.setText(getResources().getString(R.string.contacts_literal));
        lvContacts.addHeaderView(contactsHeader);           // must be called before setAdapter if pre-Kitkat (Android 4.4)
        lvContacts.setAdapter(contactsArrayAdapter);

        retrieveContacts();

        // unmarshall intent extras
        Object obj = getIntent().getSerializableExtra(getResources().getString(R.string.KEY_DB_INTERFACE_OBJ));
        if (obj instanceof DatabaseInterface) {
            db = (DatabaseInterface)obj;
        } else {
            Log.e(TAG, Thread.currentThread().getStackTrace()[0] + "Object cannot be cast to DatabaseInterface");
        }
        obj = getIntent().getSerializableExtra(getResources().getString(R.string.KEY_USER));
        if (obj instanceof User) {
            user = (User)obj;
        } else {
            Log.e(TAG, Thread.currentThread().getStackTrace()[0] + "Object cannot be cast to User");
        }

        btnAddContact.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // add contact with the server's help
                Intent intent = new Intent(ManageContactsActivity.this, AddContactActivity.class);
                intent.putExtra(getResources().getString(R.string.KEY_USER), user);
                intent.putExtra(getResources().getString(R.string.KEY_DB_INTERFACE_OBJ), db);
                startActivityForResult(intent, ADD_CONTACT_REQUEST);
            }
        });
    }

    // change may have occurred from delete as well
    @Override
    protected void onPause() {
        super.onPause();
    }

    // Tycho id = 34
    // TestAdd id = 42
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        // add existing contact (ie. on the server)
        if (requestCode == ADD_CONTACT_REQUEST) {
            if (resultCode == RESULT_OK) {
                boolean contactAdded = data.getBooleanExtra("success", false);
                if (contactAdded == true) {
                    // replace contacts list (no method for returning added contact and adding individually)
                    String getContactsInfoResult = db.getContactsInfo(user.getUserId());
                    if (getContactsInfoResult.equals("null")) {
                        Log.d(TAG, "No registered user with id " + user.getUserId());
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
            } else if (resultCode == RESULT_CANCELED) { }
        }
    }

    private void retrieveContacts() {
        String getContactsInfoResult = db.getContactsInfo(user.getUserId());
        if (getContactsInfoResult.equals("null")) {
            Log.d(TAG, "No registered user with id " + user.getUserId());
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
