package com.foodxplorer.foodxplorer.fragments;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.widget.DrawerLayout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;


import com.foodxplorer.foodxplorer.MainActivity;
import com.foodxplorer.foodxplorer.R;
import com.foodxplorer.foodxplorer.helpers.AsyncResponse;
import com.foodxplorer.foodxplorer.helpers.Settings;
import com.foodxplorer.foodxplorer.helpers.TareaComprobarLogin;
import com.foodxplorer.foodxplorer.objetos.Usuario;



public class FragmentLogin extends Fragment implements View.OnClickListener, AsyncResponse {
    private DrawerLayout drawerLayout;
    private NavigationView navView;
    private MainActivity tienda;

    private EditText etUsuario;
    private EditText etPassword;



    @Override
    public void processFinish(boolean response) {
        if (response) {
            Log.d(Settings.LOGTAG, "Login ok");
            this.tienda.login(etUsuario.getText().toString());
            Toast.makeText(tienda, "Hola de nuevo, "+ etUsuario.getText(), Toast.LENGTH_LONG).show();
            this.tienda.goTo(MainActivity.PROMOCIONES);
        } else {
            Log.e(Settings.LOGTAG, "Login fail");
            Toast.makeText(tienda, R.string.LOGIN_FAIL, Toast.LENGTH_LONG).show();
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
        etUsuario = (EditText) view.findViewById(R.id.editTextNumeroSeguimiento);
        etPassword = (EditText) view.findViewById(R.id.editTextPasswordLogin);

        //La aplicacion no permite que se llegue a este fragment si se esta logueado, pero este codigo
        //se asegura que efectivamente no se produzca un segundo login.
        if(!"".equals(tienda.carrito.getUsuarioLogueado())){
            this.tienda.goTo(MainActivity.PROMOCIONES);
        }

        return view;
    }

    @Override
    public void onClick(View view) {
        if (R.id.btnRegistrar == view.getId()) {
            Fragment fragment = new FragmentRegistro(this.tienda);
            getFragmentManager().beginTransaction().replace(R.id.content_frame, fragment).commit();
        } else {
            InputMethodManager inputManager = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
            inputManager.hideSoftInputFromWindow((null == getActivity().getCurrentFocus()) ? null : getActivity().getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);

            TareaComprobarLogin tarea = new TareaComprobarLogin();
            tarea.delegate = this;
            tarea.execute(new Usuario(etUsuario.getText().toString(), etPassword.getText().toString()));
        }
    }
}



