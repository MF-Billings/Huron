package vresky.billings.huron;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

// TODO add code that informs user how close they are to the character limit for their new status
// TODO check that new status doesn't exist in the list already
/**
 * Created by Matt on 22/12/2016.
 * Allows user to create a custom status
 * displays activity as a dialog via dialog theme in AndroidManifest.xml
 */
public class AddStatusActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_status);

        Button btnCancel = (Button) findViewById(R.id.btn_add_status_cancel);
        Button btnSave = (Button) findViewById(R.id.btn_add_status_save);

        // LISTENERS

        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        // TODO implement actual code
        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                Toast.makeText(AddStatusActivity.this, "status saved", Toast.LENGTH_SHORT).show();
//                finish();
                EditText et = (EditText) findViewById(R.id.et_add_status_new_status);
                String newStatus = et.getText().toString();

                // check to make sure an identical status isn't in the list already

                Intent intent = new Intent();
                intent.putExtra(getResources().getString(R.string.KEY_NEW_STATUS), newStatus);
                finishWithResult(intent);
            }
        });
    }

    private void finishWithResult(Intent intent) {
        setResult(RESULT_OK, intent);
        finish();
    }
}
