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

import com.foodxplorer.foodxplorer.MainActivity;
import com.foodxplorer.foodxplorer.R;
import com.foodxplorer.foodxplorer.helpers.MD5;
import com.foodxplorer.foodxplorer.helpers.Settings;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;

import static com.foodxplorer.foodxplorer.helpers.Settings.LOGTAG;

interface AsyncResponse {
    void processFinish(boolean response);
}

public class FragmentRegistro extends Fragment implements View.OnClickListener, AsyncResponse {
    private EditText contrasena;
    private EditText correo;
    private Button btnRegistro;
    private MainActivity tienda;

    public FragmentRegistro() {
        // Required empty public constructor
    }
    public FragmentRegistro(MainActivity tienda)
    {
        this.tienda = tienda;
    }

    /**
     * Cuando cargamos el fragment lo primero que hacemos es detectar los componentes del fragment
     * @param inflater
     * @param container
     * @param savedInstanceState
     * @return
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_registro, container, false);
        contrasena = (EditText) view.findViewById(R.id.editTextPasswordLogin);
        correo = (EditText) view.findViewById(R.id.editTextCorreoRegistro);
        btnRegistro = (Button) view.findViewById(R.id.btnRegistrarRegistro);
        btnRegistro.setOnClickListener(this);
        return view;
    }

    /**
     * En el metodo onCLick detectamos los clicks de los botones, para hacer más correcta la
     * codificación miramos por el id si es el botón seleccionado, en el caso de que sea el correcto,
     * lanceremos la tarea Registrar.
     *
     * @param view
     */
    @Override
    public void onClick(View view) {
        if (R.id.btnRegistrarRegistro == view.getId()) {
            TareaWSRegistrarUsuario tareaRegistrar = new TareaWSRegistrarUsuario();
            tareaRegistrar.delegate = this;
            tareaRegistrar.execute(correo.getText().toString(), contrasena.getText().toString());
        }
    }

    /**
     * Cuando el registro sea correcto, nos llevará al fragment promociones
     * @param response
     */
    @Override
    public void processFinish(boolean response) {
        if (response) {
            Log.e(LOGTAG, "Registro ok");
            this.tienda.goTo(MainActivity.PROMOCIONES);
        } else {
            Log.e(LOGTAG, "Registro fail");
        }
    }

    class TareaWSRegistrarUsuario extends AsyncTask<Object, Integer, Boolean> {
        public AsyncResponse delegate = null;

        /**
         *  Al ejecutar la tarea, abrimos la conexión con el servidor, obtenemos los datos que se
         *  han escrito en el fragment y se los pasamos al servidor para que los trate con el método
         *  introducido en la url
         * @param params
         * @return
         */
        @Override
        protected Boolean doInBackground(Object... params) {
            boolean insertadoEnDBexterna = true;
            OutputStreamWriter osw;
            try {
                URL url = new URL(Settings.DIRECCIO_SERVIDOR + "ServcioFoodXPlorer/webresources/generic/InsertarUsuario");
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("PUT");
                conn.setDoOutput(true);
                conn.setReadTimeout(1000 /*milliseconds*/);
                conn.setConnectTimeout(500);
                conn.setRequestProperty("Content-Type", "application/json");
                osw = new OutputStreamWriter(conn.getOutputStream());
                osw.write(getStringJSON(params));
                osw.flush();
                osw.close();
                System.err.println(conn.getResponseMessage());
            } catch (java.io.IOException ex) {
                Log.e(LOGTAG, "Temps d'espera esgotat al iniciar la conexio amb la BBDD extena");
                insertadoEnDBexterna = false;
            } catch (org.json.JSONException ex) {
                Log.e(LOGTAG, "Error en la transformacio de l'objecte JSON: " + ex);
                insertadoEnDBexterna = false;
            }
            return insertadoEnDBexterna;
        }

        /**
         * Le pasamos los parametros para insertarlos en la base de datos.
         * @param params
         * @return
         * @throws JSONException
         * @throws UnsupportedEncodingException
         */
        private String getStringJSON(Object... params) throws JSONException, UnsupportedEncodingException {
            JSONObject dato = new JSONObject();
            dato.put("correo", params[0]);
            dato.put("contrasena", MD5.hash((String) params[1]));
            Log.d(LOGTAG, "El usuario que se insertara es:" + dato.toString());
            return String.valueOf(dato);
        }

        @Override
        protected void onPostExecute(Boolean aBoolean) {
            delegate.processFinish(aBoolean);
        }
    }
}
