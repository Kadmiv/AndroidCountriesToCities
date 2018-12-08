
package com.example.gaijin.countriestocities.dataclasses;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class CountryAlphaCode {

    @SerializedName("Name")
    @Expose
    private String name;
    @SerializedName("Code")
    @Expose
    private String code;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

}
