package com.example.youngseok.syscall;

public class Angle {
    private MyCar myCar;
    private OtherCar otherCar;

    Angle(MyCar myCar, OtherCar otherCar) {
        this.myCar = myCar;
        this.otherCar = otherCar;
    }

    private double calcAngle() {
        double dirLat = myCar.getDirLat(); double dirLon = myCar.getDirLon();
        double otherLat = otherCar.getLatitude(); double otherLon = otherCar.getLongitude();
        double angle = Math.acos(dirLon / Math.sqrt(dirLat*dirLat + dirLon*dirLon));

        otherLat = otherLat - myCar.getLatitude();
        otherLon = otherLon - myCar.getLongitude();

        double otherConvertLat = otherLat * Math.cos(angle) - otherLon * Math.sin(angle);
        double otherConvertLon = otherLat * Math.sin(angle) + otherLon * Math.cos(angle);

        double containedAngle = Math.acos(otherConvertLon / Math.sqrt(otherConvertLat*otherConvertLat + otherConvertLon*otherConvertLon));

        return containedAngle;
    }

    public String getAngleWithOtherCar() {
        double angle = Math.toDegrees(calcAngle());
        String angleWithOtherCar;

        if(345 < angle && angle <= 15) angleWithOtherCar = "12시 방향";
        else if(15 < angle && angle <= 45) angleWithOtherCar = "1시 방향";
        else if(45 < angle && angle <= 75) angleWithOtherCar = "2시 방향";
        else if(75 < angle && angle <= 105) angleWithOtherCar = "3시 방향";
        else if(105 < angle && angle <= 145) angleWithOtherCar = "4시 방향";
        else if(145 < angle && angle <= 175) angleWithOtherCar = "5시 방향";
        else if(175 < angle && angle <= 205) angleWithOtherCar = "6시 방향";
        else if(205 < angle && angle <= 235) angleWithOtherCar = "7시 방향";
        else if(235 < angle && angle <= 265) angleWithOtherCar = "8시 방향";
        else if(265 < angle && angle <= 295) angleWithOtherCar = "9시 방향";
        else if(295 < angle && angle <= 315) angleWithOtherCar = "10시 방향";
        else if(315 < angle && angle <= 345) angleWithOtherCar = "11시 방향";
        else angleWithOtherCar = "";

        return angleWithOtherCar;
    }
}
