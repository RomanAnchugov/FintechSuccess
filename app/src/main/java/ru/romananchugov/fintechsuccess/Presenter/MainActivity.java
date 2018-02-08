package ru.romananchugov.fintechsuccess.Presenter;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import ru.romananchugov.fintechsuccess.Model.ApiResponse;
import ru.romananchugov.fintechsuccess.R;
import ru.romananchugov.fintechsuccess.Service.APIClient;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";

    private Spinner currencyFromSpinner;
    private Spinner currencyToSpinner;
    private TextView answerTextView;
    private Button showExchangeRateButton;
    private String[] list;
    private String currencyFrom;
    private String currentTo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        list = getResources().getStringArray(R.array.drop_down);

        currencyFromSpinner = findViewById(R.id.spinner_from_currency);
        currencyFromSpinner.setSelection(31);
        currencyFromSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                Log.i(TAG, "onItemSelected(from):" + list[i]);
                currencyFrom = list[i];
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        currencyToSpinner = findViewById(R.id.spinner_to_currency);
        currencyToSpinner.setSelection(26);
        currencyToSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                Log.i(TAG, "onItemSelected(To):" + list[i]);
                currentTo = list[i];
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        answerTextView = findViewById(R.id.text_view_answer);
        showExchangeRateButton = findViewById(R.id.button_execute);
        showExchangeRateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //TODO: checking last item date in sharepref
                if(currencyFrom != null && currentTo != null && !currencyFrom.equals(currentTo)){
                    makeRequest();
                }
            }
        });

    }

    public void makeRequest(){
        APIClient apiClient = new APIClient();
        apiClient.getClient().getJson(currencyFrom, currentTo)
                .enqueue(new Callback<ApiResponse>() {
                    @Override
                    public void onResponse(Call<ApiResponse> call, Response<ApiResponse> response) {
                        Log.i(TAG, "onResponse: " + response.body().getRates().getRate() + " " + response.toString());
                        answerTextView.setText(getResources().getString(R.string.exchange_rate,
                                response.body().getRates().getRate()));
                        //TODO: add new shared pref
                    }

                    @Override
                    public void onFailure(Call<ApiResponse> call, Throwable t) {
                        Log.i(TAG, "onFailure: " + t.getMessage());
                    }
                });
    }
}
