package com.foodxplorer.foodxplorer.fragments;

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

import com.foodxplorer.foodxplorer.FragmentRegistro;
import com.foodxplorer.foodxplorer.MainActivity;
import com.foodxplorer.foodxplorer.R;
import com.foodxplorer.foodxplorer.helpers.Settings;

import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;


interface AsyncResponse {
    void processFinish(boolean response);
}

public class FragmentLogin extends Fragment implements View.OnClickListener, AsyncResponse {
    private DrawerLayout drawerLayout;
    private NavigationView navView;
    private MainActivity tienda;

    private EditText etUsuario;
    private EditText etPassword;


    @Override
    public void processFinish(boolean response) {
        if (response) {
            Log.e(Settings.LOGTAG, "Login ok");
        } else {
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
        Button btnLogin = (Button) view.findViewById(R.id.btnLogin);
        btnLogin.setOnClickListener(this);
        Button btnRegister = (Button) view.findViewById(R.id.btnRegistrar);
        btnRegister.setOnClickListener(this);

        etUsuario = (EditText) view.findViewById(R.id.editTextLoginUsuario);
        etPassword = (EditText) view.findViewById(R.id.editTextPasswordLogin);


        return view;
    }

    @Override
    public void onClick(View view) {
        if (R.id.btnRegistrar == view.getId()) {
            System.out.println("Joderrrr");
            Fragment fragment = new FragmentRegistro();
            getFragmentManager().beginTransaction().replace(R.id.content_frame, fragment).commit();
            // drawerLayout.closeDrawers();
        } else {
            TareaWScomprobarLogin tarea = new TareaWScomprobarLogin();
            tarea.delegate = this;
            tarea.execute(new Usuario(etUsuario.getText().toString(), etPassword.getText().toString()));
        }
    }

    public boolean comprobarlogin() {
        return true;
    }
}

class TareaWScomprobarLogin extends AsyncTask<Object, Void, Boolean> {

    public AsyncResponse delegate = null;

    @Override
    protected Boolean doInBackground(Object... params) {
        boolean result = true;
        InputStreamReader osw;
        try {
            Usuario user= (Usuario) params[0];
            String aux = (Settings.DIRECCIO_SERVIDOR + "ServcioFoodXPlorer/webresources/generic/loguearUsuario");
            aux = aux + "/" + user.username + "/" + user.password + "/";
            System.out.println(aux);
            URL url = new URL(aux);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            //conn.setReadTimeout(5000);/*milliseconds*/
            //conn.setConnectTimeout(5000);
            conn.setRequestProperty("Content-Type", "application/json");
            osw = new InputStreamReader(conn.getInputStream());
            int data = osw.read();
            String res = "";
            while (data != -1) {
                char current = (char) data;
                data = osw.read();
                System.out.print(current);
                res = res + current;
            }
            if(res.equals("false")){
                Log.e(Settings.LOGTAG, "Error de login para user: " +  params[0].toString() );
                result = false;
            }
            osw.close();
            //System.err.println(conn.getResponseMessage());
        } catch (java.net.ProtocolException ex) {
            Log.e(Settings.LOGTAG, "Error de protocol: " + ex);
            result = false;
        } catch (java.io.FileNotFoundException ex) {
            Log.e(Settings.LOGTAG, "Error de ruta d'acces: " + ex);
            result = false;
        } catch (java.io.IOException ex) {
            Log.e(Settings.LOGTAG, "Temps d'espera esgotat al iniciar la conexio amb la BBDD extena: " + ex);
            result = false;
        }

        //   delegate.processFinish(insertadoEnDBexterna);
        return result;
    }
    @Override
    protected void onPostExecute(Boolean result) {
        // si usuari es valid
        if (result) {
            System.out.println("Login correcte.");
        } else {
            System.out.println("Login erroni.");
        }
    }
}

class Usuario {

    String username;
    String password;

    public Usuario(String username, String password) {
        this.username = username;
        this.password = password;
    }

    public String getUsername() {
        return username;
    }


    public String getPassword() {
        return password;
    }

}


