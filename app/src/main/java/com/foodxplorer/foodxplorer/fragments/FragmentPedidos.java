package com.foodxplorer.foodxplorer.fragments;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.foodxplorer.foodxplorer.MainActivity;
import com.foodxplorer.foodxplorer.R;
import com.foodxplorer.foodxplorer.adapters.AdaptadorPedido;
import com.foodxplorer.foodxplorer.helpers.RestManager;
import com.foodxplorer.foodxplorer.helpers.Settings;
import com.foodxplorer.foodxplorer.objetos.Pedidos;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.util.ArrayList;

import static com.foodxplorer.foodxplorer.helpers.Settings.LOGTAG;


public class FragmentPedidos extends Fragment implements AdapterView.OnItemClickListener {
    private MainActivity tienda;
    private ListView lista;

    private AdaptadorPedido adaptador;
    private ArrayList<Pedidos> listaPedidos;

    public FragmentPedidos(MainActivity tienda) {
        this.tienda = tienda;
    }

    public FragmentPedidos() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        if (tienda.carrito.getUsuarioLogueado() != null && !tienda.carrito.getUsuarioLogueado().equals("")) {
            TareaWSRecuperarPedidos tareaPedidos = new TareaWSRecuperarPedidos();
            tareaPedidos.execute();
        } else {
            Toast.makeText(tienda, "NO ESTÁS LOGUEADO", Toast.LENGTH_SHORT).show();
        }
        View view = inflater.inflate(R.layout.fragment_pedidos, container, false);
        lista = (ListView) view.findViewById(R.id.listViewPedidos);
        lista.setOnItemClickListener(FragmentPedidos.this);

        return view;
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Pedidos pedido;
        pedido = listaPedidos.get(position);
        Fragment fragment = new FragmentResumenPedido(tienda, pedido);
        tienda.getSupportFragmentManager().beginTransaction()
                .replace(R.id.content_frame, fragment)
                .commit();
    }

    class TareaWSRecuperarPedidos extends AsyncTask<Object, Void, Boolean> {
        JSONArray listadoPedidosJSON;


        @Override
        protected Boolean doInBackground(Object... params) {
            BufferedReader reader;
            String url=null;
            boolean result=true;
            try {
                url =Settings.DIRECCIO_SERVIDOR + Settings.PATH +"pedidos/" + tienda.carrito.getUsuarioLogueado();
                System.out.println(url);
                RestManager restManager = new RestManager(url);
                restManager.setRequestMethod(RestManager.GET);
                reader = restManager.getBufferedReader();
                listadoPedidosJSON = new JSONArray(reader.readLine());
            } catch (java.io.FileNotFoundException ex) {
                Log.e(LOGTAG, "Error al obtener el pedido de:" + url + "\n" + ex);
                result=false;
            } catch (java.io.IOException ex) {
                Log.e(LOGTAG, "Temps d'espera esgotat al iniciar la conexio amb la BBDD externa:" + url.toString() + "\n" + ex);
                result=false;
            } catch (org.json.JSONException ex) {
                Log.e(LOGTAG, "Error en la transformacio de l'objecte JSON: " + ex);
                result=false;
            }
            return result;
        }


        @Override
        protected void onPostExecute(Boolean result) {
            if (result) {
                try {
                    if (!rellenarArray()) {
                        Toast.makeText(tienda, "NO HAY PEDIDOS", Toast.LENGTH_SHORT).show();
                        tienda.goTo(MainActivity.PROMOCIONES);
                    } else {
                        adaptador = new AdaptadorPedido(getActivity(), listaPedidos);
                        lista.setAdapter(adaptador);

                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }else {
                Toast.makeText(tienda, "Error al recuperar los datos", Toast.LENGTH_SHORT).show();
                tienda.goTo(MainActivity.PROMOCIONES);
            }
        }

        private boolean rellenarArray() throws JSONException {
            boolean estado;
            listaPedidos = new ArrayList();
            if (listadoPedidosJSON.length() > 0) {
                for (int i = 0; i < listadoPedidosJSON.length(); i++) {
                    JSONObject jsonobject = listadoPedidosJSON.getJSONObject(i);
                    Pedidos pedido = new Pedidos(jsonobject.getLong("idPedido"), jsonobject.getString("fechaSalida"),
                            jsonobject.getLong("idDireccion"), jsonobject.getLong("idEstado"));
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
