package locations;

import java.io.Serializable;

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

    public Location( float _lat, float _lon, String _title, boolean discovered )
    {
        lat = _lat;
        lon = _lon;
        title = _title;
        isDiscovered = discovered;

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
            isDiscovered = true;
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
