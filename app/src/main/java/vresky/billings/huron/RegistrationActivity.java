package vresky.billings.huron;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
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

    public final String TAG = this.getClass().getSimpleName();
    boolean registrationIsSuccessful = false;
    User user;
    DatabaseInterface db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        final EditText etUsername = (EditText)findViewById(R.id.et_username);
        Button btnCancel = (Button)findViewById(R.id.btn_cancel);
        Button btnRegister = (Button)findViewById(R.id.btn_register);
        TextView tvCharLimit = (TextView)findViewById(R.id.tv_char_limit) ;

        // unmarshall intent extras
        Object obj = getIntent().getSerializableExtra(getResources().getString(R.string.KEY_DB_INTERFACE_OBJ));
        if (obj instanceof DatabaseInterface) {
            db = (DatabaseInterface)obj;
        } else {
            Log.e(TAG, Thread.currentThread().getStackTrace()[0] + "Object cannot be cast to DatabaseInterface");
        }
        obj = getIntent().getSerializableExtra(getResources().getString(R.string.KEY_USER));
        if (obj instanceof User) {
            user = (User)obj;
        } else {
            Log.e(TAG, Thread.currentThread().getStackTrace()[0] + "Object cannot be cast to User");
        }

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
                setResult(RESULT_CANCELED);
                finish();
            }
        });

        btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String userName = etUsername.getText().toString();
                Toast.makeText(RegistrationActivity.this, "register btn", Toast.LENGTH_SHORT).show();

                // add user to database if the input is valid
                if (!userName.isEmpty()) {
                    String result = db.addUser(userName, "GNDN");
                    if (result.equals("error")) {
                        StackTraceElement[] stackTraceElements = Thread.currentThread().getStackTrace();
                        Log.e(TAG, "User could not be added to database" + stackTraceElements.toString());
                    } else {
                        // DEBUG when commented
                        user.setUserId(Integer.valueOf(result));
                        // store user data
                        SharedPreferences prefs = getSharedPreferences(
                                getResources().getString(R.string.APP_TAG), MODE_PRIVATE);
                        SharedPreferences.Editor prefsEditor = prefs.edit();
                        prefsEditor.putInt(getResources().getString(R.string.KEY_USER_ID), user.getUserId());
                        prefsEditor.putString(getResources().getString(R.string.KEY_USERNAME), userName);
                        prefsEditor.apply();
                        registrationIsSuccessful = true;
                    }
                } else {
                    Toast.makeText(RegistrationActivity.this, "Cannot leave an empty username", Toast.LENGTH_SHORT).show();
                }

                if (registrationIsSuccessful) {
                    // return user object
                    Intent intent = new Intent();
                    intent.putExtra(getResources().getString(R.string.KEY_USER), user);
                    setResult(RESULT_OK, intent);
                }
                finish();
            }
        });
    }
}
