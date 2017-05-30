package com.foodxplorer.foodxplorer.fragments;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.Toast;

import com.foodxplorer.foodxplorer.MainActivity;
import com.foodxplorer.foodxplorer.R;
import com.foodxplorer.foodxplorer.helpers.*;
import com.foodxplorer.foodxplorer.helpers.AsyncResponse;
import com.foodxplorer.foodxplorer.objetos.Pedidos;
import com.foodxplorer.foodxplorer.objetos.Producto;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

public class FragmentCarrito extends Fragment implements AdapterView.OnClickListener {

    MainActivity tienda;
    private ArrayList<String> direccionsClient=new ArrayList<>();

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
        Button boto = (Button) view.findViewById(R.id.botonPagar);
        boto.setOnClickListener(FragmentCarrito.this);

        TareaObtenerDirecciones obtenerDirecciones = new TareaObtenerDirecciones();
        obtenerDirecciones.execute();

        return view;
    }

public void carregarSpinnerDireccions(){
    Spinner mySpinner = (Spinner) getView().findViewById(R.id.spinnerDireccionesConfirmarPedido);
    // Spinner adapter

    //Boolea de control per determinar si s'ha rebut totes les dades extenes
}

    @Override
    public void onClick(View view) {
        System.out.println("Insertado");
    }
    class TareaObtenerDirecciones extends AsyncTask<Object, Void, Boolean> {

        AsyncResponse delegate = null;

        @Override
        protected Boolean doInBackground(Object... params) {
            boolean result = true;
            BufferedReader osw;
            RestManager restManager;
            try {
                String aux = (Settings.DIRECCIO_SERVIDOR + Settings.PATH + "direccion/obtenerPorCorreo/"+tienda.carrito.getUsuarioLogueado());
                restManager = new RestManager(aux);
                osw = restManager.getBufferedReader();
                JSONArray jsonarray = new JSONArray(osw.readLine());
                //Afegim totes les direccions del client registrat trobats a un array
                direccionsClient.add("Seleccione direccion");
                for (int i = 0; i < jsonarray.length(); i++) {
                    JSONObject jsonobject = jsonarray.getJSONObject(i);
                    System.out.println(jsonobject.toString());
                    direccionsClient.add(jsonobject.optString("calle")+" "+jsonobject.optString("piso")+" "+jsonobject.optString("poblacion"));
                }
                osw.close();
                restManager.disconnect();
            } catch (java.net.ProtocolException ex) {
                Log.e(Settings.LOGTAG, "Error de protocol: " + ex);
                result = false;
            } catch (java.io.FileNotFoundException | java.net.MalformedURLException ex) {
                Log.e(Settings.LOGTAG, "Error de ruta d'acces: " + ex);
                result = false;
            } catch (java.net.SocketTimeoutException ex) {
                Log.e(Settings.LOGTAG, "Temps d'espera esgotat al iniciar la conexio amb la BBDD extena: " + ex);
                result = false;
            } catch (java.io.IOException ex) {
                Log.e(Settings.LOGTAG, "Undefined error: " + ex);
                result = false;
            } catch (JSONException e) {
                Log.e(Settings.LOGTAG, "Json error: " + e);
            }
            return result;
        }

        @Override
        protected void onPostExecute(Boolean result) {
            if (result) {

            } else {
                Log.e(Settings.LOGTAG, "Error al carregar les dades de les direccions");
            }

            delegate.processFinish(result);
        }
    }

    class TareaInsertarPedido extends AsyncTask<Object, Void, Boolean> {

        AsyncResponse delegate = null;

        @Override
        protected Boolean doInBackground(Object... params) {
            boolean result = true;
            InputStreamReader osw;
            RestManager restManager;
            try {
                Usuario user = (Usuario) params[0];
                String aux = (Settings.DIRECCIO_SERVIDOR + Settings.PATH + "pedido/insertar/");
                Pedidos pedido= new Pedidos();


                //pedido.setIdDireccion();
                pedido.setFechaPedido("hoy");
                aux = aux + "/" + user.getUsername() + "/" + user.getPassword() + "/";
                restManager = new RestManager(aux);
                osw = restManager.getInputStream();
                int data = osw.read();
                String res = "";
                while (data != -1) {
                    char current = (char) data;
                    data = osw.read();
                    res = res + current;
                }
                if (!res.equals("true")) {
                    Log.e(Settings.LOGTAG, "Error de login para user: " + user.getUsername());
                    result = false;
                }
                osw.close();
                restManager.disconnect();
            } catch (java.net.ProtocolException ex) {
                Log.e(Settings.LOGTAG, "Error de protocol: " + ex);
                result = false;
            } catch (java.io.FileNotFoundException | java.net.MalformedURLException ex) {
                Log.e(Settings.LOGTAG, "Error de ruta d'acces: " + ex);
                result = false;
            } catch (java.net.SocketTimeoutException ex) {
                Log.e(Settings.LOGTAG, "Temps d'espera esgotat al iniciar la conexio amb la BBDD extena: " + ex);
                result = false;
            } catch (java.io.IOException ex) {
                Log.e(Settings.LOGTAG, "Undefined error: " + ex);
                result = false;
            }
            return result;
        }

        @Override
        protected void onPostExecute(Boolean result) {
            delegate.processFinish(result);
        }
    }
}
