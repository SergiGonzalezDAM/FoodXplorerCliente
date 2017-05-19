package com.foodxplorer.foodxplorer;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.widget.DrawerLayout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.RequestParams;

import org.json.JSONObject;

import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;

public class FragmentLogin extends Fragment implements View.OnClickListener, FragmentLogin.AsyncResponse {
    private DrawerLayout drawerLayout;
    private NavigationView navView;
    private MainActivity tienda;

    private EditText etUsuario;
    private EditText etPassword;
    public interface AsyncResponse {
        void processFinish(boolean response);
    }


    @Override
    public void processFinish(boolean response) {
        if(response){
            Log.e(Settings.LOGTAG, "Login ok");
        }
        else{
            Log.e(Settings.LOGTAG, "Login fail");
        }
    }




    public FragmentLogin() {
        // Required empty public constructor
    }

    public FragmentLogin(MainActivity tienda) {
        this.tienda = tienda;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_login, container, false);
        drawerLayout = (DrawerLayout) view.findViewById(R.id.drawer_layout);
        Button button = (Button) view.findViewById(R.id.btnLogin);
        button.setOnClickListener(this);

        etUsuario = (EditText) view.findViewById(R.id.editTextLoginUsuario);
        etPassword = (EditText) view.findViewById(R.id.editTextPasswordLogin);


        return view;
    }

    @Override
    public void onClick(View view) {
        if (R.id.btnRegistrar == view.getId()) {
            Fragment fragment = new FragmentRegistro();
            getFragmentManager().beginTransaction().replace(R.id.content_frame, fragment).commit();
            // drawerLayout.closeDrawers();
        } else {
           comprobarlogin();
        }
    }

    public boolean comprobarlogin() {
        return true;
    }
}

class TareaWScomprobarLogin extends AsyncTask<Object, Integer, Boolean> {


    @Override
    protected Boolean doInBackground(Object... params) {
        boolean insertadoEnDBexterna = true;
        OutputStreamWriter osw;
        try {
            String aux=(Settings.DIRECCIO_SERVIDOR+"/ServicioWeb/webresources/generic/insertarPosicion");
            URL url = new URL(aux);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("PUT");
            conn.setReadTimeout(5000);/*milliseconds*/
            conn.setConnectTimeout(5000);
            conn.setRequestProperty("Content-Type", "application/json");
            osw = new OutputStreamWriter(conn.getOutputStream());
            JSONObject jsonUser = new JSONObject();
            jsonUser.put("usuario")
            osw.write(getStringJSON(params));
            osw.flush();
            osw.close();
            System.err.println(conn.getResponseMessage());
        } catch (java.io.IOException ex) {
            Log.e(Settings.LOGTAG, "Temps d'espera esgotat al iniciar la conexio amb la BBDD extena"+ex);
            insertadoEnDBexterna = false;
        } catch (org.json.JSONException ex) {
            Log.e(LOGTAG, "Error en la transformacio de l'objecte JSON: " + ex);
            insertadoEnDBexterna = false;
        }
        return insertadoEnDBexterna;
    }
}
}

