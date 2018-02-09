package ru.romananchugov.fintechsuccess.Model;

import com.google.gson.annotations.Expose;

/**
 * Created by romananchugov on 08.02.2018.
 */

public class ApiResponse {

    @Expose
    private String base;

    @Expose
    private String date;

    @Expose
    private RateObject rates;

    public String getBase() {
        return base;
    }

    public String getDate() {
        return date;
    }

    public RateObject getRates() {
        return rates;
    }
}
