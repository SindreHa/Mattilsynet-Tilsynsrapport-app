package com.example.mattilsynet.Sokeresultat;

import com.example.mattilsynet.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class InfoKort {

    private String stedNavn;
    private String stedOrgNr;
    private String stedDato;
    private String stedAdresse;
    private String stedPostkode;
    private String stedPoststed;
    private int stedKarakter;

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
        this.stedKarakter = jsonPost.optInt(STED_KARAKTER);
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
        return this.stedDato;
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
        switch (this.stedKarakter) {
            case 0:
                this.stedKarakter = R.drawable.smilefjes;
                break;
            case 1:
                this.stedKarakter = R.drawable.noytralfjes;
                break;
            case 2:
                this.stedKarakter = R.drawable.surfjes;
                break;
        }
        return this.stedKarakter;
    }
}
