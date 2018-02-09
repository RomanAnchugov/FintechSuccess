package ru.romananchugov.fintechsuccess.Presenter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.lang.reflect.Type;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import ru.romananchugov.fintechsuccess.Model.ApiResponse;
import ru.romananchugov.fintechsuccess.Model.DataStorageObject;
import ru.romananchugov.fintechsuccess.R;
import ru.romananchugov.fintechsuccess.Service.APIClient;

@SuppressLint("StaticFieldLeak")

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";

    private Spinner currencyFromSpinner;
    private Spinner currencyToSpinner;
    private TextView answerTextView;
    private Button showExchangeRateButton;
    private ProgressBar progressBar;

    private String[] list;//list of dropdown
    private String currencyFrom;
    private String currencyTo;

    private Type typeOfData;
    private String currentDate;
    private String dataPath;

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        typeOfData = new TypeToken<ArrayList<DataStorageObject>>(){}.getType();
        currentDate = new SimpleDateFormat("yyyy-MM-dd", Locale.ROOT)
                .format(Calendar.getInstance().getTime());
        dataPath = getString(R.string.pref_path);

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
                currencyTo = list[i];
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
                new AsyncAnalyzing().execute();
            }
        });

        progressBar = findViewById(R.id.progress_bar);
        progressBar.setVisibility(View.GONE);

    }

    //async class for loading file, check existence of them and analyse them
    private class AsyncAnalyzing extends AsyncTask<Void, Void, DataStorageObject>{

        @Override
        protected void onPreExecute() {
            progressBar.setVisibility(View.VISIBLE);
            Log.i(TAG, "onPreExecute: asyncAnalise");
        }

        @Override
        protected DataStorageObject doInBackground(Void... voids) {
            Log.i(TAG, "doInBackground: async analise of file existing and first launch, etc.");
            
            String file = readFile(dataPath);

            //checking existence and first launch
            if(file == null && currencyFrom != null && currencyTo != null && !currencyFrom.equals(currencyTo)){
                Log.i(TAG, "onClick: first launch - create new file");
                makeRequest();
            }else if(!currencyFrom.equals(currencyTo)){
                Log.i(TAG, "onClick: file is existing");
                ArrayList<DataStorageObject> list = new Gson().fromJson(file, typeOfData);

                if (list != null) {
                    //finding coincidence in file
                    for(DataStorageObject obj: list){
                        if(obj.getFrom().equals(currencyFrom) && obj.getTo().equals(currencyTo)){
                            Log.i(TAG, "onClick: found concrete item in file");

                            //up-to-currentDate checking
                            if(!obj.getDate().equals(currentDate)){
                                Log.i(TAG, "onClick: found out-of-currentDate item, deleted them and load new - " + currentDate);
                                list.remove(obj);
                                saveFile(dataPath, new Gson().toJson(list));
                                makeRequest();
                            }else{//everything correct
                                Log.i(TAG, "onClick: found correct fresh item, don't need to load new");
                                return obj;
                            }
                            return null;
                        }
                    }
                }

                //load new element
                Log.i(TAG, "onClick: didn't find any coincidences in file, load new");
                makeRequest();
            }
            return null;
        }

        @Override
        protected void onPostExecute(DataStorageObject obj) {
            if(obj != null){
                answerTextView.setText(getString(R.string.exchange_rate, obj.getValue()));
                progressBar.setVisibility(View.GONE);
            }
        }
    }

    //async class for set-up new loaded data to the file
    private class AsyncUpdate extends AsyncTask<Response<ApiResponse>, Void, Response<ApiResponse>>{

        @Override
        protected void onPreExecute() {
            progressBar.setVisibility(View.VISIBLE);
            Log.i(TAG, "onPreExecute: asyncUpdate ");

        }

        @Override
        protected Response<ApiResponse> doInBackground(Response<ApiResponse>[] responses) {
            Log.i(TAG, "doInBackground: async set-up new info");
            Response<ApiResponse> response = responses[0];

            String file = readFile(dataPath);
            ArrayList arrayList;
            //create new obj for stored data
            DataStorageObject dataStorageObject =
                    new DataStorageObject(response.body().getBase() + ""
                            , response.body().getRates().getName() + ""
                            ,  currentDate
                            , response.body().getRates().getRate());

            //checking existence and first launch
            if(file == null){
                arrayList = new ArrayList();
                arrayList.add(dataStorageObject);
                String text = new Gson().toJson(arrayList);
                saveFile(getString(R.string.pref_path), text);
                Log.i(TAG, "onResponse: created new file with - " + text);
            }else{
                arrayList = new Gson().fromJson(file, typeOfData);
                arrayList.add(dataStorageObject);
                String text = new Gson().toJson(arrayList);
                saveFile(getString(R.string.pref_path), text);
                Log.i(TAG, "onResponse: added to existing file - " + text);
            }
            return response;
        }

        @Override
        protected void onPostExecute(Response<ApiResponse> response) {
            answerTextView.setText(getString(R.string.exchange_rate,
                    response.body().getRates().getRate()));

            progressBar.setVisibility(View.GONE);
        }
    }

    public void makeRequest(){
        APIClient apiClient = new APIClient();
        apiClient.getClient().getJson(currencyFrom, currencyTo)
                .enqueue(new Callback<ApiResponse>() {

                    @Override
                    public void onResponse(Call<ApiResponse> call, Response<ApiResponse> response) {
                        Log.i(TAG, "onResponse: loaded new element");
                        new AsyncUpdate().execute(response);
                    }

                    @Override
                    public void onFailure(Call<ApiResponse> call, Throwable t) {
                        Log.i(TAG, "onFailure: " + t.getMessage());
                    }
                });
    }

    public void saveFile(String file, String text){
        try {
            FileOutputStream out = openFileOutput(file , Context.MODE_PRIVATE);
            out.write(text.getBytes());
            out.close();
            Log.i(TAG, "saveFile: " + text + " saved");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public String readFile(String file){
        String text = null;

        try {
            FileInputStream in = openFileInput(file);
            byte[] buffer = new byte[in.available()];
            in.read(buffer);
            in.close();
            text = new String(buffer);
            Log.i(TAG, "readFile: " + text + " read");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return text;
    }

}//TODO: more tests, asynch - add progress bar
