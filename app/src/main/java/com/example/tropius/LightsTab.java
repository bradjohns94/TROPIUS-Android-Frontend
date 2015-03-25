package com.example.tropius;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import org.json.JSONObject;

public class LightsTab extends APIAccessor {
	/* class that makes up the lights tab of the control activity. The
	 * lights tab should include features that will activate any command
	 * pertaining to the lights endpoints in the TROPIUS API
	 */
	
	public LightsTab() {
        super();
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragment_tab, container, false);
		TextView deviceText = (TextView)view.findViewById(R.id.device_text);
		deviceText.setText("Lights:");
		return view;
	}
	
	public void asyncCallback(JSONObject response) {
		/* Depending on the response key included in the
		 * JSON response, add the correct fields to the UI
		 */
		// TODO lights stuff
	}
}
