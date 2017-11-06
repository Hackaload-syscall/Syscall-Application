package com.example.youngseok.syscall;

public class Classification {
    private String classification;
    private int id;

    Classification(String classification, int id) {
        this.classification = classification;
        this.id = id;
    }

    public String getClassification() { return classification; }

    public int getId() { return id; }
}
