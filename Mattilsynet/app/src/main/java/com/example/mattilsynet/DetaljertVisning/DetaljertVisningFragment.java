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

    private String tilsynId;
    private View view;
    private boolean hentInfoFerdig, hentKravFerdig;
    private ArrayList<DetaljerInfoKort> infoListe = new ArrayList<>();
    private final String LOG_TAG = DetaljerInfoKort.class.getSimpleName();
    public final static String TILSYN_URL = "https://hotell.difi.no/api/json/mattilsynet/smilefjes/tilsyn?";
    public final static String KRAVPUNKTER_URL = "https://hotell.difi.no/api/json/mattilsynet/smilefjes/kravpunkter?";

    public DetaljertVisningFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        this.view = inflater.inflate(R.layout.fragment_detaljert_visning, container, false);

        hentinfoKortData();
        settInnInfoData();

        return view;
    }

    private void hentinfoKortData() {
        final Bundle arguments = getArguments();
        if (arguments != null) {
            tilsynId = arguments.getString("tilsynid");
        }
        String infoliste_URL = TILSYN_URL + tilsynId;
        if (isOnline()){
            RequestQueue queue = Volley.newRequestQueue(getContext());
            StringRequest stringRequest =
                    new StringRequest(Request.Method.GET, infoliste_URL, this, this);
            queue.add(stringRequest);
        }else{
        }
    }

    private void hentKravpunkterData() {
        final Bundle arguments = getArguments();
        if (arguments != null) {
            tilsynId = arguments.getString("tilsynid");
        }
        String kravpunkterURL = KRAVPUNKTER_URL + tilsynId;
        if (isOnline()){
            RequestQueue queue = Volley.newRequestQueue(getContext());
            StringRequest stringRequest =
                    new StringRequest(Request.Method.GET, kravpunkterURL, this, this);
            queue.add(stringRequest);
        }else{
        }
    }

    private void settInnInfoData() {

        final Animation in = new AlphaAnimation(0.0f, 1.0f);
        in.setDuration(200);
        //stedNavn.startAnimation(in);

        final Bundle infoKortBundle = getArguments();
        Log.d(LOG_TAG, "navn= " + infoKortBundle.getString("stedNavn"));

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
            if (hentInfoFerdig) {
                //infoListe = DetaljerInfoKort.createInfoCard(response);
                settInnInfoData();
            } else {
                //Kravpunkter
            }
        }catch (Exception e){
            Log.d(LOG_TAG, "Error: " + e);
        }
    }
}
