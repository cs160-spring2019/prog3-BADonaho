package com.example.cs160_sp18.prog3;

public class StatueButton {

    public String statueName;
    public int distance;
    public String imageFilename;

    StatueButton(String statueName, String imageFilename) {
        this.statueName = statueName;
        this.imageFilename = imageFilename;
    }

    public void changeDistance(double distance) {
        this.distance = (int) Math.round(distance);
    }

}
