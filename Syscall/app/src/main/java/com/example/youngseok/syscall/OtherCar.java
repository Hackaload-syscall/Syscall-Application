package com.example.youngseok.syscall;

public class OtherCar {
    private int brand;
    private int classfication;
    private int color;
    private double latitude;
    private double longitude;
    private double vectorLat;
    private double vectorLon;
    private int beginner;
    private int drowsy;

    OtherCar(int brand, int classfication, int color, double latitude, double longitude,
             double vectorLat, double vectorLon, int beginner, int drowsy) {
        this.brand = brand;
        this.classfication = classfication;
        this.color = color;
        this.latitude = latitude;
        this.longitude = longitude;
        this.vectorLat = vectorLat;
        this.vectorLon = vectorLon;
        this.beginner = beginner;
        this.drowsy = drowsy;
    }

    public int getBrand() { return brand; }
    public int getClassfication() { return getClassfication(); }
    public int getColor() { return color; }
    public double getLatitude() { return latitude; }
    public double getLongitude() { return longitude; }
    public double getVectorLat() { return vectorLat; }
    public double getVectorLon() { return vectorLon; }
    public int getBeginner() { return beginner; }
    public int getDrowsy() { return drowsy; }
}
