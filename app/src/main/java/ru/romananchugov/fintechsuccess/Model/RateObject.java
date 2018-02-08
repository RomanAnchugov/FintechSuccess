package ru.romananchugov.fintechsuccess.Model;

/**
 * Created by romananchugov on 08.02.2018.
 */

public class RateObject {

        public RateObject(String name, double rate) {
            this.name = name;
            this.rate = rate;
        }

        String name;
        double rate;

    public String getName() {
        return name;
    }

    public double getRate() {
        return rate;
    }
}
