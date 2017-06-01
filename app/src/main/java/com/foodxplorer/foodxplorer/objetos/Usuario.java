package com.foodxplorer.foodxplorer.objetos;

/**
 * Projecte FoodXplorer
 *
 * @Autors Abel Serrano, Sergi Gonazalez, Roger G.
 * Created by IES on 01/06/2017.
 */

public class Usuario {

    private String username;
    private String password;

    public Usuario(String username, String password) {
        this.username = username;
        this.password = password;
    }

    public String getUsername() {
        return username;
    }


    public String getPassword() {
        return password;
    }

}

