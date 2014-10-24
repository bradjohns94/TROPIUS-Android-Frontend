package com.example.tropius;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Color;
import android.os.Bundle;
import android.util.DisplayMetrics;
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
	private static final int androidBlue = Color.parseColor("#3399FF");
	
	public HostTab(Activity controller, String baseUrl) {
		super(controller, baseUrl);
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragment_tab, container, false);
		container.setBackgroundColor(Color.parseColor("#E6E6E6"));
		nameToId = new HashMap<String, Integer>();
		// Set the device text to "Host:"
		TextView deviceText = (TextView)view.findViewById(R.id.device_text);
		deviceText.setText("Host:");
		// Add the host data to the tab
		GET("/TROPIUS/hosts/list");
		// Get the table for future use and add a power row
		TableLayout table = (TableLayout)view.findViewById(R.id.table);
		addRow("Power", view);
		// Add the separator line
		addMagicBlueLine(view);
		// Create the media row
		addRow("Media", view);
		addMagicBlueLine(view);
		
		return view;
	}
	
	private void addRow(String rowName, View view) {
		// Add a row to the host view table layout with the given name
		TableLayout table = (TableLayout)view.findViewById(R.id.table);
		TableRow row = new TableRow(controller);
		TableLayout.LayoutParams params = new TableLayout.LayoutParams(TableLayout.LayoutParams.FILL_PARENT,
																	   convertDpToPixel(25, controller));
		row.setPadding(10, 10, 5, 10);
		row.setGravity(Gravity.CENTER);
		TextView text = new TextView(controller);
		text.setTextAppearance(controller, android.R.style.TextAppearance_Medium);
		text.setText(rowName);
		row.addView(text);
		table.addView(row);
	}
	
	private void addMagicBlueLine(View view) {
		/* Rule 1 of Android development: Don't
		 * Rule 2 of Android development: When in doubt, add a blue line
		 * This method is for those of us who broke rule 1, but follow
		 * rule 2
		 */
		TableLayout table = (TableLayout)view.findViewById(R.id.table);
		View magicBlueLine = new View(controller);
		magicBlueLine.setBackgroundColor(androidBlue);
		LayoutParams lineParams = new LayoutParams(LayoutParams.MATCH_PARENT,
												   convertDpToPixel(1, controller));
		table.addView(magicBlueLine, lineParams);
	}
	
	public void asyncCallback(JSONObject response) {
		/* Depending on the response key included in the
		 * JSON response, add the correct fields to the UI
		 */
		String key = response.keys().next().toString();
		if (key.equals("list")) { // Only one item in outer wrapper
			// Get a list of TROPIUS enabled devices
			processList(response);
		} else if (key.equals("get")) {
			// Fill the UI with data about the selected host
			
		}
	}
	
	private void processList(JSONObject response) {
		/* Sub method to async callback for breaking up the code into
		 * more readable chunks. This method processes a response to
		 * a /TROPIUS/hosts/list request by making a host list spinner
		 * out of names from the response, as well as filling the nameToId
		 * map
		 */
		ArrayList<String> devices = new ArrayList<String>();
		try {
			response = response.getJSONObject("list");
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
	
	public static int convertDpToPixel(int intDP, Context context){
		float dp = (float)intDP;
	    Resources resources = context.getResources();
	    DisplayMetrics metrics = resources.getDisplayMetrics();
	    float px = dp * (metrics.densityDpi / 160f);
	    return (int)px;
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
