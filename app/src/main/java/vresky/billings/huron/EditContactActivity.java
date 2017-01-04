package vresky.billings.huron;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

/**
 * Created by Matt on 30/12/2016.
 * Handle editing of contact data for existing contacts
 */
public class EditContactActivity extends AppCompatActivity {

    Button btnCancel;
    Button btnSave;
    EditText etFirstName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_contact);

        btnCancel = (Button) findViewById(R.id.edit_contact_btn_cancel);
        btnSave = (Button) findViewById(R.id.edit_contact_btn_save);
        etFirstName = (EditText) findViewById(R.id.edit_contact_et_first_name);

        // fill in existing information
        Intent impetus = getIntent();
        Object obj = impetus.getSerializableExtra(getResources().getString(R.string.KEY_PARCELABLE_CONTACT));
        Contact contact = null;
        if (obj instanceof Contact) {
            contact = (Contact) obj;
        }
        //final Contact contact = impetus.getSerializableExtra(getResources().getString(R.string.KEY_PARCELABLE_CONTACT));
        if (contact.getFirstName() != null) {
            etFirstName.setText(contact.getFirstName());
        }

        final Intent RETURN_INTENT = new Intent();

        // LISTENERS

        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setResult(RESULT_CANCELED, null);
                finish();
            }
        });
        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String firstName = etFirstName.getText().toString();
                // make sure there is data to save
                if (!firstName.isEmpty()) {
                    // create a new contact that reflects user edits
                    Contact modifiedContact = new Contact();
                    modifiedContact.setFirstName(firstName);
                    // attach contact to the intent being returned
                    RETURN_INTENT.putExtra(getResources().getString(R.string.KEY_PARCELABLE_CONTACT), modifiedContact);
                    int itemPosition = getIntent().getIntExtra(
                            getResources().getString(R.string.KEY_ITEM_POSITION),
                            -1);
                    if (itemPosition != -1) {
                        RETURN_INTENT.putExtra(getResources().getString(R.string.KEY_ITEM_POSITION), itemPosition);
                    } else {
                        Log.d(getResources().getString(R.string.APP_TAG), "EditActivity did not receive parsable item position from" +
                                "the activity that started it");
                    }

                    setResult(RESULT_OK, RETURN_INTENT);
                }
                // not enough data to save
                else {
                    RETURN_INTENT.putExtra(getResources().getString(R.string.KEY_INVALID_CONTACT_EDIT), getResources().getString(R.string.invalid_contact_edit_message));
                    setResult(RESULT_CANCELED, RETURN_INTENT);
                }
                finish();
            }
        });
    }
}
