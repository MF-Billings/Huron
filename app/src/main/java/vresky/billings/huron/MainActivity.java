package vresky.billings.huron;

import android.Manifest;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.places.Places;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.ArrayList;
import java.util.List;

import vresky.billings.huron.Database.DatabaseInterface;

/**
 * Created by Matt on 14/12/2016
 * polls for location updates while app is running in the foreground
 * doesn't track when phone is locked (app in foreground or not)
 */
/* NOTE Because of the inability of the emulator to regularly update the location there are certain
    issues that occur when running the app on an emulator

    - for some reason the blue dot that normally appears to indicate the user's position will not
    appear until the map coordinates are sent to the device, through the extended controls for instance.
    A result of this is that the button in the top-right corner of the map that moves the camera to
    the user's current position will not work at this point.

    - when location permission is requested upon running the app, the map does not receive the
    coordinates of the users location, and while the MyLocation button will de displayed in the
    upper right, it will not relocate the camera and marker to the user's current location

    - toggling the tracking using the action button in the toolbar removes the marker but does not
    replace it when run in the emulator
 */
public class MainActivity extends AppCompatActivity implements
        OnMapReadyCallback,
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        LocationListener {

    private static final String TAG = MainActivity.class.getSimpleName();
    public static boolean trackingIsEnabled;          // true if the user's location is currently being recorded
    public static boolean userIsLoggedIn;
    public static boolean mapUpdated;                   // prevent cases where more than 1 map function might call the same function when it's not necessary

    private final int REGISTER_REQUEST = 1;
    private final int LOGIN_REQUEST = 2;

    private User user;                  // the app user
    private SharedPreferences prefs;
    private DatabaseInterface db;
    private Menu optionsMenu;           // allow access of toolbar menu outside toolbar-specific methods
    private List<ContactMapWrapper> mapContacts = new ArrayList<>();

    // MAP FIELDS

    private GoogleMap mMap;

    // A default location (Sydney, Australia) and default zoom to use when location permission is
    // not granted.
    private final LatLng mDefaultLocation = new LatLng(43.117608, -79.246525);
    private static final int DEFAULT_ZOOM = 15;
    private static final int PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 1;
    private boolean mLocationPermissionGranted;

    // The geographical location where the device is currently located.
    private Location mCurrentLocation;
    // The entry point to Google Play services, used by the Places API and Fused Location Provider.
    private GoogleApiClient mGoogleApiClient;
    // A request object to store parameters for requests to the FusedLocationProviderApi.
    private LocationRequest mLocationRequest;
    // The desired interval for location updates. Inexact. Updates may be more or less frequent.
    private static final long UPDATE_INTERVAL_IN_MILLISECONDS = 10000;
    // The fastest rate for active location updates. Exact. Updates will never be more frequent
    // than this value.
    private static final long FASTEST_UPDATE_INTERVAL_IN_MILLISECONDS =
            UPDATE_INTERVAL_IN_MILLISECONDS / 2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        buildGoogleApiClient();
        mGoogleApiClient.connect();
        createLocationRequest();

        trackingIsEnabled = true;           // gotta do it somewhere!
        db = DatabaseInterface.getInstance();

        // determine if user is registered
        if (user == null) {
            // get user id if one exists
            prefs = getSharedPreferences(getResources().getString(R.string.APP_TAG), MODE_PRIVATE);
            int userId = prefs.getInt(getResources().getString(R.string.KEY_USER_ID), User.USER_ID_NOT_FOUND);
            // user has already registered
            if (userId == User.USER_ID_NOT_FOUND) {
                // ?
            }
        }
        else {
            populateUsersContactList();
        }
    }

    // for debugging purposes
    @Override
    protected void onStart() {
        super.onStart();
        Log.d(TAG, "In onStart");
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.d(TAG, "In onResume");
        // update map to reflect any changes in contact list
        // need this for when onMapReady is not called upon resuming the activity, ie. when an activity is displayed as a dialog
        if (mMap != null && user != null && trackingIsEnabled) {
            // TODO post info
            updateContactLocations();
            mapUpdated = true;
        }
    }

    @Override
    protected void onPause() {
        Log.d(TAG, "Before super.onPause");
        mapUpdated = false;
        super.onPause();
        Log.d(TAG, "After super.onPause");
    }

    @Override
    protected void onDestroy() {
        Log.d(TAG, "Before super.onDestroy");
        super.onDestroy();
        Log.d(TAG, "After super.onDestroy");
    }

    // MAP  ----------------------------------------------------------------------------------------

    // called when getMapAsync returns; map is ready to be used.
    @Override
    @SuppressWarnings("MissingPermission")
    public void onMapReady(GoogleMap map) {
        mMap = map;

        // someone logged in at some point when the app was in the foreground
        if (user != null) {
            user = User.getInstance();
        }
        // Turn on the My Location layer and the related control on the map.
        updateLocationUI();

        // this point should only be reached if the location tracking permission has been granted
        if (mCurrentLocation != null) {
            LatLng latLng = new LatLng(mCurrentLocation.getLatitude(), mCurrentLocation.getLongitude());    // maybe add default zoom?

            // display my-location layer  data, namely the my-location button
            // TEST
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, DEFAULT_ZOOM));
            mMap.setMyLocationEnabled(true);
            mMap.getUiSettings().setMyLocationButtonEnabled(true);

            // send location info on initialization to server and display contacts
            if (user != null && user.isRegistered() && trackingIsEnabled) {
                db.setUserInfo(latLng.latitude, latLng.longitude, mCurrentLocation.getTime(), "");
                updateContactLocations();
            }
            Log.d(TAG, "Current LatLng: " + latLng.latitude + ", " + latLng.longitude + "\n"
                + "Time: " + mCurrentLocation.getTime());
        } else {
            Log.d(TAG, "Current location is null. Using default location.");
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(mDefaultLocation, DEFAULT_ZOOM));
        }
    }

    private synchronized void buildGoogleApiClient() {
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this /* FragmentActivity */,
                        this /* OnConnectionFailedListener */)
                .addConnectionCallbacks(this)
                .addApi(LocationServices.API)
                .addApi(Places.GEO_DATA_API)
                .addApi(Places.PLACE_DETECTION_API)
                .build();
    }

    // request runtime permissions in the app
    // checks for required fine location permission, requesting it if it hasn't been granted
    private void getDeviceLocation() {
        if (ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            mLocationPermissionGranted = true;
        }
        // display rationale to user as to why location tracking is necessary and prompt user for permission
        else {
            ActivityCompat.requestPermissions(MainActivity.this,
                            new String[] {Manifest.permission.ACCESS_FINE_LOCATION},
                            PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION);
        }
        // get last-known location of device and register for location updates
        if (mLocationPermissionGranted) {
            mCurrentLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);

            if (trackingIsEnabled) {
                // enables location updates as well as setting the tracking boolean
                enableTracking();
            }
        }
    }

    // handle callback when the location changes
    @Override
    public void onLocationChanged(Location location) {
        if (trackingIsEnabled && mLocationPermissionGranted) {
            mCurrentLocation = location;
            // update location marker for user
            if (mMap != null) {
                // update server info
                Log.i(TAG, "Current LatLng: " + location.getLatitude() + ", " + location.getLongitude() + "\n"
                        + "Time: " + DateFormat.format("dd-MM-yyyy", mCurrentLocation.getTime()) + "\n"
                        +  ((user != null && user.isRegistered()) ? "User data updated server-side" : ""));
            }
        }
    }

     // Handles the result of the request for location permissions.
     // A dialog box is presented whenever any App requests permissions. When the user responds,
     // the system invokes app’s onRequestPermissionsResult() method, passing it the user response.
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String permissions[], @NonNull int[] grantResults) {
        mLocationPermissionGranted = false;
        switch (requestCode) {
            case PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION: {
                // if request is cancelled then the result arrays are empty
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    mLocationPermissionGranted = true;
                }
            }
        }
        updateLocationUI();
    }

    // get map fragment handle and register for the map callback
    // called when the activity is resumed
    @Override
    public void onConnected(Bundle connectionHint) {
        getDeviceLocation();
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }

    /**
     * Handles failure to connect to the Google Play services client.
     */
    @Override
    public void onConnectionFailed(@NonNull ConnectionResult result) {
        // Refer to the reference doc for ConnectionResult to see what error codes might
        // be returned in onConnectionFailed.
        Log.d(TAG, "Play services connection failed: ConnectionResult.getErrorCode() = "
                + result.getErrorCode());
    }

    /**
     * Handles suspension of the connection to the Google Play services client.
     */
    @Override
    public void onConnectionSuspended(int cause) {
        Log.d(TAG, "Play services connection suspended");
    }

    // set location controls on the map
    @SuppressWarnings("MissingPermission")      // mLocationPermissionGranted handles permission
    private void updateLocationUI() {
        if (mMap == null) {
            return;
        }
        // if the user has granted location permission, enable the My Location layer and control on the map, otherwise disable them and set the current location to null:
        if (mLocationPermissionGranted) {
            mMap.setMyLocationEnabled(true);
            mMap.getUiSettings().setMyLocationButtonEnabled(true);
        } else {
            mMap.setMyLocationEnabled(false);                           // gives warning w/o suppression
            mMap.getUiSettings().setMyLocationButtonEnabled(false);
            mCurrentLocation = null;
        }
    }

    private void createLocationRequest() {
        mLocationRequest = new LocationRequest();

        // Sets the desired interval for active location updates.
        // This interval is inexact. You may not receive updates at all if no location sources are available, or
        // you may receive them slower than requested. You may also receive updates faster than
        // requested if other applications are requesting location at a faster interval.
        mLocationRequest.setInterval(UPDATE_INTERVAL_IN_MILLISECONDS);
        // application will never receive updates faster than this value
        mLocationRequest.setFastestInterval(FASTEST_UPDATE_INTERVAL_IN_MILLISECONDS);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
    }

    private void populateUsersContactList() {
        List<Contact> contactsList = Helper.retrieveContacts(TAG, user, db);

        if (user.getContacts().isEmpty()) {
            for (Contact c : contactsList) {
                user.getContacts().add(c);
            }
        } else {
            Log.d(TAG, "User contacts list was expected to be empty on app load but contained contacts");
        }
    }

    /**
     * Update the contact data displayed on the map.
     */
    private void updateContactLocations() {
        if (mMap != null) {
            String markerSnippetString;
            // remove any contacts that might be stored which do not belong to the user
            for (ContactMapWrapper wrapper : mapContacts) {
                wrapper.wipe();
            }
            mapContacts.clear();

            // retrieve contacts list to display on map
            user.setContacts(Helper.retrieveContacts(TAG, user, db));
            List<Contact> contactsList = user.getContacts();
            for (Contact c : contactsList) {
                mapContacts.add(new ContactMapWrapper(c));
            }
            // display contacts location to the user on the map
            for (ContactMapWrapper c : mapContacts) {
                LatLng contactLatLng = new LatLng(c.getContact().getLocation().getLatitude(),
                        c.getContact().getLocation().getLongitude());
                c.setMarker(mMap.addMarker(new MarkerOptions()
                        .position(contactLatLng)
                        .title(c.getContact().getName())
                ));

                markerSnippetString = "";
                // include contact status, if one exists
                if (!c.getContact().getStatus().equals("")) {
                    markerSnippetString = c.getContact().getStatus() + " - ";
                }
                // add the time the contact data was last updated to the snippet
                // convert time in milliseconds to human-readable format
                String timeOfLastUpdate = (String)DateFormat.format("HH:mm", c.getContact().getLocation().getTime());
                markerSnippetString += "last updated at " + timeOfLastUpdate;

                c.getMarker().setSnippet(markerSnippetString);
            }
        }
    }

    // METHODS -------------------------------------------------------------------------------------

    @SuppressWarnings("MissingPermission")
    private void disableTracking() {
        LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient, this);
        trackingIsEnabled = false;
        // hide contact information from user (friendship's a 2-way street!)
        for (ContactMapWrapper w : mapContacts) {
            w.wipe();
        }

        Log.i(TAG, getResources().getString(R.string.DEBUG_TRACKING_STATUS,
                (trackingIsEnabled) ? "enabled" : "disabled"));
    }

    @SuppressWarnings("MissingPermission")
    private void enableTracking() {
        if (mLocationPermissionGranted) {
            LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this);
            // update user's location when tracking is re-enabled to try and
            // discourage position anonymity created by toggling the tracking control without moving

            // show contact information on map
            // used when tracking is enabled outside of initial app start-up
            if (mMap != null) {
                mCurrentLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);

                if (user != null) {
                    db.setUserInfo(mCurrentLocation.getLatitude(), mCurrentLocation.getLongitude(),
                            mCurrentLocation.getTime(), user.getStatus());
                    updateContactLocations();
                }
            }
            trackingIsEnabled = true;
            Log.i(TAG, getResources().getString(R.string.DEBUG_TRACKING_STATUS,
                    (trackingIsEnabled) ? "enabled" : "disabled"));
        }
    }

    private void logout() {
        userIsLoggedIn = false;
        user = null;
        // clear all contact info from mapContacts and remove markers
        for (ContactMapWrapper w : mapContacts) {
            w.wipe();
        }
        mapContacts.clear();
        disableTracking();
        updateOptionsMenu();
    }

    private void login() {
        user = User.getInstance();
        userIsLoggedIn = true;
        updateContactLocations();
        enableTracking();
        updateOptionsMenu();
        Toast.makeText(this, "Logged in as " + user.getUsername(), Toast.LENGTH_SHORT).show();
    }

    private void updateOptionsMenu() {
        if (optionsMenu != null) {
            MenuItem manageContactsAction = optionsMenu.findItem(R.id.action_manage_contacts);
            MenuItem updateStatusAction = optionsMenu.findItem(R.id.action_update_status);
            MenuItem logoutAction = optionsMenu.findItem(R.id.action_logout);
            MenuItem updateMapAction = optionsMenu.findItem(R.id.action_update_map);
            MenuItem registerAction = optionsMenu.findItem(R.id.action_register);
            MenuItem loginAction = optionsMenu.findItem(R.id.action_login);
            MenuItem getInfoAction = optionsMenu.findItem(R.id.action_get_user_info);

            if (userIsLoggedIn) {
                manageContactsAction.setVisible(true);
                updateStatusAction.setVisible(true);
                logoutAction.setVisible(true);
                updateMapAction.setVisible(true);
                getInfoAction.setVisible(true);
                registerAction.setVisible(false);
                loginAction.setVisible(false);

            }
            else {
                manageContactsAction.setVisible(false);
                updateStatusAction.setVisible(false);
                logoutAction.setVisible(false);
                updateMapAction.setVisible(false);
                // DEBUG
//                getInfoAction.setVisible(false);
                registerAction.setVisible(true);
                loginAction.setVisible(true);
            }
        }
    }
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REGISTER_REQUEST) {
            if (resultCode == RESULT_OK) {
                // successful registration
                // remove registration action
                // DEBUG put this back when testing requiring frequent use of different users is done
//                if (optionsMenu != null) {
//                    MenuItem item = optionsMenu.findItem(R.id.action_register);
//                    item.setVisible(false);
//                    invalidateOptionsMenu();
//                }
            }
        } else if (requestCode == LOGIN_REQUEST) {
            if (resultCode == RESULT_OK) {
                login();
            }
        }
    }

    // MENU ----------------------------------------------------------------------------------------

    // display toolbar
    public boolean onCreateOptionsMenu(final Menu menu) {
        optionsMenu = menu;
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main_menu, menu);
        // hide registration option if an existing user id is stored
        // DEBUG remove
        if (user != null && user.isRegistered()) {
//            MenuItem item = menu.findItem(R.id.action_register);
//            item.setVisible(false);
//            invalidateOptionsMenu();
        }
        optionsMenu = menu;
        updateOptionsMenu();
        return true;
    }

    @SuppressWarnings("MissingPermission")
    public boolean onOptionsItemSelected(MenuItem item) {
        boolean result = true;
        Intent intent;
        switch (item.getItemId()) {
            // the eye button
            case R.id.action_toggle_tracking_visibility:
                // toggle user location tracking
                if (trackingIsEnabled) {
                    // disable location tracking
                    item.setIcon(R.drawable.ic_visibility_off_black_24dp);
                    disableTracking();
                } else {
                    item.setIcon(R.drawable.ic_visibility_black_24dp);
                    enableTracking();
                }
                Toast.makeText(this, "Tracking " + ((trackingIsEnabled) ? "enabled" : "disabled"),
                        Toast.LENGTH_SHORT).show();
                break;
            case R.id.action_update_map:
                // get the most recent contact data and update the server-side user data
                db.setUserInfo(mCurrentLocation.getLatitude(), mCurrentLocation.getLongitude(),
                    mCurrentLocation.getTime(), user.getStatus());
                updateContactLocations();
                break;
            case R.id.action_manage_contacts:
                intent = new Intent(this, ManageContactsActivity.class);
                startActivity(intent);
                break;
            case R.id.action_update_status:
                intent = new Intent(this, UpdateStatusActivity.class);
                startActivity(intent);
                break;
            case R.id.action_register:
                // only allow registration if user doesn't have a user id
//                if (!user.isRegistered()) {
                    intent = new Intent(this, RegistrationActivity.class);
                    startActivityForResult(intent, REGISTER_REQUEST);
//                }
                break;
            case R.id.action_login:
                intent = new Intent(this, LoginActivity.class);
                startActivityForResult(intent, LOGIN_REQUEST);
                break;
            case R.id.action_get_user_info:
                if (user != null) {
                    Toast.makeText(MainActivity.this, ((user == null) ? "You are not currently logged in" : user.toString()), Toast.LENGTH_LONG).show();
                    Log.d(TAG, user.toString());
                }
                break;
            case R.id.action_logout:
                Log.d(TAG, "Logged in user:\n" + ((user == null) ? "null" : user.toString()));
                if (user != null) {
                    logout();
                    Toast.makeText(MainActivity.this, "You are now logged out", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(MainActivity.this, "You have already logged out", Toast.LENGTH_SHORT).show();
                }
                break;
            default:
                result = false;
                break;
        }
        return result;
    }
}
