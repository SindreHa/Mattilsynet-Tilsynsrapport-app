package com.example.mattilsynet.DetaljertVisning;

import android.os.Bundle;
import android.util.Log;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.StringJoiner;

public class DetaljerInfoKort {

    private final String LOG_TAG = DetaljerInfoKort.class.getSimpleName();

    private String stedNavn;
    private String stedOrgNr;
    private String stedDato;
    private String stedAdresse;
    private String stedPostkode;
    private String stedPoststed;

    static final String TABELL_NAVN         = "entries";
    static final String STED_NAVN           = "navn";
    static final String STED_ORGNR          = "orgnummer";
    static final String STED_DATO           = "dato";
    static final String STED_ADRESSE        = "adrlinje1";
    static final String STED_POSTKODE       = "postnr";
    static final String STED_POSTSTED       = "poststed";

    public DetaljerInfoKort(JSONObject jsonPost) {
        this.stedNavn = jsonPost.optString(STED_NAVN);
        this.stedOrgNr = jsonPost.optString(STED_ORGNR);
        this.stedDato = jsonPost.optString(STED_DATO);
        this.stedAdresse = jsonPost.optString(STED_ADRESSE);
        this.stedPostkode = jsonPost.optString(STED_POSTKODE);
        this.stedPoststed = jsonPost.optString(STED_POSTSTED);
    }

    public static ArrayList<DetaljerInfoKort> createInfoCard(String jsonPostString)
            throws JSONException, NullPointerException {

        ArrayList<DetaljerInfoKort> infoKortListe = new ArrayList<DetaljerInfoKort>();
        JSONObject jsonData  = new JSONObject(jsonPostString);
        JSONArray jsonPostTabell = jsonData.optJSONArray("entries");

        if(jsonPostTabell != null)
        {
            for (int i = 0; i < jsonPostTabell.length(); i++) {
                JSONObject jsonPost = (JSONObject) jsonPostTabell.get(i);
                // jsonPost må matche verdiene i databasetabellen post
                DetaljerInfoKort infoKort = new DetaljerInfoKort(jsonPost);
                infoKortListe.add(infoKort);
            }
        } else {
            System.out.println("jsonPostTabell null " + TABELL_NAVN);
        }

        return infoKortListe;
    }

    public String getStedNavn() {
        return stedNavn;
    }

    public String getStedOrgNr() {
        return stedOrgNr;
    }

    public String getStedDato() {
        //Gjør om dato til en mer lesbar dato https://stackoverflow.com/questions/63150/whats-the-best-way-to-build-a-string-of-delimited-items-in-java
        String datoArr[] = this.stedDato.split("");
        StringJoiner joiner = new StringJoiner("/");
        joiner.add(datoArr[1] + datoArr[2]).add(datoArr[3] + datoArr[4]).add(datoArr[5] + datoArr[6] + datoArr[7] + datoArr[8]);
        String joinedString = joiner.toString();
        return joinedString;
    }

    public String getStedAdresse() {
        return stedAdresse;
    }

    public String getStedPostkode() {
        return stedPostkode;
    }

    public String getStedPoststed() {
        return stedPoststed;
    }
}

