package com.foodxplorer.foodxplorer.objetos;

public class LineasPedido {
    private long idPedido;
    private long idProducto;
    private int cantidad;
    private double precio;
    private int iva;

    public LineasPedido() {
    }

    public LineasPedido(long idPedido, long idProducto, int cantidad, double precio, int iva) {
        this.idPedido = idPedido;
        this.idProducto = idProducto;
        this.cantidad = cantidad;
        this.precio = precio;
        this.iva = iva;
    }
    public double getPrecio() {
        return precio;
    }

    @Override
    public String toString() {
        return "LineasPedido{" +
                "idPedido=" + idPedido +
                ", idProducto=" + idProducto +
                ", cantidad=" + cantidad +
                ", precio=" + precio +
                ", iva=" + iva +
                '}';
    }
}
