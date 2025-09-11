package com.example.pricingscheduler;

public class Tick {
    private double price;
    private double volume;

    public Tick(double price, double volume) {
        this.price = price;
        this.volume = volume;
    }

    public double getPrice() {
        return price;
    }

    public double getVolume() {
        return volume;
    }
}
