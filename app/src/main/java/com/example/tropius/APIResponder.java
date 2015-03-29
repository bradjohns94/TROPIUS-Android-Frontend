package com.example.tropius;

import org.json.JSONObject;

public interface APIResponder {

	public void asyncCallback(JSONObject response);

    public void getProgressUpdate(double progress);
}
