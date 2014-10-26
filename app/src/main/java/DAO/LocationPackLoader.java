package DAO;

import java.util.LinkedList;
import java.util.List;

import locations.Location;

/**
 * Created by Calvin_2 on 26/10/2014.
 */
public class LocationPackLoader
{
    public List<locations.LocationPack> getLocationPacks()
    {
        // TEST STUFF ONLY AT THE MINUTE!

        List<locations.LocationPack> list = new LinkedList<locations.LocationPack>();

        locations.LocationPack pack = new locations.LocationPack( "Random Places" );
        pack.addLocation( new Location( 128, 32, "My House", true ) );
        pack.addLocation( new Location( 18, 32, "Your House", false ) );
        pack.addLocation( new Location( 28, 32, "Their House", true ) );
        pack.addLocation( new Location( 18, 32, "Our House", false ) );
        list.add( pack );

        pack = new locations.LocationPack( "Less Random Places" );
        pack.addLocation( new Location( 49.249f, -123.001f, "BCIT SE12", false ) );
        pack.addLocation( new Location( 32, 20, "Your Place", false ) );
        pack.addLocation( new Location( 32, 30, "Their Place", true ) );
        pack.addLocation( new Location( 32, 40, "Our Place", false ) );
        list.add( pack );

        return list;
    }
}
