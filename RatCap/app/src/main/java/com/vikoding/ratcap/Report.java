package com.vikoding.ratcap;

import java.io.Serializable;

/**
 * Created by Mati on 10/9/2017.
 */

public class Report implements Serializable {
    private String address;
    private String bor;
    private String city;
    private String date;
    private float lat;
    private float longitude;
    private String locType;
    private String zip;

    public Report() {
        this("", "", "", "", 0, 0, "", "");
    }

    public Report(String address, String borough, String city, String date, float latitude, float longitude, String locType, String zip) {
        this.address = address;
        this.bor = borough;
        this.city = city;
        this.date = date;
        this.lat = latitude;
        this.longitude = longitude;
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
        return longitude;
    }

    public void setLon(float longitude) {
        this.longitude = longitude;
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

}
