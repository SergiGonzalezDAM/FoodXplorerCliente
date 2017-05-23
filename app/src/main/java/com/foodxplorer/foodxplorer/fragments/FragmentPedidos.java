package com.foodxplorer.foodxplorer.fragments;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.Toast;

import com.foodxplorer.foodxplorer.MainActivity;
import com.foodxplorer.foodxplorer.Producto;
import com.foodxplorer.foodxplorer.adapters.AdaptadorPedido;
import com.foodxplorer.foodxplorer.Pedidos;
import com.foodxplorer.foodxplorer.R;

import com.foodxplorer.foodxplorer.adapters.AdaptadorProducto;
import com.foodxplorer.foodxplorer.helpers.AsyncResponse;
import com.foodxplorer.foodxplorer.helpers.RestManager;
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


public class FragmentPedidos extends Fragment {
    private MainActivity tienda;
    private ListView lista;

    public FragmentPedidos(MainActivity tienda) {
        this.tienda = tienda;
    }

    public FragmentPedidos() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        TareaWSRecuperarPedidos tareaPedidos = new TareaWSRecuperarPedidos();
        tareaPedidos.execute();
        View view = inflater.inflate(R.layout.fragment_pedidos, container, false);
        lista = (ListView) view.findViewById(R.id.listViewPedidos);
//        Pedidos pedido;
//        // Introduzco los datos
//        pedido = new Pedidos("56431351", "15/05/2017");
//        arraydira.add(pedido);
//        pedido = new Pedidos("13152541", "11/05/2017");
//        arraydira.add(pedido);
//        pedido = new Pedidos("35431565", "12/05/2017");
//        arraydira.add(pedido);
//        adaptador = new AdaptadorPedido(getActivity(), arraydira);
        //       lista.setAdapter(adaptador);
        return view;

    }

    class TareaWSRecuperarPedidos extends AsyncTask<Object, Void, Boolean> {
        JSONArray listadoPedidosJSON;
        public AsyncResponse delegate = null;
        private AdaptadorPedido adaptador;
        ArrayList<Pedidos> listaPedidos;

        @Override
        protected Boolean doInBackground(Object... params) {
            BufferedReader reader;
            URL url = null;
            try {
                System.out.println(tienda.CurrentState.getUsuarioLogueado());
                url = new URL(Settings.DIRECCIO_SERVIDOR + "ServcioFoodXPlorer/webresources/generic/pedidos/" + tienda.CurrentState.getUsuarioLogueado());
                reader = getBufferedReader(url);
                listadoPedidosJSON = new JSONArray(reader.readLine());
            } catch (java.io.FileNotFoundException ex) {
                Log.e(LOGTAG, "Error al obtenir la posicio de:" + url.toString() + "\n" + ex);
            } catch (java.io.IOException ex) {
                Log.e(LOGTAG, "Temps d'espera esgotat al iniciar la conexio amb la BBDD externa:" + url.toString() + "\n" + ex);
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
            if (result) {
                try {
                    if (!rellenarArray()) {
                        Toast.makeText(tienda,"NO HAY PEDIDOS",Toast.LENGTH_SHORT).show();
                    } else {
                        adaptador = new AdaptadorPedido(getActivity(), listaPedidos);
                        lista.setAdapter(adaptador);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }

        private boolean rellenarArray() throws JSONException {
            boolean estado;
            listaPedidos = new ArrayList();
            if (listadoPedidosJSON.length() > 0) {
                for (int i = 0; i < listadoPedidosJSON.length(); i++) {
                    JSONObject jsonobject = listadoPedidosJSON.getJSONObject(i);
                    Pedidos pedido = new Pedidos(jsonobject.getLong("idPedido"), jsonobject.getString("fechaSalida"));
                    listaPedidos.add(pedido);
                }
                estado = true;
            } else {
                estado = false;
            }
            return estado;
        }
    }
}
