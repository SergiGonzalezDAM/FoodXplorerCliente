package com.foodxplorer.foodxplorer;

import android.annotation.TargetApi;
import android.content.Context;
import android.support.v4.app.ListFragment;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

public class FragmentPromociones extends Fragment implements AdapterView.OnItemClickListener{

    public interface OnAddToCart{
        void onAddToCart(Promociones promociones);
    }

    private OnAddToCart mOnAddToCart;

    ArrayList<Promociones> arraydir;
    ListView lista;
    AdaptadorPromociones adaptador;
    public FragmentPromociones()
    {

    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_promociones, container, false);
        lista = (ListView) view.findViewById(R.id.listViewPromociones);

        Promociones promocion;
        arraydir = new ArrayList<>();
        // Introduzco los datos
        promocion = new Promociones(getResources().getDrawable(R.drawable.pizza2), "4 Quesos",14,"Artesana");
        arraydir.add(promocion);
        promocion = new Promociones(getResources().getDrawable(R.drawable.pizza2), "5 Quesos",15,"Artesana");
        arraydir.add(promocion);
        promocion = new Promociones(getResources().getDrawable(R.drawable.pizza2), "6 Quesos",16,"Artesana");
        arraydir.add(promocion);
        adaptador = new AdaptadorPromociones(getActivity(), arraydir);
        lista.setAdapter(adaptador);
        lista.setOnItemClickListener(this);
        return view;
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {


        /*Promociones promocion = arraydir.get(position);
        ArrayList<Promociones> promociones = new ArrayList();
        promociones.add(promocion);

        AdaptadorPromociones adaptadorSec = new AdaptadorPromociones(getActivity(), promociones);
        lista.setAdapter(adaptadorSec);*/

        final Promociones promocion = arraydir.get(position);

        AlertDialog.Builder mBuilder = new AlertDialog.Builder(getContext());
        View mView = getLayoutInflater(null).inflate(R.layout.listapromociones,null);

        ImageView foto = (ImageView) mView.findViewById(R.id.imagenPromocion);
        foto.setImageDrawable(promocion.getImagenProducto());
        TextView nombre = (TextView)mView.findViewById(R.id.textViewNombrePromocion);
        nombre.setText(promocion.getNombreProducto());
        TextView precio = (TextView) mView.findViewById(R.id.textViewPrecioPromocion);
        precio.setText(""+promocion.getPrecio());
        TextView descripcion = (TextView)mView.findViewById(R.id.textViewDescripcionPromociones);
        descripcion.setText(promocion.getDescripcion());
        mView.findViewById(R.id.btnAñadirPromocion).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mOnAddToCart.onAddToCart(promocion);
                System.out.println("sadfgdsafgfasghjfgfasdgshgfasfdgssafdgdfgsfasdgfas vsgbdsdafvbdgfevs");
            }
        });

        mBuilder.setView(mView);
        AlertDialog dialog = mBuilder.create();

        //TextView hola = (TextView) dialog.findViewById(R.id.textViewDescripcionPromociones);
        //hola.setText("Hola");

        //AlertDialog dialog = mBuilder.create();

        dialog.show();

    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mOnAddToCart = (OnAddToCart) context;
    }
}
