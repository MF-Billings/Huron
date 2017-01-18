package vresky.billings.huron;

import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.support.v7.app.AlertDialog;
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
    static View selectedItem;
    static boolean isFirstTimeRunning = true;


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
        Button btnDelete = (Button) view.findViewById(R.id.btn_delete);

        tvString.setText(statusList.get(position));

        // highlight the item that was selected the last time the user was on the status activity this run
        if (!isFirstTimeRunning) {
            SharedPreferences prefs = context.getSharedPreferences(context.getResources().getString(R.string.APP_TAG), Context.MODE_PRIVATE);
            int positionToSelect = prefs.getInt("Selected Index", -1);

            if (positionToSelect != -1) {
//                view.setBackgroundColor(Color.TRANSPARENT);
                if (position == positionToSelect) {
//                    view.setBackgroundColor(Color.CYAN);
                    selectedItem = view;
                }
            }
        }

        // LISTENERS

        btnDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // delete if chosen status isn't the active user status
                View containerLayout = ((LinearLayout)v.getParent());
                Drawable drawable = containerLayout.getBackground();
                ColorDrawable bg = (ColorDrawable)drawable;

                if (bg != null && bg.getColor() == UpdateStatusActivity.getStatusColor()) {
                    Toast.makeText(context, "Cannot delete active status", Toast.LENGTH_SHORT).show();
                } else {
                    // prompt user for confirmation of delete action
                    // prompt user for delete confirmation.
                    AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context);
                    alertDialogBuilder.setTitle("Delete Status?")
                            .setMessage(context.getResources().getString(R.string.delete_confirmation_message, "this status"))
                            .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    deleteStatus(position);
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
            }
        });
        return view;
    }

    // doing this in a method allows the dialog to return before carrying out any action
    // additionally, calling remove form within onClick directly crashes the app
    private void deleteStatus(int position) {
        statusList.remove(position);
        notifyDataSetChanged();
    }
}
