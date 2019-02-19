package com.dljm.stormy;

import android.content.Context;
import android.databinding.DataBindingUtil;
import android.graphics.drawable.Drawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.method.LinkMovementMethod;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.dljm.stormy.databinding.ActivityMainBinding;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class MainActivity extends AppCompatActivity {

    public static final String TAG = MainActivity.class.getSimpleName();

    private CurrentWeather currentWeather;
    private ImageView iconImageView;
    private ActivityMainBinding binding;

    final double latitude = 46.3091;
    final double longitude = -79.4608;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //set up data binding class for activity_main.xml
        binding = DataBindingUtil.setContentView(MainActivity.this, R.layout.activity_main);
        //on app startup show a loading screen for first weather request
        findViewById(R.id.loadingPanel).setVisibility(View.VISIBLE);
        getForecast(latitude, longitude);
    }//end onCreate
    /*
        Helper Functions for onCreate method
     */
    // @summary - makes the call to Dark Sky to get forecast data and updates Data model contents
    private void getForecast(double latitude, double longitude) {
        //add the dark sky attribution link
        TextView darkSky = findViewById(R.id.darkskyAttribution);
        darkSky.setMovementMethod(LinkMovementMethod.getInstance());
        //dynamically update image icon in UI
        iconImageView = findViewById(R.id.iconImageView);

        String apiKey = "0afd324ed5cce01c640d7d78fe0d7fb9";
        String forecastURL = "https://api.darksky.net/forecast/" + apiKey + "/" + latitude + "," + longitude +
                "?exclude=minutely,flags&units=ca";

        //ensure the network is available before attempting to use the network
        if(isNetworkAvailable()) {
            OkHttpClient client = new OkHttpClient();
            Request request = new Request.Builder().url(forecastURL).build();

            Call call = client.newCall(request);
            call.enqueue(new Callback() {
                @Override
                public void onFailure(Call call, IOException e) {
                    Log.e(TAG, "IO Exception caught", e);
                    alertUserAboutError("Error connecting to server. Please tap the refresh icon to try again.");
                }//end onFailure
                @Override
                public void onResponse(Call call, Response response) throws IOException {
                    try {
                        String jsonData = response.body().string();
                        Log.v(TAG, jsonData);
                        if (response.isSuccessful()) {
                            //get the forecast details and parse the JSON object
                            currentWeather = getCurrentDetails(jsonData);

                            //bind data to new binding variable
                            binding.setWeather(currentWeather);

                            //update the weather image icon in the UI this must be done on the Main thread
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    Drawable drawable = getResources().getDrawable(currentWeather.getIconId());
                                    iconImageView.setImageDrawable(drawable);
                                    //findViewById(R.id.loadingPanel).setVisibility(View.GONE);
                                }
                            });
                        } else {
                            alertUserAboutError(getString(R.string.error_message));
                        }
                    } catch (IOException e) {
                        Log.e(TAG, "IO Exception caught:", e);
                        alertUserAboutError("The weather data took too long to load, please tap refresh to try again.");
                    } catch(JSONException e){
                        Log.e(TAG, "JSON Exception caught:", e);
                        alertUserAboutError(getString(R.string.error_message));
                    }
                    //hide loading circles
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            findViewById(R.id.loadingPanel).setVisibility(View.GONE);
                            findViewById(R.id.refreshLoad).setVisibility(View.GONE);
                        }
                    });
                }//end onResponse
            });//end call.enqueue
        }//end if isNetworkAvailable
    }//end getForcast

    /*
    * parse the JSON object
    * @param string jsonData - the string of the JSON object received from Dark Sky
    * */
    private CurrentWeather getCurrentDetails(String jsonData) throws JSONException {

        JSONObject forecast = new JSONObject(jsonData);
        String timezone = forecast.getString("timezone");
        Log.i(TAG, "From JSON: " + timezone);

        JSONObject currently = forecast.getJSONObject("currently");
        CurrentWeather currentWeather = new CurrentWeather();
        currentWeather.setTimeZone(timezone);
        currentWeather.setHumidity(currently.getDouble("humidity"));
        currentWeather.setIcon(currently.getString("icon"));
        currentWeather.setTime(currently.getLong("time")); //UNIX time
        currentWeather.setSummary(currently.getString("summary"));
        currentWeather.setTemperature(currently.getDouble("temperature"));
        currentWeather.setPrecipChance(currently.getDouble("precipProbability"));
        //TODO add location dynamically
        currentWeather.setLocationLabel("North Bay, ON");

        return currentWeather;
    }//end getCurrentDetails

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
    //@param - String message - the message displayed in the error prompt
    private void alertUserAboutError(String message) {
        final Bundle args = new Bundle();
        args.putString("message_key", message);
        AlertDialogFragment dialog = new AlertDialogFragment();
        dialog.setArguments(args);
        dialog.show(getSupportFragmentManager(), "error_dialog");
    }//end alertUser

    //adds button functionality to refresh icon in UI
    public void refreshOnClick(View view){
        findViewById(R.id.refreshLoad).setVisibility(View.VISIBLE);
        getForecast(latitude, longitude);
    }//end refreshOnClick
}//end MainActivity
