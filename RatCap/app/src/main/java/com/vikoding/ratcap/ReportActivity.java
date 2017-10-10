package com.vikoding.ratcap;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.widget.TextView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

public class ReportActivity extends AppCompatActivity implements OnMapReadyCallback {

    private Report report;
    private TextView addressTV;
    private TextView cityTV;
    private TextView zipTV;
    private TextView dateTV;
    private TextView locTypeTV;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_report);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        report = (Report) getIntent().getSerializableExtra("REPORT");
        addressTV = (TextView) findViewById(R.id.sightings_detail_address);
        cityTV = (TextView) findViewById(R.id.sightings_detail_city);
        zipTV = (TextView) findViewById(R.id.sightings_detail_zip);
        dateTV = (TextView) findViewById(R.id.sightings_detail_date);
        locTypeTV = (TextView) findViewById(R.id.sightings_detail_loctype);

        addressTV.setText("Address: " + report.getAddress());
        cityTV.setText("City: " + report.getCity());
        zipTV.setText("Zip: " + report.getZip());
        dateTV.setText("Date: " + report.getDate());
        locTypeTV.setText("Location Type: " + report.getLocType());

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }

    @Override
    public void onMapReady(GoogleMap map) {
        LatLng pos = new LatLng(report.getLat(), report.getLon());
        map.addMarker(new MarkerOptions()
                .position(pos)
                .title("Marker"));
        map.moveCamera(CameraUpdateFactory.newLatLng(pos));
        map.moveCamera(CameraUpdateFactory.zoomTo(15));
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

}
