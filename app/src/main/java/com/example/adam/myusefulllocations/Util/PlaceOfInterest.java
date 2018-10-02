package com.example.adam.myusefulllocations.Util;

public class PlaceOfInterest {

    private int _id;
    private String address;
    private double latitude;
    private double longitude;
    private String name;
    private String photoUrl;
    private double distance;


    //TODO NEEDS TO CHANGE ALL DB AND LOCATION CLASSES ACCORDING THE NEW COLUMNS!!9חח


    public PlaceOfInterest(String address, double latitude, double longitude, String name, String photoUrl, double distance) {
        this.address = address;
        this.latitude = latitude;
        this.longitude = longitude;
        this.name = name;
        this.photoUrl = photoUrl;
        this.distance = distance;
    }

    public PlaceOfInterest(int _id, String address, float latitude, float longitude, String name, String photoUrl, float distance) {
        this._id = _id;
        this.address = address;
        this.latitude = latitude;
        this.longitude = longitude;
        this.name = name;
        this.photoUrl = photoUrl;
        this.distance = distance;

    }

    public double getDistance() {
        return distance;
    }

    public void setDistance(float distance) {
        this.distance = distance;
    }

    public int get_id() {
        return _id;
    }

    public void set_id(int _id) {
        this._id = _id;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(float latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(float longitude) {
        this.longitude = longitude;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPhotoUrl() {
        return photoUrl;
    }

    public void setPhotoUrl(String photoUrl) {
        this.photoUrl = photoUrl;
    }
}
