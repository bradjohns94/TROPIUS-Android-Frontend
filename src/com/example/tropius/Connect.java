package com.example.tropius;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import java.net.InetAddress;
import java.net.UnknownHostException;

public class Connect extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_connect);
		new Thread(new Runnable() {
			public void run() {
				while (!connect());
			}
		});
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.connect, menu);
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
	
	public boolean connect() {
		/* Attempt to establish a connection with the configured
		 * TROPIUS endpoint. If no connection can be established,
		 * display an error message and set the configuration and
		 * retry buttons to be visible.
		 */
		// Initialize the intent
		Intent toControl = new Intent(this, Control.class);
		// Start by getting the network data from the shared preferences file
        SharedPreferences data = this.getPreferences(Activity.MODE_PRIVATE);
        String publicIP = data.getString("Public IP", "0.0.0.0");
        String privateIP = data.getString("Private IP", "0.0.0.0");
        
        // Attempt to establish a connection with the given TROPIUS host
        try {
        	// Initially, we try to connect to the public IP
        	InetAddress pub = InetAddress.getByName(publicIP);
        	if (!pub.isReachable(5)) {
        		throw new UnknownHostException("Could not connect to given public IP");
        	}
        	toControl.putExtra("IP", publicIP);
        } catch (Exception pubError) { // TODO This is terrible practice, don't do this
        	// If connecting to the public IP fails, try the private (Some routers are wonky)
        	try {
        		InetAddress priv = InetAddress.getByName(privateIP);
        		if (!priv.isReachable(5)) {
        			throw new UnknownHostException("Could not connect to the given private IP");
        		}
        	} catch (Exception privError) { // TODO still terrible
        		return false; // Can't connect, can't move on to next activity
        	}
        	toControl.putExtra("IP", privateIP);
        }
        // If we didn't return, the connection was successful. Proceed to control
        startActivity(toControl);
        return true;
	}
}
