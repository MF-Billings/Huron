package vresky.billings.huron;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

import vresky.billings.huron.Database.DatabaseInterface;

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

        db = DatabaseInterface.getInstance();
        user = User.getInstance();

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

                // add user to database if the input is valid
                if (!userName.isEmpty()) {
                    String result = db.addUser(userName, "GNDN");

                    // result should return an integer id if the statement was successful
                    if (result.matches("\\d+")) {
                        // DEBUG
                        // equivalent to a session-only login
                        if (user.isRegistered()) {
                            user.setUserId(Integer.valueOf(result));
                            user.setStatus("test status");
                            user.setUsername(userName);
                            user.setContacts(new ArrayList<Contact>());
                        }
                        // store user data
//                        SharedPreferences prefs = getSharedPreferences(
//                                getResources().getString(R.string.APP_TAG), MODE_PRIVATE);
//                        SharedPreferences.Editor prefsEditor = prefs.edit();
//                        prefsEditor.putInt(getResources().getString(R.string.KEY_USER_ID), user.getUserId());
//                        prefsEditor.putString(getResources().getString(R.string.KEY_USERNAME), userName);
//                        prefsEditor.apply();
                        registrationIsSuccessful = true;
                        Log.i(TAG, "user with id " + user.getUserId() + " added");
                    } else {
                        StackTraceElement[] stackTraceElements = Thread.currentThread().getStackTrace();
                        Log.e(TAG, "User could not be added to database" + stackTraceElements.toString());
                    }
                } else {
                    Toast.makeText(RegistrationActivity.this, "Cannot leave an empty username", Toast.LENGTH_SHORT).show();
                }

                if (registrationIsSuccessful) {
                    // indicate the registration was successful
                    setResult(RESULT_OK);
                }
                finish();
            }
        });
    }
}
