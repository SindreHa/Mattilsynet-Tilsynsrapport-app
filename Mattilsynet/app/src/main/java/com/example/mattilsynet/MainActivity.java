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

    //Metode som setter opp alle view elementer
    private void initialiserView() {
        setContentView(R.layout.activity_main);

        //Setter opp toolbar i toppen som har tilbakeknappene og tittel på siden man er på
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        //Oppsett av fragmenter som bruker i NavigationUI navigering
        mAppBarKonfigurasjon = new AppBarConfiguration.Builder(

                R.id.nav_hjem, R.id.nav_sokeresultat, R.id.nav_detaljert_visning)
                .build();

        //Oppsett av host til fragment hvor alle fragmenter navigeres imellom
        navKontroller = Navigation.findNavController(this, R.id.nav_host_fragment);
        //Setter opp navigasjon med actionbar i toppen
        NavigationUI.setupActionBarWithNavController(this, navKontroller);
    }


    @Override
    public boolean onSupportNavigateUp() {
        //Metode som brukes når bruker trykker på tilbakeknappen i toolbar
        NavController navKontroller = Navigation.findNavController(this, R.id.nav_host_fragment);
        return NavigationUI.navigateUp(navKontroller, mAppBarKonfigurasjon)
                || super.onSupportNavigateUp();
    }
}
