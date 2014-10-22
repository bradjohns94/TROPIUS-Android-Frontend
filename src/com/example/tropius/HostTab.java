package com.example.tropius;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

public class HostTab extends APIAccessor {
	/* Class that makes up the hosts tab of the control activity. The
	 * control tab should include features that will activate any command
	 * pertaining to the host endpoints in the TROPIUS API
	 */
	
	private HashMap<String, Integer> nameToId;
	
	public HostTab(Activity controller, String baseUrl) {
		super(controller, baseUrl);
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragment_tab, container, false);
		nameToId = new HashMap<String, Integer>();
		// Set the device text to "Host:"
		TextView deviceText = (TextView)view.findViewById(R.id.device_text);
		deviceText.setText("Host:");
		// Add the host data to the tab
		GET("/TROPIUS/hosts/list");
		return view;
	}
	
	public void asyncCallback(JSONObject response) {
		/* Depending on the response key included in the
		 * JSON response, add the correct fields to the UI
		 */
		String key = response.keys().next().toString();
		if (key.equals("list")) { // Only one item in outer wrapper
			// Get a list of TROPIUS enabled devices
			ArrayList<String> devices = new ArrayList<String>();
			try {
				response = response.getJSONObject(key);
			} catch (JSONException ex) {
				// TODO error handling
			}
			Iterator<String> ids = response.keys();
			while (ids.hasNext()) { // Add all hostnames to the devices array list
				String id = ids.next().toString();
				try { // Java, why you do this?
					String nextDevice = response.getJSONObject(id).getString("devicename");
					devices.add(nextDevice);
					nameToId.put(nextDevice, Integer.parseInt(id));
				} catch (JSONException ex) {
					// TODO error handling
				}
			}
			// Create and add the device spinner
			final ArrayList<String> hosts = devices;
			Spinner deviceSpinner = new Spinner(controller);
			ArrayAdapter<String> adapter 
			= new ArrayAdapter<String>(controller,
					android.R.layout.simple_spinner_dropdown_item,
					hosts);
			deviceSpinner.setAdapter(adapter);
			deviceSpinner.setTextAlignment(Spinner.TEXT_ALIGNMENT_TEXT_END);
			deviceSpinner.setOnItemSelectedListener(new HostChangeListener(this));
			// Add the spinner to the first row of the table layout
			TableRow deviceRow = (TableRow)getView().findViewById(R.id.device);
			TableRow.LayoutParams params = new TableRow.LayoutParams(0, TableRow.LayoutParams.WRAP_CONTENT, (float)0.55);
			params.column = 2;
			deviceRow.addView(deviceSpinner, params);
		}
	}
	
	private class HostChangeListener implements OnItemSelectedListener {
		
		private APIAccessor sender;
		
		public HostChangeListener(APIAccessor parent) {
			sender = parent;
		}
		
		@Override
		public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
			/* Run a get on the currently selected host and populate the UI with the appropriate
			 * data
			 */
			String selected = parent.getItemAtPosition(position).toString();
			int sid = nameToId.get(selected);
			sender.GET("/TROPIUS/hosts/" + sid + "/get");
		}
		
		@Override
		public void onNothingSelected(AdapterView<?> parentView) {
			// TODO I don't know what yet, but something here.
		}
	}
}
