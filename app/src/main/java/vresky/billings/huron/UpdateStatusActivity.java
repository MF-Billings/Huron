package vresky.billings.huron;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.TypedValue;
import android.view.View;
import android.widget.Button;
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

/**
 * Created by Matt on 21/12/2016.
 */

public class UpdateStatusActivity extends AppCompatActivity {

    private static final int ADD_STATUS_REQUEST = 1;

    private List<String> statusList;
    private StatusAdapter statusAdapter;            // easily call adapter notify functions
    private ListView lvStatus;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_status);

        statusList = new ArrayList<>();
        lvStatus = (ListView) findViewById(R.id.lv_statuses);
        Button btnAddStatus = (Button) findViewById(R.id.btn_add_status);

        TextView statusListHeader = new TextView(this);
        statusListHeader.setTextSize(TypedValue.COMPLEX_UNIT_SP,
                getResources().getInteger(R.integer.LISTVIEW_HEADER_FONT_SIZE));
        statusListHeader.setText(getResources().getString(R.string.statuses_literal));

        statusAdapter = new StatusAdapter(this, statusList);
        lvStatus.addHeaderView(statusListHeader);
        lvStatus.setAdapter(statusAdapter);

        // LISTENERS

        btnAddStatus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), AddStatusActivity.class);
                startActivityForResult(intent, ADD_STATUS_REQUEST);
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
            // DEBUG test list re-population with defaults
            if (statusList.isEmpty()) {
                throw new FileNotFoundException();
            }
            bReader.close();
        } catch (FileNotFoundException e) {
            // populate list with default statuses
            String[] defaultStatusList = getResources().getStringArray(R.array.default_status_array);
            statusList.addAll(Arrays.asList(defaultStatusList));
            Toast.makeText(UpdateStatusActivity.this, "Status file not found. Using defaults.", Toast.LENGTH_SHORT).show();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            statusAdapter.notifyDataSetChanged();
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
            // convert the adapter that contains the modified status list into a list
            int adapterSize = lvStatus.getAdapter().getCount();
            // need to reduce adapter size by 1 to account for header
            for (int i = 0; i < adapterSize - 1; i++) {
                String status = statusAdapter.getStatus(i);
                bWriter.write(status);
                bWriter.newLine();
            }
            bWriter.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // used with startActivityForResult
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == ADD_STATUS_REQUEST) {
            if (resultCode == RESULT_OK) {
                String key = getString(R.string.KEY_NEW_STATUS);
                statusList.add(data.getStringExtra(key));
                statusAdapter.notifyDataSetChanged();
            } else if (resultCode == RESULT_CANCELED) {
                // status is empty
                String errorMsg = "";
                if (data != null) {
                    errorMsg = data.getStringExtra(getResources().getString(R.string.KEY_INVALID_STATUS_EDIT));
                }

                if (errorMsg != null && !errorMsg.isEmpty()) {
                    Toast.makeText(UpdateStatusActivity.this, errorMsg, Toast.LENGTH_SHORT).show();
                }
            }
        } else if (requestCode == getResources().getInteger(R.integer.MODIFY_STATUS_REQUEST)) {
            if (resultCode == RESULT_OK) {
                String key = getString(R.string.KEY_NEW_STATUS);
                int itemPosition = data.getIntExtra(getResources().getString(R.string.KEY_ITEM_POSITION), -1);

                if (itemPosition != -1) {
                    statusList.set(itemPosition, data.getStringExtra(key));
                    statusAdapter.notifyDataSetChanged();
                }
            } else if (resultCode == RESULT_CANCELED) {

            }
        }
    }
}
