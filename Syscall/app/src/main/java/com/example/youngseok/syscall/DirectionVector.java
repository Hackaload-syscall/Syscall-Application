package com.example.youngseok.syscall;

public class DirectionVector {
    private double directionLat;
    private double directionLon;

    public double getDirectionLat() {
        return directionLat;
    }

    public double getDirectionLon() {
        return directionLon;
    }

    public void getDirection(double prevLat, double prevLon, double curLat, double curLon) {
        this.directionLat = curLat - prevLat;
        this.directionLon = curLon - prevLon;
    }
}
