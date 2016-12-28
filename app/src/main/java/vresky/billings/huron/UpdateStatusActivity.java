package vresky.billings.huron;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.util.TypedValue;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.HeaderViewListAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;

/**
 * Created by Matt on 21/12/2016.
 * no way of restoring built-in statuses
 */
public class UpdateStatusActivity extends AppCompatActivity {

    private static final int ADD_STATUS_REQUEST = 1;
    private static final String PREF_NAME_USER_STATUS_LIST = "user_status_list";

    private SharedPreferences mPrefs;
    private List<String> statusList;
    private ListView lvStatus;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_status);

        statusList = new ArrayList<>();
        mPrefs = getSharedPreferences(PREF_NAME_USER_STATUS_LIST, MODE_PRIVATE);
        lvStatus = (ListView) findViewById(R.id.lv_statuses);
        Button btnAddStatus = (Button) findViewById(R.id.btn_add_status);

        TextView statusListHeader = new TextView(this);
        statusListHeader.setTextSize(TypedValue.COMPLEX_UNIT_SP,
                getResources().getInteger(R.integer.LISTVIEW_HEADER_FONT_SIZE));
        statusListHeader.setText(getResources().getString(R.string.statuses_literal));
        lvStatus.addHeaderView(statusListHeader);
        final ArrayAdapter<String> statusAdapter = new ArrayAdapter<>(this, R.layout.standard_list_item,
                R.id.std_list_item_tv_message, statusList);
//        final StandardListAdapter statusAdapter = new StandardListAdapter(this, R.layout.standard_list_item,
//                R.id.std_list_item_tv_message, statusList);d
        lvStatus.setAdapter(statusAdapter);

        // LISTENERS

        btnAddStatus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(UpdateStatusActivity.this, AddStatusActivity.class);
                startActivityForResult(intent, ADD_STATUS_REQUEST);

            }
        });

        final SwipeDetector swipeDetector = new SwipeDetector();
        // touch listener changes the data within SwipeDetector
        lvStatus.setOnTouchListener(swipeDetector);
        /* uses data provided by touch event in SwipeDetector to essentially condense the information
         * action
         */
        lvStatus.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                // 1st condition is to ignore touch or click events on header
                if (position > 0 && swipeDetector.swipeDetected()) {

                    int statusListIndex = position - 1;
                    SwipeDetector.Action swipeAction = swipeDetector.getAction();
                    if (swipeAction == SwipeDetector.Action.LEFT_TO_RIGHT) {
                        statusList.remove(statusListIndex);
                        notifyStatusDataSetChanged();
                    } else if (swipeAction == SwipeDetector.Action.RIGHT_TO_LEFT) {
                        statusList.remove(statusListIndex);
                        notifyStatusDataSetChanged();
                    }
                }
            }
        });

        // read status list
        FileReader reader;
        BufferedReader bReader;
        try {
            reader = new FileReader(getFilesDir() + "/" + getString(R.string.status_file_name));
            bReader = new BufferedReader(reader);
            String data;
            while ((data = bReader.readLine()) != null) {
                statusList.add(data);
            }
            // DEBUG test list repopulation with defaults
            if (statusList.isEmpty()) {
                throw new FileNotFoundException();
            }
            bReader.close();
        } catch (FileNotFoundException e) {
            // populate list with default statuses
            String[] defaultStatusList = getResources().getStringArray(R.array.default_status_array);
            statusList = Arrays.asList(defaultStatusList);
            Toast.makeText(UpdateStatusActivity.this, "Status file not found. Using defaults.", Toast.LENGTH_SHORT).show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    // maybe move this into another lifecycle method because this occurs before new status is even added
    @Override
    protected void onPause() {
        super.onPause();

        FileWriter writer;
        File statusFile = new File(getFilesDir(), getString(R.string.status_file_name));
        BufferedWriter bWriter;
        try {
            writer = new FileWriter(statusFile);
            bWriter = new BufferedWriter(writer);
            for (String status : statusList) {
                bWriter.write(status);
                bWriter.newLine();
            }
            bWriter.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void notifyStatusDataSetChanged() {
        HeaderViewListAdapter headerViewListAdapter = (HeaderViewListAdapter) lvStatus.getAdapter();
        ((BaseAdapter) headerViewListAdapter.getWrappedAdapter()).notifyDataSetChanged();
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == ADD_STATUS_REQUEST) {
            if (resultCode == RESULT_OK) {
                String key = getString(R.string.KEY_NEW_STATUS);
                statusList.add(data.getStringExtra(key));
                notifyStatusDataSetChanged();
            }
        }
    }
}
