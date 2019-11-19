package com.example.mattilsynet.SearchResult;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class InfoCard {

    private String placeTitle;
    private String placeOrgNum;
    private String placeDate;
    private String placeAddress;
    private String placeZipCode;
    private String placeZipName;
    private int placeGrade;

    static final String TABELL_NAVN         = "entries";
    static final String PLACE_TITLE         = "navn";
    static final String PLACE_ORGNUM        = "orgnummer";
    static final String PLACE_DATE          = "dato";
    static final String PLACE_ADDRESS       = "adrlinje1";
    static final String PLACE_ZIPCODE       = "postnr";
    static final String PLACE_ZIPNAME       = "poststed";
    static final String PLACE_GRADE         = "total_karakter";

    public InfoCard(String placeTitle, String placeOrgNum, String placeAddress, String placeZipCode, String placeZipName, int placeGrade) {
        this.placeTitle     = placeTitle;
        this.placeOrgNum    = placeOrgNum;
        this.placeAddress   = placeAddress;
        this.placeZipCode   = placeZipCode;
        this.placeZipName   = placeZipName;
        this.placeGrade     = placeGrade;
    }

    public InfoCard(JSONObject jsonPost) {
        this.placeTitle     = jsonPost.optString(PLACE_TITLE);
        this.placeOrgNum    = jsonPost.optString(PLACE_ORGNUM);
        this.placeDate      = jsonPost.optString(PLACE_DATE);
        this.placeAddress   = jsonPost.optString(PLACE_ADDRESS);
        this.placeZipCode   = jsonPost.optString(PLACE_ZIPCODE);
        this.placeZipName   = jsonPost.optString(PLACE_ZIPNAME);
        //this.placeGrade     = jsonPost.optString(PLACE_GRADE);
        //if (jsonPost.optInt(PLACE_GRADE) == 1) {}
        //this.placeGrade     = R.drawable.smilefjes;
    }

    public InfoCard() {}

    public static ArrayList<InfoCard> createInfoCard(String jsonPostString)
            throws JSONException, NullPointerException {

        ArrayList<InfoCard> postListe = new ArrayList<InfoCard>();
        JSONObject jsonData  = new JSONObject(jsonPostString);
        JSONArray jsonPostTabell = jsonData.optJSONArray("entries");

        if(jsonPostTabell != null)
        {
            for (int i = 0; i < jsonPostTabell.length(); i++) {
                JSONObject jsonPost = (JSONObject) jsonPostTabell.get(i);
                // jsonPost må matche verdiene i databasetabellen post
                InfoCard postKort = new InfoCard(jsonPost);
                postListe.add(postKort);
            }
        } else {
            System.out.println("jsonPostTabell null " + TABELL_NAVN);
        }

        return postListe;
    }

    public String getPlaceTitle() {
        return this.placeTitle;
    }

    public String getPlaceOrgNum() {
        return this.placeOrgNum;
    }

    public String getPlaceDate() {
        return this.placeDate;
    }

    public String getPlaceAddress() {
        return this.placeAddress;
    }

    public String getPlaceZipCode() {
        return this.placeZipCode;
    }

    public String getPlaceZipName() {
        return this.placeZipName;
    }

    public int getPlaceGrade() {
        return this.placeGrade;
    }
}
