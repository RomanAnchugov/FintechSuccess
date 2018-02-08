package ru.romananchugov.fintechsuccess;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import ru.romananchugov.fintechsuccess.Model.ApiResponse;
import ru.romananchugov.fintechsuccess.Service.APIClient;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        APIClient apiClient = new APIClient();
        apiClient.getClient().getJson("RUB", "USD")
                .enqueue(new Callback<ApiResponse>() {
                    @Override
                    public void onResponse(Call<ApiResponse> call, Response<ApiResponse> response) {
                        Log.i(TAG, "onResponse: " + response.body().getRates().getRate());
                    }

                    @Override
                    public void onFailure(Call<ApiResponse> call, Throwable t) {
                        Log.i(TAG, "onFailure: ");
                    }
                });


    }
}
