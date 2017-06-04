package com.foodxplorer.foodxplorer.fragments;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.foodxplorer.foodxplorer.MainActivity;
import com.foodxplorer.foodxplorer.R;
import com.foodxplorer.foodxplorer.helpers.AsyncResponse;
import com.foodxplorer.foodxplorer.helpers.Settings;
import com.foodxplorer.foodxplorer.objetos.Pedidos;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.URL;
import java.nio.charset.Charset;

import static com.foodxplorer.foodxplorer.helpers.Settings.LOGTAG;

public class FragmentSeguimientoPedido extends Fragment implements View.OnClickListener {
    EditText editTextNumPedido;
    Button btnBuscarPedidoParaSeguir;
    MainActivity tienda;
    String numPedido;
    Pedidos pedido;
    private TareaWSRecuperarPedidosSeguimiento tarea;

    public FragmentSeguimientoPedido() {
        // Required empty public constructor
    }

    public FragmentSeguimientoPedido(MainActivity tienda) {
        this.tienda = tienda;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view;
        view = inflater.inflate(R.layout.fragment_seguimientopedido, container, false);
        editTextNumPedido = (EditText) view.findViewById(R.id.editTextNumeroSeguimiento);
        btnBuscarPedidoParaSeguir = (Button) view.findViewById(R.id.btnBuscarPedido);
        btnBuscarPedidoParaSeguir.setOnClickListener(this);
        return view;
    }

    /**
     * Método onClick en el cual se gestionan los clicks del fragment, en este caso como solo
     * tenemos un botón no es necesario identificar el click(id), al clicar en el botón nos ejecuta
     * la tarea que extiende de AsyncTask, en el caso que al finalizar la tarea numeroPedidoEncontrado
     * sea true cambiaremos de fragment
     * @param view
     */
    @Override
    public void onClick(View view) {
        numPedido = String.valueOf(editTextNumPedido.getText());
        tarea = new TareaWSRecuperarPedidosSeguimiento();
        tarea.execute();

    }

    /**
     * Creamos una clase anonima que extienda de AsyncTask para poder ejecutar las consultas lanzadas
     * por el rest
     */
    class TareaWSRecuperarPedidosSeguimiento extends AsyncTask<Object, Void, Boolean> {
        public AsyncResponse delegate = null;
        JSONObject pedidoJSON;

        /**
         * Método que se ejecuta al lanzar la AsyncTask, rellenamos el objeto JSON con los datos de la consulta
         * @param params
         * @return
         */
        @Override
        protected Boolean doInBackground(Object... params) {
            try {
                Log.d(LOGTAG, Settings.DIRECCIO_SERVIDOR + Settings.PATH + "obtenerPedido/" + numPedido);
                pedidoJSON = readJsonFromUrl(Settings.DIRECCIO_SERVIDOR + Settings.PATH + "obtenerPedido/" + numPedido);
            } catch (java.io.FileNotFoundException ex) {
                Log.e(LOGTAG, "Error al obtener el pedido");
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
         * Método en el cual le pasamos una url y nos obtiene todos los datos de esa url
         * @param url
         * @return objeto JSON
         * @throws IOException
         * @throws JSONException
         */
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

        /**
         * Cuando ha acabado la tarea de ejecutarse se llama a este método que verificamos que:
         * 1. Esté logueado, si no lo está manda un error por pantalla
         * 2. Si está ejecutado verifica que el número de pedido exista
         * 3. Si existe, cambia la variable numeroPedidoEncontrado a true
         * @param result
         */
        @Override
        protected void onPostExecute(Boolean result) {
            if (result) {
                try {

                        if (!rellenarObjeto()) {
                            Toast.makeText(tienda, R.string.PEDIDO_O_USUARIO_NOT_FOUND, Toast.LENGTH_SHORT).show();
                        } else {
                            Log.d(Settings.LOGTAG, "PostExecuteSeguimiento. Pedido encontrado" + pedido.toString());
                                Fragment fragment = new FragmentResumenPedido(tienda, pedido);
                                tienda.getSupportFragmentManager().beginTransaction()
                                        .replace(R.id.content_frame, fragment)
                                        .commit();
                          }

                }
                 catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }

        /**
         * Verifica que un numero de pedido exista y devuelve el boolean si es true o false
         * @return
         * @throws JSONException
         */
        private boolean rellenarObjeto() throws JSONException {
            boolean estado;
            if (pedidoJSON != null) {
                pedido = new Pedidos(pedidoJSON.getLong("idPedido"), pedidoJSON.getString("fechaSalida"),
                        pedidoJSON.getLong("idDireccion"), pedidoJSON.getLong("idEstado"));
                if (!pedidoJSON.isNull("correo")) {
                    pedido.setUser(pedidoJSON.getString("correo"));
                }
                estado = true;
                System.out.println("Encontrado");
            } else {
                Log.e(Settings.LOGTAG, "Ningun dato obtenido.");
                estado = false;
            }
            return estado;
        }
    }

}
