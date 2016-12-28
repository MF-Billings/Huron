package vresky.billings.huron;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.TypedValue;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.HeaderViewListAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Matt on 16/12/2016.
 * consider using a floating action button to add contacts
 */
public class ManageContactsActivity extends AppCompatActivity {

    static final int ADD_CONTACT_REQUEST = 1;

    List<Contact> contacts = new ArrayList<>();
    ListView lvContacts;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manage_contacts);

        Button btnAddContacts = (Button) findViewById(R.id.btn_add_contact);
        lvContacts = (ListView) findViewById(R.id.lv_contacts);

        contacts.add(new Contact("Aaron"));
        contacts.add(new Contact("Sam"));
        contacts.add(new Contact("Matt"));

        final ArrayAdapter<Contact> contactsArrayAdapter = new ArrayAdapter<>(this, R.layout.standard_list_item,
                R.id.std_list_item_tv_message, contacts);

        // create textview for header in code and add it to the listview
        TextView contactsHeader = new TextView(this);
        contactsHeader.setTextSize(TypedValue.COMPLEX_UNIT_SP, 22);
        contactsHeader.setText(getResources().getString(R.string.contacts_literal));
        lvContacts.addHeaderView(contactsHeader);           // must be called before setAdapter if pre-Kitkat (Android 4.4)
        lvContacts.setAdapter(contactsArrayAdapter);

        // LISTENERS

        btnAddContacts.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ManageContactsActivity.this, ContactsActivity.class);
                startActivityForResult(intent, ADD_CONTACT_REQUEST);   // getResources().getInteger(R.integer.ADD_CONTACT_REQUEST)
            }
        });

        // NOTE the header counts as a position
        final SwipeDetector swipeDetector = new SwipeDetector();
        lvContacts.setOnTouchListener(swipeDetector);
        lvContacts.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (position > 0 && swipeDetector.swipeDetected()) {
                    // position counts HeaderView as the 1st element
                    int contactsIndex = position - 1;
                    SwipeDetector.Action swipeAction = swipeDetector.getAction();

                    if (swipeAction == SwipeDetector.Action.LEFT_TO_RIGHT) {
                        contacts.remove(contactsIndex);
                        HeaderViewListAdapter headerViewListAdapter = (HeaderViewListAdapter) lvContacts.getAdapter();
                        ((BaseAdapter) headerViewListAdapter.getWrappedAdapter()).notifyDataSetChanged();
                    } else if (swipeAction == SwipeDetector.Action.RIGHT_TO_LEFT) {
                        contacts.remove(contactsIndex);
                        HeaderViewListAdapter headerViewListAdapter = (HeaderViewListAdapter) lvContacts.getAdapter();
                        ((BaseAdapter) headerViewListAdapter.getWrappedAdapter()).notifyDataSetChanged();
                    }
                }
            }
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
//        Toast.makeText(getApplicationContext(), "contact acknowledged", Toast.LENGTH_SHORT).show();
        if (requestCode == ADD_CONTACT_REQUEST) {
            if (resultCode == RESULT_OK) {
                Contact addedContact = data.getParcelableExtra(getResources().getString(R.string.contact_parcel_key));
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
        }
    }
 }
