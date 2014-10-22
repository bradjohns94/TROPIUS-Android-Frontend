/** APIAccessor
 * @author Bradley Johns
 * APIAccessor is the abstract superclass that must be implemented by anything
 * that makes a call to the TROPIUS API. APIAccessor comes equipped with an
 * asyncCallback method which should be overridden to suit the needs of the
 * inheriting class as well as methods to send out different forms of HTTP request
 * (GET, POST, PATCH, DELETE, PUT)
 */

package com.example.tropius;

import org.json.JSONObject;

import com.loopj.android.http.AsyncHttpClient;

import android.app.Activity;
import android.app.Fragment;

public abstract class APIAccessor extends Fragment implements APIResponder {
	
	final Activity controller;
	final String baseUrl;
	
	public APIAccessor(Activity contr, String url) {
		controller = contr;
		baseUrl = url;
	}
	
	public void GET(String url) {
		// Send an HTTP GET request to the given URL
		url = baseUrl + url;
		AsyncHttpClient client = new AsyncHttpClient();
		client.setResponseTimeout(5000);
		APIHandler api = new APIHandler(this);
		client.get(url, api);
	}
	
	// TODO add the other http request types
}
