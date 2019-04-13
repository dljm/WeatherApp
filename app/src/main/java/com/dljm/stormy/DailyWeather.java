package com.dljm.stormy;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

public class DailyWeather implements Serializable {
    private String mainSummary;
    private String mainIcon;
    private int numDays;
    private String timeZone;

    //each index is a seperate day
    private long[] time;
    private double[] maxTemperatures;
    private double[] minTemperatures;
    private String[] icons;
    private double[] humidity;
    private String[] summaries;
    private double[] precipChance;

    public DailyWeather(int numDays){
        this.numDays = numDays;
        time = new long[numDays];
        maxTemperatures = new double[numDays];
        minTemperatures = new double[numDays];
        icons = new String[numDays];
        humidity = new double[numDays];
        summaries = new String[numDays];
        precipChance = new double[numDays];
    }

    public String getMainSummary() {
        return mainSummary;
    }

    public void setMainSummary(String mainSummary) {
        this.mainSummary = mainSummary;
    }

    public String getMainIcon() {
        return mainIcon;
    }

    public void setMainIcon(String mainIcon) {
        this.mainIcon = mainIcon;
    }

    public int getNumDays() {
        return numDays;
    }

    public void setNumDays(int numDays) {
        this.numDays = numDays;
    }

    /*
    * Array member variables each require index of element requested
    * */
    public long getTime(int i) {
        return time[i];
    }

    public void setTime(int i, long time) {
        this.time[i] = time;
    }

    //also provide method to retrieve entire array after initializing
    public long[] getTime() {
        return time;
    }

    public void setTime(long[] time) {
        this.time = time;
    }

    public String[] getFormattedTime(){
        String[] days = new String[numDays];
        SimpleDateFormat formatter = new SimpleDateFormat("EEEE"); //day name
        formatter.setTimeZone(TimeZone.getTimeZone(timeZone));
        for(int i = 0; i < numDays; i++){
            Date dateTime = new Date(time[i]*1000); //time needs to be in ms not seconds
            days[i] = formatter.format(dateTime);
        }

        return days;
    }

    //index method
    public double getMaxTemperatures(int i) {
        return maxTemperatures[i];
    }

    public void setMaxTemperatures(int i, double maxTemperatures) {
        this.maxTemperatures[i] = maxTemperatures;
    }

    public double[] getMaxTemperatures() {
        return maxTemperatures;
    }

    public void setMaxTemperatures(double[] maxTemperatures) {
        this.maxTemperatures = maxTemperatures;
    }

    public double getMinTemperatures(int i) {
        return minTemperatures[i];
    }

    public void setMinTemperatures(int i, double minTemperatures) {
        this.minTemperatures[i] = minTemperatures;
    }

    public double[] getMinTemperatures() {
        return minTemperatures;
    }

    public void setMinTemperatures(double[] minTemperatures) {
        this.minTemperatures = minTemperatures;
    }

    public int getIcons(int i) {
        //clear-day, clear-night, rain, snow, sleet, wind, fog, cloudy, partly-cloudy-day, or partly-cloudy-night
        int iconId = R.drawable.clear_day;
        //switch statement for linking icon strings to icon images
        //provided icon images are added to the res folder
        switch (icons[i]) {
            case "clear-day":
                iconId = R.drawable.clear_day;
                break;
            case "clear-night":
                iconId = R.drawable.clear_night;
                break;
            case "rain":
                iconId = R.drawable.rain;
                break;
            case "snow":
                iconId = R.drawable.snow;
                break;
            case "sleet":
                iconId = R.drawable.sleet;
                break;
            case "wind":
                iconId = R.drawable.wind;
                break;
            case "fog":
                iconId = R.drawable.fog;
                break;
            case "cloudy":
                iconId = R.drawable.cloudy;
                break;
            case "partly-cloudy-day":
                iconId = R.drawable.partly_cloudy;
                break;
            case "partly-cloudy-night":
                iconId = R.drawable.cloudy_night;
                break;
        }
        return iconId;
    }

    public void setIcons(int i, String icon) {
        this.icons[i] = icon;
    }

    public String[] getIcons() {
        return icons;
    }

    public void setIcons(String[] icons) {
        this.icons = icons;
    }

    public double getHumidity(int i) {
        return humidity[i];
    }

    public void setHumidity(int i, double humidity) {
        this.humidity[i] = humidity;
    }

    public double[] getHumidity() {
        return humidity;
    }

    public void setHumidity(double[] humidity) {
        this.humidity = humidity;
    }

    public String getSummaries(int i) {
        return summaries[i];
    }

    public void setSummaries(int i, String summary) {
        this.summaries[i] = summary;
    }

    public String[] getSummaries() {
        return summaries;
    }

    public void setSummaries(String[] summaries) {
        this.summaries = summaries;
    }

    public double getPrecipChance(int i) {
        return precipChance[i];
    }

    public void setPrecipChance(int i, double precipChance) {
        this.precipChance[i] = precipChance;
    }

    public double[] getPrecipChance() {
        return precipChance;
    }

    public void setPrecipChance(double[] precipChance) {
        this.precipChance = precipChance;
    }

    public void setTimeZone(String timeZone) {
        this.timeZone = timeZone;
    }
}
