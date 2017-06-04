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

import com.foodxplorer.foodxplorer.MainActivity;
import com.foodxplorer.foodxplorer.R;
import com.foodxplorer.foodxplorer.adapters.AdaptadorProducto;
import com.foodxplorer.foodxplorer.helpers.RestManager;
import com.foodxplorer.foodxplorer.helpers.Settings;
import com.foodxplorer.foodxplorer.objetos.Direccion;
import com.foodxplorer.foodxplorer.objetos.Estado;
import com.foodxplorer.foodxplorer.objetos.LineasPedido;
import com.foodxplorer.foodxplorer.objetos.Pedidos;
import com.foodxplorer.foodxplorer.objetos.Producto;

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
    AdaptadorProducto adapProducto;
    ListView listView;
    private MainActivity tienda;
    private TextView nombreCliente, importe, direccion, estado;
    private double importeTotal;
    private Pedidos pedido;
    private Direccion direccionObject;
    private Estado estadoObjeto;
    private ArrayList<LineasPedido> listaLineasPedido;
    private ArrayList<Producto> listaProductos;

    public FragmentResumenPedido() {
        // Required empty public constructor
    }

    public FragmentResumenPedido(MainActivity tienda, Pedidos pedido) {
        this.tienda = tienda;
        this.pedido = pedido;
    }

    /**
     * Cuando carga el fragment identifica los componentes del fragment y lanza las tareas
     * correspondientes
     * @param inflater
     * @param container
     * @param savedInstanceState
     * @return
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

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

    /**
     * Generamos una clase anonima que extienda de AsyncTask para poder lanzar consultas
     */
    private class TareaWSRecuperarDireccion extends AsyncTask<Object, Void, Boolean> {
        JSONObject direccionJSON;

        /**
         * Se lanza este método al ejecutar la consulta lo que hacemos es rellenar un objeto JSON
         * con los datos recibidos del servidor
         * @param params
         * @return
         */
        @Override
        protected Boolean doInBackground(Object... params) {
            try {
                direccionJSON = readJsonFromUrl(Settings.DIRECCIO_SERVIDOR + Settings.PATH+"direccion/obtener2/" + pedido.getIdDireccion());
            } catch (java.io.FileNotFoundException ex) {
                Log.e(LOGTAG, "Error al obtener la direccion:"+Settings.DIRECCIO_SERVIDOR + Settings.PATH+"direccion/obtener2/" + pedido.getIdDireccion());
            } catch (java.io.IOException ex) {
                Log.e(LOGTAG, "Temps d'espera esgotat al iniciar la conexio amb la BBDD externa:");
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return true;
        }

        /**
         * Método que le pasamos un objeto Reader y leemos el contenido del mismo hasta que no hayan
         * más datos disponibles para leer, todo esto leido se pasa como String en el return
         * @param rd
         * @return
         * @throws IOException Error
         */
        private String readAll(Reader rd) throws IOException {
            StringBuilder sb = new StringBuilder();
            int cp;
            while ((cp = rd.read()) != -1) {
                sb.append((char) cp);
            }
            return sb.toString();
        }

        /**
         * Método en el cual le pasamos una url y nos obtiene todos los datos de esa url
         * @param url la url para cargar
         * @return objeto JSON
         * @throws IOException Error
         * @throws JSONException Error Json
         */
        private JSONObject readJsonFromUrl(String url) throws IOException, JSONException {
            InputStream is = new URL(url).openStream();
            try {
                BufferedReader rd = new BufferedReader(new InputStreamReader(is, Charset.forName("UTF-8")));
                String jsonText = readAll(rd);
                return new JSONObject(jsonText);
            } finally {
                is.close();
            }
        }

        /**
         * Después de ejecutar la tarea verifica si la dirección existe y si está logueado, en el
         * caso de que esas condiciones se cumplen obtenemos los datos del JSON y pintamos en el
         * fragment la dirección del cliente
         * @param result
         */
        @Override
        protected void onPostExecute(Boolean result) {
            if (result) {
                try {
                    if (!rellenarObjeto() || tienda.carrito.getUsuarioLogueado() == null || tienda.carrito.getUsuarioLogueado().equals("")) {
                        Toast.makeText(tienda, "NO EXISTE ESE NUMERO DE PEDIDO", Toast.LENGTH_SHORT).show();
                    } else {
                        direccion.setText(direccionObject.getCalle() + direccionObject.getPiso() + "\n" + direccionObject.getPoblacion() + "\n" + direccionObject.getCodPostal());
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }

        /**
         * En el caso de que el objeto JSON tenga datos, los cargamos en un objeto dirección y devolvemos true
         * @return
         * @throws JSONException
         */
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

    private class TareaWSRecuperarLineasPedido extends AsyncTask<Object, Void, Boolean> {
        JSONArray lineasPedidoJSON;

        /**
         *Cuando lanzamos la tarea se ejecuta este método que lo configuramos de tal forma que
         * hacemos método GET y leemos los datos que nos llegan de la url
         * @param params
         * @return
         */
        @Override
        protected Boolean doInBackground(Object... params) {
            BufferedReader reader;
            String url=null;
            try {
                url = Settings.DIRECCIO_SERVIDOR + Settings.PATH+ "pedido/obtenerDetalles/" + pedido.getIdPedido();
                RestManager restManager = new RestManager(url);
                restManager.setRequestMethod(RestManager.GET);
                reader = restManager.getBufferedReader();
                lineasPedidoJSON = new JSONArray(reader.readLine());

            } catch (java.io.FileNotFoundException ex) {
                Log.e(LOGTAG, "Error al obtener los detalles:" + url);
            } catch (java.io.IOException ex) {
                Log.e(LOGTAG, "Temps d'espera esgotat al iniciar la conexio amb la BBDD externa:");
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return true;
        }

        /**
         * Una vez finalizada la tarea, verificamos si el servidor nos ha devuelto algo, en caso
         * de que esta respuesta sea sí, hacemos un for para recorrer el array de las lineasPedido y
         * vamos sumando el precio del producto
         *
         * @param result
         */
        @Override
        protected void onPostExecute(Boolean result) {
            if (result) {
                if(listaLineasPedido!=null) {
                    for (LineasPedido linea : listaLineasPedido) {
                        importeTotal += linea.getPrecio();
                    }
                    importe.setText(String.valueOf(importeTotal) + "€");
                }
                else{
                    Log.e(LOGTAG, "Error. ListalineasPedidos null;");
                }
            }
        }
    }

    private class TareaWSRecuperarEstado extends AsyncTask<Object, Void, Boolean> {
        JSONObject estadoJSON;

        /**
         * Al lanzar la tarea rellenamos un objeto JSON con los datos obtenidos del servidor
         *
         * @param params
         * @return
         */
        @Override
        protected Boolean doInBackground(Object... params) {
            try {
                estadoJSON = readJsonFromUrl(Settings.DIRECCIO_SERVIDOR + Settings.PATH+"obtenerEstado/" + pedido.getIdEstado());
            } catch (java.io.FileNotFoundException ex) {
                Log.e(LOGTAG, "Error al obtener el estado: "+Settings.DIRECCIO_SERVIDOR + Settings.PATH+"obtenerEstado/" + pedido.getIdEstado());
            } catch (java.io.IOException ex) {
                Log.e(LOGTAG, "Temps d'espera esgotat al iniciar la conexio amb la BBDD externa:");
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return true;
        }

        /**
         * Leemos los datos, obtenidos hasta que no queden más datos y los devolvemos comos String
         * @param rd
         * @return
         * @throws IOException
         */
        private String readAll(Reader rd) throws IOException {
            StringBuilder sb = new StringBuilder();
            int cp;
            while ((cp = rd.read()) != -1) {
                sb.append((char) cp);
            }
            return sb.toString();
        }

        /**
         * Tellenamos un objeto JSON con los datos obtenidos por el servidor
         * @param url
         * @return
         * @throws IOException
         * @throws JSONException
         */
        private JSONObject readJsonFromUrl(String url) throws IOException, JSONException {
            InputStream is = new URL(url).openStream();
            try {
                BufferedReader rd = new BufferedReader(new InputStreamReader(is, Charset.forName("UTF-8")));
                String jsonText = readAll(rd);
                return new JSONObject(jsonText);
            } finally {
                is.close();
            }
        }

        /**
         * Una vez hemos finalizado la tarea verificamos que todo haya ido correctamente y pintamos
         * el estado en el fragment
         * @param result
         */
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

        /**
         * Verificamos que el objeto JSON no sea null y creamos un objeto Estado con los datos del JSON
         * @return
         * @throws JSONException
         */
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

    private class TareaWSRecuperarProductosPedido extends AsyncTask<Object, Void, Boolean> {
        JSONArray productosJSON;

        /**
         * Al ejecutar la tarea se lanza este método, con el obtenemos los datos del servidor y los
         * almacenamos en un objeto JSON
         * @param params
         * @return
         */
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

        /**
         * Una vez finalizada la tarea verificamos que haya ido todo correctamente y en el caso de
         * que la respuesta sea sí, cargamos el adaptador en el listview de resumenPedido
         * @param result
         */
        @Override
        protected void onPostExecute(Boolean result) {
            if (result) {
                try {
                    if (!rellenarArray()) {
                        Toast.makeText(tienda, "Error en la obtencion de datos.", Toast.LENGTH_SHORT).show();
                        tienda.goTo(MainActivity.PROMOCIONES);
                    }
                    else if (tienda.carrito.getUsuarioLogueado() == null || tienda.carrito.getUsuarioLogueado().equals("")){
                        Toast.makeText(tienda, "Undefined Error", Toast.LENGTH_SHORT).show();
                    }
                    else {
                        adapProducto = new AdaptadorProducto(getActivity(), listaProductos);
                        Double totalPedido = 0.0;
                        for (Producto producto : listaProductos) {
                            totalPedido += producto.getPrecio() - (producto.getPrecio() * producto.getOfertaProducto()) / 100;
                        }
                        TextView precio = (TextView) getActivity().findViewById(R.id.textViewImporte);
                        precio.setText(String.valueOf(totalPedido));
                        listView.setAdapter(adapProducto);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }

        /**
         * Rellenamos el array de listaProductos con tantos productos como tenga el objetoJSON
         * @return
         * @throws JSONException
         */
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
