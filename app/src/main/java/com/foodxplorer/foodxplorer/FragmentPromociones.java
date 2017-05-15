package com.foodxplorer.foodxplorer;

import android.annotation.TargetApi;
import android.support.v4.app.ListFragment;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;

public class FragmentPromociones extends ListFragment {

    public FragmentPromociones()
    {

    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_promociones, container, false);

        ListView lista = (ListView) view.findViewById(R.id.listViewPromociones);
        ArrayList<Promociones> arraydir = new ArrayList<>();
        Promociones promocion;

        // Introduzco los datos
        promocion = new Promociones(getResources().getDrawable(R.drawable.pizza2), "4 Quesos");
        arraydir.add(promocion);
        promocion = new Promociones(getResources().getDrawable(R.drawable.pizza2), "4 Quesos");
        arraydir.add(promocion);
        promocion = new Promociones(getResources().getDrawable(R.drawable.pizza2), "4 Quesos");
        arraydir.add(promocion);
        final AdaptadorPromociones adaptador = new AdaptadorPromociones(getActivity(), arraydir);
        lista.setAdapter(adaptador);
        return view;
    }
    public void onListItemClick (ListView l, View v, int position, long id) {
        Toast.makeText(getContext(),"asdasdasda", Toast.LENGTH_LONG).show();
    }
}
