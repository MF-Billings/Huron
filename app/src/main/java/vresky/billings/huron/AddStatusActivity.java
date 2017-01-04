package vresky.billings.huron;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

// TODO check that new status doesn't exist in the list already
/**
 * Created by Matt on 22/12/2016.
 * Allows user to create a custom status
 * displays activity as a dialog via dialog theme in AndroidManifest.xml
 */
public class AddStatusActivity extends Activity {

    // keep track of how many characters the user has available for their status
    private int charsRemaining;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_status);

        // begin with the full character amount
        charsRemaining = getResources().getInteger(R.integer.STATUS_CHARACTER_LIMIT);

        EditText etStatus = (EditText) findViewById(R.id.et_add_status_new_status);
        final TextView tvCharLimit = (TextView) findViewById(R.id.add_status_tv_char_limit);
        Button btnCancel = (Button) findViewById(R.id.btn_add_status_cancel);
        Button btnSave = (Button) findViewById(R.id.btn_add_status_save);

        // initialize tv with proper data
        tvCharLimit.setText(getResources().getString(R.string.characters_remaining_message,
                charsRemaining));

        // LISTENERS

        // update character limit displayed to user when status text is changed
        etStatus.addTextChangedListener(new TextWatcher() {
            private int oldStringLength = -1;
            private int newStringLength = -1;
            // the count characters at start are about to be replaced by new text with length after
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                oldStringLength = s.length();
            }

            // the count characters beginning at start have just replaced old text that had length before
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                newStringLength = s.length();
                charsRemaining -= (newStringLength - oldStringLength);
                tvCharLimit.setText(getResources().getString(R.string.characters_remaining_message,
                        charsRemaining));
            }

            @Override
            public void afterTextChanged(Editable s) { }
        });
        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EditText et = (EditText) findViewById(R.id.et_add_status_new_status);
                String newStatus = et.getText().toString();

                Intent returnIntent = new Intent();

                // status is invalid
                if (newStatus.isEmpty()) {
                    returnIntent.putExtra(getResources().getString(R.string.KEY_INVALID_STATUS_EDIT),
                            getResources().getString(R.string.invalid_status_edit_message_empty));
                    setResult(RESULT_CANCELED, returnIntent);
                }
                // status is valid
                else {
                    returnIntent.putExtra(getResources().getString(R.string.KEY_NEW_STATUS), newStatus);
                    setResult(RESULT_OK, returnIntent);
                }
                finish();
            }
        });
    }
}
