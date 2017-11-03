package com.example.youngseok.syscall;

public class CalculateVelocity {
    private double prevLat, prevLon;
    private double curLat, curLon;

    public double getVelocity(double prevLat, double prevLon, double curLat, double curLon) {
        this.prevLat = prevLat; this.prevLon = prevLon;
        this.curLat = curLat; this.curLon = curLon;


        return calcDistance(); // m/s
//        return ((calcDistance() * 3600) / 1000); // km/h
    }

    private double calcDistance() {
        double theta, dist;
        theta = prevLon - curLon;
        dist = Math.sin(degToRad(prevLat)) * Math.sin(degToRad(curLat)) + Math.cos(degToRad(prevLat)) * Math.cos(degToRad(curLat)) * Math.cos(degToRad(theta));
        dist = Math.acos(dist);
        dist = radToDeg(dist);

        dist = dist * 60 * 1.1515;
        dist = dist * 1.609344;
        dist = dist * 1000.0;

        return dist;
    }

    private double degToRad(double deg) {
        return (double) (deg * Math.PI / (double) 180d);
    }

    private double radToDeg(double rad) {
        return (double) (rad * (double) 180d / Math.PI);
    }
}
