package ru.romananchugov.fintechsuccess.Model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by romananchugov on 08.02.2018.
 */

public class ApiResponse {
    @SerializedName("base")
    @Expose
    private String base;
    @SerializedName("date")
    @Expose
    private String date;
    @SerializedName("rates")
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
