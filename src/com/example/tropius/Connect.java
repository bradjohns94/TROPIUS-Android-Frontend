package com.example.tropius;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.net.InetAddress;
import java.net.UnknownHostException;

public class Connect extends Activity {
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_connect);
		tryConnection();
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
	
	public void retry(View view) {
		/* This method literally just exists for the onClick for retry.
		 * All it does is launch tryConnection...
		 */
		tryConnection();
	}
	
	public void tryConnection() {
		/* Create a new thread to start a connection over the network
		 * If the connection is successful, launch the control activity,
		 * otherwise display an error message and make the retry and
		 * configure buttons visible.
		 */
		new Thread(new Runnable() {
			public void run() {
				final Intent toControl = connect();
				if (toControl != null) {
					runOnUiThread(new Runnable() {
						public void run() {
							// Start the control activity
							startActivity(toControl);
						}
					});
				} else {
					runOnUiThread(new Runnable() {
						public void run() {
							// Display an error message
							String message = ErrorMessage.getErrorMessage();
							TextView error = (TextView)findViewById(R.id.error);
							error.setText(message);
							error.setVisibility(View.VISIBLE);
							// Show the retry and configure buttons
							Button retry = (Button)findViewById(R.id.retry);
							Button config = (Button)findViewById(R.id.configure);
							retry.setVisibility(View.VISIBLE);
							config.setVisibility(View.VISIBLE);
						}
					});
				}
			}
		}).start();
	}
	
	public Intent connect() {
		/* Attempt to establish a connection with the configured
		 * TROPIUS endpoint. If a connection can be established,
		 * return an Intent object packed with an IP extra ready
		 * to transition to the next activity. Otherwise return null
		 */
		// Initialize the intent
		Intent ret = new Intent(this, Control.class);
		// Start by getting the network data from the shared preferences file
        SharedPreferences settings = getSharedPreferences("network_data", MODE_PRIVATE);
        String publicIP = settings.getString("public_ip", "0.0.0.0");
        String privateIP = settings.getString("private_ip", "0.0.0.0");
        
        // Attempt to establish a connection with the given TROPIUS host
        try {
        	// Initially, we try to connect to the public IP
        	InetAddress pub = InetAddress.getByName(publicIP);
        	if (!pub.isReachable(5000)) {
        		throw new UnknownHostException("Could not connect to given public IP");
        	}
        	ret.putExtra("IP", publicIP);
        } catch (Exception pubError) { // TODO This is terrible practice, don't do this
        	// If connecting to the public IP fails, try the private (Some routers are wonky)
        	try {
        		InetAddress priv = InetAddress.getByName(privateIP);
        		if (!priv.isReachable(5000)) {
        			throw new UnknownHostException("Could not connect to the given private IP");
        		}
        	} catch (Exception privError) { // TODO still terrible
        		return null; // Can't connect, can't move on to next activity
        	}
        	ret.putExtra("IP", privateIP);
        }
        // If we didn't return, the connection was successful. Proceed to control
        return ret;
	}
	
	public void configure(View view) {
		Intent toConfig = new Intent(this, Config.class);
		startActivity(toConfig);
	}
}
