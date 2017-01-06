package vresky.billings.huron;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

/**
 * Created by Matt on 05/01/2017.
 * Create an account to use the app. Required to add user data to the server
 */
public class RegistrationActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        final EditText etUsername = (EditText)findViewById(R.id.et_username);
        Button btnCancel = (Button)findViewById(R.id.btn_cancel);
        Button btnRegister = (Button)findViewById(R.id.btn_register);
        TextView tvCharLimit = (TextView)findViewById(R.id.tv_char_limit) ;

        // initialize character limit message
        tvCharLimit.setText(getResources().getString(R.string.characters_remaining_message,
                getResources().getInteger(R.integer.USERNAME_CHARACTER_LIMIT)));

        etUsername.addTextChangedListener(new CharacterLimitNotifier(
                this,
                getResources().getInteger(R.integer.USERNAME_CHARACTER_LIMIT),
                tvCharLimit)
        );

        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String userName = etUsername.getText().toString();
                Toast.makeText(RegistrationActivity.this, "register btn", Toast.LENGTH_SHORT).show();

                if (!userName.isEmpty()) {

                } else {
                    Toast.makeText(RegistrationActivity.this, "Cannot leave an empty username", Toast.LENGTH_SHORT).show();
                }
                finish();
            }
        });
    }
}
