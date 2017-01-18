package vresky.billings.huron;

import android.content.Context;
import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.TextView;

import java.util.List;

import vresky.billings.huron.Database.DatabaseInterface;

/**
 * doesn't use edit button
 */
public class ContactsAdapter extends BaseAdapter {

    final String TAG = this.getClass().getSimpleName();

    private Context context;
    private List<Contact> contacts;
    private DatabaseInterface db;

    public ContactsAdapter(Context context, List<Contact> contacts) {
        super();
        db = DatabaseInterface.getInstance();
        this.contacts = contacts;
        this.context = context;
    }

    public void replace(List<Contact> contactList) {
        contacts = contactList;
    }

    public List<Contact> getContacts() {
        return contacts;
    }

    @Override
    public int getCount() {
        return contacts.size();
    }

    @Override
    public Object getItem(int position) {
        return contacts.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(final int position, View convertView, final ViewGroup parent) {
        View view = convertView;

        if (view == null) {
            LayoutInflater inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view = inflater.inflate(R.layout.contact_row_view, null);
        }

        TextView tvContact = (TextView) view.findViewById(R.id.contact_rv_tv_name);
        Button btnDelete = (Button) view.findViewById(R.id.btn_delete);

        tvContact.setText(contacts.get(position).getName());

        // LISTENERS

        btnDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // prompt user for delete confirmation.
                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context);
                alertDialogBuilder.setTitle("Delete Contact?")
                    .setMessage(context.getResources().getString(R.string.delete_confirmation_message, "this contact"))
                    .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            deleteContact(position);
                        }
                    })
                    .setNegativeButton("No", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.cancel();
                        }
                    });

                AlertDialog deleteConfirmationDialog = alertDialogBuilder.create();
                deleteConfirmationDialog.show();
            }
        });
        return view;
    }

    private void deleteContact(int position) {
        // remove the contact from user's contacts list
        int userId = -1;
        if (context instanceof ManageContactsActivity) {
//            userId = ((ManageContactsActivity)context).user.getUserId();
            userId = User.getInstance().getUserId();
        }

        if (db != null) {
            Contact contactToRemove = contacts.get(position);
            String removeResult = db.removeContact(contactToRemove.getId());
            if (removeResult.equals("error")) {
                Log.d(TAG, "Contact could not be removed server-side");
            }
        }
        contacts.remove(position);
        notifyDataSetChanged();
    }
}
