package com.foodxplorer.foodxplorer.fragments;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.foodxplorer.foodxplorer.objetos.Direccion;
import com.foodxplorer.foodxplorer.objetos.Estado;
import com.foodxplorer.foodxplorer.objetos.LineasPedido;
import com.foodxplorer.foodxplorer.MainActivity;
import com.foodxplorer.foodxplorer.objetos.Pedidos;
import com.foodxplorer.foodxplorer.objetos.Producto;
import com.foodxplorer.foodxplorer.R;
import com.foodxplorer.foodxplorer.adapters.AdaptadorProducto;
import com.foodxplorer.foodxplorer.helpers.RestManager;
import com.foodxplorer.foodxplorer.helpers.Settings;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.ArrayList;

import static com.foodxplorer.foodxplorer.helpers.Settings.LOGTAG;

public class FragmentResumenPedido extends Fragment {
    private MainActivity tienda;
    private TextView nombreCliente, importe, direccion, estado;
    private double importeTotal;
    private Pedidos pedido;
    private Direccion direccionObject;
    private Estado estadoObjeto;
    private ArrayList<LineasPedido> listaLineasPedido;
    private ArrayList<Producto> listaProductos;
    AdaptadorProducto adapProducto;
    ListView listView;

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
        listView = (ListView) view.findViewById(R.id.listViewInfoPedido);
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
        TareaWSRecuperarProductosPedido tareaProductos = new TareaWSRecuperarProductosPedido();
        tareaProductos.execute();
        return view;
    }

    class TareaWSRecuperarDireccion extends AsyncTask<Object, Void, Boolean> {
        JSONObject direccionJSON;

        @Override
        protected Boolean doInBackground(Object... params) {
            try {
                direccionJSON = readJsonFromUrl(Settings.DIRECCIO_SERVIDOR + Settings.PATH+"/direccion/obtener2/" + pedido.getIdDireccion());
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
            try {
                String url = Settings.DIRECCIO_SERVIDOR + Settings.PATH+ "/pedido/obtenerDetalles/" + pedido.getIdPedido();
                RestManager restManager = new RestManager(url);
                restManager.setRequestMethod(RestManager.GET);
                reader = restManager.getBufferedReader();
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


        @Override
        protected void onPostExecute(Boolean result) {
            if (result) {
                for (LineasPedido linea : listaLineasPedido) {
                    importeTotal += linea.getPrecio();
                }
                importe.setText(String.valueOf(importeTotal) + "€");
            }
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

    class TareaWSRecuperarProductosPedido extends AsyncTask<Object, Void, Boolean> {
        JSONArray productosJSON;

        @Override
        protected Boolean doInBackground(Object... params) {
            try {
                String url = Settings.DIRECCIO_SERVIDOR + Settings.PATH + "obtenerProductosPorIdPedido/" + pedido.getIdPedido();
                RestManager restManager = new RestManager(url);
                BufferedReader reader = restManager.getBufferedReader();
                productosJSON = new JSONArray(reader.readLine());
            } catch (java.io.FileNotFoundException ex) {
                Log.e(LOGTAG, "Error al obtener los productos");
            } catch (java.io.IOException ex) {
                Log.e(LOGTAG, "Temps d'espera esgotat al iniciar la conexio amb la BBDD externa:");
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return true;
        }


        @Override
        protected void onPostExecute(Boolean result) {
            if (result) {
                try {
                    if (!rellenarArray() || tienda.carrito.getUsuarioLogueado() == null || tienda.carrito.getUsuarioLogueado().equals("")) {
                        Toast.makeText(tienda, "ERROR", Toast.LENGTH_SHORT).show();
                    } else {
                        adapProducto = new AdaptadorProducto(getActivity(), listaProductos);
                        listView.setAdapter(adapProducto);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }

        private boolean rellenarArray() throws JSONException {
            boolean estado;
            listaProductos = new ArrayList<>();
            if (productosJSON.length() > 0) {
                for (int i = 0; i < productosJSON.length(); i++) {
                    JSONObject jsonobject = productosJSON.getJSONObject(i);
                    Producto p = new Producto(jsonobject.getInt("idProducto"),
                            jsonobject.getString("nombre"), jsonobject.getString("descripcion"),
                            jsonobject.getDouble("precio"), jsonobject.getInt("iva"),
                            jsonobject.getInt("ofertaDescuento"), jsonobject.getInt("activo"),
                            jsonobject.getInt("idTipoProducto"),
                            jsonobject.getString("urlImagen"));
                    listaProductos.add(p);
                }
                estado = true;
            } else {
                estado = false;
            }
            return estado;
        }
    }


}
