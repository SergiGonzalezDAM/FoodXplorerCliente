package com.foodxplorer.foodxplorer.fragments;

import android.os.AsyncTask;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.foodxplorer.foodxplorer.Direccion;
import com.foodxplorer.foodxplorer.Estado;
import com.foodxplorer.foodxplorer.LineasPedido;
import com.foodxplorer.foodxplorer.MainActivity;
import com.foodxplorer.foodxplorer.Pedidos;
import com.foodxplorer.foodxplorer.R;
import com.foodxplorer.foodxplorer.helpers.AsyncResponse;
import com.foodxplorer.foodxplorer.helpers.Settings;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

import static com.foodxplorer.foodxplorer.helpers.Settings.LOGTAG;

public class FragmentResumenPedido extends Fragment {
    private MainActivity tienda;
    private TextView nombreCliente, importe, direccion, estado;
    private double importeTotal;
    private Pedidos pedido;
    private Direccion direccionObject;
    private Estado estadoObjeto;
    private ArrayList<LineasPedido> listaLineasPedido;
    private ArrayList<Long> listaIdProductos;

    public FragmentResumenPedido() {
        // Required empty public constructor
    }

    public FragmentResumenPedido(MainActivity tienda, Pedidos pedido) {
        this.tienda = tienda;
        this.pedido = pedido;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_resumen_pedido, container, false);
        nombreCliente = (TextView) view.findViewById(R.id.textViewNombreCliente);
        importe = (TextView) view.findViewById(R.id.textViewImporte);
        direccion = (TextView) view.findViewById(R.id.textViewDireccion);
        estado = (TextView) view.findViewById(R.id.textViewEstadoResumenPedido);
        nombreCliente.setText(tienda.carrito.getUsuarioLogueado());
        TareaWSRecuperarDireccion tareaRecDir = new TareaWSRecuperarDireccion();
        tareaRecDir.execute();
        TareaWSRecuperarLineasPedido tareaLines = new TareaWSRecuperarLineasPedido();
        tareaLines.execute();
        TareaWSRecuperarEstado tareaEstado = new TareaWSRecuperarEstado();
        tareaEstado.execute();
        List<String> lista = new ArrayList();
        lista.add("Barbacoa");
        lista.add("4 Quesos");
        ListView listView = (ListView) view.findViewById(R.id.listViewInfoPedido);
        ArrayAdapter<String> adaptador = new ArrayAdapter(view.getContext(), android.R.layout.simple_list_item_1, lista);
        listView.setAdapter(adaptador);
        return view;
    }

    class TareaWSRecuperarDireccion extends AsyncTask<Object, Void, Boolean> {
        JSONObject direccionJSON;

        @Override
        protected Boolean doInBackground(Object... params) {
            try {
                direccionJSON = readJsonFromUrl(Settings.DIRECCIO_SERVIDOR + "ServcioFoodXPlorer/webresources/generic/direccion/obtener2/" + pedido.getIdDireccion());
            } catch (java.io.FileNotFoundException ex) {
                Log.e(LOGTAG, "Error al obtener la direccion");
            } catch (java.io.IOException ex) {
                Log.e(LOGTAG, "Temps d'espera esgotat al iniciar la conexio amb la BBDD externa:");
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return true;
        }


        private String readAll(Reader rd) throws IOException {
            StringBuilder sb = new StringBuilder();
            int cp;
            while ((cp = rd.read()) != -1) {
                sb.append((char) cp);
            }
            return sb.toString();
        }

        private JSONObject readJsonFromUrl(String url) throws IOException, JSONException {
            InputStream is = new URL(url).openStream();
            try {
                BufferedReader rd = new BufferedReader(new InputStreamReader(is, Charset.forName("UTF-8")));
                String jsonText = readAll(rd);
                JSONObject json = new JSONObject(jsonText);
                return json;
            } finally {
                is.close();
            }
        }

        @Override
        protected void onPostExecute(Boolean result) {
            if (result) {
                try {
                    if (!rellenarObjeto() || tienda.carrito.getUsuarioLogueado() == null || tienda.carrito.getUsuarioLogueado().equals("")) {
                        Toast.makeText(tienda, "NO EXISTE ESE NUMERO DE PEDIDO", Toast.LENGTH_SHORT).show();
                    } else {
                        direccion.setText(direccionObject.getPiso() + "\n" + direccionObject.getCalle() + "\n" + direccionObject.getPoblacion());
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }

        private boolean rellenarObjeto() throws JSONException {
            boolean estado;
            if (direccionJSON != null) {
                direccionObject = new Direccion(direccionJSON.getLong("idDireccion"), direccionJSON.getString("calle"), direccionJSON.getString("piso"),
                        direccionJSON.getString("poblacion"), direccionJSON.getString("codPostal"));
                estado = true;
            } else {
                estado = false;
            }
            return estado;
        }
    }

    class TareaWSRecuperarLineasPedido extends AsyncTask<Object, Void, Boolean> {
        JSONArray lineasPedidoJSON;

        @Override
        protected Boolean doInBackground(Object... params) {
            BufferedReader reader;
            URL url = null;
            try {
                url = new URL(Settings.DIRECCIO_SERVIDOR + "ServcioFoodXPlorer/webresources/generic/pedido/obtenerDetalles/" + pedido.getIdPedido());
                reader = getBufferedReader(url);
                lineasPedidoJSON = new JSONArray(reader.readLine());

            } catch (java.io.FileNotFoundException ex) {
                Log.e(LOGTAG, "Error al obtener la direccion");
            } catch (java.io.IOException ex) {
                Log.e(LOGTAG, "Temps d'espera esgotat al iniciar la conexio amb la BBDD externa:");
            } catch (JSONException e) {
                e.printStackTrace();
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
                    if (!rellenarArray() || tienda.carrito.getUsuarioLogueado() == null || tienda.carrito.getUsuarioLogueado().equals("")) {
                        Toast.makeText(tienda, "ERROR", Toast.LENGTH_SHORT).show();
                    } else {
                        for (LineasPedido linea : listaLineasPedido) {
                            importeTotal += linea.getPrecio();
                        }
                        importe.setText(String.valueOf(importeTotal) + "€");
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }

        private boolean rellenarArray() throws JSONException {
            boolean estado;
            listaLineasPedido = new ArrayList<>();
            listaIdProductos = new ArrayList<>();
            if (lineasPedidoJSON.length() > 0) {
                for (int i = 0; i < lineasPedidoJSON.length(); i++) {
                    JSONObject jsonobject = lineasPedidoJSON.getJSONObject(i);
                    LineasPedido lineasPedido = new LineasPedido(jsonobject.getLong("idPedido"), jsonobject.getLong("idProducto"), jsonobject.getInt("cantidad"),
                            jsonobject.getDouble("precio"), jsonobject.getInt("iva"));
                    listaIdProductos.add(lineasPedido.getIdProducto());
                    listaLineasPedido.add(lineasPedido);
                }
                estado = true;
            } else {
                estado = false;
            }
            return estado;
        }
    }

    class TareaWSRecuperarEstado extends AsyncTask<Object, Void, Boolean> {
        JSONObject estadoJSON;

        @Override
        protected Boolean doInBackground(Object... params) {
            try {
                estadoJSON = readJsonFromUrl(Settings.DIRECCIO_SERVIDOR + "ServcioFoodXPlorer/webresources/generic/obtenerEstado/" + pedido.getIdEstado());
            } catch (java.io.FileNotFoundException ex) {
                Log.e(LOGTAG, "Error al obtener la direccion");
            } catch (java.io.IOException ex) {
                Log.e(LOGTAG, "Temps d'espera esgotat al iniciar la conexio amb la BBDD externa:");
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return true;
        }


        private String readAll(Reader rd) throws IOException {
            StringBuilder sb = new StringBuilder();
            int cp;
            while ((cp = rd.read()) != -1) {
                sb.append((char) cp);
            }
            return sb.toString();
        }

        private JSONObject readJsonFromUrl(String url) throws IOException, JSONException {
            InputStream is = new URL(url).openStream();
            try {
                BufferedReader rd = new BufferedReader(new InputStreamReader(is, Charset.forName("UTF-8")));
                String jsonText = readAll(rd);
                JSONObject json = new JSONObject(jsonText);
                return json;
            } finally {
                is.close();
            }
        }

        @Override
        protected void onPostExecute(Boolean result) {
            if (result) {
                try {
                    if (!rellenarObjeto() || tienda.carrito.getUsuarioLogueado() == null || tienda.carrito.getUsuarioLogueado().equals("")) {
                        Toast.makeText(tienda, "ERROR CON EL ESTADO", Toast.LENGTH_SHORT).show();
                    } else {
                        estado.setText(estadoObjeto.getNomEstado());
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }

        private boolean rellenarObjeto() throws JSONException {
            boolean estado;
            if (estadoJSON != null) {
                estadoObjeto = new Estado(estadoJSON.getLong("idEstado"), estadoJSON.getString("nombreEstado"), estadoJSON.getDouble("tiempo"));
                estado = true;
            } else {
                estado = false;
            }
            return estado;
        }
    }


}
