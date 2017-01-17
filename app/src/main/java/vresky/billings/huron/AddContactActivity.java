package vresky.billings.huron;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import vresky.billings.huron.Database.DatabaseInterface;

/**
 * Created by Matt on 20/12/2016.
 * Note that adding a contact results in the user being added to the specified contact's contact list as well
 */
public class AddContactActivity extends Activity {

    static final String TAG = AddContactActivity.class.getSimpleName();
    private boolean returnDataIsEmpty;
    private DatabaseInterface db;
    private User user;
    private boolean isValidInputId;        // ensures that the set of user contacts aligns with expectations

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_contact);

        returnDataIsEmpty = true;
        Button btnCancel = (Button)findViewById(R.id.add_contact_cancel);
        Button btnAddContact = (Button)findViewById(R.id.add_contact_save);
        final EditText etContactId = (EditText)findViewById(R.id.et_first_name);

        db = DatabaseInterface.getInstance();
        user = User.getInstance();

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
                isValidInputId = true;

                if (!contactIdInput.equals("")) {
                    String addContactResult;
                    int nContactIdInput = Integer.valueOf(contactIdInput);

                    // TODO check for contact's existence server-side

                   if (user.isRegistered()) {
                       // the user shouldn't be able to add themselves as a contact
                       if (nContactIdInput == user.getUserId()) {
//                           operationUserFeedback = "You cannot add yourself as a contact";
//                           isValidInputId = false;
                       } else {
                           // the user shouldn't be able to add the same contact more than once
                           for (Contact c : user.getContacts()) {
                               if (c.getId() == nContactIdInput) {
                                   operationUserFeedback = "You cannot add the same user multiple times";
                                   isValidInputId = false;
                                   break;
                               }
                           }
                       }

                       if (isValidInputId) {
                           addContactResult = db.addContact(user.getUserId(), nContactIdInput);

                           // catch server-side errors
                           if (addContactResult.equals("error")) {
                               operationUserFeedback = "Could not find contact with id " + contactIdInput;
                           }
                           // success; return contact data to contact's list
                           else {
                               // replace contacts list
                               returnDataIsEmpty = false;
                               operationUserFeedback = "Contact added";
                           }
                       }
                   }
                    Log.d(TAG, contactIdInput);
                } else {
                    operationUserFeedback = "Cannot add contact without contact id";
                }
                // notify user of operation result
                Toast toast = Toast.makeText(AddContactActivity.this, operationUserFeedback, Toast.LENGTH_SHORT);
                toast.show();

                // return contact data to ManageContactsActivity
                if (!returnDataIsEmpty) {
                    setResult(Activity.RESULT_OK);
                } else {
                    setResult(RESULT_CANCELED);
                }
                finish();
            }
        });
    }
}
