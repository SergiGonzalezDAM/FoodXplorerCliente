package com.foodxplorer.foodxplorer;


import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import com.foodxplorer.foodxplorer.fragments.FragmentLogin;
import com.foodxplorer.foodxplorer.fragments.FragmentPedidos;
import com.foodxplorer.foodxplorer.fragments.FragmentProductos;
import com.foodxplorer.foodxplorer.fragments.FragmentPromociones;
import com.foodxplorer.foodxplorer.fragments.FragmentSeguimientoPedido;
import com.foodxplorer.foodxplorer.helpers.CurrentState;


public class MainActivity extends AppCompatActivity implements FragmentPromociones.OnAddToCart {

    private Toolbar appbar;
    private DrawerLayout drawerLayout;
    private NavigationView navView;
    public CurrentState CurrentState;

    public static final int PROMOCIONES = 0;
    public static final int SEGUIMIENTO = 1;
    public static final int PEDIDOS = 2;
    public static final int PRODUCTOS = 3;
    public static final int LOGIN = 4;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        appbar = (Toolbar) findViewById(R.id.appbar);
        setSupportActionBar(appbar);

        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_nav_menu);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);

        /************************************
         */
        Fragment fragment = new FragmentPromociones();
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.content_frame, fragment)
                .commit();
        drawerLayout.closeDrawers();

        /*
        //Eventos del Drawer Layout
        drawerLayout.setDrawerListener(new DrawerLayout.DrawerListener() {
            @Override
            public void onDrawerSlide(View drawerView, float slideOffset) {

            }

            @Override
            public void onDrawerOpened(View drawerView) {

            }

            @Override
            public void onDrawerClosed(View drawerView) {

            }

            @Override
            public void onDrawerStateChanged(int newState) {

            }
        });
        */
        CurrentState = new CurrentState();

        navView = (NavigationView) findViewById(R.id.navview);

        navView.setNavigationItemSelectedListener(
                new NavigationView.OnNavigationItemSelectedListener() {
                    @Override
                    public boolean onNavigationItemSelected(MenuItem menuItem) {

                        Fragment fragment = null;
                        switch (menuItem.getItemId()) {
                            case R.id.menu_seccion_1:
                                fragment = new FragmentPromociones();
                                break;
                            case R.id.menu_seccion_2:
                                fragment = new FragmentLogin(MainActivity.this);
                                break;
                            case R.id.menu_seccion_3:
                                fragment = new FragmentSeguimientoPedido();
                                break;
                            case R.id.menu_seccion_4:
                                fragment = new FragmentPedidos();
                                break;
                            case R.id.menu_seccion_5:
                                fragment = new FragmentProductos();
                                break;
                        }

                        if (fragment != null) {
                            getSupportFragmentManager().beginTransaction()
                                    .replace(R.id.content_frame, fragment)
                                    .commit();

                            menuItem.setChecked(true);
                            getSupportActionBar().setTitle(menuItem.getTitle());
                        }

                        drawerLayout.closeDrawers();

                        return true;
                    }
                });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
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
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onAddToCart(Producto producto) {
        Log.i("ON_ADD_TO_CART", producto.toString() + "");
    }

    /**
     * Go to a especific location.
     *
     * @param loc
     */
    public void goTo(int loc) {
        Fragment fragment = null;
        switch (loc) {
            case PROMOCIONES:
                fragment = new FragmentPromociones();
                navView.setCheckedItem(R.id.menu_seccion_1);
                break;
            case LOGIN:
                fragment = new FragmentLogin(MainActivity.this);
                navView.setCheckedItem(R.id.menu_seccion_2);
                break;
            case SEGUIMIENTO:
                fragment = new FragmentSeguimientoPedido();
                navView.setCheckedItem(R.id.menu_seccion_3);
                break;
            case PEDIDOS:
                fragment = new FragmentPedidos();
                navView.setCheckedItem(R.id.menu_seccion_4);
                break;
            case PRODUCTOS:
                fragment = new FragmentProductos();
                break;
        }

        if (fragment != null) {
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.content_frame, fragment)
                    .commit();


           // getSupportActionBar().setTitle(navView.getTitle());
        }

        drawerLayout.closeDrawers();
    }
}

