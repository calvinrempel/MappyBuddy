package DAO;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.LinkedList;
import java.util.List;

import locations.LocationPack;

/**
 * Created by Marc on 2014-10-20.
 */
public class LocationPackAccess {
    private static final int DATABASE_VERSION      = 19;
    private static final String TABLE_NAME         = "LocationPack";
    private static final String DATABASE_NAME      = "locationPackDatabase";
    private static final String ID_ATTRIBUTE       = "_id";
    private static final String NAME_ATTRIBUTE     = "name";
    private static final String EDITABLE_ATTRIBUTE = "editable";
    private SQLiteDatabase READ_DB;
    private SQLiteDatabase WRITE_DB;
    private static LocationPackAccess instance;

    public static LocationPackAccess getInstance( Context context )
    {
        if ( instance == null )
        {
            instance = new LocationPackAccess( context );
        }

        return instance;
    }

    private LocationPackAccess( Context context )
    {
        READ_DB = new LocationPackDatabase( context ).getReadableDatabase();
        WRITE_DB = new LocationPackDatabase( context ).getWritableDatabase();
    }

    /**
     * This function returns a LocationPack From the Server.
     *
     * @param name This should be one string with the name, multiple elements will cause undefined behaviour.
     * @return a location pack with the name of the given location pack.
     */
    public LocationPack getLocationPack( Context context, String... name )
    {

        Cursor c = READ_DB.rawQuery("SELECT * FROM " + TABLE_NAME + " WHERE " + NAME_ATTRIBUTE + " =  ?", name);

        return new LocationPack( context,
                            c.getString(c.getColumnIndex(NAME_ATTRIBUTE)),
                           ( c.getInt( c.getColumnIndex( EDITABLE_ATTRIBUTE ) ) != 0  ),
                             c.getInt( c.getColumnIndex(ID_ATTRIBUTE)));
    }

    public List<LocationPack> getAllLocationPacks( Context context )
    {
        Cursor c = READ_DB.rawQuery("SELECT * FROM " + TABLE_NAME, new String[0]);
        List<LocationPack> packs = new LinkedList<LocationPack>();

        if ( c.getCount() < 1 )
        {
            return packs;
        }

        while ( c.moveToNext() )
        {
            int index = c.getColumnIndex( ID_ATTRIBUTE );
            int id = c.getInt( index );

            int numCols = c.getColumnCount();
            String names[] = c.getColumnNames();

            packs.add( new LocationPack( context,
                    c.getString(c.getColumnIndex(NAME_ATTRIBUTE)),
                    ( c.getInt( c.getColumnIndex( EDITABLE_ATTRIBUTE ) ) != 0  ),
                    c.getInt( c.getColumnIndex(ID_ATTRIBUTE))) );
        }

        return packs;
    }

    public void delete( LocationPack pack )
    {
        int id = pack.getId();

        WRITE_DB.execSQL( "DELETE FROM " + TABLE_NAME + " WHERE " + ID_ATTRIBUTE + " = " + pack.getId() );
    }

    /**
     * Inserts a location into the database.
     *
     * NOTE: This method does not add the Locations themselves, that must be done separately.
     *
     * @param pack The LocationPack to store.
     * @return The ID in the DB for the LocationPack
     */
    public long insertLocationPack( LocationPack pack, boolean isEditable )
    {
        ContentValues values = new ContentValues();

        values.put(NAME_ATTRIBUTE, pack.getName());
        values.put(EDITABLE_ATTRIBUTE, isEditable? 1 : 0  );


        long id = WRITE_DB.insertWithOnConflict(TABLE_NAME, null, values, SQLiteDatabase.CONFLICT_REPLACE );
        pack.setId( (int) id );

        return id;
    }

    /**
     *  This is the database to be used for managing location packs. Its super nifty.
     */
    private class LocationPackDatabase extends SQLiteOpenHelper
    {
        private static final String PACKAGE_DATABASE_CREATE =
                "CREATE TABLE " + TABLE_NAME
                        + "("
                        + ID_ATTRIBUTE +      " INTEGER PRIMARY KEY, "
                        + NAME_ATTRIBUTE +    " TEXT, "
                        + EDITABLE_ATTRIBUTE + " INTEGER "
                        + ")";
        private static final String PACKAGE_DATABASE_UPDATE =
                "ALTER TABLE " + TABLE_NAME + " ADD COLUMN " + EDITABLE_ATTRIBUTE + " INTEGER";
        private static final String PACKAGE_DATABASE_UPDATE2 =
                "ALTER TABLE " + TABLE_NAME + " MODIFY COLUMN " + ID_ATTRIBUTE + " INTEGER auto_increment";

        LocationPackDatabase( Context context )
        {
            super(context, DATABASE_NAME, null, DATABASE_VERSION );
        }


        @Override
        public void onCreate(SQLiteDatabase db)
        {
            db.execSQL(PACKAGE_DATABASE_CREATE);
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion)
        {
            db.execSQL("DROP TABLE " + TABLE_NAME );
            db.execSQL(PACKAGE_DATABASE_CREATE);
        }
    }
}