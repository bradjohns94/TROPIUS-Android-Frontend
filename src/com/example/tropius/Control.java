/** Control
 * @author Bradley Johns
 * The main class in charge of handling the interaction between the android frontend and the
 * TROPIUS API. The control activity should make up a tabbed layout for different types of
 * commands to be processed by the API and forward the input from the user to the TROPIUS
 * backed as a valid RESTFUL http request
 */

package com.example.tropius;

import android.app.ActionBar;
import android.app.ActionBar.Tab;
import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;


public class Control extends Activity {

	protected String connectionIP; // Protected for future implementations of "Advanced" activities
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		/* Specify the base url to connect to and initialize the
		 * tabs
		 */
		// Get the accessible IP from the connect activity
		Intent intent = getIntent();
		connectionIP = intent.getStringExtra("IP");
		String baseUrl = "http://" + connectionIP + ":8073";
		// Initialize the tab variables
		ActionBar.Tab hosts, lights;
		Fragment hostTab = new HostTab(this, baseUrl);
		Fragment lightsTab = new LightsTab(this, baseUrl);
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
}
