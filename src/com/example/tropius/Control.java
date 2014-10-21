/** Control
 * @author Bradley Johns
 * The main class in charge of handling the interaction between the android frontend and the
 * TROPIUS API. The control activity should make up a tabbed layout for different types of
 * commands to be processed by the API and forward the input from the user to the TROPIUS
 * backed as a valid RESTFUL http request
 */

package com.example.tropius;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import android.app.ActionBar;
import android.app.ActionBar.Tab;
import android.app.ActionBar.TabListener;
import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import org.json.*;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

public class Control extends Activity implements APIAccessor {

	protected String connectionIP; // Protected for future implementations of "Advanced" activities
	private static String baseUrl;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		/* Specify the base url to connect to and initialize the
		 * tabs
		 */
		// Get the accessible IP from the connect activity
		Intent intent = getIntent();
		connectionIP = intent.getStringExtra("IP");
		baseUrl = "http://" + connectionIP + ":8073";
		// Initialize the tab variables
		ActionBar.Tab hosts, lights;
		Fragment hostTab = new HostTab();
		Fragment lightsTab = new LightsTab();
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_control);

		// Setup the action bar
		ActionBar actionBar = getActionBar();
		actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);
		hosts = actionBar.newTab().setText(R.string.hosts);
		lights = actionBar.newTab().setText(R.string.lights);
		// Add the tab listeners
		hosts.setTabListener(new ControlTabListener(hostTab));
		lights.setTabListener(new ControlTabListener(lightsTab));
		// Add the tabs to the action bar
		actionBar.addTab(hosts);
		actionBar.addTab(lights);
	}

	public void GET(String url) {
		// Send an HTTP GET request to the given URL
		url = baseUrl + url;
		System.out.println("Connecting to " + url);
		AsyncHttpClient client = new AsyncHttpClient();
		APIHandler api = new APIHandler(this);
		client.get(url, api);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.control, menu);
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
	
	public void asyncCallback(JSONObject response) {
		/* Depending on the response key included in the
		 * JSON response, add the correct fields to the UI
		 */
		// For now just make it display the text because I'm lazy...
		TextView text = (TextView)findViewById(R.id.response);
		text.setText(response.toString());
	}

	private class ControlTabListener implements ActionBar.TabListener {
		/* Controls any transactions made between tabs and changes the tab
		 * to the appropriately clicked one
		 */
		Fragment fragment;

		public ControlTabListener(Fragment newFragment) {
			fragment = newFragment;
		}

		@Override
		public void onTabSelected(Tab tab, FragmentTransaction tx) {
			// Switch the current view to the selected tab
			tx.replace(R.id.fragment_container,  fragment);
		}

		@Override
		public void onTabUnselected(Tab tab, FragmentTransaction tx) {
			// Take the view selected tab
			tx.remove(fragment);
		}

		@Override
		public void onTabReselected(Tab tab, FragmentTransaction tx) {}
	}

	private class HostTab extends Fragment {
		/* Subclass that makes up the hosts tab of the control activity. The
		 * control tab should include features that will activate any command
		 * pertaining to the host endpoints in the TROPIUS API
		 */
		@Override
		public View onCreateView(LayoutInflater inflater, ViewGroup container,
				Bundle savedInstanceState) {
			View view = inflater.inflate(R.layout.fragment_host_tab, container, false);
			// TODO XML stuffs
			// TODO probably move this so it doesn't run every time
			// Lets just do a host list request
			GET("/TROPIUS/hosts/list");
			return view;
		}
	}

	private class LightsTab extends Fragment {
		/* Subclass that makes up the lights tab of the control activity. The
		 * lights tab should include features that will activate any command
		 * pertaining to the lights endpoints in the TROPIUS API
		 */
		@Override
		public View onCreateView(LayoutInflater inflater, ViewGroup container,
				Bundle savedInstanceState) {
			View view = inflater.inflate(R.layout.fragment_lights_tab, container, false);
			// TODO XML stuffs
			// TODO actually fill the tab
			return view;
		}
	}
}
