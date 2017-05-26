package com.foodxplorer.foodxplorer.adapters;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.foodxplorer.foodxplorer.objetos.Pedidos;
import com.foodxplorer.foodxplorer.R;

import java.util.ArrayList;

/**
 * Created by ALUMNEDAM on 15/05/2017.
 */

public class AdaptadorPedido extends BaseAdapter
{
        private Activity activity;
        //ARRAYLIST CON TODOS LOS ITEMS
        private ArrayList<Pedidos> items;

        //CONSTRUCTOR
    public AdaptadorPedido(Activity activity, ArrayList<Pedidos> items) {
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
        return items.get(position).getIdPedido();
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
            v = inf.inflate(R.layout.listapedidos, null);
        }

        Pedidos dir = items.get(position);
        //RELLENAMOS LA IMAGEN Y EL TEXTO
        TextView foto = (TextView) v.findViewById(R.id.textViewShowUsername);
        foto.setText(String.valueOf(dir.getIdPedido()));
        TextView nombre = (TextView) v.findViewById(R.id.textViewShowFechaPedido);
        nombre.setText(dir.getFechaPedido());

        // DEVOLVEMOS VISTA
        return v;
    }
    }

