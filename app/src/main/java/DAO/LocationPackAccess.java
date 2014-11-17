package DAO;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by Marc on 2014-10-20.
 */
public class LocationPackAccess {
    private static final int DATABASE_VERSION = 1;
    private static final String TABLE_NAME        = "LocationPack";
    private static final String DATABASE_NAME     = "locationDatabase";
    private static final String ID_ATTRIBUTE      = "_id";
    private static final String NAME_ATTRIBUTE    = "name";
    private final SQLiteDatabase READ_DB  = new LocationDatabase(null).getReadableDatabase();
    private final SQLiteDatabase WRITE_DB = new LocationDatabase(null).getWritableDatabase();

    class LocationDatabase extends SQLiteOpenHelper
    {
        private static final String PACKAGE_DATABASE_CREATE =
                "CREATE TABLE " + TABLE_NAME + "(" +
                        ID_ATTRIBUTE +      " REAL " +
                        NAME_ATTRIBUTE +    " TEXT " +
                        ")";

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

        }
    }
}