package com.foodxplorer.foodxplorer.helpers;

import com.foodxplorer.foodxplorer.Producto;

import java.util.ArrayList;
import java.util.List;

/**
 * Projecte FoodXplorer
 *
 * @Autors Abel Serrano, Sergi Gonazalez, Roger G.
 * Created by IES on 22/05/2017.
 */

public class Carrito {

    String usuarioLogueado=null;
    List <Producto> productosEnCarrito;
    List <Integer> cantidades;

    public Carrito(){
      productosEnCarrito= new ArrayList<>();
        cantidades= new ArrayList<>();
    }

    public String getUsuarioLogueado() {
        return usuarioLogueado;
    }

    public void setUsuarioLogueado(String usuarioLogueado) {
        this.usuarioLogueado = usuarioLogueado;
    }


    public List<Producto> getProductosEnCarrito() {
        return productosEnCarrito;
    }

    public void addProducto(Producto producto, int cantidad) {
        if (!productosEnCarrito.contains(producto)){
            productosEnCarrito.add(producto);
            cantidades.add(cantidad);
        }
        else{
            int pos=productosEnCarrito.indexOf(producto);
            productosEnCarrito.add(pos,producto);
        }
    }
}




