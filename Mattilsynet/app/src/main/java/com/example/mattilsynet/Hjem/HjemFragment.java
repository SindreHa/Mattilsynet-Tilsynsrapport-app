package com.example.mattilsynet.Hjem;


import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.location.Location;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;
import androidx.preference.PreferenceManager;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.mattilsynet.Innstillinger.Innstillinger;
import com.example.mattilsynet.R;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.textfield.TextInputLayout;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class HjemFragment extends Fragment implements
        Response.Listener<String>, Response.ErrorListener {

    private View view;
    private final String LOG_TAG = HjemFragment.class.getSimpleName();
    private final static int MY_REQUEST_LOCATION = 1;
    private double lon;
    private double lat;
    private String gpsPostkode;
    private String sokeUrl;
    private SharedPreferences innstillinger;
    private FusedLocationProviderClient fusedLocationClient;

    public HjemFragment() {}

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //Setter opp GPS henting
        if(getActivity()!=null) {
            fusedLocationClient = LocationServices.getFusedLocationProviderClient(getActivity());
        } else {
            Log.d(LOG_TAG, "Activity null");
        }

        setHasOptionsMenu(true);
    }

    //Disabler rotasjon https://stackoverflow.com/questions/29623752/turn-off-auto-rotation-in-fragment
    @Override
    public void onResume() {
        super.onResume();
        getActivity().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
    }

    @Override
    public void onPause() {
        super.onPause();
        getActivity().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_FULL_SENSOR);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        this.view = inflater.inflate(R.layout.fragment_hjem, container, false);

        initialiserView();

        return view;
    }

    //Lager optionsmenu hvor innstillinger snarevei vises
    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.menu_main, menu);
    }

    //Setter lytter på innstillinger knapp
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_instillinger) {
            startActivity(new Intent(getActivity(), Innstillinger.class));
        }

        return super.onOptionsItemSelected(item);
    }

    /*
     * Metoder for grafiske elementer
     */

    private void initialiserView() {
        settKlikkLyttere();
    }

    //Metode som setter klikkluttere
    private void settKlikkLyttere() {

        Button sokeKnapp = view.findViewById(R.id.sokeknapp);

        //Setter lytter på søkeknapp
        sokeKnapp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                hideKeyboardFrom(getContext(), v);
                //Lager bundle som sender inn søkeurl som brukes i neste fragment
                Bundle b = new Bundle();
                hentSokeKriterierURL();
                b.putString("sokeKriterier", sokeUrl);
                Navigation.findNavController(view).navigate(R.id.action_nav_home_to_nav_search_result, b);
            }
        });

        sokeKriterierCheckbox();
    }

    //Metode som lytter til hva som avhukes på søkekriterier
    private void sokeKriterierCheckbox() {

        final EditText stedNavn = view.findViewById(R.id.sok_stednavn);

        //Henter innstillinger
        if (getContext()!=null) {
            innstillinger = PreferenceManager.getDefaultSharedPreferences(getContext());
        }

        final String favorittStedNavn = innstillinger.getString("favorittstednavn", "");

        //Lytter på avhuk boks for å bruke lagret favorittsted
        final CheckBox brukFavorittsted = view.findViewById(R.id.bruk_favorittsted);
        brukFavorittsted.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (brukFavorittsted.isChecked()) {
                    stedNavn.setText(favorittStedNavn);
                } else {
                    stedNavn.setText("");
                }
            }
        });

        //Lytter for avhuk av "Bruk min posisjon" boks
        final TextInputLayout sokPoststedContainer = view.findViewById(R.id.sok_poststed_container);
        final CheckBox brukGPSPosisjon = view.findViewById(R.id.bruk_gps_posisjon);
        brukGPSPosisjon.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (brukGPSPosisjon.isChecked()) {
                    //Skjuler felt for postssted siden det ikke trengs
                    sokPoststedContainer.setVisibility(View.GONE);
                    hentGPSPosisjon();
                } else {
                    //Gjør felt for poststed synlig igjen
                    sokPoststedContainer.setVisibility(View.VISIBLE);
                }
            }
        });
    }

    //Legger ned virtuelt tastatur https://stackoverflow.com/questions/1109022/close-hide-the-android-soft-keyboard
    public static void hideKeyboardFrom(Context context, View view) {
        InputMethodManager imm = (InputMethodManager) context.getSystemService(Activity.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }


    /*
     * Metoder for innhenting av data
     */

    //Metode som sjekker hva søkeurl skal være basert på avhukede bokser og innstillinger
    private void hentSokeKriterierURL() {

        //Henter inn innstillinger
        if (getContext()!=null) {
            innstillinger = PreferenceManager.getDefaultSharedPreferences(getContext());
        }

        //Henter string for favoritt årstall
        final String favorittSortering = innstillinger.getString("favoritt_sortering", "");

        CheckBox brukGPSPosisjon = view.findViewById(R.id.bruk_gps_posisjon);

        final EditText stedNavn = view.findViewById(R.id.sok_stednavn);
        final String sStedNavn = stedNavn.getText().toString();

        final EditText postSted = view.findViewById(R.id.sok_poststed);
        final String sPostSted = postSted.getText().toString();

        //Hvis min posisjon skal brukes endres url til å søke på postnummer
        if (!brukGPSPosisjon.isChecked()) {
            sokeUrl = "navn=" + sStedNavn + "&poststed=" + sPostSted;
        } else {
            sokeUrl = "navn=" + sStedNavn + "&postnr=" + gpsPostkode;
        }

        //Legger til sortering på årstall hvis det er lagret et årstall i innstillinger
        if (!favorittSortering.isEmpty()) {
            sokeUrl += "&dato=*" + favorittSortering;
        }
        //Log.d(LOG_TAG, sokeUrl);
    }

    //Mtode som henter høyde og breddegrader på posisjon
    private void hentGPSPosisjon() {
        //Sjekker først om bruker har tilatt å bruke GPS
        if (ContextCompat.checkSelfPermission(getActivity().getApplicationContext(),
                Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            fusedLocationClient.getLastLocation()
                    .addOnSuccessListener(new OnSuccessListener<Location>() {
                        @Override
                        public void onSuccess(Location location) {
                            // Got last known location. In some rare situations this can be null.
                            if (location != null) {
                                //Setter lon og lat verdier
                                lon = location.getLongitude();
                                lat = location.getLatitude();
                                //Ber metode som å hente ut postnummer med volley
                                hentPostkode();
                            }
                        }
                    });
        } else {
            // Appen har ikke tillatelse, spør bruker
            ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    MY_REQUEST_LOCATION);
        }
    }

    //Metode som sender url inn til kartverket for å hente ut postnummer på posisjon
    private void hentPostkode() {
        String postkodeURL = "https://ws.geonorge.no/adresser/v1/punktsok?radius=2000&lat=" + lat
                + "&lon=" + lon + "&filtrer=adresser.postnummer&treffPerSide=1&side=0";

        Log.d(LOG_TAG, postkodeURL);
        if (isOnline()){
            RequestQueue queue = Volley.newRequestQueue(getContext());
            StringRequest stringRequest =
                    new StringRequest(Request.Method.GET, postkodeURL, this, this);
            queue.add(stringRequest);
        }else{
        }
    }

    //Gjør om response fra Volley til en enkel string med postnummer
    private void JSONTilString(String sokResultat) throws JSONException {
        JSONObject jsonData = new JSONObject(sokResultat);
        JSONArray jsonArray = jsonData.optJSONArray("adresser");
        JSONObject jsonPostKode = (JSONObject) jsonArray.get(0);

        gpsPostkode =  jsonPostKode.getString("postnummer");
        //Log.d(LOG_TAG, gpsPostkode);
    }

    //Sjekker nettverkstilgang
    private boolean isOnline() {
        ConnectivityManager conMgr = (ConnectivityManager) getActivity().getSystemService(Activity.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = conMgr.getActiveNetworkInfo();
        return (networkInfo != null && networkInfo.isConnected());
    }

    @Override
    public void onErrorResponse(VolleyError error) { }

    @Override
    public void onResponse(String response) {
        Log.d(LOG_TAG, response);
        try {
            JSONTilString(response);
        } catch (Exception e){
            Log.d(LOG_TAG, "Kunne ikke hente GPS Postkode: " + e);
        }
    }
}
