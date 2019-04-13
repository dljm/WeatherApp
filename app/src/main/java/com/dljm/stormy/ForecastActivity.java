package com.dljm.stormy;

import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.graphics.drawable.Drawable;
import android.media.Image;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

import com.dljm.stormy.databinding.ActivityForecastBinding;

public class ForecastActivity extends AppCompatActivity {
    public static final String TAG = MainActivity.class.getSimpleName();

    private ImageView iconImageView;
    private ImageView[] weeklyIcons;
    private ImageView[] hourlyIcons;
    private ActivityForecastBinding binding;
    private CurrentWeather currentWeather;
    private HourlyWeather hourlyWeather;
    private DailyWeather dailyWeather;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forecast);
        //get the serialized forecast data object passed from main activity
        currentWeather = (CurrentWeather) getIntent().getSerializableExtra("serial_current");
        hourlyWeather = (HourlyWeather) getIntent().getSerializableExtra("serial_hourly");
        dailyWeather = (DailyWeather) getIntent().getSerializableExtra("serial_daily");

        Log.d(TAG, currentWeather.getLocationLabel());

        //set up data binding class for activity_forecast.xml
        binding = DataBindingUtil.setContentView(ForecastActivity.this, R.layout.activity_forecast);
        //bind data to new binding variable
        binding.setWeather(currentWeather);
        binding.setHourly(hourlyWeather);
        binding.setDaily(dailyWeather);

        //dynamically update image icon in UI
        iconImageView = findViewById(R.id.forecastIconImageView);
        weeklyIcons = new ImageView[dailyWeather.getNumDays()];
        for(int i = 0; i < dailyWeather.getNumDays(); i++){
            String weeklyIconId = "forecastIcon" + i;
            //since we are dynamically accessing IDs we must use a slightly different method than before
            weeklyIcons[i] = findViewById(getResources().getIdentifier(weeklyIconId, "id", getPackageName()));
        }

        hourlyIcons = new ImageView[5];
        //darksky provides 48 hours of forecast data as stated in their docs.
        //current interface only uses next 8 hours in 2 hour increments
        for(int i = 0; i < 5; i++){
            String hourlyIconId = "hourIcon" + i*2; //0, 2, 4, 6 ,8
            hourlyIcons[i] = findViewById(getResources().getIdentifier(hourlyIconId, "id", getPackageName()));
        }


        //update the weather image icons in the UI this must be done on the Main thread
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Drawable drawable = getResources().getDrawable(currentWeather.getIconId());
                iconImageView.setImageDrawable(drawable);

                Drawable[] dailyDrawables = new Drawable[dailyWeather.getNumDays()];
                //currently only displaying five day forecasts however we do have data for 8
                for(int i = 0; i < 5; i++){
                    dailyDrawables[i] = getResources().getDrawable(dailyWeather.getIcons(i));
                    weeklyIcons[i].setImageDrawable(dailyDrawables[i]);
                }

                //currently only displaying five different hours of weather data
                for(int i = 0; i < 5; i++){
                    //want hours 2 hours apart i.e. i*2
                    hourlyIcons[i].setImageDrawable(getResources().getDrawable(hourlyWeather.getIcons(i*2)));
                }

            }
        });

    }



    public void exitForecast(View view){
        //use an intent to indicate that we want to switch to a new activity
        Intent intent = new Intent(this, MainActivity.class);
        //go back to the main activity and execute onCreate
        startActivity(intent); //start the new activity

    }
}
