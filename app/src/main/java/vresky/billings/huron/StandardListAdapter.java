package vresky.billings.huron;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.List;

/**
 * Created by Matt on 23/12/2016.
 * NEW since Dec.23 push
 * tie std list XML to list.
 * Unnecessary as long as nothing more than a TextView is needed
 */
public class StandardListAdapter extends ArrayAdapter<String>{

    Context context;
    TextView textView;

    public StandardListAdapter(Context context, int resource, int textViewResourceId) {
        super(context, resource, textViewResourceId);
    }

    // creates layout for row item and maps data to views
//    @Override
//    public View getView(int position, View convertView, ViewGroup parent) {
//        //return super.getView(position, convertView, parent);
//        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
//        View rowView = inflater.inflate(R.layout.standard_list_item, parent, false);
//        return rowView;
//    }
}
