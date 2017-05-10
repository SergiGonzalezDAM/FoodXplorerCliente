package com.foodxplorer.foodxplorer;

import android.graphics.drawable.Drawable;

public class Promociones
{
    private Drawable imagenProducto;
    private String nombreProducto;
    private int idProducto;

    public Promociones()
    {

    }
    public Promociones(Drawable imagenProducto, String nombreProducto, int idProducto)
    {
        this.nombreProducto = nombreProducto;
        this.imagenProducto = imagenProducto;
        this.idProducto = idProducto;
    }
    public Promociones(Drawable imagenProducto, String nombreProducto)
    {
        this.nombreProducto = nombreProducto;
        this.imagenProducto = imagenProducto;
    }
    public Drawable getImagen()
    {
        return imagenProducto;
    }
    public void setImagen(Drawable imagen)
    {
        this.imagenProducto = imagen;
    }
    public String getTexto()
    {
        return nombreProducto;
    }
    public void setTexto(String nombreProducto)
    {
        this.nombreProducto = nombreProducto;
    }
    public int getIdProducto()
    {
        return idProducto;
    }
    public void setIdProducto(int id)
    {
        this.idProducto = id;
    }
}
