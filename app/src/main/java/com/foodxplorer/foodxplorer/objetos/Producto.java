package com.foodxplorer.foodxplorer.objetos;

import android.graphics.drawable.Drawable;

public class Producto
{

    private Drawable imagenProducto;
    private String Nombre;
    private int idProducto;
    private String linkImagen;
    private double precio;
    private int iva;
    private double ofertaProducto;
    private int tipoProducto;
    private int activo;
    private String descripcion;

    /**
     * Generamos la clase producto con un constructor vacio y otro con los datos necesarios para poder recuperarlos de la base de datos
     * lo usaremos para generar promociones o productos en
     */
    public Producto()
    {

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

    public String getNombre() {
        return Nombre;
    }

    public int getIdProducto() {
        return idProducto;
    }

    public String getLinkImagen() {
        return linkImagen;
    }

    public double getPrecio() {
        return precio;
    }

    public String getDescripcion() {
        return descripcion;
    }

    @Override
    public String toString() {
        return "Producto{" +
                "imagenProducto=" + imagenProducto +
                ", Nombre='" + Nombre + '\'' +
                ", idProducto=" + idProducto +
                ", linkImagen='" + linkImagen + '\'' +
                ", precio=" + precio +
                ", iva=" + iva +
                ", ofertaProducto=" + ofertaProducto +
                ", tipoProducto=" + tipoProducto +
                ", activo=" + activo +
                ", descripcion='" + descripcion + '\'' +
                '}';
    }
}