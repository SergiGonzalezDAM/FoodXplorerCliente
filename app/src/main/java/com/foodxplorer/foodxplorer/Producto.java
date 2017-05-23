package com.foodxplorer.foodxplorer;

import android.graphics.drawable.Drawable;

public class Producto
{

    private Drawable imagenProducto;
    private String Nombre;
    private int idProducto;
    String linkImagen;
    private double precio;
    int iva;
    double ofertaProducto;
    int tipoProducto;
    int activo;
    String descripcion;

    public Producto()
    {

    }

    public Producto(Drawable imagenProducto, String nombreProducto, int idProducto, double precio,String descripcion)
    {
        this.Nombre = nombreProducto;
        this.imagenProducto = imagenProducto;
        this.idProducto = idProducto;
        this.precio = precio;
        this.descripcion = descripcion;
    }
    public Producto(int idProducto, String nombreProducto, String descripcion, double precio, int iva, double ofertaProducto, int activo,int tipoProducto, String linkImagen)
    {
        this.Nombre = nombreProducto;
        this.linkImagen = linkImagen;
        this.iva = iva;
        this.activo = activo;
        this.tipoProducto = tipoProducto;
        this.ofertaProducto = ofertaProducto;
        this.idProducto = idProducto;
        this.precio = precio;
        this.descripcion = descripcion;
    }

    public Producto(Drawable imagenProducto, String nombreProducto, double precio,String descripcion)
    {
        this.Nombre = nombreProducto;
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
        return Nombre;
    }

    public void setNombreProducto(String nombreProducto) {
        this.Nombre = nombreProducto;
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
                ", Nombre='" + Nombre + '\'' +
                ", idProducto=" + idProducto +
                ", precio=" + precio +
                ", descripcion='" + descripcion + '\'' +
                '}';
    }
}