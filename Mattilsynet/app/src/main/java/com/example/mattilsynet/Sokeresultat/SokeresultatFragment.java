package com.example.mattilsynet.Sokeresultat;


import android.app.Activity;
import android.content.DialogInterface;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.view.ContextThemeWrapper;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.OvershootInterpolator;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.mattilsynet.R;
import com.google.android.material.snackbar.Snackbar;

import java.util.ArrayList;


public class SokeresultatFragment extends Fragment implements InfoListeAdapter.OnItemClickListener,
        Response.Listener<String>, Response.ErrorListener  {

    private View view;
    private RecyclerView mRecyclerView;
    ImageView fjernFilter;
    CardView fjernFilterKort;
    private InfoListeAdapter infoAdapter;
    String filterArstall = "";
    private ArrayList<InfoKort> infoListe = new ArrayList<>();
    private final String LOG_TAG = SokeresultatFragment.class.getSimpleName();
    public final static String ENDPOINT = "https://hotell.difi.no/api/json/mattilsynet/smilefjes/tilsyn?";

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        this.view = inflater.inflate(R.layout.fragment_search_result, container, false);

        initialiserView();

        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        initialiserData();
    }

    private void initialiserView() {
        recyclerViewInit();
        settKlikkLyttere();
    }

    private void settKlikkLyttere() {
        CardView knapp = view.findViewById(R.id.filter_sok);
        knapp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                filterDialog();
            }
        });

        fjernFilter = view.findViewById(R.id.klarer_filter);
        fjernFilterKort = view.findViewById(R.id.klarer_filter_kort);
        fjernFilterKort.setVisibility(View.INVISIBLE);

        fjernFilter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                fjernFilterKort.animate()
                        .scaleX(0)
                        .scaleY(0)
                        .setDuration(250)
                        .start();

                filterArstall = "";
                initialiserData();
            }
        });
    }

    private void recyclerViewInit() {
        mRecyclerView = view.findViewById(R.id.recyclerView_sokeresultat);
        oppdaterRecyclerView();
    }

    private void oppdaterRecyclerView() {
        infoAdapter = new InfoListeAdapter(getContext(), infoListe, new InfoListeAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(InfoKort card) {
                Bundle b = new Bundle();
                b.putString("title", card.getStedAdresse());
                Navigation.findNavController(view).navigate(R.id.action_nav_search_result_to_nav_detailed_view, b);
            }
        });
        mRecyclerView.setAdapter(infoAdapter);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false));
    }

    private void initialiserData() {
        hentData();
        recyclerAnimasjon();
    }

    private void hentData() {
        infoListe.clear();
        final Bundle sokeDataBundle = getArguments();
        String infoliste_URL = ENDPOINT + sokeDataBundle.getString("sokeKriterier") + "&dato=*" + filterArstall;
        Log.d(LOG_TAG, infoliste_URL);
        if (isOnline()){
            RequestQueue queue = Volley.newRequestQueue(getContext());
            StringRequest stringRequest =
                    new StringRequest(Request.Method.GET, infoliste_URL, this, this);
            queue.add(stringRequest);
        }else{
            lagSnackbar("Ingen nettverkstilgang. Kan ikke laste varer.");
        }

        /*infoListe.add(new InfoKort(getString(R.string.placeholder_title), "Org nr: 5231761235", "Gatenavn 1", "6242", "Stedsnavn", R.drawable.smilefjes));
        infoListe.add(new InfoKort(getString(R.string.placeholder_title), "Org nr: 5231761235", "Gatenavn 2", "6242", "Stedsnavn", R.drawable.noytralfjes));
        infoListe.add(new InfoKort(getString(R.string.placeholder_title), "Org nr: 5231761235", "Gatenavn 3", "6242", "Stedsnavn", R.drawable.noytralfjes));*/
    }

    // Sjekker om nettverkstilgang
    private boolean isOnline() {
        ConnectivityManager conMgr = (ConnectivityManager) getActivity().getSystemService(Activity.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = conMgr.getActiveNetworkInfo();
        return (networkInfo != null && networkInfo.isConnected());
    }

    @Override
    public void onResponse(String response) {
        try{
            //Log.d(LOG_TAG, response);
            infoListe = InfoKort.createInfoCard(response);
            oppdaterRecyclerView();
        }catch (Exception e){
        }
        infoAdapter.notifyDataSetChanged();
        recyclerAnimasjon();
    }

    @Override
    public void onErrorResponse(VolleyError error) {
        Toast.makeText(getContext(), error.getMessage(),
                Toast.LENGTH_SHORT).show();
    }

    private void recyclerAnimasjon() {
        //https://stackoverflow.com/questions/38909542/how-to-animate-recyclerview-items-when-adapter-is-initialized-in-order
        mRecyclerView.getViewTreeObserver().addOnPreDrawListener(
                new ViewTreeObserver.OnPreDrawListener() {

                    @Override
                    public boolean onPreDraw() {
                        mRecyclerView.getViewTreeObserver().removeOnPreDrawListener(this);
                        int tid = 450;
                        for (int i = 0; i < mRecyclerView.getChildCount(); i++) {
                            tid = tid + 100;
                            View v = mRecyclerView.getChildAt(i);
                            View reCycler = mRecyclerView;
                            v.setAlpha(1.0f);
                            v.setTranslationY(reCycler.getHeight());
                            v.animate()
                                    .translationY(0)
                                    .setInterpolator(new AccelerateDecelerateInterpolator())
                                    .alpha(1.0f)
                                    .setDuration(tid)
                                    .setStartDelay(i * 50)
                                    .start();
                        }
                        return true;
                    }
                });
    }

    @Override
    public void onItemClick(InfoKort card) { }

    public void filterDialog() {
        final TextView klarerFilterVerdi = view.findViewById(R.id.klarer_filter_verdi);
        //https://stackoverflow.com/questions/2795300/how-to-implement-a-custom-alertdialog-view
        LayoutInflater inflater = getLayoutInflater();
        View dialoglayout = inflater.inflate(R.layout.filter_dialog, null);
        final EditText text = dialoglayout.findViewById(R.id.filter);
        AlertDialog dialog = new AlertDialog.Builder(new ContextThemeWrapper(getContext(),R.style.AlertDialogCustom))
                .setTitle("Skriv inn ønsket dato")
                .setMessage("Skriv inn dato uten mellomtegn, f.eks 012018 gir deg alt fra januar 2018")
                .setView(dialoglayout)
                .setPositiveButton("Søk", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        filterArstall = text.getText().toString();

                        if (!filterArstall.matches("")) {
                            klarerFilterVerdi.setText(filterArstall);
                            fjernFilterKort.setScaleX(0);
                            fjernFilterKort.setScaleY(0);
                            fjernFilterKort.setVisibility(View.VISIBLE);
                            fjernFilterKort.animate()
                                    .scaleX(1)
                                    .scaleY(1)
                                    .setDuration(250)
                                    .start();

                            initialiserData();
                        }
                    }
                })
                .setNegativeButton("Avbryt", null)
                .create();
        dialog.show();
    }

    private void lagSnackbar(String melding) {
        final Snackbar snackBar = Snackbar.make(view, melding, Snackbar.LENGTH_LONG);
        snackBar.setAction("Ok", new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                snackBar.dismiss();
            }
        });
        snackBar.show();
    }
}
