package vresky.billings.huron;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
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
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

/**
 * Created by Matt on 14/12/2016
 * polls for location updates while app is running in the foreground
 * doesn't track when phone is locked (app in foreground or not)
 */
/* NOTE Because of the inability of the emulator to regularly update the location there are certain
    issues that occur when running the app on an emulator

    - when location permission is requested upon running the app, the map does not receive the
    coordinates of the users location, and while the MyLocation button will de displayed in the
    upper right, it will not relocate the camera and marker to the user's current location

    - toggling the tracking using the action button in the toolbar removes the marker but does not
    replace it when run in the emulator
 */
// TODO hide registration feature after user registers for the 1st time
// TODO system breaks if user doesn't have a status when they register
// TODO add list for user's contacts
// TODO what's a user's status when they first log in?
public class MainActivity extends AppCompatActivity implements
        OnMapReadyCallback,
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        LocationListener {

    private static final String TAG = MainActivity.class.getSimpleName();
    public static boolean trackingIsEnabled = true;          // true if the user's location is currently being recorded

    private final int REGISTER_REQUEST = 1;

    private User user;       // the app user
//    private ArrayList<Contact>
    SharedPreferences prefs;
    private DatabaseInterface db;
    private Menu optionsMenu;           // allow access of toolbar menu outside toolbar-specific methods

    // MAP FIELDS
    private GoogleMap mMap;

    // A default location (Sydney, Australia) and default zoom to use when location permission is
    // not granted.
    private final LatLng mDefaultLocation = new LatLng(-33.8523341, 151.2106085);
    private static final int DEFAULT_ZOOM = 15;
    private static final int PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 1;
    private boolean mLocationPermissionGranted;

    // The geographical location where the device is currently located.
    private Location mCurrentLocation;
    private Marker mCurrentLocationMarker;
    private MarkerOptions mMarkerOptions;

    //private CameraPosition mCameraPosition;

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

        MapsInitializer.initialize(getApplicationContext());        // allow use of BitmapDescriptorFactory
        buildGoogleApiClient();
        mGoogleApiClient.connect();
        createLocationRequest();

        // determine if user is registered
        user = new User();
        // get user id if one exists
        prefs = getSharedPreferences(getResources().getString(R.string.APP_TAG), MODE_PRIVATE);
        int userId = prefs.getInt(getResources().getString(R.string.KEY_USER_ID), User.USER_ID_NOT_FOUND);
        // user has already registered
        // it matters what constructor you call
        if (userId == User.USER_ID_NOT_FOUND) {
            db = new DatabaseInterface();
        } else {
            // DEBUG
            user.setUserId(prefs.getInt(getResources().getString(R.string.KEY_USER_ID), -1));
            user.setUsername(prefs.getString(getResources().getString(R.string.KEY_USERNAME), ""));
            user.setStatus("");
            db = new DatabaseInterface(user.getUserId(), "GNDN");
        }
        // retrieve contacts list

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
    }

    @Override
    protected void onPause() {
        Log.d(TAG, "Before super.onPause");
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

    // called when getMapAsync returns; map is ready to be used
    @Override
    @SuppressWarnings("MissingPermission")
    public void onMapReady(GoogleMap map) {
        mMap = map;

        if (mCurrentLocation != null) {
            LatLng latLng = new LatLng(mCurrentLocation.getLatitude(), mCurrentLocation.getLongitude());    // maybe add default zoom?
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, DEFAULT_ZOOM));

            // display my-location layer  data, namely the my-location button
            // TEST
            if (mLocationPermissionGranted) {
                mMap.setMyLocationEnabled(true);
                mMap.getUiSettings().setMyLocationButtonEnabled(true);

                // send location info on initialization to server
                if (user.isRegistered()) {
                    db.setUserInfo(user.getUserId(), latLng.latitude, latLng.longitude, mCurrentLocation.getTime(), "");
                }

                // DEBUG retrieve info
//                String result = db.getUserInfo(user.getUserId());
//                JSONtoArrayList JSONparser = new JSONtoArrayList();
//                ArrayList<InfoBundle> contactBundle = JSONparser.convert(result);
                InfoBundle contactBundle = new InfoBundle(-1, "testboi", mCurrentLocation, "status");
                Log.d(TAG, "Info Bundle:\n" + contactBundle);   //.get(0).toString()
            }
            Log.d(TAG, "Current LatLng: " + latLng.latitude + ", " + latLng.longitude + "\n"
                + "Time: " + mCurrentLocation.getTime());
        } else {
            Log.d(TAG, "Current location is null. Using default location.");
//            mCurrentLocationMarker = mMap.addMarker(new MarkerOptions().position(mDefaultLocation));
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
            // create and display simple dialog
            AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
            alertDialogBuilder.setTitle("Location tracking required")
                    .setMessage(getResources().getString(R.string.location_permission_rationale))
                    .setPositiveButton("OKAY", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.cancel();
                    ActivityCompat.requestPermissions(MainActivity.this,
                            new String[] {Manifest.permission.ACCESS_FINE_LOCATION},
                            PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION);
                }
            });

            AlertDialog rationaleDialog = alertDialogBuilder.create();
            rationaleDialog.show();
        }
        // get last-known location of device and register for location updates
        if (mLocationPermissionGranted) {
            mCurrentLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
            enableTracking();
        }
    }

    // handle callback when the location changes
    @Override
    public void onLocationChanged(Location location) {
        if (trackingIsEnabled) {
            mCurrentLocation = location;
            // update location marker for user
            if (mMap != null) {
                // update server info given proper conditions
                // trackingIsEnabled is always true??
                if (trackingIsEnabled && user.isRegistered()) {
                    String result = db.setUserInfo(user.getUserId(), location.getLatitude(), location.getLongitude(),
                            location.getTime(), user.getStatus());
                    // defined by db interface class
                    if (result.equals("error")) {
                        Log.e(TAG, "setUserInfo failed at onLocationChanged");
                    } else {

                    }
                }
                Log.d(TAG, "Current LatLng: " + location.getLatitude() + ", " + location.getLongitude() + "\n"
                        + "Time: " + DateFormat.format("dd-MM-yyyy", mCurrentLocation.getTime()));
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

    // METHODS -------------------------------------------------------------------------------------

    @SuppressWarnings("MissingPermission")
    private void disableTracking() {
        LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient, this);
//        mCurrentLocationMarker.remove();
//        updateLocationUI();
        trackingIsEnabled = false;
        Log.d(TAG, getResources().getString(R.string.DEBUG_TRACKING_STATUS,
                (trackingIsEnabled) ? "enabled" : "disabled"));
    }
    // might need to also place marker
    @SuppressWarnings("MissingPermission")
    private void enableTracking() {
        if (mLocationPermissionGranted) {
            LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this);
            trackingIsEnabled = true;
            Log.d(TAG, getResources().getString(R.string.DEBUG_TRACKING_STATUS,
                    (trackingIsEnabled) ? "enabled" : "disabled"));
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REGISTER_REQUEST) {
            if (resultCode == RESULT_OK) {
                // successful registration
                // store user
                Object obj = data.getSerializableExtra(getResources().getString(R.string.KEY_USER));
                if (obj instanceof User) {
                    user = (User)obj;
                }
                // remove registration action
                // DEBUG remove
//                if (optionsMenu != null) {
//                    MenuItem item = optionsMenu.findItem(R.id.action_register);
//                    item.setVisible(false);
//                    invalidateOptionsMenu();
//                }
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
//        if (user.isRegistered()) {
//            MenuItem item = menu.findItem(R.id.action_register);
//            item.setVisible(false);
//            invalidateOptionsMenu();
//        }
        return true;
    }

    @SuppressWarnings("MissingPermission")
    public boolean onOptionsItemSelected(MenuItem item) {
        boolean result = true;
        Intent intent;
        switch (item.getItemId()) {
            case R.id.action_manage_contacts:
                intent = new Intent(this, ManageContactsActivity.class);
                intent.putExtra(getResources().getString(R.string.KEY_USER), user);
                intent.putExtra(getResources().getString(R.string.KEY_DB_INTERFACE_OBJ), db);
                startActivity(intent);
                break;
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
            case R.id.action_update_status:
                intent = new Intent(this, UpdateStatusActivity.class);
                if (user.isRegistered()) {
                    intent.putExtra(getResources().getString(R.string.KEY_USER), user);
                }
                startActivity(intent);
                break;
            case R.id.action_register:
                // only allow registration if user doesn't have a user id
//                if (!user.isRegistered()) {
                    intent = new Intent(this, RegistrationActivity.class);
                    intent.putExtra(getResources().getString(R.string.KEY_USER), user);
                    intent.putExtra(getResources().getString(R.string.KEY_DB_INTERFACE_OBJ), db);
                    startActivityForResult(intent, REGISTER_REQUEST);
//                }
                break;
            case R.id.action_settings:
                intent = new Intent(this, SettingsActivity.class);
                intent.putExtra(getResources().getString(R.string.KEY_USER), user);
                startActivity(intent);
                break;
            default:
                result = false;
                break;
        }
        return result;
    }
}
