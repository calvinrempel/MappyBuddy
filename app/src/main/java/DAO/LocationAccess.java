package DAO;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.HashMap;

import locations.Location;
import locations.LocationPack;

/**
 * Created by Marc on 2014-10-20.
 */
public class LocationAccess {
    private static final int DATABASE_VERSION        = 3;
    private static final String TABLE_NAME           = "Location";
    private static final String DATABASE_NAME        = "locationDatabase";
    private static final String ID_ATTRIBUTE         = "_id";
    private static final String PREQ_ATTRIBUTE       = "pre_req";
    private static final String NAME_ATTRIBUTE       = "name";
    private static final String X_ATTRIBUTE          = "x";
    private static final String Y_ATTRIBUTE          = "y";
    private static final String PACKAGE_ATTRIBUTE    = "packageId";
    private static final String DISCOVERED_ATTRIBUTE = "discovered";
    private final SQLiteDatabase READ_DB  = new LocationDatabase(null).getReadableDatabase();
    private final SQLiteDatabase WRITE_DB = new LocationDatabase(null).getWritableDatabase();


    public Location[] getLocations( int packId )
    {
        SQLiteDatabase db = new LocationDatabase( null ).getReadableDatabase();
        // stores a list of the locations with their prereq ids.
        HashMap<Location, Integer> prereq   = new HashMap<Location, Integer>();
        // stores a list of locations with their own Ids.
        HashMap<Integer, Location> retval   = new HashMap<Integer, Location>();


        String[] args = {"" + packId};

        Cursor curse = db.rawQuery( "SELECT " + NAME_ATTRIBUTE + "," + X_ATTRIBUTE + "," + Y_ATTRIBUTE + ", " + DISCOVERED_ATTRIBUTE +
                   " FROM " + TABLE_NAME + " WHERE " + PACKAGE_ATTRIBUTE + " = ?", args );

        while( curse.moveToNext() )
        {
            Location temp = new Location (  curse.getFloat( curse.getColumnIndex( X_ATTRIBUTE ) ),
                curse.getFloat( curse.getColumnIndex( Y_ATTRIBUTE ) ),
                curse.getString( curse.getColumnIndex( NAME_ATTRIBUTE ) ),
                ( curse.getInt( curse.getColumnIndex( DISCOVERED_ATTRIBUTE ) ) != 0 ) );

        }

        return retval.entrySet().toArray(new Location[retval.entrySet().size()]);

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
        SQLiteDatabase db = new LocationDatabase( null ).getWritableDatabase();

        ContentValues location = new ContentValues();

        // for updating columns.
        if( local.getId() != 0 )
        {
            location.put(ID_ATTRIBUTE, local.getId()); // Not sure if this works
        }
        location.put(PREQ_ATTRIBUTE, local.getPrereq().getId() );
        location.put(NAME_ATTRIBUTE, local.getTitle());
        location.put(Y_ATTRIBUTE, local.getLongitude());
        location.put(X_ATTRIBUTE, local.getLongitude());
        location.put(PACKAGE_ATTRIBUTE, pack.getId() );
        location.put(DISCOVERED_ATTRIBUTE, local.isLocationDiscovered()? 1 : 0 );

        // insert returns the last insert id using the MySQLite library.
        // if the item is already in the table, it replaces it. NOTE: conflicts will only appear with
        // a duplicate ID, so make sure the ID is set if you want it to replace.
        return db.insertWithOnConflict(TABLE_NAME, null, location, SQLiteDatabase.CONFLICT_REPLACE);
    }


    class LocationDatabase extends SQLiteOpenHelper
    {
        private static final String LOCATION_DATABASE_CREATE =
                "CREATE TABLE " + TABLE_NAME + "(" +
                        ID_ATTRIBUTE +      " REAL " +
                        PREQ_ATTRIBUTE +    "INTEGER" +
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
                default:
                    break;
            }

        }
    }
}
