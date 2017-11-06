package com.example.youngseok.syscall;

public class Brand {
    private String name;
    private int logo;
    private int id;

    Brand(String name, int logo, int id) {
        this.name = name;
        this.logo = logo;
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public int getLogo() {
        return logo;
    }

    public int getId() { return id; }
}
