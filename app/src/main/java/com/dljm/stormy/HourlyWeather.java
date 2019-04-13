package com.dljm.stormy;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

public class HourlyWeather implements Serializable {
    private String mainSummary;
    private String mainIcon;
    private int numHours;
    private String timeZone;

    //each index is a seperate hour
    private long[] time;
    private double[] apparentTemperatures;
    private String[] icons;
    private double[] humidity;
    private String[] summaries;
    private double[] precipChance;

    public HourlyWeather(int numHours){
        this.numHours = numHours;
        time = new long[numHours];
        apparentTemperatures = new double[numHours];
        icons = new String[numHours];
        humidity = new double[numHours];
        summaries = new String[numHours];
        precipChance = new double[numHours];
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

    public int getNumHours() {
        return numHours;
    }

    public void setNumHours(int numDays) {
        this.numHours = numHours;
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

    public String[] getFormattedTime(){
        String[] hours = new String[numHours];
        SimpleDateFormat formatter = new SimpleDateFormat("ha"); //hour and am/pm
        formatter.setTimeZone(TimeZone.getTimeZone(timeZone));
        for(int i = 0; i < numHours; i++){
            Date dateTime = new Date(time[i]*1000); //time needs to be in ms not seconds
            hours[i] = formatter.format(dateTime);
        }

        return hours;
    }

    public void setTime(long[] time) {
        this.time = time;
    }

    //index method
    public double getApparentTemperature(int i) {
        return apparentTemperatures[i];
    }

    public void setApparentTemperature(int i, double apparentTemperature) {
        this.apparentTemperatures[i] = apparentTemperature;
    }

    public double[] getApparentTemperatures() {
        return apparentTemperatures;
    }

    public void setApparentTemperatures(double[] apparentTemperatures) {
        this.apparentTemperatures = apparentTemperatures;
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

    public String getTimeZone() {
        return timeZone;
    }
}
