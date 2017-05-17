package com.foodxplorer.foodxplorer;

import android.graphics.drawable.Drawable;

public class Promociones
{
    private Drawable imagenProducto;
    private String nombreProducto;
    private int idProducto;
    private double precio;
    String descripcion;

    public Promociones()
    {

    }
    public Promociones(Drawable imagenProducto, String nombreProducto, int idProducto, double precio,String descripcion)
    {
        this.nombreProducto = nombreProducto;
        this.imagenProducto = imagenProducto;
        this.idProducto = idProducto;
        this.precio = precio;
        this.descripcion = descripcion;
    }
    public Promociones(Drawable imagenProducto, String nombreProducto, double precio,String descripcion)
    {
        this.nombreProducto = nombreProducto;
        this.imagenProducto = imagenProducto;
        this.precio = precio;
        this.descripcion = descripcion;
    }

    public double getPrecio() {
        return precio;
    }

    public void setPrecio(double precio) {
        this.precio = precio;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
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

    @Override
    public String toString() {
        return "Promociones{" +
                "imagenProducto=" + imagenProducto +
                ", nombreProducto='" + nombreProducto + '\'' +
                ", idProducto=" + idProducto +
                ", precio=" + precio +
                ", descripcion='" + descripcion + '\'' +
                '}';
    }
}
