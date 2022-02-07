package com.example.weatherapp;

import android.os.AsyncTask;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;


public class MainActivity extends AppCompatActivity {

    private Button getRequestButton;
    private ImageButton refreshButton;
    private TextView weather_info;
    private String weather_text_info="";
    //Weather data
    private String city = "Barcelona";
    private final String apiId = "6e36078b02f257df49f83be2314b0b32";
    private String currentTemp = "";
    private String skyState = "";
    private String tempMin = "";
    private String tempMax = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        weather_info = findViewById(R.id.textView);
        refreshButton = findViewById(R.id.imageButton);

        new weatherBackgroundTask().execute();
    }

    class weatherBackgroundTask extends AsyncTask<String, Void, String> {
        @Override
        protected void onPreExecute() {
           super.onPreExecute();

            /* Showing the ProgressBar, Making the main design GONE
            findViewById(R.id.loader).setVisibility(View.VISIBLE);
            findViewById(R.id.mainContainer).setVisibility(View.GONE);
            findViewById(R.id.errorText).setVisibility(View.GONE);*/
        }

        protected String doInBackground(String... args) {
            String response;
            try {
                response = getRequest.getWeather("https://api.openweathermap.org/data/2.5/weather?q=" + city + "&units=metric&appid=" + apiId);
            } catch (IOException e) {
                response = e.toString();
            }
            return response;
        }

        @Override
        protected void onPostExecute(String result) {
            try {
                JSONtoVARS(result);
                weather_info.setText(currentTemp);

                //Refresh the weather data with a button
                //using thread because internet request
                //have to be done in background
                refreshButton.setOnClickListener(view -> {
                    //We have to do a thread for a network petition to work in background(mandatory)
                    Thread thread = new Thread(() -> {
                        try {
                            weather_text_info=getRequest.getWeather("https://api.openweathermap.org/data/2.5/weather?lat=35&lon=139&appid=6e36078b02f257df49f83be2314b0b32");
                            JSONtoVARS(weather_text_info);
                        } catch (Exception e) {
                            weather_text_info = e.toString();
                        }
                    });
                    thread.start();
                    try {
                        thread.join();
                        weather_info.setText(currentTemp+tempMin+tempMax+skyState);
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

    protected void JSONtoVARS(String jsonString) throws JSONException {
        JSONObject jsonObj = new JSONObject(jsonString);
        JSONObject main = jsonObj.getJSONObject("main");
        JSONObject weather = jsonObj.getJSONArray("weather").getJSONObject(0);

        currentTemp = main.getString("temp").charAt(0) + "°C"; //round degrees
        tempMin = main.getString("temp_min").charAt(0) + "°C";
        tempMax = main.getString("temp_max").charAt(0) + "°C";

        skyState = weather.getString("description");

        city = jsonObj.getString("name");
    }
}