package com.example.cs160_sp18.prog3;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.os.Looper;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.StringWriter;
import java.io.UnsupportedEncodingException;
import java.io.Writer;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class MainActivity extends AppCompatActivity {

    Logger logger = Logger.getLogger(MainActivity.class.getName());

    private Location currentLocation;
    private LocationCallback locationCallback;
    private LocationRequest locationRequest;
    private FusedLocationProviderClient fusedLocationClient;

    private RecyclerView mRecyclerView;
    private RecyclerView.Adapter mAdapter;

    private List<String> landmarkNameList = new ArrayList<>();
    private List<Double> latitudeList = new ArrayList<>();
    private List<Double> longitudeList = new ArrayList<>();
    private List<Double> distances = new ArrayList<>();
    private List<String> filenameList = new ArrayList<>();
    private List<StatueButton> statueButtons = new ArrayList<>();

    private static final int REQUEST_CODE = 1;

    private String username;

    RelativeLayout layout;
    Toolbar mToolbar;
    Button updateLocationButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_activity_feed);
        username = getIntent().getStringExtra("username");
        layout = (RelativeLayout) findViewById(R.id.main_layout);
        mToolbar = (Toolbar) findViewById(R.id.main_toolbar);
        updateLocationButton = (Button) layout.findViewById(R.id.update_location_button);
        setSupportActionBar(mToolbar);
        mRecyclerView = (RecyclerView) findViewById(R.id.statue_name_recycler);
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        InputStream is = getResources().openRawResource(R.raw.bear_statues);
        Writer writer = new StringWriter();
        char[] buffer = new char[1024];
        try {
            Reader reader = new BufferedReader(new InputStreamReader(is, "UTF-8"));
            int n;
            while ((n = reader.read(buffer)) != -1) {
                writer.write(buffer, 0, n);
            }
        } catch (UnsupportedEncodingException e) {
            logger.log(Level.ALL, "Error: UnsupportedEncodingException");
        } catch (IOException e) {
            logger.log(Level.ALL, "Error: IOException");
        } finally {
            try {
                is.close();
            } catch (IOException e) {
                logger.log(Level.ALL, "Error: IOException");
            }
        }
        String jsonString = writer.toString();
        try {
            JSONArray bearStatues = new JSONArray(jsonString);
            for (int i = 0; i < 8; i += 1) {
                JSONObject statue = bearStatues.getJSONObject(i);
                landmarkNameList.add(statue.getString("landmark_name"));
                String coordinatesString = statue.getString("coordinates");
                String [] coordinatesStrings = coordinatesString.split(", ");
                latitudeList.add(Double.parseDouble(coordinatesStrings[0]));
                longitudeList.add(Double.parseDouble(coordinatesStrings[1]));
                String filename = statue.getString("filename");
                filenameList.add(filename);
            }
        } catch (JSONException e) {
            logger.log(Level.ALL, "Error: JSONException");
        }
        for (int i = 0; i < 8; i += 1) {
            statueButtons.add(new StatueButton(landmarkNameList.get(i), filenameList.get(i)));
        }
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        createLocationCallback();
        createLocationRequest();
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_CODE);
        }
        fusedLocationClient.requestLocationUpdates(locationRequest, locationCallback, Looper.myLooper());
        updateLocationButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateLocation();
            }
        });
        initLocation();
    }

    private void addButtons() {
        mAdapter = new StatueButtonAdapter(this, statueButtons);
        mRecyclerView.setAdapter(mAdapter);
    }

    private void initLocation() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_CODE);
        }
        fusedLocationClient.getLastLocation().addOnSuccessListener(this, new OnSuccessListener<Location>() {
            @Override
            public void onSuccess(Location location) {
                if (location != null) {
                    currentLocation = location;
                    updateLocation();
                }
            }
        });
        addButtons();
    }

    private void updateLocation() {
        for (int i = 0; i < 8; i += 1) {
            double latitude = latitudeList.get(i);
            double longitude = longitudeList.get(i);
            Location statueLocation = new Location("");
            statueLocation.setLatitude(latitude);
            statueLocation.setLongitude(longitude);
            double distance = statueLocation.distanceTo(currentLocation);
            distances.add(distance);
            statueButtons.get(i).changeDistance(distance);
        }
        addButtons();
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }

    private void createLocationCallback() {
        locationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(LocationResult locationResult) {
                super.onLocationResult(locationResult);
                currentLocation = locationResult.getLastLocation();
            }
        };
    }

    private void createLocationRequest() {
        locationRequest = new LocationRequest();
        locationRequest.setInterval(10000);
        locationRequest.setFastestInterval(5000);
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
    }

    public void openCommentFeed(String name) {
        Intent commentIntent = new Intent(this, CommentFeedActivity.class);
        commentIntent.putExtra("statue name", name);
        commentIntent.putExtra("username", username);
        startActivity(commentIntent);
    }

}