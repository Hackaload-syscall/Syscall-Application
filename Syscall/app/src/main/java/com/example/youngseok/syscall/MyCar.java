package com.example.youngseok.syscall;

public class MyCar {
    private double latitude;
    private double longitude;
    private double dirLat;
    private double dirLon;

    MyCar(double latitude, double longitude, double dirLat, double dirLon) {
        this.latitude = latitude;
        this.longitude = longitude;
        this.dirLat = dirLat;
        this.dirLon = dirLon;
    }

    public double getLatitude() { return latitude; }
    public double getLongitude() { return longitude; }
    public double getDirLat() { return dirLat; }
    public double getDirLon() { return dirLon; }
}
