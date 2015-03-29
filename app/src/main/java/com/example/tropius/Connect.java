/** Connect
 * @author Bradley Johns
 * The class responsible for making first contact with the TROPIUS backend
 * when the app is started. Upon success the activity will forward the user
 * to the Control activity and on failure will throw a quirky error message
 * and redirect the user to either try again or reconfigure
 */

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

import com.loopj.android.http.AsyncHttpClient;

import org.json.JSONException;
import org.json.JSONObject;

public class Connect extends Activity implements APIResponder {
	
	/* The state variable tells which state of connecting the activity
	 * is in. 0 for when the private address has not been tested,
	 * 1 for when the private address has been tested but the public
	 * address has not, and 2 if both attempts failed.
	 */
	private int state;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		/* Initialize the state variable and attempt to connect
		 * to the private IP address specified in config
		 */
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_connect);
		state = 0;
		connect();
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
		/* Set the Settings and try again button to be invisible. Set the
		 * Error text to be invisible, and re-run connect
		 */
		// Remove the error display
		Button retry = (Button)findViewById(R.id.retry);
		Button config = (Button)findViewById(R.id.configure);
		retry.setVisibility(View.GONE);
		config.setVisibility(View.GONE);
		TextView error = (TextView)findViewById(R.id.error);
		error.setVisibility(View.GONE);
		// Retry the connection
		connect();
	}
	
	@Override
	public void asyncCallback(JSONObject response) {
		/* Check the response key to see if the connection
		 * was a success or not. If so, switch to the control
		 * activity connecting to the IP address the TROPIUS server
		 * responded to. Otherwise either increment state or throw an error
		 * depending on the current state.
		 */
		boolean success = false;
		try {
			success = response.getBoolean("success");
		} catch (JSONException ex) {
			// TODO Error handling
		}
		if (success) { // Get the connection IP and start control
			SharedPreferences settings = getSharedPreferences("network_data", MODE_PRIVATE);
			String ip = "";
			if (state == 0) ip = settings.getString("private_ip", "0.0.0.0");
			else ip = settings.getString("public_ip", "0.0.0.0");
			startControl(response, ip);
		} else if (state == 0) { // Try to connect to the public IP
			state++;
			connect();
		} else { // Display an error
			showError();
		}
	}

    @Override
    public void getProgressUpdate(double progress) {
        // This is unncecessary for this activity
    }

    public void showError() {
		/* Get a miscellaneous pokemon quote from the ErrorMessage class, then
		 * display it as well as set the retry and reconfigure buttons to be
		 * visible
		 */
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
	
	public void connect() {
		/* Send an asynchronous http request to the specified TROPIUS server. The address
		 * the request is sent to is determined by the state variable. If the request is
		 * processed within 5 seconds then the app will connect to the given IP, otherwise
		 * the state will be incremented.
		 */
		// Initialize the intent
		// Start by getting the network data from the shared preferences file
        SharedPreferences settings = getSharedPreferences("network_data", MODE_PRIVATE);
        String publicIP = settings.getString("public_ip", "0.0.0.0");
        String privateIP = settings.getString("private_ip", "0.0.0.0");
        
        // Attempt to establish a connection with the given TROPIUS host
        String ip = privateIP;
        if (state == 1) ip = publicIP;
        String url = "http://" + ip + ":8073";
        url += "/TROPIUS/connection/test";
        System.out.println("Attempting http GET to " + url);
        AsyncHttpClient client = new AsyncHttpClient();
		client.setResponseTimeout(2000);
		APIHandler api = new APIHandler(this);
		client.get(url, api);
	}
	
	public void startControl(JSONObject response, String IP) {
		/* Pass the connection IP address to a new intent and start
		 * the control activity
		 */
		Intent toControl = new Intent(this, Control.class);
		toControl.putExtra("IP", IP);
		startActivity(toControl);
	}
	
	public void configure(View view) {
		// Return to the configure activity
		Intent toConfig = new Intent(this, Config.class);
		startActivity(toConfig);
	}
}
