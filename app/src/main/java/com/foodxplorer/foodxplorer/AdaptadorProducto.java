package com.foodxplorer.foodxplorer;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by ALUMNEDAM on 15/05/2017.
 */

public class AdaptadorProducto extends BaseAdapter
{
    protected Activity activity;
    //ARRAYLIST CON TODOS LOS ITEMS
    protected ArrayList<Producto> items;

    //CONSTRUCTOR
    public AdaptadorProducto(Activity activity, ArrayList<Producto> items) {
        this.activity = activity;
        this.items = items;
    }
    //CUENTA LOS ELEMENTOS
    @Override
    public int getCount() {
        return items.size();
    }
    //DEVUELVE UN OBJETO DE UNA DETERMINADA POSICION
    @Override
    public Object getItem(int arg0) {
        return items.get(arg0);
    }
    //DEVUELVE EL ID DE UN ELEMENTO
    @Override
    public long getItemId(int position) {
        return items.get(position).getIdProducto();
    }
    //METODO PRINCIPAL, AQUI SE LLENAN LOS DATOS
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        //SE GENERA UN CONVERTVIEW POR MOTIVOS DE EFICIENCIA DE MEMORIA
        //ES UN NIVEL MAS BAJO DE VISTA, PARA QUE OCUPEN MENOS MEMORIA LAS IMAGENES
        View v = convertView;
        //ASOCIAMOS LA VISTA AL LAYOUT DEL RECURSO XML DONDE ESTA LA BASE DE CADA ITEM
        if(convertView == null)
        {
            LayoutInflater inf = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            v = inf.inflate(R.layout.listaproductos, null);
        }

        Producto dir = items.get(position);
        //RELLENAMOS LA IMAGEN Y EL TEXTO
        ImageView foto = (ImageView) v.findViewById(R.id.imagenProducto);
        foto.setImageDrawable(dir.getImagenProducto());
        TextView nombre = (TextView) v.findViewById(R.id.textViewNombreProducto);
        nombre.setText(dir.getNombreProducto());
        TextView precio = (TextView) v.findViewById(R.id.precioProducto);
        precio.setText(""+dir.getPrecio()+" €");
        TextView descripcion = (TextView) v.findViewById(R.id.textViewDescripcionProducto);
        descripcion.setText(dir.getDescripcion());

        // DEVOLVEMOS VISTA
        return v;
    }
}
