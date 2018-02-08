package ru.romananchugov.fintechsuccess.Model;

/**
 * Created by romananchugov on 09.02.2018.
 */

public class DataStorageObject {
    String from;
    String to;
    String date;
    double value;

    public DataStorageObject(String from, String to, String date, double value) {
        this.from = from;
        this.to = to;
        this.date = date;
        this.value = value;
    }

    public String getFrom() {
        return from;
    }

    public String getTo() {
        return to;
    }

    public String getDate() {
        return date;
    }

    public double getValue() {
        return value;
    }
}
