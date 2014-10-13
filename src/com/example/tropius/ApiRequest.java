package com.example.tropius;

import java.io.IOException;
import java.io.InputStream;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.protocol.BasicHttpContext;
import org.apache.http.protocol.HttpContext;

import android.os.AsyncTask;

public class ApiRequest extends AsyncTask<String, Void, String>{
	
	protected String doInBackground(String... params) {
		String url = params[0];
		HttpClient client = new DefaultHttpClient();
		HttpContext context = new BasicHttpContext();
		// Make it a get for now, we'll play around with this later
		HttpGet get = new HttpGet(url);
		String res = null;
		try {
			HttpResponse httpRes = client.execute(get, context);
			HttpEntity entity = httpRes.getEntity();
			res = getASCIIContentFromEntity(entity);
		} catch (Exception e) { // TODO stop doing this...
			// TODO error catching stuff
		}
		return res;
	}

	protected String getASCIIContentFromEntity(HttpEntity entity) throws IllegalStateException, IOException {
		InputStream in = entity.getContent();
		StringBuffer out = new StringBuffer();
		int n = 1;
		while (n>0) {
			byte[] b = new byte[4096];
			n =  in.read(b);
			if (n>0) out.append(new String(b, 0, n));
		}
		return out.toString();
	}
}
