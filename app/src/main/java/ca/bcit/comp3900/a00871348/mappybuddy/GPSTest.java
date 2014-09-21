package ca.bcit.comp3900.a00871348.mappybuddy;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.location.LocationProvider;
import android.os.Bundle;
import android.provider.Settings;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

/**
 * Testing GPS functionality
 */
public class GPSTest extends Activity {
    private LocationListener locListener;
    private LocationManager locManager;
    private TextView locationLabel;
    private TextView statusLabel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gpstest);

        // Initialize everything if they haven't already been initialized.
        if (locManager == null) {
            locManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
            locationLabel = (TextView) findViewById(R.id.location_label);
            statusLabel = (TextView) findViewById(R.id.gps_status_label);
            locListener = new GPSListener();
            updateLocation();
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.gpstest, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        return super.onOptionsItemSelected(item);
    }

    /**
     * Update the current location and print the results to screen.
     *
     * @param view the view that triggered the action.
     */
    public void updateLocation(View view) {
        // If GPS is not enabled, ask the user to enable it!
        if (!locManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
            startActivity(intent);
        }

        // If the GPS is now active, update the current position
        if (locManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            updateLocation();

            // If a cached "last known location" exists, display that until new results come in.
            if (locManager.getLastKnownLocation(LocationManager.GPS_PROVIDER) != null) {
                Location l = locManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);

                locationLabel.setText("Lat: " + l.getLatitude() + "\n"
                        + "Lon: " + l.getLongitude());
            } else {
                locationLabel.setText(R.string.gps_waiting);
            }
        }
        // If GPS is still inactive, inform the user.
        else {
            locationLabel.setText(R.string.gps_disabled);
        }
    }

    /**
     * Request new Location data from the GPS service.
     */
    private void updateLocation() {
        locManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,
                5000,
                10,
                locListener);
    }

    /**
     * A LocationListener responds to actions fired by the GPS service.
     */
    private class GPSListener implements LocationListener {
        /**
         * When a new location is requested, update update the onscreen textview.
         *
         * @param l
         */
        public void onLocationChanged(Location l) {
            locationLabel.setText("Lat: " + l.getLatitude() + "\n"
                    + "Lon: " + l.getLongitude());

            // Stop listening for location changes immediately after the first is found.
            locManager.removeUpdates(this);
        }

        /**
         * Show the user some information about the status of the GPS service.
         *
         * @param provider
         * @param status
         * @param extras
         */
        public void onStatusChanged(String provider, int status, Bundle extras) {
            if (provider.equals(LocationManager.GPS_PROVIDER)) {
                switch (status) {
                    case LocationProvider.AVAILABLE:
                        statusLabel.setText(R.string.gps_available);
                        break;

                    case LocationProvider.OUT_OF_SERVICE:
                        statusLabel.setText(R.string.gps_no_service);
                        break;

                    case LocationProvider.TEMPORARILY_UNAVAILABLE:
                        statusLabel.setText(R.string.gps_temp_unavailable);
                        break;
                }
            }
        }

        public void onProviderEnabled(String s) {
        }

        public void onProviderDisabled(String s) {
        }
    }
}
