package com.example.mattilsynet.DetaljertVisning;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class DetaljerKravpunkterKort {

    private String kravpunktTittel;
    private String kravpunktKarakter;

    //Navn for datahenting i JSONtabell
    static final String TABELL_NAVN         = "entries";
    static final String KRAVPUNKT_TITTEL    = "kravpunktnavn_no";
    static final String STED_ORGNR          = "karakter";

    public DetaljerKravpunkterKort(JSONObject jsonPost) {
        this.kravpunktTittel = jsonPost.optString(KRAVPUNKT_TITTEL);
        this.kravpunktKarakter = jsonPost.optString(STED_ORGNR);
    }

    //Metode som returnerer en ArrayList med Infokort som settes inn i recyclerview
    public static ArrayList<DetaljerKravpunkterKort> lagKravpunktKort(String jsonPostString)
            throws JSONException, NullPointerException {

        //Lager en Arraylist
        ArrayList<DetaljerKravpunkterKort> kravpunkterListe = new ArrayList<>();
        //Lager en instans av et JSONobjekt
        JSONObject jsonData  = new JSONObject(jsonPostString);
        //Bruker JSONobjekt til å lage et JSONarray som brukes til å hente ut data
        JSONArray jsonKravpunkterTabell = jsonData.optJSONArray("entries");

        if(jsonKravpunkterTabell != null)
        {
            for (int i = 0; i < jsonKravpunkterTabell.length(); i++) {
                //Henter ut data for et og et Kravpunkt og legger inn i Array
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

