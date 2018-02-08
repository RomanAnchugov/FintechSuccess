package ru.romananchugov.fintechsuccess.Service;

import com.google.gson.GsonBuilder;

import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.GET;
import retrofit2.http.Query;
import ru.romananchugov.fintechsuccess.Model.ApiResponse;
import ru.romananchugov.fintechsuccess.Model.RateObject;

/**
 * Created by romananchugov on 08.02.2018.
 */

public class APIClient {

    private static final String BASE_URL = "http://api.fixer.io/";

    public interface APIInterface {

        @GET("latest")
        Call<ApiResponse> getJson(@Query("base") String base, @Query("symbols") String symbols);
    }

    public APIInterface getClient(){
        GsonBuilder gsonBuilder = new GsonBuilder();
        gsonBuilder.registerTypeAdapter(RateObject.class, new RatesDeserializer());


        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create(gsonBuilder.create()))
                .build();
        return retrofit.create(APIInterface.class);
    }



}
