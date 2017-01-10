package vresky.billings.huron;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Matt on 20/12/2016.
 * single fragment activity to contain the AddContactFragment
 */
public class AddContactActivity extends Activity {

    static final String TAG = AddContactActivity.class.getSimpleName();
    private boolean bundleIsEmpty;
    private DatabaseInterface db;
    private User user;
    private List<Contact> contactsList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_contact);

        bundleIsEmpty = true;
        Button btnCancel = (Button)findViewById(R.id.add_contact_cancel);
        Button btnAddContact = (Button)findViewById(R.id.add_contact_save);
        final EditText etContactId = (EditText)findViewById(R.id.et_first_name);

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

        // LISTENERS

        // return to Contacts activity
        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        // save the contact locally
        btnAddContact.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String contactIdInput = etContactId.getText().toString();
                Intent result = new Intent();

                if (!contactIdInput.equals("")) {
                    String addContactResult;
                    // check for contact server-side
                    if (user.isRegistered()) {
                        addContactResult = db.addContact(user.getUserId(), Integer.valueOf(contactIdInput));

                        if (addContactResult.equals("error")) {
                            Toast.makeText(AddContactActivity.this, "Could not find contact with id " + contactIdInput, Toast.LENGTH_SHORT).show();
                        }
                        // return contact data to contact's list
                        else {
                            // replace contacts list
                            String getContactsInfoResult = db.getContactsInfo(user.getUserId());
                            if (getContactsInfoResult != null) {
                                JSONtoArrayList JSONparser = new JSONtoArrayList();
                                Object contactsBundle = JSONparser.convert(getContactsInfoResult);
                                if (contactsBundle instanceof ArrayList) {
                                    contactsList = (ArrayList<Contact>)contactsBundle;
                                } else {
                                    Log.d(TAG, "Received object from database is not an instance of ArrayList");
                                }
                            } else {
                                Log.d(TAG, "error retrieving contact info");
                            }
//                            ArrayList<InfoBundle> contactsBundle = JSONparser.convert(db.getUserInfo(user.getUserId()));
//                            result.putExtra("contactBundle", contactsBundle);
                            bundleIsEmpty = true;
                        }
                    }

                    Log.d(TAG, contactIdInput);
                } else {
                    Toast.makeText(AddContactActivity.this, "Cannot add contact without contact id", Toast.LENGTH_SHORT).show();
                }

                // return contact data to ManageContactsActivity
                if (!bundleIsEmpty) {
                    setResult(Activity.RESULT_OK, result);
                    finish();
                } else {
                    setResult(RESULT_CANCELED, result);
                    finish();
                }
            }
        });
    }
}
