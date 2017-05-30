package com.foodxplorer.foodxplorer.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.foodxplorer.foodxplorer.MainActivity;
import com.foodxplorer.foodxplorer.R;
import com.foodxplorer.foodxplorer.objetos.Producto;

import java.util.ArrayList;
import java.util.List;

public class FragmentCarrito extends Fragment implements AdapterView.OnItemClickListener{

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
        View view = inflater.inflate(R.layout.fragment_confirmacion_pedido, container, false);
        List<String> lista = new ArrayList();
        ArrayList<Producto> productosenCarrito =(ArrayList) tienda.carrito.getProductosEnCarrito();
        ArrayList<Integer> cantidadesenCarrito = (ArrayList) tienda.carrito.getCantidades();
        if(productosenCarrito.size()==0){
            lista.add("No tienes ningun producto en el carrito aun.");
        }
        for (int i = 0; i < productosenCarrito.size(); i++) {
            lista.add(productosenCarrito.get(i).getNombre() + " cantidad: "+ cantidadesenCarrito.get(i)+ " Importe: " + String.format("%.2f", productosenCarrito.get(i).getPrecio()*cantidadesenCarrito.get(i)) );
        }
        ListView listView =(ListView) view.findViewById(R.id.listViewConfPedido);
        ArrayAdapter<String> adaptador = new ArrayAdapter(view.getContext(), android.R.layout.simple_list_item_1, lista);
        listView.setAdapter(adaptador);
        //Button boto = (Button)

        return view;
    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

    }
}
