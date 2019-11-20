package com.example.mattilsynet.DetaljertVisning;


import android.app.Activity;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

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


    private ArrayList<DetaljerInfoKort> infoListe = new ArrayList<>();
    private final String LOG_TAG = DetaljerInfoKort.class.getSimpleName();
    String tilsynId;
    View view;

    public DetaljertVisningFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        this.view = inflater.inflate(R.layout.fragment_detaljert_visning, container, false);

        hentinfoKortData();

        return view;
    }

    private void hentinfoKortData() {
        final Bundle arguments = getArguments();
        if (arguments != null) {
            tilsynId = arguments.getString("tilsynid");
        }
        String infoliste_URL = "https://hotell.difi.no/api/json/mattilsynet/smilefjes/tilsyn?tilsynid=" + tilsynId;
        //Log.d(LOG_TAG, infoliste_URL);
        if (isOnline()){
            RequestQueue queue = Volley.newRequestQueue(getContext());
            StringRequest stringRequest =
                    new StringRequest(Request.Method.GET, infoliste_URL, this, this);
            queue.add(stringRequest);
        }else{
        }

    }

    private void settInnInfoData() {

        final Animation in = new AlphaAnimation(0.0f, 1.0f);
        in.setDuration(200);

        TextView stedNavn = view.findViewById(R.id.detaljer_navn);
        stedNavn.setText(infoListe.get(0).getStedNavn());
        //stedNavn.startAnimation(in);
        TextView stedOrgNr = view.findViewById(R.id.detaljer_orgnr);
        stedOrgNr.setText(infoListe.get(0).getStedOrgNr());
        //stedOrgNr.startAnimation(in);
        TextView rapportDato = view.findViewById(R.id.detaljer_dato);
        rapportDato.setText(infoListe.get(0).getStedDato());
        //stedOrgNr.startAnimation(in);
        TextView stedAdresse = view.findViewById(R.id.detaljer_adresse);
        stedAdresse.setText(infoListe.get(0).getStedAdresse());
        //stedOrgNr.startAnimation(in);
        TextView stedPostNr = view.findViewById(R.id.detaljer_postnr);
        stedPostNr.setText(infoListe.get(0).getStedPostkode());
        //stedOrgNr.startAnimation(in);
        TextView stedPoststed = view.findViewById(R.id.detaljer_poststed);
        stedPoststed.setText(infoListe.get(0).getStedPoststed());
        //stedOrgNr.startAnimation(in);

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
            infoListe = DetaljerInfoKort.createInfoCard(response);
            settInnInfoData();
        }catch (Exception e){
            Log.d(LOG_TAG, "Error: " + e);
        }
    }
}
