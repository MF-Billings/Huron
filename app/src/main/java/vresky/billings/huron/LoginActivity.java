package vresky.billings.huron;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;

/**
 * Created by Matt on 11/01/2017.
 */
public class LoginActivity extends Activity {

    public static final String TAG = LoginActivity.class.getSimpleName();
    DatabaseInterface db = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

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
            }
        });
    }
}
