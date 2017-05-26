package com.foodxplorer.foodxplorer.fragments;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
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

import com.foodxplorer.foodxplorer.MainActivity;
import com.foodxplorer.foodxplorer.Producto;
import com.foodxplorer.foodxplorer.R;
import com.foodxplorer.foodxplorer.adapters.AdaptadorProducto;
import com.foodxplorer.foodxplorer.helpers.AsyncResponse;
import com.foodxplorer.foodxplorer.helpers.RestManager;
import com.foodxplorer.foodxplorer.helpers.Settings;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.util.ArrayList;

import static com.foodxplorer.foodxplorer.helpers.Settings.LOGTAG;

public class FragmentPromociones extends Fragment implements AdapterView.OnItemClickListener {


    public interface OnAddToCart {
        void onAddToCart(Producto producto, int cantidad);
    }

    private OnAddToCart mOnAddToCart;
    ListView lista;
    AdaptadorProducto adaptador;
    JSONArray listadoPromocionesJSON;
    ArrayList<Producto> listadoPromociones;
    MainActivity tienda;

    public FragmentPromociones() {

    }

    public FragmentPromociones(MainActivity tienda) {
        this.tienda = tienda;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        TareaWSRecuperarProductos tarea = new TareaWSRecuperarProductos();
        tarea.execute();

        View view = inflater.inflate(R.layout.fragment_productos, container, false);
        lista = (ListView) view.findViewById(R.id.listViewProductos);

        lista.setOnItemClickListener(this);
        return view;
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

        final Producto producto = listadoPromociones.get(position);

        final AlertDialog.Builder mBuilder = new AlertDialog.Builder(getContext());
        final View mView = getLayoutInflater(null).inflate(R.layout.activity_dialog, null);

        ImageView image = (ImageView) mView.findViewById(R.id.imagenPromocion);
        Picasso.with(mView.getContext()).load("http://www.cicis.com/media/1138/pizza_trad_pepperoni.png").into(image);
        TextView nombre = (TextView) mView.findViewById(R.id.textViewNombrePromocion);
        nombre.setText(producto.getNombre());
        TextView precio = (TextView) mView.findViewById(R.id.textViewPrecioPromocion);
        precio.setText(producto.getPrecio() + " €");
        TextView descripcion = (TextView) mView.findViewById(R.id.textViewDescripcionPromociones);
        descripcion.setText(producto.getDescripcion());

        mBuilder.setView(mView);
        final AlertDialog dialog = mBuilder.create();
        final TextView cantidad = (TextView) mView.findViewById(R.id.popUpTextViewCantidad);

        Button addmore = (Button) mView.findViewById(R.id.btnAddMore);
        addmore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                int aux= Integer.valueOf(cantidad.getText().toString());
                cantidad.setText(String.valueOf(++aux));
            }
        });


        Button addless = (Button) mView.findViewById(R.id.btnAddLess);
        addless.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int aux= Integer.valueOf(cantidad.getText().toString());
                if(aux>0){
                    cantidad.setText(String.valueOf(--aux));
                }
            }
        });

        mView.findViewById(R.id.btnAddCart).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mOnAddToCart.onAddToCart(producto, Integer.valueOf(cantidad.getText().toString()));
                dialog.dismiss();
            }
        });


        dialog.show();
    }

    class TareaWSRecuperarProductos extends AsyncTask<Object, Void, Boolean> {


        @Override
        protected Boolean doInBackground(Object... params) {
            boolean result = true;

            BufferedReader reader;
            String url;
            try {
                Log.d(LOGTAG, "Obteniendo ofertas");
                url = Settings.DIRECCIO_SERVIDOR + Settings.PATH+"productos/ofertas";
                RestManager rest = new RestManager(url);
                rest.setRequestMethod(RestManager.GET);
                reader = rest.getBufferedReader();
                listadoPromocionesJSON = new JSONArray(reader.readLine());
            } catch (java.net.ProtocolException ex) {
                Log.e(Settings.LOGTAG, "Error de protocol: " + ex);
                result = false;
            } catch (java.io.FileNotFoundException | java.net.MalformedURLException ex) {
                Log.e(Settings.LOGTAG, "Error de ruta d'acces: " + ex);
                result = false;
            } catch (java.net.SocketTimeoutException ex) {
                Log.e(Settings.LOGTAG, "Temps d'espera esgotat al iniciar la conexio amb la BBDD extena: " + ex);
                result = false;
            } catch (java.net.ConnectException ex) {
                Log.e(Settings.LOGTAG, "Connect exeption: " + ex.getMessage());
                result = false;
            } catch (java.io.IOException ex) {
                Log.e(Settings.LOGTAG, "Undefined error: " + ex);
                result = false;
            } catch (org.json.JSONException ex) {
                Log.e(LOGTAG, "Error en la transformacio de l'objecte JSON: " + ex);
                result = false;
            }
            return result;
        }


        @Override
        protected void onPostExecute(Boolean result) {
            if (result) {
                try {
                    rellenarArray();
                    adaptador = new AdaptadorProducto(getActivity(), listadoPromociones);
                    lista.setAdapter(adaptador);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            } else {
                Toast.makeText(tienda, "Error de obtencion de datos.", Toast.LENGTH_LONG).show();
            }
        }

        private void rellenarArray() throws JSONException {
            listadoPromociones = new ArrayList();
            System.err.println("Promociones recibidas: " + listadoPromocionesJSON.length());
            for (int i = 0; i < listadoPromocionesJSON.length(); i++) {
                JSONObject jsonobject = listadoPromocionesJSON.getJSONObject(i);
                Producto producto = new Producto(jsonobject.getInt("idProducto"), jsonobject.getString("nombre"), jsonobject.getString("descripcion"), jsonobject.getDouble("precio"), jsonobject.getInt("iva"), jsonobject.getDouble("ofertaDescuento"), jsonobject.getInt("activo"), jsonobject.getInt("idTipoProducto"), jsonobject.getString("urlImagen"));
                listadoPromociones.add(producto);
            }
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mOnAddToCart = (OnAddToCart) context;
    }
}
