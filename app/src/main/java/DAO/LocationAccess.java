package DAO;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;
import java.util.Map;
import java.util.TreeMap;

import locations.Location;
import locations.LocationPack;

/**
 * Created by Marc on 2014-10-20.
 */
public class LocationAccess {
    private static final int DATABASE_VERSION        = 16;
    private static final String TABLE_NAME           = "Location";
    private static final String DATABASE_NAME        = "locationDatabase";
    private static final String ID_ATTRIBUTE         = "_id";
    private static final String PREQ_ATTRIBUTE       = "pre_req";
    private static final String NAME_ATTRIBUTE       = "name";
    private static final String X_ATTRIBUTE          = "x";
    private static final String Y_ATTRIBUTE          = "y";
    private static final String PACKAGE_ATTRIBUTE    = "packageId";
    private static final String DISCOVERED_ATTRIBUTE = "discovered";
    private SQLiteDatabase READ_DB;
    private SQLiteDatabase WRITE_DB;
    private Context context;


    public LocationAccess( Context context )
    {
        READ_DB  = new LocationDatabase( context ).getReadableDatabase();
        WRITE_DB = new LocationDatabase( context ).getWritableDatabase();
        this.context = context;
    }

    public ArrayList<Location> getLocations( int packId )
    {
        // stores a list of the locations with their prereq ids.
        TreeMap<Location, Integer> prereq   = new TreeMap<Location, Integer>();
        // stores a list of locations with their own Ids.
        TreeMap<Integer, Location> retval   = new TreeMap<Integer, Location>();


        String[] args = {"" + packId};

        Cursor curse = READ_DB.rawQuery( "SELECT " + ID_ATTRIBUTE + ", " + NAME_ATTRIBUTE + "," + X_ATTRIBUTE + "," + Y_ATTRIBUTE + ", " + DISCOVERED_ATTRIBUTE + ", " + PREQ_ATTRIBUTE +
                   " FROM " + TABLE_NAME + " WHERE " + PACKAGE_ATTRIBUTE + " = ?", args );

        while( curse.moveToNext() )
        {
            Location temp = new Location (  curse.getFloat(  curse.getColumnIndex( X_ATTRIBUTE ) ),
                                            curse.getFloat(  curse.getColumnIndex( Y_ATTRIBUTE ) ),
                                            curse.getString( curse.getColumnIndex( NAME_ATTRIBUTE ) ),
                                            ( curse.getInt(  curse.getColumnIndex( DISCOVERED_ATTRIBUTE ) ) != 0 ),
                                            curse.getInt(curse.getColumnIndex(ID_ATTRIBUTE)) );

            // Loads up a list of pre requists to be set after the list has loaded.
            prereq.put( temp, curse.getInt( curse.getColumnIndex( PREQ_ATTRIBUTE )));
            retval.put( curse.getInt(curse.getColumnIndex(ID_ATTRIBUTE)), temp );
        }

        // Set up the pre-req locations.
        for( Map.Entry<Location, Integer> entry : prereq.entrySet() )
        {
            // Add pre req location to map.
            entry.getKey().setPrereq( retval.get(entry.getValue()));
        }

        return new ArrayList<Location> ( retval.values() );
    }


    /**
     * Store a location into the location table. or replace if the ID already exists.
     *
     * @param local The location to be stored in the database.
     * @param pack  The package that the location is stored in.
     * @return the Id of the item inserted.
     */
    public long insertLocation( Location local, LocationPack pack )
    {
        SQLiteDatabase db = new LocationDatabase( context ).getWritableDatabase();

        ContentValues location = new ContentValues();

        if ( local.getPrereq() != null && local.getPrereq().getId() != -1 )
        {
            location.put(PREQ_ATTRIBUTE, local.getPrereq().getId());
        }
        else
        {
            location.put(PREQ_ATTRIBUTE, -1 );
        }

        location.put(NAME_ATTRIBUTE, local.getTitle());
        location.put(Y_ATTRIBUTE, local.getLongitude());
        location.put(X_ATTRIBUTE, local.getLongitude());
        location.put(PACKAGE_ATTRIBUTE, pack.getId() );
        location.put(DISCOVERED_ATTRIBUTE, local.isLocationDiscovered() );

        // insert returns the last insert id using the MySQLite library.
        // if the item is already in the table, it replaces it. NOTE: conflicts will only appear with
        // a duplicate ID, so make sure the ID is set if you want it to replace.
        return db.insertWithOnConflict(TABLE_NAME, null, location, SQLiteDatabase.CONFLICT_REPLACE);
    }

    public long updateDiscovered( Location loc )
    {
        ContentValues visited = new ContentValues();
        String[] update = {"" + loc.getId()};

        visited.put( DISCOVERED_ATTRIBUTE, loc.isLocationDiscovered() );

        return WRITE_DB.update(TABLE_NAME, visited, ID_ATTRIBUTE + " = ?", update);
    }


    class LocationDatabase extends SQLiteOpenHelper
    {
        private static final String LOCATION_DATABASE_CREATE =
                "CREATE TABLE " + TABLE_NAME + "(" +
                        ID_ATTRIBUTE +      " REAL " +
                        PREQ_ATTRIBUTE +    " INTEGER" +
                        NAME_ATTRIBUTE +    " TEXT " +
                        Y_ATTRIBUTE +       " REAL " +
                        X_ATTRIBUTE +       " REAL " +
                        PACKAGE_ATTRIBUTE + " INTEGER " +
                        DISCOVERED_ATTRIBUTE + " INTEGER " +

                ")";

        private static final String LOCATION_DATABASE_UPDATE =
                "ALTER TABLE " + TABLE_NAME + " ADD COLUMN " + DISCOVERED_ATTRIBUTE + " INTEGER";
        private static final String LOCATION_DATABASE_UPDATE_PREREQ =
                "ALETER TABLE " + TABLE_NAME + " ADD COLUMN " + PREQ_ATTRIBUTE + " INTEGER";
        private static final String LOCATION_DATABASE_ID_AUTOUPDATE =
                "ALTER TABLE " + TABLE_NAME + " MODIFY " + ID_ATTRIBUTE + " INTEGER PRIMARY KEY NOT NULL auto_increment";

        LocationDatabase( Context context )
        {
            super(context, DATABASE_NAME, null, DATABASE_VERSION );
        }


        @Override
        public void onCreate(SQLiteDatabase db)
        {
            db.execSQL(LOCATION_DATABASE_CREATE);
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion)
        {
            switch( newVersion )
            {
                case 2:
                    db.execSQL(LOCATION_DATABASE_UPDATE);
                case 3:
                    db.execSQL(LOCATION_DATABASE_UPDATE_PREREQ);
                    db.execSQL(LOCATION_DATABASE_ID_AUTOUPDATE);
                    break;

                case 15:
                    db.execSQL( "DROP TABLE " + TABLE_NAME );
                    db.execSQL( LOCATION_DATABASE_CREATE );

                case 16:
                    System.out.println( LOCATION_DATABASE_CREATE );

                default:
                    break;
            }

        }
    }
}
