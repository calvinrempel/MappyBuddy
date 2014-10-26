package ca.bcit.comp3900.a00871348.mappybuddy;

import android.app.Activity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import locations.LocationPack;


public class PackDetailsActivity extends Activity {

    public static final String BUNDLE_KEY_PACK = "LocationPack";

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
    }
}
