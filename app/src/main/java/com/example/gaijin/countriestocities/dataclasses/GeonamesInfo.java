
package com.example.gaijin.countriestocities.dataclasses;

import java.util.List;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class GeonamesInfo {

    @SerializedName("totalResultsCount")
    @Expose
    private Integer totalResultsCount;
    @SerializedName("geonames")
    @Expose
    private List<Geoname> geonames = null;

    public Integer getTotalResultsCount() {
        return totalResultsCount;
    }

    public void setTotalResultsCount(Integer totalResultsCount) {
        this.totalResultsCount = totalResultsCount;
    }

    public List<Geoname> getGeonames() {
        return geonames;
    }

    public void setGeonames(List<Geoname> geonames) {
        this.geonames = geonames;
    }

    @Override
    public String toString() {
        String info = "";
        for (Geoname geo : geonames) {
            info += geo.toString();
        }
        return info;
    }

}
