package com.example.gaijin.countriestocities.dataclasses;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class CountryInfo {

    @SerializedName("flag")
    @Expose
    private String flag;
    @SerializedName("name")
    @Expose
    private String name;
    @SerializedName("alpha2Code")
    @Expose
    private String alpha2Code;

    public String getFlag() {
        return flag;
    }

    public void setFlag(String flag) {
        this.flag = flag;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAlpha2Code() {
        return alpha2Code;
    }

    public void setAlpha2Code(String alpha2Code) {
        this.alpha2Code = alpha2Code;
    }

    public String toString() {
        return String.format("Name: %s Code: %s\nFlag: %s", name, alpha2Code, flag);
    }

}