package com.foodxplorer.foodxplorer;

import android.graphics.drawable.Drawable;

/**
 * Created by ALUMNEDAM on 15/05/2017.
 */

public class Pedidos
{
    private String numeroPedido;
    private String fechaPedido;
    private int idPedido;
    public Pedidos()
    {

    }
    public Pedidos(String numeroPedido, String fechaPedido, int idPedido)
    {
        this.numeroPedido = numeroPedido;
        this.fechaPedido = fechaPedido;
        this.idPedido = idPedido;
    }
    public Pedidos(String numeroPedido, String fechaPedido)
    {
        this.numeroPedido = numeroPedido;
        this.fechaPedido = fechaPedido;
    }

    public String getNumeroPedido() {
        return numeroPedido;
    }

    public void setNumeroPedido(String numeroPedido) {
        this.numeroPedido = numeroPedido;
    }

    public String getFechaPedido() {
        return fechaPedido;
    }

    public void setFechaPedido(String fechaPedido) {
        this.fechaPedido = fechaPedido;
    }

    public int getIdPedido() {
        return idPedido;
    }

    public void setIdPedido(int idPedido) {
        this.idPedido = idPedido;
    }
}
