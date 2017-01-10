package vresky.billings.huron;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.TextView;

import java.util.List;

/**
 * doesn't use edit button
 */
public class ContactsAdapter extends BaseAdapter {

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

        tvContact.setText(contacts.get(position).toString());

        // LISTENERS

        btnDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                contacts.remove(position);
                notifyDataSetChanged();
            }
        });
        return view;
    }
}
