package com.example.mattilsynet.DetaljertVisning;


import android.app.Activity;
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
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.mattilsynet.R;

import java.util.ArrayList;

public class DetaljertVisningFragment extends Fragment implements
        Response.Listener<String>, Response.ErrorListener  {

    private String tilsynId;
    private View view;
    private RecyclerView mRecyclerView;
    private KravpunkterAdapter kravpunkterAdapter;
    private ArrayList<DetaljerKravpunkterKort> kravpunkterListe = new ArrayList<>();
    private final String LOG_TAG = DetaljerKravpunkterKort.class.getSimpleName();
    public final static String KRAVPUNKTER_URL = "https://hotell.difi.no/api/json/mattilsynet/smilefjes/kravpunkter?";

    public DetaljertVisningFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        this.view = inflater.inflate(R.layout.fragment_detaljert_visning, container, false);

        initialiserView();

        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        initialiserData();
    }

    private void initialiserView() {
        recyclerViewInit();
    }

    private void recyclerViewInit() {
        mRecyclerView = view.findViewById(R.id.detaljer_kravpunkt_recycler);
        oppdaterRecyclerView();
    }

    private void oppdaterRecyclerView() {
        kravpunkterAdapter = new KravpunkterAdapter(getContext(), kravpunkterListe);
        mRecyclerView.setAdapter(kravpunkterAdapter);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false));
    }

    private void initialiserData() {
        settInnInfoData();
        hentKravpunkterData();
    }

    private void hentKravpunkterData() {
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

    }

    // Sjekker om nettverkstilgang
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
            kravpunkterListe = DetaljerKravpunkterKort.lagKravpunktKort(response);
            oppdaterRecyclerView();
            settInnInfoData();
        }catch (Exception e){
            Log.d(LOG_TAG, "Error: " + e);
        }
    }
}
