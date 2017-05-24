package com.foodxplorer.foodxplorer.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.foodxplorer.foodxplorer.MainActivity;
import com.foodxplorer.foodxplorer.R;

import java.util.ArrayList;
import java.util.List;

public class FragmentCarrito extends Fragment {

    MainActivity tienda;

    public FragmentCarrito(MainActivity tienda) {

        this.tienda=tienda;
    }

    public FragmentCarrito() {
        // Required empty public constructor
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_resumen_pedido, container, false);
        List<String> lista = new ArrayList();
        lista.add("Barbacoa");
        lista.add("4 Quesos");
        ListView listView =(ListView) view.findViewById(R.id.listViewInfoPedido);
        ArrayAdapter<String> adaptador = new ArrayAdapter(view.getContext(), android.R.layout.simple_list_item_1, lista);
        listView.setAdapter(adaptador);
        return view;
    }
}
