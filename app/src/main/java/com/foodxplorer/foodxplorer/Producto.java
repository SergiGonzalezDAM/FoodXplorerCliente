package com.foodxplorer.foodxplorer;

import android.graphics.drawable.Drawable;

public class Producto
{
    private Drawable imagenProducto;
    private String nombreProducto;
    private int idProducto;
    double precioProducto;

    public Producto()
    {

    }
    public Producto(Drawable imagenProducto, String nombreProducto, double precioProducto, int idProducto)
    {
        this.nombreProducto = nombreProducto;
        this.imagenProducto = imagenProducto;
        this.precioProducto = precioProducto;
        this.idProducto = idProducto;
    }
    public Producto(Drawable imagenProducto, String nombreProducto, double precioProducto)
    {
        this.nombreProducto = nombreProducto;
        this.imagenProducto = imagenProducto;
        this.precioProducto = precioProducto;
    }

    public Drawable getImagenProducto() {
        return imagenProducto;
    }

    public void setImagenProducto(Drawable imagenProducto) {
        this.imagenProducto = imagenProducto;
    }

    public String getNombreProducto() {
        return nombreProducto;
    }

    public void setNombreProducto(String nombreProducto) {
        this.nombreProducto = nombreProducto;
    }

    public int getIdProducto() {
        return idProducto;
    }

    public void setIdProducto(int idProducto) {
        this.idProducto = idProducto;
    }

    public double getPrecioProducto() {
        return precioProducto;
    }

    public void setPrecioProducto(double precioProducto) {
        this.precioProducto = precioProducto;
    }
}
