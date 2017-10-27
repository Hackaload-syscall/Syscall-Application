package com.example.youngseok.syscall;

public class Brand {
    private String name;
    private int logo;

    Brand(String name, int logo) {
        this.name = name;
        this.logo = logo;
    }

    public String getName() {
        return name;
    }

    public int getLogo() {
        return logo;
    }
}
