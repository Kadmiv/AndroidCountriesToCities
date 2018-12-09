
package com.example.gaijin.countriestocities.dataclasses;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Geoname {

    @SerializedName("adminCode1")
    @Expose
    private String adminCode1;
    @SerializedName("lng")
    @Expose
    private String lng;
    @SerializedName("geonameId")
    @Expose
    private Integer geonameId;
    @SerializedName("toponymName")
    @Expose
    private String toponymName;
    @SerializedName("countryId")
    @Expose
    private String countryId;
    @SerializedName("fcl")
    @Expose
    private String fcl;
    @SerializedName("population")
    @Expose
    private Integer population;
    @SerializedName("code")
    @Expose
    private String countryCode;
    @SerializedName("name")
    @Expose
    private String name;
    @SerializedName("fclName")
    @Expose
    private String fclName;
    @SerializedName("adminCodes1")
    @Expose
    private AdminCodes1 adminCodes1;
    @SerializedName("name")
    @Expose
    private String countryName;
    @SerializedName("fcodeName")
    @Expose
    private String fcodeName;
    @SerializedName("adminName1")
    @Expose
    private String adminName1;
    @SerializedName("lat")
    @Expose
    private String lat;
    @SerializedName("fcode")
    @Expose
    private String fcode;

    public String getAdminCode1() {
        return adminCode1;
    }

    public void setAdminCode1(String adminCode1) {
        this.adminCode1 = adminCode1;
    }

    public String getLng() {
        return lng;
    }

    public void setLng(String lng) {
        this.lng = lng;
    }

    public Integer getGeonameId() {
        return geonameId;
    }

    public void setGeonameId(Integer geonameId) {
        this.geonameId = geonameId;
    }

    public String getToponymName() {
        return toponymName;
    }

    public void setToponymName(String toponymName) {
        this.toponymName = toponymName;
    }

    public String getCountryId() {
        return countryId;
    }

    public void setCountryId(String countryId) {
        this.countryId = countryId;
    }

    public String getFcl() {
        return fcl;
    }

    public void setFcl(String fcl) {
        this.fcl = fcl;
    }

    public Integer getPopulation() {
        return population;
    }

    public void setPopulation(Integer population) {
        this.population = population;
    }

    public String getCountryCode() {
        return countryCode;
    }

    public void setCountryCode(String countryCode) {
        this.countryCode = countryCode;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getFclName() {
        return fclName;
    }

    public void setFclName(String fclName) {
        this.fclName = fclName;
    }

    public AdminCodes1 getAdminCodes1() {
        return adminCodes1;
    }

    public void setAdminCodes1(AdminCodes1 adminCodes1) {
        this.adminCodes1 = adminCodes1;
    }

    public String getCountryName() {
        return countryName;
    }

    public void setCountryName(String countryName) {
        this.countryName = countryName;
    }

    public String getFcodeName() {
        return fcodeName;
    }

    public void setFcodeName(String fcodeName) {
        this.fcodeName = fcodeName;
    }

    public String getAdminName1() {
        return adminName1;
    }

    public void setAdminName1(String adminName1) {
        this.adminName1 = adminName1;
    }

    public String getLat() {
        return lat;
    }

    public void setLat(String lat) {
        this.lat = lat;
    }

    public String getFcode() {
        return fcode;
    }

    public void setFcode(String fcode) {
        this.fcode = fcode;
    }

    @Override
    public String toString() {
        return String.format(" name: %s\n fcodeName: %s\n countryCod: %s\n", countryName, fcodeName, countryCode);
    }

}
