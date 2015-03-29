/**
 * APIHandler
 * @author Bradley Johns
 * An extension of the AsyncHttpResponseHandler class meant to
 * handle all requests to the TROPIUS rest api and make an
 * accessible JSONObject to return response data
 */

package com.example.tropius;

import com.loopj.android.http.AsyncHttpResponseHandler;

import org.apache.http.Header;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;

public class APIHandler extends AsyncHttpResponseHandler {

	private APIResponder caller;
    private double progress;
	
	public APIHandler(APIResponder caller) {
		/* Initialize the calling activity so that we can call
		 * back to it
		 */
		this.caller = caller;
        this.progress = 0.0;
	}

	@Override
	public void onSuccess(int status, Header[] header, byte[] responseArray) {
		/* On a successful http request, the handler should gather the response
		 * data into a JSONObject and make an asynchronous callback to the calling
		 * activity
		 */
		// Your request was successful! Have some data!
		String res = "";
		try { // Format the response into a string
			res = new String(responseArray, "UTF-8");
		} catch (UnsupportedEncodingException ex) {
			ex.printStackTrace(); // TODO something better...
		}
		try { // Convert the response string into a JSONObject
			JSONObject json = new JSONObject(res);
			caller.asyncCallback(json); // callback
		} catch (JSONException ex) {
			ex.printStackTrace(); // TODO something else
		}
	}
	
	@Override
	public void onFailure(int statusCode, Header[] header,
						  byte[] contentArray, Throwable err) {
		/* On a failed http request, the handler should gather any error message that
		 * was returned as well as the statusCode and send them back to the calling
		 * activity as a JSONObject
		 */
		// You done screwed up, son. Have an error.
		JSONObject response = new JSONObject();
		String content = "";
		try { // Attempt to parse the error message if any
			if (contentArray != null) content = new String(contentArray, "UTF-8");
		} catch (UnsupportedEncodingException ex) {
			ex.printStackTrace(); // TODO something better...
		}
		// Create a JSON Error response
		try {
			response.put("error", statusCode);
			response.put("message", content);
		} catch (JSONException ex) {
			// TODO error handling
		}
		caller.asyncCallback(response); // callback
	}

    @Override
    public void onProgress(int bytesWritten, int totalSize) {
        caller.getProgressUpdate((double)bytesWritten / (double)totalSize);
    }
}
