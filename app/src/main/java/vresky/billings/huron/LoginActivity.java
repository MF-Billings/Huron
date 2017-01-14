package vresky.billings.huron;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;

import vresky.billings.huron.Database.DatabaseInterface;

/**
 * Created by Matt on 11/01/2017.
 */
public class LoginActivity extends Activity {

    public static final String TAG = LoginActivity.class.getSimpleName();
    private DatabaseInterface db = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        db = DatabaseInterface.getInstance();

        final EditText etUserId = (EditText)findViewById(R.id.et_user_id);
        final CheckBox chkRememberMe = (CheckBox)findViewById(R.id.chk_remember_me);
        Button btnLogin = (Button)findViewById(R.id.btn_login);
        Button btnCancel = (Button)findViewById(R.id.btn_cancel);

        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        // TODO test
        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO set userAccount with result from server query
                User userAccount = new User();
                int userId = Integer.valueOf(etUserId.getText().toString());
//                DatabaseInterface db = new DatabaseInterface(userId, "GNDN");
                // store user info if checkbox is checked
                boolean userWithGivenIdExist = true;
                if (userWithGivenIdExist) {
                    if (chkRememberMe.isChecked()) {
                        // store user data
//                    SharedPreferences prefs = getSharedPreferences(
//                            getResources().getString(R.string.APP_TAG), MODE_PRIVATE);
//                    SharedPreferences.Editor prefsEditor = prefs.edit();
//                    prefsEditor.putInt(getResources().getString(R.string.KEY_USER_ID), userAccount.getUserId());
//                    prefsEditor.putString(getResources().getString(R.string.KEY_USERNAME), userAccount.getUsername());
//                    prefsEditor.apply();
//                    Log.i(TAG, "user with id " + userAccount.getUserId() + "added");
                    }
                    // Tycho id = 34 - has no position at this point
                    // TestAdd id = 42
                    // some1 w/ above as contacts id = 43 at 43.1209, -79.2504 location??
                    // 51 has TestAdd at Brock as contact
                    //user = new User(51, "TheLofts51", "test status");
                    // DEBUG for testing different user's without server support
                    User user = null;
                    switch (userId) {
                        case 34:
                            user = new User(userId, "Tycho", "");
                            break;
                        case 42:
                            user = new User(userId, "TestAdd", "");
                            break;
                        case 43:
                            user = new User(userId, "Watcher", "");
                            break;
                    }

                    if (user != null) {
                        Intent intent = new Intent();
                        intent.putExtra(getResources().getString(R.string.KEY_USER), user);
                        setResult(RESULT_OK, intent);
                        Log.i(TAG, String.format("Logged in as user %s with id %d", user.getUsername(), user.getUserId()));
                    }
                }
                finish();
            }
        });
    }
}
