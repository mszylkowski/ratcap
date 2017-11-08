package com.vikoding.ratcap;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class MapsActivity extends AppCompatActivity implements OnMapReadyCallback {

    private static GoogleMap mMap;
    private float latitude;
    private float longitude;

    private static Button fromButton;
    private static Button toButton;
    private static String fromDate = "2002-01-01 00:00";
    private static String toDate = "2020-01-01 00:00";
    private static List<Report> reportList;
    private static List<Report> originalList;
    private static SupportMapFragment mapFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        fromButton = (Button)findViewById(R.id.chooseFrom);
        toButton = (Button)findViewById(R.id.chooseTo);

        fromButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DialogFragment newFragment = new DatePickerFragment();
                newFragment.show(getSupportFragmentManager(), "datePicker");
            }
        });
        originalList = (List<Report>) getIntent().getExtras().get("REPORTS");
    }

    public static void fetchData() {
        /*
        FirebaseDatabase.getInstance().getReference("reports").orderByChild("date").startAt(fromDate).endAt(toDate)
                .limitToFirst(5).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                reportList = (List<Report>) dataSnapshot;
                System.out.println(reportList.size());
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });*/
        if (mMap != null) mMap.clear();
        reportList = new ArrayList<>();
        for (Report r : originalList) {
            if (fromDate.compareTo(r.getDate()) <= 0 & toDate.compareTo(r.getDate()) >= 0) {
                reportList.add(r);
                mMap.addMarker(new MarkerOptions().position(new LatLng(r.getLat(), r.getLon())).title(r.getKey()));
            }
        }


    }


    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        // Add a marker in Sydney and move the camera
        LatLng newYork = new LatLng(40.6784, -74.0456);
        mMap.moveCamera(CameraUpdateFactory.newLatLng(newYork));
        mMap.moveCamera(CameraUpdateFactory.zoomTo(10));
        LocationManager locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
        String provider = LocationManager.NETWORK_PROVIDER;
        fetchData();
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            Toast toast = Toast.makeText(getApplicationContext(), "Accept location", Toast.LENGTH_SHORT);
            toast.show();
            return;
        }
        locationManager.requestLocationUpdates(provider, 1000, 10, new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
                /*
                latitude = (float) location.getLatitude();
                longitude = (float) location.getLongitude();
                mMap.moveCamera(CameraUpdateFactory.newLatLng(new LatLng(latitude, longitude)));
                */
            }

            @Override
            public void onStatusChanged(String s, int i, Bundle bundle) {

            }

            @Override
            public void onProviderEnabled(String s) {

            }

            @Override
            public void onProviderDisabled(String s) {

            }
        });
    }

    public static class DatePickerFragment extends DialogFragment
            implements DatePickerDialog.OnDateSetListener {

        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            final Calendar c = Calendar.getInstance();
            int year = c.get(Calendar.YEAR);
            int month = c.get(Calendar.MONTH);
            int day = c.get(Calendar.DAY_OF_MONTH);
            DatePickerDialog dialog = new DatePickerDialog(getActivity(), this, year, month, day);
            dialog.getDatePicker().setMaxDate(c.getTimeInMillis());
            return  dialog;
        }

        public void onDateSet(DatePicker view, int year, int month, int day) {
            fromDate = year + "-" + (month < 10 ? "0" : "") + month + "-" + (day < 10 ? "0" : "") + day;
            fetchData();
        }
    }
}
