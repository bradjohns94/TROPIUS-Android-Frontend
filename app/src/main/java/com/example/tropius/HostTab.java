package com.example.tropius;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Color;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import org.apache.commons.validator.routines.InetAddressValidator;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

public class HostTab extends APIAccessor {
	/* Class that makes up the hosts tab of the control activity. The
	 * control tab should include features that will activate any command
	 * pertaining to the host endpoints in the TROPIUS API
	 */
	
	private HashMap<String, Integer> nameToId;
	private HashMap<String, View> rowByName;
	private JSONObject hostData;
	private String subsection;
	private boolean subsectionMutex;
	private static final int androidBlue = Color.parseColor("#3399FF");

    public HostTab() {
        super();
    }

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragment_tab, container, false);
		container.setBackgroundColor(Color.parseColor("#E6E6E6"));
		nameToId = new HashMap<String, Integer>();
		rowByName = new HashMap<String, View>();
		hostData = new JSONObject();
		subsection = "";
		subsectionMutex = true; // Mutex to not allow changes while requests are being processed
		// Set the device text to "Host:"
		TextView deviceText = (TextView)view.findViewById(R.id.device_text);
		deviceText.setText("Host:");
		// Add the host data to the tab
		GET("/TROPIUS/hosts/list");
		// Get the table for future use and add a power row
		TableLayout table = (TableLayout)view.findViewById(R.id.table);
		RowExpander rowListener = new RowExpander(table);
		addRow("Power", view, rowListener);
		// Add the separator line
		addMagicBlueLine(view);
		// Create the media row
		addRow("Media", view, rowListener);
		addMagicBlueLine(view);
		// Create the add row
        addRow("Add Hosts", view, rowListener);
        addMagicBlueLine(view);
        // Create the remove row
        addRow("Remove Hosts", view, rowListener);
        addMagicBlueLine(view);
		return view;
	}
	
	private void addRow(String rowName, View view, RowExpander rowListener) {
		// Add a row to the host view table layout with the given name
		TableLayout table = (TableLayout)view.findViewById(R.id.table);
		TableRow row = new TableRow(controller);
		TableLayout.LayoutParams params = new TableLayout.LayoutParams(TableLayout.LayoutParams.FILL_PARENT,
																	   convertDpToPixel(25, controller));
		row.setPadding(10, 10, 5, 10);
		row.setGravity(Gravity.CENTER);
		row.setOnClickListener(rowListener);
		TextView text = new TextView(controller);
		text.setTextAppearance(controller, android.R.style.TextAppearance_Medium);
        text.setGravity(Gravity.CENTER);
		text.setText(rowName);
		row.addView(text);
		table.addView(row);
		rowByName.put(rowName, row);
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
			// Set the hostData object
			try {
				hostData = response.getJSONObject("get");
			} catch (JSONException e) {
				// TODO error handling
			}
		} else {
			subsectionMutex = false; // Lock changes while results are processed
			if (key.equals("library") && subsection.equals("Media")) {
				// Populate song selector spinners
				try {
					response = response.getJSONObject("library");
					addSongSelectorSpinners(response);
				} catch (JSONException e) {
					// TODO error handling
				}
			}
			subsectionMutex = true; // unlock subsection changes
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
	
	private void addSongSelectorSpinners(JSONObject library) {
		// Get the media row's base index
		TableLayout table = (TableLayout)getView().findViewById(R.id.table);
		View row = rowByName.get("Media");
		int index = getIndex(table, row);
		/* Rows are set up as title, divider line, content row 1, content row 2, ..., divider
		 * Since we want the row of song selector spinners to be the first content row, and the
		 * index has been calculated to be the view of the title row. Thus, the index needs to be
		 * incremented by 2 to be where the first row of content will be.
		 */
		index += 2;
		SongSelectorSpinner songSelector = new SongSelectorSpinner(controller, library, table, index);
		songSelector.drawSpinners();
		// Add the play button
		Button play = new Button(controller);
		play.setText("Play");
		play.setLayoutParams(new TableRow.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT));
		table.addView(play, index + 3);
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
	
	private int getIndex(TableLayout layout, View view) {
		for (int i = 0; i < layout.getChildCount(); i++) {
			if (view.equals(layout.getChildAt(i))) {
				return i;
			}
		}
		return layout.getChildCount() - 1;
	}
	
	private class RowExpander implements OnClickListener {
		
		private ArrayList<View> added;
        private final TableLayout table;
		
		public RowExpander(TableLayout parent) {
            added = new ArrayList<View>();
            table = parent;
		}
		
		public void onClick(View view) {
			if (!subsectionMutex) return; // Don't change section if mutex locked
			// Remove all previously added views
			for (View toRemove : added) {
				table.removeView(toRemove);
			}
			added = new ArrayList<View>();
			TableRow clicked = (TableRow)view;
			TextView text = (TextView)clicked.getChildAt(0);
			String title = text.getText().toString();
			subsection = title;
			if (title.equals("Power")) {
				expandPowerRow(view);
			} else if (title.equals("Media")) {
				expandMediaRow(view);
			} else if (title.equals("Add Hosts")) {
                expandAddRow(view);
            } else if (title.equals("Remove Hosts")) {
                expandRemoveRow(view);
            }
		}

        private void expandPowerRow(View view) {
            // Insert the divider line
            int index = addDivider(table, view);
            // Insert the data for the power row
            Switch powerSwitch = new Switch(controller);
            boolean checked = false;
            try {
                checked = (hostData.getString("state").equals("on"));
            } catch (JSONException ex) {
                // TODO error handling
            }
            powerSwitch.setChecked(checked);
            powerSwitch.setOnClickListener(new OnClickListener() {
                public void onClick(View view) {
                    try {
                        String id = hostData.getString("sid");
                        POST("/TROPIUS/hosts/" + id + "/power", null);
                    } catch (JSONException ex) {
                        // TODO error handling
                    }
                }
            });
            table.addView(powerSwitch, index);
            added.add(powerSwitch);
        }

        private void expandMediaRow(View view) {
            // Insert the divider line
            addDivider(table, view);
            // Request the library xml file to populate artist selectors
            try {
                String id = hostData.getString("sid");
                GET("/TROPIUS/hosts/" + id + "/music");
            } catch (JSONException e) {
                // TODO error handling
            }
        }

        private void expandAddRow(View view) {
            // Initialize the rows and views
            TableRow nameRow = new TableRow(controller);
            TableRow ipRow = new TableRow(controller);
            TableRow macRow = new TableRow(controller);
            TextView nameText = new TextView(controller);
            TextView ipText = new TextView(controller);
            TextView macText = new TextView(controller);
            final EditText nameField = new EditText(controller);
            final EditText ipField = new EditText(controller);
            final EditText macField = new EditText(controller);
            // Set the text values of the text views
            nameText.setText("Device Name:");
            ipText.setText("Private IP:");
            macText.setText("Mac Address:");
            // Set the parameters of the text views/fields to match the config activity
            nameRow.setPadding(0, 10, 0, 10);
            ipRow.setPadding(0, 10, 0, 10);
            macRow.setPadding(0, 10, 0, 10);
            TableRow.LayoutParams textParams = new TableRow.LayoutParams(0, LayoutParams.WRAP_CONTENT, 0.45f);
            TableRow.LayoutParams fieldParams = new TableRow.LayoutParams(0, TableRow.LayoutParams.WRAP_CONTENT, 0.55f);
            nameText.setLayoutParams(textParams);
            ipText.setLayoutParams(textParams);
            macText.setLayoutParams(textParams);
            nameField.setLayoutParams(fieldParams);
            ipField.setLayoutParams(fieldParams);
            macField.setLayoutParams(fieldParams);
            // Set the hint of the edit texts
            nameField.setHint("Ex. TROPIUS");
            ipField.setHint("Ex. 80.73.80.73");
            macField.setHint("Ex. ef:ef:ef:ef:ef:ef");
            // Add the views to each row
            nameRow.addView(nameText); nameRow.addView(nameField);
            ipRow.addView(ipText); ipRow.addView(ipField);
            macRow.addView(macText); macRow.addView(macField);
            // Add the rows to the table
            final int index = addDivider(table, view);
            table.addView(nameRow, index);
            added.add(nameRow);
            table.addView(ipRow, index + 1);
            added.add(ipRow);
            table.addView(macRow, index + 2);
            added.add(macRow);
            // Create the button to actually perform the add
            Button addHost = new Button(controller);
            addHost.setText("Add Host");
            addHost.setLayoutParams(new TableLayout.LayoutParams(LayoutParams.MATCH_PARENT,
                    LayoutParams.WRAP_CONTENT));
            addHost.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View view) {
                    boolean noErrors = true;
                    String name = nameField.getText().toString();
                    String ip = ipField.getText().toString();
                    String mac = macField.getText().toString();
                    if (!checkIp(ip, table, index + 2)) {
                        noErrors = false;
                    }
                    if (!checkMac(mac)) {
                        // TODO display error text
                        noErrors = false;
                    }
                    if (noErrors) { // Build the JSON Params and post it to TROPIUS
                        try {
                            JSONObject params = new JSONObject();
                            params.put("deviceName", name);
                            params.put("ip", ip);
                            params.put("mac", mac);
                            POST("/TROPIUS/hosts/add", params);
                        } catch (JSONException ex) {
                            // TODO error handling
                        }
                    }
                }
            });
            table.addView(addHost, index + 3);
            added.add(addHost);
        }


        private void expandRemoveRow(View view) {

        }
		
		private int addDivider(TableLayout table, View view) {
			View divider = new View(controller);
			divider.setBackgroundColor(Color.BLACK);
			LayoutParams lineParams = new LayoutParams(LayoutParams.MATCH_PARENT,
													   convertDpToPixel(1, controller));
			int index = getIndex(table, view) + 2;
			table.addView(divider, index, lineParams);
			added.add(divider);
			return index;
		}

        private boolean checkIp(String ip, TableLayout table, int errorIndex) {
            InetAddressValidator validator = new InetAddressValidator();
            if (!validator.isValidInet4Address(ip)) {
                // The given IP was invalid, throw an error
                TextView error = new TextView(controller);
                error.setText(ip + " is not a valid IPv4 Address");
                error.setTextColor(Color.RED);
                table.addView(error, errorIndex);
                return false;
            }
            return true;
        }

        private boolean checkMac(String mac) {
            return true;
        }
	}
}
