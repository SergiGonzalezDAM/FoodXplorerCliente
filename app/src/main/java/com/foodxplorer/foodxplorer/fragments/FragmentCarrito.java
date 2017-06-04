package com.foodxplorer.foodxplorer.fragments;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.foodxplorer.foodxplorer.MainActivity;
import com.foodxplorer.foodxplorer.R;
import com.foodxplorer.foodxplorer.helpers.RestManager;
import com.foodxplorer.foodxplorer.helpers.Settings;
import com.foodxplorer.foodxplorer.objetos.Direccion;
import com.foodxplorer.foodxplorer.objetos.LineasPedido;
import com.foodxplorer.foodxplorer.objetos.Pedidos;
import com.foodxplorer.foodxplorer.objetos.Producto;
import com.paypal.android.sdk.payments.PayPalPayment;
import com.paypal.android.sdk.payments.PayPalService;
import com.paypal.android.sdk.payments.PaymentActivity;
import com.paypal.android.sdk.payments.PaymentConfirmation;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import static com.foodxplorer.foodxplorer.helpers.Settings.LOGTAG;

public class FragmentCarrito extends Fragment implements AdapterView.OnClickListener, AdapterView.OnItemClickListener, AdapterView.OnItemSelectedListener {

    MainActivity tienda;
    JSONArray jsonarrayDireccions = new JSONArray();
    Spinner spinnerDirecciones;
    Pedidos pedido = new Pedidos();
    double precioAcumulado;
    private ArrayList<String> direccionsClient = new ArrayList<>();


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
        spinnerDirecciones = (Spinner) view.findViewById(R.id.spinnerDireccionesConfirmarPedido);
        spinnerDirecciones.setOnItemSelectedListener(this);
        //Al cargar el view, obtenemos las diferentes direcciones del usuario logueado.
        TareaObtenerDirecciones obtenerDirecciones = new TareaObtenerDirecciones();
        obtenerDirecciones.execute();

        //Rellenamos los datos del pedido, usando como origen el carrito.
        rellenarPantallaDePedido(view);

        Button boto = (Button) view.findViewById(R.id.botonPagar);
        boto.setOnClickListener(FragmentCarrito.this);

        return view;
    }

    /**
     * A partir del carrito, rellena el listview que muestra al usuario el contenido del carrito
     *
     * @param view el view en el que se realizara la insercion de datos
     */
    private void rellenarPantallaDePedido(View view) {
        List<String> lista = new ArrayList();
        ArrayList<Producto> productosenCarrito = (ArrayList) tienda.carrito.getProductosEnCarrito();
        ArrayList<Integer> cantidadesenCarrito = (ArrayList) tienda.carrito.getCantidades();
        if (productosenCarrito.size() == 0) {
            lista.add(getString(R.string.no_products_in_cart));
        }
        precioAcumulado = 0;
        //Calculamos el precio acumulado del pedido, para poder actulizar el total del mismo
        for (int i = 0; i < productosenCarrito.size(); i++) {
            double precioDescontado = (productosenCarrito.get(i).getPrecio() - (productosenCarrito.get(i).getPrecio() * productosenCarrito.get(i).getOfertaProducto()) / 100) * cantidadesenCarrito.get(i);

            String aux = cantidadesenCarrito.get(i) + "X " + productosenCarrito.get(i).getNombre() + "\n" + String.format("%.2f", precioDescontado) + "€";
            lista.add(aux);
            precioAcumulado = precioAcumulado + precioDescontado;
        }

        //Finalemnte mostramos el precio del pedido total
        TextView subTotal = (TextView) view.findViewById(R.id.txtLabelImporteTotal);
        subTotal.setText(String.format("%.2f", precioAcumulado) + "€");

        //Cargamos el list View con los diferentes prodcutos que conforman el pedido del carrito
        ListView listViewContenidoPedido = (ListView) view.findViewById(R.id.listViewConfPedido);
        ArrayAdapter<String> adaptador = new ArrayAdapter(view.getContext(), android.R.layout.simple_list_item_1, lista);
        listViewContenidoPedido.setAdapter(adaptador);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        System.out.println("carregat el view");
    }

    /**
     * Carga el Spinner que muestra las diferentes direccions del usuario.
     */
    public void carregarSpinnerDireccions() {
        try {
            //Si hay direcciones guardadas, solicitamos al usuario seleccionar una de ellas.
            if (direccionsClient.size() > 0) {
                direccionsClient.add(0, "Selecciona la direccion de entrega");
            }
            // La ultima opcion siempre sera añadir una opcion nueva
            direccionsClient.add("Nueva direccion de entrega");
            Spinner mySpinner = (Spinner) getView().findViewById(R.id.spinnerDireccionesConfirmarPedido);
            mySpinner.setAdapter(new ArrayAdapter<>(getView().getContext(),
                    android.R.layout.simple_spinner_dropdown_item,
                    direccionsClient));
        } catch (IndexOutOfBoundsException ex) {
            Log.e(Settings.LOGTAG, "Unexpected error while loading");
        }
    }

    @Override
    public void onClick(View view) {
        //Si el carrito no esta vacio
        if (this.tienda.carrito.getProductosEnCarrito().size() > 0) {
            int aux;
            try {
                aux = obtenerIdDireccion();
                if (aux < 0) {
                    Log.e(Settings.LOGTAG, "Selecciona una direccion de entrega.");
                    Toast.makeText(tienda, R.string.no_direction_Selected, Toast.LENGTH_LONG).show();

                } else if (aux == 0) {

                    final AlertDialog.Builder mBuilder = new AlertDialog.Builder(tienda);
                    final View mView = getLayoutInflater(null).inflate(R.layout.insertar_direccion_dialog, null);
                    mBuilder.setView(mView);
                    final AlertDialog dialog = mBuilder.create();
                    final EditText calle = (EditText) mView.findViewById(R.id.etCalleDireccion);
                    final EditText codPostal = (EditText) mView.findViewById(R.id.etCodigoPostalInsertarDireccion);
                    final EditText psio = (EditText) mView.findViewById(R.id.etPiso);
                    final EditText poblacion = (EditText) mView.findViewById(R.id.etPoblacionInsertarDireccion);

                    mView.findViewById(R.id.btInsertarDireccion).setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            dialog.dismiss();
                            Direccion direccion = new Direccion();
                            direccion.setCalle(calle.getText().toString());
                            direccion.setCodPostal(codPostal.getText().toString());
                            direccion.setPiso(psio.getText().toString());
                            direccion.setPoblacion(poblacion.getText().toString());
                            TareaInsertarDireccion tarea = new TareaInsertarDireccion();
                            tarea.execute(direccion);
                        }
                    });
                    dialog.show();
                } else {
                    TareaInsertarPedido tarea = new TareaInsertarPedido();
                    tarea.execute();
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }

        } else {
            Toast.makeText(tienda, R.string.empty_Cart, Toast.LENGTH_LONG).show();

        }
    }

    /**
     * Al finalizar la inserción de un pedido, procesa su pago.
     *
     * @param numeroPedido
     */
    public void pedidoInsertado(int numeroPedido) {
        pedido.setIdPedido(numeroPedido);
        PayPalPayment payment = new PayPalPayment(new BigDecimal(precioAcumulado), "EUR", "Pago del pedido:" + numeroPedido,
                PayPalPayment.PAYMENT_INTENT_SALE);

        Intent intent = new Intent(tienda, PaymentActivity.class);
        // send the same configuration for restart resiliency
        intent.putExtra(PayPalService.EXTRA_PAYPAL_CONFIGURATION, MainActivity.config);
        intent.putExtra(PaymentActivity.EXTRA_PAYMENT, payment);
        startActivityForResult(intent, 0);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == Activity.RESULT_OK) {
            PaymentConfirmation confirm = data.getParcelableExtra(PaymentActivity.EXTRA_RESULT_CONFIRMATION);
            if (confirm != null) {
                try {
                    Log.i("paymentExample", confirm.toJSONObject().toString(4));

                    // TODO: send 'confirm' to your server for verification.
                    // see https://developer.paypal.com/webapps/developer/docs/integration/mobile/verify-mobile-payment/
                    // for more details.

                    Toast.makeText(tienda, "Pedido " + pedido.getIdPedido() + " realizado.", Toast.LENGTH_LONG).show();
                    Log.d(Settings.LOGTAG, "Pedido " + pedido.getIdPedido() + " realizado.");
                    this.tienda.carrito.clearCarrito();
                    this.tienda.actualizarIconoCarrito();
                    rellenarPantallaDePedido(getView());

                } catch (JSONException e) {
                    Log.e(LOGTAG, "an extremely unlikely failure occurred: ", e);
                }
            }
        } else if (resultCode == Activity.RESULT_CANCELED) {
            Log.i(LOGTAG, "The user canceled.");
        } else if (resultCode == PaymentActivity.RESULT_EXTRAS_INVALID) {
            Log.i(LOGTAG, "An invalid Payment or PayPalConfiguration was submitted. Please see the docs.");
        }
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

    /**
     * Retorna un id de direccio
     *
     * @return 0 if needs to add a new direction, -1 if no direction selected.
     * @throws JSONException
     */
    int obtenerIdDireccion() throws JSONException {
        int result;
        if (spinnerDirecciones.getSelectedItem().toString().equals(getString(R.string.new_direction)) || spinnerDirecciones.getSelectedItem().toString().equals("Selecciona la direccion de entrega")) {
            if (spinnerDirecciones.getSelectedItem().toString().equals(getString(R.string.new_direction))) {
                System.err.println("Nueva direccion");
                result = 0;
            } else {
                System.err.println("Selecciona una direccion de entrega.");
                result = -1;
            }
        } else {
            System.err.println("Ya tenemos direccion");
            JSONObject jsonobject = jsonarrayDireccions.getJSONObject(spinnerDirecciones.getSelectedItemPosition() - 1);
            result = jsonobject.getInt("idDireccion");
        }
        return result;
    }

    /**
     * Obtiene las diferentes direcciones y las carga en el spinner.
     */
    private class TareaObtenerDirecciones extends AsyncTask<Object, Void, Boolean> {

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

    /**
     * Tarea que se encarga de insertar el pedido y las lineas que lo componen a la BBDD.
     */
    private class TareaInsertarPedido extends AsyncTask<Object, Void, Integer> {


        @Override
        protected Integer doInBackground(Object... params) {
            int numeroPedido = 0;
            OutputStreamWriter osw;
            BufferedInputStream isr;
            RestManager restManager;
            try {
                String aux = (Settings.DIRECCIO_SERVIDOR + Settings.PATH + "pedido/insertar/");
                DateFormat df = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
                Date today = Calendar.getInstance().getTime();
                String fechaPedido = df.format(today);
                System.out.println("El id de direccion es=:" + pedido.getIdDireccion());
                if (pedido.getIdDireccion() == -3) {
                    pedido.setIdDireccion(obtenerIdDireccion());
                }
                if (pedido.getIdDireccion() <= 0) {
                    Log.e(Settings.LOGTAG, "Error al obtenir la direccio de l'spinner");
                } else {
                    if (pedido.getIdDireccion() <= 0) {
                        Log.d(Settings.LOGTAG, "Error al obtenir la direccio de l'spinner");
                        return -1;
                    }
                    pedido.setFechaPedido(fechaPedido);
                    //aux = aux + "/" + user.getUsername() + "/" + user.getPassword() + "/";
                    restManager = new RestManager(aux);
                    restManager.setRequestMethod(RestManager.POST);
                    osw = restManager.getOutputStreamWriter();
                    osw.write(getStringJSON(pedido));
                    osw.flush();
                    BufferedReader bfr = restManager.getBufferedReader();
                    numeroPedido = Integer.valueOf(bfr.readLine());
                    System.err.println(restManager.getResponseMessage());
                    System.err.println(restManager.getResponseCode());


                    ArrayList<LineasPedido> lineas = new ArrayList<>();
                    ArrayList<Producto> productos = (ArrayList) tienda.carrito.getProductosEnCarrito();
                    ArrayList<Integer> cantidades = (ArrayList) tienda.carrito.getCantidades();

                    for (int i = 0; i < productos.size(); i++) {
                        Producto poducto = productos.get(i);
                        lineas.add(new LineasPedido(numeroPedido, poducto.getIdProducto(), cantidades.get(i), poducto.getPrecio(), poducto.getIva()));
                    }
                    for (LineasPedido linea : lineas) {
                        RestManager restManager2;
                        String aux2 = (Settings.DIRECCIO_SERVIDOR + Settings.PATH + "lineasPedido/insertar/");
                        restManager2 = new RestManager(aux2);
                        restManager2.setRequestMethod(RestManager.POST);
                        osw = restManager2.getOutputStreamWriter();
                        String linea2 = getStringJSON(linea);
                        osw.write(linea2);
                        osw.flush();
                        BufferedReader bfr2 = restManager2.getBufferedReader();
                        if (!restManager2.getResponseMessage().equals("OK")) {
                            return -2;
                        }
                    }
                }
            } catch (java.net.ProtocolException ex) {
                Log.e(Settings.LOGTAG, "Error de protocol: " + ex);
                numeroPedido = 0;
            } catch (java.io.FileNotFoundException | java.net.MalformedURLException ex) {
                Log.e(Settings.LOGTAG, "Error de ruta d'acces: " + ex);
                numeroPedido = 0;
            } catch (java.net.SocketTimeoutException ex) {
                Log.e(Settings.LOGTAG, "Temps d'espera esgotat al iniciar la conexio amb la BBDD extena: " + ex);
                numeroPedido = 0;
            } catch (java.io.IOException ex) {
                Log.e(Settings.LOGTAG, "Undefined error: " + ex);
                numeroPedido = 0;
            } catch (JSONException e) {
                Log.e(Settings.LOGTAG, "Error de manipulacio de l'objecte JSON: " + Arrays.toString(e.getStackTrace()));
            }

            return numeroPedido;
        }

        @Override
        protected void onPostExecute(Integer numeroPedido) {
            if (pedido.getIdDireccion() < 0) {
                Toast.makeText(tienda, "Selecciona una direccion de entrega.", Toast.LENGTH_LONG).show();
            } else {
                if (numeroPedido == 0) {
                    Toast.makeText(tienda, "Error al procesar el pedido.", Toast.LENGTH_LONG).show();
                } else {
                    pedidoInsertado(numeroPedido);
                }
            }
        }

        /**
         * Trasnforma un objeto Pedido en un String Json  util para nuestro rest
         *
         * @param pedido
         * @return
         * @throws JSONException
         * @throws UnsupportedEncodingException
         */
        private String getStringJSON(Pedidos pedido) throws JSONException, UnsupportedEncodingException {
            JSONObject dato = new JSONObject();
            dato.put("idDireccion", pedido.getIdDireccion());
            dato.put("idEstado", 1);
            dato.put("fechaSalida", pedido.getFechaPedido());
            if (!tienda.carrito.getUsuarioLogueado().equals("")) {
                dato.put("correo", tienda.carrito.getUsuarioLogueado());
            }
            Log.d(LOGTAG, "El pedido a insertar es:" + dato.toString());
            return String.valueOf(dato);
        }

        /**
         * Trasnforma un objeto LineaPedido en un String Json  util para nuestro rest
         *
         * @param lineas
         * @return
         * @throws JSONException
         * @throws UnsupportedEncodingException
         */
        private String getStringJSON(LineasPedido lineas) throws JSONException, UnsupportedEncodingException {
            JSONObject dato = new JSONObject();
            dato.put("idPedido", lineas.getIdPedido());
            dato.put("idProducto", lineas.getIdProducto());
            dato.put("cantidad", lineas.getCantidad());
            dato.put("precio", lineas.getPrecio());
            dato.put("iva", lineas.getIva());
            return String.valueOf(dato);
        }


    }

    /**
     * Inserta una direccion a la BBDD
     */
    private class TareaInsertarDireccion extends AsyncTask<Object, Void, Boolean> {


        @Override
        protected Boolean doInBackground(Object... params) {
            boolean result = true;

            OutputStreamWriter osw;
            RestManager restManager;
            try {
                String aux = (Settings.DIRECCIO_SERVIDOR + Settings.PATH + "direccion/insertar/");
                restManager = new RestManager(aux);
                restManager.setRequestMethod(RestManager.POST);
                osw = restManager.getOutputStreamWriter();
                osw.write(getStringJSON((Direccion) params[0]));
                osw.flush();
                BufferedReader bfr = restManager.getBufferedReader();
                pedido.setIdDireccion(Integer.valueOf(bfr.readLine()));
                System.err.println(restManager.getResponseMessage());
                System.err.println(restManager.getResponseCode());
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
                Log.e(Settings.LOGTAG, "Error de manipulacio de l'objecte JSON: " + Arrays.toString(e.getStackTrace()));
            }
            return result;
        }

        /**
         * Trasnforma un objeto Direccion en un String Json  util para nuestro rest
         * @param direccion
         * @return
         * @throws JSONException
         * @throws UnsupportedEncodingException
         */
        private String getStringJSON(Direccion direccion) throws JSONException, UnsupportedEncodingException {
            JSONObject dato = new JSONObject();
            dato.put("calle", direccion.getCalle());
            dato.put("piso", direccion.getPiso());
            dato.put("poblacion", direccion.getPoblacion());
            dato.put("codPostal", direccion.getCodPostal());
            if (tienda.carrito.isUserLogedIn()) {
                dato.put("idUsuario", tienda.carrito.getIdUsuarioLogueado());
            }
            Log.d(LOGTAG, "La direccion a insertar es:" + dato.toString());
            return String.valueOf(dato);
        }

        @Override
        protected void onPostExecute(Boolean result) {
            if (result) {
                TareaInsertarPedido tarea = new TareaInsertarPedido();
                tarea.execute();
            }
        }
    }
}
