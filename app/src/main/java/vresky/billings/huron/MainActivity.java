package vresky.billings.huron;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
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
// TODO status list not updated immediately when new status is added, changes to status list aren't saved
// TODO app seems to initialize location to the default, even when permission is granted. May be a problem of position being requested before permission is granted
// TODO implement code for saving map state
// TODO inform user of the necessity of permission tracking for app
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

    // Keys for storing activity state.
//    private static final String KEY_CAMERA_POSITION = "camera_position";
//    private static final String KEY_LOCATION = "location";

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
    public void onMapReady(GoogleMap map) {
        mMap = map;
        if (mCurrentLocation != null) {
//            Toast.makeText(MainActivity.this, "mCurrentLocation not null", Toast.LENGTH_SHORT).show();
            LatLng latLng = new LatLng(mCurrentLocation.getLatitude(), mCurrentLocation.getLongitude());    // maybe add default zoom?
            mCurrentLocationMarker = mMap.addMarker(mMarkerOptions.position(latLng)
                .snippet("Latitude: " + latLng.latitude + " Longitude: " + latLng.longitude));
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, DEFAULT_ZOOM));
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
        } else {
            ActivityCompat.requestPermissions(this,
                    new String[] {Manifest.permission.ACCESS_FINE_LOCATION},
                    PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION);
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
            mCurrentLocationMarker.remove();
            mCurrentLocationMarker = mMap.addMarker(mMarkerOptions.position(latLng));
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
