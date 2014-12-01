package locations;

import android.content.Context;

import java.io.Serializable;

import DAO.LocationAccess;

/**
 * Created by Calvin_2 on 26/10/2014.
 */
public class Location implements Serializable
{
    private float lat;
    private float lon;
    private String title;
    private boolean isDiscovered;
    private Location prereq;
    private int id;
    private Context context;

    public Location( Context context, float _lat, float _lon, String _title, boolean discovered )
    {
        lat = _lat;
        lon = _lon;
        title = _title;
        isDiscovered = discovered;
        this.context = context;

        prereq = null;
    }

    public Location( Context context, float _lat, float _lon, String _title, boolean discovered, int _id )
    {
        lat = _lat;
        lon = _lon;
        title = _title;
        isDiscovered = discovered;
        id = _id;
        this.context = context;

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

    public void checkIn()
    {

        if ( prereq == null || prereq.isLocationDiscovered() )
        {
            new LocationAccess( context ).updateDiscovered( this );
            isDiscovered = true;
        }
    }

    public void setPrereq( Location location )
    {
        prereq = location;
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
