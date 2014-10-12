package com.example.tropius;

import retrofit.http.*;

/** API
 * @author Bradley Johns
 * Utilizes the retrofit rest API package to make methods
 * which make TROPIUS API calls
 */
public interface API {
	@GET("/TROPIUS/hosts/list")
	public String listHosts();
}