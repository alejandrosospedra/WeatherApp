package com.example.weatherapp;

import android.annotation.SuppressLint;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.loader.content.AsyncTaskLoader;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;


public class MainActivity extends AppCompatActivity {

    private Button getRequestButton;
    private ImageButton refreshButton;
    private TextView weather_info;
    private String weather_text_info="";
    private String city = "Barcelona";
    private String apiId = "6e36078b02f257df49f83be2314b0b32";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        getRequestButton = (Button) findViewById(R.id.get_weather);
        weather_info = (TextView) findViewById(R.id.textView);
        refreshButton = (ImageButton) findViewById(R.id.imageButton);

        new weatherBackgroundTask().execute();
    }

    static class weatherBackgroundTask extends AsyncTask<String, Void, String> {
        /*@Override
        protected void onPreExecute() {
           super.onPreExecute();

            // Showing the ProgressBar, Making the main design GONE
            findViewById(R.id.loader).setVisibility(View.VISIBLE);
            findViewById(R.id.mainContainer).setVisibility(View.GONE);
            findViewById(R.id.errorText).setVisibility(View.GONE);
        }*/

        protected String doInBackground(String... args) {
            String response = "";
            try {
                response = getWeather("https://api.openweathermap.org/data/2.5/weather?q=" + city + "&units=metric&appid=" + apiId);
            } catch (IOException e) {
                response = e.toString();
            }
            return response;
        }

        @Override
        protected void onPostExecute(String result) {


            try {
                JSONObject jsonObj = new JSONObject(result);
                JSONObject main = jsonObj.getJSONObject("main");
                JSONObject sys = jsonObj.getJSONObject("sys");
                JSONObject wind = jsonObj.getJSONObject("wind");
                JSONObject weather = jsonObj.getJSONArray("weather").getJSONObject(0);

                Long updatedAt = jsonObj.getLong("dt");
                String updatedAtText = "Updated at: " + new SimpleDateFormat("dd/MM/yyyy hh:mm a", Locale.ENGLISH).format(new Date(updatedAt * 1000));
                String temp = main.getString("temp") + "°C";
                String tempMin = "Min Temp: " + main.getString("temp_min") + "°C";
                String tempMax = "Max Temp: " + main.getString("temp_max") + "°C";
                String pressure = main.getString("pressure");
                String humidity = main.getString("humidity");

                Long sunrise = sys.getLong("sunrise");
                Long sunset = sys.getLong("sunset");
                String windSpeed = wind.getString("speed");
                String weatherDescription = weather.getString("description");

                String address = jsonObj.getString("name") + ", " + sys.getString("country");

                weather_info.setText(temp+" " +tempMin+" "+tempMax);

                /* Populating extracted data into our views
                addressTxt.setText(address);
                updated_atTxt.setText(updatedAtText);
                statusTxt.setText(weatherDescription.toUpperCase());
                tempTxt.setText(temp);
                temp_minTxt.setText(tempMin);
                temp_maxTxt.setText(tempMax);
                sunriseTxt.setText(new SimpleDateFormat("hh:mm a", Locale.ENGLISH).format(new Date(sunrise * 1000)));
                sunsetTxt.setText(new SimpleDateFormat("hh:mm a", Locale.ENGLISH).format(new Date(sunset * 1000)));
                windTxt.setText(windSpeed);
                pressureTxt.setText(pressure);
                humidityTxt.setText(humidity);*/

                /* Views populated, Hiding the loader, Showing the main design
                findViewById(R.id.loader).setVisibility(View.GONE);
                findViewById(R.id.mainContainer).setVisibility(View.VISIBLE);
                */

                //Refresh the weather data with a button
                //using thread because internet request
                //have to be done in background
                refreshButton.setOnClickListener(view -> {
                    //We have to do a thread for a network petition for work in background(mandatory)
                    Thread thread = new Thread(() -> {
                        try {
                            weather_text_info=getWeather("https://api.openweathermap.org/data/2.5/weather?lat=35&lon=139&appid=6e36078b02f257df49f83be2314b0b32");
                        } catch (IOException e) {
                            weather_text_info = e.toString();
                        }
                    });
                    thread.start();
                    try {
                        thread.join();
                        weather_info.setText(weather_text_info);
                    } catch (InterruptedException e) {
                        weather_info.setText(e.toString());
                    }
                });
            } catch (JSONException e) {
                weather_info.setText(e.toString());
                /*
                findViewById(R.id.loader).setVisibility(View.GONE);
                findViewById(R.id.errorText).setVisibility(View.VISIBLE);*/
            }

        }
    }


    protected String getWeather (String urlPetition) throws IOException{
        StringBuilder result = new StringBuilder();
        URL url = new URL(urlPetition);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("GET");
        BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
        for (String line; (line = reader.readLine()) != null; ) {
            result.append(line);
        }
        return result.toString();
    }

}