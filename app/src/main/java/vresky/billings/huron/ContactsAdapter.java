package vresky.billings.huron;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.TextView;

import java.util.List;

public class ContactsAdapter extends BaseAdapter {

    static final int MODIFY_CONTACT_REQUEST = 2;

    private Context context;
    private List<Contact> contacts;

    public ContactsAdapter(Context context, List<Contact> contacts) {
        super();
        this.contacts = contacts;
        this.context = context;
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
        Button btnEdit = (Button) view.findViewById(R.id.contact_rv_btn_edit);
        Button btnDelete = (Button) view.findViewById(R.id.contact_rv_btn_delete);

        tvContact.setText(contacts.get(position).toString());

        // LISTENERS

        // test this
        btnEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, EditContactActivity.class);
                String contactKey = context.getResources().getString(R.string.KEY_PARCELABLE_CONTACT);
                String itemPositionKey = context.getResources().getString(R.string.KEY_ITEM_POSITION);

                intent.putExtra(contactKey, (Contact)getItem(position));
                intent.putExtra(itemPositionKey, position);
                ((AppCompatActivity)parent.getContext()).startActivityForResult(intent, MODIFY_CONTACT_REQUEST);
            }
        });

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
