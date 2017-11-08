package com.vikoding.ratcap;

import android.content.Context;
import android.content.pm.PackageManager;
import android.database.DataSetObserver;
import android.location.Address;
import android.location.Criteria;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.SpinnerAdapter;
import android.widget.Toast;

import com.google.firebase.database.FirebaseDatabase;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Map;

public class CreateReportActivity extends AppCompatActivity {
    private EditText mAddress;
    private EditText mZip;
    private EditText mCity;
    private EditText mBorough;
    private float latitude;
    private float longitude;
    private String locationType;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_report);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        final ArrayList<String> locationsArray = new ArrayList<String>();
        locationsArray.add("3+ Family Apt. Building");
        locationsArray.add("3+ Family Mixed Use Building");
        locationsArray.add("1-2 Family Dwelling");
        locationsArray.add("1-2 Family Mixed Use Building");
        locationsArray.add("Commercial Building");
        locationsArray.add("Construction Site");
        locationsArray.add("Catch Basin/Sewer");
        locationsArray.add("Public Garden");
        locationsArray.add("Vacant Lot");
        locationsArray.add("Other");

        final Spinner locationSpinner = (Spinner) findViewById(R.id.send_location);
        final ArrayAdapter<String> spinnerArrayAdapter = new ArrayAdapter<String>
                (this, android.R.layout.simple_spinner_item,
                        locationsArray); //selected item will look like a spinner set from XML
        spinnerArrayAdapter.setDropDownViewResource(android.R.layout
                .simple_spinner_dropdown_item);
        locationSpinner.setAdapter(spinnerArrayAdapter);
        locationSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                locationType = spinnerArrayAdapter.getItem(position);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
                locationType = "";
            }

        });

        mAddress = (EditText) findViewById(R.id.send_address);
        mZip = (EditText) findViewById(R.id.send_zip);
        mCity = (EditText) findViewById(R.id.send_city);
        mBorough = (EditText) findViewById(R.id.send_borough);

        Button sendButton = (Button) findViewById(R.id.send_button);
        sendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String key = FirebaseDatabase.getInstance().getReference("reports").push().getKey();
                String timeStamp = new SimpleDateFormat("yyyy-MM-dd hh:mm").format(new Date());
                Report report = new Report(mAddress.getText().toString(), mBorough.getText().toString(),
                        mCity.getText().toString(), timeStamp, latitude, longitude,
                        locationType, mZip.getText().toString(), key);
                Map<String, Object> postValues = report.toMap();
                FirebaseDatabase.getInstance().getReference("reports").child(key).updateChildren(postValues);
                finish();
            }
        });

        LocationManager locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
        String provider = LocationManager.NETWORK_PROVIDER;
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            Toast toast = Toast.makeText(getApplicationContext(), "Accept location", Toast.LENGTH_SHORT);
            toast.show();
            return;
        }
        locationManager.requestLocationUpdates(provider, 1000, 10, new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
                latitude = (float) location.getLatitude();
                longitude = (float) location.getLongitude();
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

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

}
