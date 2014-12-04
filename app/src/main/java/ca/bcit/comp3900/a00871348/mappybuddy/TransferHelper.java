package ca.bcit.comp3900.a00871348.mappybuddy;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Collection;
import java.util.List;

import locations.Location;
import locations.LocationPack;

/**
 * Created by Georgi on 24-Nov-14.
 */
public class TransferHelper
{
    public static JSONObject locationPack2JSON( LocationPack lp ) throws JSONException
    {
        JSONObject locationPack = new JSONObject();
        locationPack.put( "name", lp.getName() );

        List<Location> locationList = lp.getLocations();//from
        JSONArray locations = new JSONArray();          //to

        for( Location l : locationList )
        {
            JSONObject location = new JSONObject();

            location.put("lat", l.getLatitude() );
            location.put("lon", l.getLongitude() );
            location.put("title", l.getTitle() );
            location.put("prereq", locationList.indexOf(l.getPrereq()));

            locations.put( location );
        }

        locationPack.put( "locations", locations );

        return locationPack;
    }

    public static LocationPack JSON2LocationPack( JSONObject locationPack ) throws JSONException
    {
        LocationPack lp = new LocationPack( locationPack.getString( "name" ), false );

        JSONArray locations = locationPack.getJSONArray( "locations" );//from
        List<Location> locationList = lp.getLocations();               //to

        for( int i = 0; i < locations.length(); ++i )
        {
            JSONObject loc = locations.getJSONObject( i );
            Location location = new Location( (float)loc.getDouble( "lat" )
                    , (float)loc.getDouble( "lon" )
                    , loc.getString( "title" )
                    ,  false );
            locationList.add( location );
        }

        for( int i = 0; i < locations.length(); ++i )
        {
            JSONObject loc = locations.getJSONObject( i );
            Location location = locationList.get( i );
            Location prereq = locationList.get( loc.getInt( "prereq" ) );

            location.setPrereq( prereq );
        }

        return lp;
    }

    public static byte[] locationPacks2byteArray( Collection< LocationPack > packs ) throws JSONException
    {
        JSONArray locationPackArray = new JSONArray();

        for( LocationPack lp : packs )
            locationPackArray.put( locationPack2JSON( lp ) );

        return locationPackArray.toString().getBytes();
    }
}
