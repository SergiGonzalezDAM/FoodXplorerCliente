package com.foodxplorer.foodxplorer.helpers;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.util.Set;

/**
 * Projecte FoodXplorer
 *
 * @Autors Abel Serrano, Sergi Gonazalez, Roger G.
 * Created by IES on 22/05/2017.
 */

public class RestManager {

    private HttpURLConnection conn;
    public static final String GET = "GET";
    public static final String POST = "POST";

public RestManager(String direccio) throws IOException {
    URL url = new URL(direccio);
    conn = (HttpURLConnection) url.openConnection();

    conn.setReadTimeout(Settings.SERVER_READ_TIMEOUT);/*milliseconds*/
    conn.setConnectTimeout(Settings.SERVER_CONNECT_TIMEOUT);
    conn.setRequestProperty("Content-Type", "application/json");

}

    /**
     * Método para configurar metodo GET/POST
     * @param Method
     * @throws ProtocolException
     */
    public void setRequestMethod(String Method) throws ProtocolException {
    conn.setRequestMethod(Method);
}

    /**
     * Método para enviar datos al servidor
     * @return
     * @throws IOException
     */
    public InputStreamReader getInputStream() throws IOException {
    return  new InputStreamReader(conn.getInputStream());
}

    /**
     * Metodo para leer datos del servidor
     * @return
     * @throws IOException
     */
    public BufferedReader getBufferedReader() throws IOException {
    return new BufferedReader(new InputStreamReader(conn.getInputStream()));
}

public OutputStreamWriter getOutputStreamWriter() throws IOException {
    return new OutputStreamWriter(conn.getOutputStream());
}

public void disconnect(){
    conn.disconnect();
}

}
