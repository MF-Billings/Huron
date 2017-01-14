package vresky.billings.huron;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import vresky.billings.huron.Database.DatabaseInterface;

/**
 * Created by Matt on 20/12/2016.
 * single fragment activity to contain the AddContactFragment
 */
public class AddContactActivity extends Activity {

    static final String TAG = AddContactActivity.class.getSimpleName();
    private boolean returnDataIsEmpty;
    private DatabaseInterface db;
    private User user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_contact);

        returnDataIsEmpty = true;
        Button btnCancel = (Button)findViewById(R.id.add_contact_cancel);
        Button btnAddContact = (Button)findViewById(R.id.add_contact_save);
        final EditText etContactId = (EditText)findViewById(R.id.et_first_name);

        db = DatabaseInterface.getInstance();

        // unmarshall intent extras
        Object obj = getIntent().getSerializableExtra(getResources().getString(R.string.KEY_DB_INTERFACE_OBJ));
//        if (obj instanceof DatabaseInterface) {
//            db = (DatabaseInterface)obj;
//        } else {
//            Log.e(TAG, Thread.currentThread().getStackTrace()[0] + "Object cannot be cast to DatabaseInterface");
//        }
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
                String operationUserFeedback = "";
                String contactIdInput = etContactId.getText().toString();
                Intent result = new Intent();

                if (!contactIdInput.equals("")) {
                    String addContactResult;
                    // check for contact server-side
                    if (user.isRegistered()) {
                        addContactResult = db.addContact(user.getUserId(), Integer.valueOf(contactIdInput));

                        if (addContactResult.equals("error")) {
                            operationUserFeedback = "Could not find contact with id " + contactIdInput;
                        }
                        // return contact data to contact's list
                        else {
                            // notify user of successful operation
                            // replace contacts list
                            returnDataIsEmpty = false;
                            result.putExtra("success", true);
                            operationUserFeedback = "Contact added";
                        }
                    }
                    Log.d(TAG, contactIdInput);
                } else {
                    operationUserFeedback = "Cannot add contact without contact id";
                }
                Toast.makeText(AddContactActivity.this, operationUserFeedback, Toast.LENGTH_SHORT).show();

                // return contact data to ManageContactsActivity
                if (!returnDataIsEmpty) {
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
