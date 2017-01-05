package vresky.billings.huron;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

/**
 * Created by Matt on 04/01/2017.
 * Includes settings and preferences the user may wish to modify
 */
public class SettingsActivity extends AppCompatActivity {

    static int zoom_level;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

//        Button btnCancel = (Button) findViewById(R.id.settings__btn_cancel);
//        Button btnSave = (Button) findViewById(R.id.settings_btn_save);
//        Spinner zoomSpinner = (Spinner) findViewById(R.id.settings_spnr_zoom_level);
//
//        ArrayAdapter<CharSequence> zoomAdapter = ArrayAdapter.createFromResource(this, R.array.settings_zoom_level, android.R.layout.simple_spinner_item);
//
//        // LISTENERS
//
//        btnCancel.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                finish();
//            }
//        });
//
//        btnSave.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
////                zoom_level =
//            }
//        });
    }


}
