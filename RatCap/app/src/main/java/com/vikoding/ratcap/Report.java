package com.vikoding.ratcap;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

/**
 * @author Matias Szylkowski
 * This class represents a report in the list
 */

public class Report implements Serializable {
    private String address;
    private String bor;
    private String city;
    private String date;
    private float lat;
    private float lon;
    private String locType;
    private String zip;

    /**
     * Create an instance of the report with all the attributes empty
     */
    public Report() {
        this("", "", "", "", 0, 0, "", "");
    }

    /**
     * Create an instance of Report with the given parameters
     * @param address
     * @param borough
     * @param city
     * @param date
     * @param latitude
     * @param longitude
     * @param locType
     * @param zip
     */
    public Report(String address, String borough, String city, String date, float latitude, float longitude, String locType, String zip) {
        this.address = address;
        this.bor = borough;
        this.city = city;
        this.date = date;
        this.lat = latitude;
        this.lon = longitude;
        this.locType = locType;
        this.zip = zip;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getBor() {
        return bor;
    }

    public void setBor(String bor) {
        this.bor = bor;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public float getLat() {
        return lat;
    }

    public void setLat(float lat) {
        this.lat = lat;
    }

    public float getLon() {
        return lon;
    }

    public void setLon(float longitude) {
        this.lon = longitude;
    }

    public String getLocType() {
        return locType;
    }

    public void setLocType(String locType) {
        this.locType = locType;
    }

    public String getZip() {
        return zip;
    }

    public void setZip(String zipCode) {
        this.zip = zipCode;
    }

    public Map<String, Object> toMap() {
        HashMap<String, Object> result = new HashMap<>();
        result.put("address", address);
        result.put("bor", bor);
        result.put("city", city);
        result.put("date", date);
        result.put("lat", lat);
        result.put("lon", lon);
        result.put("locType", locType);
        result.put("zip", zip);

        return result;
    }

}
