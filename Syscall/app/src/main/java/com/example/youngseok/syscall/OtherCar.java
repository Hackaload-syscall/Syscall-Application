package com.example.youngseok.syscall;

public class OtherCar {
    private int brand;
    private int classification;
    private int color;
    private double latitude;
    private double longitude;
    private double vectorLat;
    private double vectorLon;
    private int beginner;
    private int drowsy;

    OtherCar(int brand, int classification, int color, double latitude, double longitude,
             double vectorLat, double vectorLon, int beginner, int drowsy) {
        this.brand = brand;
        this.classification = classification;
        this.color = color;
        this.latitude = latitude;
        this.longitude = longitude;
        this.vectorLat = vectorLat;
        this.vectorLon = vectorLon;
        this.beginner = beginner;
        this.drowsy = drowsy;
    }

    public int getBrand() { return brand; }
    public int getClassification() { return classification; }
    public int getColor() { return color; }
    public double getLatitude() { return latitude; }
    public double getLongitude() { return longitude; }
    public double getVectorLat() { return vectorLat; }
    public double getVectorLon() { return vectorLon; }
    public int getBeginner() { return beginner; }
    public int getDrowsy() { return drowsy; }
}
