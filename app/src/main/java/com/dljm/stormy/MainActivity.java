package com.dljm.stormy;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class MainActivity extends AppCompatActivity {

    public static final String TAG = MainActivity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        String apiKey = "0afd324ed5cce01c640d7d78fe0d7fb9";
        double latitude = 37.8267;
        double longitude = -122.4233;
        String forecastURL = "https://api.darksky.net/forecast/" + apiKey + "/" + latitude + "," + longitude;

        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder().url(forecastURL).build();

        Call call = client.newCall(request);
        call.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.e(TAG, "IO Exception caught", e);
            }//end onFailure
            @Override
            public void onResponse(Call call, Response response) throws IOException {
                try {

                    if(response.isSuccessful()){
                        //simply log the response in the Logcat terminal
                        Log.v(TAG, response.body().string());
                    }
                } catch (IOException e) {
                    Log.e(TAG, "IO Exception caught:" , e);
                }
            }//end onResponse
        });//end call.enqueue
    }//end onCreate
}//end MainActivity
