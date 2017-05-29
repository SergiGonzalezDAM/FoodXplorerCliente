package com.foodxplorer.foodxplorer.objetos;

import android.util.Log;

import com.foodxplorer.foodxplorer.helpers.Settings;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Projecte FoodXplorer
 *
 * @Autors Abel Serrano, Sergi Gonazalez, Roger G.
 * Created by IES on 22/05/2017.
 */

public class Carrito {

    private String usuarioLogueado = "";
    private List<Producto> productosEnCarrito;
    private List<Integer> cantidades;

    public Carrito() {
        productosEnCarrito = new ArrayList<>();
        cantidades = new ArrayList<>();
    }

    public String getUsuarioLogueado() {
        return usuarioLogueado;
    }

    /**
     * Guarda l'usuari loguejat de l'aplicacio
     *
     * @param usuarioLogueado
     */
    public void setUsuarioLogueado(String usuarioLogueado) {
        this.usuarioLogueado = usuarioLogueado;
    }

    /**
     * Obte una list de productes que estan al carro.
     *
     * @return llista de productes.
     */
    public List<Producto> getProductosEnCarrito() {
        return productosEnCarrito;
    }

    /**
     * Obte una list amb les quantitats de cada producte del carro.
     *
     * @return llista de quantitats.
     */
    public List<Integer> getCantidades() {
        return cantidades;
    }

    /**
     * Afegeix un producte al carro de la compra. Si aquest existeix, l'actualitza amb la nova
     * quantitat. Si es pasa una cantitat 0, el producte s'esborra del carro.
     *
     * @param productoAnadir El producte a afegir al carro
     * @param cantidad       La cantitat que volem afegir del producte
     */
    public void addProducto(Producto productoAnadir, int cantidad) {

        Iterator iterator = productosEnCarrito.iterator();
        boolean found = false;
        try {
            while (iterator.hasNext()) {
                Producto productoEnCarrito = (Producto) iterator.next();
                //Si el producte existeix en el carro...
                if (productoEnCarrito.getIdProducto() == productoAnadir.getIdProducto()) {
                    Log.d(Settings.LOGTAG, "productoAnadir encontrado en carrito, actualizamos");
                    int pos = productosEnCarrito.indexOf(productoEnCarrito);
                    productosEnCarrito.remove(pos);
                    int cantidadAntigua = cantidades.get(pos);
                    cantidades.remove(pos);
                    productosEnCarrito.add(pos, productoAnadir);
                    int nuevaCantidad = cantidadAntigua + cantidad;
                    cantidades.add(pos, nuevaCantidad);
                    found = true;
                    break;
                }
            }


            //Si no existeix en el carro, l'afegim
            if (!found) {
                Log.d(Settings.LOGTAG, "productoAnadir NO encontrado en carrito, añadimos");
                productosEnCarrito.add(productoAnadir);
                cantidades.add(cantidad);
            }
        } catch (ArrayIndexOutOfBoundsException ex) {
            Log.e(Settings.LOGTAG, ex.getMessage());
        }
    }


    public int getTotalProductos() {
        int cantidad = 0;
        for (Integer cantid : cantidades) {
            int product = cantid;
            cantidad = cantidad + product;
        }

        return cantidad;
    }
}



