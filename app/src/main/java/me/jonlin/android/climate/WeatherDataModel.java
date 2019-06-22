package me.jonlin.android.climate;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Locale;

class WeatherDataModel
{
    private int mCondition;
    private String mCity;
    private String mIconName;
    //in fahrenheit
    private String mTemperature;

    static WeatherDataModel fromJSON(JSONObject jsonObj)
    {
        /*
        JSON exceptions: nan, inf, server side screwed up data format on corner cases maybe? very unlikely but may happen.
        */
        WeatherDataModel weatherData = new WeatherDataModel();
        try
        {
            // {} = obj in json
            // [] = json array
            //temperature defualt is kelvin
            weatherData.mCondition = jsonObj.getJSONArray("weather").getJSONObject(0).getInt("id");
            weatherData.mCity = jsonObj.getString("name");
            weatherData.mIconName = updateWeatherIcon(weatherData.mCondition);
            // round decimal
            weatherData.mTemperature = String.format(new Locale("en","US"),"%.0f",
                                                     kelvinToFahrenheit(jsonObj.getJSONObject("main").getDouble("temp")))
                                      + "°";
            /*
            int roundedValue = Math.rint(kelvinToFahrenheit(res));
             */

        } catch (JSONException e)
        {
            //weather app, user types in a location where we look up data based on it.
            // error handling.
            // maybe showing an error to UI if encountered unexpected data.
            weatherData.mTemperature = "NA";
            weatherData.mCondition = Integer.MAX_VALUE;
            weatherData.mCity = "NA";
            weatherData.mIconName = "NA";
            e.printStackTrace();
            return null;
        }
        return weatherData;
    }

    private static double kelvinToCelcius (double temp)
    {
      return temp-273.15;
    }

    private static double kelvinToFahrenheit(double temp)
    {
      return kelvinToCelcius(temp) * 9.0/5.0 + 32;
    }

    private static String updateWeatherIcon(int condition)
    {
        if (condition >= 0 && condition < 300) {
            return "tstorm1";
        } else if (condition >= 300 && condition < 500) {
            return "light_rain";
        } else if (condition >= 500 && condition < 600) {
            return "shower3";
        } else if (condition >= 600 && condition <= 700) {
            return "snow4";
        } else if (condition >= 701 && condition <= 771) {
            return "fog";
        } else if (condition >= 772 && condition < 800) {
            return "tstorm3";
        } else if (condition == 800) {
            return "sunny";
        } else if (condition >= 801 && condition <= 804) {
            return "cloudy2";
        } else if (condition >= 900 && condition <= 902) {
            return "tstorm3";
        } else if (condition == 903) {
            return "snow5";
        } else if (condition == 904) {
            return "sunny";
        } else if (condition >= 905 && condition <= 1000) {
            return "tstorm3";
        }
        return "error";
    }

    String getTemperature()
    {
        return mTemperature;
    }

    String getCity()
    {
        return mCity;
    }

    String getIconName()
    {
        return mIconName;
    }
}
