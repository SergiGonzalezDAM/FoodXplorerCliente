package com.foodxplorer.foodxplorer.helpers;

import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Set;

/**
 * Projecte FoodXplorer
 *
 * @Autors Abel Serrano, Sergi Gonazalez, Roger G.
 * Created by IES on 22/05/2017.
 */

public class RestManager {

    HttpURLConnection conn;

public RestManager(String direccio) throws IOException {
    URL url = new URL(direccio);
    conn = (HttpURLConnection) url.openConnection();
    conn.setRequestMethod("GET");
    conn.setReadTimeout(Settings.SERVER_READ_TIMEOUT);/*milliseconds*/
    conn.setConnectTimeout(Settings.SERVER_CONNECT_TIMEOUT);
    conn.setRequestProperty("Content-Type", "application/json");

}


public InputStreamReader getInputStream() throws IOException {
    return  new InputStreamReader(conn.getInputStream());
}


}
