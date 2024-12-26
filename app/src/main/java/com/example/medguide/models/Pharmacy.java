package com.example.medguide.models;
public class Pharmacy {
    private final String name;
    private final String address;

    public Pharmacy(String name, String address) {
        this.name = name;
        this.address = address;
    }

    public String getName() {
        return name;
    }

    public String getAddress() {
        return address;
    }
}
