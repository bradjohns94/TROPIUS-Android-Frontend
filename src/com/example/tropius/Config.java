package com.example.tropius;

/** Config
 * @author Bradley Johns
 * Run if the TROPIUS app cannot connect to an endpoing
 * This activity is meant to gather the data required to
 * establish a connection to a TROPIUS endpoint.
 */

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import org.apache.commons.validator.routines.InetAddressValidator;

public class Config extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_config);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.config, menu);
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
	
	public void connect(View view) {
		/* Run when the configuration is complete and the
		 * user hits the "connect" button to attempt to connect
		 * to a host. This method then gathers the user-input data,
		 * verifies its validity, stores it in a shared preferences
		 * file and attempts a connection
		 */
		// Get the values from the XML EditText fields
		EditText publicIPField = (EditText)findViewById(R.id.public_ip_edit);
		String publicIP = publicIPField.getText().toString();
		EditText privateIPField = (EditText)findViewById(R.id.private_ip_edit);
		String privateIP = privateIPField.getText().toString();
		EditText usernameField = (EditText)findViewById(R.id.username_edit);
		String username = usernameField.getText().toString();
		EditText passwordField = (EditText)findViewById(R.id.password_edit);
		String password = passwordField.getText().toString();
		
		// Check that the given IP's are valid IPv4 addresses
		InetAddressValidator validator = new InetAddressValidator();
		boolean valid = true;
		if (!validator.isValidInet4Address(publicIP)) {
			// The given IP was invalid, throw an error
			TextView publicIPError = (TextView)findViewById(R.id.public_ip_error);
			publicIPError.setText(publicIP + " is not a valid IPv4 Address");
			publicIPError.setTextColor(Color.RED);
			publicIPError.setVisibility(0);
			valid = false;
		}
		if (!validator.isValidInet4Address(privateIP)) {
			// The given IP was invalid, throw an error
			TextView privateIPError = (TextView)findViewById(R.id.private_ip_error);
			privateIPError.setText(publicIP + " is not a valid IPv4 Address");
			privateIPError.setTextColor(Color.RED);
			privateIPError.setVisibility(0);
			valid = false;
		}
		if (valid) {
			// Write the verified data to a preferences file
			SharedPreferences settings = getSharedPreferences("network_data", MODE_PRIVATE);
			SharedPreferences.Editor editor = settings.edit();
			editor.putString("public_ip", publicIP);
			editor.putString("private_ip", privateIP);
			editor.putString("username", username);
			editor.putString("password", password);
			editor.commit();
			// Start the connect activity
			Intent toConnect = new Intent(this, Connect.class);
			startActivity(toConnect);
		}
	}
}
