package com.example.mattilsynet.SearchResult;

public class InfoCard {

    private String placeTitle;
    private String placeOrgNum;
    private String placeAddress;
    private String placeZipCode;
    private String placeZipName;
    private int placeGrade;

    public InfoCard(String placeTitle, String placeOrgNum, String placeAddress, String placeZipCode, String placeZipName) {
        this.placeTitle = placeTitle;
        this.placeOrgNum = placeOrgNum;
        this.placeAddress = placeAddress;
        this.placeZipCode = placeZipCode;
        this.placeZipName = placeZipName;
        //this.placeGrade = placeGrade;
    }

    public InfoCard() {}

    public String getPlaceTitle() {
        return this.placeTitle;
    }

    public String getPlaceOrgNum() {
        return this.placeOrgNum;
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
