package locations;

import android.content.Context;

import java.io.Serializable;

import DAO.LocationAccess;

/**
 * Created by Calvin_2 on 26/10/2014.
 */
public class Location implements Serializable, Comparable<Location>
{
    private float lat;
    private float lon;
    private String title;
    private boolean isDiscovered;
    private Location prereq;
    private int id;

    public int compareTo( Location other )
    {
        if ( other == null )
        {
            return 1;
        }

        if ( other.getId() > getId() )
        {
            return -1;
        }
        if ( other.getId() == getId() )
        {
            return 0;
        }

        return 1;
    }

    public Location( float _lat, float _lon, String _title, boolean discovered )
    {
        lat = _lat;
        lon = _lon;
        title = _title;
        isDiscovered = discovered;

        prereq = null;
    }

    public Location( float _lat, float _lon, String _title, boolean discovered, int _id )
    {
        lat = _lat;
        lon = _lon;
        title = _title;
        isDiscovered = discovered;
        id = _id;

        prereq = null;
    }

    public int getId()
    {
        return id;
    }

    public Location getPrereq()
    {
        return prereq;
    }

    public float getLatitude()
    {
        return lat;
    }

    public float getLongitude()
    {
        return lon;
    }

    public String getTitle()
    {
        return title;
    }

    public boolean isLocationDiscovered()
    {
        return isDiscovered;
    }

    public void checkIn( Context context )
    {
        if ( prereq == null || prereq.isLocationDiscovered() )
        {
            isDiscovered = true;
            LocationAccess.getInstance( context ).updateDiscovered( this );
        }
    }

    public void setPrereq( Location location )
    {
        prereq = location;
    }

    public void setId( long id )
    {
        this.id = (int) id;
    }

    /**
     * Shhhhh... don't tell D'Arcy
     * @return the Name of the Location
     */
    public String toString()
    {
        String state = "\u2714";

        if ( !isLocationDiscovered() )
        {
            state = "\u2718";
        }

        return state + " " + title;
    }
}
