package com.example.myapplication;

import android.Manifest;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentActivity;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback,
        GoogleMap.OnMyLocationClickListener, GoogleMap.OnMapClickListener {

    private static final String TAG = "MapsActivity";

    private SupportMapFragment mapFragment;
    private GoogleMap mMap;
    public Location mLastKnownLocation;
    private ControllerMapActivity controller;
    private FusedLocationProviderClient mFusedLocationProviderClient;
    private LocationCallback mLocationCallback;
    private LocationRequest mLocationRequest;

    private boolean mLocationPermissionGranted;
    private boolean addActive;

    private Button addButton;
    public TextView textView;
    private ArrayList<Marker> markers;

    //--------------------------------------------- OVERRIDE METHODS ACTIVITY-----------------------------

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);

        mLocationPermissionGranted = false;
        getLocationPermission();

        controller = new ControllerMapActivity(this);

        addButton = findViewById(R.id.add);
        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (v.getId() == R.id.add) {
                    chanceAdd();
                }
            }
        });

        textView = findViewById(R.id.description);
        markers = new ArrayList<>();
        initLocationService();

    }


    @Override
    protected void onResume() {
        super.onResume();
        mFusedLocationProviderClient.requestLocationUpdates(mLocationRequest, mLocationCallback, Looper.myLooper());
    }

    @Override
    protected void onPause() {
        super.onPause();
        mFusedLocationProviderClient.removeLocationUpdates(mLocationCallback);
    }

    //------------------------------OVERRIDE METHODS THAT DIFERENT INTERFACES(Listeners,etc) -----------------------------------

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        activeServices();
    }

    @Override
    public void onMyLocationClick(@NonNull Location location) {
        if (location != null)
            try {
                Geocoder geo = new Geocoder(this, Locale.getDefault());
                List<Address> addresses = geo.getFromLocation(location.getLatitude(), location.getLongitude(), 1);
                Toast.makeText(this, "Ubicación:\n" + addresses.get(0).getFeatureName() + ", " + addresses.get(0).getLocality() + ", " + addresses.get(0).getAdminArea() + ", " + addresses.get(0).getCountryName(), Toast.LENGTH_LONG).show();
            } catch (Exception e) {
                e.printStackTrace();
            }

    }

    @Override
    public void onMapClick(LatLng latLng) {
        if (addActive) {
            DialogAdd dialog = new DialogAdd(this, latLng, controller);
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            dialog.show();
        }
    }

    //---------------------------------- HELPER METHODS THAT CREATED BY ME ----------------------------------------

    private void updateMarkers() {
        for (int i = 0; i < markers.size(); i++)
            markers.get(i).remove();
        markers = new ArrayList<>();
        controller.initMarkes();
    }


    private void moveCamera() {
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(
                new LatLng(mLastKnownLocation.getLatitude(),
                        mLastKnownLocation.getLongitude()), 15));
    }


    private void activeServices() {

        if (mLocationPermissionGranted) {
            mMap.setMyLocationEnabled(true);
            mMap.setOnMapClickListener(this);
            mMap.setOnMyLocationClickListener(this);
        }
    }

    private void initLocationService() {
        mFusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);

        mLocationRequest = LocationRequest.create();
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        mLocationRequest.setInterval(100);
        mLocationRequest.setFastestInterval(100);
        mLocationRequest.setSmallestDisplacement(1);
        mLocationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(LocationResult locationResult) {
                mLastKnownLocation = locationResult.getLastLocation();
                Log.i("Location_Result", mLastKnownLocation.getLatitude() + ", " + mLastKnownLocation.getLongitude());
                updateMarkers();
                moveCamera();
            }
        };
    }


    public void chanceAdd() {
        if (addActive) {
            addButton.setBackground(ContextCompat.getDrawable(this, R.drawable.circle_button_yellow));
            addButton.setText("+");
        } else {
            addButton.setBackground(ContextCompat.getDrawable(this, R.drawable.circle_button_green));
            addButton.setText("X");
        }
        addActive = !addActive;
    }

    public void addMark(Mark mark, String description) {
        markers.add(mMap.addMarker(new MarkerOptions().position(new LatLng(mark.lat, mark.lng))
                .title(description)));
    }


    // ---------------------------------------- PERMISSIONS --------------------------------------------------
    private void getLocationPermission() {
        /*
         * Request location permission, so that we can get the location of the
         * device. The result of the permission request is handled by a callback,
         * onRequestPermissionsResult.
         */
        if (ContextCompat.checkSelfPermission(this.getApplicationContext(), android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
                && ContextCompat.checkSelfPermission(this.getApplicationContext(), Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            mLocationPermissionGranted = true;
            mapFragment.getMapAsync(this);
        } else
            ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION}, PackageManager.PERMISSION_GRANTED);

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        if (requestCode == PackageManager.PERMISSION_GRANTED) {
            if (permissions.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                mLocationPermissionGranted = true;
                mapFragment.getMapAsync(this);
            } else {
                // Permission was denied. Display an error message.
                Log.e("MapsActivity", "Exception: DOESN'T PERMITED");
            }
        }
    }

}
