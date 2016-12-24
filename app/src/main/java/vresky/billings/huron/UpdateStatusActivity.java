package vresky.billings.huron;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.TypedValue;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.HeaderViewListAdapter;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Matt on 21/12/2016.
 */
public class UpdateStatusActivity extends AppCompatActivity {

    List<String> statusList;
    ListView lvStatus;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_status);

        lvStatus = (ListView) findViewById(R.id.lv_statuses);
        Button btnAddStatus = (Button) findViewById(R.id.btn_add_status);

        // populate list with default statuses
        statusList = new ArrayList<>();
        statusList.add("Reading");
        statusList.add("Eating");
        statusList.add("Leisure");
        statusList.add("Busy");
        statusList.add("Killing time");

        TextView statusListHeader = new TextView(this);
        statusListHeader.setTextSize(TypedValue.COMPLEX_UNIT_SP,
                getResources().getInteger(R.integer.LISTVIEW_HEADER_FONT_SIZE));
        statusListHeader.setText("Statuses");
        lvStatus.addHeaderView(statusListHeader);
        ArrayAdapter<String> statusAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, statusList);
        lvStatus.setAdapter(statusAdapter);

        btnAddStatus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(UpdateStatusActivity.this, AddStatusActivity.class);
                startActivity(intent);

            }
        });

        lvStatus.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                int statusIndex = position - 1;
                statusList.remove(statusIndex);
                HeaderViewListAdapter headerViewListAdapter = (HeaderViewListAdapter) lvStatus.getAdapter();
                ((BaseAdapter) headerViewListAdapter.getWrappedAdapter()).notifyDataSetChanged();
            }
        });
    }
}
