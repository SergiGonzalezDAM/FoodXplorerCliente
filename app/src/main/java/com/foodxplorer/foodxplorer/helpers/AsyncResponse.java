package com.foodxplorer.foodxplorer.helpers;

/**
 * Projecte FoodXplorer
 *
 * @Autors Abel Serrano, Sergi Gonazalez, Roger G.
 * Created by IES on 23/05/2017.
 */

public interface AsyncResponse {
    /**
     * Interface que utilizaremos para saber si un proceso a finalizado o no
     * @param response
     */
    void processFinish(boolean response);
}
