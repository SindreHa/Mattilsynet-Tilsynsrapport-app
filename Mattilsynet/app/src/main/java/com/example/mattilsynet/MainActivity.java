package com.example.mattilsynet;

import android.os.Bundle;

import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.navigation.ui.NavigationUI;

import android.view.Menu;
import android.view.MenuItem;

public class MainActivity extends AppCompatActivity {

    private AppBarConfiguration mAppBarKonfigurasjon;
    private NavController navKontroller;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        initialiserView();
    }

    private void initialiserView() {
        setContentView(R.layout.activity_main);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        mAppBarKonfigurasjon = new AppBarConfiguration.Builder(

                R.id.nav_hjem, R.id.nav_sokeresultat, R.id.nav_detaljert_visning)
                .build();

        navKontroller = Navigation.findNavController(this, R.id.nav_host_fragment);
        NavigationUI.setupActionBarWithNavController(this, navKontroller);
    }


    @Override
    public boolean onSupportNavigateUp() {
        NavController navKontroller = Navigation.findNavController(this, R.id.nav_host_fragment);
        return NavigationUI.navigateUp(navKontroller, mAppBarKonfigurasjon)
                || super.onSupportNavigateUp();
    }
}
