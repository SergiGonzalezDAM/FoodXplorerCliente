package com.foodxplorer.foodxplorer.helpers;

/**
 * Projecte FoodXplorer
 *
 * @Autors Abel Serrano, Sergi Gonazalez, Roger G.
 * Created by IES on 19/05/2017.
 */

/**
 * La clase Settings almacenamos información importante para toda la aplicación.
 */
public class Settings {

    //public static final  String DIRECCIO_SERVIDOR="http://192.168.120.59:40223/";
    // public static final String DIRECCIO_SERVIDOR = "http://192.168.120.47:8080/";
    public static final String DIRECCIO_SERVIDOR = "http://192.168.1.9:8080/";
    //public static final  String DIRECCIO_SERVIDOR="http://server.blusoft.net:8080/";
    public static final String PATH="ServcioFoodXPlorer/webresources/generic/";
    public static final  String LOGTAG="FoodXplorer Client";


    static final int SERVER_READ_TIMEOUT = 2500;
    static final int SERVER_CONNECT_TIMEOUT = 2500;
}
