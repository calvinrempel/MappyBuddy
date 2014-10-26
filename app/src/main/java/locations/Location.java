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

    public Location( float _lat, float _lon, String _title, boolean discovered )
    {
        lat = _lat;
        lon = _lon;
        title = _title;
        isDiscovered = discovered;
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
        isDiscovered = true;
    }


    /**
     * Shhhhh... don't tell D'Arcy
     * @return the Name of the LocationPack
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
