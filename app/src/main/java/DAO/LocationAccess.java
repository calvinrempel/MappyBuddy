package DAO;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;

import locations.Location;

/**
 * Created by Marc on 2014-10-20.
 */
public class LocationAccess {
    private static final int DATABASE_VERSION = 2;
    private static final String TABLE_NAME           = "Location";
    private static final String DATABASE_NAME        = "locationDatabase";
    private static final String ID_ATTRIBUTE         = "_id";
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
        ArrayList<Location> retval = new ArrayList<Location>();

        String[] args = {"" + packId};

        Cursor curse = db.rawQuery( "SELECT " + NAME_ATTRIBUTE + "," + X_ATTRIBUTE + "," + Y_ATTRIBUTE + ", " + DISCOVERED_ATTRIBUTE +
                   " FROM " + TABLE_NAME + " WHERE " + PACKAGE_ATTRIBUTE + " = ?", args );

        while( curse.moveToNext() )
        {
            retval.add( new Location (  curse.getFloat( curse.getColumnIndex( X_ATTRIBUTE ) ),
                        curse.getFloat( curse.getColumnIndex( Y_ATTRIBUTE ) ),
                        curse.getString( curse.getColumnIndex( NAME_ATTRIBUTE ) ),
                      ( curse.getInt( curse.getColumnIndex( DISCOVERED_ATTRIBUTE ) ) != 0 ) ) );
        }

        return retval.toArray(new Location[retval.size()]);

    }

    class LocationDatabase extends SQLiteOpenHelper
    {
        private static final String LOCATION_DATABASE_CREATE =
                "CREATE TABLE " + TABLE_NAME + "(" +
                        ID_ATTRIBUTE +      " REAL " +
                        NAME_ATTRIBUTE +    " TEXT " +
                        Y_ATTRIBUTE +       " REAL " +
                        X_ATTRIBUTE +       " REAL " +
                        PACKAGE_ATTRIBUTE + " INTEGER " +
                        DISCOVERED_ATTRIBUTE + " INTEGER " +

                ")";

        private static final String LOCATION_DATABASE_UPDATE =
                "ALTER TABLE " + TABLE_NAME + " ADD COLUMN " + DISCOVERED_ATTRIBUTE + " INTEGER";

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
            }

        }
    }
}
