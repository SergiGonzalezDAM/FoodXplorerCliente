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
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.foodxplorer.foodxplorer.Producto;
import com.foodxplorer.foodxplorer.adapters.AdaptadorProducto;
import com.foodxplorer.foodxplorer.R;
import com.foodxplorer.foodxplorer.helpers.AsyncResponse;
import com.foodxplorer.foodxplorer.helpers.Settings;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

import static com.foodxplorer.foodxplorer.helpers.Settings.LOGTAG;

public class FragmentPromociones extends Fragment implements AdapterView.OnItemClickListener{


    public interface OnAddToCart{
        void onAddToCart(Producto producto);
    }

    private OnAddToCart mOnAddToCart;

    ArrayList<Producto> arraydir;
    ListView lista;
    AdaptadorProducto adaptador;
    JSONArray listadoPromocionesJSON;
    ArrayList<Producto> listadoPromociones;
    public FragmentPromociones()
    {

    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
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
    class TareaWSRecuperarProductos extends AsyncTask<Object, Void, Boolean> {

        public AsyncResponse delegate = null;

        @Override
        protected Boolean doInBackground(Object... params) {
            BufferedReader reader;
            URL url = null;
            try {

                    Log.d(LOGTAG, "Obteniendo las ultimas posiciones de todos los autobuses");
                    url = new URL(Settings.DIRECCIO_SERVIDOR+ "ServcioFoodXPlorer/webresources/generic/productos/ofertas");
                    reader = getBufferedReader(url);
                    listadoPromocionesJSON = new JSONArray(reader.readLine());

            }
            catch (java.io.FileNotFoundException ex) {
                Log.e(LOGTAG, "Error al obtenir la posicio de:" + url.toString()+"\n"+ex);
            }catch (java.io.IOException ex) {
                Log.e(LOGTAG, "Temps d'espera esgotat al iniciar la conexio amb la BBDD externa:" + url.toString()+"\n"+ex);
            } catch (org.json.JSONException ex) {
                Log.e(LOGTAG, "Error en la transformacio de l'objecte JSON: " + ex);
            }
            return true;
        }
        private BufferedReader getBufferedReader(URL url) throws java.io.IOException {
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
           // conn.setReadTimeout(10000 /*milliseconds*/);
           // conn.setConnectTimeout(10000);
            conn.setRequestProperty("Content-Type", "application/json");
            return new BufferedReader(new InputStreamReader(conn.getInputStream()));
        }

        @Override
        protected void onPostExecute(Boolean result) {

            if(result){
                try {
                    rellenarArray();
                    adaptador = new AdaptadorProducto(getActivity(), listadoPromociones);
                    lista.setAdapter(adaptador);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }


        }

        private void rellenarArray() throws JSONException {
            listadoPromociones = new ArrayList();
            for (int i = 0; i < listadoPromocionesJSON.length(); i++) {
                JSONObject jsonobject = listadoPromocionesJSON.getJSONObject(i);
                Producto producto = new Producto(jsonobject.getInt("idProducto"),jsonobject.getString("nombre"),jsonobject.getString("descripcion"),jsonobject.getDouble("precio"),jsonobject.getInt("iva"), jsonobject.getDouble("ofertaDescuento"),jsonobject.getInt("activo"),jsonobject.getInt("idTipoProducto"),jsonobject.getString("urlImagen"));
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
