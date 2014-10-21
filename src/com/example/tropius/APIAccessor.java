/** APIAccessor
 * @author Bradley Johns
 * Incredibly simplistic interface implemented by anything that makes
 * a call to the TROPIUS API. The interface guarantees that the
 * APIHandler class will have something to callback to with its
 * processed JSONObject
 */

package com.example.tropius;

import org.json.JSONObject;

public interface APIAccessor {
	
	public void asyncCallback(JSONObject response);
}
