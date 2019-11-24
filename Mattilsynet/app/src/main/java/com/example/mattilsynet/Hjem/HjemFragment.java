package com.example.mattilsynet.Hjem;


import android.Manifest;
import android.app.Activity;
import android.app.ActivityOptions;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
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
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputLayout;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

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

        if(getActivity()!=null) {
            fusedLocationClient = LocationServices.getFusedLocationProviderClient(getActivity());
        } else {
            Log.d(LOG_TAG, "Activity null");
        }

        setHasOptionsMenu(true);
    }

    //https://stackoverflow.com/questions/29623752/turn-off-auto-rotation-in-fragment
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

        //hentGPSPosisjon();

        return view;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        // Inflate the menu; this adds items to the action bar if it is present.
        inflater.inflate(R.menu.menu_main, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_instillinger) {
            startActivity(new Intent(getActivity(), Innstillinger.class));
        }

        return super.onOptionsItemSelected(item);
    }

    private void initialiserView() {
        settKlikkLyttere();
        sokeKriterierCheckbox();
    }

    private void settKlikkLyttere() {
        Button knapp = view.findViewById(R.id.sokeknapp);

        knapp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                hideKeyboardFrom(getContext(), v);

                Bundle b = new Bundle();
                hentSokeKriterierURL();
                b.putString("sokeKriterier", sokeUrl);
                Navigation.findNavController(view).navigate(R.id.action_nav_home_to_nav_search_result, b);
            }
        });
    }

    private void sokeKriterierCheckbox() {

        final EditText stedNavn = view.findViewById(R.id.sok_stednavn);
        final EditText postSted = view.findViewById(R.id.sok_poststed);

        if (getContext()!=null) {
            innstillinger = PreferenceManager.getDefaultSharedPreferences(getContext());
        }

        final String favorittStedNavn = innstillinger.getString("favorittstednavn", "");

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

        final TextInputLayout sokPoststedContainer = view.findViewById(R.id.sok_poststed_container);
        final CheckBox brukGPSPosisjon = view.findViewById(R.id.bruk_gps_posisjon);
        brukGPSPosisjon.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (brukGPSPosisjon.isChecked()) {
                    sokPoststedContainer.setVisibility(View.GONE);
                    hentGPSPosisjonPostkode();
                } else {
                    sokPoststedContainer.setVisibility(View.VISIBLE);
                }
            }
        });
    }

    private void hentSokeKriterierURL() {

        if (getContext()!=null) {
            innstillinger = PreferenceManager.getDefaultSharedPreferences(getContext());
        }

        final String favorittSortering = innstillinger.getString("favoritt_sortering", "");

        CheckBox brukGPSPosisjon = view.findViewById(R.id.bruk_gps_posisjon);

        final EditText stedNavn = view.findViewById(R.id.sok_stednavn);
        final String sStedNavn = stedNavn.getText().toString();

        final EditText postSted = view.findViewById(R.id.sok_poststed);
        final String sPostSted = postSted.getText().toString();

        if (!brukGPSPosisjon.isChecked()) {
            sokeUrl = "navn=" + sStedNavn + "&poststed=" + sPostSted;
        } else {
            sokeUrl = "navn=" + sStedNavn + "&postnr=" + gpsPostkode;
        }

        if (!favorittSortering.isEmpty()) {
            sokeUrl += "&dato=*" + favorittSortering;
        }
        //Log.d(LOG_TAG, sokeUrl);
    }

    //Legger ned virtuelt tastatur https://stackoverflow.com/questions/1109022/close-hide-the-android-soft-keyboard
    public static void hideKeyboardFrom(Context context, View view) {
        InputMethodManager imm = (InputMethodManager) context.getSystemService(Activity.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }

    //Enkel funksjon som viser en snackbar med melding som parameter
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

    private void hentGPSPosisjonPostkode() {
        if (ContextCompat.checkSelfPermission(getActivity().getApplicationContext(),
                Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            fusedLocationClient.getLastLocation()
                    .addOnSuccessListener(new OnSuccessListener<Location>() {
                        @Override
                        public void onSuccess(Location location) {
                            // Got last known location. In some rare situations this can be null.
                            if (location != null) {
                                lon = location.getLongitude();
                                lat = location.getLatitude();
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

    private void GPSJsonTilString(String sokResultat) throws JSONException {
        JSONObject jsonData = new JSONObject(sokResultat);
        JSONArray jsonArray = jsonData.optJSONArray("adresser");
        JSONObject jsonPostKode = (JSONObject) jsonArray.get(0);

        gpsPostkode =  jsonPostKode.getString("postnummer");
        Log.d(LOG_TAG, gpsPostkode);
    }

    //Sjekker nettverkstilgang
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
        Log.d(LOG_TAG, response);
        try {
            GPSJsonTilString(response);
        } catch (Exception e){
            Log.d(LOG_TAG, "Kunne ikke hente GPS Postkode: " + e);
        }
    }
}
