package vresky.billings.huron;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

/**
 * Created by Matt on 14/12/2016.
 */
public class MainActivity extends AppCompatActivity {

    public boolean trackingIsEnabled = true;          // true if the user's location is currently being recorded

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // doesn't work
//        MapsFragment mapsFragment = new MapsFragment();
//        getSupportFragmentManager().beginTransaction()
//                .add(R.id.fragment_container, mapsFragment).commit();
    }

    // MENU ----------------------------------------------------------------------------------------

    // display toolbar
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main_menu, menu);
        return true;
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        boolean result = true;
        Intent intent;
        switch (item.getItemId()) {
            case R.id.action_manage_contacts:
                intent = new Intent(this, ManageContactsActivity.class);
                startActivity(intent);
                break;
            case R.id.action_toggle_tracking_visibility:
                if (trackingIsEnabled) {
                    item.setIcon(R.drawable.ic_visibility_off_black_24dp);
                } else {
                    item.setIcon(R.drawable.ic_visibility_black_24dp);
                }
                trackingIsEnabled = !trackingIsEnabled;
                break;
            case R.id.action_update_status:
                intent = new Intent(this, UpdateStatusActivity.class);
                startActivity(intent);
                break;
            default:
                result = false;
                break;
        }
        return result;
    }
}
