package com.example.padstartscherm;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class RequestActivity extends AppCompatActivity {

    private static final int MY_PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 1;
    Button button;
    Button logoutButton;
    public EditText aantalzakken, aantalKlikos, aantalContainers;
    TextView textView;
    private FusedLocationProviderClient fusedLocationClient;
    final String URL_POST = "http://52.157.237.247/Report.php";
    Map<String, String> addressInfo = new HashMap<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState); // Call super method
        setContentView(R.layout.activity_main2); // Show report screen on user's screen

        Resources resources = getResources(); // Get all resources
        String userLogedIn = String.format(resources.getString(R.string.gebruikerTekst), getIntent().getStringExtra("license_plate"));

        // Get elements from view
        aantalzakken = findViewById(R.id.aantalZakken);
        aantalKlikos = findViewById(R.id.aantalKlikos);
        aantalContainers = findViewById(R.id.aantalContainers);
        textView = findViewById(R.id.eersteTekst);

        // Prepare all text elements
        textView.setText(userLogedIn);
        aantalzakken.setText("0");
        aantalKlikos.setText("0");
        aantalContainers.setText("0");

        logoutButton = findViewById(R.id.Uitloggen);

        button = findViewById(R.id.submit);
        // Prepare location services on current activity
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        fetchLocation(); // Get user location on load.

        // Make an attempt to logout the employee
        // Make an attempt to logout the employee
        // Pass in an onClickListener so we can get its onClick() method in order to respond to the users input
        logoutButton.setOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View v) {

                // Create a new AlertDialog.Builder object to create an alert dialog pop-up
                AlertDialog.Builder builder = new AlertDialog.Builder(RequestActivity.this);

                // Make sure the user can't close the dialog when clicking outside the dialog
                builder.setCancelable(false);

                // Set the title of the Alert
                builder.setTitle("Weet u zeker dat u wilt uitloggen?");

                // Create a negative button so the user will be able to cancel the dialog
                // Because we're dealing with an Alert Dialog we're going to pass in a new DialogInterface.OnClickListener interface
                builder.setNegativeButton("Annuleren", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });
                // Create a positive button so the user will be able to logout
                builder.setPositiveButton("Uitloggen", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // Creat an explicit intent to go back to the first intent
                        Intent intent = new Intent(RequestActivity.this, HomeActivity.class);
                        // Log the user out (Start the first activity again)
                        RequestActivity.this.startActivity(intent);
                    }
                });
                builder.show();
            }
        }
        );


        // Make an attempt to report current location and garbage data to the database
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                fetchLocation(); // Get latest location information

                Map<String, String> params = new HashMap<>(); // Reserve a place for data

                // Put each data item in their own variable
                String license_plate = getIntent().getStringExtra("license_plate");
                String city = addressInfo.get("city");
                String area = addressInfo.get("area");
                String street = addressInfo.get("street");
                String house_number = addressInfo.get("home_number");
                String lng = addressInfo.get("long");
                String lat = addressInfo.get("lat");
                String garbage_bags = aantalzakken.getText().toString();
                String containers = aantalContainers.getText().toString();
                String klikos = aantalKlikos.getText().toString();

                // Put all the needed data inside params for the api
                params.put("license_plate", license_plate);
                params.put("city", city);
                params.put("area", area);
                params.put("street", street);
                params.put("house_number", house_number);
                params.put("long", lng);
                params.put("lat", lat);
                params.put("input[garbage_bags]", garbage_bags);
                params.put("input[containers]", containers);
                params.put("input[klikos]", klikos);

                // Make an request to report current location's garbage data
                Request.makeRequest(params, URL_POST, RequestActivity.this, null, "Melding is geplaatst");
            }
        });
    }

    /**
     * This is the method to request the location access from the user.
     * If the user did not give access for the location this will be asked.
     */
    private void fetchLocation() {
        // Here, thisActivity is the current activity
        if (ContextCompat.checkSelfPermission(RequestActivity.this,
                Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {

            // Permission is not granted. Prompt the user to allow the access.
            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(RequestActivity.this,
                    Manifest.permission.ACCESS_FINE_LOCATION)) {
                // Show an explanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.

                new AlertDialog.Builder(this)
                        .setTitle("Locatievoorzienig")
                        .setMessage("Locatievoorziening is nodig om te app te laten werken")
                        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            // When the user presses OK the acces will be granted.
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                ActivityCompat.requestPermissions(RequestActivity.this,
                                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                                        MY_PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION);
                            }
                        })
                        // When the user presses "Annuleer" the access will be denied
                        // and it will be ask again.
                        .setNegativeButton("Annuleer", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        })
                        .create()
                        .show();
            } else {
                // No explanation needed; request the permission
                ActivityCompat.requestPermissions(RequestActivity.this,
                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                        MY_PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION);

                // MY_PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION is an
                // app-defined int constant. The callback method gets the
                // result of the request.
            }
        } else {
            // Permission has already been granted
            fusedLocationClient.getLastLocation().addOnSuccessListener(this, new OnSuccessListener<Location>() {
                @Override
                public void onSuccess(Location location) {
                    // Got last known location.
                    // Location can be null if the GPS is turned off.
                    if (location != null) {
                        // Get GPS coordinates' address information
                        geoLocateAddress(location.getLatitude(), location.getLongitude());
                    }
                }
            });
        }
    }

    /**
     * Changes the latitude en longitude the an address.
     *
     * @param lat The latitude of the location.
     * @param lng The longitude of the location.
     */
    public void geoLocateAddress(double lat, double lng) {
        Geocoder gc = new Geocoder(this);

        // Try to get address information from given GPS coordinates
        try {
            List<Address> list = gc.getFromLocation(lat, lng, 1);
            Address add = list.get(0); // Get first result from getFromLocation method

            // Put all the needed data in their own variables
            String streetname = add.getThoroughfare();
            String homenumber = add.getSubThoroughfare();
            String city = add.getLocality();
            String area = add.getSubLocality();
            String lnglng = String.valueOf(add.getLongitude());
            String latlat = String.valueOf(add.getLatitude());

            // Put all address information into the global variable
            addressInfo.put("street", streetname);
            addressInfo.put("home_number", homenumber);
            addressInfo.put("city", city);
            addressInfo.put("area", area);
            addressInfo.put("long", lnglng);
            addressInfo.put("lat", latlat);

        } catch (IOException e) {
            e.printStackTrace();
        }

    }

}
