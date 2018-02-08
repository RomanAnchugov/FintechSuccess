package ru.romananchugov.fintechsuccess.Model;

/**
 * Created by romananchugov on 08.02.2018.
 */

public class RateObject {

    private String name;
    private double rate;

    public RateObject(String name, double rate) {
        this.name = name;
        this.rate = rate;
    }

    public String getName() {
        return name;
    }

    public double getRate() {
        return rate;
    }
}
