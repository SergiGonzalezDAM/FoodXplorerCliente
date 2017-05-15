package com.foodxplorer.foodxplorer;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import java.util.ArrayList;

public class FragmentPromociones extends Fragment {

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
        AdaptadorPromociones adaptador = new AdaptadorPromociones(getActivity(), arraydir);
        lista.setAdapter(adaptador);
        return view;

    }
}
