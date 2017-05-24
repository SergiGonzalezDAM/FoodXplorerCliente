package com.foodxplorer.foodxplorer;


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
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.foodxplorer.foodxplorer.fragments.FragmentLogin;
import com.foodxplorer.foodxplorer.fragments.FragmentPedidos;
import com.foodxplorer.foodxplorer.fragments.FragmentProductos;
import com.foodxplorer.foodxplorer.fragments.FragmentPromociones;
import com.foodxplorer.foodxplorer.fragments.FragmentSeguimientoPedido;
import com.foodxplorer.foodxplorer.helpers.Carrito;


public class MainActivity extends AppCompatActivity implements FragmentPromociones.OnAddToCart {

    private Toolbar appbar;
    private DrawerLayout drawerLayout;
    private NavigationView navView;
    public Carrito CurrentState;

    public static final String PROMOCIONES = "PROMOCIONES";
    public static final String SEGUIMIENTO = "SEGUIMIENTO";
    public static final String PEDIDOS = "PEDIDOS";
    public static final String PRODUCTOS = "PRODUCTOS";
    public static final String LOGIN = "LOGIN";
    public static final String REGISTRO = "REGISTRO";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        appbar = (Toolbar) findViewById(R.id.appbar);
        setSupportActionBar(appbar);

        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_nav_menu);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);


        Fragment fragment = new FragmentPromociones(this);
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.content_frame, fragment)
                .commit();
        drawerLayout.closeDrawers();

        CurrentState = new Carrito();
        navView = (NavigationView) findViewById(R.id.navview);

        navView.setNavigationItemSelectedListener(
                new NavigationView.OnNavigationItemSelectedListener() {
                    @Override
                    public boolean onNavigationItemSelected(MenuItem menuItem) {

                        switch (menuItem.getItemId()) {
                            case R.id.menu_seccion_1:
                                goTo(PROMOCIONES);
                                break;
                            case R.id.menu_seccion_2:
                                goTo(LOGIN);
                                break;
                            case R.id.menu_seccion_3:
                                goTo(SEGUIMIENTO);
                                break;
                            case R.id.menu_seccion_4:
                                goTo(PEDIDOS);
                                break;
                            case R.id.menu_seccion_5:
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
        //MenuItem item = menu.findItem(R.id.badge);
        //MenuItemCompat.setActionView(item, R.layout.cart_button);
        //RelativeLayout notifCount = (RelativeLayout)   MenuItemCompat.getActionView(item);
        //TextView tv = (TextView) notifCount.findViewById(R.id.actionbar_notifcation_textview);
        //tv.setText("12");
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
                System.out.println("Carro de la compra ");
                return true;

            default:
                System.out.println("clicado algo raro....");
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onAddToCart(Producto producto, int cantidad) {
        Log.i("ON_ADD_TO_CART", producto.toString() + "");
    }

    /**
     * Go to a especific location.
     *
     * @param loc El fragment al que volem anar
     */
    public void goTo(String loc) {
        Fragment fragment = null;
        switch (loc) {
            case PROMOCIONES:
                fragment = new FragmentPromociones(this);
                navView.setCheckedItem(R.id.menu_seccion_1);
                getSupportActionBar().setTitle(MainActivity.PROMOCIONES);
                break;
            case LOGIN:
                fragment = new FragmentLogin(MainActivity.this);
                navView.setCheckedItem(R.id.menu_seccion_2);
                getSupportActionBar().setTitle(MainActivity.LOGIN);
                break;
            case SEGUIMIENTO:
                fragment = new FragmentSeguimientoPedido();
                navView.setCheckedItem(R.id.menu_seccion_3);
                getSupportActionBar().setTitle(MainActivity.SEGUIMIENTO);
                break;
            case PEDIDOS:
                fragment = new FragmentPedidos(MainActivity.this);
                navView.setCheckedItem(R.id.menu_seccion_4);
                getSupportActionBar().setTitle(MainActivity.PEDIDOS);
                break;
            case PRODUCTOS:
                fragment = new FragmentProductos();
                navView.setCheckedItem(R.id.menu_seccion_5);
                getSupportActionBar().setTitle(MainActivity.PRODUCTOS);
                break;
            case REGISTRO:
                fragment = new FragmentRegistro();
                navView.setCheckedItem(R.id.menu_seccion_5);
                getSupportActionBar().setTitle(MainActivity.REGISTRO);
                break;
        }

        if (fragment != null) {
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.content_frame, fragment)
                    .commit();


        }

        drawerLayout.closeDrawers();
    }
}

