package com.foodxplorer.foodxplorer.helpers;

import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * Projecte FoodXplorer
 *
 * @Autors Abel Serrano, Sergi Gonazalez, Roger G.
 * Created by IES on 22/05/2017.
 */

public class restManager {

    HttpURLConnection conn;

public restManager(String direccio) throws IOException {
    URL url = new URL(direccio);
    conn = (HttpURLConnection) url.openConnection();
    conn.setRequestMethod("GET");
    conn.setReadTimeout(5000);/*milliseconds*/
    conn.setConnectTimeout(5000);
    conn.setRequestProperty("Content-Type", "application/json");

}


public InputStreamReader getInputStream() throws IOException {
    return  new InputStreamReader(conn.getInputStream());
}


}
