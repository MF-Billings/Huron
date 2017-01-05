package vresky.billings.huron;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

/**
 * Created by Matt on 04/01/2017.
 * Includes settings and preferences the user may wish to modify
 */
// TODO display actual userId
public class SettingsActivity extends AppCompatActivity {

    static int zoom_level;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        Button btnCancel = (Button) findViewById(R.id.btn_cancel);
        Button btnSave = (Button) findViewById(R.id.btn_save);
        Button btnGetId = (Button) findViewById(R.id.btn_get_user_id);
//        Spinner zoomSpinner = (Spinner) findViewById(R.id.settings_spnr_zoom_level);

//        ArrayAdapter<CharSequence> zoomAdapter = ArrayAdapter.createFromResource(this, R.array.settings_zoom_level, android.R.layout.simple_spinner_item);

        // LISTENERS

        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                zoom_level =
            }
        });

        // display app users id to them
        btnGetId.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String userId = "user id goes here";
                Toast.makeText(SettingsActivity.this, userId, Toast.LENGTH_SHORT).show();
            }
        });
    }


}
