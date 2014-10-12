package com.example.tropius;

import android.app.ActionBar;
import android.app.ActionBar.Tab;
import android.app.ActionBar.TabListener;
import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import org.json.*;

import retrofit.Endpoint;
import retrofit.Endpoints;
import retrofit.RestAdapter;
import retrofit.http.*;

public class Control extends Activity {
	
	protected String connectionIP; // Protected for future implementations of "Advanced" activities
	private static String baseUrl;
	private static API api;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// Get the accessible IP from the connect activity
		Intent intent = getIntent();
		connectionIP = intent.getStringExtra("IP");
		baseUrl = "http://" + connectionIP + ":8073/";
		RestAdapter adapter = new RestAdapter.Builder().setEndpoint(baseUrl).build();
		api = adapter.create(API.class);
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
		// TODO document all of this once you have a better understanding of it
		Fragment fragment;
		
		public ControlTabListener(Fragment newFragment) {
			fragment = newFragment;
		}
		
		public void onTabSelected(Tab tab, FragmentTransaction tx) {
			tx.replace(R.id.fragment_container,  fragment);
			// XXX I have no idea what I'm doing! :D
		}
		
		public void onTabUnselected(Tab tab, FragmentTransaction tx) {
			tx.remove(fragment);
		}
		
		public void onTabReselected(Tab tab, FragmentTransaction tx) {}
	}
	
	private class HostTab extends Fragment {
		public View onCreateView(LayoutInflater inflater, ViewGroup container,
								 Bundle savedInstanceState) {
			View view = inflater.inflate(R.layout.fragment_host_tab, container, false);
			// TODO XML stuffs
			try { // Get the JSON data for the hosts list
				String result = api.listHosts();
				JSONObject hostData = new JSONObject(result);
				// For a temporary thing, lets print the first host name
				TextView temp = (TextView)findViewById(R.id.temp);
				temp.setText(result);
			} catch (JSONException e) {
				// TODO something here
			}
			return view;
		}
	}
	
	private class LightsTab extends Fragment {
		public View onCreateView(LayoutInflater inflater, ViewGroup container,
				 Bundle savedInstanceState) {
				View view = inflater.inflate(R.layout.fragment_lights_tab, container, false);
				// TODO XML stuffs
				// TODO actually fill the tab
				return view;
		}
	}
}
