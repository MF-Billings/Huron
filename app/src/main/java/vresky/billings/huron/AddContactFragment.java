package vresky.billings.huron;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

/**
 * Created by Matt on 16/12/2016.
 * Figure there's a lot of potential information that one could want a contact to have, so it's
 * better for it to be a stand-alone activity or fragment.
 * Consider using the bundle to keep information if the app is paused and resumed
 */
// NOTE not currently in use
public class AddContactFragment extends Fragment {

    Contact contact;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_add_contact, container, false);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        Button btnCancel = (Button) getView().findViewById(R.id.add_contact_cancel);
        Button btnSave = (Button) getView().findViewById(R.id.add_contact_save);
        final EditText etFirstName = (EditText) getView().findViewById(R.id.et_first_name);

        // assign listeners

        // return to Contacts activity
        btnCancel.setOnClickListener(new View.OnClickListener() { //http://stackoverflow.com/questions/4038479/android-go-back-to-previous-activity
            @Override
            public void onClick(View v) {
                getActivity().finish();
            }
        });

        // save the contact
        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!etFirstName.getText().toString().equals("")) {
                    contact = new Contact(etFirstName.getText().toString());
                    onDetach();
                }
            }
        });
    }

    // code may need to eventually be moved to another lifecycle method as this one may not be called
    // in some situations
    @Override
    public void onDetach() {
        super.onDetach();
        // return contact data to ManageContactsActivity
        if (contact != null) {
            Intent result = new Intent();
            result.putExtra(getResources().getString(R.string.contact_parcel_key), this.contact);
            getActivity().setResult(Activity.RESULT_OK, result);
            getActivity().finish();
        }
    }

}
