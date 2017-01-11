package vresky.billings.huron;

import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;

/**
 * Created by Matt on 04/01/2017.
 * Uses same list item layout as ContactsAdapter
 */
public class StatusAdapter extends BaseAdapter {

    private final String TAG = this.getClass().toString();
    private Context context;
    private List<String> statusList;

    public StatusAdapter(Context context, List<String> statusList) {
        super();
        this.statusList = statusList;
        this.context = context;
    }

    public String getStatus(int index) {
        return statusList.get(index);
    }

    @Override
    public int getCount() {
        return statusList.size();
    }

    @Override
    public Object getItem(int position) {
        return statusList.get(position);
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

        final TextView tvString = (TextView) view.findViewById(R.id.contact_rv_tv_name);
        Button btnDelete = (Button) view.findViewById(R.id.contact_rv_btn_delete);

        tvString.setText(statusList.get(position));

        // LISTENERS

        btnDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // delete if chosen status isn't the active user status
                View containerLayout = ((LinearLayout)v.getParent());
                Drawable drawable = containerLayout.getBackground();
                ColorDrawable bg = (ColorDrawable)drawable;

                if (bg.getColor() == UpdateStatusActivity.getStatusColor()) {
                    Toast.makeText(context, "Cannot delete active status", Toast.LENGTH_SHORT).show();
                } else {
                    statusList.remove(position);
                    notifyDataSetChanged();
                }
            }
        });
        return view;
    }
}
