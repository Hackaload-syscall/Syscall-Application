package com.example.youngseok.syscall;

public class Color {
    private String name;
    private int color;

    Color(String name, int color) {
        this.name = name;
        this.color = color;
    }

    public String getName() { return name; }

    public int getColor() { return color; }
}
