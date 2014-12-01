package DAO;

import android.content.Context;

import java.util.List;

import locations.Location;
import locations.LocationPack;

/**
 * Created by Calvin_2 on 26/10/2014.
 */
public class LocationPackLoader
{
    public List<locations.LocationPack> getLocationPacks( Context context )
    {
        List<LocationPack> list;
        LocationPackAccess packAccess = new LocationPackAccess( context );
        list = packAccess.getAllLocationPacks( context );

        if ( list.size() == 0 )
        {
            createDefault( context, packAccess, new LocationAccess( context ) );
        }

        // TEST STUFF ONLY AT THE MINUTE!

/*
        List<locations.LocationPack> list = new LinkedList<locations.LocationPack>();

        locations.LocationPack pack = new locations.LocationPack( "Canadian Legislative Buildings", false );
        pack.addLocation( new Location( 48.419f, -123.37f, "British Columbia", false ) );
        pack.addLocation( new Location(  53.533f, -113.506f, "Alberta", false ) );
        pack.addLocation( new Location(  50.433f, -104.615f, "Saskatchewan", false ) );
        pack.addLocation( new Location(  49.886f, -97.146f, "Manitoba", false ) );
        pack.addLocation( new Location(  43.662f, -79.391f, "Ontario", false ) );
        pack.addLocation( new Location(  46.808f, -71.214f, "Quebec", false ) );
        pack.addLocation( new Location(  47.583f, -52.724f, "Newfoundland", false ) );
        pack.addLocation( new Location(  45.959f, -66.636f, "New Brunswick", false ) );
        pack.addLocation( new Location(  44.648f, -63.574f, "Nova Scotia", false ) );
        pack.addLocation( new Location(  46.235f, -63.125f, "Prince Edward Island", false ) );
        pack.addLocation( new Location(  62.459f, -114.382f, "Northwest Territories", false ) );
        pack.addLocation( new Location(  60.717f, -135.049f, "Yukon", false ) );
        pack.addLocation( new Location(  63.750f, -68.523f, "Nunavut", false ) );
        list.add( pack );

        Location loc1, loc2;

        pack = new locations.LocationPack( "Burnaby Things", false );
        pack.addLocation( loc1 = new Location(  49.249f, -123.001f, "BCIT SE12", false ) );
        pack.addLocation( loc2 = new Location(  49.239f, -122.966f, "Burnaby Village Museum", true ) );

        loc1.setPrereq( loc2 );

        pack.addLocation( new Location(  49.278f, -122.918f, "SFU", false ) );
        pack.addLocation( loc2 = new Location(  49.227f, -122.999f, "Metrotown", false ) );

        pack.addLocation( loc1 = new Location(  49.246f, -123.0017f, "Not My House", false ) );
        loc1.setPrereq( loc2 );

        list.add( pack );
    */
        return list;
    }

    public void createDefault( Context context, LocationPackAccess packAccess, LocationAccess locationAccess )
    {
        locations.LocationPack pack = new locations.LocationPack( "Canadian Legislative Buildings", false );
        pack.addLocation( new Location(  48.419f, -123.37f, "British Columbia", false ) );
        pack.addLocation( new Location(  53.533f, -113.506f, "Alberta", false ) );
        pack.addLocation( new Location(  50.433f, -104.615f, "Saskatchewan", false ) );
        pack.addLocation( new Location(  49.886f, -97.146f, "Manitoba", false ) );
        pack.addLocation( new Location(  43.662f, -79.391f, "Ontario", false ) );
        pack.addLocation( new Location(  46.808f, -71.214f, "Quebec", false ) );
        pack.addLocation( new Location(  47.583f, -52.724f, "Newfoundland", false ) );
        pack.addLocation( new Location(  45.959f, -66.636f, "New Brunswick", false ) );
        pack.addLocation( new Location(  44.648f, -63.574f, "Nova Scotia", false ) );
        pack.addLocation( new Location(  46.235f, -63.125f, "Prince Edward Island", false ) );
        pack.addLocation( new Location(  62.459f, -114.382f, "Northwest Territories", false ) );
        pack.addLocation( new Location(  60.717f, -135.049f, "Yukon", false ) );
        pack.addLocation( new Location(  63.750f, -68.523f, "Nunavut", false ) );

        packAccess.insertLocationPack( pack.getName(), pack.isEditable() );
        for ( Location loc : pack.getLocations() )
        {
            locationAccess.insertLocation( loc, pack );
        }

        Location loc1, loc2;

        pack = new locations.LocationPack( "Burnaby Things", false );
        pack.addLocation( loc1 = new Location(  49.249f, -123.001f, "BCIT SE12", false ) );
        pack.addLocation( loc2 = new Location(  49.239f, -122.966f, "Burnaby Village Museum", true ) );

        loc1.setPrereq( loc2 );

        pack.addLocation( new Location(  49.278f, -122.918f, "SFU", false ) );
        pack.addLocation( loc2 = new Location(  49.227f, -122.999f, "Metrotown", false ) );

        pack.addLocation( loc1 = new Location(  49.246f, -123.0017f, "Not My House", false ) );
        loc1.setPrereq( loc2 );

        packAccess.insertLocationPack( pack.getName(), pack.isEditable() );
        for ( Location loc : pack.getLocations() )
        {
            locationAccess.insertLocation( loc, pack );
        }
    }
}
