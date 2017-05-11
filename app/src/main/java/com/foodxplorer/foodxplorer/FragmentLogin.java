package com.foodxplorer.foodxplorer;

import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.widget.DrawerLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class FragmentLogin extends Fragment implements View.OnClickListener{
    private DrawerLayout drawerLayout;
    private NavigationView navView;
    public FragmentLogin() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_login, container, false);

        return view;
    }

    @Override
    public void onClick(View view)
    {
        Fragment fragment = new FragmentRegistro();
        getFragmentManager().beginTransaction().replace(R.id.content_frame, fragment).commit();
        drawerLayout.closeDrawers();
    }
}
