package vresky.billings.huron;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

/**
 * Created by Matt on 20/12/2016.
 * single fragment activity to contain the AddContactFragment
 */
public class AddContactActivity extends Activity {

    Contact contact;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_contact);

        Button btnCancel = (Button)findViewById(R.id.add_contact_cancel);
        Button btnAddContact = (Button)findViewById(R.id.add_contact_save);
        final EditText contactId = (EditText)findViewById(R.id.et_first_name);

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
                if (!contactId.getText().toString().equals("")) {
                    Toast.makeText(AddContactActivity.this, contactId.getText().toString(), Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(AddContactActivity.this, "Cannot add contact without contact id", Toast.LENGTH_SHORT).show();
                }

                Intent result = new Intent();
                // return contact data to ManageContactsActivity
                if (contact != null) {
                    result.putExtra(getResources().getString(R.string.contact_parcel_key), contact);
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
