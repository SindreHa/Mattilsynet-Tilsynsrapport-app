package com.example.mattilsynet.DetaljertVisning;


import android.animation.Animator;
import android.animation.ObjectAnimator;
import android.app.Activity;
import android.content.pm.ActivityInfo;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.DecelerateInterpolator;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.mattilsynet.R;

import java.util.ArrayList;

import static android.animation.ObjectAnimator.ofInt;

public class DetaljertVisningFragment extends Fragment implements
        Response.Listener<String>, Response.ErrorListener  {

    private String tilsynId;
    private View view;
    private RecyclerView mRecyclerView;
    private KravpunkterAdapter kravpunkterAdapter;
    private ProgressBar progressBar;
    private ObjectAnimator anim;
    private ArrayList<DetaljerKravpunkterKort> kravpunkterListe = new ArrayList<>();
    private final String LOG_TAG = DetaljerKravpunkterKort.class.getSimpleName();
    public final static String KRAVPUNKTER_URL = "https://hotell.difi.no/api/json/mattilsynet/smilefjes/kravpunkter?";

    public DetaljertVisningFragment() { }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        this.view = inflater.inflate(R.layout.fragment_detaljert_visning, container, false);

        initialiserView();

        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        settInnInfoData();
    }

    @Override
    public Animation onCreateAnimation(int transit, final boolean enter, int nextAnim) {
        Animation anim = AnimationUtils.loadAnimation(getActivity(), nextAnim);

        anim.setAnimationListener(new Animation.AnimationListener() {

            @Override
            public void onAnimationStart(Animation animation) {
                Log.d(LOG_TAG, "Animation started.");
                progressBar(0);
                // additional functionality
            }

            @Override
            public void onAnimationRepeat(Animation animation) {
                Log.d(LOG_TAG, "Animation repeating.");
                // additional functionality
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                Log.d(LOG_TAG, "Animation ended.");
                if (enter) {
                    progressBar(33);
                    initialiserData();
                }
                // additional functionality
            }
        });

        return anim;
    }

    /*
     * Metoder for grafiske elementer
     */

    private void initialiserView() {
        recyclerViewInit();
    }

    //Setter opp recyclerview som viser kravpunkter
    private void recyclerViewInit() {
        mRecyclerView = view.findViewById(R.id.detaljer_kravpunkt_recycler);
        oppdaterRecyclerView();
    }

    //Setter adapter på recyclerview som setter inn all data
    private void oppdaterRecyclerView() {
        kravpunkterAdapter = new KravpunkterAdapter(getContext(), kravpunkterListe);
        mRecyclerView.setAdapter(kravpunkterAdapter);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false));
    }

    private void recyclerAnimasjonInn() {
        //https://stackoverflow.com/questions/38909542/how-to-animate-recyclerview-items-when-adapter-is-initialized-in-order
        mRecyclerView.getViewTreeObserver().addOnPreDrawListener(
                new ViewTreeObserver.OnPreDrawListener() {

                    @Override
                    public boolean onPreDraw() {
                        mRecyclerView.getViewTreeObserver().removeOnPreDrawListener(this);
                        for (int i = 0; i < mRecyclerView.getChildCount(); i++) {
                            /*
                             * Animasjon som animerer hvert kort synlig i view.
                             * pr loop økes delay for å gi en mykere animasjon
                             */
                            View v = mRecyclerView.getChildAt(i);
                            v.setAlpha(0f);
                            v.animate()
                                    .alpha(1f)
                                    .setDuration(300) //Lengde i ms på animasjon pr kort
                                    .setStartDelay(i * 50) //Tid før neste kort skal animeres
                                    .start();
                        }
                        return true;
                    }
                });

    }

    private void progressBar(int prog) {
        if (progressBar==null) {
            progressBar = view.findViewById(R.id.detailed_view_progressbar);
            progressBar.setProgress(0);
        }
        anim = ObjectAnimator.ofInt(progressBar, "progress", prog);
        anim.setInterpolator(new DecelerateInterpolator(4));
        anim.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animator) { }
            @Override
            public void onAnimationEnd(Animator animator) {
                if (progressBar.getProgress()==100) {
                    progressBar.setVisibility(View.INVISIBLE);
                }
            }
            @Override
            public void onAnimationCancel(Animator animator) { }
            @Override
            public void onAnimationRepeat(Animator animator) { }
        });
        anim.setDuration(500).start();
    }

    /*
     * Metoder for innhenting av data
     */

    private void initialiserData() {
        hentKravpunkterData();
    }

    //Funksjon som henter inn data med volley
    private void hentKravpunkterData() {
        //Henter ut tilsynsid fra bundle sendt inn fra forrige fragment
        final Bundle arguments = getArguments();
        if (arguments != null) {
            tilsynId = arguments.getString("tilsynid");
        }
        String kravpunkterURL = KRAVPUNKTER_URL + "tilsynid=" + tilsynId;
        if (isOnline()){
            RequestQueue queue = Volley.newRequestQueue(getContext());
            StringRequest stringRequest =
                    new StringRequest(Request.Method.GET, kravpunkterURL, this, this);
            queue.add(stringRequest);
        }else{
        }
    }

    //En enkel metode som setter all tilsyn info inn med data fra bundle
    private void settInnInfoData() {

        final Bundle infoKortBundle = getArguments();

        TextView stedNavn = view.findViewById(R.id.detaljer_navn);
        stedNavn.setText(infoKortBundle.getString("stedNavn"));
        TextView stedOrgNr = view.findViewById(R.id.detaljer_orgnr);
        stedOrgNr.setText(infoKortBundle.getString("stedOrgNr"));
        TextView rapportDato = view.findViewById(R.id.detaljer_dato);
        rapportDato.setText(infoKortBundle.getString("rapportDato"));
        TextView stedTotKarakter = view.findViewById(R.id.detaljer_tot_karakter);
        stedTotKarakter.setText(infoKortBundle.getString("stedKarakter"));
        TextView stedAdresse = view.findViewById(R.id.detaljer_adresse);
        stedAdresse.setText(infoKortBundle.getString("stedAdresse"));
        TextView stedPostNr = view.findViewById(R.id.detaljer_postnr);
        stedPostNr.setText(infoKortBundle.getString("stedPostKode"));
        TextView stedPoststed = view.findViewById(R.id.detaljer_poststed);
        stedPoststed.setText(infoKortBundle.getString("stedPostSted"));
        ImageView stedKarakterBilde = view.findViewById(R.id.detaljer_bilde);
        stedKarakterBilde.setImageResource(infoKortBundle.getInt("stedKarakterBilde"));
    }

    // Sjekker nettverkstilgang
    private boolean isOnline() {
        ConnectivityManager conMgr = (ConnectivityManager) getActivity().getSystemService(Activity.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = conMgr.getActiveNetworkInfo();
        return (networkInfo != null && networkInfo.isConnected());
    }

    @Override
    public void onErrorResponse(VolleyError error) {

    }

    @Override
    public void onResponse(String response) {
        try{
            //Setter inn data for tilsyninfo
            settInnInfoData();
            //Lager kravpunktliste
            kravpunkterListe = DetaljerKravpunkterKort.lagKravpunktKort(response);
            //Oppdaterer kravpunkt recycler
            oppdaterRecyclerView();
            recyclerAnimasjonInn();
            progressBar(100);
        }catch (Exception e){
            Log.d(LOG_TAG, "Kunne ikke hente kravpunktdata: " + e);
        }
    }
}
