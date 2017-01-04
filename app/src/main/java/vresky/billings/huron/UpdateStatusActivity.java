package vresky.billings.huron;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
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

    private static final String TAG = UpdateStatusActivity.class.getSimpleName();
    private static final int ADD_STATUS_REQUEST = 1;

    private List<String> statusList;
    private RecyclerView rvStatus;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_status);

        statusList = new ArrayList<>();
        rvStatus = (RecyclerView) findViewById(R.id.rv_statuses);
        Button btnAddStatus = (Button) findViewById(R.id.btn_add_status);

        // NOTE this will be removed in the next commit. Kept temporarily as reference
        // listview code
//        TextView statusListHeader = new TextView(this);
//        statusListHeader.setTextSize(TypedValue.COMPLEX_UNIT_SP,
//                getResources().getInteger(R.integer.LISTVIEW_HEADER_FONT_SIZE));
//        statusListHeader.setText(getResources().getString(R.string.statuses_literal));

        //rvStatus.addHeaderView(statusListHeader);

        // listview code
//        final ArrayAdapter<String> statusAdapter = new ArrayAdapter<>(this, R.layout.standard_list_item,
//                R.id.std_list_item_tv_message, statusList);
//        final StandardListAdapter statusAdapter = new StandardListAdapter(this, R.layout.standard_list_item,
//                R.id.std_list_item_tv_message, statusList);d
//        lvStatus.setAdapter(statusAdapter);

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
            statusList = Arrays.asList(defaultStatusList);
            Toast.makeText(UpdateStatusActivity.this, "Status file not found. Using defaults.", Toast.LENGTH_SHORT).show();
        } catch (Exception e) {
            e.printStackTrace();
        }

        rvStatus.setLayoutManager(new LinearLayoutManager(this));
        StatusRecyclerAdapter statusRecyclerAdapter = new StatusRecyclerAdapter(rvStatus, statusList);
        rvStatus.setAdapter(statusRecyclerAdapter);
        statusRecyclerAdapter.itemTouchHelper.attachToRecyclerView(rvStatus);
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
            int adapterSize = rvStatus.getAdapter().getItemCount();
            StatusRecyclerAdapter statusAdapter = (StatusRecyclerAdapter) rvStatus.getAdapter();
            for (int i = 0; i < adapterSize; i++) {
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
                StatusRecyclerAdapter statusAdapter = (StatusRecyclerAdapter)rvStatus.getAdapter();
                statusAdapter.addStatus(data.getStringExtra(key));
                statusAdapter.notifyItemInserted(statusAdapter.getItemCount() - 1);
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
        }
    }
    // NOTE this will be removed in the next commit. Kept temporarily as reference
//    private void setupItemTouchHelper() {
//        // callback listens to move and swipe events
//        final ItemTouchHelper.SimpleCallback itemTouchCallback = new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {
//
//            // we want to cache these and not allocate anything repeatedly in the onChildDraw method
//            Drawable background;
//            Drawable xMark;
//            int xMarkMargin;
//            boolean initiated;

//            private void init() {
//                background = new ColorDrawable(Color.RED);
//                xMark = ContextCompat.getDrawable(MainActivity.this, R.drawable.ic_clear_24dp);
//                xMark.setColorFilter(Color.WHITE, PorterDuff.Mode.SRC_ATOP);
//                xMarkMargin = (int) MainActivity.this.getResources().getDimension(R.dimen.ic_clear_margin);
//                initiated = true;
//            }

            // for drag & drop - not used
//            @Override
//            public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
//                return false;
//            }

//            @Override
//            public int getSwipeDirs(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder) {
//                int position = viewHolder.getAdapterPosition();
//                StatusRecyclerAdapter adapter = (StatusRecyclerAdapter)recyclerView.getAdapter();
//                if (StatusRecyclerAdapter.isUndoOn() && StatusRecyclerAdapter.isPendingRemoval(position)) {
//                    return 0;
//                }
//                return super.getSwipeDirs(recyclerView, viewHolder);
//            }

//            @Override
//            public void onSwiped(RecyclerView.ViewHolder viewHolder, int swipeDir) {
//                int swipedPosition = viewHolder.getAdapterPosition();
//                StatusRecyclerAdapter adapter = (StatusRecyclerAdapter) rvStatus.getAdapter();
//                boolean undoOn = adapter.isUndoOn();
//                if (undoOn) {
//                    adapter.pendingRemoval(swipedPosition);
//                } else {
//                    adapter.remove(swipedPosition);
//                }
//                adapter.remove(swipedPosition);
//            }


//            @Override
//            public void onChildDraw(Canvas c, RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, float dX, float dY, int actionState, boolean isCurrentlyActive) {
//                super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);
//                View itemView = viewHolder.itemView;
//
//                // not sure why, but this method get's called for viewholder that are already swiped away
//                if (viewHolder.getAdapterPosition() == -1) {
//                    // not interested in those
//                    return;
//                }
//
//                if (!initiated) {
//                    init();
//                }
//
//                // draw red background
//                background.setBounds(itemView.getRight() + (int) dX, itemView.getTop(), itemView.getRight(), itemView.getBottom());
//                background.draw(c);

//                // draw x mark
//                int itemHeight = itemView.getBottom() - itemView.getTop();
//                int intrinsicWidth = xMark.getIntrinsicWidth();
//                int intrinsicHeight = xMark.getIntrinsicWidth();
//
//                int xMarkLeft = itemView.getRight() - xMarkMargin - intrinsicWidth;
//                int xMarkRight = itemView.getRight() - xMarkMargin;
//                int xMarkTop = itemView.getTop() + (itemHeight - intrinsicHeight)/2;
//                int xMarkBottom = xMarkTop + intrinsicHeight;
//                xMark.setBounds(xMarkLeft, xMarkTop, xMarkRight, xMarkBottom);
//
//                xMark.draw(c);
//            }
//        };
//        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(itemTouchCallback);
//        itemTouchHelper.attachToRecyclerView(rvStatus);
//    }
}
