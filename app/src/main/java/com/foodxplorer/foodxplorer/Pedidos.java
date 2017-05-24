package com.foodxplorer.foodxplorer;

import android.graphics.drawable.Drawable;

/**
 * Created by ALUMNEDAM on 15/05/2017.
 */

public class Pedidos {
    private long idPedido;
    private String fechaPedido;
    private long idDireccion;

    public Pedidos() {
    }

    public Pedidos(long idPedido, String fechaPedido, long idDireccion) {
        this.idPedido = idPedido;
        this.fechaPedido = fechaPedido;
        this.idDireccion = idDireccion;
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

    public long getIdDireccion() {
        return idDireccion;
    }

    public void setIdDireccion(long idDireccion) {
        this.idDireccion = idDireccion;
    }
}
