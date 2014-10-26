package locations;


import com.google.android.gms.maps.model.LatLng;
import com.google.maps.android.SphericalUtil;

import java.io.Serializable;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by Calvin_2 on 26/10/2014.
 */
public class LocationPack implements Serializable
{
    private List<Location> locations;
    private String name;

    public LocationPack( String name )
    {
        this.name = name;
        locations = new LinkedList<Location>();
    }

    public List<Location> getLocations()
    {
        return locations;
    }

    public void addLocation( Location location )
    {
        locations.add( location );
    }

    public List<Location> getLocationsInRange( float lat, float lon, float radius )
    {
        List<Location> inRange = new LinkedList<Location>();
        Iterator<Location> itr = locations.iterator();

        while ( itr.hasNext() )
        {
            Location loc = itr.next();

            if ( !loc.isLocationDiscovered() ) {
                double dist = SphericalUtil.computeDistanceBetween(new LatLng(lat, lon),
                        new LatLng(loc.getLatitude(), loc.getLongitude()));

                if (dist < radius)
                {
                    inRange.add(loc);
                }
            }
        }

        return inRange;
    }

    /**
     * Shhhhh... don't tell D'Arcy
     * @return the Name of the LocationPack
     */
    public String toString()
    {
        return name;
    }
}
