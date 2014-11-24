package DAO;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import locations.LocationPack;

/**
 * Created by Marc on 2014-10-20.
 */
public class LocationPackAccess {
    private static final int DATABASE_VERSION = 2;
    private static final String TABLE_NAME         = "LocationPack";
    private static final String DATABASE_NAME      = "locationDatabase";
    private static final String ID_ATTRIBUTE       = "_id";
    private static final String NAME_ATTRIBUTE     = "name";
    private static final String EDITABLE_ATTRIBUTE = "editable";
    private final SQLiteDatabase READ_DB  = new LocationDatabase(null).getReadableDatabase();
    private final SQLiteDatabase WRITE_DB = new LocationDatabase(null).getWritableDatabase();

    public LocationPack getLocationPack( String name )
    {
        SQLiteDatabase db = new LocationDatabase(null).getReadableDatabase();

        String[] args = {name};
        Cursor c = db.rawQuery("SELECT * FROM " + TABLE_NAME + " WHERE " + NAME_ATTRIBUTE + " =  ?", args);

    return new LocationPack( c.getString(c.getColumnIndex(NAME_ATTRIBUTE)),
                           ( c.getInt( c.getColumnIndex( ID_ATTRIBUTE ) ) == 0  ),
                             c.getInt( c.getColumnIndex(ID_ATTRIBUTE)));
    }

    public long insertLocationPack( LocationPack lp )
    {

    }

    private class LocationDatabase extends SQLiteOpenHelper
    {
        private static final String PACKAGE_DATABASE_CREATE =
                "CREATE TABLE " + TABLE_NAME + "(" +
                        ID_ATTRIBUTE +      " REAL " +
                        NAME_ATTRIBUTE +    " TEXT " +
                        EDITABLE_ATTRIBUTE + " INTEGER " +
                        ")";
        private static final String PACKAGE_DATABASE_UPDATE =
                "ALTER TABLE " + TABLE_NAME + " ADD COLUMN " + EDITABLE_ATTRIBUTE + " INTEGER";
        private static final String PACKAGE_DATABASE_UPDATE2 =
                "ALTER TABLE " + TABLE_NAME + " MODIFY COLUMN " + ID_ATTRIBUTE + " INTEGER auto_increment";

        LocationDatabase( Context context )
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
            switch( newVersion )
            {
                case 2:
                    db.execSQL(PACKAGE_DATABASE_UPDATE);
                    db.execSQL(PACKAGE_DATABASE_UPDATE2);
                    break;
            }
        }
    }
}