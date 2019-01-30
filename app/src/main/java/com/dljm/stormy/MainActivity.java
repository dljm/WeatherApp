package com.dljm.stormy;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

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

        //ensure the network is available before attempting to use the network
        if(isNetworkAvailable()) {
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
                        Log.v(TAG, response.body().string());
                        if (response.isSuccessful()) {

                        } else {
                            alertUserAboutError(getString(R.string.error_message));
                        }
                    } catch (IOException e) {
                        Log.e(TAG, "IO Exception caught:", e);
                    }
                }//end onResponse
            });//end call.enqueue
        }//end if isNetworkAvailable
    }//end onCreate

    /*
        Helper Functions for onCreate method
     */
    //check if internet is available return true if connected
    private boolean isNetworkAvailable() {
        //get current network connectivity information
        ConnectivityManager manager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = manager.getActiveNetworkInfo(); //requires manifest permission

        boolean isAvailable = false;
        if(networkInfo != null && networkInfo.isConnected()){
            isAvailable = true;
        }
        else{
            alertUserAboutError(getString(R.string.network_unavailable_message));
        }
        return isAvailable;
    }//end isNetworkAvailable

    //use a dialog fragment to display errors to user
    private void alertUserAboutError(String message) {
        final Bundle args = new Bundle();
        args.putString("message_key", message);
        AlertDialogFragment dialog = new AlertDialogFragment();
        dialog.setArguments(args);
        dialog.show(getSupportFragmentManager(), "error_dialog");
    }//end alertUser
}//end MainActivity
