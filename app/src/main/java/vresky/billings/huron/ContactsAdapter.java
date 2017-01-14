package vresky.billings.huron;

import android.content.Context;
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
// TODO add confirmation for deletion
public class ContactsAdapter extends BaseAdapter {

    final String TAG = this.getClass().getSimpleName();
    static final int MODIFY_CONTACT_REQUEST = 2;

    private Context context;
    private List<Contact> contacts;

    public ContactsAdapter(Context context, List<Contact> contacts) {
        super();
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
        Button btnDelete = (Button) view.findViewById(R.id.contact_rv_btn_delete);

        tvContact.setText(contacts.get(position).getName());

        // LISTENERS

        btnDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // DEBUG note sure if this will cause problems
                DatabaseInterface db = null;
                int userId = -1;
//                db.removeContact()
                if (context instanceof ManageContactsActivity) {
                    db = ((ManageContactsActivity)context).db;
                    userId = ((ManageContactsActivity)context).user.getUserId();
                }
                if (db != null) {
                    Contact contactToRemove = contacts.get(position);
                    String removeResult = db.removeContact(userId, contactToRemove.getId());
                    if (removeResult.equals("error")) {
                        Log.d(TAG, "Contact could not be removed server-side");
                    }
                }
                contacts.remove(position);
                // delete if chosen status isn't the active user status
//                View containerLayout = ((LinearLayout)v.getParent());
//                Drawable drawable = containerLayout.getBackground();
//                ColorDrawable bg = (ColorDrawable)drawable;
//
//                if (bg.getColor() == UpdateStatusActivity.getStatusColor()) {
//                    Toast.makeText(context, "Cannot delete active status", Toast.LENGTH_SHORT).show();
//                } else {
//                    statusList.remove(position);
//                    notifyDataSetChanged();
//                }
                notifyDataSetChanged();
            }
        });
        return view;
    }
}
