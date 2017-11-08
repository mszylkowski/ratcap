package com.vikoding.ratcap;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.pm.PackageManager;
import android.graphics.Color;
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

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.IAxisValueFormatter;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class GraphsActivity extends AppCompatActivity {

    private static Button fromButton;
    private static Button toButton;
    private static String fromDate = "2011-01-01 00:00";
    private static String toDate = "2020-01-01 00:00";
    private static Map<String, Long> monthMap;
    private static LineChart graph;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_graphs);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        fromButton = (Button)findViewById(R.id.chooseFrom);
        toButton = (Button)findViewById(R.id.chooseTo);
        graph = (LineChart) findViewById(R.id.chart);

        fromButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DialogFragment newFragment = new DatePickerFragment();
                newFragment.show(getSupportFragmentManager(), "datePicker");
            }
        });
        fetchData();
    }

    public static void fetchData() {

        FirebaseDatabase.getInstance().getReference("months").orderByKey().startAt(fromDate).endAt(toDate)
                .addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                List<Entry> entries = new ArrayList<Entry>();
                for (DataSnapshot snap : dataSnapshot.getChildren()) {
                    int year = Integer.parseInt(snap.getKey().split("-")[0]);
                    int month = Integer.parseInt(snap.getKey().split("-")[1]);
                    entries.add(new Entry(year * 12 + month, (Long) snap.getValue()));
                }
                updateChart(entries);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private static void updateChart(List<Entry> points) {
        LineDataSet dataSet = new LineDataSet(points, "Reports");
        LineData data = new LineData(dataSet);
        dataSet.setCircleColor(Color.BLUE);
        graph.getXAxis().setDrawLabels(true);
        graph.setData(data);
        graph.getXAxis().setGranularity(1);
        graph.getXAxis().setGranularityEnabled(true);
        graph.getXAxis().setPosition(XAxis.XAxisPosition.BOTTOM);
        graph.setVisibleYRangeMinimum(0, YAxis.AxisDependency.LEFT);
        graph.getAxisRight().setEnabled(false);
        graph.getXAxis().setValueFormatter(new IAxisValueFormatter() {
            @Override
            public String getFormattedValue(float value, AxisBase axis) {
                int newVal = (int)value - 1;
                int year = newVal / 12;
                int month = newVal % 12 + 1;
                return year + "-" + month;
            }
        });
        graph.invalidate();
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
