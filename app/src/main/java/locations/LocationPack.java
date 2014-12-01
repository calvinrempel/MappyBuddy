package locations;


import android.content.Context;

import com.google.android.gms.maps.model.LatLng;
import com.google.maps.android.SphericalUtil;

import java.io.Serializable;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import DAO.LocationAccess;

/**
 * Created by Calvin_2 on 26/10/2014.
 */
public class LocationPack implements Serializable
{
    private boolean isEditable;
    private List<Location> locations;
    private String name;
    private int id;

    public LocationPack( Context context, String name, boolean isEditable, int id )
    {
        this.name = name;
        this.isEditable = isEditable;
        this.id = id;
        locations = new LocationAccess( context ).getLocations(id);
    }

    public LocationPack( String name, boolean isEditable )
    {
        this.name = name;
        this.isEditable = isEditable;
        locations = new LinkedList<Location>();
        //id = 0;
    }

    public void setId( int _id )
    {
        id = _id;
    }


    public int getId()
    {
        return id;
    }

    public List<Location> getLocations()
    {
        return locations;
    }

    public void addLocation( Location location )
    {
        locations.add( location );
    }

    public boolean isEditable()
    {
        return isEditable;
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
     * @deprecated use getName instead.
     * @return the Name of the LocationPack
     */
    @Deprecated
    public String toString()
    {
        return name;
    }

    /**
     * Gets the name of the Location Pack
     *
     * @return name of the location pack
     */
    public String getName()
    {
        return name;
    }
}
