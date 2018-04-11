package com.example.tobia.myapplication;

import android.content.Intent;
import android.content.res.Resources;
import android.graphics.PorterDuff;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.location.Location;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.maps.GoogleMap.InfoWindowAdapter;
import com.google.android.gms.maps.GoogleMap.OnInfoWindowClickListener;
import com.google.android.gms.maps.GoogleMap.OnInfoWindowCloseListener;
import com.google.android.gms.maps.GoogleMap.OnInfoWindowLongClickListener;
import com.google.android.gms.maps.GoogleMap.OnMarkerClickListener;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.places.GeoDataClient;
import com.google.android.gms.location.places.PlaceDetectionClient;
import com.google.android.gms.location.places.PlaceLikelihood;
import com.google.android.gms.location.places.PlaceLikelihoodBufferResponse;
import com.google.android.gms.location.places.Places;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

import static com.example.tobia.myapplication.R.id.map;

public class MapsActivity extends FragmentActivity implements
        OnMapReadyCallback {

    private static final String TAG = MapsActivity.class.getSimpleName();
    private GoogleMap mMap;
    private CameraPosition mCameraPosition;

    private GeoDataClient mGeoDataClient;
    private PlaceDetectionClient mPlaceDetectionClient;
    private FusedLocationProviderClient mFusedLocationProviderClient;


    private final LatLng mDefaultLocation = new LatLng(-33.8523341, 151.2106085);
    private static final int DEFAULT_ZOOM = 15;
    private static final int PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 1;
    private boolean mLocationPermissionGranted;

    private Location mLastKnownLocation;

    private static final String KEY_CAMERA_POSITION = "camera_position";
    private static final String KEY_LOCATION = "location";

    private static final int M_MAX_ENTRIES = 5;
    private String[] mLikelyPlaceNames;
    private String[] mLikelyPlaceAddresses;
    private String[] mLikelyPlaceAttributions;
    private LatLng[] mLikelyPlaceLatLngs;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (savedInstanceState != null) {
            mLastKnownLocation = savedInstanceState.getParcelable(KEY_LOCATION);
            mCameraPosition = savedInstanceState.getParcelable(KEY_CAMERA_POSITION);
        }

        setContentView(R.layout.activity_maps);

        mGeoDataClient = Places.getGeoDataClient(this, null);

        // Construct a PlaceDetectionClient.
        mPlaceDetectionClient = Places.getPlaceDetectionClient(this, null);

        mFusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(map);
        mapFragment.getMapAsync(this);

        BottomNavigationView bottomNavigationView = (BottomNavigationView) findViewById(R.id.bottom_navigation);
        BottomNavigationViewHelper.disableShiftMode(bottomNavigationView);
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId())
                {
                    case R.id.homeMiniBtn:
                        Intent startIntend = new Intent(getApplicationContext(), MainActivity.class);
                        startActivity(startIntend);
                        break;
                    case R.id.suggestedTourMiniBtn:
                        Intent startIntendSug = new Intent(getApplicationContext(), SuggestedTour.class);
                        startActivity(startIntendSug);
                        break;
                    case R.id.customTourMiniBtn:
                        Intent startIntendCus = new Intent(getApplicationContext(), CustomTour.class);
                        startActivity(startIntendCus);
                        break;
                    case R.id.personMiniBtn:
                        Intent startIntendPer = new Intent(getApplicationContext(), Person.class);
                        startActivity(startIntendPer);
                        break;
                }
                return true;
            }
        });
    }
    boolean clicked = false;

    public void buttonOnClick(View v) {
        Button button =(Button) v;
        ((Button) v).setText("Show places");
        Resources res = this.getResources();
        final int newColor = res.getColor(R.color.White);

        //Create the position at Cassiopeia.
        LatLng Cassiopeia = new LatLng(57.0122672, 9.991290);
        //Instantiate a new polyline object. Used as route on the map.
        PolylineOptions rectOptions = new PolylineOptions()
                .add(new LatLng(57.0131437,9.990876))
                .add(new LatLng(57.0126457,9.991772))
                .add(new LatLng(57.0126417,9.991858))
                .add(new LatLng(57.0122347,9.991850))
                .add(new LatLng(57.0122357,9.991981))
                .add(new LatLng(57.0119987,9.991951))
                .add(new LatLng(57.0117387,9.991463))
                .add(new LatLng(57.0117737,9.990066))
                .add(new LatLng(57.0127357,9.990128))
                .add(new LatLng(57.0131437,9.990876));

        Polyline polyline = mMap.addPolyline(rectOptions);
        Marker marker = mMap.addMarker(new MarkerOptions().position(Cassiopeia).title("Cassiopeia!"));

        ImageView img1 = (ImageView) findViewById(R.id.imageView2);
        ImageView img2 = (ImageView) findViewById(R.id.imageView3);
        ImageView img3 = (ImageView) findViewById(R.id.imageView6);
        ImageView img4 = (ImageView) findViewById(R.id.imageView7);

        img1.clearColorFilter();
        img2.clearColorFilter();
        img3.clearColorFilter();
        img4.clearColorFilter();

        img1.setImageResource(R.drawable.cassio1);
        img2.setImageResource(R.drawable.cassio2);
        img3.setImageResource(R.drawable.cassio3);
        img4.setImageResource(R.drawable.cassio4);

        //When the use pushes a route, the camera will zoom to the position of the route.
        CameraPosition cameraPosition = new CameraPosition.Builder()
                .target(new LatLng(57.0122672, 9.991290)) //Sets the center of map to be Cassiopeia.
                .zoom(17) //Zoom level.
                .build();

        if (clicked == true) {
            mMap.clear();
            clicked = false;
            img1.setColorFilter(newColor, PorterDuff.Mode.ADD);
            img2.setColorFilter(newColor, PorterDuff.Mode.ADD);
            img3.setColorFilter(newColor, PorterDuff.Mode.ADD);
            img4.setColorFilter(newColor, PorterDuff.Mode.ADD);
        } else {
            mMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
            clicked = true;
        }
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        if(clicked == true) {
            LatLng Cassiopeia = new LatLng(57.0122672, 9.989444);
                mMap.addMarker(new MarkerOptions().position(Cassiopeia).title("Cassiopeia!"));
        }
        getLocationPermission();
        updateLocationUI();
        getDeviceLocation();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        if (mMap != null) {
            outState.putParcelable(KEY_CAMERA_POSITION, mMap.getCameraPosition());
            outState.putParcelable(KEY_LOCATION, mLastKnownLocation);
            super.onSaveInstanceState(outState);
        }


    }

    private void getDeviceLocation() {
        /*
         * Get the best and most recent location of the device, which may be null in rare
         * cases when a location is not available.
         */
        try {
            if (mLocationPermissionGranted) {
                Task<Location> locationResult = mFusedLocationProviderClient.getLastLocation();
                locationResult.addOnCompleteListener(this, new OnCompleteListener<Location>() {
                    @Override
                    public void onComplete(@NonNull Task<Location> task) {
                        if (task.isSuccessful()) {
                            // Set the map's camera position to the current location of the device.
                            mLastKnownLocation = task.getResult();
                            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(
                                    new LatLng(mLastKnownLocation.getLatitude(),
                                            mLastKnownLocation.getLongitude()), DEFAULT_ZOOM));
                        } else {
                            Log.d(TAG, "Current location is null. Using defaults.");
                            Log.e(TAG, "Exception: %s", task.getException());
                            mMap.moveCamera(CameraUpdateFactory
                                    .newLatLngZoom(mDefaultLocation, DEFAULT_ZOOM));
                            mMap.getUiSettings().setMyLocationButtonEnabled(false);
                        }
                    }
                });
            }
        } catch (SecurityException e)  {
            Log.e("Exception: %s", e.getMessage());
        }
    }

    private void getLocationPermission() {
        /*
         * Request location permission, so that we can get the location of the
         * device. The result of the permission request is handled by a callback,
         * onRequestPermissionsResult.
         */
        if (ContextCompat.checkSelfPermission(this.getApplicationContext(),
                android.Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            mLocationPermissionGranted = true;
        } else {
            ActivityCompat.requestPermissions(this,
                    new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},
                    PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String permissions[],
                                           @NonNull int[] grantResults) {
        mLocationPermissionGranted = false;
        switch (requestCode) {
            case PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    mLocationPermissionGranted = true;
                }
            }
        }
        updateLocationUI();
    }

    private void updateLocationUI() {
        if (mMap == null) {
            return;
        }
        try {
            if (mLocationPermissionGranted) {
                mMap.setMyLocationEnabled(true);
                mMap.getUiSettings().setMyLocationButtonEnabled(true);
            } else {
                mMap.setMyLocationEnabled(false);
                mMap.getUiSettings().setMyLocationButtonEnabled(false);
                mLastKnownLocation = null;
                getLocationPermission();
            }
        } catch (SecurityException e)  {
            Log.e("Exception: %s", e.getMessage());
        }
    }
}

