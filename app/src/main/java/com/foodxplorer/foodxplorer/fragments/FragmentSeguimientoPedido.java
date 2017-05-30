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
    private boolean numeroPedidoEncontrado;
    private TareaWSRecuperarPedidosSeguimiento tarea;
    Pedidos pedido;

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

    @Override
    public void onClick(View view) {
        numPedido = String.valueOf(editTextNumPedido.getText());
        tarea = new TareaWSRecuperarPedidosSeguimiento();
        tarea.execute();
        if (numeroPedidoEncontrado) {
            Fragment fragment = new FragmentResumenPedido(tienda, pedido);
            tienda.getSupportFragmentManager().beginTransaction()
                    .replace(R.id.content_frame, fragment)
                    .commit();
//            this.tienda.goTo(MainActivity.PEDIDOSRESUMEN);
        }
    }

    class TareaWSRecuperarPedidosSeguimiento extends AsyncTask<Object, Void, Boolean> {
        JSONObject pedidoJSON;
        public AsyncResponse delegate = null;


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
                    if (tienda.carrito.getUsuarioLogueado().equals("")) {
                        Toast.makeText(tienda, "Logueate para ver pedidos.", Toast.LENGTH_SHORT).show();
                    } else {
                        if (!rellenarObjeto()) {
                            Toast.makeText(tienda, "NO EXISTE ESE NUMERO DE PEDIDO", Toast.LENGTH_SHORT).show();
                        } else {
                            numeroPedidoEncontrado = true;
                            Log.d(Settings.LOGTAG, "PostExecuteSeguimiento. Pedido encontrado");
                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }

        private boolean rellenarObjeto() throws JSONException {
            boolean estado;
            if (pedidoJSON != null) {
                pedido = new Pedidos(pedidoJSON.getLong("idPedido"), pedidoJSON.getString("fechaSalida"),
                        pedidoJSON.getLong("idDireccion"), pedidoJSON.getLong("idEstado"));
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
