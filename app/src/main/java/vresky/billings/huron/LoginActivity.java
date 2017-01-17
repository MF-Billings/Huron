package vresky.billings.huron;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

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
//        final CheckBox chkRememberMe = (CheckBox)findViewById(R.id.chk_remember_me);
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

                // store user info if checkbox is checked

                boolean userWithGivenIdExist = true;
                if (userWithGivenIdExist) {
                    // JJ id 1
                    // Bj id 2
                    // TheLofts id 4
                    // DEBUG for testing different user's without server support
                    User user = null;

                    // client-side login for demonstrative purposes
//                    int maxNumberOfAccounts = 4;
//                    String[] userNames = new String[maxNumberOfAccounts];
//                    for (int i = 0; i < maxNumberOfAccounts; i++) {
//                        userNames[i] = "PLACEHOLDER!";
//                    }
//                    for (int i = 0; i < maxNumberOfAccounts; i++) {
//                        if (userId == i) {
//                            user.setUserId(i);
//                            user.setUsername(userNames[i]);
//                            user.setStatus("");
//                            user.setContacts(new ArrayList<Contact>());
//                            break;
//                        }
//                    }
                    switch (userId) {
                        case 1:
                            user = new User(userId, "JJ", "chillin'");
                            break;
                        case 6:
                            user = new User(userId, "Ken", "staying calm");
                            break;
                        case 20:
                            user = new User(userId, "20", "following myself");
                            break;
                    }

                    if (user != null) {
                        setResult(RESULT_OK);
                        Log.i(TAG, String.format("Logged in as user %s with id %d", user.getUsername(), user.getUserId()));
                    } else {
                        // display message to user that no one with that account number could be found
                        Toast.makeText(LoginActivity.this, "user with id " + userId + " does not exist", Toast.LENGTH_SHORT).show();
                    }
                }
                finish();
            }
        });
    }
}
