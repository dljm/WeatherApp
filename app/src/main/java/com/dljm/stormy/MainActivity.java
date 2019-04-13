package com.dljm.stormy;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.databinding.DataBindingUtil;
import android.graphics.drawable.Drawable;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.method.LinkMovementMethod;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import com.dljm.stormy.databinding.ActivityMainBinding;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.List;
import java.util.Locale;


import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class MainActivity extends AppCompatActivity {

    public static final String TAG = MainActivity.class.getSimpleName();

    private CurrentWeather currentWeather;
    private HourlyWeather hourlyWeather;
    private DailyWeather dailyWeather;


    private ImageView iconImageView;
    private ActivityMainBinding binding;

    private FusedLocationProviderClient fusedLocationClient;
    private final int MY_PERMISSIONS_REQUEST_LOCATION = 0;
    private Location mLastLocation;
    //updated dynamically
    private double latitude = 46.3091;
    private double longitude = -79.4608;
    private String locality = "North Bay";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //set up location services
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        //set up data binding class for activity_main.xml
        binding = DataBindingUtil.setContentView(MainActivity.this, R.layout.activity_main);
        //on app startup show a loading screen for first weather request
        findViewById(R.id.loadingPanel).setVisibility(View.VISIBLE);
        getForecast(latitude, longitude);
    }//end onCreate

    @Override
    public void onStart() {
        super.onStart();
        if (!checkPermissions()) {
            requestPermissions();
        } else {
            getLastLocation();
        }
    }//end onStart

    /**
     * Return the current state of the permissions needed.
     */
    private boolean checkPermissions() {
        int permissionState = ActivityCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_COARSE_LOCATION);
        return permissionState == PackageManager.PERMISSION_GRANTED;
    }

    private void startLocationPermissionRequest() {
        ActivityCompat.requestPermissions(MainActivity.this,
                new String[]{Manifest.permission.ACCESS_COARSE_LOCATION},
                MY_PERMISSIONS_REQUEST_LOCATION);
    }

    private void requestPermissions() {
        boolean shouldProvideRationale =
                ActivityCompat.shouldShowRequestPermissionRationale(this,
                        Manifest.permission.ACCESS_COARSE_LOCATION);

        // Provide an additional rationale to the user. This would happen if the user denied the
        // request previously, but didn't check the "Don't ask again" checkbox.
        if (shouldProvideRationale) {
            Log.i(TAG, "Displaying permission rationale to provide additional context.");
            alertUserAboutError(getString(R.string.permission_rationale));
            startLocationPermissionRequest();

        } else {
            Log.i(TAG, "Requesting permission");
            // Request permission. It's possible this can be auto answered if device policy
            // sets the permission in a given state or the user denied the permission
            // previously and checked "Never ask again".
            startLocationPermissionRequest();
        }
    }

    /**
     * Callback received when a permissions request has been completed.
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        Log.i(TAG, "onRequestPermissionResult");
        if (requestCode == MY_PERMISSIONS_REQUEST_LOCATION) {
            if (grantResults.length <= 0) {
                // If user interaction was interrupted, the permission request is cancelled and you
                // receive empty arrays.
                Log.i(TAG, "User interaction was cancelled.");
            } else if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permission granted.
                getLastLocation();
            } else {
                // Permission denied.
                // Notify the user that they have rejected a core permission for the
                // app, which makes the Activity useless. In a real app, core permissions would
                // typically be best requested during a welcome-screen flow.

                // Additionally, it is important to remember that a permission might have been
                // rejected without asking the user for permission (device policy or "Never ask
                // again" prompts). Therefore, a user interface affordance is typically implemented
                // when permissions are denied. Otherwise, your app could appear unresponsive to
                // touches or interactions which have required permissions.
                alertUserAboutError(getString(R.string.permission_rationale));
            }
        }
    }//end onRequestPermission

    /**
     * Provides a simple way of getting a device's location and is well suited for
     * applications that do not require a fine-grained location and that do not need location
     * updates. Gets the best and most recent location currently available, which may be null
     * in rare cases when a location is not available.
     * <p>
     * Note: this method should be called after location permission has been granted.
     */
    @SuppressWarnings("MissingPermission")
    private void getLastLocation() {
        fusedLocationClient.getLastLocation()
                .addOnCompleteListener(this, new OnCompleteListener<Location>() {
                    @Override
                    public void onComplete(@NonNull Task<Location> task) {
                        if (task.isSuccessful() && task.getResult() != null) {
                            mLastLocation = task.getResult();
                            latitude = mLastLocation.getLatitude();
                            longitude = mLastLocation.getLongitude();

                        } else {
                            Log.w(TAG, "getLastLocation:exception", task.getException());
                            alertUserAboutError("No Location Detected.");
                        }
                    }
                });
    }

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
                            hourlyWeather = getHourlyDetails(jsonData);
                            dailyWeather = getDailyDetails(jsonData);

                            //bind data to new binding variable
                            binding.setWeather(currentWeather);

                            //update the weather image icon in the UI this must be done on the Main thread
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    Drawable drawable = getResources().getDrawable(currentWeather.getIconId());
                                    iconImageView.setImageDrawable(drawable);
                                }
                            });
                        } else {
                            alertUserAboutError(getString(R.string.error_message));
                        }
                    } catch (IOException e) {
                        Log.e(TAG, "IO Exception caught:", e);
                        alertUserAboutError(getString(R.string.timeout_error));
                    } catch(JSONException e){
                        Log.e(TAG, "JSON Exception caught:", e);
                        alertUserAboutError(getString(R.string.error_message));
                    }
                    //hide loading circles, must be done on main thread
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

    private DailyWeather getDailyDetails(String jsonData) throws JSONException {
        JSONObject forecast = new JSONObject(jsonData);
        String timezone = forecast.getString("timezone");
        JSONObject daily = forecast.getJSONObject("daily");
        JSONArray days = daily.getJSONArray("data");
        int numDays = days.length();

        //create a new object to store hourly weather data
        DailyWeather dailyWeather = new DailyWeather(numDays);
        dailyWeather.setMainSummary(daily.getString("summary"));
        dailyWeather.setMainIcon(daily.getString("icon"));
        dailyWeather.setNumDays(numDays);
        dailyWeather.setTimeZone(timezone);

        JSONObject currentDay;
        for(int i = 0; i < numDays; i++){
            currentDay = days.getJSONObject(i);
            dailyWeather.setTime(i, currentDay.getLong("time"));
            dailyWeather.setSummaries(i, currentDay.getString("summary"));
            dailyWeather.setIcons(i, currentDay.getString("icon"));
            dailyWeather.setPrecipChance(i, currentDay.getDouble("precipProbability"));
            //there is also times for low and high temps available
            dailyWeather.setMaxTemperatures(i, currentDay.getDouble("temperatureHigh"));
            dailyWeather.setMinTemperatures(i, currentDay.getDouble("temperatureLow"));
            dailyWeather.setHumidity(i, currentDay.getDouble("humidity"));
        }

        return dailyWeather;
    }

    private HourlyWeather getHourlyDetails(String jsonData) throws JSONException {
        JSONObject forecast = new JSONObject(jsonData);
        String timezone = forecast.getString("timezone");
        JSONObject hourly = forecast.getJSONObject("hourly");
        JSONArray hours = hourly.getJSONArray("data");
        int numHours = hours.length();

        //create a new object to store hourly weather data
        HourlyWeather hourlyWeather = new HourlyWeather(numHours);
        hourlyWeather.setMainSummary(hourly.getString("summary"));
        hourlyWeather.setMainIcon(hourly.getString("icon"));
        hourlyWeather.setNumHours(numHours);
        hourlyWeather.setTimeZone(timezone);

        JSONObject currentHour;
        for(int i = 0; i < numHours; i++){
            currentHour = hours.getJSONObject(i);
            hourlyWeather.setTime(i, currentHour.getLong("time"));
            hourlyWeather.setSummaries(i, currentHour.getString("summary"));
            hourlyWeather.setIcons(i, currentHour.getString("icon"));
            hourlyWeather.setPrecipChance(i, currentHour.getDouble("precipProbability"));
            //there is also times for low and high temps available
            hourlyWeather.setApparentTemperature(i, currentHour.getDouble("apparentTemperature"));
            hourlyWeather.setHumidity(i, currentHour.getDouble("humidity"));
        }

        return hourlyWeather;
    }

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

        JSONObject hourly = forecast.getJSONObject("hourly");



        JSONObject daily = forecast.getJSONObject("daily");


        //dynamically get the city name
        Geocoder gcd = new Geocoder(this, Locale.getDefault());
        List<Address> addresses = null;
        try {
            //could be improved by running on different thread
            addresses = gcd.getFromLocation(latitude, longitude, 1);
        } catch (IOException e) {
            e.printStackTrace();
        }
        if (addresses != null && addresses.size() > 0) {
            locality = addresses.get(0).getLocality();
        }
        currentWeather.setLocationLabel(locality);

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
        if (!checkPermissions()) {
            requestPermissions();
        } else {
            getLastLocation();
        }
        getForecast(latitude, longitude);
    }//end refreshOnClick

    public void startForecast(View view){
        //use an intent to indicate that we want to switch to a new activity
        Intent intent = new Intent(this, ForecastActivity.class);
        intent.putExtra("serial_current", currentWeather); //add all the relevant forecast data to display in new activity
        intent.putExtra("serial_hourly", hourlyWeather);
        intent.putExtra("serial_daily", dailyWeather);

        startActivity(intent); //start the new activity


    }

}//end MainActivity
