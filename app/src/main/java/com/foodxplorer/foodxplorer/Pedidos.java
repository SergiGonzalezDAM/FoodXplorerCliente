package com.foodxplorer.foodxplorer;

import android.graphics.drawable.Drawable;

/**
 * Created by ALUMNEDAM on 15/05/2017.
 */

public class Pedidos {
    private long idPedido;
    private String fechaPedido;


    public Pedidos() {
    }

    public Pedidos(long idPedido, String fechaPedido) {
        this.idPedido = idPedido;
        this.fechaPedido = fechaPedido;
    }

    public String getFechaPedido() {
        return fechaPedido;
    }

    public void setFechaPedido(String fechaPedido) {
        this.fechaPedido = fechaPedido;
    }

    public long getIdPedido() {
        return idPedido;
    }

    public void setIdPedido(long idPedido) {
        this.idPedido = idPedido;
    }
}
