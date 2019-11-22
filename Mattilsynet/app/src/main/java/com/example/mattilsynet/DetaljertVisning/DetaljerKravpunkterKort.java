package com.example.mattilsynet.DetaljertVisning;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class DetaljerKravpunkterKort {

    private final String LOG_TAG = DetaljerKravpunkterKort.class.getSimpleName();

    private String kravpunktTittel;
    private String kravpunktKarakter;

    static final String TABELL_NAVN         = "entries";
    static final String KRAVPUNKT_TITTEL    = "kravpunktnavn_no";
    static final String STED_ORGNR          = "karakter";

    public DetaljerKravpunkterKort(JSONObject jsonPost) {
        this.kravpunktTittel = jsonPost.optString(KRAVPUNKT_TITTEL);
        this.kravpunktKarakter = jsonPost.optString(STED_ORGNR);
    }

    public static ArrayList<DetaljerKravpunkterKort> lagKravpunktKort(String jsonPostString)
            throws JSONException, NullPointerException {

        ArrayList<DetaljerKravpunkterKort> kravpunkterListe = new ArrayList<>();
        JSONObject jsonData  = new JSONObject(jsonPostString);
        JSONArray jsonKravpunkterTabell = jsonData.optJSONArray("entries");

        if(jsonKravpunkterTabell != null)
        {
            for (int i = 0; i < jsonKravpunkterTabell.length(); i++) {
                JSONObject jsonKravpunkt = (JSONObject) jsonKravpunkterTabell.get(i);
                DetaljerKravpunkterKort infoKort = new DetaljerKravpunkterKort(jsonKravpunkt);
                kravpunkterListe.add(infoKort);
            }
        } else {
            System.out.println("jsonKravpunkterTabell null " + TABELL_NAVN);
        }

        return kravpunkterListe;
    }

    public String getKravpunktTittel() {
        return kravpunktTittel;
    }

    public String getKravpunktKarakter() {
        return "Karakter: " + kravpunktKarakter;
    }
}

