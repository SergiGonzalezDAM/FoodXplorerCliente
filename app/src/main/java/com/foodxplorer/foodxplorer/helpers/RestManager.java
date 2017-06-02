package com.foodxplorer.foodxplorer.helpers;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.ProtocolException;
import java.net.URL;

public class RestManager {

    public static final String GET = "GET";
    public static final String POST = "POST";
    public static final String PUT = "PUT";
    private HttpURLConnection conn;

    public RestManager(String direccio) throws IOException {
        URL url = new URL(direccio);
        conn = (HttpURLConnection) url.openConnection();

        conn.setReadTimeout(Settings.SERVER_READ_TIMEOUT);/*milliseconds*/
        conn.setConnectTimeout(Settings.SERVER_CONNECT_TIMEOUT);
        conn.setRequestProperty("Content-Type", "application/json");


    }

    public void setRequestMethod(String Method) throws ProtocolException {
        if (Method.equals(POST) || Method.equals(PUT)) {
            conn.setDoOutput(true);
        }
        conn.setRequestMethod(Method);
    }


    public InputStreamReader getInputStream() throws IOException {
        return  new InputStreamReader(conn.getInputStream());
    }

    public BufferedReader getBufferedReader() throws IOException {
        return new BufferedReader(new InputStreamReader(conn.getInputStream()));
    }

    public OutputStreamWriter getOutputStreamWriter() throws IOException {
        return new OutputStreamWriter(conn.getOutputStream());
    }

    public void disconnect(){
        conn.disconnect();
    }

    public int getResponseCode() throws IOException {
        return conn.getResponseCode();
    }

    public String getResponseMessage() throws IOException {
        return conn.getResponseMessage();
    }


}
