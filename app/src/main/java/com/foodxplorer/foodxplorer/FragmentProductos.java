package com.foodxplorer.foodxplorer;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import java.util.ArrayList;

public class FragmentProductos extends Fragment {
    public FragmentProductos() {

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_productos, container, false);

        ListView lista = (ListView) view.findViewById(R.id.listViewProductos);
        ArrayList<Producto> arraydira = new ArrayList<>();
        Producto producto;

        // Introduzco los datos
        producto = new Producto(getResources().getDrawable(R.drawable.pizza2), "4 Quesos",12.2);
        arraydira.add(producto);
        producto = new Producto(getResources().getDrawable(R.drawable.pizza2), "4 Quesos",12.2);
        arraydira.add(producto);
        producto = new Producto(getResources().getDrawable(R.drawable.pizza2), "4 Quesos",12.2);
        arraydira.add(producto);
        AdaptadorProducto adaptador = new AdaptadorProducto(getActivity(), arraydira);
        lista.setAdapter(adaptador);
        return view;

    }
}