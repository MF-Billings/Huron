package vresky.billings.huron;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

/**
 * Created by Matt on 04/01/2017.
 * Includes widget to return user id
 */
// TODO display actual userId
public class SettingsActivity extends AppCompatActivity {

    private User user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        user = (User)getIntent().getSerializableExtra(getResources().getString(R.string.KEY_USER));

        Button btnCancel = (Button) findViewById(R.id.btn_cancel);
        Button btnSave = (Button) findViewById(R.id.btn_save);
        Button btnGetId = (Button) findViewById(R.id.btn_get_user_id);

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
                String result = "You are not yet registered";
                if (user != null && user.isRegistered()) {
                    result = String.valueOf(user.getUserId());
                }

                Toast.makeText(SettingsActivity.this, result, Toast.LENGTH_SHORT).show();
            }
        });
    }
}
