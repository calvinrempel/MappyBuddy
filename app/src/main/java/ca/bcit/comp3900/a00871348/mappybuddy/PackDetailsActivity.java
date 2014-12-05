package ca.bcit.comp3900.a00871348.mappybuddy;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import java.io.Serializable;

import DAO.LocationPackAccess;
import locations.LocationPack;


public class PackDetailsActivity extends Activity {

    public static final String BUNDLE_KEY_PACK = "LocationPack";
    public static final String BUNDLE_KEY_LOCATION = "Location";
    public static final int SELECT_LOCATION_REQUEST = 0;

    private LocationPack pack;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pack_details);

        pack = (LocationPack) getIntent().getSerializableExtra( BUNDLE_KEY_PACK );
        initUI();
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.pack_details, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public void initUI()
    {
        ( (TextView) findViewById( R.id.pack_name ) ).setText( pack.toString() );
        ListView list = (ListView) findViewById( R.id.location_list );

        list.setAdapter(new ArrayAdapter<locations.Location>(
                        this,
                        android.R.layout.simple_list_item_activated_1,
                        android.R.id.text1,
                        pack.getLocations() )
        );

        list.setOnItemClickListener( new ListListener() );
    }

    public void deletePack( View view )
    {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Are you sure?").setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                LocationPackAccess packAccess = new LocationPackAccess( PackDetailsActivity.this );
                packAccess.delete( pack );
            }
        })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                }).show();

    }

    private class ListListener implements AdapterView.OnItemClickListener
    {
        @Override
        public void onItemClick( AdapterView<?> adapterView, View view, int num, long id )
        {
            Intent intent = new Intent();
            locations.Location loc = pack.getLocations().get(num);
            intent.putExtra( BUNDLE_KEY_LOCATION, (Serializable) loc );
            setResult(RESULT_OK, intent);
            finish();
        }
    }
}
