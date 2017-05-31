package com.foodxplorer.foodxplorer.fragments;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.foodxplorer.foodxplorer.MainActivity;
import com.foodxplorer.foodxplorer.R;
import com.foodxplorer.foodxplorer.helpers.AsyncResponse;
import com.foodxplorer.foodxplorer.helpers.RestManager;
import com.foodxplorer.foodxplorer.helpers.Settings;
import com.foodxplorer.foodxplorer.objetos.Pedidos;
import com.foodxplorer.foodxplorer.objetos.Producto;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import static com.foodxplorer.foodxplorer.helpers.Settings.LOGTAG;

public class FragmentCarrito extends Fragment implements AdapterView.OnClickListener, AdapterView.OnItemClickListener, AdapterView.OnItemSelectedListener {

    MainActivity tienda;
    private ArrayList<String> direccionsClient = new ArrayList<>();
    JSONArray jsonarrayDireccions = new JSONArray();
    Spinner mySpinner;
    TextView direccio;
    Pedidos pedido;

    public FragmentCarrito(MainActivity tienda) {

        this.tienda = tienda;
    }

    public FragmentCarrito() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_confirmacion_pedido, container, false);
        mySpinner = (Spinner) view.findViewById(R.id.spinnerDireccionesConfirmarPedido);
        mySpinner.setOnItemSelectedListener(this);
        List<String> lista = new ArrayList();
        ArrayList<Producto> productosenCarrito = (ArrayList) tienda.carrito.getProductosEnCarrito();
        ArrayList<Integer> cantidadesenCarrito = (ArrayList) tienda.carrito.getCantidades();

            TareaObtenerDirecciones obtenerDirecciones = new TareaObtenerDirecciones();
            obtenerDirecciones.execute();

        if (productosenCarrito.size() == 0) {
            lista.add("No tienes ningun producto en el carrito aun.");
        }
        double precioTotal = 0;
        for (int i = 0; i < productosenCarrito.size(); i++) {
            lista.add(productosenCarrito.get(i).getNombre() + " cantidad: " + cantidadesenCarrito.get(i) + " Importe: " + String.format("%.2f", productosenCarrito.get(i).getPrecio() * cantidadesenCarrito.get(i)));
            precioTotal = precioTotal + productosenCarrito.get(i).getPrecio() * cantidadesenCarrito.get(i);
        }
        TextView subTotal = (TextView) view.findViewById(R.id.txtLabelImporteTotal);
        subTotal.setText(String.format("%.2f", precioTotal) + "€");
        ListView listView = (ListView) view.findViewById(R.id.listViewConfPedido);
        ArrayAdapter<String> adaptador = new ArrayAdapter(view.getContext(), android.R.layout.simple_list_item_1, lista);
        listView.setAdapter(adaptador);
        Button boto = (Button) view.findViewById(R.id.botonPagar);
        boto.setOnClickListener(FragmentCarrito.this);

        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        System.out.println("carregat el view");
    }

    public void carregarSpinnerDireccions() {
        try {
            if (direccionsClient.size() > 0) {
                direccionsClient.add(0, "Selecciona la direccion de entrega");
            }
                direccionsClient.add("Nueva direccion de entrega");
            Spinner mySpinner = (Spinner) getView().findViewById(R.id.spinnerDireccionesConfirmarPedido);
            mySpinner.setAdapter(new ArrayAdapter<>(getView().getContext(),
                    android.R.layout.simple_spinner_dropdown_item,
                    direccionsClient));
        } catch (IndexOutOfBoundsException ex) {
            Log.e(Settings.LOGTAG, "Unexpected error while loading");
        }
        //Boolea de control per determinar si s'ha rebut totes les dades extenes
    }

    @Override
    public void onClick(View view) {
        TareaInsertarPedido tarea = new TareaInsertarPedido();
        tarea.execute();

    }

    public void pedidoInsertado(int numeroPedido) {
        Toast.makeText(tienda, "Pedido " + numeroPedido + " realizado.", Toast.LENGTH_LONG).show();
        Log.d(Settings.LOGTAG, "Pedido " + numeroPedido + " realizado.");
    }


    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
        System.err.println("polsada la direccio" + i);
    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {

    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

    }

    class TareaObtenerDirecciones extends AsyncTask<Object, Void, Boolean> {

        AsyncResponse delegate = null;

        @Override
        protected Boolean doInBackground(Object... params) {
            boolean result = true;
            BufferedReader osw;
            RestManager restManager;
            direccionsClient.clear();
            if (!tienda.carrito.getUsuarioLogueado().equals("")) {
                try {
                    String aux = (Settings.DIRECCIO_SERVIDOR + Settings.PATH + "direccion/obtenerPorCorreo/" + tienda.carrito.getUsuarioLogueado());
                    restManager = new RestManager(aux);
                    osw = restManager.getBufferedReader();
                    jsonarrayDireccions = new JSONArray(osw.readLine());
                    for (int i = 0; i < jsonarrayDireccions.length(); i++) {
                        JSONObject jsonobject = jsonarrayDireccions.getJSONObject(i);
                        System.out.println(jsonobject.toString());
                        direccionsClient.add(jsonobject.optString("calle") + " " + jsonobject.optString("piso") + " " + jsonobject.optString("poblacion"));
                    }
                    osw.close();
                    restManager.disconnect();
                } catch (java.net.ProtocolException ex) {
                    Log.e(Settings.LOGTAG, "Error de protocol: " + ex);
                    result = false;
                } catch (java.io.FileNotFoundException | java.net.MalformedURLException ex) {
                    Log.e(Settings.LOGTAG, "Error de ruta d'acces: " + ex);
                    result = false;
                } catch (java.net.SocketTimeoutException ex) {
                    Log.e(Settings.LOGTAG, "Temps d'espera esgotat al iniciar la conexio amb la BBDD extena: " + ex);
                    result = false;
                } catch (java.io.IOException ex) {
                    Log.e(Settings.LOGTAG, "Undefined error: " + ex);
                    result = false;
                } catch (JSONException e) {
                    Log.e(Settings.LOGTAG, "Json error: " + e);
                }
            }
            return result;
        }

        @Override
        protected void onPostExecute(Boolean result) {
            if (result) {
                carregarSpinnerDireccions();
            } else {
                Log.e(Settings.LOGTAG, "Error al carregar les dades de les direccions");
            }


        }
    }

    class TareaInsertarPedido extends AsyncTask<Object, Void, Boolean> {


        @Override
        protected Boolean doInBackground(Object... params) {
            boolean result = true;
            OutputStreamWriter osw;
            RestManager restManager;
            try {

                String aux = (Settings.DIRECCIO_SERVIDOR + Settings.PATH + "pedido/insertar/");
                pedido = new Pedidos();

                DateFormat df = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
                Date today = Calendar.getInstance().getTime();
                String fechaPedido = df.format(today);
                pedido.setIdDireccion(obtenerIdDireccion());
                if (pedido.getIdDireccion() <= 0) {
                    return false;
                }
                pedido.setFechaPedido(fechaPedido);
                //aux = aux + "/" + user.getUsername() + "/" + user.getPassword() + "/";
                restManager = new RestManager(aux);
                restManager.setRequestMethod(RestManager.POST);
                osw = restManager.getOutputStreamWriter();
                osw.write(getStringJSON(pedido));

                osw.close();
                restManager.disconnect();
            } catch (java.net.ProtocolException ex) {
                Log.e(Settings.LOGTAG, "Error de protocol: " + ex);
                result = false;
            } catch (java.io.FileNotFoundException | java.net.MalformedURLException ex) {
                Log.e(Settings.LOGTAG, "Error de ruta d'acces: " + ex);
                result = false;
            } catch (java.net.SocketTimeoutException ex) {
                Log.e(Settings.LOGTAG, "Temps d'espera esgotat al iniciar la conexio amb la BBDD extena: " + ex);
                result = false;
            } catch (java.io.IOException ex) {
                Log.e(Settings.LOGTAG, "Undefined error: " + ex);
                result = false;
            } catch (JSONException e) {
                Log.e(Settings.LOGTAG, "Error de manipulacio de l'objecte JSON: " + e.getStackTrace());
            }
            return result;
        }

        private String getStringJSON(Pedidos pedido) throws JSONException, UnsupportedEncodingException {
            JSONObject dato = new JSONObject();
            System.err.println("Primer OK");
            dato.put("idDireccion", pedido.getIdDireccion());
            System.err.println("Segundo OK");
            dato.put("estado", 1);
            System.err.println("Tercer OK");
            dato.put("fechaPedido", pedido.getFechaPedido());
            System.err.println("Cuarto OK");
            if (tienda.carrito.getUsuarioLogueado() != "") {
                dato.put("correo", tienda.carrito.getUsuarioLogueado());
            }
            Log.d(LOGTAG, "El pedido a insertar es:" + dato.toString());
            return String.valueOf(dato);
        }


        public int obtenerIdDireccion() throws JSONException {
            int result = 0;
            if (mySpinner.getSelectedItem().toString().equals("Nueva direccion de entrega") || mySpinner.getSelectedItem().toString().equals("Selecciona la direccion de entrega")) {
                if (mySpinner.getSelectedItem().toString().equals("Nueva direccion de entrega")) {
                    System.err.println("Nueva direccion");
                } else {
                    System.err.println("Selecciona una direccion de entrega.");
                    result = -1;
                }
            } else {
                System.err.println("Ya tenemos direccion");
                JSONObject jsonobject = jsonarrayDireccions.getJSONObject(mySpinner.getSelectedItemPosition() - 1);
                result = jsonobject.getInt("idDireccion");

            }
            return result;
        }

        @Override
        protected void onPostExecute(Boolean result) {
            if (pedido.getIdDireccion() <= 0) {
                if (pedido.getIdDireccion() == -1) {
                    Toast.makeText(tienda, "Selecciona una direccion de entrega.", Toast.LENGTH_LONG).show();
                } else {
                    Toast.makeText(tienda, "Nueva direccion de entrega.", Toast.LENGTH_LONG).show();
                }
            }

            if (result) {
                pedidoInsertado(123456);
            }
        }
    }
}
