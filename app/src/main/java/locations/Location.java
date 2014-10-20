package locations;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by Marc on 2014-10-20.
 */
public class Location {
    private static final int DATABASE_VERSION = 1;
    private static final String TABLE_NAME        = "Location";
    private static final String DATABASE_NAME     = "locationDatabase";
    private static final String ID_ATTRIBUTE      = "_id";
    private static final String NAME_ATTRIBUTE    = "name";
    private static final String X_ATTRIBUTE       = "x";
    private static final String Y_ATTRIBUTE       = "y";
    private static final String PACKAGE_ATTRIBUTE = "packageId";
    private final SQLiteDatabase READ_DB  = new LocationDatabase(null).getReadableDatabase();
    private final SQLiteDatabase WRITE_DB = new LocationDatabase(null).getWritableDatabase();

    class LocationDatabase extends SQLiteOpenHelper
    {
        private static final String LOCATION_DATABASE_CREATE =
                "CREATE TABLE " + TABLE_NAME + "(" +
                        ID_ATTRIBUTE +      " REAL " +
                        NAME_ATTRIBUTE +    " TEXT " +
                        Y_ATTRIBUTE +       " REAL " +
                        X_ATTRIBUTE +       " REAL " +
                        PACKAGE_ATTRIBUTE + " INTEGER " +
                ")";

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

        }
    }
}
