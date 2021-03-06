package com.foodxplorer.foodxplorer;


import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.foodxplorer.foodxplorer.fragments.FragmentCarrito;
import com.foodxplorer.foodxplorer.fragments.FragmentLogin;
import com.foodxplorer.foodxplorer.fragments.FragmentPedidos;
import com.foodxplorer.foodxplorer.fragments.FragmentProductos;
import com.foodxplorer.foodxplorer.fragments.FragmentPromociones;
import com.foodxplorer.foodxplorer.fragments.FragmentRegistro;
import com.foodxplorer.foodxplorer.fragments.FragmentSeguimientoPedido;
import com.foodxplorer.foodxplorer.helpers.Settings;
import com.foodxplorer.foodxplorer.objetos.Carrito;
import com.foodxplorer.foodxplorer.objetos.Producto;
import com.paypal.android.sdk.payments.PayPalConfiguration;
import com.paypal.android.sdk.payments.PayPalService;


public class MainActivity extends AppCompatActivity implements FragmentPromociones.OnAddToCart, FragmentProductos.OnAddToCart{


    public static final String PROMOCIONES = "Promociones";
    public static final String SEGUIMIENTO = "Seguimiento";
    public static final String PEDIDOS = "Pedidos";
    public static final String PRODUCTOS = "Productos";
    public static final String LOGIN = "Login";
    public static final String MIS_PEDIDOS = "Mis Pedidos";
    public static final String REGISTRO = "Registro";
    public static final String CARRITO = "Carrito";
    public static final String LOGOUT = "Logout";
    public static final int  LOGIN_ID = 9865556;
    public static final int LOGOUT_ID = 9568632;
    public static final int MIS_PEDIDOS_ID = 965889;
    /**
     * Objeto de configuracion de paypal
     */
    public static PayPalConfiguration config = new PayPalConfiguration()

            // Start with mock environment.  When ready, switch to sandbox (ENVIRONMENT_SANDBOX)
            // or live (ENVIRONMENT_PRODUCTION)
            .environment(PayPalConfiguration.ENVIRONMENT_NO_NETWORK)
            .clientId("<YOUR_CLIENT_ID>")
            .languageOrLocale("es");
    public Carrito carrito;
    private DrawerLayout drawerLayout;
    private NavigationView navView;

    @Override
    public void onDestroy() {
        stopService(new Intent(this, PayPalService.class));
        super.onDestroy();
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        //Paypal Intent. Requerido por la API de Paypal
        Intent intent = new Intent(this, PayPalService.class);
        intent.putExtra(PayPalService.EXTRA_PAYPAL_CONFIGURATION, config);
        startService(intent);


        Toolbar appbar = (Toolbar) findViewById(R.id.appbar);
        setSupportActionBar(appbar);

        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_nav_menu);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);


        Fragment fragment = new FragmentPromociones(this);
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.content_frame, fragment)
                .commit();
        drawerLayout.closeDrawers();

        carrito = new Carrito();
        navView = (NavigationView) findViewById(R.id.navview);

        navView.setNavigationItemSelectedListener(
                new NavigationView.OnNavigationItemSelectedListener() {
                    @Override
                    public boolean onNavigationItemSelected(MenuItem menuItem) {

                        switch (menuItem.getItemId()) {
                            case R.id.menu_seccion_promciones:
                                goTo(PROMOCIONES);
                                break;
                            case LOGIN_ID:
                                goTo(LOGIN);
                                break;
                            case LOGOUT_ID:
                                logout();
                                goTo(PROMOCIONES);
                                break;
                            case R.id.menu_seccion_seguimiento:
                                goTo(SEGUIMIENTO);
                                break;
                            case MIS_PEDIDOS_ID:
                                goTo(PEDIDOS);
                                break;
                            case R.id.menu_seccion_productos:
                                goTo(PRODUCTOS);
                                break;

                        }
                        return true;
                    }
                });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        MenuItem item = menu.findItem(R.id.badge);
        MenuItemCompat.setActionView(item, R.layout.cart_button);
        RelativeLayout notifCount = (RelativeLayout)   MenuItemCompat.getActionView(item);
        TextView tv = (TextView) notifCount.findViewById(R.id.actionbar_notifcation_textview);
        tv.setText(String.valueOf(carrito.getTotalProductos()));
        ImageView imagenCarro = (ImageView) notifCount.findViewById(R.id.imatgeCarre);
        imagenCarro.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                goTo(CARRITO);
            }
        });
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.

        switch (item.getItemId()) {
            case android.R.id.home:
                drawerLayout.openDrawer(GravityCompat.START);
                return true;
            case R.id.badge:
                goTo(CARRITO);
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        if(!"".equals(carrito.getUsuarioLogueado())) {
            navView.getMenu().add(0,MIS_PEDIDOS_ID, Menu.NONE , MIS_PEDIDOS ).setIcon(R.drawable.ic_menu);
            navView.getMenu().add(0,LOGOUT_ID, Menu.NONE , LOGOUT).setIcon(R.drawable.ic_menu);
        }
        else{
            navView.getMenu().add(0,LOGIN_ID, Menu.NONE , LOGIN).setIcon(R.drawable.ic_menu);
        }
        return true;
    }

    @Override
    public void onAddToCart(Producto producto, int cantidad) {
        Log.i(Settings.LOGTAG, producto.toString() + "cantidad:"+cantidad);
        this.carrito.addProducto(producto,cantidad);
        actualizarIconoCarrito();
    }

    public void actualizarIconoCarrito(){
        TextView tv = (TextView) findViewById(R.id.actionbar_notifcation_textview);
        tv.setText(String.valueOf(carrito.getTotalProductos()));
    }

    /**
     * Go to a especific fragment location.
     *
     * @param loc El fragment al que volem anar
     */
    public void goTo(String loc) {
        Fragment fragment = null;
        try {
            switch (loc) {
                case PROMOCIONES:
                    fragment = new FragmentPromociones(this);
                    navView.setCheckedItem(R.id.menu_seccion_promciones);
                    getSupportActionBar().setTitle(MainActivity.PROMOCIONES);
                    break;
                case LOGIN:
                    fragment = new FragmentLogin(MainActivity.this);
                    navView.setCheckedItem(navView.getMenu().findItem(LOGIN_ID).getItemId());
                    getSupportActionBar().setTitle(MainActivity.LOGIN);
                    break;
                case SEGUIMIENTO:
                    fragment = new FragmentSeguimientoPedido(MainActivity.this);
                    navView.setCheckedItem(R.id.menu_seccion_seguimiento);
                    getSupportActionBar().setTitle(MainActivity.SEGUIMIENTO);
                    break;
                case PEDIDOS:
                    fragment = new FragmentPedidos(MainActivity.this);
                    navView.setCheckedItem(navView.getMenu().findItem(MIS_PEDIDOS_ID).getItemId());
                    getSupportActionBar().setTitle(MainActivity.PEDIDOS);
                    break;
                case PRODUCTOS:
                    fragment = new FragmentProductos(MainActivity.this);
                    navView.setCheckedItem(R.id.menu_seccion_productos);
                    getSupportActionBar().setTitle(MainActivity.PRODUCTOS);
                    break;
                case REGISTRO:
                    fragment = new FragmentRegistro();
                    navView.setCheckedItem(R.id.menu_seccion_productos);
                    getSupportActionBar().setTitle(MainActivity.REGISTRO);
                    break;
                case CARRITO:
                    fragment = new FragmentCarrito(MainActivity.this);
                    getSupportActionBar().setTitle(MainActivity.CARRITO);
                    break;
                case LOGOUT:
                    this.logout();
            }

            if (fragment != null) {
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.content_frame, fragment)
                        .addToBackStack(null)
                        .commit();
            }

            drawerLayout.closeDrawers();
        }catch (NullPointerException ex){
            Log.e(Settings.LOGTAG, ex.getMessage());
        }
    }

    private void logout(){
        this.carrito.setUsuarioLogueado("");
        this.carrito.setIdUsuarioLogueado(-1);
        navView.getMenu().removeItem(LOGOUT_ID);
        navView.getMenu().removeItem(MIS_PEDIDOS_ID);
        TextView txtNavViewUser = (TextView) navView.findViewById(R.id.navViewUser);
        txtNavViewUser.setText(R.string.unregistered_User);
        invalidateOptionsMenu();
        this.goTo(PROMOCIONES);
    }

    public void login(String login){
        this.carrito.setUsuarioLogueado(login);
        navView.getMenu().removeItem(LOGIN_ID);
        TextView txtNavViewUser = (TextView) navView.findViewById(R.id.navViewUser);
        txtNavViewUser.setText(login);
        invalidateOptionsMenu();
    }
}

