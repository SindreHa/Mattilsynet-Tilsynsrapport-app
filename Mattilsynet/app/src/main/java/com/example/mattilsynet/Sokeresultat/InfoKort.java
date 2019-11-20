package com.example.mattilsynet.Sokeresultat;

import android.util.Log;

import com.example.mattilsynet.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.StringJoiner;

public class InfoKort {


    private final String LOG_TAG = InfoKort.class.getSimpleName();

    private String stedNavn;
    private String stedOrgNr;
    private String stedDato;
    private String stedAdresse;
    private String stedPostkode;
    private String stedPoststed;
    private String stedKarakter;

    static final String TABELL_NAVN         = "entries";
    static final String STED_NAVN           = "navn";
    static final String STED_ORGNR          = "orgnummer";
    static final String STED_DATO           = "dato";
    static final String STED_ADRESSE        = "adrlinje1";
    static final String STED_POSTKODE       = "postnr";
    static final String STED_POSTSTED       = "poststed";
    static final String STED_KARAKTER       = "total_karakter";

    public InfoKort() {}

    public InfoKort(JSONObject jsonPost) {
        this.stedNavn = jsonPost.optString(STED_NAVN);
        this.stedOrgNr = jsonPost.optString(STED_ORGNR);
        this.stedDato = jsonPost.optString(STED_DATO);
        this.stedAdresse = jsonPost.optString(STED_ADRESSE);
        this.stedPostkode = jsonPost.optString(STED_POSTKODE);
        this.stedPoststed = jsonPost.optString(STED_POSTSTED);
        this.stedKarakter = jsonPost.optString(STED_KARAKTER);
    }


    public static ArrayList<InfoKort> createInfoCard(String jsonPostString)
            throws JSONException, NullPointerException {

        ArrayList<InfoKort> postListe = new ArrayList<InfoKort>();
        JSONObject jsonData  = new JSONObject(jsonPostString);
        JSONArray jsonPostTabell = jsonData.optJSONArray("entries");

        if(jsonPostTabell != null)
        {
            for (int i = 0; i < jsonPostTabell.length(); i++) {
                JSONObject jsonPost = (JSONObject) jsonPostTabell.get(i);
                // jsonPost må matche verdiene i databasetabellen post
                InfoKort postKort = new InfoKort(jsonPost);
                postListe.add(postKort);
            }
        } else {
            System.out.println("jsonPostTabell null " + TABELL_NAVN);
        }

        return postListe;
    }

    public String getStedNavn() {
        return this.stedNavn;
    }

    public String getStedOrgNr() {
        return this.stedOrgNr;
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
        return this.stedAdresse;
    }

    public String getStedPostkode() {
        return this.stedPostkode;
    }

    public String getStedPoststed() {
        return this.stedPoststed;
    }

    public int getStedKarakter() {
        int bilde = 0;
        switch (this.stedKarakter) {
            //https://data.norge.no/data/mattilsynet/smilefjestilsyn-p%C3%A5-serveringssteder
            case "0": case "1":
                bilde = R.drawable.smilefjes;
                break;
            case "2":
                bilde = R.drawable.noytralfjes;
                break;
            case "3":
                bilde = R.drawable.surfjes;
                break;
        }
        //Log.d(LOG_TAG, this.stedKarakter + "");
        return bilde;
    }
}