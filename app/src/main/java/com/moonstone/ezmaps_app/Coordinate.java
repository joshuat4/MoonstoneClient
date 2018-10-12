package com.moonstone.ezmaps_app;

public class Coordinate {

    double lat;
    double lng;

    public Coordinate(double lat, double lng){
        this.lat = lat;
        this.lng = lng;
    }

    public double getLat(){
        return lat;
    }

    public double getLng(){
        return lng;
    }

    @Override
    public String toString() {
        return "Lat: " + lat + ", Lng: " + lng;
    }

}
