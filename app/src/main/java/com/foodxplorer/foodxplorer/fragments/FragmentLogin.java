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
import android.widget.TextView;
import android.widget.Toast;

import com.foodxplorer.foodxplorer.FragmentRegistro;
import com.foodxplorer.foodxplorer.MainActivity;
import com.foodxplorer.foodxplorer.R;
import com.foodxplorer.foodxplorer.helpers.*;




import java.io.InputStreamReader;




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
            this.tienda.CurrentState.setUsuarioLogueado(etUsuario.getText().toString());
            this.tienda.goTo(MainActivity.PROMOCIONES);
        } else {
            Log.e(Settings.LOGTAG, "Login fail");
            Toast.makeText(tienda, "Login fail", Toast.LENGTH_LONG);
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
            Fragment fragment = new FragmentRegistro(this.tienda);
            getFragmentManager().beginTransaction().replace(R.id.content_frame, fragment).commit();
        } else {
            TareaWScomprobarLogin tarea = new TareaWScomprobarLogin();
            tarea.delegate = this;
            tarea.execute(new Usuario(etUsuario.getText().toString(), etPassword.getText().toString()));
        }
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
            String aux = (Settings.DIRECCIO_SERVIDOR + Settings.PATH+"loguearUsuario");
            aux = aux + "/" + user.getUsername() + "/" + user.getPassword() + "/";
            RestManager restManager= new RestManager(aux);
            osw = restManager.getInputStream();
            int data = osw.read();
            String res = "";
            while (data != -1) {
                char current = (char) data;
                data = osw.read();
                res = res + current;
            }
            if(!res.equals("true")){
                Log.e(Settings.LOGTAG, "Error de login para user: " +  user.getUsername() );
                result = false;
            }
            osw.close();
            //System.err.println(conn.getResponseMessage());
        } catch (java.net.ProtocolException ex) {
            Log.e(Settings.LOGTAG, "Error de protocol: " + ex);
            result = false;
        } catch (java.io.FileNotFoundException | java.net.MalformedURLException ex) {
            Log.e(Settings.LOGTAG, "Error de ruta d'acces: " + ex);
            result = false;
        }
        catch (java.net.SocketTimeoutException ex){
            Log.e(Settings.LOGTAG, "Temps d'espera esgotat al iniciar la conexio amb la BBDD extena: " + ex);
            result = false;
        }
        catch (java.io.IOException ex) {
            Log.e(Settings.LOGTAG, "Undefined error: " + ex);
            result = false;
        }
        return result;
    }
    @Override
    protected void onPostExecute(Boolean result) {
        delegate.processFinish(result);
    }
}

class Usuario {

    private String username;
    private String password;

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


