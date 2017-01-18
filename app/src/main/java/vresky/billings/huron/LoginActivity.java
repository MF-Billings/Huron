package vresky.billings.huron;

import android.app.Activity;
import android.os.Bundle;
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
    private User user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        db = DatabaseInterface.getInstance();
        user = User.getInstance();

        final EditText etUserId = (EditText)findViewById(R.id.et_user_id);
        final EditText etPassword = (EditText)findViewById(R.id.et_user_pwd);
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
                String loginInput = etUserId.getText().toString();
                String passwordInput = etPassword.getText().toString();

                if (loginInput.matches("\\d+")) {
                    int userId = Integer.valueOf(loginInput);

                    // returns username if authentication was successful
                    String authenticationResult = db.authUser(userId, passwordInput);
                    String errorMsg = "";
                    if (authenticationResult.equals("unauth")) {
                        errorMsg = "Incorrect login credentials";
                    } else if (authenticationResult.equals("error")) {
                        errorMsg = "An error occurred";
                    }
                    // success
                    else {
                        user.setUserId(userId);
                        user.setUsername(authenticationResult);
                        user.setStatus("");
                        setResult(RESULT_OK);
                    }
                    if (!errorMsg.isEmpty()) {
                        Toast.makeText(LoginActivity.this, errorMsg, Toast.LENGTH_SHORT).show();
                    }
                }
                // wrong input data
                else {
                    Toast.makeText(LoginActivity.this, "You must enter an integer number for user id",
                            Toast.LENGTH_SHORT).show();
                }

                finish();
            }
        });
    }
}
