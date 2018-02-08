package ru.romananchugov.fintechsuccess.Presenter;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import ru.romananchugov.fintechsuccess.Model.ApiResponse;
import ru.romananchugov.fintechsuccess.Model.DataStorageObject;
import ru.romananchugov.fintechsuccess.R;
import ru.romananchugov.fintechsuccess.Service.APIClient;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";

    private Spinner currencyFromSpinner;
    private Spinner currencyToSpinner;
    private TextView answerTextView;
    private Button showExchangeRateButton;
    private String[] list;//list of dropdown
    private String currencyFrom;
    private String currentTo;

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
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

                SharedPreferences sharedPreferences = getPreferences(Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPreferences.edit();

                //reading prefs
                SharedPreferences pref = getPreferences(Context.MODE_PRIVATE);
                String jsonText = pref.getString(getString(R.string.pref_key), "error");
                java.lang.reflect.Type type = new TypeToken<ArrayList<DataStorageObject>>(){}.getType();

                //checking existence of prefs
                if(!jsonText.equals("error")) {

                    ArrayList<DataStorageObject> arrayList = new Gson()
                            .fromJson(jsonText, type);
                    String date = new SimpleDateFormat("yyyy-MM-dd")
                            .format(Calendar.getInstance().getTime());//date

                    //finding coincidence with data in pref
                    for(DataStorageObject obj: arrayList){
                        if(obj.getFrom().equals(currencyFrom)
                                && obj.getTo().equals(currentTo)){
                            Log.i(TAG, "onClick: found concrete item");

                            //date is not fresh
                            if(!obj.getDate().equals(date)) {
                                Log.i(TAG, "onClick: this item is out of date - updating");
                                arrayList.remove(obj);
                                editor.clear();
                                editor.putString(getString(R.string.pref_key),
                                        new Gson().toJson(arrayList));
                                editor.apply();
                                editor.commit();
                                makeRequest();
                            }else{//date is fresh
                                Log.i(TAG, "onClick: item is up-to-date(on click if) - just reading from pref");
                                answerTextView.setText(getString(R.string.exchange_rate, obj.getValue()));
                            }
                            return;
                        }
                    }

                    //if didn't find any coincidences
                    Log.i(TAG, "onClick: didn't find any coincidences - creating new item in pref");
                    makeRequest();

                }else if(currencyFrom != null && currentTo != null && !currencyFrom.equals(currentTo)){//first launch
                    Log.i(TAG, "onClick: first launch of the app - creating new pref's");
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

                        //reading prefs
                        SharedPreferences pref = getPreferences(Context.MODE_PRIVATE);
                        String jsonText = pref.getString(getString(R.string.pref_key), "error");
                        java.lang.reflect.Type type = new TypeToken<ArrayList<DataStorageObject>>(){}.getType();

                        ArrayList arrayList = null;

                        //checking existence
                        if(!jsonText.equals("error")) {
                            arrayList = new Gson().fromJson(jsonText, type);
                        }else{//first launch
                            arrayList = new ArrayList();
                        }

                        //create new obj for pref
                        DataStorageObject dataStorageObject =
                                new DataStorageObject(response.body().getBase() + ""
                                        , response.body().getRates().getName() + ""
                                        , response.body().getDate() + ""
                                        , response.body().getRates().getRate());

                        arrayList.add(dataStorageObject);

                        SharedPreferences.Editor editor = pref.edit();
                        editor.putString(getString(R.string.pref_key), new Gson().toJson(arrayList));
                        editor.apply();
                        editor.commit();

                        Log.i(TAG, "onResponse: " + "added new obj(made request)");
                    }

                    @Override
                    public void onFailure(Call<ApiResponse> call, Throwable t) {
                        Log.i(TAG, "onFailure: " + t.getMessage());
                    }
                });
    }
}
