package com.vikoding.ratcap;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;

    private Query mQuery;
    private MyAdapter mMyAdapter;
    private ArrayList<Report> mAdapterItems;
    private ArrayList<String> mAdapterKeys;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mAuth = FirebaseAuth.getInstance();
        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user != null) {
                    Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                    startActivity(intent);
                    finish();
                }
            }
        };

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), CreateReportActivity.class);
                startActivity(intent);
            }
        });

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);

        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        final View topHeader = navigationView.getHeaderView(0);

        mAdapterItems = new ArrayList<Report>();
        mAdapterKeys = new ArrayList<String>();

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            String name = user.getDisplayName();
            String email = user.getEmail();
            if (name == null || name.equals("")) {
                name = getIntent().getStringExtra("NAME");
            }
            if (name != null) {
                boolean isAdmin = name.contains("[admin]");
                name = name.replace("[admin]", "");
                ((TextView) topHeader.findViewById(R.id.name_navbar)).setText(name);
            }
            ((TextView) topHeader.findViewById(R.id.email_navbar)).setText(email);
        }

        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user == null) {
                    Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
                    startActivity(intent);
                    finish();
                }
            }
        };
        setupRecyclerview();
    }

    /**
     * Sets the adapter for the RecyclerView
     */
    private void setupRecyclerview() {
        mQuery = FirebaseDatabase.getInstance().getReference("reports").limitToLast(50).orderByKey();
        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.sightings_master_recyclerview);
        mMyAdapter = new MyAdapter(mQuery, mAdapterItems, mAdapterKeys);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(mMyAdapter);
    }

    @Override
    public void onStart() {
        super.onStart();
        mAuth.addAuthStateListener(mAuthListener);
    }

    @Override
    public void onStop() {
        super.onStop();
        if (mAuthListener != null) {
            mAuth.removeAuthStateListener(mAuthListener);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        if (mMyAdapter != null) {
            mMyAdapter.notifyDataSetChanged();
        }
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_settings) {
            FirebaseAuth.getInstance().signOut();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.profile) {
            // Handle the camera action
        } else if (id == R.id.map) {
            for (int i = 0; i < mAdapterItems.size(); i++) {
                mAdapterItems.get(i).setKey(mAdapterKeys.get(i));
            }
            Intent intent = new Intent(getApplicationContext(), MapsActivity.class);
            intent.putExtra("REPORTS", mAdapterItems);
            startActivity(intent);
        } else if (id == R.id.graphs) {
            Intent intent = new Intent(getApplicationContext(), GraphsActivity.class);
            startActivity(intent);
        } else if (id == R.id.nav_manage) {

        } else if (id == R.id.nav_share) {

        } else if (id == R.id.nav_send) {

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    /**
     * RecyclerView adapter that fetches data from firebase. Stores all the reports in mAdapterItems
     */
    private class MyAdapter extends FirebaseRecyclerAdapter<MyAdapter.ViewHolder, Report> {

        public class ViewHolder extends RecyclerView.ViewHolder {

            TextView textViewAddress;
            TextView textViewCity;
            TextView textViewDate;
            View layout;

            public ViewHolder(View view) {
                super(view);
                textViewAddress = (TextView) view.findViewById(R.id.sightings_master_item_address);
                textViewCity = (TextView) view.findViewById(R.id.sightings_master_item_city);
                textViewDate = (TextView) view.findViewById(R.id.sightings_master_item_date);
                layout = view;
            }
        }

        public MyAdapter(Query query, @Nullable ArrayList<Report> items,
                         @Nullable ArrayList<String> keys) {
            super(query, items, keys);
        }

        @Override
        public MyAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item, parent, false);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(MyAdapter.ViewHolder holder, int position) {
            final Report item = getItem(position);
            holder.textViewAddress.setText(item.getAddress());
            holder.textViewCity.setText(item.getCity());
            holder.textViewDate.setText(item.getDate());
            holder.layout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(getApplicationContext(), ReportActivity.class);
                    intent.putExtra("REPORT", item);
                    startActivity(intent);
                }
            });
        }

        @Override
        protected void itemAdded(Report item, String key, int position) {

        }

        @Override
        protected void itemChanged(Report oldItem, Report newItem, String key, int position) {

        }

        @Override
        protected void itemRemoved(Report item, String key, int position) {

        }

        @Override
        protected void itemMoved(Report item, String key, int oldPosition, int newPosition) {

        }
    }
}
