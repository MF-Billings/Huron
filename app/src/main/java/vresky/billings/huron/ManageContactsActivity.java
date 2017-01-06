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
import android.widget.Toast;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Matt on 16/12/2016.
 * Main interface for interacting with and viewing user contacts
 */
// TODO implement add contact functionality
public class ManageContactsActivity extends AppCompatActivity {

    static final int ADD_CONTACT_REQUEST = 1;

    final List<Contact> contacts = new ArrayList<>();
    ListView lvContacts;
    ContactsAdapter contactsArrayAdapter;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manage_contacts);

        lvContacts = (ListView) findViewById(R.id.lv_contacts);
        Button btnAddContact = (Button) findViewById(R.id.btn_add_contact);

        // read in list of contacts
        restoreContacts();

        if (contacts.isEmpty()) {
            // hardcode some contacts for testing
            contacts.add(new Contact("Aaron"));
            contacts.add(new Contact("Sam"));
            contacts.add(new Contact("Kim"));

            // convey that re-population of dummy contacts was intentional
            Toast.makeText(ManageContactsActivity.this, "DEBUG re-populating empty contacts list with dummy values", Toast.LENGTH_SHORT).show();
        }

        contactsArrayAdapter = new ContactsAdapter(this, contacts);

        // create textview for header in code and add it to the listview
        TextView contactsHeader = new TextView(this);
        contactsHeader.setTextSize(TypedValue.COMPLEX_UNIT_SP, 22);
        contactsHeader.setText(getResources().getString(R.string.contacts_literal));
        lvContacts.addHeaderView(contactsHeader);           // must be called before setAdapter if pre-Kitkat (Android 4.4)
        lvContacts.setAdapter(contactsArrayAdapter);

        btnAddContact.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // waiting on database interface
                Intent intent = new Intent(ManageContactsActivity.this, AddContactActivity.class);
                startActivityForResult(intent, ADD_CONTACT_REQUEST);
            }
        });
    }

    // change may have occurred from delete as well
    @Override
    protected void onPause() {
        super.onPause();
        saveContacts();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == ADD_CONTACT_REQUEST) {
            if (resultCode == RESULT_OK) {
                Contact addedContact = (Contact)data.getSerializableExtra(getResources().getString(R.string.contact_parcel_key));   // getParceableExtra
                // update list
                contacts.add(addedContact);
                // update the listview to reflect the changes in the backing dataset
                /* when addHeaderView is used, the adapter is wrapped in a HeaderViewListAdapter
                 * which can't be cast to a BaseAdapter
                 */
                HeaderViewListAdapter headerViewListAdapter = (HeaderViewListAdapter) lvContacts.getAdapter();
                ((BaseAdapter)headerViewListAdapter.getWrappedAdapter()).notifyDataSetChanged();
            } else if (resultCode == RESULT_CANCELED) {
                Toast.makeText(this, "cancelled", Toast.LENGTH_SHORT).show();
            }
        } else if (requestCode == ContactsAdapter.MODIFY_CONTACT_REQUEST) {
            if (resultCode == RESULT_OK) {
                String itemPositionKey = getResources().getString(R.string.KEY_ITEM_POSITION);
                int positionOfModifiedItem = data.getIntExtra(itemPositionKey, -1);
                if (positionOfModifiedItem != -1) {
                    String contactKey = getResources().getString(R.string.KEY_PARCELABLE_CONTACT);
                    contacts.set(positionOfModifiedItem, (Contact)data.getSerializableExtra(contactKey)); //getParcelableExtra
                    contactsArrayAdapter.notifyDataSetChanged();
                } else {
                    Log.d(getResources().getString(R.string.APP_TAG), "invalid list index supplied by EditContact activity");
                }
            } else if (resultCode == RESULT_CANCELED) {
                String toastMsg = "";
                String errorMsg = "";
                if (data != null) {
                    errorMsg = data.getStringExtra(getResources().getString(R.string.KEY_INVALID_CONTACT_EDIT));
                }
                if (!errorMsg.isEmpty()) {
                    toastMsg = errorMsg;
                }

                // display user feedback if there is any
                if (!toastMsg.isEmpty()) {
                    Toast.makeText(this, toastMsg, Toast.LENGTH_SHORT).show();
                }
            }
        }
    }

    private void saveContacts() {
        File filePath = new File(getFilesDir() + "/" + getResources().getString(R.string.contacts_file_name));
        FileOutputStream fout;
        ObjectOutputStream oout = null;

        try {
            fout = new FileOutputStream(filePath);
            oout = new ObjectOutputStream(fout);
            oout.writeObject(contacts);
        } catch (Exception e) {
            e.printStackTrace();
        }
        // close connection
        finally {
            try {
                if (oout != null)
                    oout.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void restoreContacts() {
        // NOTE directory separator used may be Windows specific
        // write a file using the file name prepended with the path where app files are stored by the Android
        File filePath = new File(getFilesDir() + "/" + getResources().getString(R.string.contacts_file_name));
        FileInputStream fin;
        ObjectInputStream oin = null;

        try {
            fin = new FileInputStream(filePath);
            oin = new ObjectInputStream(fin);
            Object obj = oin.readObject();

            // populate contacts member. obj should be a List
            if (obj instanceof ArrayList) {
                // assume ArrayList is made up of Contact objects
                ArrayList<Contact> arrayList = ((ArrayList<Contact>)obj);
                for (int i = 0; i < arrayList.size(); i++) {
                    contacts.add(arrayList.get(i));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (oin != null) {
                try {
                    oin.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

        }

    }
 }
