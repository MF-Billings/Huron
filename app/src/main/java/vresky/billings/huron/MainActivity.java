package vresky.billings.huron;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
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
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.text.DateFormat;
import java.util.Date;

/**
 * Created by Matt on 14/12/2016
 * polls for location updates while app is running in the foreground
 */
/* NOTE Because of the inability of the emulator to regularly update the location there are certain
    issues that occur when running the app on an emulator

    - when location permission is requested upon running the app, the map does not receive the
    coordinates of the users location, and while the MyLocation button will de displayed in the
    upper right, it will not relocate the camera and marker to the user's current location

    - toggling the tracking using the action button in the toolbar removes the marker but does not
    replace it when run in the emulator
 */
    // TODO set zoom back to previous amount
// TODO places multiple markers for the users position - needs more testing when travelling significant distances.
// I noticed 3 markers placed over a short walk, with 2 being almost on top of one another
// and the other reflection the current position
// TODO stop app from constantly re-centering the camera every update (DO THIS BEFORE GOING OUT TO TEST ON PHONE)
// TODO MyLocation button and tracking seems to disappear when app is launched after the location is set to the user's location
    // when testing on phone
// TODO continue to update location when app does not have focus
public class MainActivity extends AppCompatActivity implements
        OnMapReadyCallback,
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        LocationListener {

    public static boolean trackingIsEnabled = true;          // true if the user's location is currently being recorded

    private static final String TAG = MainActivity.class.getSimpleName();
    private GoogleMap mMap;

    // A default location (Sydney, Australia) and default zoom to use when location permission is
    // not granted.
    private final LatLng mDefaultLocation = new LatLng(-33.8523341, 151.2106085);
    private static final int DEFAULT_ZOOM = 5; //15;
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
        mMarkerOptions = new MarkerOptions().title("You")
                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE));
        createLocationRequest();
    }

    // MAP  ----------------------------------------------------------------------------------------

    // called when getMapAsync returns; map is ready to be used
    @Override
    @SuppressWarnings("MissingPermission")
    public void onMapReady(GoogleMap map) {
        mMap = map;

        if (mCurrentLocation != null) {
//            Toast.makeText(MainActivity.this, "mCurrentLocation not null", Toast.LENGTH_SHORT).show();
            LatLng latLng = new LatLng(mCurrentLocation.getLatitude(), mCurrentLocation.getLongitude());    // maybe add default zoom?
            if (mCurrentLocationMarker != null) {
                mCurrentLocationMarker.remove();
                Log.d("marker", "Removed marker during onLocationChanged");
            }
            mCurrentLocationMarker = mMap.addMarker(mMarkerOptions.position(latLng)
                .snippet("Latitude: " + latLng.latitude + " Longitude: " + latLng.longitude));
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, DEFAULT_ZOOM));

            // display my-location layer  data, namely the my-location button
            // TEST
            if (mLocationPermissionGranted) {
                mMap.setMyLocationEnabled(true);
                mMap.getUiSettings().setMyLocationButtonEnabled(true);
            }
            Log.d(TAG, "Current LatLng: " + latLng.latitude + ", " + latLng.longitude);
        } else {
            Log.d(TAG, "Current location is null. Using default location.");
            mCurrentLocationMarker = mMap.addMarker(new MarkerOptions().position(mDefaultLocation));
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
        // get last-know location of device and register for location updates
        if (mLocationPermissionGranted) {
            mCurrentLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
            LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this);
        }
    }

    // handle callback when the location changes
    @Override
    public void onLocationChanged(Location location) {
        mCurrentLocation = location;
        // update location marker for user
        if (mMap != null) {
            LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
            if (mCurrentLocationMarker != null) {
                mCurrentLocationMarker.remove();
                Log.d("marker", "Removed marker during onLocationChanged");
            }
            mCurrentLocationMarker = mMap.addMarker(mMarkerOptions.position(latLng));
            // check that markers are being created
//            mMap.addMarker(mMarkerOptions.position(latLng));
            // DEBUG try removing to stop forced camera movement
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, DEFAULT_ZOOM));
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

    // MENU ----------------------------------------------------------------------------------------

    // display toolbar
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main_menu, menu);
        return true;
    }

    @SuppressWarnings("MissingPermission")
    public boolean onOptionsItemSelected(MenuItem item) {
        boolean result = true;
        Intent intent;
        switch (item.getItemId()) {
            case R.id.action_manage_contacts:
                intent = new Intent(this, ManageContactsActivity.class);
                startActivity(intent);
                break;
            case R.id.action_toggle_tracking_visibility:
                // toggle user location tracking
                if (trackingIsEnabled) {
                    // disable location tracking
                    item.setIcon(R.drawable.ic_visibility_off_black_24dp);
                    LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient, this);
                    mCurrentLocationMarker.remove();
                } else {
                    item.setIcon(R.drawable.ic_visibility_black_24dp);
                    if (mLocationPermissionGranted) {
                        LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this);
                    }
                }
                trackingIsEnabled = !trackingIsEnabled;
                Toast.makeText(this, "Tracking " + ((trackingIsEnabled) ? "enabled" : "disabled"),
                        Toast.LENGTH_SHORT).show();
                Log.d(TAG, "Tracking " + ((trackingIsEnabled) ? "enabled" : "disabled") + " at " + DateFormat.getDateTimeInstance().format(new Date()));
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
