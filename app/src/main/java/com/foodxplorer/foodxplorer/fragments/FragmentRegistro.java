package com.foodxplorer.foodxplorer.fragments;

import android.content.Context;
import android.os.AsyncTask;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.v4.widget.DrawerLayout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;

import com.foodxplorer.foodxplorer.MainActivity;
import com.foodxplorer.foodxplorer.R;
import com.foodxplorer.foodxplorer.helpers.Settings;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;

import static com.foodxplorer.foodxplorer.helpers.Settings.LOGTAG;


public class FragmentRegistro extends Fragment implements View.OnClickListener {
    private DrawerLayout drawerLayout;
    private NavigationView navView;
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


    @Override
    public void onClick(View view) {
        if (R.id.btnRegistrarRegistro == view.getId()) {
            InputMethodManager inputManager = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
            inputManager.hideSoftInputFromWindow((null == getActivity().getCurrentFocus()) ? null : getActivity().getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
            TareaWSRegistrarUsuario tareaRegistrar = new TareaWSRegistrarUsuario();
            tareaRegistrar.execute(correo.getText(), contrasena.getText());
        }
    }

    class TareaWSRegistrarUsuario extends AsyncTask<Object, Integer, Boolean> {


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

        private String getStringJSON(Object... params) throws JSONException, UnsupportedEncodingException {
            JSONObject dato = new JSONObject();
            dato.put("correo", params[0]);
            dato.put("contrasena", params[1]);
            Log.d(LOGTAG, "El usuario que se insertara es:" + dato.toString());
            return String.valueOf(dato);
        }

        @Override
        protected void onPostExecute(Boolean aBoolean) {
                     if (aBoolean) {
                Log.e(LOGTAG, "Registro ok");
                tienda.goTo(MainActivity.PROMOCIONES);
            } else {
                Log.e(LOGTAG, "Registro fail");
            }
        }
    }
}
