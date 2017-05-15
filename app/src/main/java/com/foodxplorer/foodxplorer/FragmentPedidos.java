package com.foodxplorer.foodxplorer;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import java.util.ArrayList;

public class FragmentPedidos extends Fragment
{
    public FragmentPedidos()
    {

    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_pedidos, container, false);

        ListView lista = (ListView) view.findViewById(R.id.listViewPedidos);
        ArrayList<Pedidos> arraydira = new ArrayList<>();
        Pedidos pedido;

        // Introduzco los datos
        pedido = new Pedidos("56431351", "15/05/2017");
        arraydira.add(pedido);
        pedido = new Pedidos("13152541", "11/05/2017");
        arraydira.add(pedido);
        pedido = new Pedidos("35431565", "12/05/2017");
        arraydira.add(pedido);
        AdaptadorPedido adaptador = new AdaptadorPedido(getActivity(), arraydira);
        lista.setAdapter(adaptador);
        return view;

    }

}
