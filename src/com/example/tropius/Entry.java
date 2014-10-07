package com.example.tropius;

/** Entry
 * @author Bradley Johns
 * This activity should never be visible. Its purpose is to
 * determine whether or not there is existing shared preferences
 * data and transition into either the configuration or connection
 * activities accordingly.
 */

import android.app.Activity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.content.SharedPreferences;
import android.content.Intent;


public class Entry extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
    	/* Check if any data (namely the endpoints public IP)
    	 * is stored in the shared preferences file. Attempt
    	 * to connect if it is, 
    	 */
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_entry);
        SharedPreferences data = this.getPreferences(Activity.MODE_PRIVATE);
        // Test if there is an existing config
        String ip = data.getString("Public IP", null);
        if (ip == null) { // No config exists as of yet, go to config activity
        	Intent toConfig = new Intent(this, Config.class);
        	startActivity(toConfig);
        } else { // Attempt to connect to the TROPIUS IP
        	Intent toConnect = new Intent(this, Connect.class);
        	startActivity(toConnect);
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.entry, menu);
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
}
