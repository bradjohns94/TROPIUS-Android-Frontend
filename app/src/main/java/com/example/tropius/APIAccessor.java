/** APIAccessor
 * @author Bradley Johns
 * APIAccessor is the abstract superclass that must be implemented by anything
 * that makes a call to the TROPIUS API. APIAccessor comes equipped with an
 * asyncCallback method which should be overridden to suit the needs of the
 * inheriting class as well as methods to send out different forms of HTTP request
 * (GET, POST, PATCH, DELETE, PUT)
 */

package com.example.tropius;

import android.app.Activity;
import android.app.Fragment;
import android.os.Bundle;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.RequestParams;

import org.apache.http.entity.StringEntity;
import org.apache.http.message.BasicHeader;
import org.apache.http.protocol.HTTP;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.HashMap;

public abstract class APIAccessor extends Fragment implements APIResponder {
	
	Activity controller;
	String baseUrl;

    public APIAccessor() {
        controller = getActivity();
        baseUrl = "";
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        controller = activity;
    }

	public void GET(String url) {
        // Retrieve the base url from the args bundle
        Bundle args = this.getArguments();
        baseUrl = args.getString("url");
		// Send an HTTP GET request to the given URL
		url = baseUrl + url;
		AsyncHttpClient client = new AsyncHttpClient();
		client.setTimeout(100000); // TODO, make this less stupid
		APIHandler api = new APIHandler(this);
		client.get(url, api);
	}
	
	public void POST(String url, JSONObject params) {
        // Retrieve the base url from the args bundle
        Bundle args = this.getArguments();
        baseUrl = args.getString("url");
		// Send an HTTP PATCH request to the given URL with the specified params
		url = baseUrl + url;
        try {
            AsyncHttpClient client = new AsyncHttpClient();
            StringEntity entity = new StringEntity(params.toString());
            entity.setContentType(new BasicHeader(HTTP.CONTENT_TYPE, "application/json"));
            APIHandler api = new APIHandler(this);
            client.post(controller, url, entity, "application/json", api);
        } catch (UnsupportedEncodingException ex) {
            // TODO error handling
        }
	}
	
	// TODO add the other http request types
}
