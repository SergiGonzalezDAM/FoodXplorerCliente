package com.foodxplorer.foodxplorer.fragments;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.foodxplorer.foodxplorer.Producto;
import com.foodxplorer.foodxplorer.adapters.AdaptadorProducto;
import com.foodxplorer.foodxplorer.R;

import java.util.ArrayList;

public class FragmentPromociones extends Fragment implements AdapterView.OnItemClickListener{

    public interface OnAddToCart{
        void onAddToCart(Producto producto);
    }

    private OnAddToCart mOnAddToCart;

    ArrayList<Producto> arraydir;
    ListView lista;
    AdaptadorProducto adaptador;
    public FragmentPromociones()
    {

    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_productos, container, false);
        lista = (ListView) view.findViewById(R.id.listViewProductos);

        Producto producto;
        arraydir = new ArrayList<>();
        // Introduzco los datos
        producto = new Producto(getResources().getDrawable(R.drawable.pizza2), "4 Quesos",14,"Artesana");
        arraydir.add(producto);
        producto = new Producto(getResources().getDrawable(R.drawable.pizza2), "5 Quesos",15,"Artesana");
        arraydir.add(producto);
        producto = new Producto(getResources().getDrawable(R.drawable.pizza2), "6 Quesos",16,"Artesana");
        arraydir.add(producto);
        adaptador = new AdaptadorProducto(getActivity(), arraydir);
        lista.setAdapter(adaptador);
        lista.setOnItemClickListener(this);
        return view;
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

        final Producto producto = arraydir.get(position);

        AlertDialog.Builder mBuilder = new AlertDialog.Builder(getContext());
        View mView = getLayoutInflater(null).inflate(R.layout.activity_dialog,null);

        ImageView foto = (ImageView) mView.findViewById(R.id.imagenPromocion);
        foto.setImageDrawable(producto.getImagenProducto());
        TextView nombre = (TextView)mView.findViewById(R.id.textViewNombrePromocion);
        nombre.setText(producto.getNombreProducto());
        TextView precio = (TextView) mView.findViewById(R.id.textViewPrecioPromocion);
        precio.setText(producto.getPrecio()+" €");
        TextView descripcion = (TextView)mView.findViewById(R.id.textViewDescripcionPromociones);
        descripcion.setText(producto.getDescripcion());

        mView.findViewById(R.id.btnAddCart).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mOnAddToCart.onAddToCart(producto);
                System.out.println("sadfgdsafgfasghjfgfasdgshgfasfdgssafdgdfgsfasdgfas vsgbdsdafvbdgfevs");
            }
        });

        mBuilder.setView(mView);
        AlertDialog dialog = mBuilder.create();

        dialog.show();

    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mOnAddToCart = (OnAddToCart) context;
    }
}
