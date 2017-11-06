package com.example.youngseok.syscall;

public class Color {
    private String name;
    private int color;
    private int id;

    Color(String name, int color, int id) {
        this.name = name;
        this.color = color;
        this.id = id;
    }

    public String getName() { return name; }

    public int getColor() { return color; }

    public int getId() { return id; }
}
