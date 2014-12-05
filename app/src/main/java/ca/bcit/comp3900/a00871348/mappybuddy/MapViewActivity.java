package ca.bcit.comp3900.a00871348.mappybuddy;

import android.app.ActionBar;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Fragment;
import android.app.FragmentManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.v4.widget.DrawerLayout;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import DAO.LocationAccess;
import DAO.LocationPackAccess;
import DAO.LocationPackLoader;
import locations.LocationPack;


public class MapViewActivity extends Activity
        implements NavigationDrawerFragment.NavigationDrawerCallbacks {

    private enum MODE
    {
        DISCOVER,
        CREATE
    }

    private final int GPS_INTERVAL_TIME_MS = 5000;
    private final int GPS_DISTANCE_DELTA_M = 10;
    private final float CHECK_IN_RADIUS = 500;
    private final int ZOOM_FACTOR = 15;

    static int idgen = 100;
    private static final int LINE_COLOR_DISCOVERED = Color.GREEN;
    private static final int LINE_COLOR_UNDISCOVERED = Color.RED;

    private GoogleMap map;
    private NavigationDrawerFragment drawer;
    private LocationPack activePack;
    private List<LocationPack> packs;
    private LocationListener locListener;
    private LocationManager locManager;
    private Marker me;
    private boolean firstTimeFound;
    private MODE mode;
    private Menu optionsMenu;
    private Map<String, locations.Location> locationMarkers;

    /**
     * Fragment managing the behaviors, interactions and presentation of the navigation drawer.
     */
    private NavigationDrawerFragment mNavigationDrawerFragment;

    /**
     * Used to store the last screen title. For use in {@link #restoreActionBar()}.
     */
    private CharSequence mTitle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map_view);
        if ( map == null )
        {
            try
            {
                locationMarkers = new HashMap<String, locations.Location>();
                map = ((MapFragment) getFragmentManager().findFragmentById(R.id.map)).getMap();
                map.setOnMarkerClickListener( new MarkerListener() );
                me = null;
                firstTimeFound = false;
                drawer = (NavigationDrawerFragment) getFragmentManager().findFragmentById(R.id.navigation_drawer);
                loadLocationPackList();
            }
            catch ( NullPointerException e )
            {
                Toast.makeText( this, "Cannot Load Google Maps.", Toast.LENGTH_LONG ).show();
            }
        }

        // Initialize everything if they haven't already been initialized.
        if (locManager == null)
        {
            locManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
            locListener = new GPSListener();

            findViewById(R.id.getLocationSpinner).setVisibility(View.VISIBLE);


            Location loc = getLastKnownLocationAll();

            if( loc != null )
            {
                UpdateCurrentLocation( loc.getLatitude(), loc.getLongitude() );
            }

            updateLocation();
        }

        mNavigationDrawerFragment = (NavigationDrawerFragment)
                getFragmentManager().findFragmentById(R.id.navigation_drawer);
        mTitle = getTitle();
        setMode( MODE.DISCOVER );

        // Set up the drawer.
        mNavigationDrawerFragment.setUp(
                R.id.navigation_drawer,
                (DrawerLayout) findViewById(R.id.drawer_layout));
    }

    public Location getLastKnownLocationAll()
    {
        Location loc = null;

        if ( locManager.getLastKnownLocation( LocationManager.NETWORK_PROVIDER) != null)
        {
            loc = locManager.getLastKnownLocation( LocationManager.NETWORK_PROVIDER );
            UpdateCurrentLocation( loc.getLatitude(), loc.getLongitude() );
        }
        else if( locManager.getLastKnownLocation( LocationManager.GPS_PROVIDER ) != null)
        {
            loc = locManager.getLastKnownLocation( LocationManager.GPS_PROVIDER);
        }
        else if( locManager.getLastKnownLocation( LocationManager.PASSIVE_PROVIDER ) != null)
        {
            loc = locManager.getLastKnownLocation( LocationManager.PASSIVE_PROVIDER );
        }

        return loc;
    }

    @Override
    public void onNavigationDrawerItemSelected(int position) {
        // update the main content by replacing fragments
        FragmentManager fragmentManager = getFragmentManager();
        fragmentManager.beginTransaction()
                .replace(R.id.container, PlaceholderFragment.newInstance(position + 1))
                .commit();

        if ( drawer != null && map != null )
        {
            LocationPack pack = drawer.getLocationPack( position );

            if ( pack == activePack && pack != null )
            {
                gotoPackDetails( pack );
            }
            else
            {
                setMode( MODE.DISCOVER );
                setActivePack(drawer.getLocationPack(position));
            }
        }
    }

    public void checkIn( View view )
    {
        if (me != null )
        {
            if (mode == MODE.DISCOVER )
            {
                Iterator<LocationPack> packItr = packs.iterator();
                List<locations.Location> inRange;


                while (packItr.hasNext())
                {
                    inRange = packItr.next().getLocationsInRange((float) me.getPosition().latitude,
                            (float) me.getPosition().longitude,
                            CHECK_IN_RADIUS);

                    for (locations.Location anInRange : inRange)
                    {
                        anInRange.checkIn( this );
                    }
                }

                // Reload Markers
                this.setActivePack(activePack);
            }
            else
            {
                if (activePack != null)
                {
                    createLocation();
                }
            }
        }
        else
        {
            Toast.makeText(this, getString(R.string.noLocationFound), Toast.LENGTH_SHORT).show();
        }
    }

    public void loadLocationPackList()
    {
        packs = ( new LocationPackLoader() ).getLocationPacks( this );
        drawer.setContents( packs );
        setActivePack( packs.get( 0 ) );
    }

    public void setActivePack( LocationPack pack )
    {
        map.clear();

        if ( me != null )
        {
            Location loc = getLastKnownLocationAll();
            if ( loc != null )
            {
                UpdateCurrentLocation( loc.getLatitude(), loc.getLongitude() );
            }
        }

        activePack = pack;

        for (locations.Location location : pack.getLocations())
        {
            addLocation(location);
        }
    }


    public void onSectionAttached(int number) {
        if ( number == 2 )
        {
            mTitle = getString(R.string.title_section2);
        }
    }

    public void restoreActionBar() {
        ActionBar actionBar = getActionBar();
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_STANDARD);
        //actionBar.setDisplayShowTitleEnabled(true);
        actionBar.setTitle(mTitle);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if (!mNavigationDrawerFragment.isDrawerOpen())
        {
            getMenuInflater().inflate(R.menu.map_view, menu);
            restoreActionBar();
            return true;
        }
        optionsMenu = menu;

        return super.onCreateOptionsMenu(menu);
    }

    protected void addLocation( locations.Location location )
    {
        String title = location.getTitle();
        MarkerOptions marker = new MarkerOptions();
        marker.position(new LatLng(location.getLatitude(), location.getLongitude()));

        locations.Location prereq = location.getPrereq();
        if ( prereq != null )
        {
            LatLng cur = new LatLng( location.getLatitude(), location.getLongitude() );
            LatLng pre = new LatLng( prereq.getLatitude(), prereq.getLongitude() );
            LatLng center;
            double centerLat, centerLon;

            centerLat = ( ( cur.latitude - pre.latitude ) / 2.0 ) + pre.latitude;
            centerLon = ( ( cur.longitude - pre.longitude ) / 2.0 ) + pre.longitude;
            center = new LatLng( centerLat, centerLon );

            double angle1 = Math.toDegrees(Math.atan2(cur.longitude - pre.longitude, cur.latitude - pre.latitude));

            if ( angle1 < 0 )
            {
                angle1 += 360;
            }

            PolylineOptions polyline = new PolylineOptions();
            polyline.add( cur );
            polyline.add( pre );

            if ( prereq.isLocationDiscovered() && location.isLocationDiscovered() )
            {
                polyline.color( LINE_COLOR_DISCOVERED );
            }
            else
            {
                polyline.color( LINE_COLOR_UNDISCOVERED );
            }

            MarkerOptions arrowMarker = new MarkerOptions();
            arrowMarker.position( center );
            arrowMarker.rotation( (float) angle1 );
            BitmapDescriptor arrow = BitmapDescriptorFactory.fromResource( R.drawable.arrow );
            arrowMarker.icon( arrow );

            map.addPolyline( polyline );
            map.addMarker( arrowMarker );
        }

        marker.title( title );

        BitmapDescriptor bitmap;
        if ( activePack.isEditable() && mode == MODE.CREATE )
        {
            bitmap = BitmapDescriptorFactory.fromResource( R.drawable.created );
        }
        else if ( location.isLocationDiscovered() )
        {
            bitmap = BitmapDescriptorFactory.fromResource( R.drawable.discovered );
        }
        else
        {
            bitmap = BitmapDescriptorFactory.fromResource( R.drawable.undiscovered );
        }

        marker.icon( bitmap );


        Marker mrk = map.addMarker(marker);
        locationMarkers.put( mrk.getId(), location );
    }

    protected void UpdateCurrentLocation( double lat, double lon )
    {
        boolean zoomToPosition = false;

        if ( me != null )
        {
            me.remove();
            me = null;
        }
        else
        {
            zoomToPosition = true;
        }

        findViewById(R.id.getLocationSpinner).setVisibility(View.INVISIBLE);
        me = map.addMarker( ( new MarkerOptions() )
                            .position( new LatLng( lat, lon ) )
                            .title( "You Are Here!" ) );

        if ( zoomToPosition )
        {
            CameraUpdate camUpdate = CameraUpdateFactory.newLatLngZoom(me.getPosition(), ZOOM_FACTOR);
            map.animateCamera( camUpdate );
        }
    }

    /**
     * Request new Location data from the GPS service.
     */
    private void updateLocation()
    {
        locManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER,
                GPS_INTERVAL_TIME_MS,
                GPS_DISTANCE_DELTA_M,
                locListener);
    }

    private void createLocation()
    {

        AlertDialog.Builder alert = new AlertDialog.Builder(this);

        alert.setTitle("Location Name");
        alert.setMessage("Enter Name:");

        // Set an EditText view to get user input
        final EditText input = new EditText(this);
        alert.setView(input);

        alert.setPositiveButton( "Ok", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                String value = input.getText().toString();

                locations.Location loc = new locations.Location( (float) me.getPosition().latitude,
                                                                 (float) me.getPosition().longitude,
                                                                 value,
                                                                 false,
                                                                 idgen++ );
                activePack.addLocation( loc );
                setActivePack( activePack );

                new LocationAccess( MapViewActivity.this ).insertLocation(loc, activePack);
            }

        });

        alert.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
            }
        });

        alert.show();
    }

    public void createLocationPack()
    {
        AlertDialog.Builder alert = new AlertDialog.Builder(this);

        alert.setTitle( "Location Pack Name" );
        alert.setMessage("Enter Name:");

        // Set an EditText view to get user input
        final EditText input = new EditText(this);
        alert.setView(input);

        alert.setPositiveButton( "Ok", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                String value = input.getText().toString();


                LocationPack pack = new LocationPack( value, true );

                packs.add(pack);
                pack.setId((int) new LocationPackAccess( MapViewActivity.this ).insertLocationPack(value, true));

                drawer.setContents(packs);
                setActivePack(pack);
                drawer.select(pack);

                // Save Pack
                setMode( MODE.CREATE );
                Button btn = (Button) findViewById( R.id.button );
                btn.setText("Add Location");


            }
        });

        alert.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton){
            }
        });

        alert.show();
    }

    public void discoverLocationPack()
    {
        setMode( MODE.DISCOVER );
        setActivePack(activePack);
    }

    public void editLocationPack()
    {
        setMode( MODE.CREATE );
        setActivePack( activePack );
    }

    /**
     * A placeholder fragment containing a simple view.
     */
    public static class PlaceholderFragment extends Fragment {
        /**
         * The fragment argument representing the section number for this
         * fragment.
         */
        private static final String ARG_SECTION_NUMBER = "section_number";

        /**
         * Returns a new instance of this fragment for the given section
         * number.
         */
        public static PlaceholderFragment newInstance(int sectionNumber) {
            PlaceholderFragment fragment = new PlaceholderFragment();
            Bundle args = new Bundle();
            args.putInt(ARG_SECTION_NUMBER, sectionNumber);
            fragment.setArguments(args);
            return fragment;
        }

        public PlaceholderFragment() {
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_map_view, container, false);
            return rootView;
        }

        @Override
        public void onAttach(Activity activity) {
            super.onAttach(activity);
            ((MapViewActivity) activity).onSectionAttached(
                    getArguments().getInt(ARG_SECTION_NUMBER));
        }
    }

    public void gotoPackDetails( LocationPack pack )
    {
        Intent intent = new Intent( this, PackDetailsActivity.class );
        intent.putExtra(PackDetailsActivity.BUNDLE_KEY_PACK, pack);
        startActivityForResult(intent, PackDetailsActivity.SELECT_LOCATION_REQUEST);
    }

    @Override
    protected void onActivityResult( int requestCode, int resultCode, Intent data )
    {
        if ( requestCode == PackDetailsActivity.SELECT_LOCATION_REQUEST && resultCode == RESULT_OK && data != null )
        {
            Bundle bundle = data.getExtras();
            locations.Location loc = (locations.Location) bundle.getSerializable( PackDetailsActivity.BUNDLE_KEY_LOCATION );
            LatLng pos = new LatLng( loc.getLatitude(), loc.getLongitude() );
            CameraUpdate camUpdate = CameraUpdateFactory.newLatLngZoom( pos, ZOOM_FACTOR);
            map.animateCamera( camUpdate );
        }
    }

    private void setMode( MODE mode )
    {
        this.mode = mode;

        switch ( mode )
        {
            case DISCOVER:
                ( (Button) findViewById(R.id.button)).setText( "Check In" );
                break;

            case CREATE:
                ( (Button) findViewById(R.id.button)).setText( "Add Location" );
                break;
        }
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
        public void onLocationChanged(Location l)
        {
            UpdateCurrentLocation( l.getLatitude(), l.getLongitude() );
        }

        public void onStatusChanged(String provider, int status, Bundle extras) {}
        public void onProviderEnabled(String s) {}
        public void onProviderDisabled(String s) {}
    }


    public class MarkerListener implements GoogleMap.OnMarkerClickListener
    {
        @Override
        public boolean onMarkerClick(final Marker marker)
        {
            if ( mode == MODE.DISCOVER )
            {
                return false;
            }

            final locations.Location loc = locationMarkers.get( marker.getId() );

            AlertDialog.Builder b = new AlertDialog.Builder( MapViewActivity.this);
            b.setTitle("Set Prereq");

            final locations.Location[] locations = new locations.Location[ activePack.getLocations().size() - 1 ];
            final String[] locationNames = new String[ locations.length + 1 ];
            locationNames[0] = "None";
            int i = 0;

            for ( locations.Location temp : activePack.getLocations() )
            {
                if ( temp.getId() != loc.getId() ) {
                    locationNames[i + 1] = temp.getTitle();
                    locations[i] = temp;
                    i++;
                }
            }

            b.setItems( locationNames, new DialogInterface.OnClickListener() {

                @Override
                public void onClick(DialogInterface dialog, int which)
                {
                    if ( which == 0 )
                    {
                        return;
                    }

                    loc.setPrereq( locations[ which ] );

                    setActivePack( activePack );
                }

            });

            b.show();
            return true;
        }
    }
}
