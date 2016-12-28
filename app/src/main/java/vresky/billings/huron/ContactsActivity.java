package vresky.billings.huron;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

/**
 * Created by Matt on 20/12/2016.
 * single fragment activity to contain the AddContactFragment
 */
public class ContactsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contact);
    }
}
